package gui;

import static util.constants.FileName.MODEL_DESCRIPTION_XML;
import static util.constants.FileName.RESULT_ARFF;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import mlGui.wekaCustom.PreprocessPanelCustom;
import mlGui.wekaCustom.ROCweka;

import org.apache.log4j.Logger;

import util.BioUtil;
import util.SystemUtil;
import util.constants.FileName;
import util.mlUtil.WekaUtil;
import weka.classifiers.Evaluation;
import weka.core.Utils;
import beans.ExperimentDataBean;
import feature.util.FileUtil;



public class ModelSummary extends JPanel implements ActionListener {
	
	private static Logger log = Logger.getLogger(ModelSummary.class);
	private JPanel contentPane;
	private Evaluation mResult;
	private JButton mBtnRocCurve;
	private JButton mBtnShowFeatures;
	private PreprocessPanelCustom mPreprocess;
	/**
	 * Path in the directory
	 */
	private String mPath;
	private ExperimentDataBean mEB;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//read it from disk and instantiate it.
					//Summary frame = new Summary(null);
					//frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @param mResult 
	 */
	/**
	 * @param pPath
	 * @throws Exception 
	 */
	public ModelSummary(String pPath) throws Exception {
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mPath = pPath;
		mResult = WekaUtil.getExecuteGridsearch(pPath+File.separator+FileName.MODEL).getBestEvaluation();
		BioUtil.consoleSummary(mResult);
		setSize(468, 336);
		contentPane = this;
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//setContentPane(contentPane);
		contentPane.setLayout(null);
		setLayout(null);
		
		mBtnRocCurve = new JButton("ROC");
		mBtnRocCurve.setBounds(322, 273, 98, 24);
		mBtnRocCurve.addActionListener(this);
		contentPane.add(mBtnRocCurve);
		
		mBtnShowFeatures = new JButton("Features");
		mBtnShowFeatures.setEnabled(true);
		mBtnShowFeatures.setBounds(324, 289, 98, 24);
		mBtnShowFeatures.addActionListener(this);
//		contentPane.add(mBtnShowFeatures);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Learning Metrics", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel.setBounds(290, 24, 166, 225);
		add(panel);
		panel.setLayout(null);
		
		//sensitivity or true positive rate (TPR) or Recall
		JLabel lblTpRate = new JLabel("Sensitivity:");
		lblTpRate.setBounds(12, 61, 82, 14);
		panel.add(lblTpRate);
		lblTpRate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTpRate.setToolTipText("= TP / P = TP / (TP + FN)");
		
		JLabel labelTpRslt = new JLabel(Utils.doubleToString(mResult.truePositiveRate(0),7, 2));
		labelTpRslt.setHorizontalAlignment(SwingConstants.LEFT);
		labelTpRslt.setBounds(104, 61, 50, 14);
		panel.add(labelTpRslt);

//		specificity (SPC) or True Negative Rate
		JLabel lblSpcRate = new JLabel("Specificity:");
		lblSpcRate.setBounds(12, 94, 82, 14);
		panel.add(lblSpcRate);
		lblSpcRate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSpcRate.setToolTipText("= TN / N = TN / (FP + TN)");
		
		JLabel labelSpcRslt = new JLabel(Utils.doubleToString(mResult.trueNegativeRate(0),7, 2));
		labelSpcRslt.setHorizontalAlignment(SwingConstants.LEFT);
		labelSpcRslt.setBounds(104, 94, 50, 14);
		panel.add(labelSpcRslt);
		
		JLabel lblPrecision = new JLabel("Precision:");
		lblPrecision.setBounds(12, 124, 82, 14);
		panel.add(lblPrecision);
		lblPrecision.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPrecision.setToolTipText("= TP / (TP + FP)");
		
		JLabel labelPrecsnResult = new JLabel(Utils.doubleToString(mResult.precision(0),7, 2));
		labelPrecsnResult.setHorizontalAlignment(SwingConstants.LEFT);
		labelPrecsnResult.setBounds(104, 124, 50, 14);
		panel.add(labelPrecsnResult);
		
		JLabel lblFmeasure = new JLabel("F-measure:");
		lblFmeasure.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFmeasure.setBounds(12, 150, 82, 14);
		panel.add(lblFmeasure);
		lblFmeasure.setToolTipText("2 * Sensitivity * Precision / ( Sensitivity + Precision )");
		
		JLabel labelFmsrResult = new JLabel(Utils.doubleToString(mResult.fMeasure(0),7, 2));
		labelFmsrResult.setHorizontalAlignment(SwingConstants.LEFT);
		labelFmsrResult.setBounds(104, 150, 50, 14);
		panel.add(labelFmsrResult);
		
		JLabel lblRocArea = new JLabel("ROC Area:");
		lblRocArea.setBounds(12, 199, 82, 14);
		panel.add(lblRocArea);
		lblRocArea.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel labelRocResult = new JLabel(Utils.doubleToString(mResult.weightedAreaUnderROC(),7, 2));
		labelRocResult.setHorizontalAlignment(SwingConstants.LEFT);
		labelRocResult.setBounds(104, 199, 50, 14);
		panel.add(labelRocResult);
		
		JLabel lblAccuracy = new JLabel("Accuracy:");
		lblAccuracy.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAccuracy.setBounds(12, 31, 82, 14);
		panel.add(lblAccuracy);
		lblAccuracy.setToolTipText("= (TP + TN) / (P + N)");
		
		JLabel lblNumFtrRslt = new JLabel(Utils.doubleToString(mResult.pctCorrect(),7, 2));
		lblNumFtrRslt.setHorizontalAlignment(SwingConstants.LEFT);
		lblNumFtrRslt.setBounds(104, 31, 50, 14);
		panel.add(lblNumFtrRslt);
		
		JLabel lblMcc = new JLabel("MCC:");
		lblMcc.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMcc.setToolTipText("(TP * TN - FP * FN)/sqrt((TP+FP)* (TP + TN) *(FP + FN) *(TN + FN))");
		lblMcc.setBounds(12, 176, 82, 14);
		panel.add(lblMcc);
		
		JLabel lbl_MCCResult = new JLabel(Utils.doubleToString(WekaUtil.getMCC(mResult),7, 2));
		lbl_MCCResult.setHorizontalAlignment(SwingConstants.LEFT);
		lbl_MCCResult.setBounds(104, 173, 50, 14);
		panel.add(lbl_MCCResult);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 24, 275, 289);
		add(scrollPane);
		
		
		mEB = (ExperimentDataBean) FileUtil.getObjectFromXml(pPath+File.separator+MODEL_DESCRIPTION_XML);
		JLabel lblNewLabel = new JLabel("<html><br>&nbsp;" + mEB.getDescription() + "<br><hr><br>&nbsp;Number of proteins used in this<br>&nbsp;model <br>&nbsp;" +
				mEB.getPos_label()+ " (Positive) : " + mEB.getPos_instances()+"<br>&nbsp;"
				+ mEB.getNeg_label()+ " (Negative) : " + mEB.getNeg_instances()
				 +  "</html>");
		scrollPane.setViewportView(lblNewLabel);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		try {
			
			if(e.getSource()==mBtnRocCurve){
				ROCweka lRW = new ROCweka(mResult);
				lRW.plot( mEB.getName()+" ROC ");
			}else if(e.getSource()== mBtnShowFeatures){
				if(mPreprocess==null){
					mPreprocess =  WekaUtil.getPreprocessPanel(mPath+File.separator+RESULT_ARFF, mPath);
				}
				mPreprocess.plot();
			}
		} catch (Exception e1) {
			SystemUtil.showTraingErrMsg("Some exception has occured. \n"+e1 );
			log.error("Fatal error ",e1);
		}
	}
}

