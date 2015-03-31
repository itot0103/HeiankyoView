package org.heiankyoview2.applet.junihitoeview;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.draw.*;

import java.util.*;

/**
 * HeianViewの描画領域への表示内容をバッファする
 * @author itot
 */
public class DefaultBuffer implements org.heiankyoview2.core.draw.Buffer {
	Tree tree;
	TreeTable tg;
	Branch rootDisplayBranch = null;
	Node pickedNode = null;

	Vector annotationList;

	/**
	 * アノテーションを格納するクラス
	 * @author itot
	 */
	public class Annotation {
		String annotation;
		double size;
		double position[];

		/**
		 * Constructor
		 */
		public Annotation() {
			position = new double[3];
		}

		/**
		 * アノテーションのサイズをセットする
		 * @param size アノテーションのサイズ
		 */
		public void setSize(double size) {
			this.size = size;
		}
		
		/**
		 * アノテーションのサイズを返す
		 * @return アノテーションのサイズ
		 */
		public double getSize() {
			return size;
		}

		/**
		 * アノテーションの文字列をセットする
		 * @param annotation アノテーションの文字列
		 */
		public void setAnnotation(String annotation) {
			this.annotation = annotation;
		}
		
		/**
		 * アノテーションの文字列を返す
		 * @return アノテーションの文字列
		 */
		public String getAnnotation() {
			return annotation;
		}

		/**
		 * アノテーションの座標値をセットする
		 * @param position アノテーションの座標値
		 */
		public void setPosition(double position[]) {
			this.position[0] = position[0];
			this.position[1] = position[1];
			this.position[2] = position[2];
		}
		
		/**
		 * アノテーションの座標値を返す
		 * @return アノテーションの座標値
		 */
		public double[] getPosition() {
			return this.position;
		}
	}

	/**
	 * Constructor
	 */
	public DefaultBuffer() {
		pickedNode = null;
		annotationList = new Vector();
	}

	/**
	 * Treeをセットする
	 * @param tree Tree
	 */
	public void setTree(Tree newTree) {
		tree = newTree;
		tg = tree.table;
		rootDisplayBranch = tree.getRootBranch();

		addBranchAnnotations();
	}


	/**
	 * Treeの全体を表示するか、あるNodeの下位階層だけを表示するか、をセットする
	 * @param sw 表示を切り替えるための数値（0:全体、1:ピックしたNodeの下位階層のみ、2:現在の1階層上まで）
	 */
	public void updatePartialDisplay(int sw) {

		//
		// display whole the tree
		//
		if (sw == 0) {
			rootDisplayBranch = tree.getRootBranch();
		}

		//
		// display only under the clicked branch
		//
		if (sw == 1) {
			Node pnode = getPickedNode();

			//
			// cannot display partial, so display original
			//
			if (pnode == null)
				return;
			Branch pbranch = pnode.getChildBranch();
			if (pbranch == null)
				return;

			//
			// switch whole to partial
			//
			else {
				rootDisplayBranch = pbranch;
				pickedNode = null;
			}
		}

		//
		// display only under the parent branch
		//
		if (sw == 2) {

			//
			// if currently whole the branch is displayed
			//
			if (rootDisplayBranch == tree.getRootBranch())
				return;

			//
			// switch to the upper branch
			//
			else {
				rootDisplayBranch =
					rootDisplayBranch.getParentNode().getCurrentBranch();
				pickedNode = null;
			}
		}

	}

	/**
	 * グラフの表示上の根branchを返す
	 * @return 表示上の根Branch
	 */
	public Branch getRootDisplayBranch() {
		return rootDisplayBranch;
	}


	/**
	 * アノテーションを1個追加する
	 * @param name アノテーションの文字列
	 * @param size アノテーションのサイズ
	 * @param position アノテーションの座標値（実座標系で）
	 */
	void addOneAnnotation(String name, double size, double position[]) {

		int j;
		Annotation a, a2;

		if (name == null || name.length() <= 0)
			return;

		//
		// Insert the node into the sorted list
		//  (It should be replaced by faster implementation) 
		//
		for (j = (annotationList.size() - 1); j >= 0; j--) {
			a = (Annotation) annotationList.elementAt(j);
			if (size > a.getSize())
				continue;

			if (j < 100000) {
				a2 = new Annotation();
				a2.setAnnotation(name);
				a2.setSize(size);
				a2.setPosition(position);
				annotationList.insertElementAt(a2, (j + 1));
			}
			break;

		}
		if (j < 0) {
			a2 = new Annotation();
			a2.setAnnotation(name);
			a2.setSize(size);
			a2.setPosition(position);
			annotationList.insertElementAt(a2, 0);
		}
	}

