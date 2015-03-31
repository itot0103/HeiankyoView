package org.heiankyoview2.core.othermethods;

import org.heiankyoview2.core.tree.*;

public class UnbrellaNodeLink {
	int count = 0;
	Branch orderedBranch[];
	double NODE_SIZE = 1.0, NODE_INTERVAL = 4.0, NODE_LEVEL_INTERVAL = -500.0;	
	
	public void placeNodesAllBranch(Tree tree) {
		orderedBranch = new Branch[tree.getNumBranch()];
		orderBranch(tree.getRootBranch(), 1);
		defineNodePosition(tree);
	}
	
	
	/**
	 * Order branches by depth search
	 */
	void orderBranch(Branch branch, int depth) {
		orderedBranch[count++] = branch;
		branch.setLevel(depth);
		for(int i = 1; i <= branch.getNumNode(); i++) {
			Node node = branch.getNodeAt(i);
			if(node.getChildBranch() == null) continue;
			orderBranch(node.getChildBranch(), (depth + 1));
		}
	}
	
	
	/**
	 * Define node position by a bottom up algorithm
	 */
	void defineNodePosition(Tree tree) {
		int leafcount = 0;
		
		// for each branch
		for(int i = orderedBranch.length - 1; i >= 0; i--) {
			Branch branch = orderedBranch[i];
			
			// for each node under the branch
			for(int j = 1; j <= branch.getNumNode(); j++) {
				Node node = branch.getNodeAt(j);
				double y = branch.getLevel() * NODE_LEVEL_INTERVAL;
				if(node.getChildBranch() == null) {
					double x = (leafcount++) * NODE_INTERVAL;
					node.setX(x);	
				}
				else {
					Branch cbranch = node.getChildBranch();
					if(cbranch.getNumNode() == 0) {
						double x = (leafcount++) * NODE_INTERVAL;
						node.setX(x);
					}
					else {
						double xx = 0.0;
						for(int k = 1; k <= cbranch.getNumNode(); k++) {
							Node cnode = cbranch.getNodeAt(k);
							xx += cnode.getX();
						}
						xx /= (double)cbranch.getNumNode();
						node.setX(xx);
					}
				}
				node.setY(y);
				node.setSize(NODE_SIZE, NODE_SIZE, NODE_SIZE);
			}
			
		}
		
		double xx = 0.0;
		Branch branch = tree.getRootBranch();
		for(int k = 1; k <= branch.getNumNode(); k++) {
			Node node = branch.getNodeAt(k);
			xx += node.getX();
		}
		xx /= (double)branch.getNumNode();
		Node rnode = branch.getParentNode();
		rnode.setX(xx);
		rnode.setY(0.0);
		rnode.setSize(NODE_SIZE, NODE_SIZE, NODE_SIZE);
		
	}
	
	
}
