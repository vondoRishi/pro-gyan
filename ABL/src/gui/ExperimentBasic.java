package gui;

import gui.etc.DeleteExperiment;
import gui.etc.SaveNewExperiment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import mlGui.SelfLearn;

import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;

import util.JTextFieldLimit;
import util.SystemUtil;
import beans.ExperimentDataBean;
import javax.swing.border.TitledBorder;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.ObjectProperty;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;


public class ExperimentBasic extends JPanel implements ActionListener {
	private static final String NEGATIVE = "Negative";
	private static final String POSITIVE = "Positive";
	private static final String RESET = "Reset";
	private static final String SELF_LEARN = "Self Learn";
	private static final String DELETE = "Delete";
	private static final String SAVE = "Save";
	private static Logger log = Logger.getLogger(ExperimentBasic.class);
	private BindingGroup m_bindingGroup;
	private beans.ExperimentDataBean experimentDataBean = new beans.ExperimentDataBean();
	private JTextField nameJTextField;
	private JTextArea descriptionJTextArea;
	private JTextField pos_dataJTextField;
	private JTextField neg_dataJTextField;
	private JButton mBtnBrowsePositive;
	private JButton mBtnBrowseNegative;
	private JButton mBtnSave;
	private JButton mBtnSelfLearn;
	private JButton mBtnCustomize;
	private JTextField neg_dataLabel;
	private JTextField pos_dataLabel;
	private JPanel panel;
	private JPanel panel_1;

	public ExperimentBasic(beans.ExperimentDataBean newExperimentDataBean) {
		this();
		setExperimentDataBean(newExperimentDataBean);
		if(experimentDataBean.isNew()){
			mBtnSave.setText(SAVE); 
			mBtnSelfLearn.setText(RESET);
		}else{
			mBtnSave.setText(DELETE);
			mBtnSelfLearn.setText(SELF_LEARN);
		}
	}

