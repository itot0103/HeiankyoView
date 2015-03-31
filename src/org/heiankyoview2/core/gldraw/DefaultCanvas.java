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
	//DefaultDrawer drawer;
	NodeLinkDrawer drawer;
	GLCanvas glc;
	BufferedImage image = null;
	
	Tree tree;
	TreeTable tg;
	
	boolean isMousePressed = false, isAnnotation = true;
	int dragMode, width, height, mouseX, mouseY;
	double linewidth = 1.0, bgR = 0.0, bgG = 0.0, bgB = 0.0;


	/**
	 * Constructor
	 * @param width ��ʂ̕�
	 * @param height ��ʂ̍���
	 * @param foregroundColor ��ʂ̑O�ʐF
	 * @param backgroundColor ��ʂ̔w�i�F
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
		//drawer = new DefaultDrawer(width, height, glc);
		drawer = new NodeLinkDrawer(width, height, glc);
		glc.addGLEventListener(drawer);
		trans = new Transformer();
		trans.viewReset();
		drawer.setTransformer(trans);
	}

	/**
	 * Constructor
	 * @param width ��ʂ̕�
	 * @param height ��ʂ̍���
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
	 * Drawer ���Z�b�g����
	 * @param d Drawer
	 */
	/*
	public void setDrawer(DefaultDrawer d) {
		drawer = d;
	}
	*/
	
	/**
	 * Transformer ���Z�b�g����
	 * @param t Transformer
	 */
	public void setTransformer(Transformer t) {
		trans = t;
	}

	/**
	 * Tree���Z�b�g����
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
	 * Tree���Q�b�g����
	 * @return
	 */
	public Tree getTree() {
		return tree;
	}
	
	/**
	 * �ĕ`��
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
	 * �摜�t�@�C���ɏo�͂���
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
	 * �O�ʐF�Ɣw�i�F���Z�b�g����
	 * @param foregroundColor �O�ʐF
	 * @param backgroundColor �w�i�F
	 */
	public void setColors(Color foregroundColor, Color backgroundColor) {
		setForeground(foregroundColor);
		setBackground(backgroundColor);
	}


	/**
	 * �}�E�X�{�^���������ꂽ���[�h��ݒ肷��
	 */
	public void mousePressed() {
		isMousePressed = true;
		trans.mousePressed();
		drawer.setMousePressSwitch(isMousePressed);
	}

	/**
	 * �}�E�X�{�^���������ꂽ���[�h��ݒ肷��
	 */
	public void mouseReleased() {
		isMousePressed = false;
		drawer.setMousePressSwitch(isMousePressed);
	}

	/**
	 * �}�E�X���h���b�O���ꂽ���[�h��ݒ肷��
	 * @param xStart ���O��X���W�l
	 * @param xNow ���݂�X���W�l
	 * @param yStart ���O��Y���W�l
	 * @param yNow ���݂�Y���W�l
	 */
	public void drag(int xStart, int xNow, int yStart, int yNow) {
		int x = xNow - xStart;
		int y = yNow - yStart;

		trans.drag(x, y, width, height, dragMode, drawer);
	}


	/**
	 * ���̑������Z�b�g����
	 * @param linewidth ���̑����i��f���j
	 */
	public void setLinewidth(double linewidth) {
		this.linewidth = linewidth;
		drawer.setLinewidth(linewidth);
	}


	/**
	 * �w�i�F��r,g,b��3�l�Őݒ肷��
	 * @param r �ԁi0�`1�j
	 * @param g �΁i0�`1�j
	 * @param b �i0�`1�j
	 */
	
	public void setBackground(double r, double g, double b) {
		bgR = r;
		bgG = g;
		bgB = b;
		setBackground(
			new Color((int) (r * 255), (int) (g * 255), (int) (b * 255)));
	}
	
	
	
	
	/**
	 * �}�E�X�h���b�O�̃��[�h��ݒ肷��
	 * @param dragMode (1:ZOOM  2:SHIFT  3:ROTATE)
	 */
	public void setDragMode(int newMode) {
		dragMode = newMode;
	}

	/**
	 * �}�E�X�h���b�O�̃��[�h�𓾂�
	 * @return dragMode (1:ZOOM  2:SHIFT  3:ROTATE)
	 */
	public int getDragMode() {
		return dragMode;
	}
	
	
	/**
	 * �}�E�X�{�^���������ꂽ���[�h��ݒ肷��
	 */
	public void mousePressed(int x, int y) {
		isMousePressed = true;
		trans.mousePressed();
		drawer.setMousePressSwitch(isMousePressed);
	}
	
	/**
	 * ��ʕ\���̊g��k���E��]�E���s�ړ��̊e��Ԃ����Z�b�g����
	 */
	public void viewReset() {
		trans.viewReset();
	}

	/**
	 * ��ʏ�̓��蕨�̂��s�b�N����
	 * @param px �s�b�N�������̂̉�ʏ��X���W�l
	 * @param py �s�b�N�������̂̉�ʏ��Y���W�l
	 */
	public void pickObjects(int px, int py) {
		drawer.pickObjects(px, py);
	}
	
	/**
	 * �s�b�N���ꂽ�m�[�h����肷��
	 * @return �s�b�N���ꂽNode
	 */
	public Node getPickedNode() {
		return drawer.getPickedNode();
	}

	/**
	 * �A�m�e�[�V�����\����ON/OFF����
	 * @param flag �\������Ȃ�true, �\�����Ȃ��Ȃ�false
	 */
	public void setAnnotationSwitch(boolean flag) {
		
	}
	
	/**
	 * �F�v�Z�N���X��ݒ肷��
	 */
	public void setColorCalculator(ColorCalculator cc) {
		drawer.setColorCalculator(cc);
	}
	
	
	/**
	 * �}�E�X�J�[�\���̃C�x���g�����m����ݒ���s��
	 * @param eventListener EventListner
	 */
	public void addCursorListener(EventListener eventListener) {
		addMouseListener((MouseListener) eventListener);
		addMouseMotionListener((MouseMotionListener) eventListener);
	}
}
