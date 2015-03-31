package org.heiankyoview2.core.placement;

import java.util.*;
import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.*;

/**
 * Treeの画面配置を行う
 * @author itot
 */
public class Packing {
	Tree tree = null, templateTree = null;
	long mill1, mill2;

	PackingGrid pg;

	/* public values for evaluation */
	public double templateErrorAverage, templateErrorMax, templateErrorMin; 
	public double aspectAverage = 0.0, spaceAverage = 0.0;

	
	/**
	 * Constructor
	 */
	public Packing() {
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
		TemplateNodeIdentifier.setTemplateTree(templateTree);

		Vector branchList = new Vector();
		Branch rootBranch = tree.getRootBranch();
		Branch branch, templateBranch;

		branchList.add((Object) rootBranch);

		//mill1 = System.currentTimeMillis();

		//
		// Traverse the hierarchy of branches
		//     starting from the root branch
		//
		int i = 0;
		while (i < branchList.size()) {
			branch = (Branch) branchList.elementAt(i++);

			//
			// for each node of the branch:
			//     Search for child branches
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

		//mill2 = System.currentTimeMillis();
		
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

		int numNode = branch.getNodeList().size();
		int nx = (int) (Math.sqrt((double) numNode - 0.5) + 1.0);
		int ny = (nx <= 0) ? 0 : (numNode / nx + 2);
		int i, j, n;
		double px, py, dx = 0.0, dy = 0.0;

		Node node;

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
				node.setSize(1.0, 1.0, 1.0);
				node.setNsize(1.0, 1.0, 1.0);
				node.setPlaced(true);
				
				/*
				{
				int type = tree.table.getColorType() + 1;
				if(type > 0) {
				Table table = tree.table.getTable(type);
				if(table.getType() == table.TABLE_DOUBLE) {
					double value = table.getDouble(node.table.getId(type));
					System.out.println("  n=" + n + " x=" + px + " y=" + py + " value=" + value);
				}
				else if(table.getType() == table.TABLE_INT) {
					int value = table.getInt(node.table.getId(type));
					System.out.println("  n=" + n + " x=" + px + " y=" + py + " value=" + value);
				}
				else if(table.getType() == table.TABLE_STRING) {
					String value = table.getString(node.table.getId(type));
					System.out.println("  n=" + n + " x=" + px + " y=" + py + " value=" + value);
				}
				}
				}
				*/
				
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

		int i;

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
		// Grid-based placement
		//
		if (i > branch.getNodeList().size()) {
			placeBranchNodesGrid(branch);

			//
			// Heiankyo-based non-template placement
			//
		} else {
		
			//
			// Find template branch
			//
			pg = new PackingGrid();
			Branch templateBranch =
				TemplateNodeIdentifier.findTemplateBranch(tree, branch);
			
			//
			// Non-template-based packing
			//
			if (templateBranch == null) {

				OneBranchPacking obp = new OneBranchPacking(pg);
				obp.placeBranchNodes(tree, branch);
			}

			//
			// Non-template-based packing
			//
			else {
				OneBranchTemplatePacking obtp = new OneBranchTemplatePacking(pg);
				obtp.placeBranchNodes(
					tree,
					branch,
					templateTree,
					templateBranch);

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
	/*
	Branch findTemplateBranch(Tree tree, Tree templateTree, Branch branch) {
		Branch templateBranch = null;

		if (templateTree == null)
			return null;
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
				//System.out.println("   ** found name=" + name + " bName=" + branchName);
				return templateBranch;
			}
		}

		return null;
	}
	*/
	
	/**
	 * Branchの識別子となる文字列を返す
	 * @param tree Tree
	 * @param branch Branch
	 * @return Branchの識別子となる文字列
	 */
	/*
	String getBranchIdentifier(Tree tree, Branch branch) {
		String name = branch.getName();
		if (name != null && name.length() > 0 && !name.startsWith("null"))
			return name;

		TreeTable tg = tree.table;
		if (tg.getNameType() < 0)
			tg.setNameType(0);

		Node parentNode = branch.getParentNode();
		name = tg.getNodeAttributeName(parentNode, tg.getNameType());
		return name;

	}
	*/
}