	public ExperimentBasic() {
		setLayout(null);
		setBounds(1, 1, 468, 348);
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setBounds(75, 20, 45, 14);
		add(nameLabel);

		nameJTextField = new JTextField();
		nameJTextField.setDocument(new JTextFieldLimit(10,true));
		nameJTextField.setBounds(138, 18, 320, 18);
		add(nameJTextField);

		JLabel descriptionLabel = new JLabel("Description:");
		descriptionLabel.setBounds(34, 126, 86, 14);
		add(descriptionLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(138, 48, 320, 170);
		add(scrollPane);

		descriptionJTextArea = new JTextArea();
		scrollPane.setViewportView(descriptionJTextArea);
		
		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, " Data ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(5, 230, 453, 67);
		add(panel);
		panel.setLayout(null);

		pos_dataLabel = new JTextField(POSITIVE);
		pos_dataLabel.setToolTipText("Enter label for positive data");
		pos_dataLabel.setDocument(new JTextFieldLimit(10,true));
		pos_dataLabel.setBounds(5, 17, 112, 18);
		panel.add(pos_dataLabel);

		pos_dataJTextField = new JTextField();
		pos_dataJTextField.setBounds(126, 17, 220, 18);
		panel.add(pos_dataJTextField);
		pos_dataJTextField.setEditable(false);

		neg_dataLabel = new JTextField(NEGATIVE);
		neg_dataLabel.setToolTipText("Enter label for negative data");
		neg_dataLabel.setDocument(new JTextFieldLimit(10,true));
		neg_dataLabel.setBounds(5, 43, 112, 18);
		panel.add(neg_dataLabel);

		neg_dataJTextField = new JTextField();
		neg_dataJTextField.setBounds(126, 43, 220, 18);
		panel.add(neg_dataJTextField);
		neg_dataJTextField.setEditable(false);
		
		mBtnBrowsePositive = new JButton("Browse");

		mBtnBrowsePositive.setBounds(358, 17, 90, 19);
		panel.add(mBtnBrowsePositive);
		
		mBtnBrowseNegative = new JButton("Browse");
		mBtnBrowseNegative.setBounds(358, 43, 90, 19);
		panel.add(mBtnBrowseNegative);
		mBtnBrowseNegative.addActionListener(this);
		mBtnBrowsePositive.addActionListener(this);
		
		panel_1 = new JPanel();
		panel_1.setBounds(124, 305, 334, 35);
		add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		
		mBtnSave = new JButton(SAVE);
		panel_1.add(mBtnSave);
		
		mBtnSelfLearn = new JButton(RESET);
		panel_1.add(mBtnSelfLearn);
		mBtnSelfLearn.addActionListener(this);
		mBtnSave.addActionListener(this);
		
		/*mBtnCustomize = new JButton("Customize");
		mBtnCustomize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		mBtnCustomize.setBounds(324, 231, 114, 24);
		mBtnCustomize.setEnabled(false);
		add(mBtnCustomize);*/

		if (experimentDataBean != null) {
			m_bindingGroup = initDataBindings();
		}
	}

	public beans.ExperimentDataBean getExperimentDataBean() {
		ExperimentDataBean l= new ExperimentDataBean();
		l.setName(nameJTextField.getText());
		l.setDescription(descriptionJTextArea.getText());
		l.setPos_data(pos_dataJTextField.getText());
		l.setNeg_data(neg_dataJTextField.getText());
		l.setPos_label(pos_dataLabel.getText());
		l.setNeg_label(neg_dataLabel.getText());
		//return experimentDataBean;
		return l;
	}

	public void setExperimentDataBean(
			beans.ExperimentDataBean newExperimentDataBean) {
		setExperimentDataBean(newExperimentDataBean, true);
	}

	public void setExperimentDataBean(
			beans.ExperimentDataBean newExperimentDataBean, boolean update) {
		experimentDataBean = newExperimentDataBean;
		if (update) {
			if (m_bindingGroup != null) {
				m_bindingGroup.unbind();
				m_bindingGroup = null;
			}
			if (experimentDataBean != null) {
				m_bindingGroup = initDataBindings();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if(e.getSource()==mBtnBrowsePositive){
				pos_dataJTextField.setText(SystemUtil.choosefile(this,"Choose a multi-fasta"));
			}else if(e.getSource()==mBtnBrowseNegative){
				neg_dataJTextField.setText(SystemUtil.choosefile(this,"Choose a multi-fasta"));
			}else if(e.getSource()==mBtnSave){
				if(mBtnSave.getText()==SAVE){
					SaveNewExperiment.execute(getExperimentDataBean());
				} else {
					DeleteExperiment.execute(getExperimentDataBean());
				}
			}else if(e.getSource()==mBtnSelfLearn){
				if(mBtnSelfLearn.getText()==RESET){
					reset();
				}else {
					SelfLearn.execute(getExperimentDataBean());
				}
			}
			
		} catch (MalformedURLException e1) {
			log.error(e1);
		}
		
	}
	
	private void reset() {
		nameJTextField.setText("");
		descriptionJTextArea.setText("");
		pos_dataJTextField.setText(""); pos_dataLabel.setText(POSITIVE);
		neg_dataJTextField.setText(""); neg_dataLabel.setText(NEGATIVE);
		
	}

	protected BindingGroup initDataBindings() {
		BeanProperty<ExperimentDataBean, String> nameProperty = BeanProperty.create("name");
		BeanProperty<JTextField, String> textProperty = BeanProperty.create("text");
		AutoBinding<ExperimentDataBean, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, experimentDataBean, nameProperty, nameJTextField, textProperty);
		autoBinding.bind();
		//
		BeanProperty<ExperimentDataBean, String> descriptionProperty = BeanProperty.create("description");
		BeanProperty<JTextArea, String> textProperty_1 = BeanProperty.create("text");
		AutoBinding<ExperimentDataBean, String, JTextArea, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, experimentDataBean, descriptionProperty, descriptionJTextArea, textProperty_1);
		autoBinding_1.bind();
		//
		BeanProperty<ExperimentDataBean, String> pos_dataProperty = BeanProperty.create("pos_data");
		BeanProperty<JTextField, String> textProperty_2 = BeanProperty.create("text");
		AutoBinding<ExperimentDataBean, String, JTextField, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ, experimentDataBean, pos_dataProperty, pos_dataJTextField, textProperty_2);
		autoBinding_2.bind();
		//
		BeanProperty<ExperimentDataBean, String> neg_dataProperty = BeanProperty.create("neg_data");
		BeanProperty<JTextField, String> textProperty_3 = BeanProperty.create("text");
		AutoBinding<ExperimentDataBean, String, JTextField, String> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ, experimentDataBean, neg_dataProperty, neg_dataJTextField, textProperty_3);
		autoBinding_3.bind();
		//
		BeanProperty<ExperimentDataBean, String> experimentDataBeanBeanProperty = BeanProperty.create("pos_label");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_1 = BeanProperty.create("text");
		AutoBinding<ExperimentDataBean, String, JTextField, String> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ, experimentDataBean, experimentDataBeanBeanProperty, pos_dataLabel, jTextFieldBeanProperty_1);
		autoBinding_4.bind();
		//
		BeanProperty<ExperimentDataBean, Boolean> experimentDataBeanBeanProperty_1 = BeanProperty.create("new");
		BeanProperty<JTextField, Boolean> jTextFieldBeanProperty = BeanProperty.create("editable");
		AutoBinding<ExperimentDataBean, Boolean, JTextField, Boolean> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ, experimentDataBean, experimentDataBeanBeanProperty_1, pos_dataLabel, jTextFieldBeanProperty);
		autoBinding_5.bind();
		//
		AutoBinding<ExperimentDataBean, Boolean, JTextField, Boolean> autoBinding_7 = Bindings.createAutoBinding(UpdateStrategy.READ, experimentDataBean, experimentDataBeanBeanProperty_1, neg_dataLabel, jTextFieldBeanProperty);
		autoBinding_7.bind();
		//
		BeanProperty<ExperimentDataBean, String> experimentDataBeanBeanProperty_2 = BeanProperty.create("neg_label");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_2 = BeanProperty.create("text");
		AutoBinding<ExperimentDataBean, String, JTextField, String> autoBinding_6 = Bindings.createAutoBinding(UpdateStrategy.READ, experimentDataBean, experimentDataBeanBeanProperty_2, neg_dataLabel, jTextFieldBeanProperty_2);
		autoBinding_6.bind();
		//
		AutoBinding<ExperimentDataBean, Boolean, JTextField, Boolean> autoBinding_8 = Bindings.createAutoBinding(UpdateStrategy.READ, experimentDataBean, experimentDataBeanBeanProperty_1, nameJTextField, jTextFieldBeanProperty);
		autoBinding_8.bind();
		//
		BeanProperty<JTextArea, Boolean> jTextAreaBeanProperty = BeanProperty.create("editable");
		AutoBinding<ExperimentDataBean, Boolean, JTextArea, Boolean> autoBinding_9 = Bindings.createAutoBinding(UpdateStrategy.READ, experimentDataBean, experimentDataBeanBeanProperty_1, descriptionJTextArea, jTextAreaBeanProperty);
		autoBinding_9.bind();
		//
		BeanProperty<JButton, Boolean> jButtonBeanProperty = BeanProperty.create("visible");
		AutoBinding<ExperimentDataBean, Boolean, JButton, Boolean> autoBinding_10 = Bindings.createAutoBinding(UpdateStrategy.READ, experimentDataBean, experimentDataBeanBeanProperty_1, mBtnBrowsePositive, jButtonBeanProperty);
		autoBinding_10.bind();
		//
		AutoBinding<ExperimentDataBean, Boolean, JButton, Boolean> autoBinding_11 = Bindings.createAutoBinding(UpdateStrategy.READ, experimentDataBean, experimentDataBeanBeanProperty_1, mBtnBrowseNegative, jButtonBeanProperty);
		autoBinding_11.bind();
		//
		BindingGroup bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding);
		bindingGroup.addBinding(autoBinding_1);
		bindingGroup.addBinding(autoBinding_2);
		bindingGroup.addBinding(autoBinding_3);
		bindingGroup.addBinding(autoBinding_4);
		bindingGroup.addBinding(autoBinding_5);
		bindingGroup.addBinding(autoBinding_7);
		bindingGroup.addBinding(autoBinding_6);
		bindingGroup.addBinding(autoBinding_8);
		bindingGroup.addBinding(autoBinding_9);
		bindingGroup.addBinding(autoBinding_10);
		bindingGroup.addBinding(autoBinding_11);
		return bindingGroup;
	}
}

