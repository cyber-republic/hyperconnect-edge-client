package com.hyper.connect.model;

import com.hyper.connect.model.enums.*;

public class Event{
	private int id;
	private String globalEventId;
	private String name;
	private EventType type; /*** local, global ***/
	private EventState state; /*** active, deactivated ***/
	private EventAverage average; /*** real-time, 1m, 5m, 15m, 1h, 3h, 6h, 1d ***/
	private EventCondition condition; /*** equal to, not equal to, greater than, less than ***/
	private String conditionValue;
	private String triggerValue;
	private String sourceDeviceUserId;
	private int sourceEdgeSensorId;
	private int sourceEdgeAttributeId;
	private String actionDeviceUserId;
	private int actionEdgeSensorId;
	private int actionEdgeAttributeId;
	private EventEdgeType edgeType; /*** source, action ***/
	
	public Event(int id, String name){
		this.id=id;
		this.name=name;
	}

	public Event(int id, String globalEventId, String name, EventType type, EventState state, EventAverage average, EventCondition condition, String conditionValue, String triggerValue, String sourceDeviceUserId, int sourceEdgeSensorId, int sourceEdgeAttributeId, String actionDeviceUserId, int actionEdgeSensorId, int actionEdgeAttributeId, EventEdgeType edgeType){
		this.id=id;
		this.globalEventId=globalEventId;
		this.name=name;
		this.type=type;
		this.state=state;
		this.average=average;
		this.condition=condition;
		this.conditionValue=conditionValue;
		this.triggerValue=triggerValue;
		this.sourceDeviceUserId=sourceDeviceUserId;
		this.sourceEdgeSensorId=sourceEdgeSensorId;
		this.sourceEdgeAttributeId=sourceEdgeAttributeId;
		this.actionDeviceUserId=actionDeviceUserId;
		this.actionEdgeSensorId=actionEdgeSensorId;
		this.actionEdgeAttributeId=actionEdgeAttributeId;
		this.edgeType=edgeType;
	}

	public int getId(){
		return this.id;
	}

	public String getGlobalEventId(){
		return globalEventId;
	}

	public String getName(){
		return this.name;
	}

	public EventType getType(){
		return type;
	}

	public EventState getState(){
		return this.state;
	}
	
	public EventAverage getAverage(){
		return this.average;
	}
	
	public EventCondition getCondition(){
		return this.condition;
	}

	public String getConditionValue(){
		return this.conditionValue;
	}

	public String getTriggerValue(){
		return this.triggerValue;
	}

	public String getSourceDeviceUserId(){
		return sourceDeviceUserId;
	}

	public int getSourceEdgeSensorId(){
		return sourceEdgeSensorId;
	}

	public int getSourceEdgeAttributeId(){
		return sourceEdgeAttributeId;
	}

	public String getActionDeviceUserId(){
		return actionDeviceUserId;
	}

	public int getActionEdgeSensorId(){
		return actionEdgeSensorId;
	}

	public int getActionEdgeAttributeId(){
		return actionEdgeAttributeId;
	}

	public EventEdgeType getEdgeType(){
		return edgeType;
	}

	public void setId(int id){
		this.id=id;
	}

	public void setGlobalEventId(String globalEventId){
		this.globalEventId=globalEventId;
	}

	public void setName(String name){
		this.name=name;
	}

	public void setType(EventType type){
		this.type=type;
	}

	public void setState(EventState state){
		this.state=state;
	}
	
	public void setAverage(EventAverage average){
		this.average=average;
	}
	
	public void setCondition(EventCondition condition){
		this.condition=condition;
	}

	public void setConditionValue(String conditionValue){
		this.conditionValue=conditionValue;
	}

	public void setTriggerValue(String triggerValue){
		this.triggerValue=triggerValue;
	}

	public void setSourceDeviceUserId(String sourceDeviceUserId){
		this.sourceDeviceUserId=sourceDeviceUserId;
	}

	public void setSourceEdgeSensorId(int sourceEdgeSensorId){
		this.sourceEdgeSensorId=sourceEdgeSensorId;
	}

	public void setSourceEdgeAttributeId(int sourceEdgeAttributeId){
		this.sourceEdgeAttributeId=sourceEdgeAttributeId;
	}

	public void setActionDeviceUserId(String actionDeviceUserId){
		this.actionDeviceUserId=actionDeviceUserId;
	}

	public void setActionEdgeSensorId(int actionEdgeSensorId){
		this.actionEdgeSensorId=actionEdgeSensorId;
	}

	public void setActionEdgeAttributeId(int actionEdgeAttributeId){
		this.actionEdgeAttributeId=actionEdgeAttributeId;
	}

	public void setEdgeType(EventEdgeType edgeType){
		this.edgeType=edgeType;
	}

	@Override
	public String toString(){
		return "Event{"+
				"id="+id+
				", globalEventId='"+globalEventId+'\''+
				", name='"+name+'\''+
				", type="+type+
				", state="+state+
				", average="+average+
				", condition="+condition+
				", conditionValue='"+conditionValue+'\''+
				", triggerValue='"+triggerValue+'\''+
				", sourceDeviceUserId='"+sourceDeviceUserId+'\''+
				", sourceEdgeSensorId="+sourceEdgeSensorId+
				", sourceEdgeAttributeId="+sourceEdgeAttributeId+
				", actionDeviceUserId='"+actionDeviceUserId+'\''+
				", actionEdgeSensorId="+actionEdgeSensorId+
				", actionEdgeAttributeId="+actionEdgeAttributeId+
				", edgeType="+edgeType+
				'}';
	}
}