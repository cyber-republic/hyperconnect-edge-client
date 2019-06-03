package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum ControllerConnectionState{
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
                throw new IllegalArgumentException("Invalid Controller Connection State");
        }
    }

    public static ControllerConnectionState valueOf(int connectionState){
        switch(connectionState){
            case 0:
                return ONLINE;
            case 1:
                return OFFLINE;
            default:
                throw new IllegalArgumentException("Invalid Controller Connection State");
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
                throw new IllegalArgumentException("Invalid Controller Connection State");
        }
    }
}