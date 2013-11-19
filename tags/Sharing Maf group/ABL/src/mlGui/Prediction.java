package mlGui;
import gui.ClassifiersWindow;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import mlGui.task.PredictionTask;

import org.apache.log4j.Logger;

import beans.PredictionDataBean;
import javax.swing.SwingConstants;

public class Prediction extends JPanel implements PropertyChangeListener {
	private static Logger log = Logger.getLogger(Prediction.class);

	private JProgressBar mProgressBar;
	private JLabel mTaskOutput;
	private JDialog dlg;
	private JFrame frame;

	private PredictionDataBean pPDB;

	/**
	 * Create the panel.
	 * @param mFilePaths 
	 * @param mLabels 
	 * @param mPath 
	 */
	
	
	public Prediction() {
		
	
		mProgressBar = new JProgressBar(0, 100);
		mProgressBar.setValue(0);
		mProgressBar.setStringPainted(true);
		mProgressBar.setIndeterminate(true);

		mTaskOutput = new JLabel("   ");
		mTaskOutput.setHorizontalAlignment(SwingConstants.CENTER);
		

		JPanel panel = new JPanel();
		panel.add(mProgressBar);
		panel.setBounds(108, 33, 158, 30);
		mTaskOutput.setBounds(12, 96, 351, 22);
		setLayout(null);
		add(panel);
		add(mTaskOutput);
	
	}

	

	public static void execute(PredictionDataBean lPDB) {
		Prediction l = new Prediction();
		PredictionTask lTLT = new PredictionTask(lPDB,l);
		l.start(lTLT);
		
	}

	private void start(PredictionTask task) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		dlg = new JDialog(getFrame(), "Wait", true);
		dlg.getContentPane().add(BorderLayout.CENTER, this);
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dlg.setSize(360, 160);
		
		dlg.setLocationRelativeTo(ClassifiersWindow.getFrame());

		// Instances of javax.swing.SwingWorker are not reusuable, so
		// we create new instances as needed.
		
		//task.setMExe(exe);
		
		task.addPropertyChangeListener(this);
		task.execute();
		dlg.setVisible(true);
	}
	
	public JFrame getFrame() {
		
		return frame==null?ClassifiersWindow.getFrame():frame;
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
