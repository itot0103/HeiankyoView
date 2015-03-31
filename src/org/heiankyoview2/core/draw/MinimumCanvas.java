package org.heiankyoview2.core.draw;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.TreeTable;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * HeianView のための描画領域を管理する
 * @author itot
 */
public class MinimumCanvas extends JPanel implements Canvas {

	/* var */
	Tree tree;
	TreeTable tg;
	Transformer view;
	Drawer drawer;
	Buffer dbuf;
	ViewFile vfile = null;
	BufferedImage image = null;
	
	
	boolean isMousePressed = false, isAnnotation = true;
	int dragMode;
	int width, height, mouseX, mouseY;
	double linewidth = 1.0, bgR = 0.0, bgG = 0.0, bgB = 0.0;


	/**
	 * Constructor
	 * @param width 画面の幅
	 * @param height 画面の高さ
	 * @param foregroundColor 画面の前面色
	 * @param backgroundColor 画面の背景色
	 */
	public MinimumCanvas(
		int width,
		int height,
		Color foregroundColor,
		Color backgroundColor) {

		super(true);
		this.width = width;
		this.height = height;
		setSize(width, height);
		setColors(foregroundColor, backgroundColor);
		dragMode = 2; // SHIFT

		/*
		drawer = new DefaultDrawer(width, height);
		view = new View();
		dbuf = new DefaultBuffer();
		drawer.setView(view);
		drawer.setBuffer(dbuf);
		*/
	}

	/**
	 * Constructor
	 * @param width 画面の幅
	 * @param height 画面の高さ
	 */
	public MinimumCanvas(int width, int height) {
		this(width, height, Color.white, Color.black);
	}


	/**
	 * Drawer をセットする
	 * @param d Drawer
	 */
	public void setDrawer(Drawer d) {
		drawer = d;
	}

	/**
	 * Buffer をセットする
	 * @param b Buffer
	 */
	public void setBuffer(Buffer b) {
		dbuf = b;
	}
	
	/**
	 * View をセットする
	 * @param v View
	 */
	public void setTransformer(Transformer v) {
		view = v;
	}


	/**
	 * Treeをセットする
	 * @param tree Tree
	 */
	public void setTree(Tree tree) {
		this.tree = tree;
		if(tree == null) return;
		tg = tree.table;
		dbuf.setTree(tree);
		view.setTree(tree);
		
		//
		// CAUTION !! this must be called AFTER dbuf.setTree()
		//
		drawer.setTree(tree);
	}

	/**
	 * Treeを返す
	 * @return Tree
	 */
	public Tree getTree() {
		return tree;
	}

	/**
	 * 横幅を返す
	 */
	public int getWidth() {
		return (int)getSize().getWidth();
	}
	
	/**
	 * 縦幅を返す
	 */
	public int getHeight() {
		return (int)getSize().getHeight();
	}
	
	
	/**
	 * 描画を実行する
	 * @param g Graphics
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // clear the background

		if (tree == null)
			return;

		Graphics2D g2 = (Graphics2D) g;
		drawer.draw(g2);
	}
	
	/**
	 * 再描画
	 */
	public void repaint() {
		display();
	}
	
	/**
	 * 再描画
	 */
	public void display() {

		Graphics g = getGraphics();
		if (g == null)
			return;

		if (drawer != null) {
			width = (int) getSize().getWidth();
			height = (int) getSize().getHeight();
			drawer.setWindowSize(width, height);
		}

		if (isMousePressed == false && dragMode == 3)
			drawer.createSortedNodeArray();

		paintComponent(g);
	}

