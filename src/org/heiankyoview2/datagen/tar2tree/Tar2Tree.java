package org.heiankyoview2.datagen.tar2tree;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.fileio.TreeFileWriter;
import org.heiankyoview2.core.xmlio.XmlTreeFileWriter;

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
public class Tar2Tree {
	String pathBuffer[] = new String[20];
	Node nodeBuffer[] = new Node[20];
	int nodeCounter = 0, maxDepth = 0;
	boolean isDirectory = false;
	String initialDir = null;
	int initialNumSlash = 0;

	/**
	 * Constructor
	 */
	public Tar2Tree() {
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
		// Allocate tables
		// 
		tg.setNumTable(3);

		table = new Table();
		tg.setTable(1, table);
		table.setName("path");
		table.setType(1); // String
		table.setSize(tablesize);

		table = new Table();
		tg.setTable(2, table);
		table.setName("newness");
		table.setType(2); // Double
		table.setSize(tablesize);

		table = new Table();
		tg.setTable(3, table);
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
	 * 日付を表す整数値を算出する
	 * @param month 月
	 * @param day 日
	 * @param year 年
	 * @return 日付を表す整数値
	 */
	public double calcTotalDays(String month, String day, String year) {
		String monthArray[] =
			{
				"Jan",
				"Feb",
				"Mar",
				"Apr",
				"May",
				"Jun",
				"Jul",
				"Aug",
				"Sep",
				"Oct",
				"Nov",
				"Dec" };
		int monthDays[] =
			{ 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };
		double ret = 0.0;

		Integer iyear = new Integer(year);
		ret += (double) (iyear.intValue() - 1970) * 365.0;

		for (int i = 0; i < 12; i++) {
			if (month.compareTo(monthArray[i]) == 0) {
				ret += (double) monthDays[i];
				break;
			}
		}

		Integer iday = new Integer(day);
		ret += (double) iday.intValue();

		return ret;
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
		double ds,
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
		tn.setNumId(3);
		tn.setId(1, nodeCounter);
		tn.setId(2, nodeCounter);
		tn.setId(3, nodeCounter);

		TreeTable tg = tree.table;

		table = tg.getTable(1);
		table.set(nodeCounter, path);

		table = tg.getTable(2);
		table.set(nodeCounter, ds);

		table = tg.getTable(3);
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

				// Permission
				tokenBuffer.nextToken();

				// Owner
				tokenBuffer.nextToken();

				// Filesize
				String filesize = tokenBuffer.nextToken();

				// Year-Month-Day
				String ymd = tokenBuffer.nextToken();
				String year = ymd.substring(0, 4);
				String month = ymd.substring(5, 7);
				String day = ymd.substring(8, 10);

				// Time
				tokenBuffer.nextToken();

				// Filepath
				String path = tokenBuffer.nextToken();

				int depth = analyzePath(path);
				double totalDays = calcTotalDays(month, day, year);
				System.out.println(
					"path=" + path + " depth=" + depth + " days= " + totalDays);
				addNode(tree, path, filesize, totalDays, depth);
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
		Tar2Tree tn = new Tar2Tree();
		/*
		String tarFilename = "html2007.tar.txt";
		String treeFilename = "html2007.tree";
		String xmlFilename = "html2007.tree.xml";
		String ts = "2695";
		*/
		String tarFilename = "perl.tar.txt";
		String treeFilename = "perl.tree";
		String xmlFilename = "perl.tree.xml";
		String ts = "4456";
		
		
		if(args.length >= 3) {
			tarFilename = args[0];
			treeFilename = args[1];
			ts = args[2];
		}
		Integer tSize = new Integer(ts);
		int tablesize = tSize.intValue();

		Tree tree = new Tree();
		tn.initializeTree(tree, tablesize);
		tn.tarFileOpen(tarFilename, tree);
		tn.rearrangeTree(tree);

		TreeFileWriter output1 = new TreeFileWriter(new File(treeFilename), tree);
		output1.writeTree();
		
		XmlTreeFileWriter output2 = new XmlTreeFileWriter(new File(xmlFilename), tree);
		output2.writeTree();
	}

}
