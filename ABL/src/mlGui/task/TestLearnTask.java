package mlGui.task;

import static util.constants.FileName.FEATURE_XML;
import static util.constants.FileName.PREDICTION_CSV;
import static util.constants.FileName.TEST_ARFF;
import static util.constants.FileName.TEST_PREFIX;
import static util.constants.FileName.TEST_RESULT_XML;
import static util.constants.FileName.TEST_SCALE_ARFF;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import mlGui.TestLearn;

import org.apache.log4j.Logger;

import util.BioUtil;
import util.SystemUtil;
import util.mlUtil.ExecuteGridsearch;
import util.mlUtil.FeatureSelector;
import util.mlUtil.WekaUtil;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.Utils;
import beans.WekaDataBean;
import feature.util.FileUtil;
import gui.TestSummary;
import gui.TrainingDataWindow;


public class TestLearnTask extends SwingWorker<Void, Void> {

	private static final String TEST_RELATION_NAME = "test";

	private static Logger log = Logger.getLogger(TestLearnTask.class);
	private String mLearnPath;
	private String [] mFilePaths = new String[2];

	private String[] mLabels = {"positive","negative"};
	private String mWrkSpc;
	private Evaluation mTestEvaluation;

	private TestLearn mTestLearn;

	private boolean mCalculateScore;

	public TestLearnTask(TestLearn pTL, String pLearnPath, String[] pLabels, String[] pFilePaths, boolean pCalculateScore) {
		mTestLearn = pTL;
		this.mLearnPath = pLearnPath;
		mLabels=pLabels;
		mFilePaths= pFilePaths;
		mCalculateScore = pCalculateScore;
		System.out.println(mCalculateScore);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String pLearnPath = "";
		String[] pLabels ={"positive","negative"};
		String[] pFilePaths= {"path.fasta","path.fasta"};
		TestLearnTask l = new TestLearnTask(null, pLearnPath, pLabels, pFilePaths,false);
		l.test();
		/*ExecuteGridsearch lEV =   WekaUtil.getExecuteGridsearch("/home/rishi.das/AFP_19/Self");
		Instances subTest = WekaUtil.getArff("/home/rishi.das/AFP_19/Self/test_0/test_scale.arff");
		Evaluation TestEvaluation = new Evaluation(lEV.getTrain());
		
		//TestEvaluation.evaluateModel(lEV.getClassifier(), subTest );

		for (int j = 0; j < subTest.numInstances(); j++) {
			System.out.println(j);
			lEV.getClassifier().classifyInstance(
					subTest.instance(j));
		}*/
	}

	private void test() throws Exception {
		for (String lFile : mFilePaths) {
			String lMsg;
			if(( lMsg = BioUtil.isValidFasta(lFile))!=null){
				SystemUtil.showTraingErrMsg(lMsg );
				return;
			}
		}

		initializeWrkSpc();

		flashMessage("Feature Extracting");

		WekaDataBean testDB = BioUtil.extractFeatures(mLabels,TEST_RELATION_NAME,mFilePaths,mWrkSpc+ File.separator+TEST_ARFF);
		ExecuteGridsearch lEV =   WekaUtil.getExecuteGridsearch(mLearnPath);

		flashMessage("Scaling Feature");
		Instances test_scale = WekaUtil.normalizeTestSet(testDB.getInst(), mLearnPath);
//		test_scale.
		test_scale.setRelationName(TEST_RELATION_NAME);

		flashMessage("Selecting optimum features");
		Instances subTest = selectFeatures(test_scale,lEV.getTrain());
		subTest.setRelationName(TEST_RELATION_NAME);
		subTest.setClassIndex(subTest.numAttributes() - 1);
		WekaUtil.saveArff(subTest,mWrkSpc+ File.separator+TEST_SCALE_ARFF);



		mTestEvaluation = new Evaluation(lEV.getTrain());
		flashMessage("Evaluating Test Data");
		LibSVM classifier = (LibSVM) lEV.getClassifier();
		
		mTestEvaluation.evaluateModel(classifier, subTest );
		saveTestResultToXml();


		//classify each instance
		Instances labeled = new Instances(subTest);

		List<String> lActual = new ArrayList<String>();
		List<String> lPredicted = new ArrayList<String>();
		 // label instances

		boolean probOutPut=mCalculateScore;
		List<String> lCsvTable;
		if(!probOutPut){
			 lCsvTable = classifyInstances(testDB, lEV, subTest,
					labeled, lActual, lPredicted);
		}else{
			 lCsvTable = distOfInstances(testDB, lEV, subTest,
					labeled, lActual, lPredicted);
		}
		
		savePredictions(lCsvTable);
	}

