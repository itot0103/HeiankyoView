package org.heiankyoview2.applet.heiankyoview;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.draw.PileCanvas;
import org.heiankyoview2.core.window.Window;
import org.heiankyoview2.core.window.*;

import java.awt.*;
import javax.swing.*;

/**
 * HeianView �̖{�̂ƂȂ� applet ���\�z���A�e�C�x���g�ɑ΂��鏈�����Ǘ�����
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
	 * applet �����������A�e��f�[�^�\��������������
	 */
	public void init() {
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
		
		// Canvas��ViewingPanel�̃��C�A�E�g
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(canvas, BorderLayout.CENTER);
		mainPanel.add(viewingPanel, BorderLayout.EAST);

		// �E�B���h�E��̃��C�A�E�g
		windowContainer = this.getContentPane();
		windowContainer.setLayout(new BorderLayout());
		windowContainer.add(mainPanel, BorderLayout.CENTER);
		windowContainer.add(menuBar, BorderLayout.NORTH);

		boolean isValid = org.heiankyoview2.core.util.ExpirationChecker.isValid();
		if(isValid == false) System.exit(-1);
		
	}




	
	/**
	 * main�֐�
	 * @param args ���s���̈���
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
