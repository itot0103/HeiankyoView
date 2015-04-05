/**
 * 
 */
package org.leafdetector.core.io;

import java.io.File;
import java.util.Vector;

import org.heiankyoview2.applet.junihitoeview.GlDefaultCanvas;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.table.TreeTable;
import org.heiankyoview2.core.table.NodeTablePointer;
import org.heiankyoview2.core.tree.Branch;
import org.leafdetector.core.tree.MyMutableTreeNode;

/**
 * @author fyamashi
 *
 */
public class PredictedResultFileExporter extends FileOpener {

	GlDefaultCanvas canvas=null;
	MyMutableTreeNode root=null;
	

	/* (?Javadoc)
	 * @see org.leafdetector.core.io.FileOpener#readFile()
	 */
	@Override
	public void readFile() {
		// TODO 
		
	}

	/* (?Javadoc)
	 * @see org.leafdetector.core.io.FileOpener#saveFile()
	 */
	@Override
	public void saveFile() {
		// TODO 

		
	}

	public void exportPredictedDataToFile(GlDefaultCanvas canvas){
		
		this.canvas=canvas;
		
		File outputFile=getFile();
		if(outputFile==null) return;
		
		FileOutput output=new FileOutput(outputFile);
		MyMutableTreeNode currentLeaf=(MyMutableTreeNode)root.getFirstLeaf();
		
		output.println(getHeader());
		
		int count=1;
		
		do{
			MyMutableTreeNode tempNode=currentLeaf;
			String tempStr2=new String("");
			while(tempNode!=root.getRoot()){
				tempStr2=getBranchName(tempNode.getBranch())+"\t"+tempStr2;
				tempNode=(MyMutableTreeNode)tempNode.getParent();
			}
			
			double[] averages=calculateAverage(currentLeaf.getBranch());
			String tempStr1=String.valueOf((float)averages[0]);
			for(int i=1;i<averages.length;i++){
				tempStr1+="\t"+String.valueOf((float)averages[i]);
			}
			for(int i=0;i<currentLeaf.getChildNumber();i++){
				output.println(currentLeaf.getChildNode(i)+"\t"+String.valueOf(count)
						+"\t"+tempStr1+"\t"+tempStr2);
			}
			count++;
		}while((currentLeaf=(MyMutableTreeNode)currentLeaf.getNextLeaf())!=null);
		output.close();
	}

	public String getBranchName(Branch branch){
		Table table=canvas.getTreeTable().getTable(1);
		return table.getString(branch.getId());
	}
	
	public void setMutableTreeRoot(MyMutableTreeNode root){
		this.root=root;
	}

	
	double[] calculateAverage(Branch branch){
		Vector<org.heiankyoview2.core.tree.Node> nodeList=branch.getNodeList();
		int targetTables[]=canvas.getTargetTables();
		double[] averages=new double[targetTables.length];
		TreeTable tt=canvas.getTreeTable();
		Table[] table=new Table[targetTables.length];
		for(int i=0;i<targetTables.length;i++){
			table[i]=new Table();
		}
		
		for(int i=0;i<averages.length;i++){
			averages[i]=0.0;
			table[i]=tt.getTable(targetTables[i] + 1);
		}
		
		for(int j=0;j<nodeList.size();j++){
			NodeTablePointer tn = (NodeTablePointer)nodeList.get(j).table;
			for(int i=0;i<averages.length;i++){	
				int id = tn.getId(targetTables[i] + 1);
				double value=0.;
				if(table[i].getType()==2){
					value=table[i].getDouble(id);
				}else if(table[i].getType()==3){
					value=table[i].getInt(id);
				}else{
				}
				averages[i]+=value/(double)nodeList.size();
			}
		}
		return averages;
	}

	String getHeader(){
		TreeTable tt=canvas.getTreeTable();
		String str=new String(tt.getTable(2).getName());
		str+="\tgroupNumber";
		int targetTables[]=canvas.getTargetTables();
		for(int i=0;i<targetTables.length;i++){	
			Table table = tt.getTable(targetTables[i] + 1);
			str+="\t"+table.getName();
		}
		return str;
	}
}
