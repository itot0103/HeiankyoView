package org.heiankyoview2.applet.junihitoeview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import javax.media.opengl.GL2;

import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.draw.Buffer;
import org.heiankyoview2.core.draw.DrawerUtility;
import org.heiankyoview2.core.draw.Transformer;


public class NodePainter1 {
	Tree tree;
	TreeTable tg;
	DrawerUtility du;
	org.heiankyoview2.core.draw.Transformer viewAwt;
	org.heiankyoview2.core.gldraw.Transformer viewGl;
	DefaultDrawer drawer;
	Node nodearray[];
	
	int targetTables[];
	int imageSize[];
	double p1[], p2[], p3[], p4[];
	int calc = 0;      // calc : 0 -> 最大値, 1 -> 最小値
	int nodelevel;
	int counter, numdim, ii, jj;
	double wmin, wmax, hmax, hmin, xmax, xmin, ymax, ymin;
	Color color;
	double scale;
	boolean isLod = false;
	
	
	static final double NODE_MAGNITUDE = 1.15;
	
	public NodePainter1() {
	}
	
	/**
	 * Treeをセットする
	 * @param tree
	 * @param nodearray
	 */
	public void setTree(Tree tree, DrawerUtility du, Node nodearray[], int imageSize[]) {
		this.tree = tree;
		tg = tree.table;
		this.du = du;
		this.nodearray = nodearray;
		this.imageSize = imageSize;
		
	}
	
	
	/**
	 * 描画対象となるテーブルのリストをセットする
	 */
	public void setTargetTables(int[] t) {
		targetTables = t;
	}
	
	
	/**
	 * Nodeの1側面（または1底面）を塗りつぶす
	 */
	void paintOneNodeFaceAwt(Graphics2D g2, Color color, double brightness) {

		int r1 = (int) (color.getRed() * brightness);
		if (r1 > 255)
			r1 = 255;
		int g1 = (int) (color.getGreen() * brightness);
		if (g1 > 255)
			g1 = 255;
		int b1 = (int) (color.getBlue() * brightness);
		if (b1 > 255)
			b1 = 255;

		g2.setPaint(new Color(r1, g1, b1));

		GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);

