package mlGui;

import gui.TrainingDataWindow;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import mlGui.task.TestLearnTask;

import org.apache.log4j.Logger;


public class TestLearn extends JPanel implements PropertyChangeListener {
	private static Logger log = Logger.getLogger(TestLearn.class);
	
	private JProgressBar mProgressBar;
	private JLabel mTaskOutput;
	private JDialog dlg;
	private JFrame frame;

	/**
	 * Create the panel.
	 * @param mFilePaths 
	 * @param mLabels 
	 * @param mPath 
	 */
	public TestLearn() {

		mProgressBar = new JProgressBar(0, 100);
		mProgressBar.setValue(0);
		mProgressBar.setStringPainted(true);
		mProgressBar.setIndeterminate(true);

		mTaskOutput = new JLabel("   ");
		//mTaskOutput.setMargin(new Insets(5, 5, 5, 5));
		//mTaskOutput.setEditable(false);

		/*java.net.URL imgURL = getClass().getResource(
				"/Application/barcircle.gif");

		Icon icon = new ImageIcon(imgURL);
		iconlabel = new JLabel(icon);*/

		JPanel panel = new JPanel();
		// panel.add(startButton);
		panel.add(mProgressBar);
//		panel.add(iconlabel);

		/*add(panel, BorderLayout.NORTH);
		add(mTaskOutput, BorderLayout.SOUTH);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));*/
		
		panel.setBounds(82, 5, 158, 30);
		mTaskOutput.setBounds(92, 53, 148, 15);
		setLayout(null);
		add(panel);
		add(mTaskOutput);
	}
	
	public static void main(String[] args) {
		
	}

	public static void execute(String mPath, String[] mLabels,
			String[] mFilePaths, boolean pCalculateScore) {
		TestLearn l = new TestLearn();
		TestLearnTask lTLT = new TestLearnTask(l,mPath,mLabels,mFilePaths,pCalculateScore);
		l.start(lTLT);
		
	}

	private void start(TestLearnTask task) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		dlg = new JDialog(getFrame(), "Wait", true);
		dlg.getContentPane().add(BorderLayout.CENTER, this);
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
//		dlg.setSize(300, 275);
		dlg.setSize(300, 120);
		dlg.setLocationRelativeTo(TrainingDataWindow.getFrame());

		// Instances of javax.swing.SwingWorker are not reusuable, so
		// we create new instances as needed.
		
		//task.setMExe(exe);
		
		task.addPropertyChangeListener(this);
		task.execute();
		dlg.setVisible(true);
	}
	
	public JFrame getFrame() {
		
		return frame==null?TrainingDataWindow.getFrame():frame;
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setMessage(String string) {
		mTaskOutput.setText(string);
		
	}

	public void setDlgVisible(boolean b) {
		dlg.setVisible(b);
	}
}

