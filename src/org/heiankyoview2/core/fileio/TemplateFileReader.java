package org.heiankyoview2.core.fileio;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Link;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.TreeTable;
import java.io.*;
import java.util.*;


/**
 * Templateファイルを読み込む
 * @author itot
 */
public class TemplateFileReader {

	/* var */
	FileInput input = null;
	Tree tree = null;

	TableFileReader table = null;

	Vector branchList = null;
	int numBranches;
	int attributeType;

	/**
	 * Constructor
	 * @param inputFile 読み込みファイル
	 */
	public TemplateFileReader(File inputFile) {
		input = new FileInput(inputFile);
		branchList = new Vector();
	}

	/**
	 * treeファイルを読み込み、生成したTreeを返す
	 * @return 生成したTree
	 */
	public Tree getData() {
		if (tree == null) { //if non-exit, generate
			tree = new Tree(); //create tree
			table = new TableFileReader(tree);
			if (!parseData(tree)) {
				input.close();
				return null;
			}
		}
		input.close();
		return tree;
	}

	/**
	 * treeファイルをparseして、Treeを構成するデータを構築する
	 * @param tree Tree
	 * @return 成功すればtrue
	 */
	public boolean parseData(Tree tree) {

		double maxX = 0.0, minX = 0.0, maxY = 0.0, minY = 0.0;

		String lineBuffer;
		int numbranch = 0;
		int numnode = 0;
		Branch currentBranch;
		Branch rootBranch;
		int branchID = 0;
		int nodeCountforSize = 0;

		while (input.ready() && (lineBuffer = input.read()) != null) {
			//read .NVW file
			if (lineBuffer.startsWith("#")) {
				continue; //skip comment line
				
			} else if (lineBuffer.startsWith("numbranch")) {
				numbranch = Integer.parseInt(lineBuffer.substring(9).trim());
				tree.setNumBranch(numbranch);
				continue;
			} else if (lineBuffer.startsWith("branchtitle")) {
				String branchName =
					setBranchName(tree, lineBuffer.substring(11).trim());
				continue;
			} else if (lineBuffer.startsWith("numnode")) {
				branchID = setNumNode(tree, lineBuffer.substring(7).trim());
				continue;
			} else if (lineBuffer.startsWith("numlink")) {
				setNumLink(tree, lineBuffer.substring(7).trim());
				continue;
			} else if (lineBuffer.startsWith("nodepos")) {
				Node currentNode =
					setNNodePos(tree, branchID, lineBuffer.substring(7).trim());
				continue;
			} else if (lineBuffer.startsWith("nodesize")) {
				nodeCountforSize++;
				continue;
			} else if (lineBuffer.startsWith("nodeimageurl")) {
				setNodeImageUrl(tree, branchID, lineBuffer.substring(12).trim());
				continue;
			} else if (lineBuffer.startsWith("nodefile")) {
				setNodeFilename(tree, branchID, lineBuffer.substring(8).trim());
				continue;
			} else if (lineBuffer.startsWith("linknode")) {
				setLinkNode(tree, lineBuffer.substring(8).trim());
				continue;
			} else if (lineBuffer.startsWith("childbranch")) {
				setChild(tree, branchID, lineBuffer.substring(11).trim());
				continue;
			} else 
				table.parseTableData(tree, branchID, lineBuffer);
			
		}

		setRootBranch(tree);

		return true; //if data read successfully!
	}



	/**
	 * BranchのIDを指定して、Branchを返す
	 * @param id BranchのID
	 * @return Branch
	 */
	public Branch getBranchAt(int id) {
		if (id < 1 || id > branchList.size()) {
			return null;
		} else
			return (Branch) branchList.elementAt(id - 1);

	}

