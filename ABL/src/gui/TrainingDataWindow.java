package gui;

import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import util.SystemUtil;
import javax.swing.JLabel;


public class TrainingDataWindow {
	private static final String TRAINING_WIN_TITLE = "Knowledge Builder";
	private static Logger log = Logger.getLogger(TrainingDataWindow.class);
	private static JFrame sFrame;
	private JFrame frmKnowledgeBuilder;
	private BrowseExperiment mTree;
	private JPanel mPanel_1;
	private JPanel mTreePanel;
	private static TrainingDataWindow mWindow;
	
	public static TrainingDataWindow getWindow() {
		return mWindow;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			

			public void run() {
				try {
					mWindow = new TrainingDataWindow();
					mWindow.frmKnowledgeBuilder.setVisible(true);
					mWindow.frmKnowledgeBuilder.setResizable(false);
					sFrame=mWindow.frmKnowledgeBuilder;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TrainingDataWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmKnowledgeBuilder = new JFrame();
		frmKnowledgeBuilder.setTitle(TRAINING_WIN_TITLE);
		frmKnowledgeBuilder.setLocationRelativeTo(MainWindow.getFrame());
		frmKnowledgeBuilder.setSize( 600, 400);
		frmKnowledgeBuilder.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmKnowledgeBuilder.getContentPane().setLayout(null);
		
		
		mTreePanel = new JPanel();
		mTreePanel.setBounds(6, 12, 105, 350);
		frmKnowledgeBuilder.getContentPane().add(mTreePanel);
		mTreePanel.setLayout(null);
		
		setTree();
		
		mPanel_1 = new JPanel();
		mPanel_1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		mPanel_1.setBounds(123, 12, 470, 350);
		frmKnowledgeBuilder.getContentPane().add(mPanel_1);
		mPanel_1.setLayout(null);
		
		JLabel lblWriteSomethingHere = new JLabel("Write Something here");
		lblWriteSomethingHere.setBounds(100, 85, 209, 27);
		mPanel_1.add(lblWriteSomethingHere);
		
		mTree.setSelectionRow(0);
	}

	private void setTree() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 105, 350);
		mTreePanel.add(scrollPane);
		
		mTree = new BrowseExperiment(SystemUtil.getWorkSpace(),this);
		scrollPane.setViewportView(mTree);
	}
	
	private void resetTree() {
		mTreePanel.removeAll();
		setTree();
		mTreePanel.updateUI();
	}

	public void showPanel(JPanel experimentBasic, String pSubTitle) {
		mPanel_1.removeAll();
		mPanel_1.add(experimentBasic);
		frmKnowledgeBuilder.setTitle(TRAINING_WIN_TITLE+pSubTitle);
		mPanel_1.updateUI();
	}

	public static JFrame getFrame() {
		return sFrame;
	}

	public static void refreshTree() {
		mWindow.resetTree();
	}
	
	public static void showDefaultPanel() {
		mWindow.mPanel_1.removeAll();
		
		mWindow.mPanel_1.updateUI();
	}
}