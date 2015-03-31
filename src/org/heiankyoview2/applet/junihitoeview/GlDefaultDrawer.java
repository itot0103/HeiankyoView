package org.heiankyoview2.applet.junihitoeview;

import java.awt.Color;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import com.sun.opengl.util.GLUT;

import org.heiankyoview2.core.tree.*;
import org.heiankyoview2.core.table.*;
import org.heiankyoview2.core.gldraw.*;


public class GlDefaultDrawer implements GLEventListener, org.heiankyoview2.core.gldraw.Drawer {

	private GL gl;
	private GLU glu;
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
	
	Node pickedNode = null;
	double minDepth;
	Node nodearray[];
	
	int targetTables[];
	int paintType = 2;

	int counter = 0;
	int nodelevel;// = 5;
	boolean isLod = false;

	NodePainter1 snp1 = new NodePainter1();
	NodePainter2 snp2 = new NodePainter2();
	
	/**
	 * Constructor
	 * @param width �`��̈�̕�
	 * @param height �`��̈�̍���
	 */
	public GlDefaultDrawer(int width, int height, GLCanvas c) {
		
		glcanvas = c;
		imageSize[0] = width;
		imageSize[1] = height;
		du = new DrawerUtility(width, height);
		
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
	public GlDefaultDrawer() {
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
	
	
	public void setLodCoefficient(double coef) {
		snp2.setLodCoefficient(coef);
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
	 * �`��ΏۂƂȂ�e�[�u���̃��X�g���Z�b�g����
	 */
	public void setTargetTables(int[] t) {
		targetTables = t;
		snp1.setTargetTables(t);
		snp2.setTargetTables(t);
	}
	
	/**
	 * paintType���Z�b�g����
	 */
	public void setPaintType(int t) {
		paintType = t;
	}
	
	/**
	 * LOD��ON/OFF�ݒ���s��
	 */
	public void setLod(boolean input) {
		snp2.setLod(false, input);
		isLod = input;
	}
	
	/**
	 * �w�肳�ꂽBranch���n�C���C�g����
	 */
	public void highlightSpecifiedBranch(Branch branch) {
		pickedNode = branch.getParentNode();
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
		
		snp1.setTree(tree, null, nodearray, imageSize);
		snp2.setTree(tree, null, nodearray, imageSize);
	}


	/**
	 * ������
	 */
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL();
		glu = new GLU();
		glut = new GLUT();
		this.glAD = drawable;
	
		gl.glEnable(GL.GL_RGBA);
		gl.glEnable(GL.GL_DEPTH);
		gl.glEnable(GL.GL_DOUBLE);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_NORMALIZE);
		gl.glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

	}
	
	
	/**
	 * �ĕ`��
	 */
	public void reshape(GLAutoDrawable drawable,
			int x, int y, int width, int height) {
		
		windowWidth = width;
		windowHeight = height;
	
		// �r���[�|�[�g�̒�`
		gl.glViewport(0, 0, width, height);
		
		// ���e�ϊ��s��̒�`
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-width / 200.0, width / 200.0, 
				  -height / 200.0, height / 200.0,
				  -1000.0, 1000.0);

		gl.glMatrixMode(GL.GL_MODELVIEW);
		
	}
	


	/**
	 * �`������s����
	 */
	public void display(GLAutoDrawable drawable) {
		float lightcolor[] = {1.0f, 1.0f, 1.0f};
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		if(tree == null) return;
		
		// ���_�ʒu������
		gl.glLoadIdentity();
		glu.gluLookAt( centerX, centerY, (centerZ + 20.0),
		           centerX, centerY, centerZ,
		           0.0, 1.0, 0.0 );

		shiftX = trans.getViewShift(0);
		shiftY = trans.getViewShift(1);
		scale = trans.getViewScale() * windowWidth / (size * 300.0);
		angleX = trans.getViewRotateY() * 45.0;
		angleY = trans.getViewRotateX() * 45.0;

		//System.out.println(scale + "," + trans.getViewScale());
		
		// �s����v�b�V��
		gl.glPushMatrix();
		
		// �������񌴓_�����ɕ��̂𓮂���
		gl.glTranslated(centerX, centerY, centerZ);
		
		// �}�E�X�̈ړ��ʂɉ����ĉ�]
		gl.glRotated(angleX, 1.0, 0.0, 0.0);
		gl.glRotated(angleY, 0.0, 1.0, 0.0); 

		// �}�E�X�̈ړ��ʂɉ����Ċg��k��
		gl.glScaled(scale, scale, 1.0);
		
		// �}�E�X�̈ړ��ʂɉ����Ĉړ�
		gl.glTranslated((shiftX * 50.0), (shiftY * 50.0), 0.0);
		
		// ���̂����Ƃ̈ʒu�ɖ߂�
		gl.glTranslated(-centerX, -centerY, -centerZ);
		
		// �ϊ��s��ƃr���[�|�[�g�̒l��ۑ�����
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, modelview);
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projection);

		// �g��`��
		gl.glDisable(GL.GL_LIGHTING);
		Branch branch = tree.getRootBranch();
		drawBorders(branch);
		
		// �_�O���t��`��
		//gl.glEnable(GL.GL_LIGHTING);
		//gl.glEnable(GL.GL_LIGHT0);
		//gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT_AND_DIFFUSE, lightcolor, 0);
		if(paintType == 1) snp1.paintNodes(null, gl, trans.getViewScale());
		if(paintType == 2) snp2.paintNodes(null, gl, trans.getViewScale());
		
		// ������`��
		gl.glDisable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_DEPTH_TEST);
		writeAnnotation(branch);
		gl.glEnable(GL.GL_DEPTH_TEST);
		
