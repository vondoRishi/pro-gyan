package util.threading;
import java.util.Random;
import java.util.concurrent.Callable;

import libsvm.svm;

import org.apache.log4j.Logger;

import util.mlUtil.GridsearchBean;
import util.mlUtil.WekaUtil;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;

public class CrossValidation implements Callable<Evaluation> {
	private static Logger log = Logger.getLogger(CrossValidation.class);
	private Instances mTrain;
	private Double mCost;
	
	private Double mGamma;
	private GridsearchBean mGSB;
	private Evaluation mEV;
	private LibSVM mLibSVM;

	public Classifier getLibSVM() throws Exception {
		mLibSVM.buildClassifier(mTrain);
		return mLibSVM;
	}

	public Evaluation getEV() {
		return mEV;
	}
	
	public Double getCost() {
		return mCost;
	}

	public Double getGamma() {
		return mGamma;
	}


	public CrossValidation(Instances pTrain, Double pCost, Double pGamma,
			GridsearchBean pGSB) {
		mTrain = pTrain;
		mCost=pCost;
		mGamma = pGamma;
		mGSB = pGSB;
	}

	@Override
	public Evaluation call() throws Exception {
		Random rand = new Random(mGSB.getRandomSeed());
	    Instances randData = new Instances(mTrain);
	    randData.randomize(rand);
	    int folds = mGSB.getCrossValidation();
		if (randData.classAttribute().isNominal())
	      randData.stratify(folds );

		
		mLibSVM = new LibSVM();
		mLibSVM.setProbabilityEstimates(WekaUtil.isProbabilistic());
		mLibSVM.setCost(Math.pow(2, mCost));
		mLibSVM.setGamma(Math.pow(2, mGamma));
		
		mEV = new Evaluation(randData);
	    for (int n = 0; n < folds; n++) {
	      Instances ltrain = randData.trainCV(folds, n);
	      Instances test = randData.testCV(folds, n);
	      // the above code is used by the StratifiedRemoveFolds filter, the
	      // code below by the Explorer/Experimenter:
	      // Instances train = randData.trainCV(folds, n, rand);

	      
		// build and evaluate classifier
	      Classifier clsCopy = Classifier.makeCopy(mLibSVM);
	      clsCopy.buildClassifier(ltrain);
	      mEV.evaluateModel(clsCopy, test);
	    }
	    
		return mEV;
	}
	
	public static void main(String[] args) throws Exception {
		CrossValidation lCV = new  CrossValidation (WekaUtil.getArff("/home/rishi.das/ABL_wrkspc/test/Self/result.arff"), -5.0, -15.0,
		new GridsearchBean());
		Evaluation mEV = lCV.call();
		System.out.println("$$$$$$$");
		double fstRun= WekaUtil.getMCC(mEV);
		mEV = lCV.call();
		System.out.println("$$$$$$$");
		System.out.println(fstRun);
		System.out.println(WekaUtil.getMCC(mEV));
	}
}
