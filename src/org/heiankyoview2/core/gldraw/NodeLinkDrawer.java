package org.heiankyoview2.core.gldraw;

import java.awt.Color;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.gl2.GLUgl2;





import org.heiankyoview2.core.tree.*;
import org.heiankyoview2.core.table.*;
import org.heiankyoview2.core.util.*;


public class NodeLinkDrawer implements GLEventListener, Drawer {

	private GL gl;
	private GL2 gl2;
	private GLU glu;
	private GLUgl2 glu2;
	private GLUT glut;
	GLAutoDrawable glAD;

	private double angleX = 0.0f;
	private double angleY = 0.0f;
	private double shiftX = 0.0f;
	private double shiftY = 0.0f;
	private double scale = 1.0f;
	private double centerX, centerY, centerZ, size;

	DoubleBuffer modelview, projection, p1, p2, p3, p4;
	IntBuffer viewport;
	
	int imageSize[] = new int[2];
	double datevalue = 0.0, interval = 0.0;
	int segmentMode = 1;
	boolean isMousePressed = false, isAnnotation = true;
	double linewidth = 1.0;
	int windowWidth, windowHeight;

	Transformer trans = null;
	DrawerUtility du = null;
	GLCanvas glcanvas;
	
	Tree tree = null;
	TreeTable tg = null;
	ColorCalculator cc;
	
	Node pickedNode = null;
	double minDepth;
	
	
	/**
	 * Constructor
	 * @param width 描画領域の幅
	 * @param height 描画領域の高さ
	 */
	public NodeLinkDrawer(int width, int height, GLCanvas c) {
		
		glcanvas = c;
		imageSize[0] = width;
		imageSize[1] = height;
		du = new DrawerUtility(width, height);
		cc = new DefaultColorCalculator();
		
		viewport = IntBuffer.allocate(4);
		modelview = DoubleBuffer.allocate(16);
		projection = DoubleBuffer.allocate(16);
		
		p1 = DoubleBuffer.allocate(3);
		p2 = DoubleBuffer.allocate(3);
		p3 = DoubleBuffer.allocate(3);
		p4 = DoubleBuffer.allocate(3);
		
		glcanvas.addGLEventListener(this);
	}

	/**
	 * Constructor
	 * @param width 描画領域の幅
	 * @param height 描画領域の高さ
	 */
	public NodeLinkDrawer() {
		this(800, 600, null);
	}
		
		
	public GLAutoDrawable getGLAutoDrawable() {
		return glAD;
	}

	/**
	 * Transformerをセットする
	 * @param transformer 
	 */
	public void setTransformer(Transformer view) {
		this.trans = view;
		du.setTransformer(view);
	}

	/**
	 * 描画領域のサイズを設定する
	 * @param width 描画領域の幅
	 * @param height 描画領域の高さ
	 */
	public void setWindowSize(int width, int height) {
		imageSize[0] = width;
		imageSize[1] = height;
		du.setWindowSize(width, height);
	}

	
	/**
	 * アノテーション表示のON/OFF制御
	 * @param flag 表示するならtrue, 表示しないならfalse
	 */
	public void setAnnotationSwitch(boolean flag) {
		
	}
	
	/**
	 * マウスボタンのON/OFFを設定する
	 * @param isMousePressed マウスボタンが押されていればtrue
	 */
	public void setMousePressSwitch(boolean isMousePressed) {
		this.isMousePressed = isMousePressed;
	}

	/**
	 * 線の太さをセットする
	 * @param lw 線の太さ（画素数）
	 */
	public void setLinewidth(double lw) {
		linewidth = lw;
		
	}

	/**
	 * ダミーメソッド
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}
	
	/**
	 * Treeをセットする
	 * @param tree Tree
	 */
	public void setTree(Tree tree) {
		this.tree = tree;
		tg = tree.table;
		trans.setTree(tree);
		du.setTree(tree);
		centerX = trans.getCenter(0);
		centerY = trans.getCenter(1);
		centerZ = trans.getCenter(2);
		size = trans.getTreeSize();
	}
	
