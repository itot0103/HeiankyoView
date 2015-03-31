package org.heiankyoview2.core.othermethods;

import java.io.*;
import java.util.*;
import org.heiankyoview2.core.tree.*;
import org.heiankyoview2.core.table.*;


public class SquarifiedTreemap {

	Tree tree = null;
	long mill1, mill2;
	
	static final double LEAF_NODE_RATIO = 2.0, NON_LEAF_NODE_RATIO = 0.48;;
	
	Object sortedNodeArray[] = null;
	Object tmpNodeArray[] = null;
	int numNode;


	void calculateNormalizedPosition(Node node) {
		Branch Branch = node.getCurrentBranch();
		Node pnode = Branch.getParentNode();

		node.setNX((node.getX() - pnode.getX()) / pnode.getWidth());
		node.setNY((node.getY() - pnode.getY()) / pnode.getHeight());
	}

	

	/*
	 * Sort nodes in the order of sizes @param Branch Branch
	 */
	public Object[] quickSortNodes(Branch Branch, int numNode) {

		sortedNodeArray = new Object[numNode];
		tmpNodeArray = new Object[numNode];

		for (int i = 0; i < numNode; i++) {
			Node node = Branch.getNodeAt(i + 1);
			sortedNodeArray[i] = (Object) node;
		}

		quickSortNodesWithRange(0, (numNode - 1));

		return sortedNodeArray;
	}

	public Object[] quickSortNodes(Branch Branch) {

		numNode = Branch.getNodeList().size();
		sortedNodeArray = new Object[numNode];
		tmpNodeArray = new Object[numNode];

		for (int i = 0; i < numNode; i++) {
			Node node = Branch.getNodeAt(i + 1);
			sortedNodeArray[i] = (Object) node;
		}

		quickSortNodesWithRange(0, (numNode - 1));

		return sortedNodeArray;
	}


	int VALUEID = 3;
	
	int getNodeSize(Node node) {	
		Branch branch = node.getChildBranch();
		if (branch != null)
			return getChildNodeSize(branch);

		Table table = tree.table.getTable(VALUEID);
		int val = (int)table.getDouble(node.table.getId(VALUEID));
		
		return val;
	}

	
	int getChildNodeSize(Branch branch) {
		int ret = 1;

		for (int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node) branch.getNodeList().elementAt(i);
			Branch cBranch = node.getChildBranch();
			if (cBranch == null) {
				Table table = tree.table.getTable(VALUEID);
				int val = (int)table.getDouble(node.table.getId(VALUEID));
				ret += val;
			}
			else
				ret += getChildNodeSize(cBranch);
		}