		// �s����|�b�v
		gl.glPopMatrix();
		
	}

	
	/**
	 * �_�O���t�̍������Z�o����
	 */
	double calcBarHeight(Node node) {
		double height = tg.calcNodeHeightValue(node);
		if (height < 0.0) height = 0.0;
		if (height > 1.0) height = 1.0;
		return (height * trans.getTreeSize() * 0.04);
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
		gl.glColor3d(0.5, 0.5, 0.5);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3d(xmin, ymin, z);
		gl.glVertex3d(xmax, ymin, z);
		gl.glVertex3d(xmax, ymax, z);
		gl.glVertex3d(xmin, ymax, z);
		gl.glEnd();
		
		// LOD�K�p���ɂ́A���ȏ�̐[���̘g�͕\�����Ȃ�
		if(paintType == 2 && isLod == true) {
			nodelevel = snp2.getNodeLevel();
			if (branch.getLevel() >= nodelevel)
				return;
		}
			
		// �}�m�[�h�̉��ɂ���e�m�[�h�ɂ��āF
		//   �ċA�I�ɘg��`��
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			if(node.getChildBranch() != null) 
				drawBorders(node.getChildBranch());
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
		glu.gluProject(xmax, ymax, z, modelview, projection, viewport, p1);
		glu.gluProject(xmax, ymin, z, modelview, projection, viewport, p2);
		glu.gluProject(xmin, ymin, z, modelview, projection, viewport, p3);
		glu.gluProject(xmin, ymax, z, modelview, projection, viewport, p4);
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
			glu.gluProject(xmax, ymax, zmax, modelview, projection, viewport, p1);
			glu.gluProject(xmax, ymin, zmax, modelview, projection, viewport, p2);
			glu.gluProject(xmin, ymin, zmax, modelview, projection, viewport, p3);
			glu.gluProject(xmin, ymax, zmax, modelview, projection, viewport, p4);
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
			glu.gluProject(xmax, ymax, zmin, modelview, projection, viewport, p1);
			glu.gluProject(xmax, ymin, zmin, modelview, projection, viewport, p2);
			glu.gluProject(xmin, ymin, zmin, modelview, projection, viewport, p3);
			glu.gluProject(xmin, ymax, zmin, modelview, projection, viewport, p4);
			flag = du.isInside(px, py, p1, p2, p3, p4);
			if (flag == true) {
				double zave = (p1.get(2) + p2.get(2) + p3.get(2) + p4.get(2)) * 0.25;
				if(zave < minDepth) {
					minDepth = zave;  pickedNode = node;
				}
			}


			// 3�ڂ̒����`�Ƃ̓��O����
			glu.gluProject(xmax, ymax, zmax, modelview, projection, viewport, p1);
			glu.gluProject(xmax, ymax, zmin, modelview, projection, viewport, p2);
			glu.gluProject(xmin, ymax, zmin, modelview, projection, viewport, p3);
			glu.gluProject(xmin, ymax, zmax, modelview, projection, viewport, p4);
			flag = du.isInside(px, py, p1, p2, p3, p4);
			if (flag == true) {
				double zave = (p1.get(2) + p2.get(2) + p3.get(2) + p4.get(2)) * 0.25;
				if(zave < minDepth) {
					minDepth = zave;  pickedNode = node;
				}
			}
			
			// 4�ڂ̒����`�Ƃ̓��O����
			glu.gluProject(xmax, ymin, zmax, modelview, projection, viewport, p1);
			glu.gluProject(xmax, ymin, zmin, modelview, projection, viewport, p2);
			glu.gluProject(xmin, ymin, zmin, modelview, projection, viewport, p3);
			glu.gluProject(xmin, ymin, zmax, modelview, projection, viewport, p4);
			flag = du.isInside(px, py, p1, p2, p3, p4);
			if (flag == true) {
				double zave = (p1.get(2) + p2.get(2) + p3.get(2) + p4.get(2)) * 0.25;
				if(zave < minDepth) {
					minDepth = zave;  pickedNode = node;
				}
			}
			
			// 5�ڂ̒����`�Ƃ̓��O����
			glu.gluProject(xmax, ymin, zmax, modelview, projection, viewport, p1);
			glu.gluProject(xmax, ymin, zmin, modelview, projection, viewport, p2);
			glu.gluProject(xmax, ymax, zmin, modelview, projection, viewport, p3);
			glu.gluProject(xmax, ymax, zmax, modelview, projection, viewport, p4);
			flag = du.isInside(px, py, p1, p2, p3, p4);
			if (flag == true) {
				double zave = (p1.get(2) + p2.get(2) + p3.get(2) + p4.get(2)) * 0.25;
				if(zave < minDepth) {
					minDepth = zave;  pickedNode = node;
				}
			}
			
			// 6�ڂ̒����`�Ƃ̓��O����
			glu.gluProject(xmin, ymin, zmax, modelview, projection, viewport, p1);
			glu.gluProject(xmin, ymin, zmin, modelview, projection, viewport, p2);
			glu.gluProject(xmin, ymax, zmin, modelview, projection, viewport, p3);
			glu.gluProject(xmin, ymax, zmax, modelview, projection, viewport, p4);
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
        gl.glColor3d(1.0, 0.0, 1.0);
		writeOneString(p1, word);

	}

	/**
	 * 1�̕������`�悷��
	 * @param str �`�悳��镶����
	 * @param font_id �t�H���gID
	 * @param color_id �J���[�e�[�u��ID
	 */
	void writeOneString(DoubleBuffer pos, String word) {
		gl.glRasterPos3d(pos.get(0), pos.get(1), pos.get(2));
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, word);
	}
}
