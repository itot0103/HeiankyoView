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
import org.heiankyoview2.core.draw.DrawerUtility;


public class NodePainter2 {
	Tree tree;
	TreeTable tg;
	DefaultDrawer drawer;
	Node nodearray[], nodearrayOrg[];
	DrawerUtility du;
	int targetTables[];
	double p1[], p2[], p3[], p4[];
	int calc = 2;	// calc : 1 -> 1つの属性値を表示, 2 -> 2つの属性値を表示
	int calc1 = 1;   	// 0 -> 最大値, 1 -> 最小値
	int calc2[] = {0, 1};
	int imageSize[];
	int nodelevel;
	int counter1, counter2;
	double value;
	int numdim, ii, jj;
	double wmin, wmax, hmax, hmin, xmax, xmin, ymax, ymin;
	//Color color, color1, color2;
	boolean isLod = false;
	double scale, lodCoefficient = 0.001;

	int NUM_DIVIDE = 3;
	static final double NODE_MAGNITUDE = 1.25;
	
	public NodePainter2() {	
	}
	
	/**
	 * Treeをセットする
	 * @param tree
	 * @param nodearray
	 */
	public void setTree(Tree tree, DrawerUtility du, Node na[], int imageSize[]) {
		this.tree = tree;
		tg = tree.table;
		this.du = du;
		this.nodearrayOrg = na;
		this.imageSize = imageSize;
		
		if(du != null) {
			nodearray = new Node[na.length];
			for(int i = 0; i < na.length; i++) 
				nodearray[i] = na[i];
		}
	}
	
	
	/**
	 * 描画対象となるテーブルのリストをセットする
	 */
	public void setTargetTables(int[] t) {
		targetTables = t;
	}
	
	/**
	 * LODのON/OFF設定を行う
	 */
	public void setLod(boolean isAwt, boolean input) {
		isLod = input;
		
		if(input == false && isAwt == true) {
			for(int i = 0; i < nodearrayOrg.length; i++) 
				nodearray[i] = nodearrayOrg[i];
		}
	}
	
