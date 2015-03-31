package org.heiankyoview2.core.table;

import org.heiankyoview2.core.tree.Tree;

/**
 * Treeの属性を表形式データで表現するためのクラス
 * @author itot
 */
public class NodeTablePointer {

	int numId = 0;
	int id[] = null;

	/**
	 * Constructor
	 */
	public NodeTablePointer() {
	}

	/**
	 * NodeからTableへのポインタの個数（通常はTableの個数）をセットする
	 * @param numId ポインタの個数
	 */
	public void setNumId(int numId) {
		if (numId < 1)
			return;

		this.numId = numId;
		id = new int[numId];
	}

	/**
	 * NodeからTableへのポインタの個数（通常はTableの個数）を返す
	 * @return ポインタの個数
	 */
	public int getNumId() {
		return numId;
	}

	/**
	 * NodeからTableへのポインタをセットする
	 * @param attId 参照するTableのID
	 * @param newId ポインタ（Table上の位置を表すID）
	 */
	public void setId(int attId, int newId) {
		if (numId < 1)
			return;
		if (attId <= 0 || attId > numId)
			return;

		id[attId - 1] = newId;
		
		//System.out.println("  NodeTablePointer: numId=" + numId + " attId=" + attId + " newId=" + newId);
	}

	/**
	 * NodeからTableへのポインタを返す
	 * @param attId 参照するTableのID
	 * @return ポインタ（Table上の位置を表すID）
	 */
	public int getId(int attId) {
		int retId = -1;
		
		if (numId < 1) {
			return -1;
		}
		if (attId <= 0 || attId > numId) {
			System.out.println("NodeTablePointer#getId: numId=" + numId + " attId=" + attId);
			return -1;
		}

		retId = id[attId - 1];
		return retId;
	}

	/**
	 * 別のNodeTablePointerに、このNodeTablePointerの属性をコピーする
	 * @param tree Tree
	 * @param another コピー先のNodeTablePointer
	 */
	public void copy(Tree tree, NodeTablePointer another) {

		int numId = this.getNumId();
		if (another.getNumId() <= 0)
			another.setNumId(numId);

		for (int i = 1; i <= numId; i++) {
			another.setId(i, this.getId(i));
		}

	}

}
