package org.heiankyoview2.core.tree;

import java.util.*;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;

public class Template {

	String name;
	int id;
	int num;
	Node templateNode[];

	/* constructor */
	public Template() {
	}
	public Template(int num) {
		this.templateNode = new Node[num];
	}

	public Template(int id, int num, String name) {
		this.id = id;
		this.num = num;
		this.name = name;
		this.templateNode = new Node[num];
	}

	/* method */

	/* ID */
	public void setID(int i) {
		this.id = i;
	}
	public int getID() {
		return id;
	}
	/* num */
	public void setNum(int i) {
		this.num = i;
		this.templateNode = new Node[num];
	}
	public int getNum() {
		return num;
	}

	/* name */
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	/*templateNode*/
	public void setTemplateNode(Node tmpNode, int i) {
		templateNode[i] = tmpNode;
	}

	public Node getTemplateNode(int i) {
		return templateNode[i];
	}

	/**
	 * makeTemplateTree
	 *@param Tree tree
	 */
	public void makeTemplateTree(Tree tree) {

		Tree normalizeTree;
		Branch branch;
		Node tmpNode, nodeList[], newNodeList[];
		int numbranch, numnode;

		Vector branchList = new Vector();
		Branch rootBranch = tree.getRootBranch();

		branchList.add((Object) rootBranch);

		//
		// Traverse the hierarchy of branches
		//     starting from the root branch
		//
		int i = 0;
		while (i < branchList.size()) {
			branch = (Branch) branchList.elementAt(i++);

			//
			// for each node of the branch:
			//     Search for child branches
			//
			for (int j = 1; j <= branch.getNodeList().size(); j++) {
				Node node = branch.getNodeAt(j);
				Branch childBranch = node.getChildBranch();
				if (childBranch != null) {
					branchList.add((Object) childBranch);
				}
			}
		}

		//
		// for each branch (in the inverse order of traverse)
		//     Place nodes in each branch
		//

		for (i = branchList.size() - 1; i >= 0; i--) {
			branch = (Branch) branchList.elementAt(i);

			setNormalizePositionInBranch(branch);

		}
	}

	/** 
	    * normalizePositionInBranch 
	    * @param nodeList 
	    * @param nodeNum in the Branch
	    * @param parentNode of the Branch
	    */
	void setNormalizePositionInBranch(Branch branch) {

		Node newNodeList[], tmpNode;
		double tmpx,
			tmpy,
			tmpz,
			rangex,
			rangey,
			rangez,
			minx = 0,
			miny = 0,
			minz = 0;

		//final double templateMin = -1.0;
		//	final double templateSize = 2.0;
		final double templateCenter = 0.0;

		if (branch.getNodeList().size() == 1) {
			branch.getNodeAt(1).setNCoordinate(
				templateCenter,
				templateCenter,
				templateCenter);
			branch.getNodeAt(1).setNsize(0.6, 0.6, 0.3);
			return;
		}

		/* decide range, minPosition of Box */
		Node parentNode = branch.getParentNode();
		rangex = parentNode.getWidth();
		rangey = parentNode.getHeight();
		rangez = parentNode.getDepth();


		/* normalize */
		for (int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = branch.getNodeAt(i + 1);

			node.setNCoordinate(
				(node.getX() - parentNode.getX()) / rangex,
				(node.getY() - parentNode.getY()) / rangey,
				(node.getZ() - parentNode.getZ()) / rangez);

			node.setNsize(
				node.getWidth() / rangex,
				node.getHeight() / rangey,
				node.getDepth() / rangez);

			/*	    node.setNCoordinate
			( (node.getX() - parentNode.getX()) *
			  templateSize / rangex,
			  (node.getY() - parentNode.getY()) *
			  templateSize / rangey,
			  (node.getZ() - parentNode.getZ() ) *
			  templateSize / rangez);
			   
			
			
			node.setNsize
			( node.getWidth() * templateSize / rangex, 
			  node.getHeight() * templateSize / rangey, 
			  node.getDepth() * templateSize / rangez );*/

		}

	}
	//------------------------------------------------------------
	/** 
	 * normalizePositionInBranch
	 * @param nodeList 
	 * @param nodeNum in the Branch
	 * @param parentNode of the Branch
	 */
	Node[] normalizePositionInBranch(
		Node[] nodeList,
		int iNodeNum,
		Node parentNode) {

		Node newNodeList[], tmpNode;
		double tmpx,
			tmpy,
			tmpz,
			rangex,
			rangey,
			rangez,
			minx = 0,
			miny = 0,
			minz = 0;

		//final double templateMin = -1.0;
		final double templateSize = 2.0;
		final double templateCenter = 0.0;

		newNodeList = new Node[iNodeNum];

		for (int j = 1; j <= iNodeNum; j++) {
			newNodeList[j - 1] = new Node();
			newNodeList[j
				- 1].setCoordinate(
					nodeList[j - 1].getX(),
					nodeList[j - 1].getY(),
					nodeList[j - 1].getZ());
			newNodeList[j
				- 1].setSize(
					nodeList[j - 1].getWidth(),
					nodeList[j - 1].getHeight(),
					nodeList[j - 1].getDepth());
		}

		/*
		if (iNodeNum == 1) {
			nodeList[0].setCoordinate(
				templateCenter,
				templateCenter,
				templateCenter);
			nodeList[0].setSize(0.6, 0.6, 0.3);
			return nodeList;
		}
		*/

		/* decide range, minPosition of Box */
		rangex = parentNode.getWidth() * 2;
		rangey = parentNode.getHeight() * 2;
		rangez = parentNode.getDepth() * 2;

		minx = parentNode.getX() - parentNode.getWidth();
		miny = parentNode.getY() - parentNode.getHeight();
		minz = parentNode.getZ() - parentNode.getDepth();

		/* normalize */
		for (int i = 0; i < iNodeNum; i++) {
			/*newNodeList[i].setCoordinate
			( (nodeList[i].getX() - minx + templateMin ) 
			  * templateSize / rangex ,
			  (nodeList[i].getY() - miny + templateMin ) 
			  * templateSize / rangey ,
			  (nodeList[i].getZ() - minz + templateMin ) 
			  * templateSize / rangez );*/
			newNodeList[i].setNCoordinate(
				(nodeList[i].getX() - parentNode.getX())
					* templateSize
					/ rangex,
				(nodeList[i].getY() - parentNode.getY())
					* templateSize
					/ rangey,
				(nodeList[i].getZ() - parentNode.getZ())
					* templateSize
					/ rangez);

			newNodeList[i].setNsize(
				nodeList[i].getWidth() * templateSize / rangex,
				nodeList[i].getHeight() * templateSize / rangey,
				nodeList[i].getDepth() * templateSize / rangez);

		}

		return newNodeList;
	}
}
