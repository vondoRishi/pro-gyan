package gui;
import static util.constants.FileName.EXP_XML;
import static util.constants.UIlabels.ARE_YOU_SURE_TO_DELETE;
import static util.constants.UIlabels.MULTIPLE_FASTA;
import static util.constants.UIlabels.PLEASE_CONFIRM;
import feature.util.FileUtil;
import gui.BrowseExperiment.NodeInfo;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import mlGui.Prediction;

import org.apache.log4j.Logger;

import util.FastaFocusListener;
import util.SystemUtil;
import util.constants.FileName;
import util.constants.UIlabels;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.JTextPane;

import beans.ExperimentDataBean;
import beans.PredictionDataBean;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import java.awt.FlowLayout;

public class ClassifiersWindow extends JFrame implements ActionListener, FocusListener {
	private static final String REMOVE = "Remove";
	private static final String MODEL_DESCRIPTION = "Model description";
	private static final String NEW_MODEL = "New";
	private static final String IMPORT_MODELS_OR_SELECT_MODELS_AND_CLASSIFY_PROTEINS = "Import or select models and classify proteins.";
	private static final String RESET = "Reset";
	private static final String FASTA_FILE = "Fasta File";
	private static final String CLASSIFY = "Classify";
	private static final String IMPORT = "Import";
	private static final String IMPORT_A_NEW_MODEL = "Import a new model";
	private static Logger log = Logger.getLogger(ClassifiersWindow.class);
	private static JFrame sFrame;
	private JPanel contentPane;
	private JTextField mInputFasta;
	private JButton mBtnAction;
	private JComboBox mModel;
	private JLabel mModelDetailsHere;
	private JButton mBtnFastaFile;
	private JButton mBtnPredict;
	private JButton mBtnReset;
	private JTextArea mFastaSeq;
	private boolean isUpdating;
	private JButton mBtnDetails;
	private JPanel mBtnPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClassifiersWindow frame = new ClassifiersWindow();
					frame.setVisible(true);
					sFrame = frame; 
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	/**
	 * Create the frame.
	 */
	public ClassifiersWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(MainWindow.getFrame());
		setSize( 493, 415);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel(IMPORT_MODELS_OR_SELECT_MODELS_AND_CLASSIFY_PROTEINS);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(29, 12, 438, 15);
		contentPane.add(lblNewLabel);
		
		/*JLabel lblModel = new JLabel("Models :");
		lblModel.setBounds(29, 44, 70, 19);
		contentPane.add(lblModel);*/
		
		mModel = new JComboBox();
		mModel.setModel(new DefaultComboBoxModel(new String[] {NEW_MODEL}));
		mModel.setBounds(27, 39, 145, 24);
		
		contentPane.add(mModel);
		mModel.addActionListener(this);
		
		mBtnAction = new JButton(IMPORT);
		mBtnAction.addActionListener(this);
		mBtnAction.setBounds(345, 39, 119, 25);
		contentPane.add(mBtnAction);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(29, 92, 438, 203);
		contentPane.add(scrollPane);
		
		mFastaSeq = new JTextArea();
		mFastaSeq.setText(MULTIPLE_FASTA);
		mFastaSeq.addFocusListener(this);
		scrollPane.setViewportView(mFastaSeq);
		
		mBtnPanel = new JPanel();
		mBtnPanel.setBounds(28, 342, 439, 35);
		contentPane.add(mBtnPanel);
		mBtnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		mInputFasta = new JTextField();
		mInputFasta.setEditable(false);
		mInputFasta.setBounds(28, 305, 439, 35);
		contentPane.add(mInputFasta);
//		mBtnPanel.add(mInputFasta);
		/*mInputFasta.setEditable(false);
		mInputFasta.setColumns(13);*/
		
		mBtnFastaFile = new JButton(FASTA_FILE);
		mBtnPanel.add(mBtnFastaFile);
		
		mBtnPredict = new JButton(CLASSIFY);
		mBtnPanel.add(mBtnPredict);
		mBtnPredict.setEnabled(false);
		
		mBtnReset = new JButton(RESET);
		mBtnPanel.add(mBtnReset);
		mBtnReset.addActionListener(this);
		mBtnPredict.addActionListener(this);
		mBtnFastaFile.addActionListener(this);
		
		mBtnDetails = new JButton("Details");
		mBtnDetails.addActionListener(this);
		mBtnDetails.setBounds(199, 39, 119, 25);
		contentPane.add(mBtnDetails);
		
		/*JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, MODEL_DESCRIPTION, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(22, 75, 450, 138);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(15, 22, 424, 101);
		panel.add(scrollPane_1);
		
		mModelDetailsHere = DefaultComponentFactory.getInstance().createLabel(IMPORT_A_NEW_MODEL);
		mModelDetailsHere.setHorizontalAlignment(SwingConstants.CENTER);
		scrollPane_1.setViewportView(mModelDetailsHere);
		mModelDetailsHere.setVerticalAlignment(SwingConstants.TOP);*/
		
