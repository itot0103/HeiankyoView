/*
 * Created on 2005/10/25
 */
package org.heiankyoview2.applet.junihitoeview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.draw.Buffer;
import org.heiankyoview2.core.draw.Drawer;
import org.heiankyoview2.core.draw.DrawerUtility;
import org.heiankyoview2.core.draw.Transformer;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;

/**
 * @author itot
 */
public class DefaultDrawer implements org.heiankyoview2.core.draw.Drawer {

	Tree tree = null;
	TreeTable tg = null;
	Transformer view = null;
	Buffer dbuf = null;
	Node nodearray[];
	double p1[], p2[], p3[], p4[];
	int imageSize[] = new int[2];
	boolean isMousePressed = false, isAnnotation = true;
	Node pickedNode = null;
	double linewidth = 1.0;
	int painterType = 1;
	DrawerUtility du = null;
	int targetTables[];
	int paintType = 2;

	int counter = 0;
	int nodelevel;// = 5;
	boolean isLod = false;

	NodePainter1 snp1 = new NodePainter1();
	NodePainter2 snp2 = new NodePainter2();
	
	
	/**
	 * Constructor
	 * 
	 * @param width
	 *            描画領域の幅
	 * @param height
	 *            描画領域の高さ
	 */
	public DefaultDrawer(int width, int height) {
		imageSize[0] = width;
		imageSize[1] = height;
		du = new DrawerUtility(width, height);
	}

	/**
	 * Viewをセットする
	 * 
	 * @param view
	 *            View
	 */
	public void setTransformer(Transformer view) {
		this.view = view;
		du.setTransformer(view);
	}

	/**
	 * Buffer をセットする
	 * 
	 * @param dbuf
	 *            Buffer
	 */
	public void setBuffer(Buffer dbuf) {
		this.dbuf = dbuf;
		du.setBuffer(dbuf);
	}

	/**
	 * 描画領域のサイズを設定する
	 * 
	 * @param width
	 *            描画領域の幅
	 * @param height
	 *            描画領域の高さ
	 */
	public void setWindowSize(int width, int height) {
		imageSize[0] = width;
		imageSize[1] = height;
		du.setWindowSize(width, height);
	}

	/**
	 * マウスボタンのON/OFFを設定する
	 * 
	 * @param isMousePressed
	 *            マウスボタンが押されていればtrue
	 */
	public void setMousePressSwitch(boolean isMousePressed) {
		this.isMousePressed = isMousePressed;
	}

	/**
	 * Treeをセットする
	 * 
	 * @param tree
	 *            Tree
	 */
	public void setTree(Tree tree) {
		this.tree = tree;
		tg = tree.table;
		view.setTree(tree);
		du.setTree(tree);
		nodearray = du.createSortedNodeArray();
		
		snp1.setTree(tree, du, nodearray, imageSize);
		snp2.setTree(tree, du, nodearray, imageSize);
	}

	/**
	 * 線の太さをセットする
	 * 
	 * @param lw
	 *            線の太さ（画素数）
	 */
	public void setLinewidth(double lw) {
		linewidth = lw;
	}

	/**
	 * アノテーション表示のON/OFF制御
	 * 
	 * @param flag
	 *            表示するならtrue, 表示しないならfalse
	 */
	public void setAnnotationSwitch(boolean flag) {
		isAnnotation = flag;
	}

	/**
	 * 描画対象となるテーブルのリストをセットする
	 */
	public void setTargetTables(int[] t) {
		targetTables = t;
		snp1.setTargetTables(t);
		snp2.setTargetTables(t);
	}
	
	/**
	 * 視点からの深さでソートしたNode配列を生成する （奥行きソート法による描画のため）
	 */
	public void createSortedNodeArray() {
		nodearray = du.createSortedNodeArray();
	}

	/**
	 * paintTypeをセットする
	 */
	public void setPaintType(int t) {
		paintType = t;
	}
	
