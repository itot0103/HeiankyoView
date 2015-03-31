package org.heiankyoview2.datagen.treegen;

import java.io.File;

import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.tree.*;
import org.heiankyoview2.core.fileio.TreeFileWriter;

public class GenerateTreeMultiVariate1 {
	static final int DEPTH = 5;      // ���������K�w�̐[��
	static final int NUMBRANCH = 4;  // �}������̐�
	static final int MAXLEAF = 30;   // �t�̍ő��
	static final int NUMVAL = 6;     // �ϐ��̐�

	static int totalNode = 0;
	static int countNode = 0;
	
	/*
	 * Tree�N���X���m�ۂ���
	 */
	Tree initializeTree() {

		/*
		 * Tree�N���X�̏�����
		 */
		Tree tree = new Tree();
		TreeTable tg = tree.table;
		tg.setNumTable(NUMVAL + 2);

		/*
		 * rootBranch�̐ݒ�
		 */
		Node rootnode = tree.getRootNode();
		tree.setNumBranch(1);
		Branch rootbranch = tree.getBranchAt(1);
		tree.setRootBranch(rootbranch);
		rootnode.setChildBranch(rootbranch);
		rootbranch.setParentNode(rootnode);
		rootbranch.setLevel(1);

		return tree;
	}

	
	/*
	 * �ċA�I��Node�𐶐�����
	 */
	void generateNodeRecursively(Tree tree, Branch branch) {
		int level = branch.getLevel();
	
		/*
		 * �t�m�[�h�̐���
		 */
		if(level >= DEPTH) {
			int numleaf = (int)((double)MAXLEAF * Math.random());
			for(int i = 0; i < numleaf; i++) {
				totalNode++;
				branch.getOneNewNode();
			}
			return;
		}
		
		/*
		 * �}�m�[�h�̐���
		 */
		else {
			for(int i = 0; i < NUMBRANCH; i++) {
				Node node = branch.getOneNewNode();
				Branch cbranch = tree.getOneNewBranch();
				node.setChildBranch(cbranch);
				cbranch.setParentNode(node);
				cbranch.setLevel(level + 1);
			}
			for(int i = 0; i < branch.getNodeList().size(); i++) {
				Node node = (Node)branch.getNodeList().elementAt(i);
				Branch cbranch = node.getChildBranch();
				generateNodeRecursively(tree, cbranch);
			}
		}
		
	}
	
	
	/*
	 * �ċA�I�ɗt�m�[�h��T���o���āA�ϐ��l��������
	 */
	void setTableValueRecursively(Tree tree, Branch branch) {
		TreeTable tg = tree.table;
		
		/*
		 * �t�m�[�h��T���o��
		 */
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() != null) continue;
			countNode++;
			NodeTablePointer tn = node.table;
			tn.setNumId(NUMVAL + 2);
			tn.setId(1, countNode);
			tn.setId(2, countNode);
			Table table = tg.getTable(1);
			table.set(countNode, Integer.toString(countNode));
			table = tg.getTable(2);
			table.set(countNode, Integer.toString(countNode));
			for(int j = 3; j <= (NUMVAL + 2); j++) {
				tn.setId(j, countNode);
				table = tg.getTable(j);
				double value = (double)(countNode % (100 * j)) / (double)(100 * j);
				table.set(countNode, value);
			}
		}
		
		/*
		 * �}�m�[�h����K�w�������Ă���
		 */
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			Branch cbranch = node.getChildBranch();
			if(cbranch == null) continue;
			setTableValueRecursively(tree, cbranch);
		}
	}
	
	
	/*
	 * Table�𐶐�����
	 */
	void generateTable(Tree tree) {
		TreeTable tg = tree.table;
		

		/*
		 * Table�̐ݒ�
		 */
		for(int i = 1; i <= 2; i++) {
			Table table = new Table();
			tg.setTable(i, table);
			table.setName("Name");
			table.setType(1);
			table.setSize(totalNode);
		}
		
		for(int i = 3; i <= (NUMVAL + 2); i++) {
			Table table = new Table();
			tg.setTable(i, table);
			table.setName("Variable" + Integer.toString(i - 1));
			table.setType(2);
			table.setSize(totalNode);
		}
		
		/*
		 * �ċA�I��Table�̐��l�����肷��
		 */
		setTableValueRecursively(tree, tree.getRootBranch());
		
	}
	
	
	/*
	 * Tree�𐶐�����
	 */
	public Tree generate() {
		Tree tree = initializeTree();
		generateNodeRecursively(tree, tree.getRootBranch());
		generateTable(tree);
		
		return tree;
	}

	
	/*
	 * main�֐�
	 */
	public static void main(String args[]) {
		GenerateTreeMultiVariate1 gnmv = new GenerateTreeMultiVariate1();
		Tree tree = gnmv.generate();
		TreeFileWriter tnf = new TreeFileWriter(new File("multivariate1.tree"), tree);
		tnf.writeTree();
	}
}
