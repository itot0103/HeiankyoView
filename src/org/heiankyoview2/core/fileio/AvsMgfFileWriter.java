
package org.heiankyoview2.core.fileio;

import java.io.File;
import java.util.Vector;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;

public class AvsMgfFileWriter {

	/* var */
	Tree tree = null;
	FileOutput output = null;

	
	/**
	 * Constructor
	 * @param outputFile 出力先treeファイル
	 * @param tree Tree
	 */
	public AvsMgfFileWriter(File outputFile, Tree tree) {

		output = new FileOutput(outputFile);
		this.tree = tree;
	}

	/**
	 * fldファイルを書き出す
	 * @return 成功すればtrue
	 */
	public boolean writeData() {
		boolean ret = true;
		
		
		/**
		 * ヘッダ情報の書き出し
		 */
		writeHeader();
		
		/**
		 * rootBranch からデータ書き出しを出発
		 */
		writeDataOneBranch(tree.getRootBranch());
		
		return true;
	}
	
	
	/**
	 * ヘッダ情報をファイルに書き込む
	 */
	void writeHeader() {
		output.println("# Micro AVS Geom:2.10");
	}
	
	/*
	 * 1グループに所属するノードの情報を書き出し
	 */
	void writeDataOneBranch(Branch branch){

		/*
		 * 当該ノードの情報を書き込む
		 */
		output.println("polyline");
		output.println("pline_sample");
		output.println("vertex");
		output.println("5");
		
		Node parentNode = branch.getParentNode();
		double xmax = parentNode.getX() + parentNode.getWidth();
		double xmin = parentNode.getX() - parentNode.getWidth();
		double ymax = parentNode.getY() + parentNode.getHeight();
		double ymin = parentNode.getY() - parentNode.getHeight();
		
		output.println(xmax + " " + ymax + " 0.0");
		output.println(xmin + " " + ymax + " 0.0");
		output.println(xmin + " " + ymin + " 0.0");
		output.println(xmax + " " + ymin + " 0.0");
		output.println(xmax + " " + ymax + " 0.0");
		output.println("");
		
		/**
		 * 各ノードごとに
		 */
		Vector nodelist = branch.getNodeList();
		for(int i = 0; i < nodelist.size(); i++) {
			Node node = (Node)nodelist.elementAt(i);
			
			/*
			 * ノードに子グループがなければ省略
			 */
			if(node.getChildBranch() == null) continue;
			writeDataOneBranch(node.getChildBranch());
		}
	}
	
}
