package org.heiankyoview2.core.util;

import org.heiankyoview2.core.table.*;
import org.heiankyoview2.core.tree.*;

public class RearrangeBranch {

	/**
	 * groupIdのTableの値でGroupを再構成する
	 * @param tree
	 * @param groupId
	 */
	public static void rearrange(Tree tree, int groupId) {
		cancelAllBranch(tree);
		generateBranch(tree, groupId);
	}
	
	
	/**
	 * Branchに属する全てのNodeを、rootBranch直下に移動し、子Branchを全て削除する
	 * @param tree
	 */
	public static void cancelAllBranch(Tree tree) {
		cancelOneBranch(tree, tree.getRootBranch());
	}

	
	/**
	 * 1個のBranchに属する全てのNodeを、rootBranch直下に移動し、子Branchを全て削除する
	 * @param tree
	 * @param branch
	 */
	static void cancelOneBranch(Tree tree, Branch branch) {
		Branch rootBranch = tree.getRootBranch();
		
		// Nodeを一時的に配列に格納する
		Node narray[] = new Node[branch.getNodeList().size()];
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			narray[i] = (Node)branch.getNodeList().elementAt(i);
		}

		// 各Nodeについて
		for(int i = 0; i < narray.length; i++) {
			Node node = narray[i];
			
			// 子Branchを再帰呼び出し
			Branch cbranch = node.getChildBranch();
			if(cbranch != null) {
				cancelOneBranch(tree, cbranch);
				tree.deleteOneBranch(cbranch);
				cbranch.getParentNode().getCurrentBranch().deleteOneNode(cbranch.getParentNode());
			}
			// 子NodeをrootBranchに移動
			else {
				rootBranch.addOneNode(node);
			}
			
			// 当該BranchからNodeを削除
			branch.deleteOneNode(node);
		}
		
	}
	
	/**
	 * rootBranch直下の子Nodeを対象にして、groupIdのTableの値でGroupを再構成する
	 * @param tree
	 * @param groupId
	 */
	public static void generateBranch(Tree tree, int groupId) {
		Branch rootBranch = tree.getRootBranch();
		Table table = tree.table.getTable(groupId + 1);
		
		// Branchを確保し、一時的に配列に格納する
		int tableSize = table.getSize();
		Branch barray[] = new Branch[tableSize + 1];
		for(int i = 0; i <= tableSize; i++)
			barray[i] = tree.getOneNewBranch();
		
		// rootBranch直下のNodeを一時的に配列に格納する
		Node narray[] = new Node[rootBranch.getNodeList().size()];
		for(int i = 0; i < rootBranch.getNodeList().size(); i++) {
			narray[i] = (Node)rootBranch.getNodeList().elementAt(i);
		}
		
		//  rootBranch直下の各Nodeについて
		for(int i = 0; i < narray.length; i++) {
			Node node = narray[i];
			
			// 該当するBranchを特定し、Branchに追加
			int bid = node.table.getId(groupId + 1) - 1;
			if(bid >= 0 && bid < barray.length)
				barray[bid].addOneNode(node);
			else
				barray[tableSize].addOneNode(node);
			
			// rootBranchから削除
			rootBranch.deleteOneNode(node);
		}
		
		// BranchをrootBranch直下に追加する
		for(int i = 0; i < barray.length; i++) {
			Node node = rootBranch.getOneNewNode();
			node.setChildBranch(barray[i]);
			barray[i].setParentNode(node);
		}
	}
}
