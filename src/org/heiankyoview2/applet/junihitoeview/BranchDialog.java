package org.heiankyoview2.applet.junihitoeview;

import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.TreeTable;



public class BranchDialog extends JDialog {

	JList list = new JList();
	JScrollPane scroll = new JScrollPane(list);
	DefaultCanvas canvas = null;
	Vector nodelist = null;
	TreeTable tg;
	
	/**
	 * @throws java.awt.HeadlessException
	 */
	public BranchDialog(Tree tree, Branch branch) throws HeadlessException {
		super();
		setTitle("List of selected drugs");

		tg = tree.table;
		setBounds(770,300,150,300);
		list.addListSelectionListener(new ListSelectionListener(){
		
			
		public void valueChanged(ListSelectionEvent e) {
		}
		
		});

		nodelist = new Vector();	
		getLeafNodeList(tree, branch, nodelist);
		add(scroll);
		setVisible(true);
	}

	public void setListData(){
		String[] string = new String[nodelist.size()];
		for(int i = 0; i < string.length; i++) {
			Node node = (Node)nodelist.elementAt(i);
			string[i] = tg.getNodeAttributeName(node, 1);
			if(string[i].endsWith(" ") == true) {
				string[i] = string[i].substring(0, string[i].length() - 1);
			}
		}
		list.setListData(string);
	}
		
	public void setCanvas(DefaultCanvas canvas){
		this.canvas = canvas;
	}
		

	/**
	 * 当該Branchの下にある葉ノード（＝化合物ノード）をリストに追加する
	 */
	void getLeafNodeList(Tree tree, Branch branch, Vector nodelist) {

		/*
		 * ノードの追加
		 */
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() == null) nodelist.add(node);
		}
	
		/*
		 * 子グループの追跡
		 */
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() != null) 
				getLeafNodeList(tree, node.getChildBranch(), nodelist);
		}
	}
	
}