	/**
	 * アノテーションの表示対象となるNodeを1個追加する
	 * @param node アノテーションの表示対象となるNode
	 */
	void addAnnotatedOneNode(Node node) {

		String name = null;
		
		if(node.getChildBranch() != null)
			name = tg.getNodeAttributeName(node, 0);
		else
			name = tg.getNodeAttributeName(node, 1);
		
		if (name == null || name.length() <= 0) {
			name = node.getChildBranch().getName();
		}
		if (name == null || name.length() <= 0 || name.startsWith("null")) {
			return;
		}

		double size = node.getWidth() * node.getHeight();
		double position[] = new double[3];
		position[0] = node.getX() - node.getWidth();
		position[1] = node.getY() - node.getHeight();
		position[2] = node.getZ();

		addOneAnnotation(name, size, position);
	}

	/**
	 * Branchを構成するNodeに対して、アノテーションの表示対象となるNodeを追加する
	 * @param branch Branch
	 * @param flag trueならば子Branchを追跡する
	 */
	void addAnnotatedNodesOneBranch(Branch branch, boolean flag) {

		int i;

		if (branch == rootDisplayBranch) {
			flag = true;
			addAnnotatedOneNode(branch.getParentNode());
		}

		//
		// for each node in the branch
		//
		for (i = 1; i <= branch.getNodeList().size(); i++) {
			if (flag == false)
				break;
			Node node = branch.getNodeAt(i);
			if (node.getChildBranch() == null)
				continue;
			addAnnotatedOneNode(node);
		}

		//
		// for each child branch of this branch
		//
		for (i = 1; i <= branch.getNodeList().size(); i++) {
			Node node = branch.getNodeAt(i);
			if (node.getChildBranch() != null) {
				addAnnotatedNodesOneBranch(node.getChildBranch(), flag);
			}
		}

	}

	/**
	 * 各Branchをたどり、アノテーションの表示対象となるNodeを追加する
	 */
	public void addBranchAnnotations() {

		if (tree == null)
			return;
		annotationList.clear();

		Branch branch = tree.getRootBranch();

		if (branch == rootDisplayBranch)
			addAnnotatedNodesOneBranch(branch, true);
		else
			addAnnotatedNodesOneBranch(branch, false);
	}


	/**
	 * アノテーションリスト上のIDを入力して、それに対応するアノテーションの文字列を返す
	 * @param id アノテーションリスト上のID
	 * @return アノテーションの文字列
	 */
	public String getAnnotationName(int id) {
		if (annotationList.size() <= 0)
			return " ";
		if (id >= annotationList.size())
			id = annotationList.size() - 1;
		Annotation a = (Annotation) annotationList.elementAt(id);
		return a.getAnnotation();
	}

	/**
	 * アノテーションリスト上のIDを入力して、それに対応するアノテーションの座標値を返す
	 * @param id アノテーションリスト上のID
	 * @return アノテーションの座標値
	 */
	public double[] getAnnotationPosition(int id) {
		if (annotationList.size() <= 0) {
			double[] ret = { 0.0, 0.0, 0.0 };
			return ret;
		}
		if (id >= annotationList.size())
			id = annotationList.size() - 1;
		Annotation a = (Annotation) annotationList.elementAt(id);
		return a.getPosition();
	}

	/**
	 * アノテーションリスト上のIDを入力して、それに対応するアノテーションのサイズを返す
	 * @param id アノテーションリスト上のID
	 * @return アノテーションのサイズ
	 */
	public double getAnnotationSize(int id) {
		if (annotationList.size() <= 0)
			return 0.0;
		if (id >= annotationList.size())
			id = annotationList.size() - 1;
		Annotation a = (Annotation) annotationList.elementAt(id);
		return a.getSize();
	}

	/**
	 * ピックされたNodeを返す
	 * @return ピックされたNode
	 */
	public Node getPickedNode() {
		return pickedNode;
	}

	/**
	 * ピックされたNodeをセットする
	 * @param node ピックされたNode
	 */
	public void setPickedNode(Node node) {
		pickedNode = node;
	}

	/**
	 * アノテーションの総数を返す
	 * @return アノテーションの総数
	 */
	public int getNumAnnotation() {
		return annotationList.size();
	}

}
