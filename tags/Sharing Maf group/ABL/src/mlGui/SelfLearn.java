package mlGui;

import gui.TrainingDataWindow;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import mlGui.task.SelfLearnTask;

import org.apache.log4j.Logger;

import beans.ExperimentDataBean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;


public class SelfLearn extends JPanel implements ActionListener,
		PropertyChangeListener {
	
	private static Logger log = Logger.getLogger(SelfLearn.class);
	private static XStream xstream = new XStream(new StaxDriver());
	private static JFrame frame = TrainingDataWindow.getFrame();
	private JDialog dlg;
	private SelfLearnTask task;
	private ExperimentDataBean mExperimentDataBean;
	private JProgressBar mProgressBar;
	private JLabel mTaskOutput;

	public SelfLearn(ExperimentDataBean pExperimentDataBean) {
		setLayout(null);
		mExperimentDataBean = pExperimentDataBean;
		task = new SelfLearnTask(this,pExperimentDataBean);
		mProgressBar = new JProgressBar(0, 100);
		mProgressBar.setValue(0);
		mProgressBar.setStringPainted(true);
		mProgressBar.setIndeterminate(true);

		mTaskOutput = new JLabel("    ");
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
		
		panel.setBounds(49, 12, 148, 25);
		mTaskOutput.setBounds(49, 45, 148, 21);
		add(panel);
		add(mTaskOutput);
	}
	

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		/*String xml = FileUtil.getFileAsString("/home/rishi.das/ABL_wrkspc/test"+File.separator+"exp.xml");
		ExperimentDataBean lEB = (ExperimentDataBean) xstream.fromXML(xml);*/
		if(args.length==0){
			System.err.println("ERROR");
			System.exit(ERROR);
		}
		System.out.println(args[0]);
		ExperimentDataBean lEB = new ExperimentDataBean();
		lEB.setName(args[0]);
		
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel mTreePanel = new JPanel();
		mTreePanel.setBounds(6, 12, 105, 350);
		frame.getContentPane().add(mTreePanel);
		mTreePanel.setLayout(null);
		
		frame.setVisible(true);
		
		execute(lEB);
	}

	public static void execute(ExperimentDataBean pEB) {
		SelfLearn lSL = new SelfLearn(pEB);
		lSL.start();
	}

	private void start() {
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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setMessage(String string) {
		mTaskOutput.setText(string);
		
	}

	public void setDlgVisible(boolean b) {
		dlg.setVisible(b);
	}
}

