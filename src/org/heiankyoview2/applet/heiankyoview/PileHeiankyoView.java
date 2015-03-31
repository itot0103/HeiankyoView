package org.heiankyoview2.applet.heiankyoview;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.draw.PileCanvas;
import org.heiankyoview2.core.window.Window;
import org.heiankyoview2.core.window.*;

import java.awt.*;
import javax.swing.*;

/**
 * HeianView の本体となる applet を構築し、各イベントに対する処理を管理する
 * 
 * @author itot
 */
public class PileHeiankyoView extends JApplet {

	// tree
	Tree tree = null;

	// GUI element
	DefaultMenuBar menuBar;
	DefaultViewingPanel viewingPanel = null;
	CursorListener cl;
	DefaultFileOpener fileOpener;
	PileCanvas canvas;
	Container windowContainer;


	/**
	 * applet を初期化し、各種データ構造を初期化する
	 */
	public void init() {
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
		canvas = new PileCanvas(512, 512);
		canvas.requestFocus();
		fileOpener.setCanvas(canvas);
		
		int pileId[] = {1,2,3,4,5,6,7,8,9,10};
		canvas.setPileId(pileId);
		
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
		mainPanel.add(viewingPanel, BorderLayout.EAST);

		// ウィンドウ上のレイアウト
		windowContainer = this.getContentPane();
		windowContainer.setLayout(new BorderLayout());
		windowContainer.add(mainPanel, BorderLayout.CENTER);
		windowContainer.add(menuBar, BorderLayout.NORTH);

		boolean isValid = org.heiankyoview2.core.util.ExpirationChecker.isValid();
		if(isValid == false) System.exit(-1);
		
	}




	
	/**
	 * main関数
	 * @param args 実行時の引数
	 */
	public static void main(String[] args) {
		Window window = new Window("HeiankyoView", 800, 600, Color.lightGray);
		PileHeiankyoView hv = new PileHeiankyoView();

		hv.init();
		window.getContentPane().add(hv);
		window.setVisible(true);

		hv.start();

	}

}
