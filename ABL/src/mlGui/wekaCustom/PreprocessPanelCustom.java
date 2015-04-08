package mlGui.wekaCustom;


import gui.TrainingDataWindow;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import util.SystemUtil;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;
import weka.gui.TaskLogger;
import weka.gui.explorer.ExplorerDefaults;
import weka.gui.explorer.PreprocessPanel;


public class PreprocessPanelCustom extends PreprocessPanel {

	private String mPath;

	public PreprocessPanelCustom(String pPath) {

		mPath = pPath;
	    // Create/Configure/Connect components
	    m_FilterEditor.setClassType(weka.filters.Filter.class);
	    if (ExplorerDefaults.getFilter() != null)
	      m_FilterEditor.setValue(ExplorerDefaults.getFilter());
	    
	    /*m_FilterEditor.addPropertyChangeListener(new PropertyChangeListener() {
	      public void propertyChange(PropertyChangeEvent e) {
	        m_ApplyFilterBut.setEnabled(true);
	        Capabilities currentCapabilitiesFilter = m_FilterEditor.getCapabilitiesFilter();
	        Filter filter = (Filter) m_FilterEditor.getValue();
	        Capabilities currentFilterCapabilities = null;
	        if (filter != null && currentCapabilitiesFilter != null &&
	            (filter instanceof CapabilitiesHandler)) {
	          currentFilterCapabilities = ((CapabilitiesHandler)filter).getCapabilities();
	          
	          if (!currentFilterCapabilities.supportsMaybe(currentCapabilitiesFilter) &&
	              !currentFilterCapabilities.supports(currentCapabilitiesFilter)) {
	            m_ApplyFilterBut.setEnabled(false);
	          }
	        }
	      }
	    });
	    m_OpenFileBut.setToolTipText("Open a set of instances from a file");
	    m_OpenURLBut.setToolTipText("Open a set of instances from a URL");
	    m_OpenDBBut.setToolTipText("Open a set of instances from a database");
	    m_GenerateBut.setToolTipText("Generates artificial data");
	    m_UndoBut.setToolTipText("Undo the last change to the dataset");
	    m_EditBut.setToolTipText("Open the current dataset in a Viewer for editing");
	    m_SaveBut.setToolTipText("Save the working relation to a file");
	    m_ApplyFilterBut.setToolTipText("Apply the current filter to the data");

	    m_FileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    m_OpenURLBut.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		setInstancesFromURLQ();
	      }
	    });
	    m_OpenDBBut.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        SqlViewerDialog dialog = new SqlViewerDialog(null);
	        dialog.setVisible(true);
	        if (dialog.getReturnValue() == JOptionPane.OK_OPTION)
	          setInstancesFromDBQ(dialog.getURL(), dialog.getUser(),
	                              dialog.getPassword(), dialog.getQuery(),
	                              dialog.getGenerateSparseData());
	      }
	    });
	    m_OpenFileBut.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		setInstancesFromFileQ();
	      }
	    });
	    m_GenerateBut.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		generateInstances();
	      }
	    });
	    m_UndoBut.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		undo();
	      }
	    });
	    m_EditBut.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        edit();
	      }
	    });
	    m_SaveBut.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		saveWorkingInstancesToFileQ();
	      }
	    });
	    m_ApplyFilterBut.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		  applyFilter((Filter) m_FilterEditor.getValue());
		}
	      });*/
	    m_AttPanel.getSelectionModel()
	      .addListSelectionListener(new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent e) {
		  if (!e.getValueIsAdjusting()) {	  
		    ListSelectionModel lm = (ListSelectionModel) e.getSource();
		    for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
		      if (lm.isSelectedIndex(i)) {
			m_AttSummaryPanel.setAttribute(i);
			m_AttVisualizePanel.setAttribute(i);
			break;
		      }
		    }
		  }
		}
	    });


	    m_InstSummaryPanel.setBorder(BorderFactory
					 .createTitledBorder("Current relation"));
	    JPanel attStuffHolderPanel = new JPanel();
	    attStuffHolderPanel.setBorder(BorderFactory
					  .createTitledBorder("Attributes"));
	    attStuffHolderPanel.setLayout(new BorderLayout());
	    attStuffHolderPanel.add(m_AttPanel, BorderLayout.CENTER);
