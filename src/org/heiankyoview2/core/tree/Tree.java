
package org.heiankyoview2.core.tree;

import java.util.*;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.frame.TreeFrame;

/**
 * 階層型データのデータ構造全体を表すクラス 
 */
public class Tree {

	/**
	 * ノードを一列に格納したもの
	 */
	Vector nodeList; 

	/**
	 * リンクを一列に格納したもの
	 */
	Vector linkList; // an array of all links
	
	/**
	 * グループを一列に格納したもの
	 */
	Vector branchList = null; // an array of all branches
	
	/**
	 * 最上位階層となるグループおよびノード
	 */
	Branch rootBranch; //a root branch  
	Node rootNode;

	/**
	 * Treeに付随する表データと時間データ
	 */
	public TreeTable table; 
	public TreeFrame frame;
	String frameFilename = "";
	
	/**
	 * 画面配置に参照するテンプレートTree
	 */
	Tree templateTree = null;

	/**
	 * Constructor
	 * @param numBranches
	 */
	public Tree(int numBranches) {
		branchList = new Vector();
		nodeList = new Vector();
		linkList = new Vector();
		rootNode = new Node(0, null);
		table = new TreeTable(this);
		setNumBranch(numBranches);
	}

	/**
	 * Constructor
	 */
	public Tree() {
		this(0);
	}


	/**
	 * 表データの実体を返す
	 */
	public TreeTable getTreeTable() {
		return table;
	}

	
	/**
	 * 時間データの実体を設定する
	 */
	public void setTreeFrame(TreeFrame tf) {
		frame = tf;
	}
	
	
	/**
	 * 時間データの実体を返す
	 */
	public TreeFrame getTreeFrame() {
		return frame;
	}
	
	
	/**
	 * 時間データのファイル名を設定する
	 */
	public void setFrameFilename(String filename) {
		frameFilename = filename;
	}
	
	
	/**
	 * 時間データのファイル名を返す
	 */
	public String getFrameFilename() {
		return frameFilename;
	}
	
	/**
	 * グループ数をセットする
	 * @param グループ数
	 */
	public void setNumBranch(int numBranches) {
		branchList.clear();
		for(int i = 0; i < numBranches; i++) {
			Branch branch = new Branch((i + 1), this);
			branchList.add((Object)branch);
		}
	}
	
	/**
	 * グループ数を返す
	 * @return グループ数
	 */
	public int getNumBranch() {
		return branchList.size();
	}

	/**
	 * グループのリストを返す
	 * @return グループのリスト
	 */
	public Vector getBranchList() {
		return branchList;
	}

	/**
	 * ある特定番号のグループを返す
	 * @param グループの番号 (1からnumBranchまで)
	 * @return グループ
	 */
	public Branch getBranchAt(int id) {
		if (id < 1 || id > branchList.size()) {
			return null;
		} else
			return (Branch) branchList.elementAt(id - 1);

	}

	/**
	 * ノード数を返す
	 * @return ノード数
	 */
	public int getNumNode() {
		return nodeList.size();
	}

	/**
	 * 1個のノードを追加する
	 * @param ノード
	 */
	public void addNode(Node node) {
		nodeList.add(node);
	}

	/**
	 * ある特定番号のノードを返す
	 * @param ノードの番号 (1からnumNodeまで)
	 * @return ノード
	 */
	public Node getNodeAt(int index) {
		if (index > 0 && index <= nodeList.size()) {
			return (Node) nodeList.elementAt(index - 1);
		} else {
			System.out.println("Error: bad index(tree-node) [" + index + "].");
			return null;
		}
	}

	
	/**
	 * リンク数を返す
	 * @return リンク数
	 */
	public int getNumLink() {
		return linkList.size();
	}

	/**
	 * リンク数をセットする
	 * @param リンク数
	 */
	public void setNumLink(int numLinks) {
		linkList.clear();
		for(int i = 0; i < numLinks; i++) {
			Link link = new Link(i + 1);
			linkList.add((Object)link);
		}
	}

	/**
	 * 1個のリンクを追加する
	 * @param リンク
	 */
	public void addLink(Link link) {
		linkList.add(link);
	}

	/**
	 * リンクのリストを返す
	 * @return リンクのリスト
	 */
	public Vector getLinkList() {
		return linkList;
	}

	/**
	 * ある特定番号のリンクを返す
	 * @param リンクの番号 (1からnumLinkまで)
	 * @return リンク
	 */
	public Link getLinkAt(int index) {
		if (index > 0 && index <= linkList.size()) {
			return (Link) linkList.elementAt(index - 1);
		} else {
			System.out.println("Error: bad index(tree-link) [" + index + "].");
			return null;
		}
	}

	/**
	 * 新しいリンクを1個生成し、それを返す
	 * @return 新しいリンク
	 */
	public Link getOneNewLink() {

		Link link = null;

		if (linkList.size() == 0) {
			System.err.println("Error:linkList does not exists");
		} else {
			link = new Link((linkList.size() + 1));
			linkList.add(link);
		}
		return link;
	}

	
	/**
	 * 新しいグループを1個生成し、それを返す
	 * @return 新しいグループ
	 */
	public Branch getOneNewBranch() {
		Branch branch = new Branch((branchList.size() + 1), this);
		branchList.add(branch);
		
		return branch;
	}

	/**
	 * 1個のグループを削除する
	 * @param 削除するグループ
	 */
	public void deleteOneBranch(Branch branch) {

		if (branchList.size() == 0) {
			System.err.println("Error:branchList does not exists");
		} else {
			branchList.remove(branch);
		}
	}


	/**
	 * 最上位階層のグループを指定する
	 * @param 最上位階層のグループ
	 */
	public void setRootBranch(Branch rootBranch) {

		this.rootBranch = rootBranch;
		rootBranch.setParentNode(rootNode);
		rootNode.setChildBranch(rootBranch);

		rootBranch.calcParentNodeSize(false);
	}

	/**
	 * 最上位階層のグループを返す
	 * @return 最上位階層のグループ
	 */
	public Branch getRootBranch() {
		return rootBranch;
	}

	/**
	 * 最上位階層のノードを返す
	 * @return 最上位階層のノード
	 */
	public Node getRootNode() {
		return rootNode;
	}


	/**
	 * テンプレートグラフを設定する
	 * @param テンプレートグラフ
	 */
	public void setTemplateTree(Tree ttree) {
		templateTree = ttree;
	}

	/**
	 * テンプレートグラフを返す
	 * @return テンプレートグラフ
	 */
	public Tree getTemplateTree() {
		return templateTree;
	}
	
	
	/**
	 * 配置が終わった、というフラグをリセットする
	 */
	public void resetPlaceFlag() {
		for(int i = 0; i < branchList.size(); i++) {
			Branch branch = (Branch)branchList.elementAt(i);
			branch.resetPlaceFlag();
		}
	}
	
	
}