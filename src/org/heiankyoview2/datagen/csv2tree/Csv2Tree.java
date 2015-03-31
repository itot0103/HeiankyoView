package org.heiankyoview2.datagen.csv2tree;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.NodeTablePointer;

import java.util.*;


/**
 * CSV�f�[�^����͂���Tree�𐶐�����
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
	 * CSV�f�[�^1�s�ɑ΂��鑮�������Z�b�g����
	 * @param num 1�s�ɑ΂��鑮����
	 */
	public void setNumAttributes(int num) {
		NUM_ATTRIBUTES = num;
	}

	/**
	 * CSV�f�[�^1�s�ɑ΂��鑮������Ԃ�
	 * @return 1�s�ɑ΂��鑮����
	 */
	public int getNumAttributes() {
		return NUM_ATTRIBUTES;
	}

	/**
	 * CSV�f�[�^����\�z����K�w�̐[�����Z�b�g����
	 * @param num �K�w�̐[��
	 */
	public void setNumLevel(int num) {
		NUM_HIERARCHY = num;
	}

	/**
	 * CSV�f�[�^����\�z����K�w�̐[����Ԃ�
	 * @return �K�w�̐[��
	 */
	public int getNumLevel() {
		return NUM_HIERARCHY;
	}

	/**
	 * CSV�f�[�^����K�w���\�z����ۂɁA�e�X�̐[���ŎQ�Ƃ��鑮����ID���Z�b�g����
	 * @param array ������ID���i�[����z��
	 */
	public void setArrayHierarchy(int array[]) {
		ARRAY_HIERARCHY = new int[array.length];
		for (int i = 0; i < array.length; i++)
			ARRAY_HIERARCHY[i] = array[i];
	}

	/**
	 * CSV�f�[�^����K�w���\�z����ۂɁA�e�X�̐[���ŎQ�Ƃ��鑮����ID��Ԃ�
	 * @return ������ID���i�[����z��
	 */
	public int[] getArrayHierarchy() {
		return ARRAY_HIERARCHY;
	}

	/**
	 * �w�肳�ꂽBranch�̉��ʊK�w���`������
	 * @param tree Tree
	 * @param branch Branch
	 * @param hierarchy �K�w�̌��݂̐[��
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
	 * �Z�b�g���ꂽ�p�����[�^�ɂ��������ĊK�w�\�����`������
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
