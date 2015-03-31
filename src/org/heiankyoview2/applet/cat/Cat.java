package org.heiankyoview2.applet.cat;

import org.heiankyoview2.core.placement.Packing;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.fileio.TreeFileReader;
import org.heiankyoview2.core.glwindow.Window;
import org.heiankyoview2.core.glwindow.*;

import java.awt.*;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;

/**
 * HeianView �̖{�̂ƂȂ� applet ���\�z���A�e�C�x���g�ɑ΂��鏈�����Ǘ�����
 * 
 * @author itot
 */
public class Cat extends JApplet {

	// tree
	Tree tree = null;

	// GUI element
	DefaultMenuBar menuBar;
	DefaultViewingPanel viewingPanel = null;
	CursorListener cl;
	DefaultFileOpener fileOpener;
	CatCanvas canvas;
	Container windowContainer;


	/**
	 * applet �����������A�e��f�[�^�\��������������
	 */
	public void init() {
		setSize(new Dimension(900,700));
		buildGUI();
	}

	/**
	 * applet �̊e�C�x���g�̎�t���X�^�[�g����
	 */
	public void start() {
	}

	/**
	 * applet �̊e�C�x���g�̎�t���X�g�b�v����
	 */
	public void stop() {
	}

	/**
	 * applet��������������
	 */
	private void buildGUI() {

		// FileOpener
		fileOpener = new DefaultFileOpener();
		fileOpener.setContainer(windowContainer);
		
		// Canvas
		canvas = new CatCanvas(512, 512);
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
		cl = new CursorListener();
		cl.setCanvas(canvas, glc);
		cl.setViewingPanel(viewingPanel);
		cl.setFileOpener(fileOpener);
		canvas.addCursorListener(cl);
		canvas.viewReset();
		
		// Canvas��ViewingPanel�̃��C�A�E�g
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(glc, BorderLayout.CENTER);
		mainPanel.add(viewingPanel, BorderLayout.WEST);

		// �E�B���h�E��̃��C�A�E�g
		windowContainer = this.getContentPane();
		windowContainer.setLayout(new BorderLayout());
		windowContainer.add(mainPanel, BorderLayout.CENTER);
		windowContainer.add(menuBar, BorderLayout.NORTH);

		/*
		boolean isValid = org.heiankyoview2.core.util.ExpirationChecker.isValid();
		if(isValid == false) System.exit(-1);
		*/
		
		/*
		CatPacking packing = new CatPacking();
		TreeFileReader input = new TreeFileReader("jiali-jnlp.tree", true);
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
	 * main�֐�
	 * @param args ���s���̈���
	 */
	public static void main(String[] args) {
		Window window = new Window("CAT", 800, 600, Color.lightGray);
		Cat cat = new Cat();

		cat.init();
		window.getContentPane().add(cat);
		window.setVisible(true);

		cat.start();

	}

}
