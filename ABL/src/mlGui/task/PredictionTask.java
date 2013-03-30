package mlGui.task;
import static util.constants.FileName.FEATURE_XML;
import static util.constants.FileName.PREDICTION_CSV;
import static util.constants.FileName.PREDICT_PREFIX;
import static util.constants.FileName.PRED_ARFF;
import static util.constants.FileName.PRED_RESULT_XML;
import static util.constants.FileName.PRED_SCALE_ARFF;
import static util.constants.FileName.MODEL;

import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import mlGui.Prediction;

import org.apache.log4j.Logger;

import protein.FastaInstanceReader;

import util.BioUtil;
import util.Log4jUtil;
import util.SystemUtil;
import util.mlUtil.ExecuteGridsearch;
import util.mlUtil.FeatureSelector;
import util.mlUtil.WekaUtil;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.Utils;
import beans.PredictionDataBean;
import beans.WekaDataBean;
import feature.common.commonIntrface.Instance;
import feature.common.commonIntrface.InstanceReader;
import feature.util.FileUtil;
import gui.ClassifiersWindow;
import gui.TestSummary;
import gui.TrainingDataWindow;
import gui.etc.CsvTable;

public class PredictionTask extends SwingWorker<Void, Void> {
	private static final String CURED_FAA = ".cured.fasta";

	private static Logger log = Logger.getLogger(PredictionTask.class);
	
	private static final String PREDICT = "predict";
	private static final String UNKNOWN = "Unknown";
	private static final String[] NOT_AN_AMINO = { "B", "O", "U", "X", "Z" , "J" , "*"};
	
	private PredictionDataBean mPDB;
	private String mWrkSpc;
	private String mLearnPath;
	private Evaluation mTestEvaluation;

	private Prediction mPrediction;

	public PredictionTask(PredictionDataBean pPDB, Prediction pPrediction) {
		mPrediction = pPrediction;
		mPDB = pPDB;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if(args.length<3){
			System.err.println("ERROR");
			System.exit(0);
		}
		if (!validateFastaFile(args[1])) {
			System.out.println("error in file");
			args[1] = args[1] + CURED_FAA;
		}
		System.out.println(args[0]);
		SystemUtil.intialize();
		Log4jUtil.reconfigure(SystemUtil.getWorkSpace() + File.separator+args[0]
				+ SystemUtil.getDateTime() + ".log");
		PredictionDataBean lPDB = new PredictionDataBean();
		
		
		
		lPDB.setModelName(args[0]);
		lPDB.setUserInput(args[1]);
		
		PredictionTask lPT = new PredictionTask(lPDB,null);
		lPT.execute();
		
		while(!lPT.isDone()) ; //a LOOOOP
		
		FileUtil.copy(lPT.mWrkSpc+ File.separator+PREDICTION_CSV, args[2]);
		FileUtil.deleteDir(new File(lPT.mWrkSpc));
	}
	
	
	public static boolean validateFastaFile(String pInputFilePaths)
			throws FileNotFoundException, IOException, Exception {
		InstanceReader pInstanceReader = new FastaInstanceReader();
		pInstanceReader.setSource(pInputFilePaths);
		boolean hasError = false;

			BufferedWriter lBW = new BufferedWriter(new FileWriter(pInputFilePaths+CURED_FAA));
			System.out.println("checking");
			Instance lInstance;
			while ((lInstance =pInstanceReader.nextInstance()) != null) {
				
				if(!valid((String)lInstance.getInstanceData())){
					
					hasError = true;
				}else{
					lBW.write(">"+lInstance.getInstanceId()+"\n");
					lBW.write(((String)lInstance.getInstanceData())+"\n");
				}
				
			}
			lBW.close();
			if (hasError) {
				System.err.println("There are error in input files");
				System.err.println(pInstanceReader.getError());
				
			}else {
				
				FileUtil.delete(pInputFilePaths+CURED_FAA);
			}
		

		return !hasError;
	}
	
	private static boolean valid(String pSeq) {
		if(pSeq.length()<10){
			return false;
		}
		for (int i = 0; i < NOT_AN_AMINO.length; i++) {
			if (pSeq.contains(NOT_AN_AMINO[i])) {
				//mErrorChar=NOT_AN_AMINO[i];
				System.err.println("isvalid");
				return false;
			}
		}
		return true;
	}

	@Override
	protected Void doInBackground() throws Exception {
		try {
			predict();
		} catch (RuntimeException e) {
			log.error("" ,e);
			SystemUtil.showPredictErrMsg(e.getMessage() );
		}catch (Exception e) {
			log.error("",e);
			SystemUtil.showPredictErrMsg(e.getMessage() );
		}catch (Error e) {
			log.error("",e);
			SystemUtil.showPredictErrMsg(e.getMessage() );
		}
		return null;
	}
	
	@Override
	public void done() {
		if(mPrediction!=null){
			Toolkit.getDefaultToolkit().beep();
			mPrediction.setCursor(null); // turn off the wait cursor
			mPrediction.setMessage("Done!\n");
			mPrediction.setDlgVisible(false);
			CsvTable mCsvTable;
			try {
				mCsvTable = CsvTable.get(mWrkSpc+ File.separator,true);
				mCsvTable.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				mCsvTable.setVisible(true);
				mCsvTable.setTitle("Prediction on "+mPDB.getInfo());
				mCsvTable.setLocationRelativeTo(ClassifiersWindow.getFrame());
				mCsvTable.setResizable(false);
			} catch (IOException e) {
				log.error("Fatal error ",e);
				SystemUtil.showPredictErrMsg(e.getMessage());
			}
		}
		
	}

