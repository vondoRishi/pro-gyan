/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package gui.etc;

import static util.constants.FileName.EXP_XML;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
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

import util.BioUtil;
import util.SystemUtil;
import beans.ExperimentDataBean;
import feature.util.FileUtil;
import gui.ExperimentBasic;
import gui.TrainingDataWindow;

public class SaveNewExperiment extends JPanel implements ActionListener,
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
				String not_given = checkMissingInputs();
				
				if(not_given.length()>0)
					SystemUtil.showTraingErrMsg("Please provide "+not_given+" of the Experiment");
				String lMsg;
				if((lMsg=BioUtil.isValidFastaByThread(mExperimentDataBean.getPos_data())) !=null){
					SystemUtil.showTraingErrMsg(mExperimentDataBean.getPos_label()+" has following errors\n\n"+lMsg);
					return null;
				}
					
				if((lMsg=BioUtil.isValidFastaByThread(mExperimentDataBean.getNeg_data())) !=null){
					SystemUtil.showTraingErrMsg(mExperimentDataBean.getNeg_label()+" has following errors\n\n"+lMsg);
					return null;
				}
				
				saveToDisk();
				
				// Check no of proteins.
			} catch (Exception e) {
				taskOutput.append("Error Occured" + e + "\nCheck Log File");
				SystemUtil.showTraingErrMsg("Some exception has occured. \n"+e );
				log.error("" + e);
			}
			return null;
		}

		private void saveToDisk() throws IOException {
			String workingDir = SystemUtil.getWorkDir(mExperimentDataBean);
			FileUtil.createDir(workingDir);
			
			FileUtil.copy(mExperimentDataBean.getPos_data(), workingDir+File.separator+mExperimentDataBean.getPos_label()+"_positive.fasta");
//			TODO 
//			mExperimentDataBean.setPos_data("No of Proteins?? VIEW");
			FileUtil.copy(mExperimentDataBean.getNeg_data(), workingDir+File.separator+mExperimentDataBean.getNeg_label()+"_negative.fasta");
//			mExperimentDataBean.setNeg_data(workingDir+File.separator+"negative.fasta");
			mExperimentDataBean.setNew(false);
			
			FileUtil.writeToXML(workingDir+File.separator+EXP_XML, mExperimentDataBean);
		}

		

		private String checkMissingInputs() {
			StringBuilder not_given = new StringBuilder();
			if(mExperimentDataBean.getName()==null || mExperimentDataBean.getName().length()==0){
				not_given.append("Name,");
			}
			if(mExperimentDataBean.getDescription()==null || mExperimentDataBean.getDescription().length()==0){
				not_given.append("Description,");
			}
			
			if(mExperimentDataBean.getPos_label()==null || mExperimentDataBean.getPos_label().length()==0){
				not_given.append("Positive label,");
			}
			
			if(mExperimentDataBean.getPos_data()==null || mExperimentDataBean.getPos_data().length()==0){
				not_given.append("Positive data,");
			}
			
			if(mExperimentDataBean.getNeg_label()==null || mExperimentDataBean.getNeg_label().length()==0){
				not_given.append("Negative label,");
			}
			if(mExperimentDataBean.getNeg_data()==null || mExperimentDataBean.getNeg_data().length()==0){
				not_given.append("Negative data");
			}
			
			return not_given.toString();
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
			TrainingDataWindow.getWindow().showPanel(new ExperimentBasic( mExperimentDataBean)," > save "+mExperimentDataBean.getName());
		}

		/*public void setMExe(Executer exe) {
			mExe = exe;
		}*/
	}

	public SaveNewExperiment(ExperimentDataBean pExperimentDataBean) throws MalformedURLException {
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

		/*java.net.URL imgURL = getClass().getResource(
				"/Application/barcircle.gif");

		Icon icon = new ImageIcon(imgURL);
		iconlabel = new JLabel(icon);*/

		JPanel panel = new JPanel();
		// panel.add(startButton);
		panel.add(progressBar);
//		panel.add(iconlabel);

		add(panel, BorderLayout.PAGE_START);
//		add(new JScrollPane(taskOutput), BorderLayout.CENTER);
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
//		dlg.setSize(300, 275);
		dlg.setSize(300, 120);
		dlg.setLocationRelativeTo(TrainingDataWindow.getFrame());

		// Instances of javax.swing.SwingWorker are not reusuable, so
		// we create new instances as needed.
		task = new Task();
		//task.setMExe(exe);
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

	public static void execute(ExperimentDataBean pExperimentDataBean) throws MalformedURLException  {
		SaveNewExperiment lProgressBar = new SaveNewExperiment(pExperimentDataBean);
		lProgressBar.start();
	}

}
