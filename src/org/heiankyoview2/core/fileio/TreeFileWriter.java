package org.heiankyoview2.core.fileio;

import org.heiankyoview2.core.tree.Link;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import java.io.*;
import java.util.*;


/**
 * treeファイルへの書き出し
 * @author itot
 */
public class TreeFileWriter {

	/* var */
	Tree tree = null;
	FileOutput output = null;
	TableFileWriter table = null;
	int numBranch;
	String dummy = " ";

	/**
	 * Constructor
	 * @param outputFile 出力先treeファイル
	 * @param tree Tree
	 */
	public TreeFileWriter(File outputFile, Tree tree) {

		output = new FileOutput(outputFile);
		this.tree = tree;
	}

	/**
	 * treeファイルを書き出す
	 * @return 成功すればtrue
	 */
	public boolean writeTree() {
		boolean ret;

		if (tree == null) {
			output.close();
			return false;
		}
		table = new TableFileWriter(tree, output);
		table.writeTables(tree);
		
		output.println(dummy);
		output.println(dummy);

		numBranch = tree.getBranchList().size();
		Integer InumBranch = new Integer(numBranch);
		String linebuf2 = "numbranch  " + InumBranch.toString();
		output.println(linebuf2);
		output.println(dummy);

		for (int i = 1; i <= numBranch; i++) {

			Branch branch = tree.getBranchAt(i);
			ret = writeOneBranch(branch);
			if (ret == false) {
				output.close();
				return ret;
			}
		}

		/* link */
		Vector linklist = tree.getLinkList();
		output.println("numlink " + linklist.size());
		for (int i = 0; i < linklist.size(); i++) {
			Link link = (Link) linklist.elementAt(i);
			Node node1 = link.getNode1();
			Node node2 = link.getNode2();
			int nid1 = node1.getId();
			int nid2 = node2.getId();
			int gid1 = node1.getCurrentBranch().getId();
			int gid2 = node2.getCurrentBranch().getId();
			output.println("linknode " + (i + 1) + " " + gid1 + " "
					+ nid1 + " " + gid2 + " " + nid2);
		}

		/* frame */
		String frameFilename = tree.getFrameFilename();
		if(frameFilename != null && frameFilename.length() > 0) {
			output.println(dummy);
			output.println("framefilename " + frameFilename);
		}
		
		output.close();
		return true;

	}


	/**
	 * treeファイルに1個のBranchの情報を書き出す
	 * @param branch Branch
	 * @return 成功すればtrue
	 */
	public boolean writeOneBranch(Branch branch) {
		boolean ret;
		int count;
		String linebuf;

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

		Node pnode = branch.getParentNode();
		linebuf = 
			"branchsize "
				+ Iid.toString()
				+ " "
				+ pnode.getWidth()
				+ " "
				+ pnode.getHeight()
				+ " "
				+ pnode.getDepth();
		output.println(linebuf);
		if (pnode.getImageUrl() != null) {
			linebuf =
				"branchimageurl "
					+ Iid.toString()
					+ " "
					+ pnode.getImageUrl();
			output.println(linebuf);
		}
		if(pnode.getPlaced() == true) {
			linebuf = 
				"branchpos "
					+ Iid.toString()
					+ " "
					+ pnode.getX()
					+ " "
					+ pnode.getY()
					+ " "
					+ pnode.getZ();
			output.println(linebuf);
		}
		
		//
		// count the number of parent and non-parent nodes
		//
		int numNode1 = 0, numNode2 = 0;
		Vector nodeList = branch.getNodeList();
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = (Node)nodeList.elementAt(i);
			Branch childBranch = node.getChildBranch();
			if (childBranch == null)
				numNode1++;
			else
				numNode2++;
		}

		Integer InumNode1 = new Integer(numNode1);
		linebuf =
			"numnode " + Iid.toString() + " " + InumNode1.toString();
		output.println(linebuf);

		Integer InumNode2 = new Integer(numNode2);
		linebuf =
			"numbranch " + Iid.toString() + " " + InumNode2.toString();
		output.println(linebuf);

		table.writeNodeTablePointer(branch.getParentNode(), true);


		//
		// for each child branch
		//
		count = 1;
		for (int i = 1; i <= nodeList.size(); i++) {
			Node node = branch.getNodeAt(i);
			Branch childBranch = node.getChildBranch();
			if (childBranch == null)
				continue;

			int childId = childBranch.getId();
			Integer Icount = new Integer(count);
			Integer IchildId = new Integer(childId);

			linebuf =
				"childbranch " + Icount.toString() + " " + IchildId.toString();
			output.println(linebuf);

			count++;
		}

		//
		// for each node
		//
		for (int i = 1; i <= nodeList.size(); i++) {
			Node node = branch.getNodeAt(i);
			Branch childBranch = node.getChildBranch();
			if (childBranch != null)
				continue;

			int nid = node.getId();
			Integer Inid = new Integer(nid);
			linebuf = 
				"nodesize "
					+ Inid.toString()
					+ " "
					+ node.getWidth()
					+ " "
					+ node.getHeight()
					+ " "
					+ node.getDepth();
			output.println(linebuf);
			

			//
			// child filename
			//
			if (node.getChildFilename() != null) {
				linebuf =
					"nodefile "
						+ Inid.toString()
						+ " "
						+ node.getChildFilename();
				output.println(linebuf);
			}

			//
			//
			// icon filename
			//
			if (node.getImageUrl() != null) {
				linebuf =
					"nodeimageurl "
						+ Inid.toString()
						+ " "
						+ node.getImageUrl();
				output.println(linebuf);
			}
			
			if (node.getFrameNodeId() > 0) {
				linebuf =
					"framenodeid "
						+ Inid.toString()
						+ " "
						+ node.getFrameNodeId();
				output.println(linebuf);
			}
			
			if(node.getPlaced() == true) {
				
				linebuf = 
					"nodepos "
						+ Inid.toString()
						+ " "
						+ node.getX()
						+ " "
						+ node.getY()
						+ " "
						+ node.getZ();
				output.println(linebuf);
			}
			
			table.writeNodeTablePointer(node, false);

			output.println(dummy);
		}

		output.println(dummy);
		return true;
	}

}
