/*
 * Created on 2005/10/25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.heiankyoview2.applet.junihitoeview;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.draw.Buffer;
import org.heiankyoview2.core.draw.DefaultBuffer;
import org.heiankyoview2.core.draw.Drawer;
import org.heiankyoview2.core.draw.MinimumCanvas;
import org.heiankyoview2.core.draw.Transformer;
import org.heiankyoview2.core.draw.ViewFile;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;

/**
 * @author itot
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DefaultCanvas extends MinimumCanvas {

	/* var */
	Tree tree;
	TreeTable tg;
	Transformer view;
	DefaultBuffer dbuf;
	DefaultDrawer drawer;
	ViewFile vfile = null;
	int targetTables[];
	int paintType;
	
	boolean isMousePressed = false, isAnnotation = true;
	int dragMode;
	int width, height, mouseX, mouseY;
	double linewidth = 1.0, bgR = 0.0, bgG = 0.0, bgB = 0.0;
	double heightRatio = 0.5;

	/**
	 * Constructor
	 * @param width ????
	 * @param height ?????
	 * @param foregroundColor ??????
	 * @param backgroundColor ??????
	 */
	public DefaultCanvas(
		int width,
		int height,
		Color foregroundColor,
		Color backgroundColor) {

		super(width, height, foregroundColor, backgroundColor);

		drawer = new DefaultDrawer(width, height);
		view = new Transformer();
		dbuf = new DefaultBuffer();
		drawer.setTransformer(view);
		drawer.setBuffer(dbuf);

		setDrawer((Drawer) drawer);
		setBuffer((Buffer) dbuf);
		setTransformer((Transformer) view);

	}

	/**
	 * Constructor
	 * @param width ????
	 * @param height ?????
	 */
	public DefaultCanvas(int width, int height) {
		this(width, height, Color.white, Color.black);
	}


	/**
	 * ????????????????
	 * @return ??????Node
	 */
	public void setPickedNode(Node node) {
		/*
		if (dbuf != null)
			dbuf.setPickedNode(node);
		drawer.setPickedNode(node);
		*/
	}


	/**
	 * Tree??????
	 * @param tree Tree
	 */
	public void setTree(Tree tree) {
		this.tree = tree;
		if (tree == null)
			return;
		tg = tree.table;
		dbuf.setTree(tree);
		view.setTree(tree);

		//
		// CAUTION !! this must be called AFTER dbuf.setTree()
		//
		drawer.setTree(tree);
	
	}

	
	/**
	 * Tree??????
	 * @param tree Tree
	 */
	public Tree getTree() {
		return tree;
	}


	/**
	 * ???実???
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
	 * ???
	 */
	public void repaint() {
		display();
	}
	
	/**
	 * ???
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

		paintComponent(g);
	}

	/**
	 * ????e?????????
	 */
	public void setTargetTables(int t[]) {
		targetTables = t;
		drawer.setTargetTables(targetTables);
	}
	
	/**
	 * ????e?????????
	 */
	public int[] getTargetTables() {
		return targetTables;
	}
	
	/**
	 * ?????Branch????????
	 */
	public void highlightSpecifiedBranch(Branch branch) {
		drawer.highlightSpecifiedBranch(branch);
	}
	
	/**
	 * ?????????s????
	 * @param px ???????????????
	 * @param py ???????????????
	 */
	public void pickObjects(int px, int py) {
		drawer.pickObjects(px, py);
	}

	/**
	 * ｻｳｲｼ､ｬｽｭｲﾃ､ｨ､ﾞ､ｷ､ｿ｡｣?????Branch????????
	 */
	public TreeTable getTableTree() {
		return tg;
	}

	/**
	 * paintTypeをセットする
	 */
	public void setPaintType(int t) {
		paintType = t;
		drawer.setPaintType(paintType);
	}
	
	/**
	 * LODのON/OFF設定を行う
	 */
	public void setLod(boolean input) {
		drawer.setLod(input);
	}
}
