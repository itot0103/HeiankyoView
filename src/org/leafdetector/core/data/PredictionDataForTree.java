/*
 * ???: 2006/07/27
 *
 * TODO ???????????????????????????????:
 * ????? - ?? - Java - ???????? - ??????????
 */
package org.leafdetector.core.data;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.leafdetector.core.tree.MyMutableTreeNode;

/**
 * @author fyamashi
 *
 * TODO ????????????????????????????????:
 * ????? - ?? - Java - ???????? - ??????????
 */
public class PredictionDataForTree {
	
	static PredictionDataForTree base=null;

	Hashtable<String,Integer> field=new Hashtable<String,Integer>();
	
	MyMutableTreeNode root;
	
	static public PredictionDataForTree getInstance(){
		if(base!=null) return base;
		base=new PredictionDataForTree();		
		return base;
	}

	public void clearData(){
		field.clear();
	}
	
	public void setFieldName(String[] fieldNames){
		for(int i=0;i<fieldNames.length;i++){
			field.put(fieldNames[i],new Integer(i));
		}
	}

	public void identifyNode(String[] data){
		String name=data[0];
		double[] temp=new double[data.length-1];
		for(int i=0;i<data.length-1;i++){
			temp[i]=Double.parseDouble(data[i+1]);
		}
		Data list=new Data(name,temp);

	}

	public void setRootNode(MyMutableTreeNode root){
		this.root=root;
	}
	
}
