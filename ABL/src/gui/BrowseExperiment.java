package gui;

import static util.constants.FileName.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


import org.apache.log4j.Logger;

import util.SystemUtil;


public class BrowseExperiment extends JTree implements TreeSelectionListener {
	
	
	private static Logger log = Logger.getLogger(BrowseExperiment.class);
	private String mRootPath;
	private DefaultMutableTreeNode mNew = new DefaultMutableTreeNode("New");
	private TrainingDataWindow mUpdateUI;
	private DefaultMutableTreeNode mTop;

	public BrowseExperiment(String pRootPath, TrainingDataWindow pUpdateUI) {
		super();
		setBounds(0, 0, 105, 350);
		this.mRootPath = pRootPath;
		this.mUpdateUI = pUpdateUI;
		DefaultMutableTreeNode top = initializeTree();
		//mTree = new JTree(top);
		setModel(new DefaultTreeModel(top));
		addTreeSelectionListener(this);
		setRootVisible(false);
	}

	private DefaultMutableTreeNode initializeTree() {
		File l = new File(mRootPath);
		mTop =
		        new DefaultMutableTreeNode(new NodeInfo(mRootPath, l.getName()));
		
		mTop.add(mNew);
		createNodes(mTop);
		
		return mTop;
	}

	private void createNodes(DefaultMutableTreeNode top) {
		File l = new File(((NodeInfo)top.getUserObject()).getPath());
		File[] children = l.listFiles();
		
		Arrays.sort(children);
		
		for (File l1 : children) {
			if(l1.isDirectory() && !l1.isHidden()){
				NodeInfo nodeInfo = new NodeInfo(l1.getAbsolutePath(), l1.getName());
				DefaultMutableTreeNode lD = new DefaultMutableTreeNode(
						nodeInfo);
				for (String lName : l1.list()) {
					if(lName.equals(EXP_XML)){
						nodeInfo.setBasic(true);
						break;
					}else if(lName.equals(SUMMARY_XML)){
						nodeInfo.setLearn(true);
						break;
					}else if(lName.equals(TEST_RESULT_XML)){
						nodeInfo.setTest(true);
						break;
					}
				}
				createNodes(lD);
				top.add(lD);
			}
		}
		
	}
	
	public  List<NodeInfo> printDescendants() {
	    
	    Enumeration children = mTop.children();
	    List<NodeInfo> lNode = new ArrayList<NodeInfo>();
	    children.nextElement();
	    while (children.hasMoreElements()) {
	    	DefaultMutableTreeNode nextElement = (DefaultMutableTreeNode) children.nextElement();
	       	lNode.add(  (NodeInfo) nextElement.getUserObject());
		}
	    
	    return lNode;
	  }

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode treeSource =  (DefaultMutableTreeNode) getLastSelectedPathComponent();
		if(treeSource==mNew){
			mUpdateUI.showPanel(new ExperimentBasic()," > New  ");
		}else {
			NodeInfo lNI = (NodeInfo)treeSource.getUserObject();
			try {
				String path = e.getNewLeadSelectionPath().toString().replaceAll(",", " > ");
				int index = path.indexOf(">");
				mUpdateUI.showPanel(SystemUtil.getPanel(lNI)," "+path.substring(index, path.length()-1));
			} catch (IOException e1) {
				SystemUtil.showTraingErrMsg("The Experiment is corrupt");
				log.error(e1);
			} catch (Exception e1) {
				log.error("Fatal error ",e1);
			}
			
		}
		
	}

	
	
	public class NodeInfo {
		private String mPath;
		private String mName;
		private boolean isBasic=false;
		private boolean isLearn = false;
		private boolean isTest =false;
		public boolean isTest() {
			return isTest;
		}

		public boolean isBasic() {
			return isBasic;
		}

		public void setTest(boolean pTest) {
			isTest =pTest;
			
		}

		public void setLearn(boolean pLearn) {
			this.isLearn  = pLearn;
		}

		public boolean isLearn() {
			return isLearn;
		}

		public void setBasic(boolean isBasic) {
			this.isBasic = isBasic;
		}

		public NodeInfo(String pPath, String pName) {
			super();
			this.mPath = pPath;
			this.mName = pName;
		}
		
		public String getPath() {
			return mPath;
		}

		@Override
		public String toString() {
			return mName;
		}
	}
}