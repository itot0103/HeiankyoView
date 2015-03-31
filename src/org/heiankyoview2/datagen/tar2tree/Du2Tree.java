package org.heiankyoview2.datagen.tar2tree;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.fileio.TreeFileWriter;

import java.io.*;
import java.util.*;


/**
 * tarコマンドの標準出力を用いて、ファイルシステムの階層構造をtreeファイル形式で生成する
 * Usage:
 * 1. Prepare a tar file.
 * 2. Create a data text file by "tar tvf (tarfile) > (textfile)"
 * 3. Invoke "java Tar2Net (textfile) (treefile) (steps of texifile)"
 *  * @author itot
 */
public class Du2Tree {
	String pathBuffer[] = new String[20];
	Node nodeBuffer[] = new Node[20];
	int nodeCounter = 0, maxDepth = 0;
	boolean isDirectory = false;
	String initialDir = null;
	int initialNumSlash = 0;

	/**
	 * Constructor
	 */
	public Du2Tree() {
	}
	
	/**
	 * Treeを初期化する
	 * @param tree Tree
	 * @param tablesize 想定されるファイル数
	 */
	public void initializeTree(Tree tree, int tablesize) {
		Table table;
		TreeTable tg = tree.table;

		//
		// Set data type
		//

		//
		// Allocate tables
		// 
		tg.setNumTable(2);

		table = new Table();
		tg.setTable(1, table);
		table.setName("path");
		table.setType(1); // String
		table.setSize(tablesize);

		table = new Table();
		tg.setTable(2, table);
		table.setName("filesize");
		table.setType(2); // Double
		table.setSize(tablesize);

	}

	/**
	 * Treeを再構築する
	 * @param tree Tree
	 */
	public void rearrangeTree(Tree tree) {
		int i, j;
		boolean isNode = false;

		Vector branchList = tree.getBranchList();
		for (i = 0; i < branchList.size(); i++) {
			Branch branch = (Branch) branchList.elementAt(i);

			Vector nodeList = branch.getNodeList();
			for (j = nodeList.size() - 1, isNode = false; j >= 0; j--) {
				Node node = (Node) nodeList.elementAt(j);
				if (node.getChildBranch() == null)
					isNode = true;
				if (isNode == true && node.getChildBranch() != null) {
					branch.exchangeParentNodeOrder(node);
					j = nodeList.size();
					isNode = false;
				}
			}
		}
	}
	
	/**
	 * ファイルのパスを解析する
	 * @param path パスを表す文字列
	 * @return パスの深さを表す整数値
	 */
	public int analyzePath(String path) {
		int countDepth = 0;
		int ret;

		/*
		if (path.compareTo("./") == 0) {
			pathBuffer[0] = "./";
			isDirectory = true;
			return 0;
		}
		*/
		if (initialDir == null) {
			pathBuffer[0] = initialDir = path;
			isDirectory = true;
			for (int i = 0; i < path.length() - 1; i++) {
				if (path.charAt(i) == '/') initialNumSlash++;
			}
			return 0;
		}


		//
		// for each slash
		//
		for (int i = 0; i < path.length(); i++) {

			if (i == path.length() - 1) {
				pathBuffer[countDepth] = path;
				if (countDepth > maxDepth)
					maxDepth = countDepth;
				if (path.charAt(i) == '/')
					isDirectory = true;
				else {
					isDirectory = false;
				}
				break;
			}

			if (path.charAt(i) == '/')
				countDepth++;
		}

		return countDepth - initialNumSlash;
	}


	/**
	 * Nodeを1個追加する
	 * @param tree Tree
	 * @param path ファイルのパス
	 * @param fs ファイルサイズ
	 * @param ds 日付を表す値
	 * @param depth 階層の深さ
	 */
	public void addNode(
		Tree tree,
		String path,
		String fs,
		int depth) {

		Double fSize = new Double(fs);
		double filesize = fSize.doubleValue();

		Node parentNode;
		Branch branch;
		Node node;
		Table table;

		if (isDirectory) {
			if (depth == 0) {
				node = tree.getRootNode();
				tree.setNumBranch(1);
				branch = tree.getBranchAt(1);
				tree.setRootBranch(branch);
				nodeBuffer[0] = node;
			} else {
				parentNode = nodeBuffer[depth - 1];
				branch = parentNode.getChildBranch();
				node = branch.getOneNewNode();
				branch = tree.getOneNewBranch();
				node.setChildBranch(branch);
				branch.setParentNode(node);
				nodeBuffer[depth] = node;
			}
		} else { // is File
			parentNode = nodeBuffer[depth - 1];
			branch = parentNode.getChildBranch();
			node = branch.getOneNewNode();
		}

		nodeCounter++;
		NodeTablePointer tn = node.table;
		tn.setNumId(2);
		tn.setId(1, nodeCounter);
		tn.setId(2, nodeCounter);
		
		TreeTable tg = tree.table;

		table = tg.getTable(1);
		table.set(nodeCounter, path);

		table = tg.getTable(2);
		table.set(nodeCounter, filesize);

	}

	/**
	 * tarファイルを開く
	 * @param tarFilename 入力tarファイル名
	 * @param tree Tree
	 */
	public void tarFileOpen(String tarFilename, Tree tree) {
		File tarFile = new File(tarFilename);
		BufferedReader reader;

		// Read file
		try {
			reader = new BufferedReader(new FileReader(tarFile));
		} catch (FileNotFoundException e) {
			System.err.println(e);
			return;
		}

		try {
			reader.ready();
			while (true) {
				String line = reader.readLine();
				if (line == null || line.length() <= 0)
					break;
				StringTokenizer tokenBuffer = new StringTokenizer(line);

				// Filesize
				String filesize = tokenBuffer.nextToken();

				// Filepath
				String path = tokenBuffer.nextToken();

				int depth = analyzePath(path);
				System.out.println(" path=" + path + " depth=" + depth);
				addNode(tree, path, filesize, depth);
			}
			reader.close();
		} catch (IOException e) {
			System.err.println(e);
			return;
		}
	}


	/**
	 * main関数
	 * @param args 引数
	 */
	public static void main(String[] args) {
		Du2Tree dn = new Du2Tree();

		String duFilename = args[0];
		String treeFilename = args[1];
		String ts = args[2];
		Integer tSize = new Integer(ts);
		int tablesize = tSize.intValue();

		Tree tree = new Tree();
		dn.initializeTree(tree, tablesize);
		dn.tarFileOpen(duFilename, tree);
		dn.rearrangeTree(tree);

		TreeFileWriter output = new TreeFileWriter(new File(treeFilename), tree);
		output.writeTree();
	}

}
