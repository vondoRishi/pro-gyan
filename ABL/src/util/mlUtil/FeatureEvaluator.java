package util.mlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import weka.core.Instance;
import weka.core.Instances;



public class FeatureEvaluator {

	public static final int FSCORE = 1;
	public static final int INFO_GAIN = 2;
	private int mEvaluator = FSCORE;

	public FeatureEvaluator(int pChoice) {
		switch (pChoice) {
		case FSCORE:
			mEvaluator = FSCORE;
			break;

		case INFO_GAIN:

			break;

		default:
			mEvaluator = FSCORE;
			break;
		}
	}

	public List<AttributeScore> evaluate(Instances pTrain){
		List<AttributeScore> score = null;
		switch (mEvaluator) {
		case FSCORE:
			score = calFScore( pTrain);
			break;

		case INFO_GAIN:

			break;

		default:
			break;
		}
		return score;
	}

	private List<AttributeScore> calFScore(Instances pTrain) {
		List<AttributeScore> lScores = new ArrayList<AttributeScore>();
		int mPositiveClass = 0;
		int mNegativeClass=0;

		for (int j = 0; j < pTrain.numInstances(); j++) {
			if(pTrain.instance(j).classValue()==0){
				mPositiveClass++;
			}else{
				mNegativeClass++;
			}
		}
		for (int i = 0; i < pTrain.numAttributes()-1; i++) {
			List<Double> mPositiveValue = new ArrayList<Double>();
			List<Double> mNegativeValue = new ArrayList<Double>();
			Double lTotalSum = 0d;
			Double lTotalPosSum = 0d;
			for (int j = 0; j < pTrain.numInstances(); j++) {
				Instance lInstnce = pTrain.instance(j);
				Double value = lInstnce.value(i);
				if(value!=null){
					lTotalSum+=value;
					if(lInstnce.classValue()==0){
						lTotalPosSum+=value;
						mPositiveValue.add(value);
					}else{
						mNegativeValue.add(value);
					}
				}
			}

			if(mPositiveValue.size()>1 && mNegativeValue.size()>1){

				Double lTotalMean = lTotalSum/(mPositiveClass+mNegativeClass);
				Double lPosMean = lTotalPosSum/mPositiveClass;
				Double lNegMean = (lTotalSum - lTotalPosSum)/mNegativeClass;
				Double lNumerator = mPositiveClass*(lTotalMean - lPosMean)*(lTotalMean - lPosMean)
									+ mNegativeClass*(lTotalMean - lNegMean)*(lTotalMean - lNegMean);

				Double lPosSqureSum = 0d;
				Double lNegSqureSum = 0d;
				for (Double double1 : mPositiveValue) {
					//lPosSqureSum += (double1 - lPosMean)*(double1 - lPosMean);
					lPosSqureSum += (double1)*(double1 );
				}
//				lPosSqureSum = lPosSqureSum/(mPositiveValue.size()-1);
				lPosSqureSum = lPosSqureSum - lTotalPosSum*lTotalPosSum/mPositiveClass;

				for (Double double1 : mNegativeValue) {
					//lNegSqureSum += (double1 - lNegMean)*(double1 - lNegMean);
					lNegSqureSum += (double1)*(double1 );
				}
//				lNegSqureSum = lNegSqureSum/(mNegativeValue.size()-1);
				lNegSqureSum = lNegSqureSum - (lTotalSum - lTotalPosSum)*(lTotalSum - lTotalPosSum)/(mNegativeClass);

				Double lScore = lNumerator/(lPosSqureSum+lNegSqureSum+1e-12);
				lScores.add(new AttributeScore(i,pTrain.attribute(i).name(),lScore));
			}
		}
		Collections.sort(lScores);

		return lScores;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}