	/**
	 * 色計算クラスを設定する
	 */
	public void setColorCalculator(ColorCalculator cc) {
		this.cc = cc;
	}


	/**
	 * 初期化
	 */
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL();
		gl2= drawable.getGL().getGL2();
		glu = new GLU();
		glu2 = new GLUgl2();
		glut = new GLUT();
		this.glAD = drawable;
		
		gl2.glEnable(GL2.GL_RGBA);
		gl2.glEnable(GL2.GL_DEPTH);
		gl2.glEnable(GL2.GL_DOUBLE);
		gl2.glEnable(GL2.GL_DEPTH_TEST);
		gl2.glEnable(GL2.GL_NORMALIZE);
		gl2.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
		gl2.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
	}
	
	
	/**
	 * 再描画
	 */
	public void reshape(GLAutoDrawable drawable,
			int x, int y, int width, int height) {
		
		windowWidth = width;
		windowHeight = height;
	
		// ビューポートの定義
		gl2.glViewport(0, 0, width, height);
		
		// 投影変換行列の定義
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(-width / 200.0, width / 200.0, 
				  -height / 200.0, height / 200.0,
				  -1000.0, 1000.0);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		
	}
	


	/**
	 * 描画を実行する
	 */
	public void display(GLAutoDrawable drawable) {
		float lightcolor[] = {0.8f, 0.8f, 0.8f};
		
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		if(tree == null) return;
		
		// 視点位置を決定
		gl2.glLoadIdentity();
		glu.gluLookAt( centerX, centerY, (centerZ + 20.0),
		           centerX, centerY, centerZ,
		           0.0, 1.0, 0.0 );

		shiftX = trans.getViewShift(0);
		shiftY = trans.getViewShift(1);
		scale = trans.getViewScale() * windowWidth / (size * 300.0);
		angleX = trans.getViewRotateY() * 45.0;
		angleY = trans.getViewRotateX() * 45.0;

		// 行列をプッシュ
		gl2.glPushMatrix();
		
		// いったん原点方向に物体を動かす
		gl2.glTranslated(centerX, centerY, centerZ);
		
		// マウスの移動量に応じて回転
		gl2.glRotated(angleX, 1.0, 0.0, 0.0);
		gl2.glRotated(angleY, 0.0, 1.0, 0.0); 

		// マウスの移動量に応じて拡大縮小
		gl2.glScaled(scale, scale, 1.0);
		
		// マウスの移動量に応じて移動
		gl2.glTranslated((shiftX * 50.0), (shiftY * 50.0), 0.0);
		
		// 物体をもとの位置に戻す
		gl2.glTranslated(-centerX, -centerY, -centerZ);
		
		// 変換行列とビューポートの値を保存する
		gl2.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
		gl2.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview);
		gl2.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection);

		// 枠を描画
		gl2.glDisable(GL2.GL_LIGHTING);
		Branch branch = tree.getRootBranch();
		drawObjects(branch);
		
		// 行列をポップ
		gl2.glPopMatrix();
		
	}

	

	/**
	 * ノードとリンクを描く
	 */
	void drawObjects(Branch branch) {
		
		// 当該ノードを描く
		Node pnode = branch.getParentNode();
		if(pnode == tree.getRootNode())
			paintOneNode(pnode);
		
		// 枝ノードの下にある各ノードについて：
		//   再帰的に描画
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			//System.out.println("  i=" + i + "/" + branch.getNodeList().size() + "  node=" + node);
			
			if(node == null) continue;
			
			// ノードを描き、その線を引く
			gl2.glColor3d(0.5, 0.5, 0.5);
			gl2.glBegin(GL2.GL_LINE_STRIP);
			double x1 = pnode.getX();
			double y1 = pnode.getY();
			double x2 = node.getX();
			double y2 = node.getY();
			gl2.glVertex3d(x1, y1, -0.1);
			gl2.glVertex3d(x2, y2, -0.1);
			gl2.glEnd();
			//System.out.println("  [" + x1 + "," + y1 + "] - [" + x2 + "," + y2 + "]");
			
			paintOneNode(node);
			
			// 子Branchを探索
			//System.out.println("  i=" + i + "/" + branch.getNodeList().size() + "  child=" + node.getChildBranch());
			if(node.getChildBranch() != null) 
				drawObjects(node.getChildBranch());
		}
		
	}
	
	/**
	 * 葉ノード（棒グラフ・アイコン）を塗りつぶす
	 */
	void paintOneNode(Node node) {
		
		// 棒グラフの頂点座標値を算出
		double SIZE = 5.0;
		double xmax = node.getX() + node.getWidth() * SIZE;
		double xmin = node.getX() - node.getWidth() * SIZE;
		double ymax = node.getY() + node.getHeight() * SIZE;
		double ymin = node.getY() - node.getHeight() * SIZE;
		double zmax = node.getZ() + calcBarHeight(node);
		double zmin = node.getZ();
				
		// 棒グラフの色を算出
		Color color = Color.gray;
		if(node.getChildBranch() == null) {
			color = tg.calcNodeColor(node, cc);
			if (color == Color.black)
				return;
		}
		double rr = (float)color.getRed()   / 255.0f;
		double gg = (float)color.getGreen() / 255.0f;
		double bb = (float)color.getBlue()  / 255.0f;
		gl2.glColor3d(rr, gg, bb);
		
		// 1枚目の長方形
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glNormal3d(0.0, 0.0, 1.0);
		gl2.glVertex3d(xmax, ymin, zmax);
		gl2.glVertex3d(xmax, ymax, zmax);
		gl2.glVertex3d(xmin, ymax, zmax);
		gl2.glVertex3d(xmin, ymin, zmax);
		gl2.glEnd();
			
		// 2枚目の長方形
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glNormal3d(0.0, 0.0, -1.0);
		gl2.glVertex3d(xmax, ymin, zmin);
		gl2.glVertex3d(xmax, ymax, zmin);
		gl2.glVertex3d(xmin, ymax, zmin);
		gl2.glVertex3d(xmin, ymin, zmin);
		gl2.glEnd();
			
		// 3枚目の長方形
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glNormal3d(0.0, 1.0, 0.0);
		gl2.glVertex3d(xmax, ymax, zmax);
		gl2.glVertex3d(xmin, ymax, zmax);
		gl2.glVertex3d(xmin, ymax, zmin);
		gl2.glVertex3d(xmax, ymax, zmin);
		gl2.glEnd();
			
		// 4枚目の長方形
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glNormal3d(0.0, -1.0, 0.0);
		gl2.glVertex3d(xmax, ymin, zmax);
		gl2.glVertex3d(xmax, ymin, zmin);
		gl2.glVertex3d(xmin, ymin, zmin);
		gl2.glVertex3d(xmin, ymin, zmax);
		gl2.glEnd();
		
		// 5枚目の長方形
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glNormal3d(1.0, 0.0, 0.0);
		gl2.glVertex3d(xmax, ymax, zmax);
		gl2.glVertex3d(xmax, ymin, zmax);
		gl2.glVertex3d(xmax, ymin, zmin);
		gl2.glVertex3d(xmax, ymax, zmin);
		gl2.glEnd();
			
		// 6枚目の長方形
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glNormal3d(-1.0, 0.0, 0.0);
		gl2.glVertex3d(xmin, ymax, zmax);
		gl2.glVertex3d(xmin, ymax, zmin);
		gl2.glVertex3d(xmin, ymin, zmin);
		gl2.glVertex3d(xmin, ymin, zmax);
		gl2.glEnd();
		
	}
	

	
	/**
	 * 棒グラフの高さを算出する
	 */
	double calcBarHeight(Node node) {
		double height = tg.calcNodeHeightValue(node);
		if (height < 0.0) height = 0.0;
		if (height > 1.0) height = 1.0;
		return (height * trans.getTreeSize() * 0.02);
	}
	
	
	/**
	 * 物体をピックする
	 * @param px ピックした物体の画面上のx座標値
	 * @param py ピックした物体の画面上の座標値
	 */
	public void pickObjects(int px, int py) {
	}
	
	
	/**
	 * ピックされた物体を返す
	 */
	public Node getPickedNode() {
		return null;
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
}
