package org.heiankyoview2.core.gldraw;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.EventListener;

import javax.imageio.ImageIO;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;

import org.heiankyoview2.core.tree.*;
import org.heiankyoview2.core.util.*;
import org.heiankyoview2.core.table.*;



public class DefaultCanvas extends JPanel implements Canvas {

	/* var */
	Transformer trans;
	DefaultDrawer drawer;
	//NodeLinkDrawer drawer;
	GLCanvas glc;
	BufferedImage image = null;
	
	Tree tree;
	TreeTable tg;
	
	boolean isMousePressed = false, isAnnotation = true;
	int dragMode, width, height, mouseX, mouseY;
	double linewidth = 1.0, bgR = 0.0, bgG = 0.0, bgB = 0.0;


	/**
	 * Constructor
	 * @param width 画面の幅
	 * @param height 画面の高さ
	 * @param foregroundColor 画面の前面色
	 * @param backgroundColor 画面の背景色
	 */
	public DefaultCanvas(
		int width,
		int height,
		Color foregroundColor,
		Color backgroundColor) {

		this.width = width;
		this.height = height;
		setSize(width, height);
		setColors(foregroundColor, backgroundColor);
		dragMode = 1;
		
		glc = new GLCanvas();
		drawer = new DefaultDrawer(width, height, glc);
		//drawer = new NodeLinkDrawer(width, height, glc);
		glc.addGLEventListener(drawer);
		trans = new Transformer();
		trans.viewReset();
		drawer.setTransformer(trans);
	}

	/**
	 * Constructor
	 * @param width 画面の幅
	 * @param height 画面の高さ
	 */
	public DefaultCanvas(int width, int height) {
		this(width, height, Color.white, Color.black);
	}
	
	/**
	 * Constructor
	 */
	public DefaultCanvas() {
		this(800, 600, Color.white, Color.black);
	}

	public GLCanvas getGLCanvas(){
		return this.glc;
	}

	/**
	 * Drawer をセットする
	 * @param d Drawer
	 */
	/*
	public void setDrawer(DefaultDrawer d) {
		drawer = d;
	}
	*/
	
	/**
	 * Transformer をセットする
	 * @param t Transformer
	 */
	public void setTransformer(Transformer t) {
		trans = t;
	}

	/**
	 * Treeをセットする
	 * @param tree Tree
	 */
	public void setTree(Tree tree) {
		this.tree = tree;
		if(tree == null) return;
		tg = tree.table;

		trans.setTree(tree);
		drawer.setTree(tree);
	}

	
	/**
	 * Treeをゲットする
	 * @return
	 */
	public Tree getTree() {
		return tree;
	}
	
	/**
	 * 再描画
	 */
	public void display() {
		GLAutoDrawable glAD = drawer.getGLAutoDrawable();
        
		if (drawer != null) {
			width = (int) getSize().getWidth();
			height = (int) getSize().getHeight();
			drawer.setWindowSize(width, height);
		}
	
		if (glAD == null)
			return;
		
		glAD.display();
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
		//drawer.draw(gg2);
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
	public void mousePressed() {
		isMousePressed = true;
		trans.mousePressed();
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

		trans.drag(x, y, width, height, dragMode, drawer);
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
	 * マウスボタンが押されたモードを設定する
	 */
	public void mousePressed(int x, int y) {
		isMousePressed = true;
		trans.mousePressed();
		drawer.setMousePressSwitch(isMousePressed);
	}
	
	/**
	 * 画面表示の拡大縮小・回転・平行移動の各状態をリセットする
	 */
	public void viewReset() {
		trans.viewReset();
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
	 * ピックされたノードを特定する
	 * @return ピックされたNode
	 */
	public Node getPickedNode() {
		return drawer.getPickedNode();
	}

	/**
	 * アノテーション表示のON/OFF制御
	 * @param flag 表示するならtrue, 表示しないならfalse
	 */
	public void setAnnotationSwitch(boolean flag) {
		
	}
	
	/**
	 * 色計算クラスを設定する
	 */
	public void setColorCalculator(ColorCalculator cc) {
		drawer.setColorCalculator(cc);
	}
	
	
	/**
	 * マウスカーソルのイベントを検知する設定を行う
	 * @param eventListener EventListner
	 */
	public void addCursorListener(EventListener eventListener) {
		addMouseListener((MouseListener) eventListener);
		addMouseMotionListener((MouseMotionListener) eventListener);
	}
}
