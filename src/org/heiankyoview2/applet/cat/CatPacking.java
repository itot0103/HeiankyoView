package org.heiankyoview2.applet.cat;

import java.util.*;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.placement.*;

/**
 * Treeの画面配置を行う
 * @author itot
 */
public class CatPacking {
	Tree tree = null, templateTree = null;
	long mill1, mill2;

	PackingGrid pg;

	/* public values for evaluation */
	public double templateErrorAverage, templateErrorMax, templateErrorMin; 
	public double aspectAverage = 0.0, spaceAverage = 0.0;

	
	/**
	 * Constructor
	 */
	public CatPacking() {
	}

	/**
	 * Treeを構成する全てのNodeを画面配置する
	 * @param tree Tree
	 * @param templateTree テンプレートTree
	 * @return Tree
	 */
	public Tree placeNodesAllBranch(Tree tree) {

		this.tree = tree;
		templateTree = tree.getTemplateTree();
		
		Vector branchList = new Vector();
		Branch rootBranch = tree.getRootBranch();
		Branch branch, templateBranch;

		branchList.add((Object) rootBranch);

		mill1 = System.currentTimeMillis();

		//
		// Traverse the hierarchy of branches
		//     starting from the root branch
		//
		int i = 0;
		while (i < branchList.size()) {
			branch = (Branch) branchList.elementAt(i++);

			//
			// for each node of the branch:
			//     Search for child beanches
			//
			for (int j = 1; j <= branch.getNodeList().size(); j++) {
				Node node = branch.getNodeAt(j);
				Branch childBranch = node.getChildBranch();
				if (childBranch != null) {
					branchList.add((Object) childBranch);
				}
			}
		}

		//
		// for each branch (in the inverse order of traverse)
		//     Place nodes in each branch
		//
		for (i = branchList.size() - 1; i >= 0; i--) {
			branch = (Branch) branchList.elementAt(i);
			placeNodesOneBranch(tree, branch, templateTree);
		}

		mill2 = System.currentTimeMillis();
		return tree;
	}



	/**
	 * 親Branchの配置にともない子Branchの配置を平行移動する
	 * @param branch Branch
	 * @param shift 平行移動量
	 */
	void moveChildBranches(Branch branch, double shift[]) {

		double shift2[];
		int i;

		//
		// for each node:
		//     move due to the movement of the parent node
		//
		for (i = 1; i <= branch.getNodeList().size(); i++) {
			Node node = branch.getNodeAt(i);

			if (shift != null) {
				node.setX(node.getX() + shift[0]);
				node.setY(node.getY() + shift[1]);
				node.setZ(node.getZ() + shift[2]);
				node.setNX(node.getNX() + shift[0]);
				node.setNY(node.getNY() + shift[1]);
				node.setNZ(node.getNZ() + shift[2]);

			}

			//
			// Recursive call
			//       for the child branch of the node
			//
			Branch childBranch = node.getChildBranch();
			if (childBranch != null) {
				shift2 = calcChildShift(node);
				moveChildBranches(childBranch, shift2);
			}
		}

	}

	/**
	 * 子Branchの平行移動量を算出する
	 * @param pNode 親Node
	 * @return 平行移動量
	 */
	double[] calcChildShift(Node pNode) {

		Branch branch = pNode.getChildBranch();

		double shift[] = new double[3];
		double gMinp[] = { 1.0e+20, 1.0e+20, 1.0e+20 };
		double gMaxp[] = { -1.0e+20, -1.0e+20, -1.0e+20 };
		int i;

		//
		// Calculate positions of mini-max box of the branch
		//
		for (i = 1; i <= branch.getNodeList().size(); i++) {
			Node node = branch.getNodeAt(i);
			double tmp;

			double x = node.getX();
			double y = node.getY();
			double z = node.getZ();
			double width = node.getWidth();
			double height = node.getHeight();
			double depth = node.getDepth();

			tmp = x + width;
			if (gMaxp[0] < tmp)
				gMaxp[0] = tmp;
			tmp = x - width;
			if (gMinp[0] > tmp)
				gMinp[0] = tmp;

			tmp = y + height;
			if (gMaxp[1] < tmp)
				gMaxp[1] = tmp;
			tmp = y - height;
			if (gMinp[1] > tmp)
				gMinp[1] = tmp;

			tmp = z + depth;
			if (gMaxp[2] < tmp)
				gMaxp[2] = tmp;
			tmp = z - depth;
			if (gMinp[2] > tmp)
				gMinp[2] = tmp;
		}

		shift[0] = pNode.getX();
		shift[1] = pNode.getY();
		shift[2] = pNode.getZ();
		if (branch.getNodeList().size() > 0) {
			shift[0] -= (gMinp[0] + gMaxp[0]) * 0.5;
			shift[1] -= (gMinp[1] + gMaxp[1]) * 0.5;
			shift[2] -= (gMinp[2] + gMaxp[2]) * 0.5;
		}

		return shift;
	}

