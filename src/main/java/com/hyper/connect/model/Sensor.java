package com.hyper.connect.model;

import java.util.ArrayList;


public class Sensor{
	private int id;
	private String name;
	private String type;
	private ArrayList<Attribute> attributeList;
	
	public Sensor(int id, String name, String type){
		this.id=id;
		this.name=name;
		this.type=type;
	}
	
	public int getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}

	public String getType(){
		return this.type;
	}
	
	public ArrayList<Attribute> getAttributeList(){
		return this.attributeList;
	}
	
	public void setId(int id){
		this.id=id;
	}

	public void setName(String name){
		this.name=name;
	}

	public void setType(String type){
		this.type=type;
	}
	
	public void setAttributeList(ArrayList<Attribute> attributeList){
		this.attributeList=attributeList;
	}
}