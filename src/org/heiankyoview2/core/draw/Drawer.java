package org.heiankyoview2.core.draw;

import org.heiankyoview2.core.tree.Tree;

import java.awt.*;

/**
 * 描画処理を実装するクラスのためのインタフェース
 * @author itot
 */
public interface Drawer {

	/**
	 * Treeをセットする
	 * @param tree Tree
	 */
	public void setTree(Tree tree);

	/**
	 * Transformerをセットする
	 * @param transformer 
	 */
	public void setTransformer(Transformer view);

	/**
	 * Buffer をセットする
	 * @param dbuf Buffer
	 */
	public void setBuffer(Buffer dbuf);
	
	/**
	 * 描画領域のサイズを設定する
	 * @param width 描画領域の幅
	 * @param height 描画領域の高さ
	 */
	public void setWindowSize(int width, int height);

	/**
	 * マウスボタンのON/OFFを設定する
	 * @param isMousePressed マウスボタンが押されていればtrue
	 */
	public void setMousePressSwitch(boolean isMousePressed);

	/**
	 * 線の太さをセットする
	 * @param lw 線の太さ（画素数）
	 */
	public void setLinewidth(double lw);

	/**
	 * アノテーション表示のON/OFF制御
	 * @param flag 表示するならtrue, 表示しないならfalse
	 */
	public void setAnnotationSwitch(boolean flag);
		
								
	/**
	 * 描画を実行する
	 * @param g2 Graphics2D
	 */
	public void draw(Graphics2D g2);

	/**
	 * 物体をピックする
	 * @param px ピックした物体の画面上のx座標値
	 * @param py ピックした物体の画面上の座標値
	 */
	public void pickObjects(int px, int py);
		
	/**
	 * 視点からの深さでソートしたNode配列を生成する （奥行きソート法による描画のため）
	 */
	public void createSortedNodeArray();
}