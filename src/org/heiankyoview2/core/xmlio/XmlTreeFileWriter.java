package org.heiankyoview2.core.xmlio;

import java.io.File;

import org.heiankyoview2.core.tree.*;
import org.heiankyoview2.core.table.*;

public class XmlTreeFileWriter {

	/* var */
	Tree tree = null;
	XmlFileOutput output = null;
	int numBranch;
	String dummy = " ";

	/**
	 * Constructor
	 * @param outputFile 出力先XMLファイル
	 * @param tree Tree
	 */
	public XmlTreeFileWriter(File outputFile, Tree tree) {

		output = new XmlFileOutput(outputFile);
		this.tree = tree;
	}

	/**
	 * XMLファイルを書き出す
	 * @return 成功すればtrue
	 */
	public boolean writeTree() {
		boolean ret = true;
		
		if (tree == null) {
			output.close();
			return false;
		}
		
		/*
		 * Treeタグの開始
		 */
		output.println("<tree>");
		
		/*
		 * Tablesタグ
		 */
		if((ret = writeTables()) == false) {
			output.close();
			return false;
		}
		
		/*
		 * Branchタグ
		 */
		if((ret = writeBranches()) == false) {
			output.close();
			return false;
		}
		
		/*
		 * Treeタグの終了
		 */
		output.println("</tree>");
		
		output.close();
		return ret;
	}
	
	
	/**
	 * XMLファイルにTableの全情報を書き出す
	 * @return 成功すればtrue
	 */
	boolean writeTables() {
		boolean ret = true;
		TreeTable tg = tree.table;
		
		/*
		 * Tablesタグの開始
		 */
		output.println(" <tables>");

		/*
		 * 各Tableごとに
		 */
		for(int i = 1; i <= tg.getNumTable(); i++) {
			Table table = tg.getTable(i);
			if((ret = writeOneTable(table)) == false) {
				output.close();
				return ret;
			}
		}
		
		/*
		 * Tablesタグの終了
		 */
		output.println(" </tables>");
		return ret;
	}
	
	/**
	 * XMLファイルにBranchの全情報を書き出す
	 * @return 成功すればtrue
	 */
	boolean writeBranches() {
		boolean ret = true;
		TreeTable tg = tree.table;
		
		/*
		 * 各Branchごとに
		 */
		Branch rootBranch = tree.getRootBranch();
		if((ret = writeOneBranch(rootBranch)) == false) {
			output.close();
			return ret;
		}
		
		return ret;
	}
	
	/**
	 * XMLファイルに1個のTableの情報を書き出す
	 * @return 成功すればtrue
	 */
	boolean writeOneTable(Table table) {
		boolean ret = true;
		String name = table.getName();
		String type = "";
		if(table.getType() == table.TABLE_STRING) type = "string";
		if(table.getType() == table.TABLE_DOUBLE) type = "double";
		if(table.getType() == table.TABLE_INT)    type = "int";
		if(table.getType() == table.TABLE_DATE)   type = "date";
		
		/*
		 * Tableタグの開始
		 */
		output.println("  <table name=\"" + name + "\" type=\"" + type + "\" >");
		
		/*
		 * Tableを構成する各行ごとに
		 */
		for(int i = 1; i <= table.getSize(); i++) {
			String value = "";
			if(table.getType() == table.TABLE_STRING)
				value = table.getString(i);
			if(table.getType() == table.TABLE_DOUBLE)
				value = Double.toString(table.getDouble(i));
			if(table.getType() == table.TABLE_INT)
				value = Integer.toString(table.getInt(i));
			output.println("   <tableline id=\"" + i + "\" value=\"" + value + "\" />");
		}
		
		/*
		 * Tableタグの終了
		 */
		output.println("  </table>");
		return ret;
	}
	
	/**
	 * XMLファイルに1個のBranchの情報を書き出す
	 * @return 成功すればtrue
	 */
	boolean writeOneBranch(Branch branch) {
		boolean ret = true;
		int indent = branch.getLevel() * 2;
		
		/*
		 * Branchタグの開始
		 */
		for(int i = 1; i <= indent; i++)
			output.print(" ");
		output.println("<branch>");
		
		/*
		 * 配置済みであれば、その情報を記述する
		 */
		Node pnode = branch.getParentNode();
		if(pnode.getPlaced() == true) {
			for(int i = 1; i <= (indent + 1); i++)
				output.print(" ");
			String sizetext =
				"<branchsize width="
				+ pnode.getWidth()
				+ " height="
				+ pnode.getHeight()
				+ " depth="
				+ pnode.getDepth()
				+ ">";
			output.println(sizetext);
			for(int i = 1; i <= (indent + 1); i++)
				output.print(" ");
			String postext =
				"<branchpos x="
				+ pnode.getX()
				+ " y="
				+ pnode.getY()
				+ " z="
				+ pnode.getZ()
				+ ">";
			output.println(postext);
		}
		
		/*
		 * BranchのTablePointer
		 */
		writeTablePointer(branch.getParentNode(), (indent + 1), true);
		
		/*
		 * 各Nodeごとに
		 */
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() != null) continue;
			for(int j = 1; j <= (indent + 1); j++)
				output.print(" ");
			output.println("<node>");
			
			/*
			 * 配置済みであれば、その情報を記述する
			 */
			if(node.getPlaced() == true) {
				for(int j = 1; j <= (indent + 2); j++)
					output.print(" ");
				String sizetext =
					"<nodesize width="
					+ node.getWidth()
					+ " height="
					+ node.getHeight()
					+ " depth="
					+ node.getDepth()
					+ ">";
				output.println(sizetext);
				for(int j = 1; j <= (indent + 2); j++)
					output.print(" ");
				String postext =
					"<nodepos x="
					+ node.getX()
					+ " y="
					+ node.getY()
					+ " z="
					+ node.getZ()
					+ ">";
				output.println(postext);
			}
			
			writeTablePointer(node, (indent + 2), true);
			for(int j = 1; j <= (indent + 1); j++)
				output.print(" ");
			output.println("</node>");
		}
		
		/*
		 * 各Child Branchごとに
		 */
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() == null) continue;
			writeOneBranch(node.getChildBranch());
		}
		
		/*
		 * Branchタグの終了
		 */
		for(int i = 1; i <= indent; i++)
			output.print(" ");
		output.println("</branch>");

		return ret;
	}
	

	/**
	 * XMLファイルに1個のNode（またはBranch）のTablePointer情報を書き込む
	 */
	void writeTablePointer(Node node, int indent, boolean branchFlag) {
		NodeTablePointer tn = node.table;
		TreeTable tg = tree.table;
		for(int i = 1; i <= tn.getNumId(); i++) {
			Table table = tg.getTable(i);
			String name = table.getName();
			int id = tn.getId(i);
			for(int j = 1; j <= indent; j++)
				output.print(" ");
			output.println("<tablepointer tablename=\"" + name + "\" tablelineid=\"" + id + "\" />");
		}
		
	}
	
}
