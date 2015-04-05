/*
 * ???: 2006/07/26
 *
 * TODO ???????????????????????????????:
 * ????? - ?? - Java - ???????? - ??????????
 */
package org.leafdetector.core.tree;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.heiankyoview2.applet.junihitoeview.GlDefaultCanvas;
import org.heiankyoview2.core.table.Table;
import org.heiankyoview2.core.tree.Branch;
import org.leafdetector.core.data.PredictionData;

/**
 * @author fyamashi
 *
 * TODO ????????????????????????????????:
 * ????? - ?? - Java - ???????? - ??????????
 */
public class Node {

	List<Criterion> criterionList=new ArrayList<Criterion>();
	List<Node> childNodeList=new ArrayList<Node>();
	
	Branch correspondingBranch;
	GlDefaultCanvas canvas=null;
	
	public Node(Branch branch, GlDefaultCanvas canvas){
		this.canvas=canvas;
		correspondingBranch=branch;
		Table table=canvas.getTreeTable().getTable(1);
		if(branch.getId()!=1) createCondition(table.getString(branch.getId()));
		setChild();
	}
	
	public void setChild(){
		int numNode=correspondingBranch.getNumNode();
		for(int i=0;i<numNode;i++){
			org.heiankyoview2.core.tree.Node node=correspondingBranch.getNodeAt(i+1);
			Branch childBranch=node.getChildBranch();
			if(childBranch!=null){
				Node child=new Node(childBranch,canvas);
				childNodeList.add(child);
			}
		}
	}
	
	public Branch getBranch(int dataID){
		PredictionData base=PredictionData.getInstance();
		Node terminal=this;
		boolean isPassed=false;
		while(terminal.childNodeList.size()!=0){
			for(int i=0;i<terminal.childNodeList.size();i++){
				Node candidate=(Node)terminal.childNodeList.get(i);
				for(int j=0;j<candidate.criterionList.size();j++){
					Criterion temp=(Criterion)candidate.criterionList.get(j);
					try{
						double value=base.getData(dataID,temp.getFieldName());
						isPassed=temp.isPassed(value);
						if(!isPassed) break;
					}catch(Exception e){
						JOptionPane.showMessageDialog(null, "Missing variables for classification!");
						System.exit(0);
					}
				}
				if(isPassed){
					terminal=candidate;
					break;
				}
			}
		}
		return terminal.correspondingBranch;
	}

	
	public Branch getBranch(String[] data){
		PredictionData base=PredictionData.getInstance();
		Node terminal=this;
		boolean isPassed=false;
		while(terminal.childNodeList.size()!=0){
			for(int i=0;i<terminal.childNodeList.size();i++){
				Node candidate=(Node)terminal.childNodeList.get(i);
				for(int j=0;j<candidate.criterionList.size();j++){
					Criterion temp=(Criterion)candidate.criterionList.get(j);
					
					try{
						int tempId=base.getFieldID(temp.getFieldName());
						double value=Double.valueOf(data[tempId]);
						isPassed=temp.isPassed(value);
						if(!isPassed) break;
					}catch(Exception e){
						JOptionPane.showMessageDialog(null, "Missing variables for classification!");
						System.exit(0);
					}
				}
				if(isPassed){
					terminal=candidate;
					break;
				}
			}
		}
		return terminal.correspondingBranch;
	}
	
	private void createCondition(String condition){
		String[] conditions=condition.split(",");
		for(int i=0;i<conditions.length;i++){
			setCondition(conditions[i]);
		}
		
	}
	
	private void setCondition(String condition){
		Criterion criterion=new Criterion();
		boolean tempFlag=false;
		String str="";
		char tempCharactor;
		try{
			for(int i=0;i<condition.length();i++){
				if((tempCharactor=condition.charAt(i))=='\''){
					tempCharactor=condition.charAt(i+1);
					if(tempCharactor=='<'){
						criterion.setFieldName(str);
						criterion.setComparator(-1);
						str="";
						i+=2;
						tempFlag=true;
					}
					if(tempCharactor=='='){
						criterion.setFieldName(str);
						criterion.setComparator(0);
						str="";
						i+=2;
						tempFlag=true;
					}
					if(tempCharactor=='>'){
						criterion.setFieldName(str);
						criterion.setComparator(1);
						str="";
						i+=2;
						tempFlag=true;
					}
				}
				else{
					str+=String.valueOf(tempCharactor);
				}
				if(tempFlag){
					str=condition.substring(i,condition.length());
					criterion.setValue(Double.parseDouble(str));
					break;
				}
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Unable to generate a tree!");
			System.exit(0);
		}
		criterionList.add(criterion);
		
	}
}
