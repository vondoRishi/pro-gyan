package beans;

import org.apache.log4j.Logger;

public class PredictionDataBean {

	private static Logger log = Logger.getLogger(PredictionDataBean.class);
	private String mInput;

	private String mInfo;

	private String mModelName;

	public PredictionDataBean(String mModelName, String mInput) {
		super();
		this.mModelName = mModelName;
		this.mInput = mInput;
	}

	/**
	 * initialize ModelName and Input
	 */
	public PredictionDataBean() {
		// TODO Auto-generated constructor stub
	}

	public String getModelName() {
		return mModelName;
	}

	public void setModelName(String pModelName) {
		mModelName = pModelName;
	}

	public void setUserInput(String pInput) {
		mInput = pInput;
	}

	public String getInput() {
		return mInput;
	}

	public void setInfo(String pInfo) {
		mInfo = pInfo;
	}
	
	public String getInfo() {
		return mInfo;
	}



}
