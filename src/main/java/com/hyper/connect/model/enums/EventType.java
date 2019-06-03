package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum EventType{
    @SerializedName("0")
    LOCAL,

    @SerializedName("1")
    GLOBAL;

    public int getValue(){
        switch(this){
            case LOCAL:
                return 0;
            case GLOBAL:
                return 1;
            default:
                throw new IllegalArgumentException("Invalid Event Type");
        }
    }

    public static EventType valueOf(int type){
        switch(type){
            case 0:
                return LOCAL;
            case 1:
                return GLOBAL;
            default:
                throw new IllegalArgumentException("Invalid Event Type");
        }
    }

    @Override
    public String toString(){
        switch(this){
            case LOCAL:
                return "Local";
            case GLOBAL:
                return "Global";
            default:
                throw new IllegalArgumentException("Invalid Event Type");
        }
    }
}
