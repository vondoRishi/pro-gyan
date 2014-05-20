package mlGui.task;

import static util.constants.FileName.*;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import mlGui.SelfLearn;

import org.apache.log4j.Logger;

import util.BioUtil;
import util.Log4jUtil;
import util.SystemUtil;
import util.constants.FileName;
import util.mlUtil.ExecuteGridsearch;
import util.mlUtil.FeatureEvaluator;
import util.mlUtil.FeatureSelector;
import util.mlUtil.WekaUtil;
import weka.core.Instances;
import weka.core.Utils;
import beans.ExperimentDataBean;
import feature.util.FileUtil;
import gui.Summary;
import gui.TrainingDataWindow;

public class SelfLearnTask extends SwingWorker<Void, Void> {
	
	
	
	private static Logger log = Logger.getLogger(SelfLearnTask.class);
	private static SelfLearn mSelfLearn;
	private ExperimentDataBean mExperimentDataBean;

	private String [] mFilePaths = new String[2];

	private String[] mLabels = {"1","-1"};

	private File mWrkSpc;

	private String mRelationName;
	private ExecuteGridsearch mExeGridSearch;
	private String mSummaryPath;
	private FeatureEvaluator mFE= new FeatureEvaluator(FeatureEvaluator.FCBF);

	public static void main(String[] args) throws IOException {
		try {
			if(args.length==0){
				System.err.println("ERROR");
				System.exit(0);
			}
			System.out.println(args[0]);
			SystemUtil.intialize();
			Log4jUtil.reconfigure(SystemUtil.getWorkSpace() + File.separator+args[0]
					+ SystemUtil.getDateTime() + ".log");
			ExperimentDataBean lEB = SystemUtil.getExperimentDataBean(SystemUtil.getWorkSpace()+ File.separator+args[0]);
			
			SelfLearnTask lSLT = null;
			if(args.length==2){
				 lSLT = new SelfLearnTask(null, lEB,Integer.parseInt(args[1]));
				
			}else{
				lSLT = new SelfLearnTask(null, lEB);
			}
			
//			lSLT.lFE = new FeatureEvaluator(FeatureEvaluator.FCBF);
			
			lSLT.doInBackground();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Fatal error ",e);
		}
		
	}
	
	/**
	 * http://weka.wikispaces.com/Use+WEKA+in+your+Java+code
	 * @param train
	 * @return
	 * @throws Exception
	 */
	private Instances normalize(Instances train) throws Exception {
		String relationName = train.relationName();
		train = WekaUtil.normalizeTrainSet(train,mWrkSpc.getAbsolutePath());
		WekaUtil.saveArff(train,mWrkSpc.getAbsolutePath() + File.separator
				+ MASTER_SCALE_ARFF);
		
		train.setRelationName(relationName+"_normalized");
		return train;
	}


	private void reportTrain(Instances train) {
		log.info("Number of Features : "+(train.numAttributes()-1));

	}

