package com.hyper.connect.model;

public class Event{
	private int id;
	private String name;
	private String state; /*** active, deactivated ***/
	private String average; /*** real-time, 1m, 5m, 15m, 1h, 3h, 6h, 1d ***/
	private String condition; /*** equal to, not equal to, greater than, less than ***/
	private String conditionValue;
	private String triggerValue;
	private int sourceSensorId;
	private int sourceAttributeId;
	private int actionSensorId;
	private int actionAttributeId;
	
	public Event(int id, String name){
		this.id=id;
		this.name=name;
	}
	
	public Event(int id, String name, String state, String average, String condition, String conditionValue, String triggerValue, int sourceSensorId, int sourceAttributeId, int actionSensorId, int actionAttributeId){
		this.id=id;
		this.name=name;
		this.state=state;
		this.average=average;
		this.condition=condition;
		this.conditionValue=conditionValue;
		this.triggerValue=triggerValue;
		this.sourceSensorId=sourceSensorId;
		this.sourceAttributeId=sourceAttributeId;
		this.actionSensorId=actionSensorId;
		this.actionAttributeId=actionAttributeId;
	}
	
	public int getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}

	public String getState(){
		return this.state;
	}
	
	public String getAverage(){
		return this.average;
	}
	
	public String getCondition(){
		return this.condition;
	}

	public String getConditionValue(){
		return this.conditionValue;
	}

	public String getTriggerValue(){
		return this.triggerValue;
	}
	
	public int getSourceSensorId(){
		return this.sourceSensorId;
	}
	
	public int getSourceAttributeId(){
		return this.sourceAttributeId;
	}
	
	public int getActionSensorId(){
		return this.actionSensorId;
	}
	
	public int getActionAttributeId(){
		return this.actionAttributeId;
	}
	
	public void setId(int id){
		this.id=id;
	}

	public void setName(String name){
		this.name=name;
	}

	public void setState(String state){
		this.state=state;
	}
	
	public void setAverage(String average){
		this.average=average;
	}
	
	public void setCondition(String condition){
		this.condition=condition;
	}

	public void setConditionValue(String conditionValue){
		this.conditionValue=conditionValue;
	}

	public void setTriggerValue(String triggerValue){
		this.triggerValue=triggerValue;
	}
	
	public void setSourceSensorId(int sourceSensorId){
		this.sourceSensorId=sourceSensorId;
	}
	
	public void setSourceAttributeId(int sourceAttributeId){
		this.sourceAttributeId=sourceAttributeId;
	}
	
	public void setActionSensorId(int actionSensorId){
		this.actionSensorId=actionSensorId;
	}
	
	public void setActionAttributeId(int actionAttributeId){
		this.actionAttributeId=actionAttributeId;
	}
	
	@Override
	public String toString(){
		String event="id: "+id+", "+
			"name: "+name+", "+
			"state: "+state+", "+
			"average: "+average+", "+
			"condition: "+condition+", "+
			"conditionValue: "+conditionValue+", "+
			"triggerValue: "+triggerValue+", "+
			"sourceSensorId: "+sourceSensorId+", "+
			"sourceAttributeId: "+sourceAttributeId+", "+
			"actionSensorId: "+actionSensorId+", "+
			"actionAttributeId: "+actionAttributeId;
		return event;
	}
}