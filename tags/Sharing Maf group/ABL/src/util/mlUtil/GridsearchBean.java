package util.mlUtil;

import org.apache.log4j.Logger;

import weka.classifiers.functions.LibSVM;


public class GridsearchBean {
	private static Logger log = Logger.getLogger(GridsearchBean.class);
	/*private double mCostMin = 1,mCostMax = 2,mCostStep = 2;
	private double mGammaMin = -7,mGammaMax = -5,mGammaStep = 2;*/
	
	private double mCostMin = -5,mCostMax = 15,mCostStep = 2;
	private double mGammaMin = -15,mGammaMax = 3,mGammaStep = 2;
	private LibSVM mLibSVM;
	
	private int mCV = 5;
	private int mRandomSeed = 0;
	
	private static GridsearchBean mGSB;

	public GridsearchBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public LibSVM getLibSVM() {
		return mLibSVM;
	}
	public void setLibSVM(LibSVM libSVM) {
		mLibSVM = libSVM;
	}
	public double getCostMax() {
		return mCostMax;
	}
	public void setCostMax(double costMax) {
		mCostMax = costMax;
	}
	public double getCostMin() {
		return mCostMin;
	}
	public void setCostMin(double costMin) {
		mCostMin = costMin;
	}
	public double getCostStep() {
		return mCostStep;
	}
	public void setCostStep(double costStep) {
		mCostStep = costStep;
	}
	public double getGammaMax() {
		return mGammaMax;
	}
	public void setGammaMax(double gammaMax) {
		mGammaMax = gammaMax;
	}
	public double getGammaMin() {
		return mGammaMin;
	}
	public void setGammaMin(double gammaMin) {
		mGammaMin = gammaMin;
	}
	public double getGammaStep() {
		return mGammaStep;
	}
	public void setGammaStep(double gammaStep) {
		mGammaStep = gammaStep;
	}
	
	public int getCrossValidation() {
		return mCV;
	}
	public void setCrossValidation(int pCV) {
		mCV = pCV;
	}
	public int getRandomSeed() {
		return mRandomSeed;
	}
	public void setRandomSeed(int pRandom) {
		 mRandomSeed = pRandom;
	}
	
	public static GridsearchBean getBean() {
		if(mGSB == null){
			mGSB = new GridsearchBean();
		}

		return mGSB;
	}
}