	public void setLodCoefficient(double coef) {
		lodCoefficient = coef;
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
	 * Nodeの1側面（または1底面）を塗りつぶす
	 */
	void paintOneNodeFace2Awt(Graphics2D g2, double x1, double x2, double y1, double y2, double z,
			Color colors[], int numbers[], double brightness) {
		int sum = numbers[0] + numbers[1] + numbers[2];	
		if(sum == 0) return;

		double ratio3 = (double)numbers[0] / (double)sum;
		double x3 = x1 * (1.0 - ratio3) + x2 * ratio3;
		double y3 = y1 * (1.0 - ratio3) + y2 * ratio3;
		
		double ratio4 = (double)numbers[2] / (double)sum;
		double x4 = x1 * ratio4 + x2 * (1.0 - ratio4);
		double y4 = y1 * ratio4 + y2 * (1.0 - ratio4);
		
		// 上の三角形を塗りつぶす
		int r11 = (int) (colors[0].getRed() * brightness);
		if (r11 > 255) r11 = 255;
		int g11 = (int) (colors[0].getGreen() * brightness);
		if (g11 > 255) g11 = 255;
		int b11 = (int) (colors[0].getBlue() * brightness);
		if (b11 > 255) b11 = 255;
		g2.setPaint(new Color(r11, g11, b11));
		p1 = du.transformPosition(x1, y1, z, 1);
		p2 = du.transformPosition(x2, y1, z, 2);
		p3 = du.transformPosition(x3, y3, z, 3);
		p4 = du.transformPosition(x1, y2, z, 4);
		GeneralPath polygon1 = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
		polygon1.moveTo((int) p1[0], (int) p1[1]);
		polygon1.lineTo((int) p2[0], (int) p2[1]);
		polygon1.lineTo((int) p3[0], (int) p3[1]);
		polygon1.closePath();
		g2.fill(polygon1);
		polygon1.moveTo((int) p1[0], (int) p1[1]);
		polygon1.lineTo((int) p3[0], (int) p3[1]);
		polygon1.lineTo((int) p4[0], (int) p4[1]);
		polygon1.closePath();
		g2.fill(polygon1);

		// 中の三角形を塗りつぶす
		int r12 = (int) (colors[1].getRed() * brightness);
		if (r12 > 255) r12 = 255;
		int g12 = (int) (colors[1].getGreen() * brightness);
		if (g12 > 255) g12 = 255;
		int b12 = (int) (colors[1].getBlue() * brightness);
		if (b12 > 255) b12 = 255;
		g2.setPaint(new Color(r12, g12, b12));
		p1 = du.transformPosition(x3, y3, z, 1);
		p2 = du.transformPosition(x2, y1, z, 2);
		p3 = du.transformPosition(x4, y4, z, 3);
		p4 = du.transformPosition(x1, y2, z, 4);
		GeneralPath polygon2 = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
		polygon2.moveTo((int) p1[0], (int) p1[1]);
		polygon2.lineTo((int) p2[0], (int) p2[1]);
		polygon2.lineTo((int) p3[0], (int) p3[1]);
		polygon2.closePath();
		g2.fill(polygon2);
		polygon2.moveTo((int) p1[0], (int) p1[1]);
		polygon2.lineTo((int) p3[0], (int) p3[1]);
		polygon2.lineTo((int) p4[0], (int) p4[1]);
		polygon2.closePath();
		g2.fill(polygon2);
		
		// 下の三角形を塗りつぶす
		int r13 = (int) (colors[2].getRed() * brightness);
		if (r13 > 255) r13 = 255;
		int g13 = (int) (colors[2].getGreen() * brightness);
		if (g13 > 255) g13 = 255;
		int b13 = (int) (colors[2].getBlue() * brightness);
		if (b13 > 255) b13 = 255;
		g2.setPaint(new Color(r13, g13, b13));
		p1 = du.transformPosition(x4, y4, z, 1);
		p2 = du.transformPosition(x2, y1, z, 2);
		p3 = du.transformPosition(x2, y2, z, 3);
		p4 = du.transformPosition(x1, y2, z, 4);
		GeneralPath polygon3 = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 3);
		polygon3.moveTo((int) p1[0], (int) p1[1]);
		polygon3.lineTo((int) p2[0], (int) p2[1]);
		polygon3.lineTo((int) p3[0], (int) p3[1]);
		polygon3.closePath();
		g2.fill(polygon3);
		polygon3.moveTo((int) p1[0], (int) p1[1]);
		polygon3.lineTo((int) p3[0], (int) p3[1]);
		polygon3.lineTo((int) p4[0], (int) p4[1]);
		polygon3.closePath();
		g2.fill(polygon3);
	}
	
	
	
