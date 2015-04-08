package util.mlUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.FCBFSearch;
import weka.attributeSelection.SymmetricalUncertAttributeSetEval;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class FeatureEvaluator {

	/**
	 * LibSVM fselect
	 */
	public static final int FSCORE = 1;

	public static final int INFO_GAIN = 2;
	/**
	 * Lei Yu, Huan Liu: Feature Selection for High-Dimensional Data: A Fast
	 * Correlation-Based Filter Solution. In: Proceedings of the Twentieth
	 * International Conference on Machine Learning,
	 */
	public static final int FCBF = 3;
	
	private int mEvaluator = FSCORE;

	public int getEvaluator() {
		return mEvaluator;
	}

	public FeatureEvaluator(int pChoice) {
		switch (pChoice) {
		case FSCORE:
			mEvaluator = FSCORE;
			break;

		case INFO_GAIN:

			break;
		
		case FCBF:
			mEvaluator = FCBF;
			break;

		default:
			mEvaluator = FSCORE;
			break;
		}
	}

	public List<AttributeScore> evaluate(Instances pTrain) throws Exception {
		List<AttributeScore> score = null;
		switch (mEvaluator) {
		case FSCORE:
			score = calFScore(pTrain);
			break;

		case INFO_GAIN:

			break;
			
		case FCBF:
			score = calFCBFcore(pTrain);
			break;

		default:
			break;
		}
		return score;
	}

	private List<AttributeScore> calFCBFcore(Instances pTrain) throws Exception {
		AttributeSelection attsel = new AttributeSelection();
		SymmetricalUncertAttributeSetEval lSUASE = new SymmetricalUncertAttributeSetEval();
		FCBFSearch lFCBF = new FCBFSearch();
		lFCBF.setGenerateRanking(true);
		lFCBF.setNumToSelect(-1);
		
		attsel.setEvaluator(lSUASE);
		attsel.setSearch(lFCBF);
		attsel.SelectAttributes(pTrain);
		
		List<AttributeScore> lScores = new ArrayList<AttributeScore>();
		double[][] lAttrScore = attsel.rankedAttributes();
		
		for (int j = 0; j < lAttrScore.length; j++) {
//			System.out.println((int)lAttrScore[j][0]+" "+Utils.doubleToString(lAttrScore[j][1],7,7)+" "+pTrain.attribute((int) lAttrScore[j][0]).name());
			lScores.add(new AttributeScore((int)lAttrScore[j][0], pTrain.attribute((int) lAttrScore[j][0]).name(),
					lAttrScore[j][1]));
		}
		
		return lScores;
	}

	private List<AttributeScore> calFScore(Instances pTrain) {
		List<AttributeScore> lScores = new ArrayList<AttributeScore>();
		int mPositiveClass = 0;
		int mNegativeClass = 0;

		for (int j = 0; j < pTrain.numInstances(); j++) {
			if (pTrain.instance(j).classValue() == 0) {
				mPositiveClass++;
			} else {
				mNegativeClass++;
			}
		}
		for (int i = 0; i < pTrain.numAttributes() - 1; i++) {
			List<Double> mPositiveValue = new ArrayList<Double>();
			List<Double> mNegativeValue = new ArrayList<Double>();
			Double lTotalSum = 0d;
			Double lTotalPosSum = 0d;
			for (int j = 0; j < pTrain.numInstances(); j++) {
				Instance lInstnce = pTrain.instance(j);
				Double value = lInstnce.value(i);
				if (value != null) {
					lTotalSum += value;
					if (lInstnce.classValue() == 0) {
						lTotalPosSum += value;
						mPositiveValue.add(value);
					} else {
						mNegativeValue.add(value);
					}
				}
			}

			if (mPositiveValue.size() > 1 && mNegativeValue.size() > 1) {

				Double lTotalMean = lTotalSum
						/ (mPositiveClass + mNegativeClass);
				Double lPosMean = lTotalPosSum / mPositiveClass;
				Double lNegMean = (lTotalSum - lTotalPosSum) / mNegativeClass;
				Double lNumerator = mPositiveClass * (lTotalMean - lPosMean)
						* (lTotalMean - lPosMean) + mNegativeClass
						* (lTotalMean - lNegMean) * (lTotalMean - lNegMean);

				Double lPosSqureSum = 0d;
				Double lNegSqureSum = 0d;
				for (Double double1 : mPositiveValue) {
					// lPosSqureSum += (double1 - lPosMean)*(double1 -
					// lPosMean);
					lPosSqureSum += (double1) * (double1);
				}
				// lPosSqureSum = lPosSqureSum/(mPositiveValue.size()-1);
				lPosSqureSum = lPosSqureSum - lTotalPosSum * lTotalPosSum
						/ mPositiveClass;

				for (Double double1 : mNegativeValue) {
					// lNegSqureSum += (double1 - lNegMean)*(double1 -
					// lNegMean);
					lNegSqureSum += (double1) * (double1);
				}
				// lNegSqureSum = lNegSqureSum/(mNegativeValue.size()-1);
				lNegSqureSum = lNegSqureSum - (lTotalSum - lTotalPosSum)
						* (lTotalSum - lTotalPosSum) / (mNegativeClass);

				Double lScore = lNumerator
						/ (lPosSqureSum + lNegSqureSum + 1e-12);
				lScores.add(new AttributeScore(i, pTrain.attribute(i).name(),
						lScore));
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
