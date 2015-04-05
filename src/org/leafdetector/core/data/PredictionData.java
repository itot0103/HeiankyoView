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

import javax.swing.JOptionPane;

/**
 * @author fyamashi
 *
 * TODO ????????????????????????????????:
 * ????? - ?? - Java - ???????? - ??????????
 */
public class PredictionData {
	
	static PredictionData base=null;

	Hashtable<String,Integer> field=new Hashtable<String,Integer>();
	List<Data> dataList=new ArrayList<Data>();
	
	static public PredictionData getInstance(){
		if(base!=null) return base;
		base=new PredictionData();		
		return base;
	}

	public int getSize(){
		return dataList.size();
	}
	
	public void clearData(){
		field.clear();
		dataList.clear();
	}
	
	public void setFieldName(String[] fieldNames){
		for(int i=0;i<fieldNames.length;i++){
			field.put(fieldNames[i],new Integer(i));
		}
	}

	public double getData(int dataId, String fieldName){
		Integer id=(Integer)field.get(fieldName);
		return ((Data)dataList.get(dataId)).getData(id.intValue()-1);
	}
	
	public int getFieldID(String fieldName){
		return field.get(fieldName);
	}
	
	public String getDataName(int dataId){
		return ((Data)dataList.get(dataId)).getName();
	}
	
	public void setData(String[] data){
		String name=data[0];
		double[] temp=new double[data.length-1];
		for(int i=0;i<data.length-1;i++){
			temp[i]=Double.parseDouble(data[i+1]);
		}
		Data list=new Data(name,temp);
		dataList.add(list);
	}

}
