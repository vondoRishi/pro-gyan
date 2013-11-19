package gui.etc;

import gui.TrainingDataWindow;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import beans.ExperimentDataBean;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class ModelDescription extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTextArea textArea;
	private JButton okButton;
	
	public ModelDescription(JFrame pFrame, ExperimentDataBean lEDB) {
		super(pFrame,"",true);

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		{
			JLabel lblName = new JLabel("Name");
			contentPanel.add(lblName, "2, 2");
		}
		{
			textField = new JTextField();
			contentPanel.add(textField, "4, 2, fill, default");
			textField.setColumns(10);
			textField.setText(lEDB.getName());
		}
		{
			JLabel lblDescription = new JLabel("Description");
			contentPanel.add(lblDescription, "2, 4");
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, "4, 4, fill, fill");
			{
				textArea = new JTextArea();
				textArea.setText(lEDB.getDescription());
				scrollPane.setViewportView(textArea);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(this);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		setLocationRelativeTo(TrainingDataWindow.getFrame());
		setVisible(true);
	
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ModelDescription dialog = new ModelDescription(null,null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void setExpName(String name) {
		textField.setText(name);		
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource()==okButton){
			setVisible(false);
		}
		
	}

	public String getExpName() {
		return textField.getText();
	}

	public String getDescription() {
		return textArea.getText();
	}

}
