package org.heiankyoview2.core.draw;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Node;
import java.io.File;

/**
 * 描画領域を管理するクラスのためのインタフェース
 * @author itot
 */
public interface Canvas  {

	/**
	 * Treeをセットする
	 * @param tree Tree
	 */
	public void setTree(Tree tree);
	
	/**
	 * Treeをゲットする
	 * @return Tree
	 */
	public Tree getTree();
	
	/**
	 * 横幅、縦幅を返す
	 */
	public int getWidth();
	public int getHeight();
	
	/**
	 * 再描画
	 */
	public void display();
	
	/**
	 * 線の太さをセットする
	 * @param linewidth 線の太さ（画素数）
	 */
	public void setLinewidth(double linewidth);

	/**
	 * 背景色をr,g,bの3値で設定する
	 * @param r 赤（0～1）
	 * @param g 緑（0～1）
	 * @param b 青（0～1）
	 */
	public void setBackground(double r, double g, double b);
	
	/**
	 * アノテーション表示のON/OFF制御
	 * @param flag 表示するならtrue, 表示しないならfalse
	 */
	public void setAnnotationSwitch(boolean flag);
	
	/**
	 * アノテーションを設定する
	 */
	public void setBranchAnnotations();
	
	/**
	 * ピックされたノードを特定する
	 * @return ピックされたNode
	 */
	public Node getPickedNode();
	
	/**
	 * マウスドラッグのモードを設定する
	 * @param dragMode (1:ZOOM  2:SHIFT  3:ROTATE)
	 */
	public void setDragMode(int newMode);

	/**
	 * マウスドラッグのモードを得る
	 * @return dragMode (1:ZOOM  2:SHIFT  3:ROTATE)
	 */
	public int getDragMode();
	
	/**
	 * 画面表示の拡大縮小・回転・平行移動の各状態をリセットする
	 */
	public void viewReset();
	
	/**
	 * マウスボタンが押されたモードを設定する
	 */
	public void mousePressed(int x, int y);
	
	/**
	 * マウスボタンが離されたモードを設定する
	 */
	public void mouseReleased();
	
	/**
	 * 画像ファイルを出力する
	 */
	public void saveImageFile(File file);
	
	/**
	 * マウスがドラッグされたモードを設定する
	 * @param xStart 直前のX座標値
	 * @param xNow 現在のX座標値
	 * @param yStart 直前のY座標値
	 * @param yNow 現在のY座標値
	 */
	public void drag(int xStart, int xNow, int yStart, int yNow);
	
	/**
	 * 画面上の特定物体をピックする
	 * @param px ピックした物体の画面上のX座標値
	 * @param py ピックした物体の画面上のY座標値
	 */
	public void pickObjects(int px, int py);
}
