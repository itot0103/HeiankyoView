/**
 * 
 */
package org.leafdetector.core.window;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import org.heiankyoview2.applet.junihitoeview.GlDefaultCanvas;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.tree.Branch;
import org.leafdetector.core.io.PredictedResultFileExporter;
import org.leafdetector.core.io.PredictionFileImporter;
import org.leafdetector.core.tree.MyMutableTreeNode;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * @author fyamashi
 *
 */
public class TreeListDialog extends JFrame {

	Container container;
	MyMutableTreeNode root;
	JScrollPane treeScrollPane;
	JScrollPane listScrollPane;
    JList list;

	GlDefaultCanvas canvas=null;
	    
	public TreeListDialog(String title) throws HeadlessException {
		super();
		container=this.getContentPane();

		setBounds(770,0,300,400);

		setVisible(true);

	}
	
	/**
	 * @param canvas2
	 */
	public void setCanvas(GlDefaultCanvas canvas) {
		// TODO 
		this.canvas=canvas;
		
	}


	public void readData(){
		PredictionFileImporter file=new PredictionFileImporter();
		file.setMutableTreeRoot(root);
		file.identifyNodeFromFile(canvas);
		file.setModal(true);

	}
	
	public void saveData(){
		PredictedResultFileExporter file=new PredictedResultFileExporter();
		file.setMutableTreeRoot(root);
		file.exportPredictedDataToFile(canvas);		
		file.setModal(true);
	}
	
	
	public void createTreeNodes(){
		
		Branch rootBranch=canvas.getTree().getBranchAt(1);
		root=new MyMutableTreeNode(getBranchName(rootBranch));
		root.setBranch(rootBranch);
		setChild(root);

		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		ImageIcon icon = new ImageIcon(getClass().getResource("img/leaf.gif"));
		renderer.setLeafIcon(icon);

		JTree tree=new JTree(root);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new SelectionListener());
		tree.setCellRenderer(renderer);
		
		treeScrollPane =new JScrollPane(tree);
		list =new JList();
		listScrollPane=new JScrollPane(list);
		JSplitPane splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,treeScrollPane,listScrollPane);
		splitPane.setDividerLocation(150);
		splitPane.setDividerSize(3);
		
		container.add(splitPane);
		
		addWindowListener(new WindowsEventHandler());
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		setVisible(true);

	}

	
	public String getBranchName(Branch branch){
		Table table=canvas.getTreeTable().getTable(1);
		return table.getString(branch.getId());
	}
	
	public void setChild(MyMutableTreeNode parent){
		int numNode=parent.getBranch().getNumNode();
		for(int i=0;i<numNode;i++){
			Branch childBranch=parent.getBranch().getNodeAt(i+1).getChildBranch();
			if(childBranch!=null){
				MyMutableTreeNode child=new MyMutableTreeNode(getBranchName(childBranch));
				child.setBranch(childBranch);
				parent.add(child);
				setChild(child);
			}
		}
	}
	
	class SelectionListener implements TreeSelectionListener{

		/* (? Javadoc)
		 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
		 */
		public void valueChanged(TreeSelectionEvent e) {
			// TODO
			JTree tree=(JTree)e.getSource();
			MyMutableTreeNode selectedNode=
				(MyMutableTreeNode)tree.getLastSelectedPathComponent();
			if(selectedNode==null){
				list.setListData(new Vector<String>());				
			}
			else if(selectedNode.getChildNumber()==0){
				list.setListData(new Vector<String>());
			}
			else{
				Vector<org.heiankyoview2.core.tree.Node> nodeList
					=selectedNode.getBranch().getNodeList();
				int targetTables[]=canvas.getTargetTables();
				double[] averages=new double[targetTables.length];
				TreeTable tt=canvas.getTreeTable();
				Table[] table=new Table[targetTables.length];
				for(int i=0;i<targetTables.length;i++){
					table[i]=new Table();
				}
				
				for(int i=0;i<averages.length;i++){
					averages[i]=0.0;
					table[i]=tt.getTable(targetTables[i] + 1);
				}
				
				for(int j=0;j<nodeList.size();j++){
					NodeTablePointer tn = (NodeTablePointer)nodeList.get(j).table;
					for(int i=0;i<averages.length;i++){	
						int id = tn.getId(targetTables[i] + 1);
						double value=0.;
						if(table[i].getType()==2){
							value=table[i].getDouble(id);
						}else if(table[i].getType()==3){
							value=table[i].getInt(id);
						}else{
						}
						averages[i]+=value/(double)nodeList.size();
					}
				}
				
				String str=new String(String.valueOf((float)averages[0]));
				for(int i=1;i<averages.length;i++){
					str+=" "+String.valueOf((float)averages[i]);
				}
				
				Vector<String> listData=(Vector<String>)selectedNode.getChild().clone();
				listData.add(0,str);
				list.setListData(listData);
			}
			canvas.highlightSpecifiedBranch(selectedNode.getBranch());
			canvas.display();
		}
		
	}
	
	class WindowsEventHandler extends WindowAdapter{
		public void windowClosing(WindowEvent e){
			System.exit(0);
		}
	}
}
