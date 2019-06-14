package com.hyper.connect.model;


public class Notification{
	private int id;
	private String type; /*** success, warning, error ***/
	private String category;
	private String message;
	private String dateTime;

	public Notification(int id, String type, String category, String message, String dateTime){
		this.id=id;
		this.type=type;
		this.category=category;
		this.message=message;
		this.dateTime=dateTime;
	}

	public int getId(){
		return this.id;
	}

	public String getType(){
		return this.type;
	}

	public String getCategory(){
		return this.category;
	}

	public String getMessage(){
		return this.message;
	}

	public String getDateTime(){
		return this.dateTime;
	}

	public void setId(int id){
		this.id=id;
	}

	public void setType(String type){
		this.type=type;
	}

	public void setCategory(String category){
		this.category=category;
	}

	public void setMessage(String message){
		this.message=message;
	}

	public void setDateTime(String dateTime){
		this.dateTime=dateTime;
	}
}