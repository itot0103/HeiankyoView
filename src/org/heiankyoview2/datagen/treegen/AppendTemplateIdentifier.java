package org.heiankyoview2.datagen.treegen;

import org.heiankyoview2.core.tree.*;
import org.heiankyoview2.core.table.*;
import org.heiankyoview2.core.fileio.TreeFileReader;
import org.heiankyoview2.core.fileio.TreeFileWriter;

import java.io.*;

public class AppendTemplateIdentifier {
	static int totalNum = 0;
	static int counter = 0;
	static Tree tree = null;
	
	
	/**
	 * NodeとBranchの総数をカウントする
	 */
	static void countTotalNum(Branch branch) {

		// 当該Branchに対して1を加算する
		totalNum++;
		
		// 各々のNodeに対して
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() != null)
				countTotalNum(node.getChildBranch());
			else
				totalNum++;
		}
		
	}
	
	
	/**
	 * 通し番号のためにTableを1個追加する
	 */
	static void addTable() {
		Table itable = new Table();
		
		// Tableを1個増やす
		TreeTable tt = tree.table;
		int numt = tt.getNumTable();
		Table tarray[] = new Table[numt];
		for(int i = 1; i <= numt; i++)
			tarray[i - 1] = tt.getTable(i);
		tt.setNumTable(numt + 1);
		tt.setTable(1, itable);
		for(int i = 0; i < numt; i++)
			tt.setTable((i + 2), tarray[i]);
		
		// 通し番号のテーブルを構築する
		itable.setName("identifier");
		itable.setType(itable.TABLE_STRING);
		itable.setSize(totalNum);
		for(int i = 1; i < totalNum; i++)
			itable.set(i, Integer.toString(i));
		
	}
	
	/**
	 * 1個のNodeに通し番号を割り振る
	 */
	static void assignIdentifierOneNode(Node node) {
		
		// NodeTablePointerを1個増やす
		NodeTablePointer ntp = node.table;
		int numt = ntp.getNumId();
		int tarray[] = new int[numt];
		for(int i = 1; i <= numt; i++)
			tarray[i - 1] = ntp.getId(i);
		ntp.setNumId(numt + 1);
		ntp.setId(1, ++counter);
		for(int i = 0; i < numt; i++)
			ntp.setId((i + 2), tarray[i]);
	}
	
	
	/**
	 * NodeとBranchに通し番号を割り振る
	 */
	static void assignIdentifier(Branch branch) {
		
		// このBranch自身に通し番号を割り振る
		Node pnode = branch.getParentNode();
		assignIdentifierOneNode(pnode);
		
		// 各々のNodeについて
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() != null)
				assignIdentifier(node.getChildBranch());
			else
				assignIdentifierOneNode(node);
		}
		
	}
	
	
	/**
	 * main関数
	 */
	public static void main(String[] args) {
		
		String filename1 = "C:/itot/projects/HeiankyoView2/data/template/jiali-min10max100_4.tree"; // treeファイル名
		String filename2 = "C:/itot/projects/HeiankyoView2/data/template/jiali-min10max100_4t.tree";
		
		// treeファイルを読む
		TreeFileReader tfr = new TreeFileReader(new File(filename1));
		tree = tfr.getTree();
		
		// NodeとBranchの総数をカウントする
		totalNum = 1;
		countTotalNum(tree.getRootBranch());
		
		// Tableを1個確保する
		addTable();
		
		// NodeとBranchに通し番号をつける
		counter = 0;
		assignIdentifier(tree.getRootBranch());
		
		// treeファイルを書く
		TreeFileWriter tnf = new TreeFileWriter(new File(filename2), tree);
		tnf.writeTree();
		
	}
}