//	    m_AttPanel holds the buttons All, None,...
	    m_RemoveButton.setEnabled(false);
	    m_RemoveButton.setToolTipText("Remove selected attributes.");
	    m_RemoveButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		  try {
		    Remove r = new Remove();
		    int [] selected = m_AttPanel.getSelectedAttributes();
		    if (selected.length == 0) {
		      return;
		    }
		    if (selected.length == m_Instances.numAttributes()) {
		      // Pop up an error optionpane
		      JOptionPane.showMessageDialog(PreprocessPanelCustom.this,
						    "Can't remove all attributes from data!\n",
						    "Remove Attributes",
						    JOptionPane.ERROR_MESSAGE);
		      m_Log.logMessage("Can't remove all attributes from data!");
		      m_Log.statusMessage("Problem removing attributes");
		      return;
		    }
		    r.setAttributeIndicesArray(selected);
		    applyFilter(r);
		  } catch (Exception ex) {
		    if (m_Log instanceof TaskLogger) {
		      ((TaskLogger)m_Log).taskFinished();
		    }
		    // Pop up an error optionpane
		    JOptionPane.showMessageDialog(PreprocessPanelCustom.this,
						  "Problem filtering instances:\n"
						  + ex.getMessage(),
						  "Remove Attributes",
						  JOptionPane.ERROR_MESSAGE);
		    m_Log.logMessage("Problem removing attributes: " + ex.getMessage());
		    m_Log.statusMessage("Problem removing attributes");
		  }
		}
	      });

	    /*JPanel p1 = new JPanel();
	    p1.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
	    p1.setLayout(new BorderLayout());
	    p1.add(m_RemoveButton, BorderLayout.CENTER);
	    attStuffHolderPanel.add(p1, BorderLayout.SOUTH);*/
	    m_AttSummaryPanel.setBorder(BorderFactory
			    .createTitledBorder("Selected attribute"));
	    m_UndoBut.setEnabled(false);
	    m_EditBut.setEnabled(false);
	    m_SaveBut.setEnabled(false);
	    m_ApplyFilterBut.setEnabled(false);
	    
	    // Set up the GUI layout
	    JPanel buttons = new JPanel();
	    buttons.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
	    buttons.setLayout(new GridLayout(1, 6, 5, 5));
	    buttons.add(m_OpenFileBut);
	    buttons.add(m_OpenURLBut);
	    buttons.add(m_OpenDBBut);
	    buttons.add(m_GenerateBut);
	    buttons.add(m_UndoBut);
	    buttons.add(m_EditBut);
	    buttons.add(m_SaveBut);

	    JPanel attInfo = new JPanel();

	    attInfo.setLayout(new BorderLayout());
	    attInfo.add(attStuffHolderPanel, BorderLayout.CENTER);

	    JPanel filter = new JPanel();
	    filter.setBorder(BorderFactory
			    .createTitledBorder("Filter"));
	    filter.setLayout(new BorderLayout());
	    filter.add(m_FilterPanel, BorderLayout.CENTER);
	    filter.add(m_ApplyFilterBut, BorderLayout.EAST); 

	    JPanel attVis = new JPanel();
	    attVis.setLayout( new GridLayout(2,1) );
	    attVis.add(m_AttSummaryPanel);

	    JComboBox colorBox = m_AttVisualizePanel.getColorBox();
	    colorBox.setToolTipText("The chosen attribute will also be used as the " +
				    "class attribute when a filter is applied.");
	    colorBox.addItemListener(new ItemListener() {
	      public void itemStateChanged(ItemEvent ie) {
		if (ie.getStateChange() == ItemEvent.SELECTED) {
		  updateCapabilitiesFilter(m_FilterEditor.getCapabilitiesFilter());
		}
	      }
	    });
	    final JButton visAllBut = new JButton("Visualize All");
	    visAllBut.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		  if (m_Instances != null) {
		    try {
		      final weka.gui.beans.AttributeSummarizer as = 
			new weka.gui.beans.AttributeSummarizer();
		      as.setColoringIndex(m_AttVisualizePanel.getColoringIndex());
		      as.setInstances(m_Instances);
		      
		      final javax.swing.JFrame jf = new javax.swing.JFrame();
		      jf.getContentPane().setLayout(new java.awt.BorderLayout());
		      
		      jf.getContentPane().add(as, java.awt.BorderLayout.CENTER);
		      jf.addWindowListener(new java.awt.event.WindowAdapter() {
			  public void windowClosing(java.awt.event.WindowEvent e) {
			    visAllBut.setEnabled(true);
			    jf.dispose();
			  }
			});
		      jf.setSize(830,600);
		      jf.setVisible(true);
		    } catch (Exception ex) {
		      ex.printStackTrace();
		    }
		  }
		}
	      });
	    JPanel histoHolder = new JPanel();
	    histoHolder.setLayout(new BorderLayout());
	    histoHolder.add(m_AttVisualizePanel, BorderLayout.CENTER);
	    JPanel histoControls = new JPanel();
	    histoControls.setLayout(new BorderLayout());
	    histoControls.add(colorBox, BorderLayout.CENTER);
	    histoControls.add(visAllBut, BorderLayout.EAST);
	    histoHolder.add(histoControls, BorderLayout.NORTH);
	    attVis.add(histoHolder);

	    JPanel lhs = new JPanel();
	    lhs.setLayout(new BorderLayout());
	    lhs.add(m_InstSummaryPanel, BorderLayout.NORTH);
	    lhs.add(attInfo, BorderLayout.CENTER);

	    JPanel rhs = new JPanel();
	    rhs.setLayout(new BorderLayout());
	    rhs.add(attVis, BorderLayout.CENTER);

	    JPanel relation = new JPanel();
	    relation.setLayout(new GridLayout(1, 2));
	    relation.add(lhs);
	    relation.add(rhs);

	    JPanel middle = new JPanel();
	    middle.setLayout(new BorderLayout());
