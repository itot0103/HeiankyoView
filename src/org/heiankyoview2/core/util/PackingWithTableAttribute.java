package org.heiankyoview2.core.util;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.placement.Packing;

/**
 * @author itot
 */
public class PackingWithTableAttribute {

	static TreeTable ttree;
	static Table xtable;
	static Table ytable;
	static int xaxisType, yaxisType;

	public static void packingWithTableAttribute(
		Tree tree,
		int xtype,
		int ytype) {

		Tree templateTree = null;
		xaxisType = xtype + 1;
		yaxisType = ytype + 1;

		ttree = tree.table;

		if (xaxisType != yaxisType
			&& xaxisType > 0
			&& xaxisType <= ttree.getNumTable()
			&& yaxisType > 0
			&& yaxisType <= ttree.getNumTable()) {
			templateTree = generateTemplateTree(tree, xaxisType, yaxisType);
			tree.setTemplateTree(templateTree);
		} else {
			tree.setTemplateTree(null);
		}

		Packing packing = new Packing();
		packing.placeNodesAllBranch(tree);
	}

	static Tree generateTemplateTree(
		Tree tree,
		int xaxisType,
		int yaxisType) {

		xtable = ttree.getTable(xaxisType);
		ytable = ttree.getTable(yaxisType);
		if (xtable.getType() == 1)
			return null;
		if (ytable.getType() == 1)
			return null;

		Tree templateTree = CopyTree.copyTree(tree);
		setIdealPositions(templateTree.getRootBranch());

		return templateTree;
	}

	static void setIdealPositions(Branch branch) {
		double xmin = 1.0e+30, xmax = -1.0e+30, ymin = 1.0e+30, ymax = -1.0e+30;
		double xvalue = 0.0, yvalue = 0.0;
		double xpos, ypos, xcoef = 0.0, ycoef = 0.0;

		// Extract min-max values of parameters		
		for (int i = 0; i < branch.getNodeList().size(); i++) {
			Node node = (Node) branch.getNodeList().elementAt(i);
			NodeTablePointer tn = node.table;

			if (xtable.getType() == 2) { // double
				xvalue = xtable.getDouble(tn.getId(xaxisType));
			} else if (xtable.getType() == 3) { // int
				xvalue = (double) xtable.getInt(tn.getId(xaxisType));
			}
			if (xmin > xvalue)
				xmin = xvalue;
			if (xmax < xvalue)
				xmax = xvalue;

			if (ytable.getType() == 2) { // double
				yvalue = ytable.getDouble(tn.getId(yaxisType));
			} else if (ytable.getType() == 3) { // int
				yvalue = (double) ytable.getInt(tn.getId(yaxisType));
			}
			if (ymin > yvalue)
				ymin = yvalue;
			if (ymax < yvalue)
				ymax = yvalue;
		}

		if (xmax - xmin < 1.0e-5)
			xcoef = 0.0;
		else
			xcoef = 2.0 / (xmax - xmin);
		if (ymax - ymin < 1.0e-5)
			ycoef = 0.0;
		else
			ycoef = 2.0 / (ymax - ymin);

		for (int i = 0; i < branch.getNodeList().size(); i++) {

			Node node = (Node) branch.getNodeList().elementAt(i);
			NodeTablePointer tn = node.table;

			if (xtable.getType() == 2) { // double
				xvalue = xtable.getDouble(tn.getId(xaxisType));
			} else if (xtable.getType() == 3) { // int
				xvalue = (double) xtable.getInt(tn.getId(xaxisType));
			}
			if (ytable.getType() == 2) { // double
				yvalue = ytable.getDouble(tn.getId(yaxisType));
			} else if (ytable.getType() == 3) { // int
				yvalue = (double) ytable.getInt(tn.getId(yaxisType));
			}

			xpos = xcoef * (xvalue - xmin) - 1.0;
			ypos = ycoef * (yvalue - ymin) - 1.0;
			node.setNCoordinate(xpos, ypos, 0.0);

			if (node.getChildBranch() != null) {
				setIdealPositions(node.getChildBranch());
			}
		}
	}

}
