package gui;

import gui.etc.CsvTable;
import gui.etc.TestDialog;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import mlGui.TestLearn;
import mlGui.wekaCustom.ROCweka;

import org.apache.log4j.Logger;

import beans.ExperimentDataBean;

import util.BioUtil;
import util.SystemUtil;
import util.mlUtil.WekaUtil;
import weka.classifiers.Evaluation;
import weka.core.Utils;
import java.awt.FlowLayout;


public class TestSummary extends JPanel implements ActionListener{
	private static Logger log = Logger.getLogger(TestSummary.class);
	private JPanel contentPane;
	private Evaluation mResult;
	private JButton mBtnRocCurve;
	private JButton mBtnPrediction;
	private JButton mBtnTest;
	private CsvTable mCsvTable;
	/**
	 * Path in the directory
	 */
	private String mPath;
	private ExperimentDataBean mEDB;

	/*public JPanel getContentPane() {
		return contentPane;
	}*/

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
	public TestSummary(String pPath) throws Exception {
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mPath = pPath;
		mResult = WekaUtil.getTestEvaluation(pPath);
		BioUtil.consoleSummary(mResult);
		setBounds(1, 1, 468, 348);
		contentPane = this;
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//setContentPane(contentPane);
		contentPane.setLayout(null);
		setLayout(null);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(12, 305, 444, 33);
		add(panel_2);
		panel_2.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 4));
		
		mBtnRocCurve = new JButton("ROC");
		panel_2.add(mBtnRocCurve);
		
		mBtnPrediction = new JButton("Predictions");
		panel_2.add(mBtnPrediction);
		mBtnPrediction.setEnabled(true);
		mBtnPrediction.addActionListener(this);
		mBtnRocCurve.addActionListener(this);
		
		/*mBtnTest = new JButton("Test");
		mBtnTest.setBounds(22, 293, 114, 24);
		mBtnTest.addActionListener(this);
		contentPane.add(mBtnTest);
		
		mBtnExportKnowledge = new JButton("Export Knowledge");
		mBtnExportKnowledge.setEnabled(false);
		mBtnExportKnowledge.setBounds(166, 293, 205, 24);
		contentPane.add(mBtnExportKnowledge);*/
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Results", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(51, 51, 51)));
		panel.setBounds(287, 30, 169, 225);
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
		labelTpRslt.setBounds(101, 61, 56, 14);
		panel.add(labelTpRslt);