	/**
	 * LODのON/OFF設定を行う
	 */
	public void setLod(boolean input) {
		snp2.setLod(true, input);
		isLod = input;
	}
	
	
	/**
	 * 描画を実行する
	 * 
	 * @param g2
	 *            Graphics2D
	 */
	public void draw(Graphics2D g2) {

		if (tree == null || view == null)
			return;

		if(targetTables == null || targetTables.length <= 0) return;
		
		TreeTable tg = tree.table;
		if(targetTables[targetTables.length - 1] >= tg.getNumTable()) return;
		
		Branch rootBranch = tree.getRootBranch();
		Branch rootDisplayBranch = dbuf.getRootDisplayBranch();

		//
		// Draw borders
		//
		if (rootBranch == rootDisplayBranch || rootDisplayBranch == null)
			drawBorders(rootBranch, g2);
		else
			drawBorders(rootDisplayBranch, g2);

		//
		// Paint nodes
		//
		if (isMousePressed == false) {
			if(paintType == 1) snp1.paintNodes(g2, null, view.getViewScale());
			if(paintType == 2) snp2.paintNodes(g2, null, view.getViewScale());
		}
			

		//
		// Write annotations
		//
		if (isAnnotation == true)
			writeAnnotation(g2);

	}

	/**
	 * Branch の境界線を描画する
	 * 
	 * @param branch
	 *            Branch
	 * @param g2
	 *            Graphics2D
	 */
	void drawBorders(Branch branch, Graphics2D g2) {
		Node parentNode = branch.getParentNode();
		double xmax, ymax, xmin, ymin;

		int level = branch.getLevel();
		if (isMousePressed == true
				&& level > 2 + dbuf.getRootDisplayBranch().getLevel())
			return;

		//
		// Skip if display flag is false
		//
		if(pickedNode == parentNode) 
			g2.setPaint(new Color(255, 0, 255));
		else
			g2.setPaint(new Color(128, 128, 128));
		g2.setStroke(new BasicStroke((float) linewidth));

		//
		// write the border line of the branch
		//
		xmax = parentNode.getX() + parentNode.getWidth();
		xmin = parentNode.getX() - parentNode.getWidth();
		ymax = parentNode.getY() + parentNode.getHeight();
		ymin = parentNode.getY() - parentNode.getHeight();
		p1 = du.transformPosition(xmax, ymax, 0.0, 1);
		p2 = du.transformPosition(xmax, ymin, 0.0, 2);
		p3 = du.transformPosition(xmin, ymin, 0.0, 3);
		p4 = du.transformPosition(xmin, ymax, 0.0, 4);

		GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);

		polygon.moveTo((int) p1[0], (int) p1[1]);
		polygon.lineTo((int) p2[0], (int) p2[1]);
		polygon.lineTo((int) p3[0], (int) p3[1]);
		polygon.lineTo((int) p4[0], (int) p4[1]);
		polygon.closePath();
		g2.draw(polygon);
		

