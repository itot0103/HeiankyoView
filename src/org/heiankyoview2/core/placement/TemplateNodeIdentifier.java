package org.heiankyoview2.core.placement;

import org.heiankyoview2.core.table.*;
import org.heiankyoview2.core.tree.*;
import java.util.*;

/**
 * TreeとTemplateの間で対応するNodeやBranchを特定する
 * 注：Tableの1番目に、NodeやBranchを特定する名前が設定されていること
 */
public class TemplateNodeIdentifier {
	static final int NUMKEY = 101;
	static Vector namearray[];
	static Tree templateTree = null;
	
	
	/**
	 * テンプレートとなる木構造を設定する
	 * @param templateTree
	 */
	static public void setTemplateTree(Tree ttree) {
		templateTree = ttree;
		if(templateTree == null) return;
		Table table = templateTree.table.getTable(1);
		
		// メモリ領域の初期化
		namearray = new Vector[NUMKEY];
		for(int i = 0; i < NUMKEY; i++) 
			namearray[i] = new Vector();
		
		// 各々のBranchについて
		for(int i = 0; i < templateTree.getBranchList().size(); i++) {
			Branch branch = (Branch)templateTree.getBranchList().elementAt(i);
			
			// Branchの名前を格納する
			int id = branch.getParentNode().table.getId(1);
			if(id > 0) {
				String name = table.getString(id);
				int hvalue = name.hashCode() % NUMKEY;
				if(hvalue < 0) hvalue += NUMKEY;
				TemplateName tn = new TemplateName(name, branch.getParentNode(), branch);
				namearray[hvalue].add(tn);
			}
			
			// 各々のNodeについて
			for(int j = 0; j < branch.getNodeList().size(); j++) {
				Node node = (Node)branch.getNodeList().elementAt(j);
				if(node.getChildBranch() != null) continue;
				
				// Nodeの名前を格納する
				id = node.table.getId(1);
				if(id > 0) {
					String name = table.getString(id);
					int hvalue = name.hashCode() % NUMKEY;
					if(hvalue < 0) hvalue += NUMKEY;
					TemplateName tn = new TemplateName(name, node, null);
					namearray[hvalue].add(tn);
				}
			}
		}
		
	}
	
	
	/**
	 * 引数nodeに対応するテンプレート上のNodeを返す
	 * @param tree
	 * @param node
	 * @return
	 */
	static Node findTemplateNode(Tree tree, Node node) {
		if(templateTree == null) return null;
		
		// 引数nodeの名前のハッシュキーを算出する
		String name = getNodeIdentifier(tree, node);
		if (name == null
				|| name.length() <= 0
				|| name.startsWith("null"))
				return null;
		int hvalue = name.hashCode() % NUMKEY;
		if(hvalue < 0) hvalue += NUMKEY;
			
		// 当該ハッシュキーに登録された名前を検索する
		for(int i = 0; i < namearray[hvalue].size(); i++) {
			TemplateName tn = (TemplateName)namearray[hvalue].elementAt(i);
			if(tn.name.compareTo(name) == 0) 
				return tn.node;
		}
		
		// テンプレート上のNodeが見つからなければnullを返す
		return null;
	}
	
	
	static Branch findTemplateBranch(Tree tree, Branch branch) {
		if(templateTree == null) return null;

		// 引数nodeの名前のハッシュキーを算出する
		String name = getBranchIdentifier(tree, branch);
		if (name == null
				|| name.length() <= 0
				|| name.startsWith("null"))
				return null;
		int hvalue = name.hashCode() % NUMKEY;
		if(hvalue < 0) hvalue += NUMKEY;

		// 当該ハッシュキーに登録された名前を検索する
		for(int i = 0; i < namearray[hvalue].size(); i++) {
			TemplateName tn = (TemplateName)namearray[hvalue].elementAt(i);
			if(tn.name.compareTo(name) == 0) {
				if(tn.branch == null) continue;
				return tn.branch;
			}
		}
		
		// テンプレート上のBranchが見つからなければnullを返す
		return null;

	}
	
	/**
	 * Branchの識別子となる名前を返す
	 * @param tree
	 * @param branch
	 * @return
	 */
	static String getBranchIdentifier(Tree tree, Branch branch) {
		String name = branch.getName();
		if (name != null && name.length() > 0 && !name.startsWith("null"))
			return name;
		
		if (tree.table.getNameType() < 0)
			tree.table.setNameType(0);

		Node parentNode = branch.getParentNode();
		name = tree.table.getNodeAttributeName(
				parentNode, tree.table.getNameType());
		return name;

	}
	
	/**
	 * Nodeの識別子となる名前を返す
	 * @param tree
	 * @param node
	 * @return
	 */
	static String getNodeIdentifier(Tree tree, Node node) {
		String name = "";

		if (tree.table.getNameType() < 0)
			tree.table.setNameType(0);

		name = tree.table.getNodeAttributeName(
				node, tree.table.getNameType());
		
		return name;
	}
	
	
	/**
	 * 1個の名前に関わるNodeとBranchを格納する
	 */
	static class TemplateName {
		String name;
		Node node;
		Branch branch;
		
		TemplateName() {
			name = null;  node = null; branch = null;
		}
		
		TemplateName(String nm, Node n, Branch b) {
			name = nm;  node = n; branch = b;
		}
	}
	
	
}
