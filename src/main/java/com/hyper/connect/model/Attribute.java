package com.hyper.connect.model;

public class Attribute{
	private int id;
	private String name;
	private String direction; /*** input, output ***/
	private String type; /*** string, boolean, integer, double ***/
	private int interval;
	private String scriptState; /*** valid, invalid ***/
	private String state; /*** active, deactivated ***/
	private int sensorId;
	
	public Attribute(int id, String name, String direction, String type, int interval, String scriptState, String state, int sensorId){
		this.id=id;
		this.name=name;
		this.direction=direction;
		this.type=type;
		this.interval=interval;
		this.scriptState=scriptState;
		this.state=state;
		this.sensorId=sensorId;
	}
	
	public int getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}

	public String getDirection(){
		return this.direction;
	}

	public String getType(){
		return this.type;
	}
	
	public int getInterval(){
		return this.interval;
	}
	
	public String getScriptState(){
		return this.scriptState;
	}
	
	public String getState(){
		return this.state;
	}

	public int getSensorId(){
		return this.sensorId;
	}
	
	public void setId(int id){
		this.id=id;
	}

	public void setName(String name){
		this.name=name;
	}

	public void setDirection(String direction){
		this.direction=direction;
	}

	public void setType(String type){
		this.type=type;
	}
	
	public void setInterval(int interval){
		this.interval=interval;
	}
	
	public void setScriptState(String scriptState){
		this.scriptState=scriptState;
	}
	
	public void setState(String state){
		this.state=state;
	}

	public void setSensorId(int sensorId){
		this.sensorId=sensorId;
	}
}