	/**
	 * 子Nodeがすべて葉ノードである場合に限り、格子状にNodeを画面配置する
	 * @param branch Branch
	 */
	public void placeBranchNodesGrid(Branch branch) {

		Node node, pnode = branch.getParentNode();
		double sx = pnode.getWidth();
		double sy = pnode.getHeight();
		int numNode = branch.getNodeList().size();
		int i, j, n, nx, ny;
		double px, py, nn, dx = 0.0, dy = 0.0;
		
		nn = (double)numNode * sx / sy;
		nx = (int) (Math.sqrt(nn - 0.5) + 1.0);
		ny = (nx <= 0) ? 0 : (numNode / nx + 2);
		
		//
		// for each node
		//
		for (i = n = 1, py = 0.0; i < ny; i++) {
			for (j = 0, px = 0.0; j < nx; j++, n++) {
				if (n > numNode)
					return;
				node = branch.getNodeAt(n);
				node.setCoordinate(px, py, 0.0);
				node.setNCoordinate(px, py, 0.0);

				//
				// CAUTION
				// 葉ノードの縦横比を3:4または4:3と決め付けているので注意
				// 葉ノードの縦横の長さを1.2および0.9と決め付けているので注意
				//
				/*
				{
					double width = node.getWidth();
					double height = node.getHeight();
					if(width > height) {
						node.setSize(1.2, 0.9, 1.0);
						node.setNsize(1.2, 0.9, 1.0);
					}
					else {
						node.setSize(0.9, 1.2, 1.0);
						node.setNsize(0.9, 1.2, 1.0);
					}
				}
				*/
				
				node.setPlaced(true);
				px += 3.0;
			}
			py += 3.0;
		}

	}

	/**
	 * 1個のBranchを構成するNodeを画面配置する
	 * @param tree Tree
	 * @param branch Branch
	 * @param templateTree テンプレートTree
	 */
	public void placeNodesOneBranch(
		Tree tree,
		Branch branch,
		Tree templateTree) {

		int i = 0;

		//
		// Check whether grid-based layout or
		// Delaunay-based layout should be applied
		//
		for (i = 1; i <= branch.getNodeList().size(); i++) {
			Node node = branch.getNodeAt(i);
			if(node.getChildBranch()!= null) break;
			//if(node.isDefaultSize() == false) break;
		}
		
		//
		// 格子状の配置
		//
		if (i > branch.getNodeList().size()) {
			placeBranchNodesGrid(branch);
		}
		else {
		
			pg = new PackingGrid();
			Branch templateBranch =
				findTemplateBranch(tree, templateTree, branch);
		
			//
			// テンプレートを使うパッキング 
			//
			if (templateBranch != null) {
				CatOneBranchTemplatePacking ogtp =
					new CatOneBranchTemplatePacking(pg);
				ogtp.placeBranchNodes(
						tree, branch, templateTree, templateBranch);
			}
		
			//
			// テンプレートを使わないパッキング
			//
			else {
				CatOneBranchPacking ogp = new CatOneBranchPacking(pg);
				ogp.placeBranchNodes(tree, branch);
			}
		}
		
		//
		// Calculate positions of mini-max box of the branch
		//
		branch.calcParentNodeSize(false);

		//
		// Move nodes of the child branches
		// Due to the placement of nodes in the branch
		//
		moveChildBranches(branch, null);

	}

	/**
	 * Branchを入力し、それに対応するテンプレートBranchを返す
	 * @param tree Tree
	 * @param templateTree テンプレートTree
	 * @param branch Branch
	 * @return テンプレートBranch
	 */
	Branch findTemplateBranch(Tree tree, Tree templateTree, Branch branch) {
		Branch templateBranch = null;

		if (templateTree == null)
			return null;
		
		if (tree == templateTree)
			return branch;
		
		String branchName = getBranchIdentifier(tree, branch);
		if (branchName == null
			|| branchName.length() <= 0
			|| branchName.startsWith("null"))
			return null;

		for (int i = 0; i < templateTree.getBranchList().size(); i++) {
			templateBranch = (Branch) templateTree.getBranchList().elementAt(i);
			String name = getBranchIdentifier(templateTree, templateBranch);
			if (name == null)
				continue;
			if (name.length() > 0 && name.startsWith(branchName)) {
				return templateBranch;
			}
		}

		return null;
	}

	/**
	 * Branchの識別子となる文字列を返す
	 * @param tree Tree
	 * @param branch Branch
	 * @return branchの識別子となる文字列
	 */
	String getBranchIdentifier(Tree tree, Branch branch) {
		String name = null;
		
		/*
		name = branch.getName();
		if (name != null && name.length() > 0 && !name.startsWith("null"))
			return name;
		*/
		
		TreeTable tg = tree.table;
		if (tg.getNameType() < 0)
			tg.setNameType(0);

		Node parentNode = branch.getParentNode();
		name = tg.getNodeAttributeName(parentNode, tg.getNameType());
		return name;

	}

}
