package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum NotificationType{
    @SerializedName("0")
    SUCCESS,

    @SerializedName("1")
    WARNING,

    @SerializedName("2")
    ERROR;

    public int getValue(){
        switch(this){
            case SUCCESS:
                return 0;
            case WARNING:
                return 1;
            case ERROR:
                return 2;
            default:
                throw new IllegalArgumentException("Invalid Notification Type");
        }
    }

    public static NotificationType valueOf(int type){
        switch(type){
            case 0:
                return SUCCESS;
            case 1:
                return WARNING;
            case 2:
                return ERROR;
            default:
                throw new IllegalArgumentException("Invalid Notification Type");
        }
    }

    @Override
    public String toString(){
        switch(this){
            case SUCCESS:
                return "Success";
            case WARNING:
                return "Warning";
            case ERROR:
                return "Error";
            default:
                throw new IllegalArgumentException("Invalid Notification Type");
        }
    }
}