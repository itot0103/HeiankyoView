package org.heiankyoview2.datagen.csv2tree;

import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.fileio.TreeFileWriter;

import java.io.*;
import java.util.*;


/**
 * CSVファイルを読み込んでTreeを構築する
 * @author itot
 */
public class CsvFileReader{
	Tree tree = null;
	int numlevel = 0;
	int numvalue = 0;
	String labels[] = null;
	
	File csvFile;
	BufferedReader reader;
	
	/**
	 * Constructor
	 * @param c2n Csv2Tree
	 */
	public CsvFileReader(File csvFile) {
		// ファイルを開く
		try {
			reader = new BufferedReader(new FileReader(csvFile));
		} catch (FileNotFoundException e) {
			System.err.println(e);
		}
	}
	
	
	/**
	 * 最初の1行で、階層の深さや、変数の次元数を読み取る
	 */
	void consumeFirstLine(StringTokenizer tokenBuffer) {
		int numtoken = tokenBuffer.countTokens();
		labels = new String[numtoken];
		Table table = null;
		int counter = 0;
		
		// トークンを1個ずつ解釈する
		while(tokenBuffer.countTokens() > 0) {
			String word = tokenBuffer.nextToken();
			labels[counter++] = word;
			
			// 階層の深さを更新する
			if(word.startsWith("Level")) {
				StringTokenizer tokenBuffer2 = new StringTokenizer(word, " ");
				tokenBuffer2.nextToken();
				String snum = tokenBuffer2.nextToken();
				int level = Integer.parseInt(snum);
				if(numlevel < level) numlevel = level;
			}
			
			// 変数の次元数を更新する
			if(word.startsWith("Value")) {
				StringTokenizer tokenBuffer2 = new StringTokenizer(word, " ");
				tokenBuffer2.nextToken();
				String snum = tokenBuffer2.nextToken();
				int value = Integer.parseInt(snum);
				if(numvalue < value) numvalue = value;
			}
		}
		
		// 値が不当であればtreeにnullを代入する
		if(numtoken != (numlevel + numvalue + 1)) {
			tree = null;  return;
		}
		if(numlevel <= 0 || numvalue <= 0) {
			tree = null;  return;
		}
		
		// Treeを確保する
		tree = new Tree();
		
		// 1個だけBranchを有する構造を構築する
		tree.setNumBranch(1);
		Branch rootBranch = (Branch) tree.getBranchList().elementAt(0);
		Node rootNode = tree.getRootNode();
		tree.setRootBranch(rootBranch);
		rootBranch.setParentNode(rootNode);
		rootNode.setChildBranch(rootBranch);
		
		// テーブルを用意する
		tree.table.setNumTable(numtoken);
		
		// Node名のテーブル
		table = new Table();
		table.setType(table.TABLE_STRING);
		table.setName("Name");
		tree.table.setTable(1, table);
		
		// 階層名のテーブル
		for(int i = 1; i <= numlevel; i++) {
			table = new Table();
			table.setType(table.TABLE_STRING);
			table.setName("Level " + i);
			tree.table.setTable((i + 1), table);
		}
		
		// 変数名のテーブル
		for(int i = 1; i <= numvalue; i++) {
			table = new Table();
			table.setType(table.TABLE_DOUBLE);
			table.setName("Value " + i);
			tree.table.setTable((i + numlevel + 1), table);
		}
		
	}
	
	
	/**
	 * これから登録すべきTableのIDを返す
	 */
	int getTableId(int counter) {
		String label = labels[counter];
		
		// 名前
		if(label.startsWith("Name") == true) return 1;
		
		// 階層の深さ
		if(label.startsWith("Level") == true) {
			StringTokenizer token = new StringTokenizer(label, " ");
			token.nextToken();
			String snum = token.nextToken();
			int level = Integer.parseInt(snum);
			return (1 + level);
		}
		
		// 変数の次元数
		if(label.startsWith("Value") == true) {
			StringTokenizer token = new StringTokenizer(label, " ");
			token.nextToken();
			String snum = token.nextToken();
			int value = Integer.parseInt(snum);
			return (1 + numlevel + value);
		}
		
		return -1;
	}
	