	/**
	 * 画像ファイルに出力する
	 */
	public void saveImageFile(File file) {

		width = (int) getSize().getWidth();
		height = (int) getSize().getHeight();
		image = new BufferedImage(width, height, 
                BufferedImage.TYPE_INT_BGR);
		
		Graphics2D gg2 = image.createGraphics();
		gg2.clearRect(0, 0, width, height);
		drawer.draw(gg2);
		try {
			ImageIO.write(image, "bmp", file);
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * 前面色と背景色をセットする
	 * @param foregroundColor 前面色
	 * @param backgroundColor 背景色
	 */
	public void setColors(Color foregroundColor, Color backgroundColor) {
		setForeground(foregroundColor);
		setBackground(backgroundColor);
	}


	/**
	 * マウスボタンが押されたモードを設定する
	 */
	public void mousePressed(int x, int y) {
		isMousePressed = true;
		view.mousePressed(dragMode, x, y);
		drawer.setMousePressSwitch(isMousePressed);
	}

	/**
	 * マウスボタンが離されたモードを設定する
	 */
	public void mouseReleased() {
		isMousePressed = false;
		drawer.setMousePressSwitch(isMousePressed);
	}

	/**
	 * マウスがドラッグされたモードを設定する
	 * @param xStart 直前のX座標値
	 * @param xNow 現在のX座標値
	 * @param yStart 直前のY座標値
	 * @param yNow 現在のY座標値
	 */
	public void drag(int xStart, int xNow, int yStart, int yNow) {
		int x = xNow - xStart;
		int y = yNow - yStart;

		view.drag(x, y, width, height, dragMode);
	}

	/**
	 * Treeの部分表示を設定する
	 * @param sw スイッチとなる整数値
	 */
	public void setPartialDisplay(int sw) {
		if (tree == null)
			return;

		dbuf.updatePartialDisplay(sw);
		this.setBranchAnnotations();
		drawer.createSortedNodeArray();
		repaint();
	}

	/**
	 * 線の太さをセットする
	 * @param linewidth 線の太さ（画素数）
	 */
	public void setLinewidth(double linewidth) {
		this.linewidth = linewidth;
		drawer.setLinewidth(linewidth);
	}


	/**
	 * 背景色をr,g,bの3値で設定する
	 * @param r 赤（0～1）
	 * @param g 緑（0～1）
	 * @param b 青（0～1）
	 */
	public void setBackground(double r, double g, double b) {
		bgR = r;
		bgG = g;
		bgB = b;
		setBackground(
			new Color((int) (r * 255), (int) (g * 255), (int) (b * 255)));
	}

	/**
	 * アノテーション表示のON/OFF制御
	 * @param flag 表示するならtrue, 表示しないならfalse
	 */
	public void setAnnotationSwitch(boolean flag) {
		isAnnotation = flag;
		drawer.setAnnotationSwitch(flag);
	}

	/**
	 * マウスドラッグのモードを設定する
	 * @param dragMode (1:ZOOM  2:SHIFT  3:ROTATE)
	 */
	public void setDragMode(int newMode) {
		dragMode = newMode;
	}

	/**
	 * マウスドラッグのモードを得る
	 * @return dragMode (1:ZOOM  2:SHIFT  3:ROTATE)
	 */
	public int getDragMode() {
		return dragMode;
	}
	
	/**
	 * 画面表示の拡大縮小・回転・平行移動の各状態をリセットする
	 */
	public void viewReset() {
		view.viewReset();
	}

	/**
	 * 画面上の特定物体をピックする
	 * @param px ピックした物体の画面上のX座標値
	 * @param py ピックした物体の画面上のY座標値
	 */
	public void pickObjects(int px, int py) {
		drawer.pickObjects(px, py);
	}

	/**
	 * アノテーションを設定する
	 */
	public void setBranchAnnotations() {
		if (dbuf != null)
			dbuf.addBranchAnnotations();
	}

	/**
	 * ピックされたノードを外部指定する
	 * @return ピックされたNode
	 */
	public void setPickedNode(Node node) {
		if (dbuf != null)
			dbuf.setPickedNode(node);
	}

	/**
	 * ピックされたノードを特定する
	 * @return ピックされたNode
	 */
	public Node getPickedNode() {
		if (dbuf != null) {
			Node node = dbuf.getPickedNode();
			return node;
		}
		return null;
	}
	
	/**
	 * 拡大縮小・回転・平行移動に関するパラメータをファイルに保存する
	 */
	public void saveViewingFile() {

		boolean ret;

		ret = vfile.saveStart();
		if (ret == false)
			return;

		vfile.setAnnotationSwitch(isAnnotation);
		vfile.setLinewidth(linewidth);
		vfile.setBackground(bgR, bgG, bgB);

		vfile.saveEnd();
	}

	
	/**
	 * 拡大縮小・回転・平行移動に関するパラメータをファイルから読み込む
	 */
	public void openViewingFile() {

		boolean ret;

		ret = vfile.openStart();
		if (ret == false)
			return;

		isAnnotation = vfile.getAnnotationSwitch();
		linewidth = vfile.getLinewidth();
		bgR = vfile.getBackgroundR();
		bgG = vfile.getBackgroundG();
		bgB = vfile.getBackgroundB();

		vfile.openEnd();

	}

	/**
	 * マウスカーソルのイベントを検知する設定を行う
	 * @param eventListener EventListner
	 */
	public void addCursorListener(EventListener eventListener) {
		addMouseListener((MouseListener) eventListener);
		addMouseMotionListener((MouseMotionListener) eventListener);
		addMouseWheelListener((MouseWheelListener) eventListener);
	}
}
