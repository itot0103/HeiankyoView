package org.heiankyoview2.core.fileio;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import java.io.*;


/**
 * Templateファイルへの書き出し
 * @author itot
 */
public class TemplateFileWriter {

	/* var */
	Tree tree = null;
	FileOutput output = null;
	TableFileWriter table = null;
	int numBranches, countBranches;
	String dummy = " ";

	/**
	 * Constructor
	 * @param outputFile 出力先templateファイル
	 * @param tree Tree
	 */
	public TemplateFileWriter(File outputFile, Tree tree) {

		output = new FileOutput(outputFile);
		this.tree = tree;
	}


	/**
	 * templateファイルに情報を書き出す
	 * @return 成功すればtrue
	 */
	public boolean writeData() {
		boolean ret;

		if (tree == null) {
			output.close();
			return false;
		}
		table = new TableFileWriter(tree, output);
		table.writeTables(tree);

		//
		// Number of branches
		//
		int numb = tree.getBranchList().size();
		Integer Inumb = new Integer(numb);
		String linebuf2 = "numbranch  " + Inumb.toString();
		output.println(linebuf2);
		output.println(dummy);

		//
		// for each branch
		//
		for (countBranches = 1; countBranches <= numb; countBranches++) {

			Branch branch = tree.getBranchAt(countBranches);
			ret = writeOneBranch(branch);
			if (ret == false) {
				output.close();
				return ret;
			}
		}

		output.close();
		return true;

	}

	/**
	 * templateファイルに1個のBranchの情報を書き出す
	 * @param branch Branch
	 * @return 成功すればtrue
	 */
	public boolean writeOneBranch(Branch branch) {
		boolean ret;

		//
		// branch ID
		//
		int id = branch.getId();
		Integer Iid = new Integer(id);
		String linebuf1 = "branch " + Iid.toString() + " #######";
		output.println(linebuf1);

		if (branch != null) {
			String name = branch.getName();
			String line;
			line = "branchtitle " + Iid.toString() + " " + name;
			output.println(line);
		}
		
		//
		// number of nodes
		//
		int numNode = branch.getNodeList().size();
		Integer InumNode = new Integer(numNode);
		String linebuf2 =
			"numnode " + Iid.toString() + " " + InumNode.toString();
		output.println(linebuf2);

		
		//
		// table data for the current branch
		//
		Node pnode = branch.getParentNode();
		table.writeNodeTablePointer(pnode, true);

		//
		// for each node
		//
		for (int i = 1; i <= branch.getNodeList().size(); i++) {
			Node node = branch.getNodeAt(i);
			writeOneNode(node);
		}

		//
		// for each child tree
		//
		for (int i = 1; i <= branch.getNodeList().size(); i++) {
			Node node = branch.getNodeAt(i);
			Branch childBranch = node.getChildBranch();
			if (childBranch == null)
				continue;

			int nodeId = node.getId();
			int childId = childBranch.getId();
			Integer InodeId = new Integer(nodeId);
			Integer IchildId = new Integer(childId);

			String linebuf4 =
				"childbranch " + InodeId.toString() + " " + IchildId.toString();
			output.println(linebuf4);

		}

		output.println(dummy);
		return true;
	}

	/**
	 * templateファイルに1個のNodeの情報を書き出す
	 * @param node Node
	 */
	public void writeOneNode(Node node) {

		int id = node.getId();
		Integer Iid = new Integer(id);

		//
		// position
		//
		double x = node.getNX();
		double y = node.getNY();
		double z = node.getNZ();
		Double Dx = new Double(x);
		Double Dy = new Double(y);
		Double Dz = new Double(z);
		String linebuf1 =
			"nodepos "
				+ Iid.toString()
				+ " "
				+ Dx.toString()
				+ " "
				+ Dy.toString()
				+ " "
				+ Dz.toString();
		output.println(linebuf1);

		//
		// size
		//
		double width = node.getNwidth();
		double height = node.getNheight();
		double depth = node.getNdepth();

		Double Dwidth = new Double(width);
		Double Dheight = new Double(height);
		Double Ddepth = new Double(depth);
		String linebuf2 =
			"nodesize "
				+ Iid.toString()
				+ " "
				+ Dwidth.toString()
				+ " "
				+ Dheight.toString()
				+ " "
				+ Ddepth.toString();
		output.println(linebuf2);

		table.writeNodeTablePointer(node, false);

	}

}
