package com.hyper.connect.model;

public class DataRecord{
	private int id;
	private int attributeId;
	private String dateTime;
	private String value;
	
	public DataRecord(String dateTime, String value){
		this.dateTime=dateTime;
		this.value=value;
	}
	
	public DataRecord(int id, int attributeId, String dateTime, String value){
		this.id=id;
		this.attributeId=attributeId;
		this.dateTime=dateTime;
		this.value=value;
	}
	
	public int getId(){
		return this.id;
	}
	
	public int getAttributeId(){
		return this.attributeId;
	}
	
	public String getDateTime(){
		return this.dateTime;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public void setId(int id){
		this.id=id;
	}
	
	public void setAttributeId(int attributeId){
		this.attributeId=attributeId;
	}
	
	public void setDateTime(String dateTime){
		this.dateTime=dateTime;
	}
	
	public void setValue(String value){
		this.value=value;
	}

	@Override
	public String toString(){
		return "DataRecord{"+
				"id="+id+
				", attributeId="+attributeId+
				", dateTime='"+dateTime+'\''+
				", value='"+value+'\''+
				'}';
	}
}