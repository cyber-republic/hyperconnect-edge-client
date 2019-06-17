package com.hyper.connect.model;

import com.hyper.connect.model.enums.ControllerConnectionState;
import com.hyper.connect.model.enums.ControllerState;

public class Controller{
	private int id;
	private String userId;
	private ControllerState state; /*** active, pending, deactivated ***/
	private ControllerConnectionState connectionState; /*** online, offline ***/

	public Controller(int id, String userId, ControllerState state, ControllerConnectionState connectionState){
		this.id=id;
		this.userId=userId;
		this.state=state;
		this.connectionState=connectionState;
	}

	public int getId(){
		return id;
	}

	public String getUserId(){
		return userId;
	}

	public ControllerState getState(){
		return state;
	}

	public ControllerConnectionState getConnectionState(){
		return connectionState;
	}

	public void setId(int id){
		this.id=id;
	}

	public void setUserId(String userId){
		this.userId=userId;
	}

	public void setState(ControllerState state){
		this.state=state;
	}

	public void setConnectionState(ControllerConnectionState connectionState){
		this.connectionState=connectionState;
	}
}