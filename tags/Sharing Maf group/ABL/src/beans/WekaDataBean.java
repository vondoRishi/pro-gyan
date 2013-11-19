package beans;

import java.util.List;

import org.apache.log4j.Logger;

import weka.core.Instances;


public class WekaDataBean {
	private static Logger log = Logger.getLogger(WekaDataBean.class);
	Instances mInst;
	List<String> mInstanceId;
	
	public Instances getInst() {
		return mInst;
	}
	public void setInst(Instances mInst) {
		this.mInst = mInst;
	}
	public List<String> getInstanceId() {
		return mInstanceId;
	}
	public void setInstanceId(List<String> mInstanceId) {
		this.mInstanceId = mInstanceId;
	}
}