	/**
	 * これから登録するNodeからTableへのポインタのIDを返す
	 */
	int getNodePointerId(Table table, String word) {
		int id = 0;
		
		// 文字型
		if(table.getType() == table.TABLE_STRING) {
			
			// 同じ文字型が既に登録されているか検索する
			for(int i = 1; i <= table.getSize(); i++) {
				String w = table.getString(i);
				if(w.compareTo(word) == 0) {
					id = i;  break;
				}
			}
			
			// 登録されていなければ、新しく登録する
			if(id <= 0) {
				id = table.getSize() + 1;
				table.set(id, word);
			}
		}
		
		// 実数型
		if(table.getType() == table.TABLE_DOUBLE) {
			double value = Double.parseDouble(word);
			id = table.getSize() + 1;
			table.set(id, value);
		}
		
		// IDを返す
		return id;
	}
	
	
	/**
	 * Nodeを1個追加する
	 * @param tree
	 * @param values
	 */
	public void addOneNode(StringTokenizer tokenBuffer) {
		int counter = 0;
		
		// Nodeを1個確保する
		Branch branch = tree.getRootBranch();
		Node node = branch.getOneNewNode();
		node.setCurrentBranch(branch);
		node.table.setNumId(labels.length);
		
		// トークンを1個ずつ解釈する
		while(tokenBuffer.countTokens() > 0) {
			String word = tokenBuffer.nextToken();
			int tableId = getTableId(counter++);
			Table table = tree.table.getTable(tableId);
			int nid = getNodePointerId(table, word);
			node.table.setId(tableId, nid);
		}
		
	}

	
	/**
	 * Nodeを分割する
	 * @param branch
	 * @param level
	 */
	public void divideNodes(Branch branch, int level) {
		int tid = -1;
		Table table = null;
		Node nodearray[] = new Node[branch.getNodeList().size()];
		
		// Nodeのコピーをとり、リストからいったん削除する
		for(int i = 0; i < nodearray.length; i++) {
			Node node = (Node)branch.getNodeList().elementAt(0);
			nodearray[i] = node;
			branch.getNodeList().remove(node);
		}
		
		// レベルに対応するTableを探す
		for(int i = 1; i <= tree.table.getNumTable(); i++) {
			table = tree.table.getTable(i);
			String tname = table.getName();
			if(tname.startsWith("Level") == false) continue;
			StringTokenizer token = new StringTokenizer(tname," ");
			token.nextToken();
			String sl = token.nextToken();
			if(Integer.parseInt(sl) == level) {
				tid = i;    break;
			}
		}
		//System.out.println("   numtable=" + tree.table.getNumTable() + " tid=" + tid);
		if(tid < 0) return;
		
		// 元々Branch直下にあった各々のNodeについて
		for(int i = 0; i < nodearray.length; i++) {
			Node node = nodearray[i];
			int nodeid1 = node.table.getId(tid);
			String nname = table.getString(nodeid1);
			//System.out.println("  level=" + level + " i=" + i + " nname=" + nname);
			
			// 既に生成されている子Branchとの比較
			int j = 0;
			for(j = 0; j < branch.getNodeList().size(); j++) {
				Node cnode = (Node)branch.getNodeList().elementAt(j);
				Branch cbranch = cnode.getChildBranch();
				if(cbranch == null) continue;
				String bname = cbranch.getName();
				
				// 当該Nodeと子Branchが一致するようなら、子Branchに登録する
				if(bname.compareTo(nname) == 0) {
					cbranch.addOneNode(node);
					node.setCurrentBranch(cbranch);
					break;
				}
			}
			
			// 当該Nodeが子Branchに登録されないなら、新しい子Branchを生成する
			if(j >= branch.getNodeList().size()) {
				Node cnode = branch.getOneNewNode();
				Branch cbranch = tree.getOneNewBranch();
				cnode.setChildBranch(cbranch);
				cbranch.setParentNode(cnode);
				cbranch.addOneNode(node);
				cbranch.setName(nname);
				node.setCurrentBranch(cbranch);
				cbranch.setName(nname);
			}
			
		}
		
		
		// 新しい子Branchに対して同様な処理を再帰的に適用する
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			Branch cbranch = node.getChildBranch();
			if(cbranch != null && level < numlevel)
				divideNodes(cbranch, (level + 1));
		}
		
		
	}
	
	
	
	/**
	 * CSVファイルを開く
	 * @param csvFilename 入力CSVファイル名
	 * @return 生成されたTree
	 */
	public Tree getTree() {
		int count = -1;
		
		// ファイルを1行ずつ読む
		try {
			reader.ready();
			while (true) {
				String line = reader.readLine();
				//System.out.println(" line=" + line);
				if (line == null || line.length() <= 0)
					break;
				StringTokenizer tokenBuffer = new StringTokenizer(line, ",");

				// 最初の1行で、階層の深さや、変数の次元数を読み取る
				if(count < 0) {
					consumeFirstLine(tokenBuffer);
					if(tree == null) return null;
				}
				
				// 2行目以降で、ノードを1個ずつ追加する
				else
					addOneNode(tokenBuffer);
				
				// 行数をカウントする
				count++;
			}
			
			// ファイルを閉じる
			reader.close();

		} catch (IOException e) {
			System.err.println(e);
			return null;
		}

		// 再帰的にNodeを分割する
		divideNodes(tree.getRootBranch(), 1);
		
		// NodeにIDを割り振り直す
		for(int j = 0; j < tree.getBranchList().size(); j++) {
			Branch branch = (Branch)tree.getBranchList().elementAt(j);
		
			for(int i = 0; i < branch.getNodeList().size(); i++) {
				Node node = (Node)branch.getNodeList().elementAt(i);
				node.setCurrentBranch(branch);
				node.setId(i + 1);
				//System.out.println("   branchID=" + branch.getId() + " i=" + i + " nodeId=" + node.getId());
			}
		}
		
		// Treeを返す
		return tree;
	}

	/**
	 * main関数
	 * @param args 引数
	 */
	public static void main(String[] args) {
		
		// ファイル名を特定する
		String csvFilename = "data/csv/sample20081210.csv";
		String treeFilename = "tmp.tree";
		if(args.length >= 2) {
			csvFilename = args[0];
			treeFilename = args[1];
		}
		
		// ファイルを読む
		CsvFileReader fcfile = new CsvFileReader(new File(csvFilename));
		Tree tree = fcfile.getTree();
		
		// 変換結果を別ファイルに書く
		TreeFileWriter output = new TreeFileWriter(new File(treeFilename), tree);
		output.writeTree();
	}
}