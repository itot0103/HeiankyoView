package org.heiankyoview2.datagen.treegen;


import java.io.File;
import java.util.Vector;

import org.heiankyoview2.core.tree.Tree;
import org.heiankyoview2.core.tree.Branch;
import org.heiankyoview2.core.tree.Node;
import org.heiankyoview2.core.fileio.TreeFileReader;
import org.heiankyoview2.core.fileio.TreeFileWriter;
//import org.heiankyoview2.core.packing.Packing;
/**
 * Å‰ºˆÊŠK‘w‚æ‚èã•”‚ÌŠK‘w‚ğØ‚èæ‚é
 */

public class CutTreeHigherLevel{
	static boolean flag = false;
	
	/**
	 * Constructor
	 */
	public CutTreeHigherLevel() {
	
	}
	
	
	/**
	 * Tree‚©‚çÅ‰ºˆÊ‚ÌŠK‘w‚ğæ‚èœ‚­
	 */
	public void cutTree(Tree tree){
		Branch rootBranch = tree.getRootBranch();
		int depth = getBranchDepth(rootBranch);
		int cnt = 1;
		
		for (int i = 1; i <= tree.getNumBranch(); i++) {
			Branch branch = tree.getBranchAt(i);

			if (branch.getLevel() == depth){
				Node pnode = branch.getParentNode();
				Node cnode = branch.getNodeAt(1);
				Branch pbranch = pnode.getCurrentBranch();
				
				//pnode.table = cnode.table;
				
				for(int j = 1; j <= pnode.table.getNumId(); j++) {
					int id = pnode.table.getId(j);
					//System.out.println(" i=" + i + " j=" + j + " id=" + id);
				}
				
				
				pnode.setChildBranch(null); 
				tree.deleteOneBranch(branch);
				i--;
			}
			else
				branch.setId(cnt++);
		}
		if (depth == 1) flag = false;

	}
	
	
	
		/**
		 * rootbranch‚ÌŠK‘w‚Ì[‚³‚ğ•Ô‚·
		 */
		public int getBranchDepth(Branch branch){
			int depth;
			int d = 1;
			
			if (branch == null) depth = 0;
			else {
				
				for (int i = 1; i <= branch.getNodeList().size(); i++) {
					Node node = branch.getNodeAt(i);
					Branch childBranch = node.getChildBranch();

					if (childBranch == null)
						d = 0;
					else if (d < getBranchDepth(childBranch))
						d = getBranchDepth(childBranch);		
				}
				depth = d + 1;
			}
			return depth;
		}
		
		
		
		/**
		 * treeƒtƒ@ƒCƒ‹‚ğ“Ç‚İ‚Ş
		 */
		public Tree readFile(File inputFile) {
			Tree tree;
			if (inputFile == null) {
				tree = null;  return null;
			} 

			String fileName = inputFile.getName();
			if (
				fileName.endsWith(".tree") == true
					|| fileName.endsWith(".TREE") == true) {

				TreeFileReader treeInput = new TreeFileReader(inputFile);
				if (treeInput == null)
					return null;

				tree = treeInput.getTree();

				//TableGraph tg = (TableGraph)graph.getAttribute();
				//tg.setNameType(0);
				//tg.setUrlType(tg.getNumTable() - 1);
				//System.out.println(" ... completed to read " + fileName);
				//if (graph == null) return null;
				//packing.placeNodesAllGroup(graph);
			}
			else tree = null;
			//tablePanel = new SARTablePanel(graph);
			//tablePanel.setCanvas(canvas);
			//nodeValuePanel = new NodeValuePanel(graph);
			//canvas.viewReset();

			return tree;
		}
		
		


		/**
		 * Tree‚ğÄ\’z‚·‚é
		 * @param tree Tree
		 */
		public void rearrangeTree(Tree tree) {
			int i, j;
			boolean isNode = false;

			Vector branchList = tree.getBranchList();
			for (i = 0; i < branchList.size(); i++) {
				Branch branch = (Branch) branchList.elementAt(i);

				Vector nodeList = branch.getNodeList();
				for (j = nodeList.size() - 1, isNode = false; j >= 0; j--) {
					Node node = (Node) nodeList.elementAt(j);
					if (node.getChildBranch() == null)
						isNode = true;
					if (isNode == true && node.getChildBranch() != null) {
						branch.exchangeParentNodeOrder(node);
						j = nodeList.size();
						isNode = false;
					}
				}
			}
		}
		
		
		
		/**
		 * mainŠÖ”
		 */
		public static void main(String[] args) {
			
			/*
			
			CutTreeHigherLevel cthl = new CutTreeHigherLevel();
			File inputFile = new File("level3.tree");
			Tree tree = cthl.readFile(inputFile);
			
			cthl.cutTree(tree);
			cthl.rearrangeTree(tree);
			
			TreeFileWriter tnf = new TreeFileWriter(new File("cut_level3.tree"), tree);
			tnf.writeTree();
			
			*/
			
			CutTreeHigherLevel cthl = new CutTreeHigherLevel();
			String dirName = "C:/itot/projects/HeiankyoView2/data/template/";
			String fileName = "jiali-min10max100_4t.tree"; 
			File inputFile = new File(dirName + fileName);
			int i;
			
			
			
			
			for (i = 1; flag = true; i++){
				Tree tree = cthl.readFile(inputFile);
				
				cthl.cutTree(tree);
				if (flag == false) break;
				cthl.rearrangeTree(tree);
				
				File outputFile = new File(dirName + "cut[" + i + "]_" + fileName);
				TreeFileWriter tnf = new TreeFileWriter(outputFile, tree);
				tnf.writeTree();
				
				inputFile = outputFile;
			}
			
		}
		
	
}