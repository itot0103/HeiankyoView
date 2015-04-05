/**
 * 
 */
package org.leafdetector.core.tree;

import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.heiankyoview2.core.tree.Branch;

/**
 * @author fyamashi
 *
 */
public class MyMutableTreeNode extends DefaultMutableTreeNode {

	Branch branch;
	List<String> listTerminalData=new Vector<String>();
	
	public MyMutableTreeNode() {
		super();
		// TODO 
	}

	public MyMutableTreeNode(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
		// TODO 
	}

	public MyMutableTreeNode(Object userObject) {
		super(userObject);
		// TODO 
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public void addTerminalData(String data){
		listTerminalData.add(data);
	}
	
	public String getChildNode(int i){
		return listTerminalData.get(i);
	}
	
	public int getChildNumber(){
		return listTerminalData.size();
	}
	
	public Vector<String> getChild(){
		return (Vector<String>)listTerminalData;
	}
	
}
