
package org.heiankyoview2.applet.cat;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import javax.media.opengl.awt.GLCanvas;

import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.glwindow.*;


public class CursorListener implements MouseListener, MouseMotionListener, MouseWheelListener {

	CatCanvas canvas = null;
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
		canvas = (CatCanvas) c;
		glcanvas = (GLCanvas) glc;
		glcanvas.addMouseListener(this);
		glcanvas.addMouseMotionListener(this);
	}
	
	/**
	 * Treeをセットする
	 * @param g Tree
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
		
		if(canvas == null) return;
		if(glcanvas == null) return;
		
		tree = canvas.getTree();
		if(tree == null) return;
		
		int cX = e.getX();
		int cY = e.getY();
		int m = e.getModifiers();
		
		// 左ボタンの処理
		if((m & MouseEvent.BUTTON1_MASK) != 0) {
		
			canvas.pickObjects(cX, cY);
			Node node = canvas.getPickedNode();
			System.out.println("  clicked: x=" + cX + " y=" + cY + " node=" + node);
		
			if (node != null && e.getClickCount() > 1) { // double-clicked !
				if(viewingPanel.getDoubleClickFlag() == viewingPanel.DOUBLE_CLICK_PANEL) {
					nodeValuePanel = fileOpener.getNodeValuePanel();
					if(nodeValuePanel != null) {
						nodeValuePanel.setVisible(true);
						nodeValuePanel.setNode(node);
					}
				}
				if(viewingPanel.getDoubleClickFlag() == viewingPanel.DOUBLE_CLICK_BROWSER) {
					if(tg.getUrlType() >= 0) {
						String url = tg.getNodeAttributeName(
								node, tg.getUrlType());
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
		
		
		canvas.display();
	}

	/**
	 * マウスボタンが押されたことを検出するリスナー
	 */
	public void mousePressed(MouseEvent e) {
		
		if(canvas == null) return;
		if(glcanvas == null) return;

		totalR = 0;
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
		
		int cX = e.getX();
		int cY = e.getY();

		canvas.pickObjects(cX, cY);
		Node node = canvas.getPickedNode();
		if(node != null && node != prevNode) {
			prevNode = node; canvas.display();
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
		if(canvas == null) return;
		if(glcanvas == null) return;
		wheelCount++;

		canvas.mousePressed(initX, initY);
		int dragMode = canvas.getDragMode();
		canvas.setDragMode(1); // ZOOM mode
		int r = e.getWheelRotation();
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
