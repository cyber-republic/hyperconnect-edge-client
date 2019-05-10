package com.hyper.connect.model;


public class Setting{
	private int id;
	private String key;
	private String value;

	public Setting(int id, String key, String value){
		this.id=id;
		this.key=key;
		this.value=value;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getKey(){
		return this.key;
	}

	public String getValue(){
		return this.value;
	}
	
	public void setId(int id){
		this.id=id;
	}
	
	public void setKey(String key){
		this.key=key;
	}

	public void setValue(String value){
		this.value=value;
	}
}