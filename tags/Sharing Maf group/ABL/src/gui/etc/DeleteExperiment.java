package gui.etc;

import static util.constants.UIlabels.ARE_YOU_SURE_TO_DELETE;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import util.SystemUtil;
import beans.ExperimentDataBean;
import feature.util.FileUtil;
import gui.TrainingDataWindow;

public class DeleteExperiment extends JPanel implements ActionListener,
		PropertyChangeListener {
	private static Logger log = Logger.getLogger(SaveNewExperiment.class);

	private JProgressBar progressBar;

	private JButton startButton;

	private JTextArea taskOutput;

	private Task task;

	private JDialog dlg;

	private JLabel iconlabel;

	private ExperimentDataBean mExperimentDataBean;

	public class Task extends SwingWorker<Void, Void> {

				/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {

			try {
				if(SystemUtil.getUserConfirmation(ARE_YOU_SURE_TO_DELETE)){
					String workingDir = SystemUtil.getWorkDir(mExperimentDataBean);
					
					FileUtil.deleteDir(new File(workingDir));
				}

				// Check no of proteins.
			} catch (Exception e) {
				taskOutput.append("Error Occured" + e + "\nCheck Log File");
				SystemUtil.showTraingErrMsg("Some exception has occured. \n"
						+ e);
				log.error("" + e);
			}
			return null;
		}

		public void setCustomProgress(int pr) {
			setProgress(pr);
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			startButton.setEnabled(true);
			setCursor(null); // turn off the wait cursor
			taskOutput.append("Done!\n");
			dlg.setVisible(false);
			TrainingDataWindow.refreshTree();
			TrainingDataWindow.showDefaultPanel();
		}

		/*
		 * public void setMExe(Executer exe) { mExe = exe; }
		 */
	}

	public DeleteExperiment(ExperimentDataBean pExperimentDataBean)
			throws MalformedURLException {
		super(new BorderLayout());
		mExperimentDataBean = pExperimentDataBean;
		startButton = new JButton("Start");
		startButton.setActionCommand("start");
		startButton.addActionListener(this);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setIndeterminate(true);

		taskOutput = new JTextArea(5, 20);
		taskOutput.setMargin(new Insets(5, 5, 5, 5));
		taskOutput.setEditable(false);

		/*
		 * java.net.URL imgURL = getClass().getResource(
		 * "/Application/barcircle.gif");
		 * 
		 * Icon icon = new ImageIcon(imgURL); iconlabel = new JLabel(icon);
		 */

		JPanel panel = new JPanel();
		// panel.add(startButton);
		panel.add(progressBar);
		// panel.add(iconlabel);

		add(panel, BorderLayout.PAGE_START);
		// add(new JScrollPane(taskOutput), BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

	}

	/**
	 * Invoked when the user presses the start button.
	 */
	public void actionPerformed(ActionEvent evt) {
		startButton.setEnabled(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	private void start() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		dlg = new JDialog(TrainingDataWindow.getFrame(), "Wait", true);
		dlg.add(BorderLayout.CENTER, this);
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		// dlg.setSize(300, 275);
		dlg.setSize(300, 120);
		dlg.setLocationRelativeTo(TrainingDataWindow.getFrame());

		// Instances of javax.swing.SwingWorker are not reusuable, so
		// we create new instances as needed.
		task = new Task();
		// task.setMExe(exe);
		task.addPropertyChangeListener(this);
		task.execute();
		dlg.setVisible(true);
	}

	/**
	 * Invoked when task's progress property changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			progressBar.setIndeterminate(false);
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
		}
	}

	public static void execute(ExperimentDataBean pExperimentDataBean)
			throws MalformedURLException {
		DeleteExperiment lProgressBar = new DeleteExperiment(
				pExperimentDataBean);
		lProgressBar.start();
	}

}
