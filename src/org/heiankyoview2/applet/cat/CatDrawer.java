package org.heiankyoview2.applet.cat;

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
import org.heiankyoview2.core.gldraw.*;

import java.awt.image.*;
import java.net.URL;

import javax.imageio.*;

import java.awt.Color;
import java.io.File;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

public class CatDrawer implements GLEventListener, org.heiankyoview2.core.gldraw.Drawer {
	
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

	DoubleBuffer modelview = null, projection = null, p1, p2, p3, p4;
	IntBuffer viewport = null;
	
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

	boolean adjustImageAspect = false;

	int MAX_THREADS = 100;
	int numthreads = 0;
	
	/**
	 * Constructor
	 * @param width �`��̈�̕�
	 * @param height �`��̈�̍���
	 */
	public CatDrawer(int width, int height, GLCanvas c) {
		
		glcanvas = c;
		imageSize[0] = width;
		imageSize[1] = height;
		du = new DrawerUtility(width, height);
		
		
		p1 = DoubleBuffer.allocate(3);
		p2 = DoubleBuffer.allocate(3);
		p3 = DoubleBuffer.allocate(3);
		p4 = DoubleBuffer.allocate(3);
		
		glcanvas.addGLEventListener(this);
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
		//du.setTransformer(view);
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
	 * �����\���̃��[�h��ݒ肷��
	 * @param segmentMode (1:ORIGINAL  2:MODIFIED  3:CLUSTERED)
	 */
	public void setSegmentMode(int newMode) {
		segmentMode = newMode;
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
	 * �A�m�e�[�V�����\����ON/OFF����
	 * @param flag �\������Ȃ�true, �\�����Ȃ��Ȃ�false
	 */
	public void setAnnotationSwitch(boolean flag) {
		
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
		gl2.glEnable(GL2.GL_CULL_FACE);
		gl2.glDisable(GL2.GL_LIGHTING);

		// �e�N�X�`���֌W�p�����[�^
		gl2.glEnable(GL.GL_TEXTURE_2D);
		gl2.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl2.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl2.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl2.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl2.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
		gl2.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		
		gl2.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
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
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(-width / 200.0, width / 200.0, 
				  -height / 200.0, height / 200.0,
				  -1000.0, 1000.0);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		
		// �ĕ`��
		display(drawable);
	}
	


	/**
	 * �`������s����
	 */
	public void display(GLAutoDrawable drawable) {
	
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		if(tree == null) return;
		
		// ���_�ʒu������
		gl2.glLoadIdentity();
		glu.gluLookAt( centerX, centerY, (centerZ + 20.0),
			           centerX, centerY, centerZ,
			           0.0, 1.0, 0.0 );
		
		shiftX = trans.getViewShift(0);
		shiftY = trans.getViewShift(1);
		scale =  Math.abs(trans.getViewScale() * windowHeight / (size * 300.0));
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
		if(viewport == null) {
			viewport = IntBuffer.allocate(4);
			modelview = DoubleBuffer.allocate(16);
			projection = DoubleBuffer.allocate(16);
		}
		gl2.glGetIntegerv(GL2.GL_VIEWPORT, viewport);
		gl2.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview);
		gl2.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection);
		
		// ��ʃO���[�v����ċA�I�ɕ`��
		Branch branch = tree.getRootBranch();
		drawOneBranch(branch, 1);
		
		// ������`��
		gl2.glDisable(GL2.GL_DEPTH_TEST);
		writeAnnotation(branch);
		gl2.glEnable(GL2.GL_DEPTH_TEST);
		
		// �s����|�b�v
		gl2.glPopMatrix();
	}

	
	/**
	 * �`������s����
	 */
	void drawOneBranch(Branch branch, int level) {
		Branch cbranch;
		Node node;
		int i;


		// ���YGroup�̘g��`�悷��
		drawOneBorder(branch);

		// ���YGroup�ɑ�����eNode�ɂ��Ĕ���
		for (i = 0; i < branch.getNodeList().size(); i++) { 
			node = (Node)branch.getNodeList().elementAt(i);
			cbranch = node.getChildBranch();
			if (cbranch == null){
				paintOneNode(node);  continue;	
			}

			// �ɓ��ɂ������Ȏ����i���P�̗]�n����j
			if(shouldDrawLowerBranch(cbranch) == true) {
				drawOneBranch(cbranch, (level + 1));
			}
			else {
				paintOneNode(node);
			}
			
		}
	}

	
	/**
	 * ���ʃu�����`��T�����ׂ����ۂ��𔻒肷��
	 * @return
	 */
	boolean shouldDrawLowerBranch(Branch branch) {
		boolean ret = true;
		final int TOOSMALL = 20;
		
		
		
		// ��\�摜���Ȃ����true
		//if(branch.getParentNode().getImage() == null)
			//return true;
		
		// �u�����`���̊e�m�[�h�ɂ���
		for(int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node)branch.getNodeList().elementAt(i);
			double xmax = node.getX() + node.getWidth();
			double xmin = node.getX() - node.getWidth();
			double ymax = node.getY() + node.getHeight();
			double ymin = node.getY() - node.getHeight();
			double z = node.getZ();
			glu2.gluProject(xmax, ymax, z, modelview, projection, viewport, p1);
			glu2.gluProject(xmin, ymax, z, modelview, projection, viewport, p2);
			glu2.gluProject(xmin, ymin, z, modelview, projection, viewport, p3);
			glu2.gluProject(xmax, ymin, z, modelview, projection, viewport, p4);
			if(Math.abs(p1.get(0) - p2.get(0)) < TOOSMALL) return false;
			if(Math.abs(p1.get(1) - p3.get(1)) < TOOSMALL) return false;
		}
		
		return ret;
	}
	
	

	/**
	 * Group �̋��E����`�悷��
	 * @param group Group
	 * @param g2 Graphics2D
	 */
	void drawOneBorder(Branch branch) {
		Node pnode = branch.getParentNode();
			
		double xmax = pnode.getX() + pnode.getWidth();
		double xmin = pnode.getX() - pnode.getWidth();
		double ymax = pnode.getY() + pnode.getHeight();
		double ymin = pnode.getY() - pnode.getHeight();
		double z = pnode.getZ();
		
		// �g��`��
		gl2.glColor3d(0.5, 0.5, 0.5);
		gl2.glBegin(GL2.GL_LINE_LOOP);
		gl2.glVertex3d(xmin, ymin, z);
		gl2.glVertex3d(xmax, ymin, z);
		gl2.glVertex3d(xmax, ymax, z);
		gl2.glVertex3d(xmin, ymax, z);
		gl2.glEnd();	
	
	}

	/**
	 * Node�̒�ʂɉ摜��`�悷��
	 */
	void paintOneNode(Node node) {
		//final int TEXTUREWIDTH = 64;
		//final int TEXTUREHEIGHT = 64;
		
  		double pxmin = 1.0e+30;
  		double pymin = 1.0e+30;
  		double pxmax = -1.0e+30;
  		double pymax = -1.0e+30;
  
		double xmax = node.getX() + node.getWidth();
		double xmin = node.getX() - node.getWidth();
		double ymax = node.getY() + node.getHeight();
		double ymin = node.getY() - node.getHeight();
		double z = node.getZ();

		// 1�ڂ̒��_
  		glu2.gluProject(xmax, ymax, z, modelview, projection, viewport, p1);
		if(pxmin > p1.get(0)) pxmin = p1.get(0);
		if(pymin > p1.get(1)) pymin = p1.get(1);
		if(pxmax < p1.get(0)) pxmax = p1.get(0);
		if(pymax < p1.get(1)) pymax = p1.get(1);

		// 2�ڂ̒��_
  		glu2.gluProject(xmin, ymax, z, modelview, projection, viewport, p2);
		if(pxmin > p2.get(0)) pxmin = p2.get(0);
		if(pymin > p2.get(1)) pymin = p2.get(1);
		if(pxmax < p2.get(0)) pxmax = p2.get(0);
		if(pymax < p2.get(1)) pymax = p2.get(1);
		
		// 3�ڂ̒��_
  		glu2.gluProject(xmin, ymin, z, modelview, projection, viewport, p3);
		if(pxmin > p3.get(0)) pxmin = p3.get(0);
		if(pymin > p3.get(1)) pymin = p3.get(1);
		if(pxmax < p3.get(0)) pxmax = p3.get(0);
		if(pymax < p3.get(1)) pymax = p3.get(1);
		
		// 4�ڂ̒��_
  		glu2.gluProject(xmax, ymin, z, modelview, projection, viewport, p4);
		if(pxmin > p4.get(0)) pxmin = p4.get(0);
		if(pymin > p4.get(1)) pymin = p4.get(1);
		if(pxmax < p4.get(0)) pxmax = p4.get(0);
		if(pymax < p4.get(1)) pymax = p4.get(1);
		
		//�E�B���h�E�͈͓����ǂ����̔���
		if(pxmax < 0 || pymax < 0||
			pxmin >= windowWidth || pymin >= windowHeight){
				BufferedImage image = (BufferedImage)node.getImage();
				if(image != null) {
					image = null;  node.setImage(null);
				}
				return;
		}

		//�摜�̓ǂݍ���
		BufferedImage image = (BufferedImage)node.getImage();
		Texture texture = null;
		if(image == null && numthreads < MAX_THREADS) {
			ImageLoaderThread ilt = new ImageLoaderThread(node);
			numthreads++;
			ilt.start();
		}
			
		// �e�N�X�`���p�ɕϊ������摜�� �ݒ肷��
		texture = (Texture)node.getTexture();
		if(texture == null) {
			image = (BufferedImage)node.getImage();
			if(image == null) return;
			TextureData textureData = AWTTextureIO.newTextureData(gl.getGLProfile(), image, false);
			texture = TextureIO.newTexture(textureData);
			node.setTexture((Object)texture);
		}
		if(texture == null) return;
		
		// �C���[�W�T�C�Y�̕␳
		if(node.getChildBranch() != null && adjustImageAspect == true && image != null) {
			int width = image.getWidth();
			int height = image.getHeight();
			double aspect1 = (double)width / (double)height;
			double aspect2 = node.getWidth() / node.getHeight();
			if(aspect1 > aspect2) {
				double newheight = node.getWidth() / aspect1;
				ymax = node.getY() + newheight;
				ymin = node.getY() - newheight;
			}
			else {
				double newwidth = node.getHeight() * aspect1;
				xmax = node.getX() + newwidth;
				xmin = node.getX() - newwidth;
			}
			
		}
		
		// �s�b�N���ꂽ�ʐ^�̊g��\��
		if(pickedNode == node) {
			double magnitude = 2.0;
			double xc = (xmin + xmax) * 0.5;
			double xw = (xmax - xmin) * magnitude;
			xmin = xc - xw;
			xmax = xc + xw;
			double yc = (ymin + ymax) * 0.5;
			double yh = (ymax - ymin) * magnitude;
			ymin = yc - yh;
			ymax = yc + yh;
			z += 0.1;
		}
		
		// �e�N�X�`�����W�l�̐ݒ�
		texture.enable(gl2);
        texture.bind(gl2);
		gl2.glBegin(GL2.GL_QUADS);
		gl2.glTexCoord2d(0.0, 1.0);
		gl2.glVertex3d(xmin, ymin, z);
		gl2.glTexCoord2d(1.0, 1.0);
		gl2.glVertex3d(xmax, ymin, z);
		gl2.glTexCoord2d(1.0, 0.0);
		gl2.glVertex3d(xmax, ymax, z);
		gl2.glTexCoord2d(0.0, 0.0);
		gl2.glVertex3d(xmin, ymax, z);
		gl2.glEnd();
		texture.disable(gl2);
		
		// �e�N�X�`���摜�f�[�^�̓o�^�̏I��
		gl.glDisable(GL.GL_TEXTURE_2D);

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
			double z = node.getZ();

			// �����`�Ƃ̓��O����
			glu2.gluProject(xmax, ymax, z, modelview, projection, viewport, p1);
			glu2.gluProject(xmax, ymin, z, modelview, projection, viewport, p2);
			glu2.gluProject(xmin, ymin, z, modelview, projection, viewport, p3);
			glu2.gluProject(xmin, ymax, z, modelview, projection, viewport, p4);
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
		p1.put(2, (pickedNode.getZ() + 0.01));
 
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
	
	
	class ImageLoaderThread extends Thread {
		Node node;
		ImageLoaderThread(Node n) {
             this.node = n;
        }
 
         public void run() {
        	 BufferedImage image = null;
 			 try {
				String surl = node.getImageUrl();
				if(surl == null) return;
				if(surl.startsWith("http")) {
					URL url = new URL(surl);
					image = ImageIO.read(url);
				}
				else {
					image = ImageIO.read(new File(surl));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if(image == null) return;
			else node.setImage(image);
			numthreads--;

         }
	}
	
	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
}