	private ExecuteGridsearch learning(Instances train, FeatureSelector featureSelector) throws Exception {
		List<Integer> toBeEvaluate = getFeatureList(train,featureSelector);
		List<ExecuteGridsearch> lAccuracyList = new ArrayList<ExecuteGridsearch>();

		for (Integer integer : toBeEvaluate) {
			Instances subTrain = featureSelector.getTopNFeature(integer, false,train);
			
			ExecuteGridsearch lEGS = new ExecuteGridsearch(subTrain);
			lEGS.executeThread();
			lAccuracyList.add(lEGS);
			flashMessage("MCC "+Utils.doubleToString(lEGS.getBestResult(),7,2)+" with "+integer+" features ");
		}

		ExecuteGridsearch lBestResult = null;
		int currentOptimumFeaturesIndex = 0;
		for (int i = 0; i < toBeEvaluate.size(); i++) {
			if(i==0){
				System.out.println("Feature Set\tCost\tGamma\tAccuracy");
				lBestResult = lAccuracyList.get(i);
				currentOptimumFeaturesIndex=i;
			}
			if(lBestResult==null){
				lBestResult = lAccuracyList.get(i);
				currentOptimumFeaturesIndex = i;
			}else if(lBestResult.getBestResult() < lAccuracyList.get(i).getBestResult()){
				lBestResult = lAccuracyList.get(i);
				currentOptimumFeaturesIndex = i;
			}
			System.out.println(toBeEvaluate.get(i) + "\t"
					+ lAccuracyList.get(i).getBestCost() + "\t"
					+ lAccuracyList.get(i).getBestGamma() + "\t"
					+ lAccuracyList.get(i).getBestResult() + "\t"
					+ lAccuracyList.get(i).getSummary());
			
			log.info(toBeEvaluate.get(i) + "\t"
					+ lAccuracyList.get(i).getBestCost() + "\t"
					+ lAccuracyList.get(i).getBestGamma() + "\t"
					+ lAccuracyList.get(i).getBestResult() + "\t"
					+ " getTrain attr "+lAccuracyList.get(i).getTrain().numAttributes()+ "\t"
					+ lAccuracyList.get(i).getSummary()+ "\t"
					);
		}
		
		//deep search
		int start = toBeEvaluate.get(0);
		int end = toBeEvaluate.get(toBeEvaluate.size()-1);
		if(lBestResult.getAttributes()==toBeEvaluate.get(0)){
			end=toBeEvaluate.get(1);
		}else if(lBestResult.getAttributes() == toBeEvaluate.get(toBeEvaluate.size()-1)){
			start = toBeEvaluate.get(toBeEvaluate.size()-2);
		}else {
			
			start = toBeEvaluate.get(currentOptimumFeaturesIndex-1);
			end = toBeEvaluate.get(currentOptimumFeaturesIndex+1);
		}
		
		if((end-start)>10){
			ExecuteGridsearch lBestBinaryResult = getDeepSearchResult(start,end,train,featureSelector);
			if(lBestBinaryResult.getBestResult() >= lBestResult.getBestResult() &&
					lBestBinaryResult.getAttributes() < lBestResult.getAttributes()){
				lBestResult = lBestBinaryResult;
				log.info("wowoooooo is it a good result");
			}
		}
		//
		FileUtil.writeToXML(mWrkSpc.getAbsolutePath()+File.separator+INTER_XML, lBestResult);
		log.info("Inter mediate Detail performance measurement of best classifier");
		log.info(lBestResult.getBestEvaluation().toSummaryString());
		log.info(lBestResult.getBestEvaluation().toClassDetailsString());
		log.info(lBestResult.getBestEvaluation().toMatrixString());
		//
		
		//deep search ends here
		/*IMBFS i = new IMBFS(lBestResult);
		
		lBestResult=i.execute();*/

		
		Instances resultTrain = lBestResult.getTrain();
		resultTrain.setRelationName(mRelationName+" normalized selected features");
		WekaUtil.saveResultArff(resultTrain, mWrkSpc.getAbsolutePath() );
		return lBestResult;

	}