		setTitle(UIlabels.CLASSIFIER_TITLE);
		setResizable(false);
		updateModelList();
	}

	private void updateModelList() {
		isUpdating = true;
		Object new1 = mModel.getItemAt(0);
		mModel.removeAllItems();
		mModel.addItem(new1);
		BrowseExperiment l = new BrowseExperiment(SystemUtil.getModelSpace(),null);
		for (NodeInfo lNode : l.printDescendants()) {
			mModel.addItem(lNode);
		}
		mBtnAction.setText(IMPORT);
		isUpdating = false;
	}
	
	public static JFrame getFrame() {
		return sFrame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if(e.getSource()== mBtnAction ){
				if(mModel.getSelectedIndex()==0){
					String lModelPath =  SystemUtil.savefile(this, IMPORT, "");
					if(lModelPath!=null){
						SystemUtil.importModel(lModelPath);
						updateModelList();
					}
				}else{
					if(SystemUtil.getPredictUserConfirmation(ARE_YOU_SURE_TO_DELETE)){
						NodeInfo modelNode = (NodeInfo)mModel.getSelectedItem();
						FileUtil.deleteDir(new File(SystemUtil.getModelSpace()+File.separator+modelNode.toString()));
						updateModelList();
					}
				}
				
			}else if(e.getSource()== mModel && !isUpdating){
				if( mModel.getSelectedIndex()== 0){
					mBtnAction.setText(IMPORT);
				} else {
					mBtnAction.setText(REMOVE);
					NodeInfo modelNode = (NodeInfo)mModel.getSelectedItem();
					updateDescription(modelNode);
				}
			}else if(e.getSource()== mBtnPredict){
				predict();
			}else if(e.getSource()== mBtnFastaFile){
				mInputFasta.setText(SystemUtil.choosefile(this,"Choose a multi-fasta"));
				if(mInputFasta.getText()!=null &&  !mInputFasta.getText().equals("")){
					mFastaSeq.setText(MULTIPLE_FASTA);
					mBtnPredict.setEnabled(true);
				}
				
			}else if(e.getSource()== mBtnReset){
				mFastaSeq.setText(MULTIPLE_FASTA);
				mInputFasta.setText("");
				mBtnPredict.setEnabled(false);
			}else if(mBtnDetails==e.getSource()){
				if(mModel.getSelectedIndex()== 0){
					JOptionPane.showMessageDialog(getFrame(), "Import a model to classify proteins");
				}else {
					NodeInfo modelNode = (NodeInfo)mModel.getSelectedItem();
					ModelSummary lEB =  new ModelSummary(modelNode.getPath());
					JFrame frame = new JFrame("");
			        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			        frame.setContentPane(lEB);
			        frame.setSize(480, 350);
			        frame.setResizable(false);
//			        frame.setBounds(1, 1, 468, 320);
			        //Display the window.
			       // frame.pack();
			        frame.setLocationRelativeTo(this);
			        frame.setTitle("Summary : "+modelNode.toString());
			        frame.setVisible(true);
			 
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			log.error("Fatal error ",e1);
			SystemUtil.showPredictErrMsg(" 209 "+e1.getMessage());
			
		}
		
	}

	private void predict() throws Exception {
		String inputPath = null, label = null ;
		if(mModel.getSelectedIndex()==0){
			SystemUtil.showPredictErrMsg("Select a model");
		}else if((mInputFasta.getText()!=null) && (!mInputFasta.getText().equals(""))){
			inputPath = mInputFasta.getText();
			File n = new File(inputPath);
			label = n.getName();
		}else if(!mFastaSeq.getText().equals(MULTIPLE_FASTA)){
			inputPath = SystemUtil.saveTextFasta(mFastaSeq);
			label = " User input";
		}else{
			SystemUtil.showPredictErrMsg("Enter input");
		}
		if(inputPath!=null){
			NodeInfo modelNode = (NodeInfo)mModel.getSelectedItem();
			PredictionDataBean lPDB = new PredictionDataBean(modelNode.toString(),inputPath);
			lPDB.setInfo(label);
			Prediction.execute(lPDB);
		}
		
		
	}

	private void updateDescription(NodeInfo modelNode) throws Exception {
//		ExperimentDataBean lEB = (ExperimentDataBean) FileUtil.getObjectFromXml(modelNode.getPath()+File.separator+EXP_XML);
		//mModelDetailsHere.setText(lEB.getDescription());
	}

	public void focusGained(FocusEvent pFE) {
		JTextArea lTA = (JTextArea) pFE.getSource();
		if (lTA.getText().equals(MULTIPLE_FASTA)){
			lTA.setText("");
			mBtnPredict.setEnabled(true);
			mInputFasta.setText("");
		}
	}

	public void focusLost(FocusEvent pFE) {
		JTextArea lTA = (JTextArea) pFE.getSource();
		if (lTA.getText().length() == 0){
			lTA.setText(MULTIPLE_FASTA);
			mBtnPredict.setEnabled(false);
		}
	}
}
