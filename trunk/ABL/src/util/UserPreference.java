package util;

import gui.MainWindow;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import static util.constants.UIlabels.*;
public class UserPreference extends JDialog implements ActionListener {

	private static final String IS_INSTALLED = "isInstalled";
	private static final String APP_HOME = "app.home";
	private static final String NUM_THREAD = "num.thread";
	private static final String SVM_PROB = "svm.prob";
	private static final String WARNING = "<html>*Warning multithreading>1 inconsistence for model generation, but good to have a quick estimate of model's learning accuracy. There after user can rebuild the model using single thread.\n<html>";
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private static Preferences mPrefs = Preferences.userNodeForPackage(UserPreference.class);;
	private JButton mBtnBrowse;
	private JButton mDone;
	private JButton mExit;
	private JButton mReset;
	private JFileChooser fc = new JFileChooser();
	private JSpinner mNumThread;
	private JCheckBox mSvmProb;
	private JCheckBox mLaunchProgyan;
	private static UserPreference sUpr ;//= new UserPreference();
	private JLabel lblNewLabel;
	private JLabel lblthePreferencesCan;

	public static UserPreference getUpr() {
		if(sUpr == null){
			sUpr = new UserPreference();
		}
		return sUpr;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UserPreference dialog = new UserPreference();
			dialog.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clear() throws BackingStoreException {
		mPrefs.clear();
		
	}

	/**
	 * Create the dialog.
	 */
	private UserPreference() {
		mPrefs = Preferences.userNodeForPackage(this.getClass());

		setUndecorated(true);
		setSize(451, 343);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblLocationToStore = new JLabel("Workspace");
		lblLocationToStore.setBounds(24, 59, 389, 15);
		contentPanel.add(lblLocationToStore);
		
		textField = new JTextField(getHome());
		textField.setEditable(false);
		textField.setBounds(24, 77, 325, 19);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		mBtnBrowse = new JButton("Browse");
		mBtnBrowse.setBounds(351, 74, 87, 25);
		mBtnBrowse.addActionListener(this);
		contentPanel.add(mBtnBrowse);
		
		JLabel lblNumberOfThreads = new JLabel("Number of Threads");
		lblNumberOfThreads.setBounds(24, 117, 150, 15);
		contentPanel.add(lblNumberOfThreads);
		
		mNumThread = new JSpinner();
		mNumThread.setModel(new SpinnerNumberModel(getNumberOfThreads(), 1, Runtime.getRuntime().availableProcessors(), 1));
		mNumThread.setBounds(192, 115, 37, 20);
		contentPanel.add(mNumThread);
		
		mSvmProb = new JCheckBox("Build probabilistic model");
		mSvmProb.setBounds(24, 205, 264, 25);
		mSvmProb.setSelected(getSvmProb());
		contentPanel.add(mSvmProb);
		
		JLabel lblWarning = new JLabel(WARNING);
		lblWarning.setForeground(Color.RED);
		lblWarning.setFont(new Font("Dialog", Font.ITALIC, 11));
		lblWarning.setBounds(24, 137, 414, 60);
		contentPanel.add(lblWarning);
		
		mLaunchProgyan = new JCheckBox("Launch "+APPLICATION_NAME);
		
		mLaunchProgyan.setBounds(24, 234, 169, 23);
		mLaunchProgyan.setVisible(false);
		if(!isInstalled()){
			mLaunchProgyan.setVisible(true);
			contentPanel.add(mLaunchProgyan);
		}
		contentPanel.add(mLaunchProgyan);
		lblNewLabel = new JLabel(APPLICATION_NAME+" preference ");
		lblNewLabel.setBounds(12, 12, 162, 15);
		contentPanel.add(lblNewLabel);
		
		lblthePreferencesCan = new JLabel("<html>The preferences can be always reset from <i>\"About Me\"</i></html>");
		lblthePreferencesCan.setBounds(24, 265, 402, 31);
		contentPanel.add(lblthePreferencesCan);
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				mDone = new JButton("Done");
				mDone.setActionCommand("OK");
				mDone.addActionListener(this);
				buttonPane.add(mDone);
				getRootPane().setDefaultButton(mDone);
			}
			
			if(!isInstalled()){
				mExit = new JButton("Exit");
				mExit.addActionListener(this);
				buttonPane.add(mExit);
			}
			
			{
				mReset = new JButton("Reset");
				mReset.addActionListener(this);
				mReset.setActionCommand("Cancel");
				buttonPane.add(mReset);
			}
		}
		
	}

	public boolean isInstalled() {
		return mPrefs.getBoolean(IS_INSTALLED, false);
	}

	public static boolean getSvmProb() {
		return mPrefs.getBoolean(SVM_PROB, true);
	}

	static Number getNumberOfThreads() {
		return mPrefs.getInt(NUM_THREAD, 1);
	}

	static String getHome() {
		return mPrefs.get(APP_HOME, System.getProperty("user.home")+File.separator+APPLICATION_NAME);
	}

	@Override
	public void actionPerformed(ActionEvent evnt) {
		if(evnt.getSource()==mBtnBrowse){
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showOpenDialog(this);
			 
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                textField.setText(fc.getSelectedFile().getAbsolutePath());
            }
		}else if(evnt.getSource() == mExit){
			System.exit(ABORT);
		}else if(evnt.getSource()== mReset){
			textField.setText(getHome());
			mNumThread.getModel().setValue(getNumberOfThreads());
			mSvmProb.setSelected(getSvmProb());
		}else{
			savePreference();
			setVisible(false);
			if(mLaunchProgyan.isSelected()){
				MainWindow.main(null);
				mLaunchProgyan.setSelected(false);
				mLaunchProgyan.setVisible(false);
				mExit.setVisible(false);
			}else if(mLaunchProgyan.isVisible()){
				System.exit(0);
			}
			
		}
		
	}

	private void savePreference() {
		mPrefs.put(APP_HOME, textField.getText());
		mPrefs.putInt(NUM_THREAD, (Integer) mNumThread.getModel().getValue());
		mPrefs.putBoolean(SVM_PROB, mSvmProb.isSelected());
		mPrefs.putBoolean(IS_INSTALLED, true);
		try {
			mPrefs.flush();
		} catch (BackingStoreException e) {
			SystemUtil.showErrMsg(null, ""+e);
		}
		
	}
}