	private ExecuteGridsearch getDeepSearchResult(int pStart, int pEnd,
			Instances train, FeatureSelector featureSelector) throws Exception {
		log.info("DeepSearch is started with "+pStart+" to "+pEnd);
		
		List<Integer> toBeEvaluate = new ArrayList<Integer>();
		int step = (pEnd-pStart)/5;
		for (int i = pStart+step; i < pEnd; i+=step) {
			toBeEvaluate.add(i);
		}
		List<ExecuteGridsearch> lAccuracyList = new ArrayList<ExecuteGridsearch>();

		for (Integer integer : toBeEvaluate) {
			Instances subTrain = featureSelector.getTopNFeature(integer, false,train);
			ExecuteGridsearch lEGS = new ExecuteGridsearch(subTrain);
			lEGS.executeThread();
			lAccuracyList.add(lEGS);
		}

		ExecuteGridsearch lBestResult = null;
		int currentOptimumFeaturesIndex = 0;
		for (int i = 0; i < toBeEvaluate.size(); i++) {
			if(i==0){
				System.out.println("Feature Set\tCost\tGamma\tAccuracy  $$$$$$ ");
				lBestResult = lAccuracyList.get(i);
				currentOptimumFeaturesIndex=i;
			}
			if(lBestResult==null){
				lBestResult = lAccuracyList.get(i);
				currentOptimumFeaturesIndex = i;
			}else if(lBestResult.getBestResult() < lAccuracyList.get(i).getBestResult()){
				lBestResult = lAccuracyList.get(i);
				currentOptimumFeaturesIndex = i;
			}
			
			
			log.info(toBeEvaluate.get(i) + "\t"
					+ lAccuracyList.get(i).getBestCost() + "\t"
					+ lAccuracyList.get(i).getBestGamma() + "\t"
					+ lAccuracyList.get(i).getBestResult() + "\t"
					+ " getTrain attr "+lAccuracyList.get(i).getTrain().numAttributes()+ "\t"
					+ lAccuracyList.get(i).getSummary()+ "\t");
		}
		
		//deep search
		int start = toBeEvaluate.get(0);
		int end = toBeEvaluate.get(toBeEvaluate.size()-1);
		if(lBestResult.getAttributes()==toBeEvaluate.get(0)){
			end=toBeEvaluate.get(1);
		}else if(lBestResult.getAttributes() == toBeEvaluate.get(toBeEvaluate.size()-1)){
			start = toBeEvaluate.get(toBeEvaluate.size()-2);
		}else {
			
			start = toBeEvaluate.get(currentOptimumFeaturesIndex-1);
			end = toBeEvaluate.get(currentOptimumFeaturesIndex+1);
		}
		
		if((end-start)>10){
			ExecuteGridsearch lBestBinaryResult = getDeepSearchResult(start,end,train,featureSelector);
			if(lBestBinaryResult.getBestResult() > lBestResult.getBestResult() || 
					( lBestBinaryResult.getBestResult() == lBestResult.getBestResult() &&
					lBestBinaryResult.getAttributes() < lBestResult.getAttributes())){
				lBestResult = lBestBinaryResult;
			}
		}
		
		//deep search ends here

		return lBestResult;
	}


	private List<Integer> getFeatureList(Instances train, FeatureSelector featureSelector) {
		List<Integer> toBeEvaluate = new ArrayList<Integer>();
//		int lNumInstance = (train.numInstances())<300 ? (train.numInstances()) : 300;
		int lNumInstance = (featureSelector.getRankedAttr().size())<400 ? (featureSelector.getRankedAttr().size()-1) : 400;
		System.out.println("Number of Features : "+lNumInstance);
		System.out.print("Feature sets to be tried containing top ");

		
		toBeEvaluate.add(2);
		

		
		for (int i = 100;  i < lNumInstance; i=i+100) {
			toBeEvaluate.add(i);
		}
		toBeEvaluate.add(lNumInstance);
		

		return toBeEvaluate;
	}

	private FeatureSelector featureEvaluate(Instances train) throws Exception {
//		lFE = new FeatureEvaluator(FeatureEvaluator.FCBF);
		FeatureSelector lFeatureSelector = new FeatureSelector(train,mFE);
		lFeatureSelector.writeList(mWrkSpc.getAbsolutePath()+File.separator+FSCORE_TXT);
		return lFeatureSelector;
	}


	public SelfLearnTask(SelfLearn pSelfLearn, ExperimentDataBean pExperimentDataBean) {
		initializeSelfLearn(pSelfLearn, pExperimentDataBean);
	}

	private void initializeSelfLearn(SelfLearn pSelfLearn,
			ExperimentDataBean pExperimentDataBean) {
		mSelfLearn = pSelfLearn;
		mExperimentDataBean = pExperimentDataBean;
		mFilePaths[0] = SystemUtil.getWorkSpace()+File.separator+mExperimentDataBean.getName()+File.separator+mExperimentDataBean.getPos_label()+"_positive.fasta";
		mFilePaths[1] = SystemUtil.getWorkSpace()+File.separator+mExperimentDataBean.getName()+File.separator+mExperimentDataBean.getNeg_label()+"_negative.fasta";
		mWrkSpc = new File(SystemUtil.getWorkSpace()+File.separator+mExperimentDataBean.getName()+File.separator+MODEL);
		mWrkSpc.mkdirs();
		mSummaryPath = mWrkSpc.getAbsolutePath()+File.separator+SUMMARY_XML;
		mRelationName = mExperimentDataBean.getName();
		mLabels[0] = mExperimentDataBean.getPos_label();mLabels[1] = mExperimentDataBean.getNeg_label();
	}

	public SelfLearnTask(SelfLearn object, ExperimentDataBean lEB, int featureRanker) {
		mFE = new FeatureEvaluator(featureRanker);
		initializeSelfLearn(object,lEB);
	}

