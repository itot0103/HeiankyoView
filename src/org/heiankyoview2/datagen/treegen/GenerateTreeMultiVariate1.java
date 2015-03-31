package org.heiankyoview2.datagen.treegen;

import java.io.File;

import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.tree.*;
import org.heiankyoview2.core.fileio.TreeFileWriter;

public class GenerateTreeMultiVariate1 {
	static final int DEPTH = 5;      // ¶¬‚³‚ê‚éŠK‘w‚Ì[‚³
	static final int NUMBRANCH = 4;  // Ž}•ª‚©‚ê‚Ì”
	static final int MAXLEAF = 30;   // —t‚ÌÅ‘åŒÂ”
	static final int NUMVAL = 6;     // •Ï”‚Ì”

	static int totalNode = 0;
	static int countNode = 0;
	
	/*
	 * TreeƒNƒ‰ƒX‚ðŠm•Û‚·‚é
	 */
	Tree initializeTree() {

		/*
		 * TreeƒNƒ‰ƒX‚Ì‰Šú‰»
		 */
		Tree tree = new Tree();
		TreeTable tg = tree.table;
		tg.setNumTable(NUMVAL + 2);

		/*
		 * rootBranch‚ÌÝ’è
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
	 * Ä‹A“I‚ÉNode‚ð¶¬‚·‚é
	 */
	void generateNodeRecursively(Tree tree, Branch branch) {
		int level = branch.getLevel();
	
		/*
		 * —tƒm[ƒh‚Ì¶¬
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
		 * Ž}ƒm[ƒh‚Ì¶¬
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
	 * Ä‹A“I‚É—tƒm[ƒh‚ð’T‚µo‚µ‚ÄA•Ï”’l‚ð‘ã“ü‚·‚é
	 */
	void setTableValueRecursively(Tree tree, Branch branch) {
		TreeTable tg = tree.table;
		
		/*
		 * —tƒm[ƒh‚ð’T‚µo‚·
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
		 * Ž}ƒm[ƒh‚©‚çŠK‘w‚ð‰º‚Á‚Ä‚¢‚­
		 */
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			Branch cbranch = node.getChildBranch();
			if(cbranch == null) continue;
			setTableValueRecursively(tree, cbranch);
		}
	}
	
	
	/*
	 * Table‚ð¶¬‚·‚é
	 */
	void generateTable(Tree tree) {
		TreeTable tg = tree.table;
		

		/*
		 * Table‚ÌÝ’è
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
		 * Ä‹A“I‚ÉTable‚Ì”’l‚ðŒˆ’è‚·‚é
		 */
		setTableValueRecursively(tree, tree.getRootBranch());
		
	}
	
	
	/*
	 * Tree‚ð¶¬‚·‚é
	 */
	public Tree generate() {
		Tree tree = initializeTree();
		generateNodeRecursively(tree, tree.getRootBranch());
		generateTable(tree);
		
		return tree;
	}

	
	/*
	 * mainŠÖ”
	 */
	public static void main(String args[]) {
		GenerateTreeMultiVariate1 gnmv = new GenerateTreeMultiVariate1();
		Tree tree = gnmv.generate();
		TreeFileWriter tnf = new TreeFileWriter(new File("multivariate1.tree"), tree);
		tnf.writeTree();
	}
}