		polygon.moveTo((int) p1[0], (int) p1[1]);
		polygon.lineTo((int) p2[0], (int) p2[1]);
		polygon.lineTo((int) p3[0], (int) p3[1]);
		polygon.lineTo((int) p4[0], (int) p4[1]);
		polygon.closePath();
		g2.fill(polygon);

	}

	/**
	 * Nodeの1側面（または1底面）を塗りつぶす
	 */
	void paintOneNodeFaceGl(GL2 gl2, Color color, double brightness) {
		float cvalue[] = new float[4];
		
		cvalue[0] = (float)color.getRed()   / 255.0f;
		cvalue[1] = (float)color.getGreen() / 255.0f;
		cvalue[2] = (float)color.getBlue()  / 255.0f;
		cvalue[3] = 1.0f;
		gl2.glColor3f(cvalue[0], cvalue[1], cvalue[2]);
		//gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE, cvalue, 0);
		
		// 1枚目の長方形
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glNormal3d(0.0, 0.0, 1.0);
		gl2.glVertex3d(p1[0], p1[1], p1[2]);
		gl2.glVertex3d(p2[0], p2[1], p2[2]);
		gl2.glVertex3d(p3[0], p3[1], p3[2]);
		gl2.glVertex3d(p4[0], p4[1], p4[2]);
		gl2.glEnd();
	
	}
	
	
	/**
	 * Nodeを塗りつぶす
	 * 
	 * @param g2
	 *            Graphics2D
	 */
	public void paintNodes(Graphics2D g2, GL2 gl2, double scale) {
		Node node;
		counter = 0;
		this.scale = scale;
		
		if(gl2 != null) {
			p1 = new double[3];
			p2 = new double[3];
			p3 = new double[3];
			p4 = new double[3];
		}
		
		// 描画のたびにnodearrayを更新する
		if (nodelevel == 0)
			nodelevel = 1;
		
		if (nodelevel == 1){
			if(g2 != null)
				nodearray[0] = tree.getRootNode();
			counter++;
		}
		if(g2 != null) {
			if (nodelevel > 1){
				Branch rbranch = tree.getRootBranch();
				rearrangeNodearray(rbranch, nodelevel);
			}
			for (int i = counter; i < nodearray.length; i++)
				nodearray[i] = null;
		}

		numdim = targetTables.length;
		ii = (int) (Math.sqrt((double) numdim)) + 1;
		jj = (int) (numdim / ii) + 1;
		
		
		if(g2 != null) {
			paintNodesAwt(g2);
		}
		else {
			paintNodesGl(gl2, tree.getRootBranch());
		}
	}
	
	
	/**
	 * Nodeの描画（AWT版）
	 */
	void paintNodesAwt(Graphics2D g2) {
		Node node;
		
		wmin = 0; hmin = 0;
		int flag = 0;
		
		// Nodeを奥行き順に
		for (int k = 0; k < nodearray.length; k++) {
			node = nodearray[k];
			if (node == null) continue;
				
			double width = node.getWidth();
			double height = node.getHeight();
				
			if (flag == 0){
				wmin = width;
				hmin = height;
				flag = 1;
			}
			else{
				if (wmin > width)
					wmin = width;
				if (hmin > height)
					hmin = height;
			}
		}
			
		// Nodeを奥行き順に
		for (int k = 0; k < nodearray.length; k++) {
			node = nodearray[k];
			if (node == null) continue;
			paintOneNode(node, g2, null);
		}
	}
	
	/**
	 * Nodeの描画（GL版）
	 */
	void paintNodesGl(GL2 gl2, Branch branch) {
		Node node;
		
		if(branch == tree.getRootBranch()) {
			wmin = 0; hmin = 0;
			int flag = 0;
			
			for (int k = 0; k < branch.getNodeList().size(); k++) {
				node = (Node)branch.getNodeList().elementAt(k);
				double width = node.getWidth();
				double height = node.getHeight();
					
				if (flag == 0){
					wmin = width;
					hmin = height;
					flag = 1;
				}
				else{
					if (wmin > width)
						wmin = width;
					if (hmin > height)
						hmin = height;
				}
			}	
		}
		
		for (int k = 0; k < branch.getNodeList().size(); k++) {
			node = (Node)branch.getNodeList().elementAt(k);
			Branch cbranch = node.getChildBranch();
			int level = branch.getLevel();
			
			if(isLod == true) {
				if (cbranch != null && level < nodelevel - 1)
					paintNodesGl(gl2, cbranch);
				
				if ((cbranch != null && level == nodelevel - 1)
						|| (cbranch == null && level <= nodelevel - 1))
					paintOneNode(node, null, gl2);
			}
			else {
				if (cbranch != null)
					paintNodesGl(gl2, cbranch);
				else
					paintOneNode(node, null, gl2);
			}
		}
	}
	
	
	/**
	 * 1個のNodeを描画する
	 */
	void paintOneNode(Node node, Graphics2D g2, GL2 gl2) {
		double z, x1, x2, y1, y2;
		
		
		// 表示するアイコンが葉ノードのときは NODE_MAGNITUDE をかける
		if (node.getChildBranch() == null){
			xmax = node.getX() + node.getWidth() * NODE_MAGNITUDE;
			xmin = node.getX() - node.getWidth() * NODE_MAGNITUDE;
			ymax = node.getY() + node.getHeight() * NODE_MAGNITUDE;
			ymin = node.getY() - node.getHeight() * NODE_MAGNITUDE;
			z = node.getZ(); // + node.getDepth();
		}
		else{
			xmax = node.getX() + wmin;
			xmin = node.getX() - wmin;
			ymax = node.getY() + hmin;
			ymin = node.getY() - hmin;
			z = node.getZ(); // + node.getDepth();
		} 
			
		int n = 0;
		for (int i = 0; i < ii; i++) {
			y1 = ymin + (ymax - ymin) * i / ii;
			y2 = ymin + (ymax - ymin) * (i + 1) / ii;
			for (int j = 0; j < jj; j++, n++) {
				if (n >= numdim)
					break;
				x1 = xmin + (xmax - xmin) * j / jj;
				x2 = xmin + (xmax - xmin) * (j + 1) / jj;

				double value = 0.0;
				Branch branch = node.getChildBranch();
				if (branch == null){
					Table table = tg.getTable(targetTables[n] + 1);
					int id = node.table.getId(targetTables[n] + 1);
					value = table.getAppearanceValue(id - 1);
				}
				else if (calc == 0)
					value = maximum(node, n, value, 0);
				else if (calc == 1)
					value = minimum(node, n, value, 0);
										
				double hue = (double)n / (double)numdim;
					
				if (value < 0.5)
					color = drawOneAreaNegative(g2, x1, x2, y1, y2, z, value, hue);
				else
					color = drawOneAreaPositive(g2, x1, x2, y1, y2, z, value, hue);

				if(g2 != null)
					paintOneNodeFaceAwt(g2, color, 1.0);
				else
					paintOneNodeFaceGl(gl2, color, 1.0);
				
			}
			if (n >= numdim) break;
		}
		
	}

	
	// nodelevel で指定された深さに nodearray を並びかえる
	void rearrangeNodearray(Branch branch, int nodelevel){
		for (int i = 1; i <= branch.getNodeList().size(); i++){
			Node node = branch.getNodeAt(i);
			Branch cbranch = node.getChildBranch();
			int level = branch.getLevel();
			
			if (cbranch != null && level < nodelevel - 1)
				rearrangeNodearray(cbranch, nodelevel);
				
			if ((cbranch != null && level == nodelevel - 1)
					|| (cbranch == null && level <= nodelevel - 1))
				nodearray[counter++] = node;
		}
	}
	
	// nodelevelの値を返す
	public int getNodeLevel(){
		nodelevel = (int) (scale * (float) imageSize[1] * 0.009f);
		return nodelevel;
	}

	
	//最大値を求める
	double maximum(Node node, int n, double max, int counter){
		Branch branch = node.getChildBranch();
		int size = branch.getNodeList().size();
		
		for (int i = 1; i <= size; i++) {
			Node cnode = branch.getNodeAt(i);
			Branch cbranch = cnode.getChildBranch();
			double value;
			
			if (cbranch != null)
				value = maximum(cnode, n, max, counter);
			
			else {
				NodeTablePointer tn = cnode.table;			
				Table table = tg.getTable(targetTables[n] + 1);
				int id = tn.getId(targetTables[n] + 1);
        	
				value = table.getAppearanceValue(id - 1);
			}
			
			if (value > max || counter == 0)
				max = value;
			
			counter++;
       		}
		
		return max;
	}	
	
