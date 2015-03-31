package org.heiankyoview2.core.draw;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.TreeTable;

import java.util.*;

/**
 * 描画処理のクラス
 * @author itot
 */
public class DrawerUtility {

	Tree tree = null;
	TreeTable tg;
	Transformer view = null;
	Buffer dbuf = null;

	Node nodearray[];
	int numNode;

	double p1[] = new double[3];
	double p2[] = new double[3];
	double p3[] = new double[3];
	double p4[] = new double[3];
	int imageSize[] = new int[2];

	Node pickedNode = null;

	DepthComparator dcomp;
	TreeSet treeSet;

	/**
	 * Constructor
	 * @param width 描画領域の幅
	 * @param height 描画領域の高さ
	 */
	public DrawerUtility(int width, int height) {
		imageSize[0] = width;
		imageSize[1] = height;
	}

	/**
	 * Treeをセットする
	 * @param tree Tree
	 */
	public void setTree(Tree tree) {
		this.tree = tree;
		tg = tree.table;
	}

	/**
	 * Buffer をセットする
	 * @param dbuf Buffer
	 */
	public void setBuffer(Buffer dbuf) {
		this.dbuf = dbuf;
	}

	/**
	 * Viewをセットする
	 * @param view View
	 */
	public void setTransformer(Transformer view) {
		this.view = view;
	}

	/**
	 * 描画領域のサイズを設定する
	 * @param width 描画領域の幅
	 * @param height 描画領域の高さ
	 */
	public void setWindowSize(int width, int height) {
		imageSize[0] = width;
		imageSize[1] = height;
	}

	/**
	 * 塗りつぶす物体面がオモテかウラかを判定する
	 * @param px 画面上のx座標値
	 * @param py 画面上のy座標値
	 * @param e1 1個目の頂点の座標値
	 * @param e2 2個目の頂点の座標値
	 * @return オモテならtrue
	 */
	int whichSide(int px, int py, double e1[], double e2[]) {
		double a = (e1[1] - (double) py) * (e2[0] - (double) px);
		double b = (e1[0] - (double) px) * (e2[1] - (double) py);
		if (a > b)
			return -1;
		if (a < b)
			return 1;

		return 0;
	}

	/**
	 * ピックした位置が四角形面の内部かを判定する
	 * @param px 画面上のx座標値
	 * @param py 画面上のy座標値
	 * @param pp1 1個目の頂点の座標値
	 * @param pp2 2個目の頂点の座標値
	 * @param pp3 3個目の頂点の座標値
	 * @param pp4 4個目の頂点の座標値
	 * @return 内部ならtrue
	 */
	public boolean isInside(
		int px,
		int py,
		double pp1[],
		double pp2[],
		double pp3[],
		double pp4[]) {

		boolean flag1 = false, flag2 = false;
		int ret;

		ret = whichSide(px, py, pp1, pp2);
		if (ret > 0)
			flag1 = true;
		if (ret < 0)
			flag2 = true;

		ret = whichSide(px, py, pp2, pp3);
		if (ret > 0)
			flag1 = true;
		if (ret < 0)
			flag2 = true;

		ret = whichSide(px, py, pp3, pp4);
		if (ret > 0)
			flag1 = true;
		if (ret < 0)
			flag2 = true;

		ret = whichSide(px, py, pp4, pp1);
		if (ret > 0)
			flag1 = true;
		if (ret < 0)
			flag2 = true;

		if (flag1 == false || flag2 == false)
			return true;
		return false;
	}