	/**
	 * Branchの名前を指定する
	 * @param tree Tree
	 * @param lineBuffer treeファイルから読み込んだ行
	 * @return 現在処理しているBranchのID
	 */
	String setBranchName(Tree tree, String lineBuffer) {

		StringTokenizer tokenBuffer = new StringTokenizer(lineBuffer);
		if (tokenBuffer.countTokens() < 2)
			return null;

		// get ID
		String idStr = tokenBuffer.nextToken();
		int branchId = Integer.parseInt(idStr);
		// get title (title="*** *** ***")
		String tmp = tokenBuffer.nextToken();
		int nameIndex = lineBuffer.indexOf(tmp, idStr.length() + 1);
		String name = lineBuffer.substring(nameIndex);

		Branch currentBranch;
		currentBranch = tree.getBranchAt(branchId);
		currentBranch.setName(name);

		return name;
	}

	/**
	 * Branchに属するNodeの個数をセットする
	 * @param tree Tree
	 * @param lineBuffer treeファイルから読み込んだ行
	 * @return 現在処理しているBranchのID
	 */
	int setNumNode(Tree tree, String lineBuffer) {
		Branch currentBranch;
		int numnode;
		int branchID;

		StringTokenizer tokenBuffer = new StringTokenizer(lineBuffer);
		if (tokenBuffer.countTokens() < 2)
			return 0;

		branchID = Integer.parseInt(tokenBuffer.nextToken());
		if ((currentBranch = tree.getBranchAt(branchID)) == null)
			return 0;

		numnode = Integer.parseInt(tokenBuffer.nextToken());
		currentBranch.setNumNode(numnode);

		return branchID;
	}


	/**
	 * Nodeのサイズを読み込み、該当するNodeを返す
	 * @param tree Tree
	 * @param branchID 現在処理しているBranchのID
	 * @param lineBuffer nvwファイルから読み込んだ行
	 * @return 読み込んだ行に指定されているNode
	 */
	Node setNodeSize(
		Tree tree, int branchID,
		String lineBuffer) {

		StringTokenizer tokenBuffer = new StringTokenizer(lineBuffer);
		int nodeID;
		Node currentNode;

		if (tokenBuffer.countTokens() < 4)
			return null;

		nodeID = Integer.parseInt(tokenBuffer.nextToken());
		currentNode = tree.getBranchAt(branchID).getNodeAt(nodeID);
		if (currentNode == null)
			return null;

		currentNode.setNsize(
			Double.parseDouble(tokenBuffer.nextToken()),
			Double.parseDouble(tokenBuffer.nextToken()),
			Double.parseDouble(tokenBuffer.nextToken()));

		return currentNode;
	}

	/**
	 * Nodeに紐つけられているファイルの名前をセットする
	 * @param tree Tree
	 * @param branchID 現在処理しているBranchのID
	 * @param lineBuffer treeファイルから読み込んだ行
	 */ 
	void setNodeFilename(
		Tree tree, int branchID,
		String lineBuffer) {

		StringTokenizer tokenBuffer = new StringTokenizer(lineBuffer);
		Node currentNode;

		if (tokenBuffer.countTokens() < 2)
			return;

		int nodeID = Integer.parseInt(tokenBuffer.nextToken());
		currentNode = tree.getBranchAt(branchID).getNodeAt(nodeID);
		if (currentNode == null)
			return;

		currentNode.setChildFilename(tokenBuffer.nextToken());
	}

	/**
	 * Nodeに紐つけられている画像URLの名前をセットする
	 * @param tree Tree
	 * @param branchID 現在処理しているBranchのID
	 * @param lineBuffer treeファイルから読み込んだ行
	 */
	void setNodeImageUrl(Tree tree, int branchID, String lineBuffer) {

		StringTokenizer tokenBuffer = new StringTokenizer(lineBuffer);
		Node currentNode;

		if (tokenBuffer.countTokens() < 2)
			return;

		int nodeID = Integer.parseInt(tokenBuffer.nextToken());
		currentNode = tree.getBranchAt(branchID).getNodeAt(nodeID);
		if (currentNode == null)
			return;

		String surl = tokenBuffer.nextToken();
		currentNode.setImageUrl(surl);
		//System.out.println(surl);
		
		/*
		Image image = null;
		
		try {
			if(surl.startsWith("http")) {
				URL url = new URL(surl);
				image = ImageIO.read(url);
			}
			else {
				image = ImageIO.read(new File(surl));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		currentNode.setImage(image);
		*/
	}
		
