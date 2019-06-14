package com.hyper.connect.model;


import com.hyper.connect.model.enums.NotificationCategory;
import com.hyper.connect.model.enums.NotificationType;

public class Notification{
	private int id;
	private NotificationType type; /*** success, warning, error ***/
	private NotificationCategory category; /*** device, sensor, attribute, event, system ***/
	private String edgeThingId; /*** device(deviceUserId), sensor(sensorId), attribute(attributeId), event(globalEventId) ***/
	private String message;
	private String dateTime;

	public Notification(int id, NotificationType type, NotificationCategory category, String edgeThingId, String message, String dateTime){
		this.id=id;
		this.type=type;
		this.category=category;
		this.edgeThingId=edgeThingId;
		this.message=message;
		this.dateTime=dateTime;
	}

	public int getId(){
		return this.id;
	}

	public NotificationType getType(){
		return this.type;
	}

	public NotificationCategory getCategory(){
		return this.category;
	}

	public String getEdgeThingId(){
		return edgeThingId;
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

	public void setType(NotificationType type){
		this.type=type;
	}

	public void setCategory(NotificationCategory category){
		this.category=category;
	}

	public void setEdgeThingId(String edgeThingId){
		this.edgeThingId=edgeThingId;
	}

	public void setMessage(String message){
		this.message=message;
	}

	public void setDateTime(String dateTime){
		this.dateTime=dateTime;
	}
}