//		specificity (SPC) or True Negative Rate
		JLabel lblSpcRate = new JLabel("Specificity:");
		lblSpcRate.setBounds(12, 94, 82, 14);
		panel.add(lblSpcRate);
		lblSpcRate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSpcRate.setToolTipText("= TN / N = TN / (FP + TN)");
		
		JLabel labelSpcRslt = new JLabel(Utils.doubleToString(mResult.trueNegativeRate(0),7, 2));
		labelSpcRslt.setHorizontalAlignment(SwingConstants.LEFT);
		labelSpcRslt.setBounds(101, 94, 56, 14);
		panel.add(labelSpcRslt);
		
		JLabel lblPrecision = new JLabel("Precision:");
		lblPrecision.setBounds(12, 124, 82, 14);
		panel.add(lblPrecision);
		lblPrecision.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPrecision.setToolTipText("= TP / (TP + FP)");
		
		JLabel labelPrecsnResult = new JLabel(Utils.doubleToString(mResult.precision(0),7, 2));
		labelPrecsnResult.setHorizontalAlignment(SwingConstants.LEFT);
		labelPrecsnResult.setBounds(101, 124, 56, 14);
		panel.add(labelPrecsnResult);
		
		JLabel lblFmeasure = new JLabel("F-measure:");
		lblFmeasure.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFmeasure.setBounds(12, 150, 82, 14);
		panel.add(lblFmeasure);
		lblFmeasure.setToolTipText(" = 2 * Sensitivity * Precision / ( Sensitivity + Precision )");
		
		JLabel labelFmsrResult = new JLabel(Utils.doubleToString(mResult.fMeasure(0),7, 2));
		labelFmsrResult.setHorizontalAlignment(SwingConstants.LEFT);
		labelFmsrResult.setBounds(101, 150, 56, 14);
		panel.add(labelFmsrResult);
		
		JLabel lblRocArea = new JLabel("ROC Area:");
		lblRocArea.setBounds(12, 199, 82, 14);
		panel.add(lblRocArea);
		lblRocArea.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel labelRocResult = new JLabel(Utils.doubleToString(mResult.weightedAreaUnderROC(),7, 2));
		labelRocResult.setHorizontalAlignment(SwingConstants.LEFT);
		labelRocResult.setBounds(101, 199, 56, 14);
		panel.add(labelRocResult);
		
		JLabel lblAccuracy = new JLabel("Accuracy:");
		lblAccuracy.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAccuracy.setBounds(12, 31, 82, 14);
		panel.add(lblAccuracy);
		lblAccuracy.setToolTipText("= (TP + TN) / (P + N)");
		
		JLabel lblNumFtrRslt = new JLabel(Utils.doubleToString(mResult.pctCorrect(),7, 2));
		lblNumFtrRslt.setHorizontalAlignment(SwingConstants.LEFT);
		lblNumFtrRslt.setBounds(101, 31, 56, 14);
		panel.add(lblNumFtrRslt);
		
		JLabel lblMcc = new JLabel("MCC:");
		lblMcc.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMcc.setToolTipText("(TP * TN - FP * FN)/sqrt((TP+FP)* (TP + TN) *(FP + FN) *(TN + FN))");
		lblMcc.setBounds(12, 173, 82, 14);
		panel.add(lblMcc);
		
		JLabel lbl_MCCResult = new JLabel(Utils.doubleToString(WekaUtil.getMCC(mResult),7, 2));
		lbl_MCCResult.setHorizontalAlignment(SwingConstants.LEFT);
		lbl_MCCResult.setBounds(101, 173, 56, 14);
		panel.add(lbl_MCCResult);
		
		String pathToModel = mPath.substring(0,mPath.lastIndexOf(File.separator));
		mEDB = SystemUtil.getExperimentDataBean(pathToModel.substring(0,pathToModel.lastIndexOf(File.separator)));
		double[][] confusionMatrix = mResult.confusionMatrix();
		int borderWidth=1;
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Confusion Matrix", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		panel_1.setBounds(12, 30, 250, 225);
		add(panel_1);
		panel_1.setLayout(new GridLayout(3, 3, 0, 0));
		
		JLabel lblBlankLabel = new JLabel("");
		lblBlankLabel.setFont(new Font("Dialog", Font.BOLD, 11));
		lblBlankLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblBlankLabel);
		
		JLabel lblTru_PredLabel = new JLabel("<html><center>"+mEDB.getPos_label()+" <br>(Predicted)</center></html>");
		lblTru_PredLabel.setFont(new Font("Dialog", Font.BOLD, 11));
		lblTru_PredLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblTru_PredLabel.setBorder(BorderFactory.createMatteBorder(borderWidth,
				borderWidth, 0, borderWidth, Color.BLACK));
		panel_1.add(lblTru_PredLabel);
		
		JLabel lblFal_PredLabel = new JLabel("<html><center>"+mEDB.getNeg_label()+" <br> (Predicted)</center></html>");
		lblFal_PredLabel.setFont(new Font("Dialog", Font.BOLD, 11));
		lblFal_PredLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblFal_PredLabel.setBorder(BorderFactory.createMatteBorder(borderWidth,
				0, 0, borderWidth, Color.BLACK));
		panel_1.add(lblFal_PredLabel);
		
		JLabel lblTru_ActLabel = new JLabel("<html><center>"+mEDB.getPos_label()+" <br> (Actual)</center></html>");
		lblTru_ActLabel.setFont(new Font("Dialog", Font.BOLD, 11));
		lblTru_ActLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblTru_ActLabel.setBorder(BorderFactory.createMatteBorder(borderWidth,
				borderWidth, borderWidth, 0, Color.BLACK));
		panel_1.add(lblTru_ActLabel);
		
		JLabel lblTru_Pos = new JLabel(confusionMatrix[0][0]+"");
		lblTru_Pos.setFont(new Font("Dialog", Font.BOLD, 11));
		lblTru_Pos.setHorizontalAlignment(SwingConstants.CENTER);
		lblTru_Pos.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel_1.add(lblTru_Pos);
		
		JLabel lblFal_Neg = new JLabel(confusionMatrix[0][1]+"");
		lblFal_Neg.setFont(new Font("Dialog", Font.BOLD, 11));
		lblFal_Neg.setHorizontalAlignment(SwingConstants.CENTER);
		lblFal_Neg.setBorder(BorderFactory.createMatteBorder(borderWidth,
				0, borderWidth, borderWidth, Color.BLACK));
		panel_1.add(lblFal_Neg);
		
		JLabel lblFal_ActLabel = new JLabel("<html><center>"+mEDB.getNeg_label()+" <br>(Actual)</center></html>");
		lblFal_ActLabel.setFont(new Font("Dialog", Font.BOLD, 11));
		lblFal_ActLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		lblFal_ActLabel.setBorder(BorderFactory.createMatteBorder(0,
				borderWidth, borderWidth, 0, Color.BLACK));
		panel_1.add(lblFal_ActLabel);
		
		JLabel lblFal_Pos = new JLabel(confusionMatrix[1][0]+"");
		lblFal_Pos.setFont(new Font("Dialog", Font.BOLD, 11));
		lblFal_Pos.setHorizontalAlignment(SwingConstants.CENTER);
		lblFal_Pos.setBorder(BorderFactory.createMatteBorder(0,
				borderWidth, borderWidth, borderWidth, Color.BLACK));
		panel_1.add(lblFal_Pos);
		
		JLabel lblTru_Neg = new JLabel(confusionMatrix[1][1]+"");
		lblTru_Neg.setFont(new Font("Dialog", Font.BOLD, 11));
		lblTru_Neg.setHorizontalAlignment(SwingConstants.CENTER);
		lblTru_Neg.setBorder(BorderFactory.createMatteBorder(0,
				0, borderWidth, borderWidth, Color.BLACK));
		panel_1.add(lblTru_Neg);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if(e.getSource()==mBtnRocCurve){
				
				String wrkSpace = SystemUtil.getWorkSpace();
				
				ROCweka lRW = new ROCweka(mResult);
				lRW.plot( mPath.replaceAll(Pattern.quote(wrkSpace+File.separator), "").replaceAll(Pattern.quote(File.separator), " > ")+" ROC ");
			}else if(e.getSource()== mBtnPrediction){
				if(mCsvTable==null){
					mCsvTable =  CsvTable.get(mPath+File.separator,true);
					mCsvTable.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
					
				}
				mCsvTable.setVisible(true);
				
			}else if(e.getSource()==mBtnTest){
				TestDialog dialog = new TestDialog(TrainingDataWindow.getFrame(),null);
				if(dialog.isTest()){
					String[] mLabels = {"positive","negative"};
					String [] mFilePaths = {dialog.getmPos_text(),dialog.getmNeg_text()};
					TestLearn.execute(mPath,mLabels,mFilePaths);
				}else{
					System.out.println("close");
				}
			}
		} catch (Exception e1) {
			SystemUtil.showTraingErrMsg("Some exception has occured. \n"+e1 );
			log.error("Fatal error ",e1);
		}
	}
}

