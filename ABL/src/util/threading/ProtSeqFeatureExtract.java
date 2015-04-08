package util.threading;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import feature.common.Extractor;
import feature.common.Feature;
import feature.common.commonIntrface.Instance;


public class ProtSeqFeatureExtract implements Callable<List<Feature>> {
	private static Logger log = Logger.getLogger(ProtSeqFeatureExtract.class);
	private Extractor mExt;
	private Instance mInstance;
	List<Feature> mFeatureList;

	public List<Feature> getFeatureList() {
		return mFeatureList;
	}

	public ProtSeqFeatureExtract(Extractor pExt, Instance lInstance) {
		mExt = pExt;
		mInstance = lInstance;
	}

	@Override
	public List<Feature> call() throws Exception {
		System.out.println("Started "+getInstanceId());
		return (mFeatureList=mExt.extract(((String)mInstance.getInstanceData()).trim()));
	}

	public String getInstanceId() {
		return mInstance.getInstanceId();
	}
}

