package util.mlUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;


/**
 *
 * @author Rishi Das Roy
 * 
 * @Organization Institute Of Genomics & Integrative Biology
 */
public class IMBFS {
	private static Logger log = Logger.getLogger(IMBFS.class);

	private Instances mTrain;
	/**
	 * The base result which needs to be improved.
	 */
	private ExecuteGridsearch mBaseResult;
	
	private ExecuteGridsearch mInterMediate ;
	private Instances mInterMediateTrain;
	
	public IMBFS(ExecuteGridsearch pBaseResult) {
		mBaseResult = pBaseResult;
		mTrain = mBaseResult.getTrain();
	}

	/**
	 * do {
	 * 	noisyAtrbts =  getNoisyAttribute
	 *  if noisyAtrbts empty
	 *  	stop
	 * 	else Accuracy = getAccuracy ( with out noisy atrbts)
	 *  	if  Accuracy < baseAcc
	 *  		DATA = DATA - max Noisy Single Attribute
	 *  		baseAcc = Accuracy(DATA) 
	 *  	else 
	 *  		DATA = DATA - noisyAtrbts
	 *  		baseAcc = Accuracy  
	 *  
	 * while ( true )  
	 * @return 
	 * @throws Exception 
	 */
	public ExecuteGridsearch execute() throws Exception{
		log.info("Started with Attributes " + mTrain.numAttributes());
		do {
			List<Integer> noisyAtrbts = getNoisyAttribute(mTrain, mBaseResult);
			if (noisyAtrbts.size() == 0) {
				return mBaseResult;
			} else {
				Instances reduced = getRemovedAttr(noisyAtrbts);
				ExecuteGridsearch lReduced = getAccuracy(reduced);

				if (noisyAtrbts.size() != (mTrain.numAttributes() - 1)
						&& lReduced.getBestResult() >= mBaseResult
								.getBestResult()) {
					mTrain = reduced;
					mBaseResult = lReduced;
				} else {
					mBaseResult = mInterMediate;
					mTrain = mInterMediateTrain;
				}
			}
			log.info("Reduced to Attributes " + mTrain.numAttributes());
		} while (true);
		 
		
	}
	
	private Instances getRemovedAttr(List<Integer> noisyAtrbts) throws Exception {
		int [] lIndices = new int[noisyAtrbts.size()];

		for (int i = 0; i < lIndices.length; i++) {
			lIndices[i]=noisyAtrbts.get(i);
		}
		
		Remove r = new Remove();
		r.setAttributeIndicesArray(lIndices);
		r.setInputFormat(mTrain);
		return Filter.useFilter(mTrain, r);
	}

	private ExecuteGridsearch getAccuracy(Instances pTrain) throws Exception {
		ExecuteGridsearch lEGS = new ExecuteGridsearch(pTrain);
		lEGS.executeThread();
		return lEGS;
	}

	/**
	 * for each attribute
	 * 		create DATA without that attribute
	 * 		if (Accuracy(DATA) >= BaseAcc)
	 * 			mark it for removal
	 * 
	 * return marked attributes 
	 */
	public List<Integer>  getNoisyAttribute(Instances pInsts,ExecuteGridsearch pBaseResult) throws Exception{
		List<Integer> removalAttr = new ArrayList<Integer>();
		double localBest=pBaseResult.getBestResult();
		for (int i = 1; i < pInsts.numAttributes()-1; i++) {
			System.out.println(pInsts.attribute(i-1).name());
			Remove r = new Remove();
			r.setAttributeIndices(""+i);
			r.setInputFormat(pInsts);
			Instances newData = Filter.useFilter(pInsts, r);
			ExecuteGridsearch lEGS = new ExecuteGridsearch(newData);
			lEGS.executeThread();
			if(pBaseResult.getBestResult() < lEGS.getBestResult()){
				if(localBest < lEGS.getBestResult()){
					localBest =lEGS.getBestResult();
					mInterMediate=lEGS;
					mInterMediateTrain = newData;
					removalAttr.add(0, i);
				}else {
					removalAttr.add(i);					
				}
			}
			log.info("Attr="+pInsts.attribute(i-1).name()+",index="+i+",result="+lEGS.getBestResult());
		}
		
		log.info("Selected indices");
		for (Integer integer : removalAttr) {
			log.info(""+integer);
		}
		
		return removalAttr;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DataSource source = new DataSource("/home/rishi.das/ABL_wrkspc/test/result.arff");
			Instances train = source.getDataSet();
			train.setClassIndex(train.numAttributes()-1);
			
			ExecuteGridsearch lEGS = new ExecuteGridsearch(train);
			lEGS.executeThread();
			log.info("base="+lEGS.getBestResult());
			IMBFS i = new IMBFS(lEGS);
			i.execute();
			 
			
		} catch (Exception e) {
			log.error("Fatal error ",e);
		}
	}

}
