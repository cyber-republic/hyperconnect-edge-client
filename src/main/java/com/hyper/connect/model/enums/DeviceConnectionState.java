package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum DeviceConnectionState{
    @SerializedName("0")
    ONLINE,

    @SerializedName("1")
    OFFLINE;

    public int getValue(){
        switch(this){
            case ONLINE:
                return 0;
            case OFFLINE:
                return 1;
            default:
                throw new IllegalArgumentException("Invalid Device Connection State");
        }
    }

    public static DeviceConnectionState valueOf(int connectionState){
        switch(connectionState){
            case 0:
                return ONLINE;
            case 1:
                return OFFLINE;
            default:
                throw new IllegalArgumentException("Invalid Device Connection State");
        }
    }

    @Override
    public String toString(){
        switch(this){
            case ONLINE:
                return "Online";
            case OFFLINE:
                return "Offline";
            default:
                throw new IllegalArgumentException("Invalid Device Connection State");
        }
    }
}