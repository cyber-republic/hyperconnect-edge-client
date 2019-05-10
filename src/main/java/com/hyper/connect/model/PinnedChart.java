package com.hyper.connect.model;


public class PinnedChart{
	private int id;
	private int attributeId;
	private String attributeName;
	private String window;
	private String average;

	public PinnedChart(int id, int attributeId, String attributeName, String window, String average){
		this.id=id;
		this.attributeId=attributeId;
		this.attributeName=attributeName;
		this.window=window;
		this.average=average;
	}

	public int getId(){
		return this.id;
	}

	public int getAttributeId(){
		return this.attributeId;
	}

	public String getAttributeName(){
		return this.attributeName;
	}

	public String getWindow(){
		return this.window;
	}

	public String getAverage(){
		return this.average;
	}

	public void setId(int id){
		this.id=id;
	}

	public void setAttributeId(int attributeId){
		this.attributeId=attributeId;
	}

	public void setAttributeName(String attributeName){
		this.attributeName=attributeName;
	}

	public void setWindow(String window){
		this.window=window;
	}

	public void setAverage(String average){
		this.average=average;
	}
	
	@Override
	public String toString(){
		String pinnedChart="id: "+id+", "+
			"attributeId: "+attributeId+", "+
			"attributeName: "+attributeName+", "+
			"window: "+window+", "+
			"average: "+average;
		return pinnedChart;
	}
}