package util.mlUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import libsvm.svm;

import org.apache.log4j.Logger;

import util.SystemUtil;
import util.threading.CrossValidation;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;



public class ExecuteGridsearch {
	private static Logger log = Logger.getLogger(ExecuteGridsearch.class);
	private GridsearchBean mGSB;
	private double mBestResult;
	private double mBestCost,mBestGamma;
	private StringBuilder mStringBuilder = new StringBuilder();
	private boolean mError;
	private Evaluation mBestEvaluation;
	private Classifier mClassifier;
	private Instances mTrain = null;
	static {
		svm.rand.setSeed(0);
	}

	public Classifier getClassifier() {
		return mClassifier;
	}

	public void setClassifier(Classifier pClassifier) {
		this.mClassifier = pClassifier;
	}

	public StringBuilder getStringBuilder() {
		return mStringBuilder;
	}

	public ExecuteGridsearch(Instances pTrain) {
		mGSB = GridsearchBean.getBean();
		mTrain = pTrain;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		
		SystemUtil.intialize();
		/*if(args.length==0){
			System.out.println("not enough arguments");
			System.exit(0);
		}*/
		
//		String lPath = SystemUtil.getWorkSpace() + File.separator+args[0]+File.separator+FileName.MODEL+File.separator;
		//FileUtil.copy(lPath + FileName.SUMMARY_XML, lPath + FileName.SUMMARY_XML + "back_up");
		//DataSource source = new DataSource(lPath + FileName.RESULT_ARFF);
		Instances train = WekaUtil.getArff("/home/rishi.das/tmp/FAAP_result.arff");
		

		double lResult = 0;
		ExecuteGridsearch lBestResult=null;
		//List<ExecuteGridsearch> lResults = new ArrayList<ExecuteGridsearch>();
		for (int i = 0; i < 1; i++) {
			
			ExecuteGridsearch lEGS = new ExecuteGridsearch(train);
			lEGS.executeThread();
			
			if(lResult<lEGS.getBestResult()){
				lBestResult = lEGS;
				lResult=lEGS.getBestResult();
			}
		}
		
		System.out.println(lBestResult.getBestResult());
		
		/*Evaluation mTestEvaluation = new Evaluation(lBestResult.getTrain());
		
		mTestEvaluation.evaluateModel(lBestResult.getClassifier(), lBestResult.getTrain() );*/
		//FileUtil.writeToXML(lPath + FileName.SUMMARY_XML, lBestResult);
		System.out.println("best result "+lResult);
		System.out.println("Detail performance measure ment of best classifier");
		System.out.println(lBestResult.getBestEvaluation().toSummaryString());
		System.out.println(lBestResult.getBestEvaluation().toClassDetailsString());
		System.out.println(lBestResult.getBestEvaluation().toMatrixString());
		log.info("Detail performance measure ment of best classifier");
		
		
		//////
		
		
		
		/*LibSVM cls = new LibSVM();
		 cls.setProbabilityEstimates(true);
		Random rand = new Random(0);
	    Instances randData = new Instances(train);
	    randData.randomize(rand);
	    int folds = 10;
		if (randData.classAttribute().isNominal())
	      randData.stratify(folds );

	    // perform cross-validation
	    Evaluation eval = new Evaluation(randData);
	    for (int n = 0; n < folds; n++) {
	      Instances ltrain = randData.trainCV(folds, n);
	      Instances test = randData.testCV(folds, n);
	      // the above code is used by the StratifiedRemoveFolds filter, the
	      // code below by the Explorer/Experimenter:
	      // Instances train = randData.trainCV(folds, n, rand);

	     
		// build and evaluate classifier
	      Classifier clsCopy = Classifier.makeCopy(cls);
	      clsCopy.buildClassifier(ltrain);
	      eval.evaluateModel(clsCopy, test);
	    }
	    log.info(" attr="+train.numAttributes()+" C="+0.03125+" g="+3.0517578125E-5+"  ACC="+eval.pctCorrect());*/
	    
		
		

		//System.out.println("\n\nC="+Math.pow(2, lCost)+" g="+Math.pow(2, lGamma)+"  Acc="+lEV.weightedTruePositiveRate());
		/*long end = System.currentTimeMillis();
		long elapsedTime = end - start;
		  System.out.println("The process took approximately: "
		  + elapsedTime + " milli seconds");*/
	}
	
