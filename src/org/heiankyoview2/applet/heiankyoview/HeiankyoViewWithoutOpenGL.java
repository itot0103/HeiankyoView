package org.heiankyoview2.applet.heiankyoview;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.draw.DefaultCanvas;
import org.heiankyoview2.core.window.*;
import org.heiankyoview2.core.fileio.TreeFileReader;
import org.heiankyoview2.core.placement.Packing;

import java.awt.*;
import javax.swing.*;

/**
 * HeianView の本体となる applet を構築し、各イベントに対する処理を管理する
 * 
 * @author itot
 */
public class HeiankyoViewWithoutOpenGL extends JApplet {

	// tree
	Tree tree = null;

	// GUI element
	DefaultMenuBar menuBar;
	DefaultViewingPanel viewingPanel = null;
	CursorListener cl;
	DefaultFileOpener fileOpener;
	//DefaultCanvas canvas;
	DefaultCanvas canvas;
	Container windowContainer;


	/**
	 * applet を初期化し、各種データ構造を初期化する
	 */
	public void init() {
		setSize(new Dimension(1000,800));
		buildGUI();
	}

	/**
	 * applet の各イベントの受付をスタートする
	 */
	public void start() {
	}

	/**
	 * applet の各イベントの受付をストップする
	 */
	public void stop() {
	}

	/**
	 * applet等を初期化する
	 */
	private void buildGUI() {

		// FileOpener
		fileOpener = new DefaultFileOpener();
		fileOpener.setContainer(windowContainer);
		
		// Canvas
		//canvas = new DefaultCanvas(512, 512);
		canvas = new DefaultCanvas(512, 512);
		canvas.requestFocus();
		fileOpener.setCanvas(canvas);
		
		// ViewingPanel
		viewingPanel = new DefaultViewingPanel();
		viewingPanel.setCanvas(canvas);	
		viewingPanel.setFileOpener(fileOpener);
	
		// MenuBar
		menuBar = new DefaultMenuBar();
		menuBar.setFileOpener(fileOpener);
		menuBar.setCanvas(canvas);
		
		// CursorListener
		cl = new CursorListener();
		cl.setCanvas(canvas);
		cl.setViewingPanel(viewingPanel);
		cl.setFileOpener(fileOpener);
		canvas.addCursorListener(cl);
		
		// CanvasとViewingPanelのレイアウト
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(canvas, BorderLayout.CENTER);
		mainPanel.add(viewingPanel, BorderLayout.WEST);

		// ウィンドウ上のレイアウト
		windowContainer = this.getContentPane();
		windowContainer.setLayout(new BorderLayout());
		windowContainer.add(mainPanel, BorderLayout.CENTER);
		windowContainer.add(menuBar, BorderLayout.NORTH);

		boolean isValid = org.heiankyoview2.core.util.ExpirationChecker.isValid();
		if(isValid == false) System.exit(-1);
		
		/*
		Packing packing = new Packing();
		TreeFileReader input = new TreeFileReader("access_log.20081103.tree", true);
		if (input == null) return;
		tree = input.getTree();
		if (tree == null) return;
		tree.table.setNameType(0);
		tree.table.setColorType(1);
		tree.table.setHeightType(1);
		packing.placeNodesAllBranch(tree);
		canvas.setTree(tree);
		*/
	}




	
	/**
	 * main関数
	 * @param args 実行時の引数
	 */
	public static void main(String[] args) {
		org.heiankyoview2.core.glwindow.Window window = 
			new org.heiankyoview2.core.glwindow.Window("HeiankyoView", 800, 600, Color.lightGray);
		HeiankyoViewWithoutOpenGL hv = new HeiankyoViewWithoutOpenGL();

		hv.init();
		window.getContentPane().add(hv);
		window.setVisible(true);

		hv.start();

	}

}
