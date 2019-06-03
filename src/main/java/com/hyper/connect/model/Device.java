package com.hyper.connect.model;

import com.hyper.connect.model.enums.DeviceConnectionState;

public class Device{
    private int id;
    private String userId;
    private DeviceConnectionState connectionState;

    public Device(int id, String userId, DeviceConnectionState connectionState){
        this.id=id;
        this.userId=userId;
        this.connectionState=connectionState;
    }

    public int getId(){
        return id;
    }

    public String getUserId(){
        return userId;
    }

    public DeviceConnectionState getConnectionState(){
        return connectionState;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setUserId(String userId){
        this.userId=userId;
    }

    public void setConnectionState(DeviceConnectionState connectionState){
        this.connectionState=connectionState;
    }

    @Override
    public String toString(){
        return "Device{"+
                "id="+id+
                ", userId='"+userId+'\''+
                ", connectionState="+connectionState+
                '}';
    }
}
