package mlGui;

import java.io.IOException;

import org.apache.log4j.Logger;

import feature.common.Config;
import feature.common.commonIntrface.ConfigBean;
import feature.common.commonIntrface.FeatureExtractor;


public class GuiFeatureConfig extends Config {
	public GuiFeatureConfig(String pFtrFactoryClassPath,
			String pFtrBeanFactoryClassPath) {
		super(pFtrFactoryClassPath, pFtrBeanFactoryClassPath);
		// TODO Auto-generated constructor stub
	}
	
	public FeatureExtractor[] getConfig() throws IOException {

		String[] lFtrExtrcNames = getFtrExtrcName();
//		String[] lSlctdFtrExtrcNames = getReqrdFtrExtrctrs(lFtrExtrcNames);
		return getFtrExtrcBean(lFtrExtrcNames);
	}
	
	private  FeatureExtractor[] getFtrExtrcBean(
			String[] pSlctdFtrExtrcNames) {
		FeatureExtractor[] lFE = new FeatureExtractor[pSlctdFtrExtrcNames.length];
		ConfigBean[] lCB = getConfigBean(pSlctdFtrExtrcNames);
		for (int i = 0; i < pSlctdFtrExtrcNames.length; i++) {
			lFE[i] = (FeatureExtractor) mFtrFactory
					.getBean(pSlctdFtrExtrcNames[i]);
			lFE[i].requirement(lCB[i]);
		}
		
		return lFE;
	}
	
	private  ConfigBean[] getConfigBean(String[] pSlctdFtrExtrcNames) {
		ConfigBean[] lCB = new ConfigBean[pSlctdFtrExtrcNames.length];
		for (int i = 0; i < pSlctdFtrExtrcNames.length; i++) {
			lCB[i] = (ConfigBean) mFtrBeanFactory.getBean(pSlctdFtrExtrcNames[i]);
		}
		return lCB;
	}

	private static Logger log = Logger.getLogger(GuiFeatureConfig.class);
}