//	最小値を求める
	double minimum(Node node, int n, double min, int counter){
		Branch branch = node.getChildBranch();
		int size = branch.getNodeList().size();
		double value;
		
		for (int i = 1; i <= size; i++) {
			Node cnode = branch.getNodeAt(i);
			Branch cbranch = cnode.getChildBranch();

			if (cbranch != null)
				value = minimum(cnode, n, min, counter);
			
			else {
				NodeTablePointer tn = cnode.table;			
				Table table = tg.getTable(targetTables[n] + 1);
				int id = tn.getId(targetTables[n] + 1);
        	
				value = table.getAppearanceValue(id - 1);
			}
			
			if (value < min || counter == 0)
				min = value;
				counter++;
       		}
			
		return min;
	}	
	
	
	/**
	 * 正値のときの形状と色を求める
	 */
	Color drawOneAreaPositive(Graphics2D g2, 
			double x1, double x2, double y1, double y2,
			double z, double value, double hue) {
		
		if(g2 != null) {
			p1 = du.transformPosition(x2, y2, z, 1);
			p2 = du.transformPosition(x2, y1, z, 2);
			p3 = du.transformPosition(x1, y1, z, 3);
			p4 = du.transformPosition(x1, y2, z, 4);
		}
		else {
			p1[0] = p2[0] = x2;
			p3[0] = p4[0] = x1;
			p1[1] = p4[1] = y2;
			p2[1] = p3[1] = y1;
			p1[2] = p2[2] = p3[2] = p4[2] = z;
		}
			
		double saturation = -1.0 + 2.0 * value;
		if (saturation < 0.0)
			saturation = 0.0;
		if (saturation > 1.0)
			saturation = 1.0;
		double brightness = saturation = 0.2 + 0.8 * saturation;
		Color color = Color.getHSBColor((float) hue, (float) saturation,
				(float) brightness);
		return color;
	}

	
	/**
	 * 負値のときの形状と色を求める
	 */
	Color drawOneAreaNegative(Graphics2D g2, 
		double x1, double x2, double y1, double y2,
		double z, double value, double hue) {
		double xc = 0.5 * (x1 + x2);
		double yc = 0.5 * (y1 + y2);
		
		if(g2 != null) {
			p1 = du.transformPosition(x2, yc, z, 1);
			p2 = du.transformPosition(xc, y1, z, 2);
			p3 = du.transformPosition(x1, yc, z, 3);
			p4 = du.transformPosition(xc, y2, z, 4);
		}
		else {
			p1[0] = x2;   p3[0] = x1;
			p2[0] = p4[0] = xc;
			p1[1] = p3[1] = yc;
			p2[1] = y1;   p4[1] = y2;
			p1[2] = p2[2] = p3[2] = p4[2] = z;	
		}
		
		
		double saturation = 1.0 - 2.0 * value;
		if (saturation < 0.0)
			saturation = 0.0;
		if (saturation > 1.0)
			saturation = 1.0;
		double brightness = saturation = 0.2 + 0.8 * saturation;
		Color color = Color.getHSBColor((float) hue, (float) saturation,
				(float) brightness);
		return color;
	}
	
	/**
	 * Nodeをピックする
	 * 
	 * @param px
	 *            ピックした物体の画面上のx座標値
	 * @param py
	 *            ピックした物体の画面上の座標値
	 */
	public Node pickNodes(boolean isAwt, int px, int py) {

		double xmax, ymax, xmin, ymin, z;
		boolean flag = false;
		Node node, pickedNode = null;

		//
		// Definition of picks, and polygon drawing
		//
		for (int i = 0; i < nodearray.length; i++) {
			node = nodearray[i];
			if (node == null)
				continue;

			//
			// calculate positions of vertices of the node
			//
			xmax = node.getX() + node.getWidth() * NODE_MAGNITUDE;
			xmin = node.getX() - node.getWidth() * NODE_MAGNITUDE;
			ymax = node.getY() + node.getHeight() * NODE_MAGNITUDE;
			ymin = node.getY() - node.getHeight() * NODE_MAGNITUDE;
			z = node.getZ(); // + node.getDepth();

			if(isAwt == true) {
				p1 = du.transformPosition(xmax, ymax, z, 1);
				p2 = du.transformPosition(xmax, ymin, z, 2);
				p3 = du.transformPosition(xmin, ymin, z, 3);
				p4 = du.transformPosition(xmin, ymax, z, 4);
			}
			else {
				p1[0] = p2[0] = xmax;
				p3[0] = p4[0] = xmin;
				p1[1] = p4[1] = ymax;
				p2[1] = p3[1] = ymin;
				p1[2] = p2[2] = p3[2] = p4[2] = z;
			}
			flag = du.isInside(px, py, p1, p2, p3, p4);
			if (flag == true)
				pickedNode = node;

		}

		return pickedNode;
	}

}
