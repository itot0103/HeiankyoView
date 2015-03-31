package org.heiankyoview2.core.window;

import org.heiankyoview2.core.tree.Tree;


/**
 * Tableに記述された属性を用いて表示属性（名前、色、高さ）を設定するためのパネル
 * @author itot
 */
public interface TablePanel {


	/**
	 * Treeをセットする
	 * @param tree Tree
	 */
	public void setTree(Tree tree); 

	/**
	 * Canvasをセットする
	 * @param c Canvas
	 */
	public void setCanvas(Object c); 

	
	/**
	 * 可視性をセットする
	 * @param flag
	 */
	public void setVisible(boolean flag); 

}
