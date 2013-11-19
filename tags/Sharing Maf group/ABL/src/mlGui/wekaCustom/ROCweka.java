package mlGui.wekaCustom;

import java.awt.BorderLayout;

import org.apache.log4j.Logger;

import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

public class ROCweka {
	private static Logger log = Logger.getLogger(ROCweka.class);
	private Evaluation mResult;

	public ROCweka(Evaluation pResult) {
		super();
		this.mResult = pResult;
	}

	public void plot(String pTitle) {
		ThresholdCurve tc = new ThresholdCurve();
		int classIndex = 0;
		Instances result = tc.getCurve(mResult.predictions(), classIndex);

		// plot curve
		ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
		vmc.setROCString("(Area under ROC = "
				+ Utils.doubleToString(tc.getROCArea(result), 4) + ")");
		vmc.setName(result.relationName());
		PlotData2D tempd = new PlotData2D(result);
		tempd.setPlotName(result.relationName());
		tempd.addInstanceNumberAttribute();
		// specify which points are connected
		boolean[] cp = new boolean[result.numInstances()];
		for (int n = 1; n < cp.length; n++)
			cp[n] = true;
		try {
			tempd.setConnectPoints(cp);
			vmc.addPlot(tempd);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			log.error(e1);
		}
		// add plot

		// display curve
		
		final javax.swing.JFrame jf = new javax.swing.JFrame(
				pTitle);
		jf.setSize(500, 400);
		jf.setLocationRelativeTo(null);
		jf.getContentPane().setLayout(new BorderLayout());
		jf.getContentPane().add(vmc, BorderLayout.CENTER);
//		jf.getContentPane().add(vmc.getPlotPanel(), BorderLayout.CENTER);
		jf.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				jf.dispose();
			}
		});
		jf.setVisible(true);
	}
}