	/**
	 * 座標値を変換する
	 * @param x 実座標系のx座標値
	 * @param y 実座標系のy座標値
	 * @param z 実座標系のz座標値
	 * @param select 四角形の頂点を特定するID（1～4のいずれかの整数）
	 */
	public double[] transformPosition(
		double x,
		double y,
		double z,
		int select) {
		double pos[];

		pos = p1;
		if (select == 2)
			pos = p2;
		if (select == 3)
			pos = p3;
		if (select == 4)
			pos = p4;

		//
		// Centering
		//
		if (view.getTreeSize() > 0.0000001) {
			x = (x - view.getTreeCenter(0)) / view.getTreeSize();
			y = (y - view.getTreeCenter(1)) / view.getTreeSize();
			z = (z - view.getTreeCenter(2)) / view.getTreeSize();
		}

		//
		// Fit to the image size
		//
		int wh = (imageSize[0] < imageSize[1]) ? imageSize[0] : imageSize[1];
		wh /= 2;
		x *= wh;
		y *= wh;
		z *= wh;

		//
		// Rotation & Shift
		//
		pos[0] =
			x * view.getViewRotate(0)
				+ y * view.getViewRotate(1)
				+ z * view.getViewRotate(2);
//				+ view.getViewShift(0);
		pos[1] =
			x * view.getViewRotate(4)
				+ y * view.getViewRotate(5)
				+ z * view.getViewRotate(6);
//				+ view.getViewShift(1);
		pos[2] =
			x * view.getViewRotate(8)
				+ y * view.getViewRotate(9)
				+ z * view.getViewRotate(10);
//				+ view.getViewShift(2);
		
		
		//
		// Scaling
		//
		pos[0] = pos[0] * view.getViewScale() + view.getViewShift(0) + 0.5 * (double) imageSize[0];
		pos[1] = pos[1] * view.getViewScale() + view.getViewShift(1) + 0.5 * (double) imageSize[1];
		pos[2] = pos[2] * view.getViewScale();

		return pos;
	}

	/**
	 * 視点からの深さでソートしたNode配列を生成する （奥行きソート法による描画のため）
	 */
	public Node[] createSortedNodeArray() {

		dcomp = new DepthComparator();
		treeSet = new TreeSet(dcomp);


		if (dbuf.getRootDisplayBranch() != null)
			depthSortNodes(dbuf.getRootDisplayBranch());
		else
			depthSortNodes(tree.getRootBranch());

		//
		// Copy the sorted nodes to an array
		//
		numNode = treeSet.size();
		if (numNode <= 0)
			return null;
		nodearray = new Node[numNode];

		for (int i = 0; i < numNode; i++) {
			Object obj = treeSet.first();
			treeSet.remove(obj);
			nodearray[i] = (Node) obj;
		}

		return nodearray;
	}

	/**
	 * Nodeを視点からの深さでソートする
	 * @param branch Branch
	 */
	void depthSortNodes(Branch branch) {
		Branch childBranch;
		Node node;
		int i;

		//
		// for each (NON-PARENT) node:
		//     Insert nodes into TreeSet
		//
		for (i = 1; i <= branch.getNodeList().size(); i++) {
			node = branch.getNodeAt(i);
			childBranch = node.getChildBranch();
			if (childBranch != null)
				continue;
			treeSet.add((Object) node);
		}

		//
		// for each (PARENT) node:
		//     Recursive call for child branches
		//
		for (i = 1; i <= branch.getNodeList().size(); i++) {
			node = branch.getNodeAt(i);
			childBranch = node.getChildBranch();
			if (childBranch == null)
				continue;
			depthSortNodes(childBranch);
		}
	}

	/**
	 * Nodeの視点からの深さを管理するクラス
	 * @author itot
	 */
	class DepthComparator implements Comparator {

		/**
		 * Constructor
		 */
		public DepthComparator() {
		}

		/**
		 * 2つのNodeの深さを比較する
		 * @param obj1 1個目のNode
		 * @param obj2 2個目のNode
		 */
		public int compare(Object obj1, Object obj2) {

			Node node1 = (Node) obj1;
			Node node2 = (Node) obj2;

			double d1 =
				node1.getX() * view.getViewRotate(8)
					+ node1.getY() * view.getViewRotate(9)
					+ node1.getZ() * view.getViewRotate(10);
			double d2 =
				node2.getX() * view.getViewRotate(8)
					+ node2.getY() * view.getViewRotate(9)
					+ node2.getZ() * view.getViewRotate(10);

			if (d1 - d2 > 0.000001)
				return 1;
			if (d2 - d1 > 0.000001)
				return -1;

			int v1 = node1.getId() + node1.getCurrentBranch().getId() * 10000;
			int v2 = node2.getId() + node2.getCurrentBranch().getId() * 10000;
			int sub = v1 - v2;

			return sub;
		}

	}

}
