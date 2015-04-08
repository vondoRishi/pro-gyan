package gui;

import static util.constants.UIlabels.APPLICATION_NAME;

import gui.etc.AboutMe;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import util.SystemUtil;
import util.UserPreference;

public class MainWindow implements ActionListener {
	
	private static Logger log = Logger.getLogger(MainWindow.class);
	private JFrame frame;
	private static MainWindow window;
	public static JFrame getFrame() {
		return (window==null)?(null):window.frame;
	}

	private JButton mBtnPredict;
	private JButton mBtnTrainMe;
	private JButton mKnowledgeXchng;

	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
/*		try {
			try {

				for (LookAndFeelInfo info : UIManager
						.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());
						break;
					}
				}
			} catch (Exception e) {
				MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
				//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				UIManager.setLookAndFeel(new MetalLookAndFeel());
			}
		} catch (UnsupportedLookAndFeelException e1) {
			SystemUtil.showErrMsg(null, ""+e1);
		}*/
		
		validateEnvironment();
		if (!UserPreference.getUpr().isInstalled()) {
			UserPreference.getUpr().setLocationRelativeTo(null);
			UserPreference.getUpr().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			UserPreference.getUpr().setVisible(true);
		} else {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {

						SystemUtil.intialize();
						window = new MainWindow();
						window.frame.setLocationByPlatform(true);
						window.frame.setVisible(true);

					} catch (Exception e) {
						log.error(""+e);
					}
				}
			});
		}
	}

	/**
	 * Create the application.
	 * @wbp.parser.entryPoint
	 */
	public MainWindow() {
		initialize();
	}
	
	private static void validateEnvironment() {
		String version = System.getProperty("java.version");
		char minor = version.charAt(2);
		if (minor < '6') {
			JOptionPane
					.showMessageDialog(null,
							"You need Java 6 to run this application \n You are presently using "
									+ version, "Update Java",
							JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 467, 245);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle(APPLICATION_NAME);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(246, 12, 201, 190);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		mBtnTrainMe = new JButton("Create Classifier");
		mBtnTrainMe.addActionListener(this);
		mBtnTrainMe.setToolTipText("Enter the lab.");
		mBtnTrainMe.setBounds(12, 22, 179, 44);
		panel.add(mBtnTrainMe);
		
		mBtnPredict = new JButton("Classify Proteins");
		mBtnPredict.setToolTipText("Protein classification from fasta sequences.");
		mBtnPredict.setBounds(12, 78, 179, 44);
		mBtnPredict.addActionListener(this);
		panel.add(mBtnPredict);
		
		mKnowledgeXchng = new JButton("About Me");
		mKnowledgeXchng.setToolTipText("");
		mKnowledgeXchng.setBounds(12, 134, 179, 44);
		mKnowledgeXchng.addActionListener(this);
		panel.add(mKnowledgeXchng);
		
		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon(MainWindow.class.getResource("/data/ritwick_3.jpg")));
		label.setBounds(12, 12, 233, 190);
		frame.getContentPane().add(label);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == mBtnTrainMe){
			TrainingDataWindow.main(null);
		}else if(e.getSource() == mBtnPredict){
			ClassifiersWindow.main(null);
		}else if(e.getSource() == mKnowledgeXchng){
			AboutMe.main(null);
		}
		
	}
}