	private List<String> distOfInstances(WekaDataBean testDB,
			ExecuteGridsearch lEV, Instances subTest, Instances labeled,
			List<String> lActual, List<String> lPredicted) throws Exception {
		

		for (int j = 0; j < subTest.numInstances(); j++) {
			log.info(testDB.getInstanceId().get(j));
			double lActualClassLabel = subTest.instance(j).classValue();
			lActual.add(subTest.classAttribute().value((int) lActualClassLabel));
			
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
		lCsvTable.add("Id,Actual,Predicted,Probabilistic Score");
		for (int i = 0; i < lActual.size(); i++) {
			lCsvTable.add(testDB.getInstanceId().get(i)+","+lActual.get(i)+","+lPredicted.get(i));
		}
		return lCsvTable;
		
		
		
	}

	private List<String> classifyInstances(WekaDataBean testDB,
			ExecuteGridsearch lEV, Instances subTest, Instances labeled,
			List<String> lActual, List<String> lPredicted) throws Exception {
		
		for (int j = 0; j < subTest.numInstances(); j++) {
			log.info(testDB.getInstanceId().get(j));
			System.out.println(testDB.getInstanceId().get(j));
			double lActualClassLabel = subTest.instance(j).classValue();
			lActual.add(subTest.classAttribute().value((int) lActualClassLabel));

			double clsLabel = lEV.getClassifier().classifyInstance(
					subTest.instance(j));
			labeled.instance(j).setClassValue(clsLabel);
			lPredicted.add(subTest.classAttribute().value((int) clsLabel));

		}


		List<String> lCsvTable = new ArrayList<String>();
		lCsvTable.add("Id,Actual,Predicted");
		for (int i = 0; i < lActual.size(); i++) {
			lCsvTable.add(testDB.getInstanceId().get(i)+","+lActual.get(i)+","+lPredicted.get(i));
		}
		return lCsvTable;
	}

	private void flashMessage(String pMsg) {
		if(mTestLearn!=null){
			mTestLearn.setMessage(pMsg);
		} else {
			System.out.println(pMsg);
		}

		log.info(pMsg);

	}

	private void savePredictions(List<String> lCsvTable) throws IOException {

		FileUtil.writeList(mWrkSpc+ File.separator+PREDICTION_CSV , lCsvTable);

	}

	private void saveTestResultToXml() {
		FileUtil.writeToXML(mWrkSpc+ File.separator+TEST_RESULT_XML, mTestEvaluation);

	}

	private Instances selectFeatures(Instances test_scale, Instances train) throws Exception {

		FeatureSelector lFeatureSelector = (FeatureSelector) FileUtil.getObjectFromXml(mLearnPath+File.separator + FEATURE_XML);
		return lFeatureSelector.getTopNFeature(test_scale,train, false);

	}

	private void initializeWrkSpc() {

		File l = new File(mLearnPath);
		int i=0;
		for (File l1 : l.listFiles()) {
			if(l1.getName().startsWith(TEST_PREFIX)){
				i++;
			}
		}
		mWrkSpc = mLearnPath+ File.separator+TEST_PREFIX+i;
		l=new File(mWrkSpc);
		l.mkdirs();


	}

	@Override
	public Void doInBackground() throws Exception {
		try {
			test();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			log.error("" ,e);
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("",e);
		}catch (Error e) {
			// TODO Auto-generated catch block
			log.error("",e);
		}
		return null;
	}

	@Override
	public void done() {
		Toolkit.getDefaultToolkit().beep();
		//startButton.setEnabled(true);
		mTestLearn.setCursor(null); // turn off the wait cursor
		mTestLearn.setMessage("Done!\n");
		mTestLearn.setDlgVisible(false);
		TrainingDataWindow.refreshTree();
		try {
			TrainingDataWindow.getWindow().showPanel(new TestSummary( mWrkSpc)," ???Test ");
		} catch (IOException e) {
			mTestLearn.setMessage("Error Occured" + e + "\nCheck Log File");
			SystemUtil.showTraingErrMsg("Some exception has occured. \n"+e );
			log.error("Fatal error ",e);
		}catch (Exception e) {
			mTestLearn.setMessage("Error Occured" + e + "\nCheck Log File");
			SystemUtil.showTraingErrMsg("Some exception has occured. \n"+e );
			log.error("Fatal error ",e);
		}catch (Error e) {
			mTestLearn.setMessage("Error Occured" + e + "\nCheck Log File");
			SystemUtil.showTraingErrMsg("Some exception has occured. \n"+e );
			log.error("Fatal error ",e);
		}

	}
}