	private void predict() throws FileNotFoundException, IOException, Exception {
		String lMsg;
		if(( lMsg = BioUtil.isValidFasta(mPDB.getInput()))!=null){
			SystemUtil.showPredictErrMsg(lMsg );
			return;
		}
		
		initializeWrkSpc();

		flashMessage("Feature Extracting");

		WekaDataBean testDB = BioUtil.extractFeatures(new String[]{UNKNOWN},PREDICT,new String [] {mPDB.getInput()},mWrkSpc+ File.separator+PRED_ARFF);
		ExecuteGridsearch lEV =   WekaUtil.getExecuteGridsearch(mLearnPath);

		flashMessage("Scaling Feature");
		Instances test_scale = WekaUtil.normalizeTestSet(testDB.getInst(), mLearnPath);
		test_scale.setRelationName(PREDICT);

		flashMessage("Selecting optimum features");
		Instances subTest = selectFeatures(test_scale,lEV.getTrain());
		subTest.setRelationName(PREDICT);
		subTest.setClassIndex(subTest.numAttributes() - 1);
		WekaUtil.saveArff(subTest,mWrkSpc+ File.separator+PRED_SCALE_ARFF);

		mTestEvaluation = new Evaluation(lEV.getTrain());
		flashMessage("Evaluating Test Data");
		LibSVM classifier = (LibSVM) lEV.getClassifier();
		
		mTestEvaluation.evaluateModel(classifier, subTest );
		saveTestResultToXml();


		//classify each instance
		Instances labeled = new Instances(subTest);

		
		List<String> lPredicted = new ArrayList<String>();
		 // label instances

		boolean probOutPut=true;
		List<String> lCsvTable;
		if(!probOutPut){
			 lCsvTable = classifyInstances(testDB, lEV, subTest,
					labeled,  lPredicted);
		}else{
			 lCsvTable = distOfInstances(testDB, lEV, subTest,
					labeled,  lPredicted);
		}

		savePredictions(lCsvTable);
	}
	
	private List<String> distOfInstances(WekaDataBean testDB,
			ExecuteGridsearch lEV, Instances subTest, Instances labeled,
			List<String> lPredicted) throws Exception {
		

		for (int j = 0; j < subTest.numInstances(); j++) {
			log.info(testDB.getInstanceId().get(j));

			double []distribution = lEV.getClassifier().distributionForInstance(
					subTest.instance(j));
			double score = distribution[0];
			int clsLabel=0;
			for (int i = 1; i < distribution.length; i++) {
				if(distribution[i]>distribution[i-1]){
					clsLabel=i;
					score = distribution[i];
				}
			}
			labeled.instance(j).setClassValue(clsLabel);
			lPredicted.add(subTest.classAttribute().value((int) clsLabel)+","+Utils.doubleToString(score,7, 3));
		}

		List<String> lCsvTable = new ArrayList<String>();
		lCsvTable.add("Id,Predicted,Probabilistic Score");
		for (int i = 0; i < lPredicted.size(); i++) {
			lCsvTable.add(testDB.getInstanceId().get(i)+","+lPredicted.get(i));
		}
		return lCsvTable;
		
		
		
	}

	private List<String> classifyInstances(WekaDataBean testDB,
			ExecuteGridsearch lEV, Instances subTest, Instances labeled,
			 List<String> lPredicted) throws Exception {
		
		for (int j = 0; j < subTest.numInstances(); j++) {
			log.info(testDB.getInstanceId().get(j));
			System.out.println(testDB.getInstanceId().get(j));

			double clsLabel = lEV.getClassifier().classifyInstance(
					subTest.instance(j));
			labeled.instance(j).setClassValue(clsLabel);
			lPredicted.add(subTest.classAttribute().value((int) clsLabel));

		}


		List<String> lCsvTable = new ArrayList<String>();
		lCsvTable.add("Id,Predicted");
		for (int i = 0; i < lPredicted.size(); i++) {
			lCsvTable.add(testDB.getInstanceId().get(i)+","+lPredicted.get(i));
		}
		return lCsvTable;
	}

	private void flashMessage(String pMsg) {
		if(mPrediction!=null){
			mPrediction.setMessage(pMsg);
		} else {
			System.out.println(pMsg);
		}

		log.info(pMsg);

	}

	private void initializeWrkSpc() throws IOException {

		mLearnPath = SystemUtil.getModelSpace()+File.separator+mPDB.getModelName()+File.separator+MODEL;
		
		mWrkSpc = util.FileUtil.getNewTmpDir()+mPDB.getModelName();
		File l=new File(mWrkSpc);
		l.mkdirs();


	}
	
	private Instances selectFeatures(Instances test_scale, Instances train) throws Exception {

		FeatureSelector lFeatureSelector = (FeatureSelector) FileUtil.getObjectFromXml(mLearnPath+File.separator + FEATURE_XML);
		return lFeatureSelector.getTopNFeature(test_scale,train, false);

	}
	
	private void savePredictions(List<String> lCsvTable) throws IOException {

		FileUtil.writeList(mWrkSpc+ File.separator+PREDICTION_CSV , lCsvTable);

	}

	private void saveTestResultToXml() {
		FileUtil.writeToXML(mWrkSpc+ File.separator+PRED_RESULT_XML, mTestEvaluation);

	}
}