//	    middle.add(filter, BorderLayout.NORTH);
	    middle.add(relation, BorderLayout.CENTER);

	    setLayout(new BorderLayout());
//	    add(buttons, BorderLayout.NORTH);
	    add(middle, BorderLayout.CENTER);
	  }
	
	/**
	   * Tells the panel to use a new base set of instances.
	   *
	   * @param inst a set of Instances
	   */
	  public void setInstances(Instances inst) {

	    m_Instances = inst;
	    try {
	      Runnable r = new Runnable() {
		public void run() {
		  m_InstSummaryPanel.setInstances(m_Instances);
		  m_AttPanel.setInstances(m_Instances);
		  m_RemoveButton.setEnabled(true);
		  m_AttSummaryPanel.setInstances(m_Instances);
		  m_AttVisualizePanel.setInstances(m_Instances);

		  // select the first attribute in the list
		  m_AttPanel.getSelectionModel().setSelectionInterval(0, 0);
		  m_AttSummaryPanel.setAttribute(0);
		  m_AttVisualizePanel.setAttribute(0);

		  m_ApplyFilterBut.setEnabled(true);

		  m_Log.logMessage("Base relation is now "
				   + m_Instances.relationName()
				   + " (" + m_Instances.numInstances()
				   + " instances)");
		  m_SaveBut.setEnabled(true);
		  m_EditBut.setEnabled(true);
		  m_Log.statusMessage("OK");
		  // Fire a propertychange event
		  m_Support.firePropertyChange("", null, null);
		  
		  // notify GOEs about change
		  try {
		    // get rid of old filter settings
		    //customized getExplorer().notifyCapabilitiesFilterListener(null);

		    int oldIndex = m_Instances.classIndex();
		    m_Instances.setClassIndex(m_AttVisualizePanel.getColorBox().getSelectedIndex() - 1);
		    
		    // send new ones
		   /* customized 
		    * if (ExplorerDefaults.getInitGenericObjectEditorFilter())
		      getExplorer().notifyCapabilitiesFilterListener(
			  Capabilities.forInstances(m_Instances));
		    else
		      getExplorer().notifyCapabilitiesFilterListener(
			  Capabilities.forInstances(new Instances(m_Instances, 0)));*/

		    m_Instances.setClassIndex(oldIndex);
		  }
		  catch (Exception e) {
		    e.printStackTrace();
		    m_Log.logMessage(e.toString());
		  }
		}
	      };
	      if (SwingUtilities.isEventDispatchThread()) {
		r.run();
	      } else {
		SwingUtilities.invokeAndWait(r);
	      }
	    } catch (Exception ex) {
	      ex.printStackTrace();
	      JOptionPane.showMessageDialog(this,
					    "Problem setting base instances:\n"
					    + ex,
					    "Instances",
					    JOptionPane.ERROR_MESSAGE);
	    }
	  }

	public void plot() {
		String wrkSpace = SystemUtil.getWorkSpace();
		mPath = mPath.replaceAll(Pattern.quote(wrkSpace+File.separator), "").replaceAll(Pattern.quote(File.separator), " > ");
		final JFrame jf = new JFrame(mPath+" > Selected Features");
		jf.setLocationRelativeTo(TrainingDataWindow.getFrame());
        jf.setSize(700, 500);
        jf.getContentPane().setLayout( new BorderLayout() );
        jf.getContentPane().add(this, BorderLayout.CENTER );
        jf.setDefaultCloseOperation( jf.DISPOSE_ON_CLOSE );
        jf.setVisible(true);
		
	}

	
}

