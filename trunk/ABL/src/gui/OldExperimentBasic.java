package gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;


public class OldExperimentBasic extends JPanel {
	private static Logger log = Logger.getLogger(OldExperimentBasic.class);
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	/**
	 * Create the panel.
	 */
	public OldExperimentBasic() {
		setLayout(null);
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(12, 25, 86, 15);
		add(lblName);
		
		JLabel lblDescription = new JLabel("Description:");
		lblDescription.setBounds(12, 55, 86, 15);
		add(lblDescription);
		
		textField = new JTextField();
		textField.setBounds(114, 20, 343, 20);
		add(textField);
		textField.setColumns(10);
		
		JPanel panel = new JPanel();
		panel.setBounds(12, 163, 445, 97);
		add(panel);
		panel.setBorder(new TitledBorder(null, "Training Data in Fasta format", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Positive Data:");
		lblNewLabel.setBounds(5, 20, 103, 18);
		panel.add(lblNewLabel);
		
		JLabel lblNegativeData = new JLabel("Negative Data:");
		lblNegativeData.setBounds(5, 50, 103, 18);
		panel.add(lblNegativeData);
		
		textField_1 = new JTextField();
		textField_1.setBounds(107, 20, 215, 18);
		panel.add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(334, 19, 95, 20);
		panel.add(btnBrowse);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(107, 51, 215, 18);
		panel.add(textField_2);
		
		JButton button = new JButton("Browse");
		button.setBounds(334, 50, 95, 20);
		panel.add(button);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(114, 55, 343, 97);
		add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(56, 288, 86, 25);
		btnSave.setEnabled(false);
		add(btnSave);
		
		JButton btnSelfLearn = new JButton("Self Learn");
		btnSelfLearn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				removeAll();
				updateUI(); 
//				Summary frame_s = new Summary();
				ExperimentBasic frame_s = new ExperimentBasic(); 
				//frame_s.setBounds(1, 1, 450, 320);
				add(frame_s);
				//frame_s.setVisible(true);
				updateUI(); 
				
			}
		});
		btnSelfLearn.setBounds(190, 288, 106, 25);
		add(btnSelfLearn);
		
		JButton btnCustomize = new JButton("Customize");
		btnCustomize.setBounds(326, 288, 115, 25);
		btnCustomize.setEnabled(false);
		add(btnCustomize);
	}
}