	/**
	 * Nodeの1側面（または1底面）を塗りつぶす
	 */
	void paintOneNodeFace2Gl(GL2 gl2, double x1, double x2, double y1, double y2, double z,
			Color colors[], int numbers[], double brightness) {
		float cvalue[] = new float[4];
		
		int sum = numbers[0] + numbers[1] + numbers[2];	
		if(sum == 0) return;

		double ratio3 = (double)numbers[0] / (double)sum;
		double x3 = x1 * (1.0 - ratio3) + x2 * ratio3;
		double y3 = y1 * (1.0 - ratio3) + y2 * ratio3;
		
		double ratio4 = (double)numbers[2] / (double)sum;
		double x4 = x1 * ratio4 + x2 * (1.0 - ratio4);
		double y4 = y1 * ratio4 + y2 * (1.0 - ratio4);
		
		// 上の三角形を塗りつぶす
		cvalue[0] = (float)colors[0].getRed()   / 255.0f;
		cvalue[1] = (float)colors[0].getGreen() / 255.0f;
		cvalue[2] = (float)colors[0].getBlue()  / 255.0f;
		gl2.glColor3f(cvalue[0], cvalue[1], cvalue[2]);

		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glNormal3d(0.0, 0.0, 1.0);
		gl2.glVertex3d(x1, y1, z);
		gl2.glVertex3d(x2, y1, z);
		gl2.glVertex3d(x3, y3, z);
		gl2.glEnd();	
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glNormal3d(0.0, 0.0, 1.0);
		gl2.glVertex3d(x1, y1, z);
		gl2.glVertex3d(x3, y3, z);
		gl2.glVertex3d(x1, y2, z);
		gl2.glEnd();		

		// 中の三角形を塗りつぶす
		cvalue[0] = (float)colors[1].getRed()   / 255.0f;
		cvalue[1] = (float)colors[1].getGreen() / 255.0f;
		cvalue[2] = (float)colors[1].getBlue()  / 255.0f;
		gl2.glColor3f(cvalue[0], cvalue[1], cvalue[2]);

		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glNormal3d(0.0, 0.0, 1.0);
		gl2.glVertex3d(x3, y3, z);
		gl2.glVertex3d(x2, y1, z);
		gl2.glVertex3d(x4, y4, z);
		gl2.glEnd();	
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glNormal3d(0.0, 0.0, 1.0);
		gl2.glVertex3d(x3, y3, z);
		gl2.glVertex3d(x4, y4, z);
		gl2.glVertex3d(x1, y2, z);
		gl2.glEnd();		
	
		// 下の三角形を塗りつぶす
		cvalue[0] = (float)colors[2].getRed()   / 255.0f;
		cvalue[1] = (float)colors[2].getGreen() / 255.0f;
		cvalue[2] = (float)colors[2].getBlue()  / 255.0f;
		gl2.glColor3f(cvalue[0], cvalue[1], cvalue[2]);

		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glNormal3d(0.0, 0.0, 1.0);
		gl2.glVertex3d(x4, y4, z);
		gl2.glVertex3d(x2, y1, z);
		gl2.glVertex3d(x2, y2, z);
		gl2.glEnd();	
		gl2.glBegin(GL2.GL_POLYGON);
		gl2.glNormal3d(0.0, 0.0, 1.0);
		gl2.glVertex3d(x4, y4, z);
		gl2.glVertex3d(x2, y2, z);
		gl2.glVertex3d(x1, y2, z);
		gl2.glEnd();		

	}
	

	
	/**
	 * Nodeを塗りつぶす
	 * 
	 * @param g2
	 *            Graphics2D
	 */
	public void paintNodes(Graphics2D g2, GL2 gl2, double scale) {
		this.scale = scale;
		counter1 = 0;
		counter2 = 0;

		if(gl2 != null) {
			p1 = new double[3];
			p2 = new double[3];
			p3 = new double[3];
			p4 = new double[3];
		}
		
		// 詳細度制御を適用するときには、描画のたびにnodearrayを更新する
		if(isLod == true) {
			nodelevel = getNodeLevel();
		
			if (nodelevel == 0)
				nodelevel = 1;
		
			if (nodelevel == 1){
				if(g2 != null)
					nodearray[0] = tree.getRootNode();
				counter1++;
			}
			
			if(g2 != null) {
				if (nodelevel > 1){
					Branch rbranch = tree.getRootBranch();
					rearrangeNodearray(rbranch, nodelevel);
				}
				for (int i = counter1; i < nodearray.length; i++)
					nodearray[i] = null;
			}	
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
	 * Nodeの描画（AWT版）
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
		Color color, colors[] = new Color[NUM_DIVIDE];
		int numbers[] = new int[NUM_DIVIDE];
		
		Branch cbranch = node.getChildBranch();
		double magnitude = NODE_MAGNITUDE;
		if (cbranch != null) magnitude = 1;
			
		//
		// calculate positions of vertices of the node
		//
		xmax = node.getX() + node.getWidth() * magnitude;
		xmin = node.getX() - node.getWidth() * magnitude;
		ymax = node.getY() + node.getHeight() * magnitude;
		ymin = node.getY() - node.getHeight() * magnitude;
		z = node.getZ(); // + node.getDepth();

		int n = 0;
		for (int i = 0; i < ii; i++) {
			y1 = ymin + (ymax - ymin) * i / ii;
			y2 = ymin + (ymax - ymin) * (i + 1) / ii;
			for (int j = 0; j < jj; j++, n++) {
				if (n >= numdim)
					break;
				x1 = xmin + (xmax - xmin) * j / jj;
				x2 = xmin + (xmax - xmin) * (j + 1) / jj;

				double hue = (double)n / (double)numdim;
				double value = 0.0;
				Branch branch = node.getChildBranch();
				if (branch == null){
					Table table = tg.getTable(targetTables[n] + 1);
					int id = node.table.getId(targetTables[n] + 1);
					value = table.getAppearanceValue(id - 1);
					
					color = drawOneArea(g2, x1, x2, y1, y2, z, value, hue);
					
					if(g2 != null)
						paintOneNodeFaceAwt(g2, color, 1.0);
					else
						paintOneNodeFaceGl(gl2, color, 1.0);
					
				}
				else if (calc == 1){
					if (calc1 == 0)
						value = maximum(node, n, value, counter2);
					else if (calc1 == 1)
						value = minimum(node, n, value, counter2);

					color = drawOneArea(g2, x1, x2, y1, y2, z, value, hue);

					if(g2 != null)
						paintOneNodeFaceAwt(g2, color, 1.0);
					else
						paintOneNodeFaceGl(gl2, color, 1.0);
	
				}
				else if (calc == 2){
					double value1 = 0.0, value2 = 0.0;

					if (calc2[0] == 0)
						value1 = maximum(node, n, value, counter2);
					else if (calc2[0] == 1)
						value1 = minimum(node, n, value, counter2);
					
					if (calc2[1] == 0)
						value2 = maximum(node, n, value, counter2);
					else if (calc2[1] == 1)
						value2 = minimum(node, n, value, counter2);

					colors[0] = calcColorOneArea(x1, x2, y1, y2, z, value1, hue);
					colors[2] = calcColorOneArea(x1, x2, y1, y2, z, value2, hue);
					colors[1] = calcColorOneArea(x1, x2, y1, y2, z, ((value1+value2)*0.5), hue);
					
					for(int ll = 0; ll < NUM_DIVIDE; ll++) 
						numbers[ll] = 0;
					calcNumbers(node, n, value1, value2, numbers);
					
					if(g2 != null)
						paintOneNodeFace2Awt(g2, x1, x2, y1, y2, z, colors, numbers, 1.0);
					else
						paintOneNodeFace2Gl(gl2, x1, x2, y1, y2, z, colors, numbers, 1.0);
				}	
			}
			if (n >= numdim)
				break;
		}
		
	}
	
	
	/**
	 * 最大値に近いノード、中間値に近いノード、最小値に近いノード、の個数をそれぞれ算出する
	 */ 
	void calcNumbers(Node node, int n, double vmax, double vmin, int[] numbers) {
		
		double th1 = (vmin * 2.0 + vmax) / 3.0;
		double th2 = (vmin + 2.0 * vmax) / 3.0;
		
		Branch branch = node.getChildBranch();
		int size = branch.getNodeList().size();
		double value;
		
		for (int i = 1; i <= size; i++) {
			Node cnode = branch.getNodeAt(i);
			Branch cbranch = cnode.getChildBranch();

			if (cbranch != null)
				calcNumbers(cnode, n, vmax, vmin, numbers);
			
			else {
				NodeTablePointer tt = cnode.table;			
				Table table = tg.getTable(targetTables[n] + 1);
				int id = tt.getId(targetTables[n] + 1);
        	
				value = table.getAppearanceValue(id - 1);
				if(value > th2) numbers[0]++;
				else if(value > th1) numbers[1]++;
				else numbers[2]++;
			}
		}
			
	}
	
	
	/**
	 * 色を算出する
	 */
	Color calcColorOneArea(double x1, double x2, double y1, double y2,
			double z, double value, double hue) {

		double saturation = value;
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
	 * nodelevel で指定された深さに nodearray を並びかえる
	 */
	void rearrangeNodearray(Branch branch, int nodelevel){
		for (int i = 1; i <= branch.getNodeList().size(); i++){
			Node node = branch.getNodeAt(i);
			Branch cbranch = node.getChildBranch();
			int level = branch.getLevel();
			
			if (cbranch != null && level < nodelevel - 1)
				rearrangeNodearray(cbranch, nodelevel);
				
			if ((cbranch != null && level == nodelevel - 1)
					|| (cbranch == null && level <= nodelevel - 1))
				nodearray[counter1++] = node;
		}
	}
	
	/**
	 * nodelevelの値を返す 
	 */
	public int getNodeLevel(){
		nodelevel = (int) (scale * (float) imageSize[1] * lodCoefficient);
		if(nodelevel < 2) nodelevel = 2;
		return nodelevel;
	}

	
	/**
	 * 最大値を求める
	 */
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
	
	/**
	 * 最小値を求める
	 */
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
	

	Color drawOneArea(Graphics2D g2, 
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
		
		double saturation = value;
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