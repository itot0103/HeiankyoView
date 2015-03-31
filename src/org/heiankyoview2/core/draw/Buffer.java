package org.heiankyoview2.core.draw; 

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;


/**
 * 描画領域への表示内容をバッファするクラスを実装するためのインタフェース
 * @author itot
 */
public interface Buffer {

	/**
	 * Treeをセットする
	 * @param tree Tree
	 */
	public void setTree(Tree tree);

	/**
	 * Treeの全体を表示するか、あるNodeの下位階層だけを表示するか、をセットする
	 * @param sw 表示を切り替えるための数値（0:全体、1:ピックしたNodeの下位階層のみ、2:現在の1階層上まで）
	 */
	public void updatePartialDisplay(int sw);
				
	/**
	 * グラフの表示上の根branchを返す
	 * @return 表示上の根Branch
	 */
	public Branch getRootDisplayBranch();
	
	/**
	 * ピックされたNodeをセットする
	 * @param node ピックされたNode
	 */
	public void setPickedNode(Node node);
	
	/**
	 * ピックされたNodeを返す
	 * @return ピックされたNode
	 */
	public Node getPickedNode();

	/**
	 * 各Branchをたどり、アノテーションの表示対象となるNodeを追加する
	 */
	public void addBranchAnnotations();
		
	/**
	 * アノテーションリスト上のIDを入力して、それに対応するアノテーションの文字列を返す
	 * @param id アノテーションリスト上のID
	 * @return アノテーションの文字列
	 */
	public String getAnnotationName(int id);
	
	/**
	 * アノテーションリスト上のIDを入力して、それに対応するアノテーションの座標値を返す
	 * @param id アノテーションリスト上のID
	 * @return アノテーションの座標値
	 */
	public double[] getAnnotationPosition(int id);
	
	/**
	 * アノテーションリスト上のIDを入力して、それに対応するアノテーションのサイズを返す
	 * @param id アノテーションリスト上のID
	 * @return アノテーションのサイズ
	 */
	public double getAnnotationSize(int id);
	
	/**
	 * アノテーションの総数を返す
	 * @return アノテーションの総数
	 */
	public int getNumAnnotation();
}
