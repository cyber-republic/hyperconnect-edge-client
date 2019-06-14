package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum NotificationCategory{
    @SerializedName("0")
    DEVICE,

    @SerializedName("1")
    SENSOR,

    @SerializedName("2")
    ATTRIBUTE,

    @SerializedName("3")
    EVENT,

    @SerializedName("4")
    SYSTEM;

    public int getValue(){
        switch(this){
            case DEVICE:
                return 0;
            case SENSOR:
                return 1;
            case ATTRIBUTE:
                return 2;
            case EVENT:
                return 3;
            case SYSTEM:
                return 4;
            default:
                throw new IllegalArgumentException("Invalid Notification Category");
        }
    }

    public static NotificationCategory valueOf(int category){
        switch(category){
            case 0:
                return DEVICE;
            case 1:
                return SENSOR;
            case 2:
                return ATTRIBUTE;
            case 3:
                return EVENT;
            case 4:
                return SYSTEM;
            default:
                throw new IllegalArgumentException("Invalid Notification Category");
        }
    }

    @Override
    public String toString(){
        switch(this){
            case DEVICE:
                return "Device";
            case SENSOR:
                return "Sensor";
            case ATTRIBUTE:
                return "Attribute";
            case EVENT:
                return "Event";
            case SYSTEM:
                return "System";
            default:
                throw new IllegalArgumentException("Invalid Notification Category");
        }
    }
}