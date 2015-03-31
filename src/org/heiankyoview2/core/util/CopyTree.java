package org.heiankyoview2.core.util;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.table.Table;

/**
 * @author itot
 */
public class CopyTree {
	static Tree tree, copyTree;

	public static Tree copyTree(Tree t) {
		tree = t;
		copyTree = new Tree();

		initializeCopyTree();
		copyHierarchy();
		copyTable();
		
		return copyTree;
	}

	static void initializeCopyTree() {

		// create the root branch
		Node copyRootNode = copyTree.getRootNode();
		copyTree.setNumBranch(1);
		Branch branch = copyTree.getBranchAt(1);
		copyTree.setRootBranch(branch);
		branch.setParentNode(copyRootNode);
		copyRootNode.setChildBranch(branch);


		NodeTablePointer tn = tree.getRootNode().table;
		NodeTablePointer copyTn = copyRootNode.table;
		copyTn.setNumId(tn.getNumId());
		for (int j = 1; j <= tn.getNumId(); j++) {
			copyTn.setId(j, tn.getId(j));
		}
	}

	static void copyHierarchy() {
		Branch rootBranch = tree.getRootBranch();
		Branch copyRootBranch = copyTree.getRootBranch();
		copyBranch(rootBranch, copyRootBranch);
	}

	static void copyBranch(Branch branch, Branch copyBranch) {
		copyBranch.setNumNode(branch.getNodeList().size());
		for (int i = 0; i < branch.getNodeList().size(); i++) {

			Node node = (Node) branch.getNodeList().elementAt(i);
			Node copyNode = (Node) copyBranch.getNodeList().elementAt(i);
			copyNode.setX(node.getX());
			copyNode.setY(node.getY());
			copyNode.setZ(node.getZ());
			copyNode.setNX(node.getNX());
			copyNode.setNY(node.getNY());
			copyNode.setNZ(node.getNZ());
			copyNode.setWidth(node.getWidth());
			copyNode.setHeight(node.getHeight());
			copyNode.setNwidth(node.getNwidth());
			copyNode.setNheight(node.getNheight());

			NodeTablePointer tn = node.table;
			NodeTablePointer copyTn = copyNode.table;
			copyTn.setNumId(tn.getNumId());
			for (int j = 1; j <= tn.getNumId(); j++) {
				copyTn.setId(j, tn.getId(j));
			}

			if (node.getChildBranch() != null) {
				Branch copyChildBranch = copyTree.getOneNewBranch();
				copyNode.setChildBranch(copyChildBranch);
				copyChildBranch.setParentNode(copyNode);
				copyBranch(node.getChildBranch(), copyChildBranch);
			}
		}
	}

	static void copyTable() {

		// Initialize
		TreeTable tg = tree.table;
		TreeTable copyTg = copyTree.table;
		copyTg.setNameType(tg.getNameType());
		copyTg.setColorType(tg.getColorType());
		copyTg.setHeightType(tg.getHeightType());

		// Allocate tables
		copyTg.setNumTable(tg.getNumTable());
		for (int i = 1; i <= tg.getNumTable(); i++) {
			copyTg.setTable(i, (new Table()));
		}

		// for each table
		for (int i = 1; i < tg.getNumTable(); i++) {
			Table table = tg.getTable(i);
			Table copyTable = copyTg.getTable(i);
			copyTable.setName(table.getName());

			int type = table.getType();
			int size = table.getSize();
			copyTable.setType(type);
			copyTable.setSize(size);

			for (int j = 1; j <= size; j++) {

				if (type == Table.TABLE_STRING) { 
					String value = table.getString(j);
					copyTable.set(j, value);
				}
				if (type == Table.TABLE_DOUBLE) {
					copyTable.set(j, table.getDouble(j));
				}
				if (type == Table.TABLE_INT) {
					copyTable.set(j, table.getInt(j));
				}

			}
		}
	}
}