package org.heiankyoview2.core.tree;

import java.util.*;
import java.awt.Color;
import org.heiankyoview2.core.table.NodeTablePointer;

/**
 * 階層型データの1階層を構成するNodeのBranchを定義する
 * 
 * @author itot
 */
public class Branch {

	int id;
	String name;
	int level;
	Color color;

	// このBranch に属する Node 
	Vector nodeList;
	Node parentNode;
	
	// この Branch が属する Tree
	Tree tree;


	/**
	 * Constructor
	 * @param id
	 * @param numNodes
	 * @param tree
	 */
	public Branch(int id, int numNodes, Tree tree) {
		nodeList = new Vector();
		setId(id);
		setNumNode(numNodes);
		level = 0;
		this.tree = tree;
	}

	/**
	 * Constructor
	 * @param id
	 * @param tree
	 */
	public Branch(int id, Tree tree) {
		this(id, 0, tree);
	}

	
	/**
	 * ノードの数を設定する
	 * @param ノードの数
	 * 
	 */
	public void setNumNode(int numnode) {
		if (nodeList.size() != 0) {
			System.err.println("Error:nodeList already exists");
		} else {
			for (int i = 1; i <= numnode; i++) {
				Node node = new Node(i, this);
				nodeList.add(node);
			}
		}
	}

	/**
	 * ノードの数を返す
	 * @return ノードの数
	 */
	public int getNumNode() {
		return nodeList.size();
	}

	/**
	 * ノードのリストを返す
	 * @return ノードのリスト
	 */
	public Vector getNodeList() {
		return nodeList;
	}

	/**
	 * 特定番号のノードを返す
	 */
	public Node getNodeAt(int index) {
		if (index > 0 && index <= nodeList.size()) {
			return (Node) nodeList.elementAt(index - 1);
		} else {
			System.out.println("Error: bad index(branch-node) [" + index + "].");
			return null;
		}
	}


	/**
	 * 1個のノードを追加生成し、そのノードを返す
	 * @return 新しいノード
	 */
	public Node getOneNewNode() {
		Node node = new Node((nodeList.size() + 1), this);
		nodeList.add(node);
		return node;
	}

	/** 
	 * ノードを1個追加する
	 * @param 追加するノード
	 */
	public void addOneNode(Node node) {
		nodeList.add(node);
	}

	/**
	 * 1個のノードを削除する
	 * @param 削除するノード
	 */
	public void deleteOneNode(Node node) {

		if (nodeList.size() == 0) {
			System.err.println("Error:nodeList does not exist");
		} else {
			nodeList.remove(node);
		}
	}


	/**
	 * グループの番号を設定する
	 * @param グループの番号
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * グループの番号を返す
	 * @return グループの番号
	 */
	public int getId() {
		return id;
	}

	/**
	 * グループの深さを設定する
	 * @param グループの深さ
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * グループの深さを返す
	 * @return グループの深さ
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * グループの名前を設定する
	 * @param グループの名前
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * グループの名前を返す
	 * @return グループの名前
	 */
	public String getName() {
		return name;
	}

	/**
	 * グループの親ノードを設定する
	 * @param グループの親ノード
	 */
	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	/**
	 * グループの親ノードを返す
	 * @return グループの親ノード
	 */
	public Node getParentNode() {
		return parentNode;
	}

	
	/**
	 * 親ノードのサイズを算出する
	 * templateFlag == true であればテンプレートグラフ
	 */
	public void calcParentNodeSize(boolean templateFlag) {

		double gMinp[] = { 1.0e+20, 1.0e+20, 1.0e+20 };
		double gMaxp[] = { -1.0e+20, -1.0e+20, -1.0e+20 };
		double gCenter[] = new double[3];
		double gSize[] = new double[3];

		Node parentNode = this.getParentNode();
		if (parentNode == null)
			return;

		//
		// for each node of the branch
		//
		for (int i = 1; i <= this.getNodeList().size(); i++) {
			Node node = this.getNodeAt(i);
			double tmp;

			/*
			double x = node.getNX();
			double y = node.getNY();
			double z = node.getNZ();
			double width  = node.getNwidth();
			double height = node.getNheight();
			double depth  = node.getNdepth();
			*/

			double x = node.getX();
			double y = node.getY();
			double z = node.getZ();
			double width = node.getWidth();
			double height = node.getHeight();
			double depth = node.getDepth();

			tmp = x + width;
			if (gMaxp[0] < tmp)
				gMaxp[0] = tmp;
			tmp = x - width;
			if (gMinp[0] > tmp)
				gMinp[0] = tmp;

			tmp = y + height;
			if (gMaxp[1] < tmp)
				gMaxp[1] = tmp;
			tmp = y - height;
			if (gMinp[1] > tmp)
				gMinp[1] = tmp;

			tmp = z + depth;
			if (gMaxp[2] < tmp)
				gMaxp[2] = tmp;
			tmp = z - depth;
			if (gMinp[2] > tmp)
				gMinp[2] = tmp;
		}

		//
		// Calculate the center and size of the branch
		//
		if (this.getNodeList().size() > 0) {
			gCenter[0] = (gMinp[0] + gMaxp[0]) * 0.5;
			gCenter[1] = (gMinp[1] + gMaxp[1]) * 0.5;
			gCenter[2] = (gMinp[2] + gMaxp[2]) * 0.5;
			gSize[0] = (gMaxp[0] - gMinp[0]) * 0.5 + 1.0;
			gSize[1] = (gMaxp[1] - gMinp[1]) * 0.5 + 1.0;
			gSize[2] = (gMaxp[2] - gMinp[2]) * 0.5 + 1.0;
		} else {
			gCenter[0] = gCenter[1] = gCenter[2] = 0.0;
			gSize[0] = gSize[1] = gSize[2] = 1.0;
		}

		//
		// Set the size of the branch to the parent node
		//
		parentNode.setSize(gSize[0], gSize[1], gSize[2]);
		parentNode.setCoordinate(gCenter[0], gCenter[1], gCenter[2]);
		parentNode.setNsize(gSize[0], gSize[1], gSize[2]);

		if (templateFlag == false)
			parentNode.setNCoordinate(gCenter[0], gCenter[1], gCenter[2]);

	}


	public void exchangeParentNodeOrder(Node node) {
		nodeList.removeElement((Object) node);
		for (int i = 0; i < nodeList.size(); i++) {
			Node n = (Node) nodeList.elementAt(i);
			n.setId(i + 1);
		}
		node.setId(nodeList.size());
		nodeList.addElement(node);
	}
	
	/**
	 * 配置が終わった、というフラグをリセットする
	 */
	public void resetPlaceFlag() {
		for(int i = 0; i < nodeList.size(); i++) {
			Node node = (Node)nodeList.elementAt(i);
			node.setPlaced(false);
			// temporary
			node.setId(i + 1);
		}
	}

}
