package org.heiankyoview2.core.xmlio;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.table.Table;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;


/**
 * xmlファイルを読み込む
 * 
 * @author itot
 */
public class XmlTreeFileReader {

	File xmlFile;
	Document doc;
	Tree tree;
	
	/**
	 * Constructor
	 * 
	 * @param inputFile
	 *            読み込みファイル
	 */
	public XmlTreeFileReader(File inputFile) {

		try {
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			// パースを実行してDocumentオブジェクトを取得
			doc = builder.parse(inputFile);
			// ルート要素を取得（タグ名：message）

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * DOM構造を読み込み、生成したTreeを返す
	 * @return 生成したTree
	 */
	public Tree getTree() {
		tree = new Tree(); //create tree
		readFile(tree);
		return tree;
	}
	
	
	/**
	 * DOM構造をparseして、Treeを構成するデータを構築する
	 * @param tree Tree
	 * @return 成功すればtrue
	 */
	public boolean readFile(Tree tree) {
	
		/*
		 * Documentの先頭ノードを得る
		 */
		org.w3c.dom.Node  treeNode = doc.getFirstChild();
		String treeName = treeNode.getNodeName();
		if(treeName.compareTo("tree") != 0) return false;
		
		/*
		 * treeノードからtablesノードとbranchノードを得る
		 */
		org.w3c.dom.NodeList  nodelist = treeNode.getChildNodes();
		org.w3c.dom.Node  tablesNode = null, rbranchNode = null;
		for(int i = 0; i < nodelist.getLength(); i++) {
			org.w3c.dom.Node node = nodelist.item(i);
			if(node.getNodeName().compareTo("tables") == 0)
				tablesNode = node;
			if(node.getNodeName().compareTo("branch") == 0)
				rbranchNode = node;
		}
		if(tablesNode == null || rbranchNode == null)
				return false;
		
		/*
		 * tablesノードの処理
		 */
		allocateTables(tree, tablesNode);
		
		/*
		 * branchノードの処理
		 */
		Branch branch = tree.getOneNewBranch();
		tree.setRootBranch(branch);
		allocateBranch(tree, rbranchNode);
		
		return true;
	}

	
	/**
	 * テーブルを確保する
	 * @param tree Tree
	 * @param tablesNode XML文書中のtablesタグに対応するノード
	 */
	void allocateTables(Tree tree, org.w3c.dom.Node tablesNode) {
		TreeTable tg = tree.table;
		
		/*
		 * テーブルの個数を確定する
		 */
		int numTables = 0;
		org.w3c.dom.NodeList  nodelist = tablesNode.getChildNodes();
		for(int i = 0; i < nodelist.getLength(); i++) {
			org.w3c.dom.Node node = nodelist.item(i);
			if(node.getNodeName() == null ||
			   node.getNodeName().compareTo("table") != 0) continue;
			numTables++;
		}
		tg.setNumTable(numTables);
		
		/*
		 * 各テーブルごとに
		 */
		int tid = 1;
		for(int i = 0; i < nodelist.getLength(); i++) {
			org.w3c.dom.Node node = nodelist.item(i);
			if(node.getNodeName() == null ||
			   node.getNodeName().compareTo("table") != 0) continue;
			Table table = new Table();
			tg.setTable(tid++, table);
			
			
			/*
			 * テーブルの名前
			 */
			org.w3c.dom.Element element = (org.w3c.dom.Element)node;
			String tablename = element.getAttribute("name");
			table.setName(tablename);
			
			/*
			 * テーブルの型
			 */
			String tabletype = element.getAttribute("type");
			if(tabletype.compareTo("string") == 0) 
				table.setType(table.TABLE_STRING);
			if(tabletype.compareTo("double") == 0) 
				table.setType(table.TABLE_DOUBLE);
			if(tabletype.compareTo("int") == 0) 
				table.setType(table.TABLE_INT);
			
			/*
			 * テーブルの要素数
			 */
			org.w3c.dom.NodeList nodelist2 = node.getChildNodes();
			int numelements = 0;
			for(int j = 0; j < nodelist2.getLength(); j++) {
				org.w3c.dom.Node node2 = nodelist2.item(j);
				if(node2.getNodeName().compareTo("tableline") != 0) continue;
				numelements++;
			}
			table.setSize(numelements);
			
			/*
			 * テーブルの各要素
			 */
			int eid = 1;
			for(int j = 0; j < nodelist2.getLength(); j++) {
				org.w3c.dom.Node node2 = nodelist2.item(j);
				if(node2.getNodeName().compareTo("tableline") != 0) continue;
				org.w3c.dom.Element element2 = (org.w3c.dom.Element)node2;
				String value = element2.getAttribute("value");
					
				if (table.getType() == table.TABLE_STRING) { // String
					table.set(eid++, value);
				}
				if (table.getType() == table.TABLE_DOUBLE) { // Double
					table.set(eid++, Double.parseDouble(value));
				}
				if (table.getType() == table.TABLE_INT) { // Int
					table.set(eid++, Integer.parseInt(value));
				}
			}

		}
		
	}
	

	/**
	 * グループを確保する
	 * @param tree Tree
	 * @param tablesNode XML文書中のtablesタグに対応するノード
	 */
	void allocateBranch(Tree tree, org.w3c.dom.Node branchNode) {
		int numbranchs = tree.getBranchList().size();
		Branch branch = (Branch)tree.getBranchList().elementAt(numbranchs - 1);
		org.w3c.dom.NodeList  xmlnodelist = branchNode.getChildNodes();
		
		/*
		 * 各々のXMLノードについて
		 */
		for(int i = 0; i < xmlnodelist.getLength(); i++) {
			org.w3c.dom.Node xmlnode = xmlnodelist.item(i);
			
			/*
			 * BranchからTableへのポインタ
			 */
			if(xmlnode.getNodeName().compareTo("tablepointer") == 0) {
				Node node = branch.getParentNode();
				setTablePointer(tree, node, xmlnode);
			}
			
			/*
			 * Node
			 */
			if(xmlnode.getNodeName().compareTo("node") == 0) {
				Node node = branch.getOneNewNode();
				node.setChildBranch(null);
				setOneNode(tree, node, xmlnode);
			}
			
			/*
			 * Branch
			 */
			if(xmlnode.getNodeName().compareTo("branch") == 0) {
				Node node = branch.getOneNewNode();
				Branch cbranch = tree.getOneNewBranch();
				cbranch.setLevel(branch.getLevel() + 1);
				node.setChildBranch(cbranch);
				cbranch.setParentNode(node);
				allocateBranch(tree, xmlnode);
			}
			
		}
		
	}
	
	
	/**
	 * グループを確保する
	 * @param tree Tree
	 * @param node Node
	 * @param xmlnode XML文書中のnodeタグに対応するノード
	 */
	void setOneNode(Tree tree, Node node, org.w3c.dom.Node xmlnode) {
		org.w3c.dom.NodeList  xmlnodelist = xmlnode.getChildNodes();
		
		/*
		 * 各々のXMLノードについて
		 */
		for(int i = 0; i < xmlnodelist.getLength(); i++) {
			org.w3c.dom.Node xmlnode2 = xmlnodelist.item(i);
			if(xmlnode2.getNodeName().compareTo("tablepointer") != 0) 
				continue;
			setTablePointer(tree, node, xmlnode2);
		}
	}
	
	
	/**
	 * テーブルポインタを設定する
	 * @param tree Tree
	 * @param node Node
	 * @param xmlnode XML文書中のnodeタグに対応するノード
	 */
	void setTablePointer(Tree tree, Node node, org.w3c.dom.Node xmlnode) {
		TreeTable tg = tree.table;
		NodeTablePointer tn = node.table;
		tn.setNumId(tg.getNumTable());
		
		org.w3c.dom.Element element = (org.w3c.dom.Element)xmlnode;
		String tablename = element.getAttribute("tablename");
		String lineid = element.getAttribute("tablelineid");
		
		/*
		 * テーブルポインタに書かれた名前に対応するTableを特定する
		 */
		Table table = null;  int ii = -1;
		for(int i = 1; i <= tg.getNumTable(); i++) {
			Table t = tg.getTable(i);
			if(t.getName().compareTo(tablename) == 0) {
				table = t;  ii = i;  break;
			}
		}
		if(table == null) return;
		
		/*
		 * テープルポインタをセットする
		 */
		int id = Integer.parseInt(lineid);
		tn.setId(ii, id);
		
	}

}