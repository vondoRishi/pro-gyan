package util.mlUtil;

import static util.constants.FileName.MASTER_ARFF;
import static util.constants.FileName.RESULT_ARFF;
import static util.constants.FileName.SCALE_XML;
import static util.constants.FileName.SUMMARY_XML;
import static util.constants.FileName.TEST_RESULT_XML;

import java.io.File;
import java.io.IOException;

import mlGui.wekaCustom.PreprocessPanelCustom;

import org.apache.log4j.Logger;

import util.UserPreference;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import feature.util.FileUtil;


public class WekaUtil {
	
	private static Logger log = Logger.getLogger(WekaUtil.class);
	private static boolean sProb = UserPreference.getSvmProb();

	public static Instances normalizeTrainSet(Instances pTrain, String pPath) throws Exception {
		Normalize lN = new Normalize();
		lN.setInputFormat(pTrain);
		
		pTrain = Filter.useFilter(pTrain, lN);
//		Instances newTest = Filter.useFilter(test, filter);
		pTrain.setClassIndex(pTrain.numAttributes() - 1);
		saveScale(lN,pPath);
		return pTrain;
	}
	
	public static Instances normalizeTestSet(Instances pTest, String pPath) throws Exception{
		Normalize lN = (Normalize) FileUtil.getObjectFromXml(pPath+File.separator+SCALE_XML);
		String lRelation = pTest.relationName();
		pTest = Filter.useFilter(pTest, lN);
		pTest.setClassIndex(pTest.numAttributes() - 1);
		pTest.setRelationName(lRelation);
		return pTest;
	}
	
	public static void saveArff(Instances train, String pPath) throws IOException {
		ArffSaver saver = new ArffSaver();
		saver.setInstances(train);
		saver.setFile(new File(pPath));
		// saver.setDestination(new File("./data/test.arff")); // **not**
		// necessary in 3.5.4 and later
		saver.writeBatch();
	}
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		DataSource source = new DataSource("/home/rishi.das/ABL_wrkspc/segment-test.arff");
		/*Instances train = source.getDataSet();
		train.setClassIndex(train.numAttributes()-1);
		train = normalizeTrainSet(train, "/home/rishi.das/ABL_wrkspc/");
		train.setClassIndex(train.numAttributes() - 1);
		
		source = new DataSource("/home/rishi.das/ABL_wrkspc/test0.arff");
		Instances test = source.getDataSet();
		test.setClassIndex(test.numAttributes()-1);
		test = normalizeTestSet(test, "/home/rishi.das/ABL_wrkspc/");
		test.setClassIndex(test.numAttributes() - 1);
		saveArff(test, "/home/rishi.das/ABL_wrkspc/scale_test0.arff");*/
		
		
		source = new DataSource("/home/rishi.das/ABL_wrkspc/test.arff");
		Instances test = source.getDataSet();
		test.setClassIndex(test.numAttributes()-1);
		test = normalizeTestSet(test, "/home/rishi.das/ABL_wrkspc/");
		test.setClassIndex(test.numAttributes() - 1);
		saveArff(test, "/home/rishi.das/ABL_wrkspc/scale_test.arff");
		

	}

	public static void saveScale(Normalize lN, String pPath) {
		FileUtil.writeToXML(pPath+File.separator+SCALE_XML, lN);
		
	}

	public static Instances getMaster(String pPath) throws Exception {
		DataSource source = new DataSource(pPath+File.separator+MASTER_ARFF);
		Instances train = source.getDataSet();
		train.setClassIndex(train.numAttributes()-1);
		return train;
	}

	public static PreprocessPanelCustom getPreprocessPanel(String pPathToARFF, String mPath) throws Exception {
		DataSource source = new DataSource(pPathToARFF);
		Instances inst = source.getDataSet();
		inst.setClassIndex(inst.numAttributes()-1);
		PreprocessPanelCustom lP = new PreprocessPanelCustom(mPath);
		lP.setInstances(inst);
		return lP;
	}

	public static ExecuteGridsearch getExecuteGridsearch(String pLearnPath) throws Exception {
		return (ExecuteGridsearch) FileUtil.getObjectFromXml(pLearnPath+File.separator+SUMMARY_XML);
		
	}

	public static void saveResultArff(Instances train, String pPath) throws IOException {
		ArffSaver saver = new ArffSaver();
		saver.setInstances(train);
		saver.setFile(new File(pPath+ File.separator+ RESULT_ARFF));
		// saver.setDestination(new File("./data/test.arff")); // **not**
		// necessary in 3.5.4 and later
		saver.writeBatch();
	}
	
	public static Instances getResult(String pPath) throws Exception {
		DataSource source = new DataSource(pPath+File.separator+RESULT_ARFF);
		Instances train = source.getDataSet();
		train.setClassIndex(train.numAttributes()-1);
		return train;
	}

	public static Evaluation getTestEvaluation(String pPath) throws Exception {
		return  (Evaluation) FileUtil.getObjectFromXml(pPath+File.separator+TEST_RESULT_XML);
	}

	public static Instances getArff(String pPath) throws Exception {
		DataSource source = new DataSource(pPath);
		Instances train = source.getDataSet();
		train.setClassIndex(train.numAttributes()-1);
		return train;
	}

	public static double getMCC(Evaluation pResult) {
		double TP = pResult.numTruePositives(0);
		double TN = pResult.numTrueNegatives(0);
		
		double FP = pResult.numFalsePositives(0);
		double FN = pResult.numFalseNegatives(0);
		double mcc;
//		System.out.println("TP="+TP+", TN"+TN+", FP="+FP+", FN"+FN);
		try {
			mcc = ((TP * TN) - (FP * FN))/Math.sqrt((TP+FP)* (TP + FN) *( TN+FP ) *(TN + FN));
			if(Double.isNaN(mcc)){
				log.info("Fatal error MCC; TP="+TP+", TN"+TN+", FP="+FP+", FN"+FN);
				mcc=0;
			}
		} catch (Exception e) {
			log.error("Fatal error MCC; TP="+TP+", TN"+TN+", FP="+FP+", FN"+FN,e);
			mcc=0;
		} 
		return mcc;
	}

	public static boolean isProbabilistic() {
		return sProb ;
	}
	
	
}

