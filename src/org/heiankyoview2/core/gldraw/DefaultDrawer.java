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


public class DefaultDrawer implements GLEventListener, Drawer {

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
	 * @param width �`��̈�̕�
	 * @param height �`��̈�̍���
	 */
	public DefaultDrawer(int width, int height, GLCanvas c) {
		
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
	 * @param width �`��̈�̕�
	 * @param height �`��̈�̍���
	 */
	public DefaultDrawer() {
		this(800, 600, null);
	}
		
		
	public GLAutoDrawable getGLAutoDrawable() {
		return glAD;
	}

	/**
	 * Transformer���Z�b�g����
	 * @param transformer 
	 */
	public void setTransformer(Transformer view) {
		this.trans = view;
		du.setTransformer(view);
	}

	/**
	 * �`��̈�̃T�C�Y��ݒ肷��
	 * @param width �`��̈�̕�
	 * @param height �`��̈�̍���
	 */
	public void setWindowSize(int width, int height) {
		imageSize[0] = width;
		imageSize[1] = height;
		du.setWindowSize(width, height);
	}

	
	/**
	 * �A�m�e�[�V�����\����ON/OFF����
	 * @param flag �\������Ȃ�true, �\�����Ȃ��Ȃ�false
	 */
	public void setAnnotationSwitch(boolean flag) {
		
	}
	
	/**
	 * �}�E�X�{�^����ON/OFF��ݒ肷��
	 * @param isMousePressed �}�E�X�{�^����������Ă����true
	 */
	public void setMousePressSwitch(boolean isMousePressed) {
		this.isMousePressed = isMousePressed;
	}

	/**
	 * ���̑������Z�b�g����
	 * @param lw ���̑����i��f���j
	 */
	public void setLinewidth(double lw) {
		linewidth = lw;
		
	}

	/**
	 * �_�~�[���\�b�h
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}
	
	/**
	 * Tree���Z�b�g����
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
	 * �F�v�Z�N���X��ݒ肷��
	 */
	public void setColorCalculator(ColorCalculator cc) {
		this.cc = cc;
	}


	/**
	 * ������
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
	 * �ĕ`��
	 */
	public void reshape(GLAutoDrawable drawable,
			int x, int y, int width, int height) {
		
		windowWidth = width;
		windowHeight = height;
	
		// �r���[�|�[�g�̒�`
		gl2.glViewport(0, 0, width, height);
		
		// ���e�ϊ��s��̒�`
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(-width / 200.0, width / 200.0, 
				  -height / 200.0, height / 200.0,
				  -1000.0, 1000.0);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		
	}
	


	/**
	 * �`������s����
	 */
	public void display(GLAutoDrawable drawable) {
		float lightcolor[] = {0.8f, 0.8f, 0.8f};
		
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		if(tree == null) return;
		
		// ���_�ʒu������
		gl2.glLoadIdentity();
		glu.gluLookAt( centerX, centerY, (centerZ + 20.0),
		           centerX, centerY, centerZ,
		           0.0, 1.0, 0.0 );

		shiftX = trans.getViewShift(0);
		shiftY = trans.getViewShift(1);
		scale = trans.getViewScale() * windowWidth / (size * 300.0);
		angleX = trans.getViewRotateY() * 45.0;
		angleY = trans.getViewRotateX() * 45.0;

		// �s����v�b�V��
		gl2.glPushMatrix();
		
		// �������񌴓_�����ɕ��̂𓮂���
		gl2.glTranslated(centerX, centerY, centerZ);
		
		// �}�E�X�̈ړ��ʂɉ����ĉ�]
		gl2.glRotated(angleX, 1.0, 0.0, 0.0);
		gl2.glRotated(angleY, 0.0, 1.0, 0.0); 

		// �}�E�X�̈ړ��ʂɉ����Ċg��k��
		gl2.glScaled(scale, scale, 1.0);
		
		// �}�E�X�̈ړ��ʂɉ����Ĉړ�
		gl2.glTranslated((shiftX * 50.0), (shiftY * 50.0), 0.0);
		
		// ���̂����Ƃ̈ʒu�ɖ߂�
		gl2.glTranslated(-centerX, -centerY, -centerZ);
		
		// �ϊ��s��ƃr���[�|�[�g�̒l��ۑ�����
		gl2.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
		gl2.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview);
		gl2.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection);

