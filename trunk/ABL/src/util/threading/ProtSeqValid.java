package util.threading;

import static protein.Constants.aminos;

import java.util.Arrays;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import feature.common.commonIntrface.Instance;


public class ProtSeqValid implements Callable<String> {
	private static Logger log = Logger.getLogger(ProtSeqValid.class);
	public static final int minSeqLength = 10;
	private Instance mInstance;

	public ProtSeqValid(Instance pInstance) {
		mInstance = pInstance;
		
	}

	@Override
	public String call() throws Exception {
		char[] seq = ((String) mInstance.getInstanceData()).toCharArray();
		String msg = null;
		for (char c : seq) {
			if(Arrays.binarySearch(aminos, c + "")<0){
				msg = mInstance.getInstanceId()+" contains invalid character '"+c+"'";
			}
		}
		if(msg == null && seq.length < minSeqLength){
			msg = mInstance.getInstanceId()+" length is less than "+minSeqLength;
		}
		return msg;
	}
}

