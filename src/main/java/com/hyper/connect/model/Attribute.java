package com.hyper.connect.model;

import com.hyper.connect.model.enums.AttributeDirection;
import com.hyper.connect.model.enums.AttributeScriptState;
import com.hyper.connect.model.enums.AttributeState;
import com.hyper.connect.model.enums.AttributeType;

public class Attribute{
	private int id;
	private String name;
	private AttributeDirection direction; /*** input, output ***/
	private AttributeType type; /*** string, boolean, integer, double ***/
	private int interval;
	private AttributeScriptState scriptState; /*** valid, invalid ***/
	private AttributeState state; /*** active, deactivated ***/
	private int sensorId;
	
	public Attribute(int id, String name, AttributeDirection direction, AttributeType type, int interval, AttributeScriptState scriptState, AttributeState state, int sensorId){
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

	public AttributeDirection getDirection(){
		return this.direction;
	}

	public AttributeType getType(){
		return this.type;
	}
	
	public int getInterval(){
		return this.interval;
	}
	
	public AttributeScriptState getScriptState(){
		return this.scriptState;
	}
	
	public AttributeState getState(){
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

	public void setDirection(AttributeDirection direction){
		this.direction=direction;
	}

	public void setType(AttributeType type){
		this.type=type;
	}
	
	public void setInterval(int interval){
		this.interval=interval;
	}
	
	public void setScriptState(AttributeScriptState scriptState){
		this.scriptState=scriptState;
	}
	
	public void setState(AttributeState state){
		this.state=state;
	}

	public void setSensorId(int sensorId){
		this.sensorId=sensorId;
	}
}