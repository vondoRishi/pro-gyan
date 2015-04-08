package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import static util.constants.FileName.*;

public class Log4jUtil {
	
	private static Logger log = Logger.getLogger(Log4jUtil.class);
	private static Properties props = new Properties();
	public static void reconfigure(String pLogFilePath) {

	      try {
	           InputStream configStream = props.getClass().getResourceAsStream(LOG4J_PROPERTIES);
	           props.load(configStream);
	           configStream.close();
	      } catch(IOException e) {
	          System.out.println("Error: Cannot laod configuration file ");
	      }
	     // props.setProperty("log4j.rootLogger","DEBUG, file");
	      props.setProperty("log4j.appender.A1.File",pLogFilePath);
	      LogManager.resetConfiguration();
	      PropertyConfigurator.configure(props);

	}

	public static void reset() {
		Properties props = new Properties();
	      try {
	           InputStream configStream = props.getClass().getResourceAsStream(LOG4J_PROPERTIES);
	           props.load(configStream);
	           configStream.close();
	      } catch(IOException e) {
	          System.out.println("Error: Cannot laod configuration file ");
	      }
	      PropertyConfigurator.configure(props);

	}

	public static String getLogFilePath() {

		return props.getProperty("log4j.appender.A1.File");
	}
}

