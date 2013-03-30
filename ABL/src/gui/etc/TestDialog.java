package gui.etc;

import gui.TrainingDataWindow;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import beans.ExperimentDataBean;

import util.SystemUtil;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class TestDialog extends JDialog implements ActionListener {
	private static Logger log = Logger.getLogger(TestDialog.class);
	private final JPanel contentPanel = new JPanel();
	private JTextField mPos_text;
	private JTextField mNeg_text;
	private JButton mOkButton;
	private JButton mCancelButton;
	private JButton mPos_Browse;
	private JButton mNeg_Browse;
	private boolean isTest = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			TestDialog dialog = new TestDialog(null,null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 * @param jFrame 
	 * @param pEDB 
	 */
	public TestDialog(JFrame jFrame, ExperimentDataBean pEDB) {
		super(jFrame,"",true);
		setTitle("Test the model");
		setBounds(100, 100, 437, 228);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblPositive = new JLabel(pEDB.getPos_label()+" :");
		lblPositive.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPositive.setBounds(16, 64, 86, 14);
		contentPanel.add(lblPositive);
		
		mPos_text = new JTextField();
		mPos_text.setEditable(false);
		mPos_text.setBounds(118, 62, 197, 18);
		contentPanel.add(mPos_text);
		
		mPos_Browse = new JButton("Browse");
		mPos_Browse.setBounds(331, 62, 86, 19);
		mPos_Browse.addActionListener(this);
		contentPanel.add(mPos_Browse);
		
		JLabel lblNegative = new JLabel(pEDB.getNeg_label()+" :");
		lblNegative.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNegative.setBounds(16, 108, 86, 14);
		contentPanel.add(lblNegative);
		
		mNeg_text = new JTextField();
		mNeg_text.setEditable(false);
		mNeg_text.setBounds(118, 106, 197, 18);
		contentPanel.add(mNeg_text);
		
		mNeg_Browse = new JButton("Browse");
		mNeg_Browse.setBounds(331, 106, 86, 19);
		mNeg_Browse.addActionListener(this);
		contentPanel.add(mNeg_Browse);
		
		JLabel lblNewLabel = new JLabel("Enter "+pEDB.getPos_label()+" or "+pEDB.getNeg_label()+" or both in fasta format");
		lblNewLabel.setBounds(39, 12, 357, 14);
		contentPanel.add(lblNewLabel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				mOkButton = new JButton("Test");
				mOkButton.addActionListener(this);
				mOkButton.setActionCommand("OK");
				buttonPane.add(mOkButton);
				getRootPane().setDefaultButton(mOkButton);
			}
			{
				mCancelButton = new JButton("Cancel");
				mCancelButton.setActionCommand("Cancel");
				mCancelButton.addActionListener(this);
				buttonPane.add(mCancelButton);
			}
		}
		
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		setLocationRelativeTo(TrainingDataWindow.getFrame());
		setVisible(true);
	}

	public String getmPos_text() {
		return mPos_text.getText();
	}

	public String getmNeg_text() {
		return mNeg_text.getText();
	}

	public boolean isTest() {
		return isTest;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==mOkButton){
			isTest =true;
			this.setVisible(false);
		}else if(e.getSource()==mPos_Browse){
			mPos_text.setText(SystemUtil.choosefile(this,"Choose a multi-fasta"));
		}else if(e.getSource()==mNeg_Browse){
			mNeg_text.setText(SystemUtil.choosefile(this,"Choose a multi-fasta"));
		}else if(e.getSource()==mCancelButton){
			this.setVisible(false);
			this.dispose();
		}
		
	}
}

