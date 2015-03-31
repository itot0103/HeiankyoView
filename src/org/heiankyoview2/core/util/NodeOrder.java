package org.heiankyoview2.core.util;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;


import org.heiankyoview2.core.table.*;
import org.heiankyoview2.core.tree.*;

/*	
 * ノードの並び替えを行う。
 */
public class NodeOrder {
	
	StringComparator  scomp = null;
	DoubleComparator  dcomp = null; 
	IntegerComparator icomp = null;
	
	
	//	コンストラクタ
	public void order(int attribute, Tree tree){
		
		// 属性IDが正しく設定されていることを確認する
		if(attribute <= 0) return;
		Table table = tree.table.getTable(attribute);
		int tabletype = table.getType();
		
	
		// ソートのために必要なツールを初期化する
		if(tabletype == table.TABLE_STRING) 
			scomp = new StringComparator();
		if(tabletype == table.TABLE_DOUBLE) 
			dcomp = new DoubleComparator();
		if(tabletype == table.TABLE_INT) 
			icomp = new IntegerComparator();
		TreeSet stree = null; 
		TreeSet dtree = null;
		TreeSet itree = null;
		
		
		// 各々のBranchについて
		for(int i = 0; i < tree.getBranchList().size(); i++) {
			Branch branch = (Branch)tree.getBranchList().elementAt(i);
			
			// 当該Branchがソート対象であるか否かを確認する
			boolean isLeafOnly = true;
			for(int j = 0; j < branch.getNodeList().size(); j++) {
				Node node = (Node)branch.getNodeList().elementAt(j);
				if(node.getChildBranch() != null) {
					isLeafOnly = false;  break;
				}
			}
			if(isLeafOnly == false) continue;
			
			if(tabletype == table.TABLE_STRING) 
				stree = new TreeSet(scomp);
			if(tabletype == table.TABLE_DOUBLE) 
				dtree = new TreeSet(dcomp);
			if(tabletype == table.TABLE_INT) 
				itree = new TreeSet(icomp);
			
			//	NodeをTreeSetに登録する
			Vector nodeList = branch.getNodeList();
			int numnodes = nodeList.size();
			int tsize = 0;
			for(int j = 0; j < nodeList.size(); j ++){	//	各ノードについて
				Node node = (Node) nodeList.elementAt(j);
				int tid = node.table.getId(attribute);
				NodeValue nv = new NodeValue();
				nv.id = j;
				nv.node = node;
				
				if (tabletype == table.TABLE_STRING) { /* STRING */    
					nv.ivalue = tid;
					stree.add((Object)nv);
					tsize = stree.size();
				}
				if (tabletype == table.TABLE_DOUBLE) { /* DOUBLE */
					nv.dvalue = table.getDouble(tid);
					dtree.add((Object)nv);
					tsize = dtree.size();
				}
				if (tabletype == table.TABLE_INT) { /* INT */
					nv.ivalue = table.getInt(tid);
					itree.add((Object)nv);
					tsize = itree.size();
				}
			}
			
			// Nodeのリストをいったんクリアする
			nodeList.clear();
			
			// NodeをTreeSetから取り出す
			for (int j = 1; j <= numnodes; j++) {
				Object obj = null;
				if (tabletype == table.TABLE_STRING) { /* STRING */    
					obj = stree.first();
					stree.remove(obj);
					tsize = stree.size();
				}
				if (tabletype == table.TABLE_DOUBLE) { /* DOUBLE */
					obj = dtree.first();
					dtree.remove(obj);
					tsize = dtree.size();
				}
				if (tabletype == table.TABLE_INT) { /* INT */
					obj = itree.first();
					itree.remove(obj);
					tsize = itree.size();
				}
				NodeValue nv = (NodeValue)obj;
				Node node = nv.node;
				node.setId(j);
				nodeList.add(node);
			}
		}
	}
			
	
	
	/**
	 * 一時的にNodeと値を格納するクラス
	 */
	class NodeValue {
		int id;
		Node node;
		int ivalue;
		double dvalue;
	}
	
	
	/**
	 * 文字列の前後関係を管理するクラス
	 * @author itot
	 */
	class StringComparator implements Comparator {

		/**
		 * Constructor
		 */
		public StringComparator() {
		}

		/**
		 * 2つのNodeの深さを比較する
		 * @param obj1 1個目のNode
		 * @param obj2 2個目のNode
		 */
		public int compare(Object obj1, Object obj2) {

			NodeValue nv1 = (NodeValue)obj1;
			NodeValue nv2 = (NodeValue)obj2;
			
			// 大小関係を返す
			int sub = nv1.ivalue - nv2.ivalue;
			if(sub > 0) return 1;
			if(sub < 0) return -1;
			
			if(nv1.id > nv2.id) return 1;
			if(nv1.id < nv2.id) return -1;
			
			return 0;
		}

	}
	
	/**
	 * 実数の大小関係を管理するクラス
	 * @author itot
	 */
	class DoubleComparator implements Comparator {

		/**
		 * Constructor
		 */
		public DoubleComparator() {
		}

		/**
		 * 2つのNodeの深さを比較する
		 * @param obj1 1個目のNode
		 * @param obj2 2個目のNode
		 */
		public int compare(Object obj1, Object obj2) {

			NodeValue nv1 = (NodeValue)obj1;
			NodeValue nv2 = (NodeValue)obj2;
			
			// 大小関係を返す
			double sub = nv1.dvalue - nv2.dvalue;
			if(sub > 1.0e-5) return 1;
			if(sub < -1.0e-5) return -1;
			
			if(nv1.id > nv2.id) return 1;
			if(nv1.id < nv2.id) return -1;
			return 0;
		}

	}
	
	
	/**
	 * 整数の大小関係を管理するクラス
	 * @author itot
	 */
	class IntegerComparator implements Comparator {

		/**
		 * Constructor
		 */
		public IntegerComparator() {
		}

		/**
		 * 2つのNodeの深さを比較する
		 * @param obj1 1個目のNode
		 * @param obj2 2個目のNode
		 */
		public int compare(Object obj1, Object obj2) {
			
			NodeValue nv1 = (NodeValue)obj1;
			NodeValue nv2 = (NodeValue)obj2;
			
			// 大小関係を返す
			int sub = nv1.ivalue - nv2.ivalue;
			if(sub > 0) return 1;
			if(sub < 0) return -1;
			
			if(nv1.id > nv2.id) return 1;
			if(nv1.id < nv2.id) return -1;
			return 0;
		}

	}
}
