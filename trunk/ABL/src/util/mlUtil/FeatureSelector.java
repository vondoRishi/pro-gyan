package util.mlUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import feature.util.FileUtil;



public class FeatureSelector {


	
	private List<AttributeScore> mRankedAttr;

	public FeatureSelector(Instances pTrain, FeatureEvaluator lfe) throws Exception {
		
		mRankedAttr = lfe.evaluate(pTrain);
	}

	public List<AttributeScore> getRankedAttr() {
		return mRankedAttr;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		DataSource source = new DataSource("pathTo.arff");
		Instances train = source.getDataSet();
		train.setClassIndex(train.numAttributes()-1);
		FeatureEvaluator lFE = new FeatureEvaluator(FeatureEvaluator.FCBF);
		FeatureSelector lFeatureSelector = new FeatureSelector(train,lFE);
		lFeatureSelector.writeList("/pathTo/fscore.txt");
		//lFeatureSelector.printScore();
		int [] ftrs = {10,50,100,200,400};
		for (int i : ftrs) {
			Instances lTopInstance = lFeatureSelector.getTopNFeature(i,false,train);
			ArffSaver saver = new ArffSaver();
			  saver.setInstances(lTopInstance);
			  saver.setFile(new File("/home/rishi.das/tmp/EColi_Features/OtherLearner/"+"master_"+i+".arff"));
			  saver.writeBatch();
		}

	}

	public Instances getTopNFeature(int pNTop, boolean pIsInvrtSelection, Instances pTrain) throws Exception {
		Remove  remove = new Remove();
	    int [] lTopIndices = new int[pNTop+1];
	    for (int i = 0; i < lTopIndices.length; i++) {
			lTopIndices[i]=mRankedAttr.get(i).getAttributeIndex();
		}
	    lTopIndices[lTopIndices.length-1]=pTrain.classIndex();
		remove.setAttributeIndicesArray(lTopIndices);
		remove.setInvertSelection(!pIsInvrtSelection);
		remove.setInputFormat(pTrain);
		return Filter.useFilter(pTrain, remove);
	}

	private void printScore() {
		for (AttributeScore lAsc : mRankedAttr) {
			System.out.println(lAsc.getAttributeIndex()+1+": "+lAsc.getScore());
		}

	}

	public void writeList(String pFilePath) throws Exception {
		List<String> lASList = new ArrayList<String>();
		for (AttributeScore lAS : mRankedAttr) {
			lASList.add(lAS.toString());
		}
		try {
			FileUtil.writeList(pFilePath, lASList);
		} catch (IOException e) {
			throw new Exception("Failed to wright Attribute Scoree list");
		}

	}

	public void reportTopScore() {
		if(mRankedAttr!=null && mRankedAttr.size()>0){
			AttributeScore lAsc = mRankedAttr.get(0);
			System.out.println(lAsc.getAttributeIndex()+1+": "+lAsc.getScore());
		}
	}

	public Instances getTopNFeature(Instances test_scale, Instances train,
			boolean pIsInvrtSelection) throws Exception {
		Remove  remove = new Remove();
	    int [] lTopIndices = new int[train.numAttributes()];
	    for (int i = 0; i < lTopIndices.length; i++) {
			lTopIndices[i]=mRankedAttr.get(i).getAttributeIndex();
		}
	    lTopIndices[lTopIndices.length-1]=test_scale.classIndex();
		remove.setAttributeIndicesArray(lTopIndices);
		remove.setInvertSelection(!pIsInvrtSelection);
		remove.setInputFormat(test_scale);
		return Filter.useFilter(test_scale, remove);
	}
}

