
package org.heiankyoview2.applet.junihitoeview;

import java.awt.*;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.glwindow.*;

public class JunihitoeView extends JApplet {

	// tree
	Tree tree = null;

	// GUI element
	SimpleMenuBar menuBar;
	SimpleViewingPanel viewingPanel = null;
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
		viewingPanel = new SimpleViewingPanel();
		viewingPanel.setCanvas(canvas);
		viewingPanel.setFileOpener(fileOpener);

		// MenuBar
		menuBar = new SimpleMenuBar();
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

		boolean isValid = org.heiankyoview2.core.util.ExpirationChecker.isValid();
		if(isValid == false) System.exit(-1);
		
	}

	/**
	 *  mainÉÅÉ\ÉbÉh
	 */
	public static void main(String[] args) {
		
		org.heiankyoview2.core.glwindow.Window window = 
			new org.heiankyoview2.core.glwindow.Window("JunihitoeView", 800, 600, Color.lightGray);
		JunihitoeView sv = new JunihitoeView();

		/* initialization */
		sv.init();
		window.getContentPane().add(sv);
		window.setVisible(true);

		/* start application */
		sv.start();

	}

}
