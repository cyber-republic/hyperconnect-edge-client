package com.hyper.connect.model;

public class Controller{
	private int id;
	private String userId;
	private String state; /*** active, pending ***/
	
	public Controller(int id, String userId, String state){
		this.id=id;
		this.userId=userId;
		this.state=state;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getUserId(){
		return this.userId;
	}
	
	public String getState(){
		return this.state;
	}
	
	public void setId(int id){
		this.id=id;
	}
	
	public void setUserId(String userId){
		this.userId=userId;
	}
	
	public void setState(String state){
		this.state=state;
	}
}