		//
		// for each (PARENT) node:
		//     Recursive call for child branches
		//
		for (int i = 1; i <= branch.getNodeList().size(); i++) {
			Node node = branch.getNodeAt(i);
			Branch childBranch = node.getChildBranch();

			if (childBranch == null)
				continue;
			if(paintType == 1) nodelevel = snp1.getNodeLevel();
			if(paintType == 2) nodelevel = snp2.getNodeLevel();
			
			if (isLod == false || level < nodelevel - 1)
				drawBorders(childBranch, g2);
		}
	}



	/**
	 * アノテーションを描画する
	 * 
	 * @param g2
	 *            Graphics2D
	 */
	void writeAnnotation(Graphics2D g2) {

		String name = null;

		//
		// Calculate the number of annotations to write
		//
		int numAnnotation = (int) (view.getViewScale() * view.getViewScale()
				* (float) imageSize[1] * 0.01f);
		Branch rootDisplayBranch = dbuf.getRootDisplayBranch();
		if (rootDisplayBranch != null
				&& tree.getRootBranch() != rootDisplayBranch) {
			Node node1 = tree.getRootBranch().getParentNode();
			Node node2 = rootDisplayBranch.getParentNode();
			numAnnotation = (int) ((double) numAnnotation
					* (node2.getWidth() * node2.getHeight()) / (node1
					.getWidth() * node1.getHeight()));
		}
		if (numAnnotation > dbuf.getNumAnnotation()) {
			numAnnotation = dbuf.getNumAnnotation();
		}

		//
		// for each annotation in the sorted order
		//
		g2.setPaint(new Color(255, 255, 0));
		for (int i = 0; i < numAnnotation; i++) {
			double pos[] = dbuf.getAnnotationPosition(i);
			name = dbuf.getAnnotationName(i);
			p1 = du.transformPosition(pos[0], pos[1], pos[2], 1);
			writeOneString(name, g2);
		}

		if (pickedNode != null) {
			g2.setPaint(new Color(255, 0, 255));
			p1 = du.transformPosition(pickedNode.getX(), pickedNode.getY(),
					pickedNode.getZ(), 1);

			if(pickedNode.getChildBranch() != null)
				name = tg.getNodeAttributeName(pickedNode, 0);
			else
				name = tg.getNodeAttributeName(pickedNode, 1);

			writeOneString(name, g2);
		}
	}

	/**
	 * 1個の文字列を描画する
	 * 
	 * @param name
	 *            描画される文字列
	 * @param g2
	 *            Graphics2D
	 */
	void writeOneString(String name, Graphics2D g2) {
		if (name == null || name.length() <= 0)
			return;
		Font font = new Font("Arial", Font.BOLD, 14);
		g2.setFont(font);
		g2.drawString(name, (int) p1[0], (int) p1[1]);
	}

	/**
	 * 物体をピックする
	 * 
	 * @param px
	 *            ピックした物体の画面上のx座標値
	 * @param py
	 *            ピックした物体の画面上の座標値
	 */
	public void pickObjects(int px, int py) {

		if (tree == null || view == null)
			return;
		pickBorders(tree.getRootBranch(), px, py);
		pickNodes(px, py);
		
		if (dbuf != null)
			dbuf.setPickedNode(pickedNode);

	}

	/**
	 * Branch境界線をピックする
	 * 
	 * @param branch
	 *            Branch
	 * @param px
	 *            ピックした物体の画面上のx座標値
	 * @param py
	 *            ピックした物体の画面上の座標値
	 */
	public Node pickBorders(Branch branch, int px, int py) {
		Node parentNode = branch.getParentNode();
		double xmax, ymax, xmin, ymin;
		boolean flag = false;

		//
		// write the border line of the branch
		//

		xmax = parentNode.getX() + parentNode.getWidth();
		xmin = parentNode.getX() - parentNode.getWidth();
		ymax = parentNode.getY() + parentNode.getHeight();
		ymin = parentNode.getY() - parentNode.getHeight();
		p1 = du.transformPosition(xmax, ymax, 0.0, 1);
		p2 = du.transformPosition(xmax, ymin, 0.0, 2);
		p3 = du.transformPosition(xmin, ymin, 0.0, 3);
		p4 = du.transformPosition(xmin, ymax, 0.0, 4);

		flag = du.isInside(px, py, p1, p2, p3, p4);
		if (flag == true)
			pickedNode = parentNode;

		//
		// for each (PARENT) node:
		//     Recursive call for child branches
		//
		for (int i = 1; i <= branch.getNodeList().size(); i++) {
			Node node = branch.getNodeAt(i);
			Branch childBranch = node.getChildBranch();

			if (childBranch == null)
				continue;
			pickedNode = pickBorders(childBranch, px, py);
		}

		return pickedNode;
	}

	/**
	 * Nodeをピックする
	 * 
	 * @param px
	 *            ピックした物体の画面上のx座標値
	 * @param py
	 *            ピックした物体の画面上の座標値
	 */
	public Node pickNodes(int px, int py) {
		Node n = null;
		if(paintType == 1) n = snp1.pickNodes(true, px, py);
		if(paintType == 2) n = snp2.pickNodes(true, px, py);
		if(n != null) pickedNode = n;
		return pickedNode;
	}
	
	/**
	 * 指定されたBranchをハイライトする
	 */
	public void highlightSpecifiedBranch(Branch branch) {
		pickedNode = branch.getParentNode();
	}
	
}
