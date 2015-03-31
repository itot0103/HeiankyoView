package org.heiankyoview2.datagen.access2tree;

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
 * Webサーバのアクセスログからtreeファイル形式で生成する
 */
public class Access2Tree {
	Vector pagelist = new Vector();
	
	/**
	 * 各ページのパスと回数を記録する
	 */
	class Page {
		String path;
		int count;
		
		public Page() {
			path = "";
			count = 1;
		}
	}
	

	/**
	 * Constructor
	 */
	public Access2Tree() {
	}
	
	
	public static Tree initializeTree() {
		Tree tree = new Tree();
		
		/*
		 * rootBranchの設定
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

	
	/**
	 * Treeを初期化する
	 * @param tree Tree
	 * @param tablesize 想定されるファイル数
	 */
	public void constructTable(Tree tree) {
		Table table;
		TreeTable tg = tree.table;

		/*
		 * 2個のTableを確保する
		 */
		tg.setNumTable(2);

		table = new Table();
		tg.setTable(1, table);
		table.setName("path");
		table.setType(table.TABLE_STRING);
		table.setSize(pagelist.size());

		table = new Table();
		tg.setTable(2, table);
		table.setName("count");
		table.setType(table.TABLE_INT); 
		table.setSize(pagelist.size());
		
		/*
		 * テーブルの個々のページについて
		 */
		for(int i = 1; i <= pagelist.size(); i++) {
			Page page = (Page)pagelist.elementAt(i - 1);
			table = tg.getTable(1);
			table.set(i, page.path);
			table = tg.getTable(2);
			table.set(i, page.count);
		}
		
	}

	
	/**
	 * アクセスしたページのパスを返す
	 * （ただし画像ファイル等だったらnullを返す）
	 * @param token
	 * @return
	 */
	public String extractPath(String token) {
		if(token.endsWith("gif")) return null;
		if(token.endsWith("jpg")) return null;
		if(token.endsWith("png")) return null;
		if(token.endsWith("css")) return null;
		if(token.endsWith("ico")) return null;
		if(token.endsWith("GIF")) return null;
		if(token.endsWith("JPG")) return null;
		if(token.endsWith("PNG")) return null;
		if(token.endsWith("CSS")) return null;
		if(token.endsWith("ICO")) return null;
		if(token.endsWith("/")) {
			token += "index.html";
		}
		
		return token;
	}
	

	/**
	 * すでに登録されているNodeと同一なものがあるか探し出す
	 * @param tree
	 * @param path
	 */
	public void searchNode(Tree tree, String path) {
		final int MAX_DEPTH = 20;
		
		/*
		 * パスを分割する
		 */
		String p[] = new String[MAX_DEPTH];
		StringTokenizer token = new StringTokenizer(path, "/");
		int depth = 0;
		for(int i = 0; i < MAX_DEPTH; i++) {
			p[i] = token.nextToken();
			if(token.countTokens() <= 0) {
				depth = i;  break;
			}
		}
		
		/*
		 * パスの各々の階層のディレクトリについて
		 */
		Branch branch = tree.getRootBranch();
		for(int i = 0; i < depth; i++) {
			
			/*
			 * 既にそのディレクトリがBranchとして存在するか探す
			 */
			Branch cbranch = null;
			for(int j = 0; j < branch.getNodeList().size(); j++) {
				Node node = (Node)branch.getNodeList().elementAt(j);
				cbranch = node.getChildBranch();
				if(cbranch == null) continue;
				if(cbranch.getName().compareTo(p[i]) == 0) break;
				else cbranch = null;
			}
			
			/*
			 * 新しいディレクトリであれば確保する
			 */
			if(cbranch == null) {
				cbranch = tree.getOneNewBranch();
				Node node = branch.getOneNewNode();
				node.setChildBranch(cbranch);
				cbranch.setParentNode(node);
				cbranch.setName(p[i]);
			}
			
			/*
			 * そのBranchを親Branchとして次の階層へ下がる
			 */
			branch = cbranch;
		}
		
		/*
		 * パスの最下位（＝ファイル）について
		 */
		Page page = null;
		for(int j = 0; j < branch.getNodeList().size(); j++) {
			Node node = (Node)branch.getNodeList().elementAt(j);
			if(node.getChildBranch() != null) continue;
			NodeTablePointer tn = node.table;
			int id = tn.getId(1);
			page = (Page)pagelist.elementAt(id - 1);
			if(path.compareTo(page.path) == 0) {
				page.count++;  break;
			}
			else {
				page = null;   continue;
			}
		}
		
		/*
		 * 初めて出現するページであれば確保する
		 */
		if(page == null) {
			Node newnode = branch.getOneNewNode();
			page = new Page();
			page.path = path;
			pagelist.add(page);
			NodeTablePointer tn = newnode.table;
			tn.setNumId(2);
			tn.setId(1, pagelist.size());
			tn.setId(2, pagelist.size());
		}
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
	 * logファイルを開く(1) ... ファイル名を抽出する
	 * @param tarFilename 入力tarファイル名
	 * @param tree Tree
	 */
	public void logFileOpen(String tarFilename, Tree tree) {
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
				while(tokenBuffer.countTokens() > 0) {
					String token = tokenBuffer.nextToken();
					if(token.endsWith("GET") || token.endsWith("POST"))
						break;
				}
				if(tokenBuffer.countTokens() <= 0) continue;
				String path = extractPath(tokenBuffer.nextToken());
				if(path == null || path.length() <= 0) continue;
				searchNode(tree, path);
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
		Access2Tree an = new Access2Tree();

		String logFilename = "C:/itot/projects/InfoVis/FRUITSNet/data/webaccess/log20090604.txt";
		String treeFilename = "access_log.tree";
		
		if(args.length > 0) logFilename = args[0];
		if(args.length > 1) treeFilename = args[1];
		
		Tree tree = initializeTree();

		an.logFileOpen(logFilename, tree);
		an.constructTable(tree);
		an.rearrangeTree(tree);
		
		TreeFileWriter output = new TreeFileWriter(new File(treeFilename), tree);
		output.writeTree();
	}

}
