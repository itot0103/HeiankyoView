package org.leafdetector.core.data;

public class Data {

	double[] data;
	String name;
	int groupId;
	
	public Data(String name, double[] data){
		this.name=name;
		this.data=data;
	}
	
	public double getData(int i){
		return data[i];
	}
	
	public String getName(){
		return name;
	}
	
	public int getGroupId(){
		return groupId;
	}
	
	public void setGroupId(int groupId){
		this.groupId=groupId;
	}

}