	public void executeThread() throws Exception  {
		log.debug("Starting execution");
		ExecutorService pool = Executors.newFixedThreadPool(SystemUtil.getNumberOfThreads());
		CompletionService<Evaluation> compService = new ExecutorCompletionService<Evaluation>(pool);
		mBestResult = 0;
		mStringBuilder = new StringBuilder();
		int workItems=0;
		List<CrossValidation> jobs = new ArrayList<CrossValidation>(); 
		for (Double lCost = mGSB.getCostMin(); lCost <= mGSB.getCostMax(); lCost+=mGSB.getCostStep()) {
			for (Double lGamma = mGSB.getGammaMin(); lGamma <= mGSB.getGammaMax() ; lGamma+=mGSB.getGammaStep()) {

				try {
					CrossValidation l = new CrossValidation(mTrain, lCost,lGamma,mGSB);
					compService.submit(l);
					jobs.add(l);
					workItems++;
				} catch (Exception e) {
					mError = true;
					mStringBuilder.append("C="+Math.pow(2, lCost)+" g="+Math.pow(2, lGamma)+e+e.getMessage());
					log.error(""+e);
				}
			}
		}
		
		pool.shutdown();
		
		while(!pool.awaitTermination(1, TimeUnit.SECONDS)){
			System.out.println("Waiting");
		}
		
		
		for (CrossValidation crossValidation : jobs) {
			  
			  Evaluation lEV = crossValidation.getEV(); // Extract result; this will not block.
			  double result = WekaUtil.getMCC(lEV);
			  if(mBestResult<result){
					mBestResult = result;
					mBestCost = crossValidation.getCost();
					mBestGamma = crossValidation.getGamma();
					mBestEvaluation = lEV;
					mClassifier = crossValidation.getLibSVM();
				}

				//System.out.println("\n\nC="+Math.pow(2, lCost)+" g="+Math.pow(2, lGamma)+"  Acc="+lEV.weightedTruePositiveRate());
				log.info(" attr="+mTrain.numAttributes()+" C="+Math.pow(2, crossValidation.getCost())+" g="+Math.pow(2, crossValidation.getGamma())+"  MCC="+result);
		}

		
	}

	/*public void execute()  {
		log.debug("Starting execution");
		
		mBestResult = 0;

		for (Double lCost = mGSB.getCostMin(); lCost <= mGSB.getCostMax(); lCost+=mGSB.getCostStep()) {
			for (Double lGamma = mGSB.getGammaMin(); lGamma <= mGSB.getGammaMax() ; lGamma+=mGSB.getGammaStep()) {

				try {
					Random rand = new Random(mGSB.getRandomSeed());
				    Instances randData = new Instances(mTrain);
				    randData.randomize(rand);
				    int folds = mGSB.getCrossValidation();
					if (randData.classAttribute().isNominal())
				      randData.stratify(folds );

					LibSVM lLibSVM = new LibSVM();
//					lLibSVM.setProbabilityEstimates(true);
					lLibSVM.setCost(Math.pow(2, lCost));
					lLibSVM.setGamma(Math.pow(2, lGamma));
					
					Evaluation lEV = new Evaluation(randData);
				    for (int n = 0; n < folds; n++) {
				      Instances ltrain = randData.trainCV(folds, n);
				      Instances test = randData.testCV(folds, n);
				      // the above code is used by the StratifiedRemoveFolds filter, the
				      // code below by the Explorer/Experimenter:
				      // Instances train = randData.trainCV(folds, n, rand);

				     
					// build and evaluate classifier
				      Classifier clsCopy = Classifier.makeCopy(lLibSVM);
				      clsCopy.buildClassifier(ltrain);
				      lEV.evaluateModel(clsCopy, test);
				    }
					if(mBestResult<WekaUtil.getMCC(lEV)){
						mBestResult = WekaUtil.getMCC(lEV);
						mBestCost = lCost;
						mBestGamma = lGamma;
						mBestEvaluation = lEV;
						mClassifier = lLibSVM;
					}

					//System.out.println("\n\nC="+Math.pow(2, lCost)+" g="+Math.pow(2, lGamma)+"  Acc="+lEV.weightedTruePositiveRate());
					log.info(" attr="+mTrain.numAttributes()+" C="+Math.pow(2, lCost)+" g="+Math.pow(2, lGamma)+"  ACC="+lEV.pctCorrect());
				} catch (Exception e) {
					mError = true;
					mStringBuilder.append("C="+Math.pow(2, lCost)+" g="+Math.pow(2, lGamma)+e+e.getMessage());
					log.error(""+e);
				}
			}
		}

	}*/

	public double getBestCost() {
		return mBestCost;
	}

	public double getBestGamma() {
		return mBestGamma;
	}

	public double getBestResult() {
		return mBestResult;
	}

	public boolean isError() {
		return mError;
	}

	public String getSummary() {
		if (mError){
			return "("+mStringBuilder.toString()+")";
		}
		return "";
	}

	public Evaluation getBestEvaluation() {
		return mBestEvaluation;
	}

	public Instances getTrain() {
		return mTrain;
	}

	/**
	 * effective number of attributes is reduced by 1,
	 * i.e. to remove the class_label from consideration.
	 * @return
	 */
	public int getAttributes() {
		
		return mTrain.numAttributes()-1;
	}
}

