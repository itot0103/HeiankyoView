package org.heiankyoview2.applet.junihitoeview;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.gldraw.Canvas;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.glwindow.*;

import java.lang.Exception;

import javax.media.opengl.GLCanvas;


public class GlCursorListener implements MouseListener, MouseMotionListener, MouseWheelListener {

	Canvas canvas = null;
	GLCanvas glcanvas = null;
	Tree tree = null;
	TreeTable tg = null;
	FileOpener fileOpener = null;
	ViewingPanel  viewingPanel = null;
	NodeValuePanel nodeValuePanel = null;
	int initX = 0, initY = 0, totalR = 0;
	long wheelCount = 0;
	Node prevNode = null;

	
	
	/**
	 * Canvasをセットする
	 * @param c Canvas
	 */
	public void setCanvas(Object c, Object glc) {
		canvas = (Canvas) c;
		glcanvas = (GLCanvas) glc;
		glcanvas.addMouseListener(this);
		glcanvas.addMouseMotionListener(this);
	}
	
	/**
	 * Treeをセットする
	 * @param t Tree
	 */
	public void setTree(Tree t) {
		tree = t;
		tg = tree.table;
	}
	
	/**
	 * ViewingPanelをセットする
	 * @param v ViewingPanel
	 */
	public void setViewingPanel(ViewingPanel v) {
		viewingPanel = v;
	}
	
	/**
	 * FileOpener をセットする
	 */
	public void setFileOpener(FileOpener fo) {
		fileOpener = fo;
	}


	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * マウスのクリックを検出するリスナー
	 */
	public void mouseClicked(MouseEvent e) {
		
		tree = canvas.getTree();
		if(canvas == null || tree == null) return;
		if(glcanvas == null) return;
		
		nodeValuePanel = fileOpener.getNodeValuePanel();
		int cX = e.getX();
		int cY = e.getY();
		int m = e.getModifiers();
		
		
		// 左ボタンの処理
		if ((m & MouseEvent.BUTTON1_MASK) != 0) {
			canvas.pickObjects(cX, cY);
			Node node = canvas.getPickedNode();
			if (node == null)
				return;
			canvas.display();

			if (e.getClickCount() > 1) { // double-clicked !
				if (viewingPanel.getDoubleClickFlag() == viewingPanel.DOUBLE_CLICK_PANEL) {
					if (node.getChildBranch() == null) {
						
						if (nodeValuePanel != null) {
							nodeValuePanel.setVisible(true);
							nodeValuePanel.setNode(node);
						}
					} else {
						/*
						BranchDialog sgp = new BranchDialog(tree, node
								.getChildBranch());
						sgp.setListData();
						sgp.setVisible(true);
						*/
					}
				}
				if (viewingPanel.getDoubleClickFlag() == viewingPanel.DOUBLE_CLICK_BROWSER) {
					tg = tree.table;
					if (tg.getUrlType() >= 0) {
						String url = tg.getNodeAttributeName(node, tg
								.getUrlType());
						Runtime rt = Runtime.getRuntime();
						System.out.println("url=" + url);
						try {
							rt.exec("iexplore " + url);
						} catch (Exception exp) {
							exp.printStackTrace();
						}
					}
				}
			}
		}
		
		// 右ボタンの処理
		if((m & MouseEvent.BUTTON3_MASK) != 0) {
			if (e.getClickCount() > 1) { // double-clicked !
				int width = (int)canvas.getWidth();
				int height = (int)canvas.getHeight();
				int dragMode = canvas.getDragMode();
				canvas.setDragMode(2); // SHIFT mode
				canvas.drag(cX, (width/2), cY, (height/2));
				canvas.setDragMode(dragMode);
				canvas.display();
			}
		}
		
	}

	/**
	 * マウスボタンが押されたことを検出するリスナー
	 */
	public void mousePressed(MouseEvent e) {
		
		if(canvas == null) return;
		if(glcanvas == null) return;
		initX = e.getX();
		initY = e.getY();
		canvas.mousePressed(initX, initY);
	}

	/**
	 * マウスボタンが離されたことを検出するリスナー
	 */
	public void mouseReleased(MouseEvent e) {
		
		if(canvas == null) return;
		if(glcanvas == null) return;
		canvas.mouseReleased();
		canvas.display();
	}

	/**
	 * マウスカーソルが動いたことを検出するリスナー
	 */
	public void mouseMoved(MouseEvent e) {
		
		if(canvas == null) return;
		if(glcanvas == null) return;
		if(viewingPanel.getCursorSensorFlag() == false) return;
		
		nodeValuePanel = fileOpener.getNodeValuePanel();
		int cX = e.getX();
		int cY = e.getY();

		canvas.pickObjects(cX, cY);
		Node node = canvas.getPickedNode();
		if(node != null && node != prevNode) {
			prevNode = node; canvas.display();
			nodeValuePanel.setNode(node);
		}
	}

	/**
	 * マウスカーソルをドラッグしたことを検出するリスナー
	 */
	public void mouseDragged(MouseEvent e) {

		if(canvas == null) return;
		if(glcanvas == null) return;
		int cX = e.getX();
		int cY = e.getY();
		
		// 右ボタンの処理
		int m = e.getModifiers();
		if((m & MouseEvent.BUTTON3_MASK) != 0) {
			int dragMode = canvas.getDragMode();
			canvas.setDragMode(2); // SHIFT mode
			canvas.drag(initX, cX, initY, cY);
			canvas.setDragMode(dragMode);
		}
		else {
			canvas.drag(initX, cX, initY, cY);
		}
		canvas.display();
	}
	
	/**
	 * マウスホイールの動きを検出するリスナー
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		System.out.println("  WheelEvent");
		if(canvas == null) return;
		if(glcanvas == null) return;
		wheelCount++;

		canvas.mousePressed(initX, initY);
		int dragMode = canvas.getDragMode();
		canvas.setDragMode(1); // ZOOM mode
		int r = e.getWheelRotation();
		System.out.println("   Wheel r=" + r);
		
		totalR -= (r * 20);
		canvas.drag(0, 0, 0, totalR);
		canvas.display();
		canvas.setDragMode(dragMode);
		WheelThread wt = new WheelThread(wheelCount);
		wt.start();
		
	}
	
	class WheelThread extends Thread {
		long count;
		WheelThread(long c) {
             this.count = c;
        }
 
         public void run() {
        	 try {
        		 Thread.sleep(500);
        	 } catch(Exception e) {
        	 	e.printStackTrace();
        	 }
        	 if(count != wheelCount) return;
        	 canvas.mouseReleased();
        	 canvas.display();
        	 totalR = 0;
         }
	}
}