	public void addPropertyChangeListener(SelfLearn selfLearn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Void doInBackground() {
		
		try {
			
			File preCalculated = new File(mSummaryPath);
			if(preCalculated.exists() && preCalculated.isFile()){
				mExeGridSearch =  WekaUtil.getExecuteGridsearch(mWrkSpc.getAbsolutePath());
			}else{
			// TODO Auto-generated method stub
			/*
			 * processOptions(args);
			 * http://weka.wikispaces.com/Use+Weka+in+your+Java+code
			 */
				
			  flashMessage("Feature Extracting");
			  Instances train = BioUtil.extractFeatures(mLabels,mRelationName,mFilePaths,mWrkSpc+ File.separator+MASTER_ARFF).getInst();
			  flashMessage("Normalizing Features");
			  train = normalize(train);
			  reportTrain(train);

			 /*
			 * noiseRemoval()
			 */
			  flashMessage("Evaluating Features");
			  FeatureSelector lFeatureSelector = featureEvaluate(train);
			  FileUtil.writeToXML(mWrkSpc.getAbsolutePath()+File.separator+FEATURE_XML, lFeatureSelector);
			  lFeatureSelector.reportTopScore();
			  
			  flashMessage("Building classifiers ");
			  mExeGridSearch = learning(train,lFeatureSelector);
//			  save the best result producing arff file 
			  saveResultToXml();
			 /*
			 *
			 * http://weka.wikispaces.com/Optimizing+parameters
			 * Trainnig()
			 * */
		
			
			}
			
			// Check no of proteins.
		} catch (Exception e) {
			flashMessage("Error Occured" + e + "\nCheck Log File");
			log.error("Fatal error ",e);
			SystemUtil.showTraingErrMsg("Some exception has occured. \n"+e );
			
		}
	
		return null;
	}
	
	public static void flashMessage(String pMsg) {
		if(mSelfLearn!=null){
			mSelfLearn.setMessage(pMsg);
		} else {
			System.out.println(pMsg);
		}

		log.info(pMsg);

	}

	
	private void saveResultToXml() {
		
		FileUtil.writeToXML(mSummaryPath, mExeGridSearch);
		
	}
	
	public void setCustomProgress(int pr) {
		setProgress(pr);
	}

	 
	@Override
	public void done() {
		Toolkit.getDefaultToolkit().beep();
		//startButton.setEnabled(true);
		if(mSelfLearn!=null){
		mSelfLearn.setCursor(null); // turn off the wait cursor
		mSelfLearn.setMessage("Done!\n");
		mSelfLearn.setDlgVisible(false);
		TrainingDataWindow.refreshTree();
		try {
			TrainingDataWindow.getWindow().showPanel(new Summary( mWrkSpc.getAbsolutePath())," ???self ");
		} catch (IOException e) {
			mSelfLearn.setMessage("Error Occured" + e + "\nCheck Log File");
			
			SystemUtil.showTraingErrMsg("Some exception has occured. \n"+e );
			log.error("Fatal error ",e);
		} catch (Exception e) {
			mSelfLearn.setMessage("Error Occured" + e + "\nCheck Log File");
			
			SystemUtil.showTraingErrMsg("Some exception has occured. \n"+e );
			log.error("Fatal error ",e);
		}
		}
		
		/*JFrame frame = new JFrame();
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		JPanel lTreePanel = new JPanel();
		lTreePanel.setBounds(6, 12, 550, 350);
		frame.add(lTreePanel);
		lTreePanel.setLayout(null);
		lTreePanel.add(new Summary( mResult));
		frame.setVisible(true);*/
		
		
	}

	
	public String[] getmFilePaths() {
		return mFilePaths;
	}

	public void setmFilePaths(String[] mFilePaths) {
		this.mFilePaths = mFilePaths;
	}

	public String[] getmLabels() {
		return mLabels;
	}

	public void setmLabels(String[] mLabels) {
		this.mLabels = mLabels;
	}

	public File getmWrkSpc() {
		return mWrkSpc;
	}

	public void setmWrkSpc(File mWrkSpc) {
		this.mWrkSpc = mWrkSpc;
	}

	public String getmRelationName() {
		return mRelationName;
	}

	public void setmRelationName(String mRelationName) {
		this.mRelationName = mRelationName;
	}


}

