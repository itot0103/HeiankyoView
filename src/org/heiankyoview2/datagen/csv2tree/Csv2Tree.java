package org.heiankyoview2.datagen.csv2tree;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.NodeTablePointer;

import java.util.*;


/**
 * CSVデータを入力してTreeを生成する
 * @author itot
 */
public class Csv2Tree {
	int numLine = 0;
	Vector nodeList;

	int NUM_ATTRIBUTES = 5;
	int NUM_HIERARCHY = 3;
	int ARRAY_HIERARCHY[] = { 0, 1, 2 };


	/**
	 * Constructor
	 */
	public Csv2Tree() {
		nodeList = new Vector();
	}

	/**
	 * CSVデータ1行に対する属性数をセットする
	 * @param num 1行に対する属性数
	 */
	public void setNumAttributes(int num) {
		NUM_ATTRIBUTES = num;
	}

	/**
	 * CSVデータ1行に対する属性数を返す
	 * @return 1行に対する属性数
	 */
	public int getNumAttributes() {
		return NUM_ATTRIBUTES;
	}

	/**
	 * CSVデータから構築する階層の深さをセットする
	 * @param num 階層の深さ
	 */
	public void setNumLevel(int num) {
		NUM_HIERARCHY = num;
	}

	/**
	 * CSVデータから構築する階層の深さを返す
	 * @return 階層の深さ
	 */
	public int getNumLevel() {
		return NUM_HIERARCHY;
	}

	/**
	 * CSVデータから階層を構築する際に、各々の深さで参照する属性のIDをセットする
	 * @param array 属性のIDを格納する配列
	 */
	public void setArrayHierarchy(int array[]) {
		ARRAY_HIERARCHY = new int[array.length];
		for (int i = 0; i < array.length; i++)
			ARRAY_HIERARCHY[i] = array[i];
	}

	/**
	 * CSVデータから階層を構築する際に、各々の深さで参照する属性のIDを返す
	 * @return 属性のIDを格納する配列
	 */
	public int[] getArrayHierarchy() {
		return ARRAY_HIERARCHY;
	}

	/**
	 * 指定されたBranchの下位階層を形成する
	 * @param tree Tree
	 * @param branch Branch
	 * @param hierarchy 階層の現在の深さ
	 */
	public void formLowerHierarchy(
		Tree tree,
		Branch branch,
		int hierarchy) {

		Node node, cnode;
		Branch cbranch = null;
		Vector nodelist = new Vector();

		if (hierarchy >= NUM_HIERARCHY)
			return;

		// for each node: move to the temporary list
		while (branch.getNodeList().size() > 0) {
			node = (Node) branch.getNodeList().elementAt(0);
			nodelist.add(node);
			branch.getNodeList().remove((Object) node);
		}

		// for each node:
		for (int i = 0; i < nodelist.size(); i++) {
			node = (Node) nodelist.elementAt(i);
			int valueId = ARRAY_HIERARCHY[hierarchy] + 1;
			NodeTablePointer tn = node.table;
			int nodeVid = tn.getId(valueId);

			// for each existing branch
			for (int j = 0; j < branch.getNodeList().size(); j++) {
				cnode = (Node) branch.getNodeList().elementAt(j);
				cbranch = cnode.getChildBranch();
				if (cbranch == null)
					continue;

				NodeTablePointer ctn = cnode.table;
				int cnodeVid = ctn.getId(valueId);

				// set the node into the current branch
				if (nodeVid == cnodeVid) {
					cbranch.getNodeList().add(node);
					node.setId(cbranch.getNodeList().size());
					node.setCurrentBranch(cbranch);
					break;
				}

				cbranch = null;
			}

			// allocate one more branch and set the node there
			if (cbranch == null) {
				cnode = branch.getOneNewNode();
				NodeTablePointer ctn = cnode.table;
				ctn.setNumId(NUM_ATTRIBUTES);
				ctn.setId(valueId, nodeVid);

				cbranch = tree.getOneNewBranch();
				cnode.setChildBranch(cbranch);
				cbranch.setParentNode(cnode);
				cbranch.getNodeList().add(node);
				node.setCurrentBranch(cbranch);
				node.setId(1);
			}

		}

		// form the lower levels
		for (int j = 0; j < branch.getNodeList().size(); j++) {
			cnode = (Node) branch.getNodeList().elementAt(j);
			cbranch = cnode.getChildBranch();
			formLowerHierarchy(tree, cbranch, (hierarchy + 1));
		}

	}


	/**
	 * セットされたパラメータにしたがって階層構造を形成する
	 * @param tree Tree
	 */
	public void formHierarchy(Tree tree) {
		Branch rootbranch, branch, cbranch = null;
		Node node, cnode;

		// create the root branch
		node = tree.getRootNode();
		tree.setNumBranch(1);
		rootbranch = tree.getBranchAt(1);
		tree.setRootBranch(rootbranch);

		// for each node: form the first level
		for (int i = 1; i <= tree.getNumNode(); i++) {
			node = tree.getNodeAt(i);
			int valueId = ARRAY_HIERARCHY[0] + 1;
			NodeTablePointer tn = node.table;
			int nodeVid = tn.getId(valueId);

			// for each existing branch
			for (int j = 0; j < rootbranch.getNodeList().size(); j++) {
				cnode = (Node) rootbranch.getNodeList().elementAt(j);
				cbranch = cnode.getChildBranch();
				if (cbranch == null)
					continue;
				NodeTablePointer ctn = cnode.table;
				int cnodeVid = ctn.getId(valueId);

				// set the node into the current branch
				if (nodeVid == cnodeVid) {
					cbranch.getNodeList().add(node);
					node.setId(cbranch.getNodeList().size());
					node.setCurrentBranch(cbranch);
					break;
				}

				cbranch = null;
			}

			// allocate one more branch and set the node there
			if (cbranch == null) {
				cnode = rootbranch.getOneNewNode();
				NodeTablePointer ctn = cnode.table;
				ctn.setNumId(NUM_ATTRIBUTES);
				ctn.setId(valueId, nodeVid);

				cbranch = tree.getOneNewBranch();
				cnode.setChildBranch(cbranch);
				cbranch.setParentNode(cnode);
				cbranch.getNodeList().add(node);
				node.setCurrentBranch(cbranch);
				node.setId(1);
			}

		}

		// form the lower levels
		for (int j = 0; j < rootbranch.getNodeList().size(); j++) {
			cnode = (Node) rootbranch.getNodeList().elementAt(j);
			cbranch = cnode.getChildBranch();
			formLowerHierarchy(tree, cbranch, 1);
		}

	}

}
