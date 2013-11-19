package util;

import static util.constants.FileName.*;
import static util.constants.UIlabels.*;
import feature.util.FileUtil;
import gui.BrowseExperiment.NodeInfo;
import gui.ClassifiersWindow;
import gui.ExperimentBasic;
import gui.Summary;
import gui.TestSummary;
import gui.TrainingDataWindow;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import beans.ExperimentDataBean;

/**
 * @author Rishi Das Roy
 *
 * @Organization Institute Of Genomics & Integrative Biology
 */
public class SystemUtil {

	

	private static Logger log = Logger.getLogger(SystemUtil.class);

	private static JFileChooser fc ;

	private static boolean sInitialized =false;

	private static final String sLogPath =getHomePath() + File.separator
			+ getDateTime() + ".log";
	
	static {
		try {
			intialize() ;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Fatal error ",e);
		}
	}
	
	public static String getHomePath() {
		return UserPreference.getHome();
	}

	public static String getDateTime() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
		df.setTimeZone(TimeZone.getTimeZone("PST"));
		return df.format(new Date());
	}

	public static void showErrMsg(JFrame frame, String pMsg) {
		JOptionPane.showMessageDialog(frame, pMsg, ERROR,
				JOptionPane.ERROR_MESSAGE);

	}

	public static void showTraingErrMsg(String pMsg) {
		 // create a JTextArea
	      JTextArea textArea = new JTextArea(6, 25);
	      textArea.setText(pMsg);
	      
	      
	      // wrap a scrollpane around it
	      JScrollPane scrollPane = new JScrollPane(textArea);
	      //textArea.setEditable(false);
		JOptionPane.showMessageDialog(TrainingDataWindow.getFrame(), scrollPane, ERROR,
				JOptionPane.ERROR_MESSAGE);

	}

	public static Double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));

	}

	public static String getClassPath(String pPath) {
		String file = pPath.getClass().getResource(pPath)
				.getFile();
		return file;
	}

	public static String getWorkSpace() {
		File l = new File(getHomePath()+File.separator+ABL_WRKSPC);
		if(!l.exists())
			l.mkdirs();

		return l.getAbsolutePath();
	}

	public static String choosefile(Component pC, String pTitle) {

		if(fc==null){
			fc = new JFileChooser();
		}
		fc.setDialogTitle(pTitle);
		fc.setFileFilter(CustomFilter.getFaaFilter());
		fc.setFileFilter(CustomFilter.getFastaFilter());
		fc.setAcceptAllFileFilterUsed(true);

		int returnVal = fc.showOpenDialog(pC);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile().getAbsolutePath();
		} else {
			//  log.append("Open command cancelled by user." + newline);
			return null;
		}

	}
	
	public static String savefile(Component pC, String pTitle, String pFileName) {

		JFileChooser pgcFC = new JFileChooser();
		pgcFC.setSelectedFile(new File(pFileName));
		pgcFC.setDialogTitle(pTitle);
		pgcFC.setFileFilter(CustomFilter.getPgcFilter());
		pgcFC.setAcceptAllFileFilterUsed(true);

		int returnVal = pgcFC.showDialog(pC,pTitle);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return pgcFC.getSelectedFile().getAbsolutePath();
		} else {
			return null;
		}

	}

	public static boolean getUserConfirmation(String pMessage) {
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(TrainingDataWindow.getFrame(), pMessage, PLEASE_CONFIRM,
				JOptionPane.YES_NO_OPTION);

	}

	public static String getWorkDir(ExperimentDataBean pExperimentDataBean) {
		return getWorkSpace()+File.separator+pExperimentDataBean.getName();

	}

	public static JPanel getPanel(NodeInfo lNI) throws Exception {
		
		if(lNI.isBasic()){
			ExperimentDataBean lEB = (ExperimentDataBean) FileUtil.getObjectFromXml(lNI.getPath()+File.separator+EXP_XML);
			return new ExperimentBasic(lEB);
		}else if(lNI.isLearn()){
			
			return new Summary(lNI.getPath());
		}else if(lNI.isTest()){
			return new TestSummary(lNI.getPath());
		}else {
			return null;
		}
		
	}

	public static String getLogPath() {
		return sLogPath;
	}

	public static void intialize() throws IOException {
		if(!sInitialized){
			Log4jUtil.reconfigure(SystemUtil.getLogPath());
			sInitialized =true;
			log.info("Log4j is working");
			/*InputStream configStream = sConfig.getClass().getResourceAsStream(CONFIG_PROPERTIES);
			sConfig.load(configStream);*/
		}
	}
	

	

	public static int getNumberOfThreads() {
		return (Integer) UserPreference.getNumberOfThreads();
	}

	public static ExperimentDataBean getExperimentDataBean(String lPath) throws Exception {
		// TODO Auto-generated method stub
		return (ExperimentDataBean) FileUtil.getObjectFromXml(lPath+File.separator+EXP_XML);
	}

	public static void importModel(String lModelPath) throws Exception {
		String outDir = getModelSpace();
		util.FileUtil.unzip(lModelPath, outDir);
		
	}

	public static String getModelSpace() {
		File l = new File(getHomePath()+File.separator+ABL_MODELSPC);
		if(!l.exists())
			l.mkdirs();

		return l.getAbsolutePath();
	}

	public static void showPredictErrMsg(String pMsg) {
		JOptionPane.showMessageDialog(ClassifiersWindow.getFrame(), pMsg, ERROR,
				JOptionPane.ERROR_MESSAGE);
	}

	public static boolean getPredictUserConfirmation(String pMessage) {
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(ClassifiersWindow.getFrame(), pMessage, PLEASE_CONFIRM,
				JOptionPane.YES_NO_OPTION);
	}

	public static String saveTextFasta(JTextArea pTextSeq) throws Exception {
		File tmpFasta = File.createTempFile(getDateTime(), "");
		if (tmpFasta.exists()) {
			tmpFasta.delete();
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(tmpFasta);
			pTextSeq.write(writer);
		} catch (IOException exception) {
			throw new Exception("Save oops");

		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException exception) {
					throw new Exception("Error closing writer");

				}
			}
		}
		return tmpFasta.getAbsolutePath();
	}

	
}