	/**
	 * アークの本数を指定する
	 * @param Tree
	 * @param lineBuffer
	 */
	void setNumLink(Tree tree, String lineBuffer) {
		Branch currentBranch;
		int numlink;
		int branchID;

		StringTokenizer tokenBuffer = new StringTokenizer(lineBuffer);
		if (tokenBuffer.countTokens() < 1)
			return;

		numlink = Integer.parseInt(tokenBuffer.nextToken());
		tree.setNumLink(numlink);
	}


	/**
	 * アークの両端のノードを指定する
	 * @param Tree
	 * @param lineBuffer
	 */
	void setLinkNode(Tree tree, String lineBuffer) {

		StringTokenizer tokenBuffer = new StringTokenizer(lineBuffer);
		int linkID, branchID;
		Branch currentBranch;
		Node node1;
		Node node2;

		if (tokenBuffer.countTokens() < 5)
			return;

		linkID = Integer.parseInt(tokenBuffer.nextToken());

		branchID = Integer.parseInt(tokenBuffer.nextToken());
		currentBranch = tree.getBranchAt(branchID);
		node1 =
			currentBranch.getNodeAt(Integer.parseInt(tokenBuffer.nextToken()));

		branchID = Integer.parseInt(tokenBuffer.nextToken());
		currentBranch = tree.getBranchAt(branchID);
		node2 =
			currentBranch.getNodeAt(Integer.parseInt(tokenBuffer.nextToken()));

		Link link = tree.getLinkAt(linkID);
		link.setNodes(node1, node2);
		
	}


	/**
	 * Branchに対する子Branchをセットする
	 * @param tree Tree
	 * @param branchID 現在処理しているBranchのID
	 * @param lineBuffer treeファイルから読み込んだ行
	 */ 
	void setChild(Tree tree, int branchID, String lineBuffer) {

		StringTokenizer tokenBuffer = new StringTokenizer(lineBuffer);
		int nodeID;
		Branch currentBranch;
		Node currentNode;
		int childbranchID;
		Branch childBranch;

		if (tokenBuffer.countTokens() < 2)
			return;
		if ((currentBranch = tree.getBranchAt(branchID)) == null)
			return;

		nodeID = Integer.parseInt(tokenBuffer.nextToken());
		currentNode = currentBranch.getNodeAt(nodeID);
		if (currentNode == null)
			return;

		childbranchID = Integer.parseInt(tokenBuffer.nextToken());
		childBranch = tree.getBranchAt(childbranchID);
		if (childBranch == null)
			return;

		childBranch.setLevel(currentBranch.getLevel() + 1);
		currentNode.setChildBranch(childBranch);
		currentNode.getChildBranch().setParentNode(currentNode);
		
	}

	/**
	 * Nodeの正規化座標系での座標値をセットする
	 * @param tree Tree
	 * @param branchID 現在処理しているBranchのID
	 * @param lineBuffer nvwファイルから読み込んだ行
	 * @return 読み込んだ行に指定されているNode
	 */
	Node setNNodePos(
		Tree tree, int branchID,
		String lineBuffer) {

		StringTokenizer tokenBuffer = new StringTokenizer(lineBuffer);
		Node currentNode = null;
		int nodeID;

		if (tokenBuffer.countTokens() < 4)
			return null;

		Branch branch = (Branch)tree.getBranchList().elementAt(branchID - 1);
		nodeID = Integer.parseInt(tokenBuffer.nextToken());
		currentNode = tree.getBranchAt(branchID).getNodeAt(nodeID);
		double x = Double.parseDouble(tokenBuffer.nextToken());
		double y = Double.parseDouble(tokenBuffer.nextToken());
		double z = Double.parseDouble(tokenBuffer.nextToken());

		currentNode.setNCoordinate(x, y, z);
		return currentNode;
	}

	/**
	 * Treeに根Branchをセットする
	 * @param tree Branch
	 */
	void setRootBranch(Tree Tree) {

		if (tree == null)
			return;

		for (int i = 1; i <= tree.getNumBranch(); i++) {
			if (tree.getBranchAt(i).getParentNode() == null) {
				tree.setRootBranch(tree.getBranchAt(i));
			}
		}
	}
}