		// �g��`��
		gl2.glDisable(GL2.GL_LIGHTING);
		Branch branch = tree.getRootBranch();
		drawBorders(branch);
		
		// �_�O���t��`��
		gl2.glEnable(GL2.GL_LIGHTING);
		gl2.glEnable(GL2.GL_LIGHT0);
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT_AND_DIFFUSE, lightcolor, 0);
		paintNodes(branch);
		gl2.glDisable(GL2.GL_LIGHTING);
		
		// �����N��`��
		drawLinks(tree);
		
		// ������`��
		gl2.glDisable(GL2.GL_DEPTH_TEST);
		writeAnnotation(branch);
		gl2.glEnable(GL2.GL_DEPTH_TEST);

		// �s����|�b�v
		gl2.glPopMatrix();
		
	}

	
	/**
	 * �_�O���t�̍������Z�o����
	 */
	double calcBarHeight(Node node) {
		double height = tg.calcNodeHeightValue(node);
		if (height < 0.0) height = 0.0;
		if (height > 1.0) height = 1.0;
		return (height * trans.getTreeSize() * 0.02);
	}
	
	
	/**
	 * �����N��`��
	 */
	void drawLinks(Tree tree) {
		//System.out.println("  drawLinks: numLink=" + tree.getLinkList().size());
		
		// �������\�����n�߂�
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		
		// �e�X�̃����N�ɂ���
		for(int i = 0; i < tree.getLinkList().size(); i++) {
			Link link = (Link) tree.getLinkList().elementAt(i);
			Node node1 = link.getNode1();
			Node node2 = link.getNode2();
			
			// ���[�_�̍��W�l�����肷��
			double x1 = node1.getX();
			double y1 = node1.getY();
			double z1 = node1.getZ() + calcBarHeight(node1);
			double x2 = node2.getX();
			double y2 = node2.getY();
			double z2 = node2.getZ() + calcBarHeight(node2);
			
			// ����`��
			gl2.glColor4d(0.7, 0.2, 0.7, 0.5);
			gl2.glBegin(GL.GL_LINES);
			gl2.glVertex3d(x1, y1, z1);
			gl2.glVertex3d(x2, y2, z2);
			gl2.glEnd();
		}
		
		// �������\������߂�
		gl.glDisable(GL.GL_BLEND);
	}
	
	
	/**
	 * �}�m�[�h�i�g�j��`��
	 */
	void drawBorders(Branch branch) {
		
		// �e�m�[�h����g�̑傫�����Z�o����
		Node parentNode = branch.getParentNode();
		double xmax = parentNode.getX() + parentNode.getWidth();
		double xmin = parentNode.getX() - parentNode.getWidth();
		double ymax = parentNode.getY() + parentNode.getHeight();
		double ymin = parentNode.getY() - parentNode.getHeight();
		double z = parentNode.getZ();
			
		// �g��`��
		gl2.glColor3d(0.5, 0.5, 0.5);
		gl2.glBegin(GL.GL_LINE_LOOP);
		gl2.glVertex3d(xmin, ymin, z);
		gl2.glVertex3d(xmax, ymin, z);
		gl2.glVertex3d(xmax, ymax, z);
		gl2.glVertex3d(xmin, ymax, z);
		gl2.glEnd();
		
		// �}�m�[�h�̉��ɂ���e�m�[�h�ɂ��āF
		//   �ċA�I�ɘg��`��
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() != null) 
				drawBorders(node.getChildBranch());
		}
		
	}
	
	/**
	 * �t�m�[�h�i�_�O���t�E�A�C�R���j��h��Ԃ�
	 */
	void paintNodes(Branch branch) {
		float cvalue[] = new float[4];
		
		// �}�m�[�h�̉��ɂ���e�m�[�h�ɂ��āF
		//   �ċA�I�ɘg��`��
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() != null)
				continue;
			
			// �_�O���t�̒��_���W�l���Z�o
			double xmax = node.getX() + node.getWidth();
			double xmin = node.getX() - node.getWidth();
			double ymax = node.getY() + node.getHeight();
			double ymin = node.getY() - node.getHeight();
			double zmax = node.getZ() + calcBarHeight(node);
			double zmin = node.getZ();
			

			
			// �_�O���t�̐F���Z�o
			Color color = tg.calcNodeColor(node, cc);
			if (color == Color.black)
				continue;
			cvalue[0] = (float)color.getRed()   / 255.0f;
			cvalue[1] = (float)color.getGreen() / 255.0f;
			cvalue[2] = (float)color.getBlue()  / 255.0f;
			cvalue[3] = 1.0f;
			gl2.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, cvalue, 0);
			
			// 1���ڂ̒����`
			gl2.glBegin(GL2.GL_POLYGON);
			gl2.glNormal3d(0.0, 0.0, 1.0);
			gl2.glVertex3d(xmax, ymin, zmax);
			gl2.glVertex3d(xmax, ymax, zmax);
			gl2.glVertex3d(xmin, ymax, zmax);
			gl2.glVertex3d(xmin, ymin, zmax);
			gl2.glEnd();
			
			// 2���ڂ̒����`
			gl2.glBegin(GL2.GL_POLYGON);
			gl2.glNormal3d(0.0, 0.0, -1.0);
			gl2.glVertex3d(xmax, ymin, zmin);
			gl2.glVertex3d(xmax, ymax, zmin);
			gl2.glVertex3d(xmin, ymax, zmin);
			gl2.glVertex3d(xmin, ymin, zmin);
			gl2.glEnd();
			
			// 3���ڂ̒����`
			gl2.glBegin(GL2.GL_POLYGON);
			gl2.glNormal3d(0.0, 1.0, 0.0);
			gl2.glVertex3d(xmax, ymax, zmax);
			gl2.glVertex3d(xmin, ymax, zmax);
			gl2.glVertex3d(xmin, ymax, zmin);
			gl2.glVertex3d(xmax, ymax, zmin);
			gl2.glEnd();
			
			// 4���ڂ̒����`
			gl2.glBegin(GL2.GL_POLYGON);
			gl2.glNormal3d(0.0, -1.0, 0.0);
			gl2.glVertex3d(xmax, ymin, zmax);
			gl2.glVertex3d(xmax, ymin, zmin);
			gl2.glVertex3d(xmin, ymin, zmin);
			gl2.glVertex3d(xmin, ymin, zmax);
			gl2.glEnd();
			
			// 5���ڂ̒����`
			gl2.glBegin(GL2.GL_POLYGON);
			gl2.glNormal3d(1.0, 0.0, 0.0);
			gl2.glVertex3d(xmax, ymax, zmax);
			gl2.glVertex3d(xmax, ymin, zmax);
			gl2.glVertex3d(xmax, ymin, zmin);
			gl2.glVertex3d(xmax, ymax, zmin);
			gl2.glEnd();
			
			// 6���ڂ̒����`
			gl2.glBegin(GL2.GL_POLYGON);
			gl2.glNormal3d(-1.0, 0.0, 0.0);
			gl2.glVertex3d(xmin, ymax, zmax);
			gl2.glVertex3d(xmin, ymax, zmin);
			gl2.glVertex3d(xmin, ymin, zmin);
			gl2.glVertex3d(xmin, ymin, zmax);
			gl2.glEnd();

		}
		
		
		// �}�m�[�h�̉��ɂ���e�m�[�h�ɂ��āF
		//   �ċA�I�ɘg��`��
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() != null) 
				paintNodes(node.getChildBranch());
		}
		
	}
	

	
	
	/**
	 * �s�b�N���ꂽ���̂�Ԃ�
	 */
	public Node getPickedNode() {
		return pickedNode;
	}
	
	
	/**
	 * ���̂��s�b�N����
	 * @param px �s�b�N�������̂̉�ʏ��x���W�l
	 * @param py �s�b�N�������̂̉�ʏ�̍��W�l
	 */
	public void pickObjects(int px, int py) {
		if (tree == null || trans == null)
			return;

		minDepth = 1.0e+30;
		pickedNode = null;
		py = viewport.get(3) - py + 1;
		
		// �g�A�A�C�R���Ƃ̓��O����ɂ�蕨�̂���肷��
		pickBorders(tree.getRootBranch(), px, py);
		pickNodes(tree.getRootBranch(), px, py);
	}

	
	/**
	 * Branch���E�����s�b�N����
	 * @param branch Branch
	 * @param px �s�b�N�������̂̉�ʏ��x���W�l
	 * @param py �s�b�N�������̂̉�ʏ�̍��W�l
	 */
	public Node pickBorders(Branch branch, int px, int py) {
		Node parentNode = branch.getParentNode();
		boolean flag = false;

		// �g�̒��_���W�l�̎Z�o
		double xmax = parentNode.getX() + parentNode.getWidth();
		double xmin = parentNode.getX() - parentNode.getWidth();
		double ymax = parentNode.getY() + parentNode.getHeight();
		double ymin = parentNode.getY() - parentNode.getHeight();
		double z = parentNode.getZ();
		
		// �g�Ƃ̓��O����
		glu2.gluProject(xmax, ymax, z, modelview, projection, viewport, p1);
		glu2.gluProject(xmax, ymin, z, modelview, projection, viewport, p2);
		glu2.gluProject(xmin, ymin, z, modelview, projection, viewport, p3);
		glu2.gluProject(xmin, ymax, z, modelview, projection, viewport, p4);
		flag = du.isInside(px, py, p1, p2, p3, p4);
		if (flag == true) {
			pickedNode = parentNode;
		}
		
		// �e�X�̎q�m�[�h�ɑ΂���
		for (int i = 1; i <= branch.getNodeList().size(); i++) {
			Node node = branch.getNodeAt(i);
			Branch childBranch = node.getChildBranch();
			if (childBranch != null)
				pickedNode = pickBorders(childBranch, px, py);
		}

		return pickedNode;
	}

	/**
	 * Node���s�b�N����
	 * @param px �s�b�N�������̂̉�ʏ��x���W�l
	 * @param py �s�b�N�������̂̉�ʏ�̍��W�l
	 */
	public Node pickNodes(Branch branch, int px, int py) {

		boolean flag = false;
		
		// �e�X�̎q�m�[�h�ɑ΂���
		for (int i = 1; i <= branch.getNodeList().size(); i++) {
			Node node = branch.getNodeAt(i);
			if(node.getChildBranch() != null) {
				pickedNode = pickNodes(node.getChildBranch(), px, py);
				continue;
			}
			
			// �_�O���t�̒��_���W�l���Z�o
			double xmax = node.getX() + node.getWidth();
			double xmin = node.getX() - node.getWidth();
			double ymax = node.getY() + node.getHeight();
			double ymin = node.getY() - node.getHeight();
			double zmax = node.getZ() + calcBarHeight(node);
			double zmin = node.getZ();

			double height = tg.calcNodeHeightValue(node);
			if (height < 0.0) continue;
			if (height > 1.0) height = 1.0;
			zmax += (height * trans.getTreeSize() * 0.02);
			
			// 1�ڂ̒����`�Ƃ̓��O����
			glu2.gluProject(xmax, ymax, zmax, modelview, projection, viewport, p1);
			glu2.gluProject(xmax, ymin, zmax, modelview, projection, viewport, p2);
			glu2.gluProject(xmin, ymin, zmax, modelview, projection, viewport, p3);
			glu2.gluProject(xmin, ymax, zmax, modelview, projection, viewport, p4);
			flag = du.isInside(px, py, p1, p2, p3, p4);
			
			/*
			System.out.println("px=" + px + " py=" + py);
			System.out.println("   p1=" + p1.get(0) + "," + p1.get(1));
			System.out.println("   p2=" + p2.get(0) + "," + p2.get(1));
			System.out.println("   p3=" + p3.get(0) + "," + p3.get(1));
			System.out.println("   p4=" + p4.get(0) + "," + p4.get(1));
			*/
			
			if (flag == true) {
				double zave = (p1.get(2) + p2.get(2) + p3.get(2) + p4.get(2)) * 0.25;
				if(zave < minDepth) {
					minDepth = zave;  pickedNode = node;
				}
			}
				
			// 2�ڂ̒����`�Ƃ̓��O����
			glu2.gluProject(xmax, ymax, zmin, modelview, projection, viewport, p1);
			glu2.gluProject(xmax, ymin, zmin, modelview, projection, viewport, p2);
			glu2.gluProject(xmin, ymin, zmin, modelview, projection, viewport, p3);
			glu2.gluProject(xmin, ymax, zmin, modelview, projection, viewport, p4);
			flag = du.isInside(px, py, p1, p2, p3, p4);
			if (flag == true) {
				double zave = (p1.get(2) + p2.get(2) + p3.get(2) + p4.get(2)) * 0.25;
				if(zave < minDepth) {
					minDepth = zave;  pickedNode = node;
				}
			}


			// 3�ڂ̒����`�Ƃ̓��O����
			glu2.gluProject(xmax, ymax, zmax, modelview, projection, viewport, p1);
			glu2.gluProject(xmax, ymax, zmin, modelview, projection, viewport, p2);
			glu2.gluProject(xmin, ymax, zmin, modelview, projection, viewport, p3);
			glu2.gluProject(xmin, ymax, zmax, modelview, projection, viewport, p4);
			flag = du.isInside(px, py, p1, p2, p3, p4);
			if (flag == true) {
				double zave = (p1.get(2) + p2.get(2) + p3.get(2) + p4.get(2)) * 0.25;
				if(zave < minDepth) {
					minDepth = zave;  pickedNode = node;
				}
			}
			
			// 4�ڂ̒����`�Ƃ̓��O����
			glu2.gluProject(xmax, ymin, zmax, modelview, projection, viewport, p1);
			glu2.gluProject(xmax, ymin, zmin, modelview, projection, viewport, p2);
			glu2.gluProject(xmin, ymin, zmin, modelview, projection, viewport, p3);
			glu2.gluProject(xmin, ymin, zmax, modelview, projection, viewport, p4);
			flag = du.isInside(px, py, p1, p2, p3, p4);
			if (flag == true) {
				double zave = (p1.get(2) + p2.get(2) + p3.get(2) + p4.get(2)) * 0.25;
				if(zave < minDepth) {
					minDepth = zave;  pickedNode = node;
				}
			}
			
			// 5�ڂ̒����`�Ƃ̓��O����
			glu2.gluProject(xmax, ymin, zmax, modelview, projection, viewport, p1);
			glu2.gluProject(xmax, ymin, zmin, modelview, projection, viewport, p2);
			glu2.gluProject(xmax, ymax, zmin, modelview, projection, viewport, p3);
			glu2.gluProject(xmax, ymax, zmax, modelview, projection, viewport, p4);
			flag = du.isInside(px, py, p1, p2, p3, p4);
			if (flag == true) {
				double zave = (p1.get(2) + p2.get(2) + p3.get(2) + p4.get(2)) * 0.25;
				if(zave < minDepth) {
					minDepth = zave;  pickedNode = node;
				}
			}
			
			// 6�ڂ̒����`�Ƃ̓��O����
			glu2.gluProject(xmin, ymin, zmax, modelview, projection, viewport, p1);
			glu2.gluProject(xmin, ymin, zmin, modelview, projection, viewport, p2);
			glu2.gluProject(xmin, ymax, zmin, modelview, projection, viewport, p3);
			glu2.gluProject(xmin, ymax, zmax, modelview, projection, viewport, p4);
			flag = du.isInside(px, py, p1, p2, p3, p4);
			if (flag == true) {
				double zave = (p1.get(2) + p2.get(2) + p3.get(2) + p4.get(2)) * 0.25;
				if(zave < minDepth) {
					minDepth = zave;  pickedNode = node;
				}
			}
			
		}

		return pickedNode;
	}

	
	
	/**
	 * ��������`��
	 */
	void writeAnnotation(Branch branch) {
		writePickedNodeAnnotation();
	}
	
	
	void writePickedNodeAnnotation() {
		if(pickedNode == null) return;
		
		p1.put(0, (pickedNode.getX() - pickedNode.getWidth()));
		p1.put(1, (pickedNode.getY() - pickedNode.getHeight())); 
		p1.put(2, (pickedNode.getZ() + calcBarHeight(pickedNode) + 0.01));
 
		String word = tg.getNodeAttributeName(pickedNode, tg.getNameType());
		if(word == null || word.length() <= 0)
			return;
        gl2.glColor3d(1.0, 0.0, 1.0);
		writeOneString(p1, word);

	}

	/**
	 * 1�̕������`�悷��
	 * @param str �`�悳��镶����
	 * @param font_id �t�H���gID
	 * @param color_id �J���[�e�[�u��ID
	 */
	void writeOneString(DoubleBuffer pos, String word) {
		gl2.glRasterPos3d(pos.get(0), pos.get(1), pos.get(2));
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, word);
	}
	
	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
}
