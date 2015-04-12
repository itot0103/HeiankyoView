
package org.heiankyoview2.applet.junihitoeview;

import java.awt.*;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.glwindow.*;
import org.heiankyoview2.core.gldraw.Canvas;
import org.leafdetector.applet.Tester;


public class SARView extends JApplet {

	// tree
	Tree tree = null;

	// GUI element
	DefaultMenuBar menuBar;
	DefaultViewingPanel viewingPanel = null;
	GlCursorListener cl;
	GlDefaultFileOpener fileOpener;
	static GlDefaultCanvas canvas;
	Container windowContainer;


	public void init() {
		setSize(new Dimension(1000,800));
		buildGUI();
	}


	public void start() {

	}


	public void stop() {
	}


	private void buildGUI() {

		// FileOpener
		fileOpener = new GlDefaultFileOpener();
		fileOpener.setContainer(windowContainer);

		// Canvas
		canvas = new GlDefaultCanvas(800, 450);
		canvas.requestFocus();
		fileOpener.setCanvas(canvas);
		GLCanvas glc = canvas.getGLCanvas();

		// ViewingPanel
		viewingPanel = new DefaultViewingPanel();
		viewingPanel.setCanvas(canvas);
		viewingPanel.setFileOpener(fileOpener);

		// MenuBar
		menuBar = new DefaultMenuBar();
		menuBar.setFileOpener(fileOpener);
		menuBar.setCanvas(canvas);

		// CursorListener
		cl = new GlCursorListener();
		cl.setCanvas(canvas, glc);
		cl.setViewingPanel(viewingPanel);
		cl.setFileOpener(fileOpener);
		canvas.addCursorListener(cl);

		// mainPanel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(glc, BorderLayout.CENTER);
		mainPanel.add(viewingPanel, BorderLayout.WEST);

		// windowContainer
		windowContainer = this.getContentPane();
		windowContainer.setLayout(new BorderLayout());
		windowContainer.add(mainPanel, BorderLayout.CENTER);
		windowContainer.add(menuBar, BorderLayout.NORTH);

	}

	/**
	 *  mainÉÅÉ\ÉbÉh
	 */
	public static void main(String[] args) {
		org.heiankyoview2.core.glwindow.Window window = 
			new org.heiankyoview2.core.glwindow.Window("SARView", 800, 600, Color.lightGray);
		SARView sv = new SARView();

		/* initialization */
		sv.init();
		window.getContentPane().add(sv);
		window.setVisible(true);

		/* start application */
		sv.start();
		
		/* Tester for SAR analysys */
		Tester tester=new Tester();
		tester.setCanvas(canvas);
		tester.start();

	}

}