		//System.out.println("   nodesize=" + ret);
		return ret;
	}

	/*
	 * Sort nodes in the order of sizes @param int range1 @param int range2
	 */
	void quickSortNodesWithRange(int range1, int range2) {

		if (range2 - range1 < 2)
			return;

		Node node1, node2, node3;

		int i, j, k, r2;
		double size1, size2, size3;

		//
		//  If less than 10 arcs in the range:
		//     Bubble Sort
		//
		if (range2 - range1 < 1000) {
			r2 = range2;
			for (i = range1; i < r2; i++) {
				for (j = range2; j > i; j--) {
					node1 = (Node) sortedNodeArray[j];
					node2 = (Node) sortedNodeArray[j - 1];
					size1 = getNodeSize(node1);
					size2 = getNodeSize(node2);

					if (size1 >= size2)
						continue;
					node3 = (Node) sortedNodeArray[j];
					sortedNodeArray[j] = sortedNodeArray[j - 1];
					sortedNodeArray[j - 1] = (Object) node3;
				}
			}

			return;
		}

		//
		//  If more than 10 arcs in the range:
		//     Quick Sort
		//
		node1 = (Node) sortedNodeArray[range1];
		node2 = (Node) sortedNodeArray[range1 + 1];
		size1 = getNodeSize(node1);
		size2 = getNodeSize(node2);
		size3 = (size1 > size2) ? size1 : size2;

		j = range1;
		k = range2;
		for (i = range1; i <= range2; i++) {
			node1 = (Node) sortedNodeArray[i];
			size1 = getNodeSize(node1);
			if (size1 < size3)
				tmpNodeArray[j++] = (Object) node1;
			else
				tmpNodeArray[k--] = (Object) node1;
		}

		for (i = range1; i <= range2; i++) {
			sortedNodeArray[i] = tmpNodeArray[i];
		}

		//
		//  Recursive call
		//
		if (range1 < k) {
			quickSortNodesWithRange(range1, k);
		} else {
			quickSortNodesWithRange(range1, range1);
			if (j <= range1)
				j = range1 + 1;
		}
		if (j < range2)
			quickSortNodesWithRange(j, range2);
		else
			quickSortNodesWithRange(range2, range2);

	}

	/**
	 * Node layout execution for all Branchs
	 * 
	 * @param Tree
	 *            Tree
	 */
	public Tree placeNodesAllBranch(Tree tree, Tree templateTree,
			int qMethod) {

		// qMethod ... 1:Strip 2:Squarified
		this.tree = tree;
		
		if (qMethod == 1)
			System.out.println("  *** Strip Treemap");
		if (qMethod == 2)
			System.out.println("  *** Squarified Treemap");

		Node rootNode = tree.getRootNode();
		rootNode.setSize(1.0, 1.0, 1.0);

		Vector branchList = new Vector();
		Branch rootBranch = tree.getRootBranch();
		Branch branch, templateBranch;

		branchList.add((Object) rootBranch);

		mill1 = System.currentTimeMillis();

		//
		// Traverse the hierarchy of Branchs
		//     starting from the root Branch
		//
		int i = 0;
		while (i < branchList.size()) {
			branch = (Branch) branchList.elementAt(i++);

			//
			// for each node of the Branch:
			//     Search for child Branchs
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
		// for each Branch
		//     Place nodes in each Branch
		//

		templateBranch = null;
		for (i = 0; i < branchList.size(); i++) {
			branch = (Branch) branchList.elementAt(i);
			
			if (i % 20 == 0) {
				System.out.println("   ... Placing nodes " + i + " / "
						+ branchList.size());
			}
			placeNodesOneBranch(branch, templateBranch, qMethod);
		}

		//RecalcLeafNodeSize(tree);

		mill2 = System.currentTimeMillis();
		return tree;
	}

	/**
	 * Node layout execution in one Branch
	 * 
	 * @param Branch
	 *            Branch
	 */
	public void placeNodesOneBranch(Branch branch, Branch templateBranch, int qMethod) {

		if (qMethod == 1) {
			placeBranchNodesStrip(branch);
		}
		if (qMethod == 2) {
			placeBranchNodesSquarified(branch);
		}
	}

	
	void RecalcLeafNodeSize(Tree tree) {

		// Find the minimum size of leaf nodes
		Branch rootBranch = tree.getRootBranch();
		double size = getMinimumLeafNodeSize(rootBranch, 1.0e+30);
		//System.out.println("*** minSize=" + size);
		setNewMinimumLeafNodeSize(rootBranch, size);
	}

	double getMinimumLeafNodeSize(Branch branch, double currentSize) {

		for (int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node) branch.getNodeList().elementAt(i);
			if (node.getChildBranch() != null) {
				currentSize = getMinimumLeafNodeSize(node.getChildBranch(),
						currentSize);
			} else {
				double w = node.getWidth();
				double h = node.getHeight();
				if (currentSize > w)
					currentSize = w;
				if (currentSize > h)
					currentSize = h;
			}
		}

		return currentSize;
	}

	void setNewMinimumLeafNodeSize(Branch branch, double newSize) {

		for (int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node) branch.getNodeList().elementAt(i);
			if (node.getChildBranch() != null) {
				setNewMinimumLeafNodeSize(node.getChildBranch(), newSize);
			} else {
				node.setSize(newSize, newSize, 0.0);
			}
		}

	}

	double calcNewAspectRatio(Node snode, int parentSize, double parentArea,
			double newStripHeight) {
		double snodeSize = (double) getNodeSize(snode);
		double snodeArea = snodeSize / (double) parentSize * parentArea;
		double snodeWidth = snodeArea / newStripHeight;
		double a = (newStripHeight > snodeWidth) ? (newStripHeight / snodeWidth)
				: (snodeWidth / newStripHeight);
		return a;
	}

	double calcNewThick(Node node, Vector stripNodes, int parentSize,
			double parentThick) {
		int totalSize = 0;
		for (int j = 0; j < stripNodes.size(); j++) {
			Node snode = (Node) stripNodes.elementAt(j);
			totalSize += getNodeSize(snode);
			//System.out.println("          calcNewThick " + j + "/" + stripNodes.size() + " : totalSize=" + totalSize);
		}
		totalSize += getNodeSize(node);
		double ret = (double) totalSize / (double) parentSize * parentThick;

		return ret;
	}

	// flag... 1:X 2:Y
	void calcStripDivider(Vector stripNodes, int parentSize, double parentArea,
			double stripThick, double startPosX, double startPosY, int flag) {

		Node snode;
		int totalSize = 0, minI = 0, numN, minN = 0;
		double minRatio = 1.0e+30;

		for (int j = 0; j < stripNodes.size(); j++) {
			snode = (Node) stripNodes.elementAt(j);
			totalSize += (double) getNodeSize(snode);
		}
		double stripLength = (double) totalSize / (double) parentSize
				* parentArea / stripThick;
		double stripRatio = stripLength / stripThick;

		//
		// Search for the appropriate horizontal & vertical numbers of nodes
		//
		for (int i = 1; i <= stripNodes.size(); i++) {
			//System.out.println("          calcStripDivider " + i + "/" + totalSize);
			numN = 0;
			for (int j = 0; j < stripNodes.size(); j++) {
				snode = (Node) stripNodes.elementAt(j);
				int ns = getNodeSize(snode);
				//System.out.println("            calcStripDivider " + i + "/" + totalSize + " nodesize=" + ns);
				int nn = ns / i;
				if (ns % i > 0)
					nn++;
				numN += nn;
			}

			double r = (double) numN / (double) i;
			double ratio = (r > stripRatio) ? (r / stripRatio)
					: (stripRatio / r);
			if (ratio < minRatio) {
				minRatio = ratio;
				minI = i;
				minN = numN;
			}
		}

		double snodeLength;
		if (flag == 1) {

			for (int j = 0; j < stripNodes.size(); j++) {
				snode = (Node) stripNodes.elementAt(j);
				int ns = getNodeSize(snode);
				int nn = ns / minI;
				if (ns % minI > 0)
					nn++;
				snodeLength = stripLength * (double) nn / (double) minN;
				snode.setX(startPosX + snodeLength * 0.5);
				snode.setY(startPosY + stripThick * 0.5);
				snode.setZ(0.0);
				//if (snode.getChildBranch() != null) {
				double ww = snodeLength * NON_LEAF_NODE_RATIO;
				double hh = stripThick * NON_LEAF_NODE_RATIO;
				snode.setSize(ww, hh, 1.0);
				//System.out.println("         leaf w=" + ww + " h=" + hh + " branch=" + snode.getChildBranch());
				
				/*
				} else {
					double size = (snodeLength < stripThick) ? snodeLength
							: stripThick;
					size *= LEAF_NODE_RATIO;
					snode.setSize(size, size, 1.0);
				}
				*/
				startPosX += snodeLength;
			}
		}

		if (flag == 2) {
			for (int j = 0; j < stripNodes.size(); j++) {
				snode = (Node) stripNodes.elementAt(j);
				int ns = getNodeSize(snode);
				int nn = ns / minI;
				if (ns % minI > 0)
					nn++;
				snodeLength = stripLength * (double) nn / (double) minN;
				snode.setX(startPosX + stripThick * 0.5);
				snode.setY(startPosY + snodeLength * 0.5);
				snode.setZ(0.0);
				//if (snode.getChildBranch() != null)
				double ww = snodeLength * NON_LEAF_NODE_RATIO;
				double hh = stripThick * NON_LEAF_NODE_RATIO;
				snode.setSize(ww, hh, 1.0);
				//System.out.println("         leaf w=" + ww + " h=" + hh + " branch=" + snode.getChildBranch());
				
				/*
				else {
					double size = (snodeLength < stripThick) ? snodeLength
							: stripThick;
					size *= LEAF_NODE_RATIO;
					snode.setSize(size, size, 1.0);
				}
				*/
				startPosY += snodeLength;
			}
		}

	}

	//
	// Strip-based quantum treemap
	//
	public void placeBranchNodesStrip(Branch branch) {

		Node node, pnode = branch.getParentNode();
		;
		Vector stripNodes = new Vector();
		int parentSize = getNodeSize(branch.getParentNode()) - 1;
		double stripHeight = 1.0, worstAspect = 1.0;
		double snodeWidth, snodeArea;

		double parentWidth = pnode.getWidth() * 2.0;
		double parentHeight = pnode.getHeight() * 2.0;
		double parentArea = parentWidth * parentHeight;

		double stripPosY = pnode.getY() - pnode.getHeight();

		// if the Branch only contains one node
		if (branch.getNodeList().size() == 1) {
			node = (Node) branch.getNodeList().elementAt(0);
			node.setX(pnode.getX());
			node.setY(pnode.getY());
			node.setZ(0.0);
			//if (node.getChildBranch() != null) {
			double ww = parentWidth * NON_LEAF_NODE_RATIO;
			double hh = parentHeight * NON_LEAF_NODE_RATIO;
			node.setSize(ww, hh, 1.0);
			System.out.println("         leaf w=" + ww + " h=" + hh + " branch=" + node.getChildBranch());
			
			/*
			} else {
				double size = (parentWidth < parentHeight) ? parentWidth
						: parentHeight;
				size *= LEAF_NODE_RATIO;
				node.setSize(size, size, 1.0);
			}
			*/
			return;
		}

		// Main-process:
		//     * Place nodes one-by-one
		//
		for (int i = branch.getNodeList().size() - 1; i >= 0; i--) {
			node = (Node) branch.getNodeList().elementAt(i);

			// try to insert the node into the current strip
			if (stripNodes.size() > 0) {
				double newStripHeight = 1.0, newWorstAspect = 1.0;
				int totalSize = 0;
				Node snode;

				// calculate the new strip height
				newStripHeight = calcNewThick(node, stripNodes, parentSize,
						parentHeight);

				// calculate the new worst aspect ratio
				double a;
				for (int j = 0; j < stripNodes.size(); j++) {
					snode = (Node) stripNodes.elementAt(j);
					a = calcNewAspectRatio(snode, parentSize, parentArea,
							newStripHeight);
					if (a > newWorstAspect)
						newWorstAspect = a;
				}
				a = calcNewAspectRatio(node, parentSize, parentArea,
						newStripHeight);
				if (a > newWorstAspect)
					newWorstAspect = a;

				// if aspect ratio gets better, or the last node
				if (newWorstAspect < worstAspect || i == 0) {
					stripHeight = newStripHeight;
					worstAspect = newWorstAspect;
					stripNodes.add((Object) node);
					if (i > 0)
						continue;
				}

				// give positions and sizes in the strip
				double stripPosX = branch.getParentNode().getX()
						- branch.getParentNode().getWidth();
				calcStripDivider(stripNodes, parentSize, parentArea,
						stripHeight, stripPosX, stripPosY, 1);
				stripPosY += stripHeight;
				if (i == 0)
					break;

			} // end if(stripNodes.size() > 0)

			// Create the new strip
			stripNodes = new Vector();
			stripNodes.add((Object) node);
			stripHeight = ((double) getNodeSize(node) * parentArea)
					/ ((double) parentSize * parentWidth);
			worstAspect = (stripHeight > parentWidth) ? (stripHeight / parentWidth)
					: (parentWidth / stripHeight);

		} // end for node in a Branch

	}

	//
	// Squarified quantum treemap
	//
	public void placeBranchNodesSquarified(Branch branch) {

		Vector stripNodes = new Vector();
		double stripThick = 1.0, worstAspect = 1.0;
		double snodeLength, snodeArea;

		Node node, prevNode = null;
		Node pnode = branch.getParentNode();
		
		int parentSize = getNodeSize(branch.getParentNode()) - 1;

		double parentWidth = pnode.getWidth() * 2.0;
		double parentHeight = pnode.getHeight() * 2.0;
		double parentArea = parentWidth * parentHeight;

		double minX = pnode.getX() - pnode.getWidth();
		double minY = pnode.getY() - pnode.getHeight();
		double maxX = pnode.getX() + pnode.getWidth();
		double maxY = pnode.getY() + pnode.getHeight();
		double posX = minX, posY = minY;

		boolean isHorizontal = true;

		// if the Branch only contains one node
		if (branch.getNodeList().size() == 1) {
			node = (Node) branch.getNodeList().elementAt(0);
			node.setX(pnode.getX());
			node.setY(pnode.getY());
			node.setZ(0.0);
			//if (node.getChildBranch() != null) {
			double ww = parentWidth * NON_LEAF_NODE_RATIO;
			double hh = parentHeight * NON_LEAF_NODE_RATIO;
			node.setSize(ww, hh, 1.0);
			//System.out.println("         leaf w=" + ww + " h=" + hh + " branch=" + node.getChildBranch());
				
			/*
			} else {
				double size = (parentWidth < parentHeight) ? parentWidth
						: parentHeight;
				size *= LEAF_NODE_RATIO;
				node.setSize(size, size, 1.0);
			}
			*/
			return;
		}

		// Pre-process:
		sortedNodeArray = quickSortNodes(branch);

		// Main-process:
		//     * Place nodes one-by-one
		//
		for (int i = branch.getNodeList().size() - 1; i >= 0; i--) {
			node = (Node) sortedNodeArray[i];
			//System.out.println("        node " + i + "/" + branch.getNodeList().size());
			
			// Calculate aspect ratio
			if (stripNodes.size() > 0) {
				double newStripThick = 1.0, newWorstAspect = 1.0;
				int totalSize = 0;
				Node snode;

				// calculate the new strip height
				newStripThick = calcNewThick(node, stripNodes, parentSize,
						parentArea);

				if (isHorizontal)
					newStripThick /= (maxX - posX);
				else
					newStripThick /= (maxY - posY);

				// calculate the new worst aspect ratio
				double a;
				for (int j = 0; j < stripNodes.size(); j++) {
					snode = (Node) stripNodes.elementAt(j);
					a = calcNewAspectRatio(snode, parentSize, parentArea,
							newStripThick);
					if (a > newWorstAspect)
						newWorstAspect = a;
				}
				a = calcNewAspectRatio(node, parentSize, parentArea,
						newStripThick);
				if (a > newWorstAspect)
					newWorstAspect = a;

				// if aspect ratio gets better, or the last node
				if (newWorstAspect < worstAspect || i == 0) {
					stripThick = newStripThick;
					worstAspect = newWorstAspect;
					stripNodes.add((Object) node);
					if (i > 0)
						continue;
				}

				// give positions and sizes in the strip
				if (isHorizontal == true) {
					double stripPosX = posX;
					calcStripDivider(stripNodes, parentSize, parentArea,
							stripThick, stripPosX, posY, 1);
					posY += stripThick;
				} else {
					double stripPosY = posY;
					calcStripDivider(stripNodes, parentSize, parentArea,
							stripThick, posX, stripPosY, 2);
					posX += stripThick;
				}

				if (i == 0)
					break;

			} // end if(stripNodes.size() > 0)

			// Create the new strip
			stripNodes = new Vector();
			stripNodes.add((Object) node);

			double nodeArea = parentArea * (double) getNodeSize(node)
					/ (double) parentSize;
			isHorizontal = (isHorizontal == true) ? false : true;

			double newLength;
			if (isHorizontal) {
				newLength = maxX - posX;
				stripThick = nodeArea / newLength;
				worstAspect = (stripThick > newLength) ? (stripThick / newLength)
						: (newLength / stripThick);
			} else {
				newLength = maxY - posY;
				stripThick = nodeArea / newLength;
				worstAspect = (stripThick > newLength) ? (stripThick / newLength)
						: (newLength / stripThick);
			}

		} // end for node in a Branch

	}

}
