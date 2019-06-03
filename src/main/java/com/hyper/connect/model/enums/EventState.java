package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum EventState{
    @SerializedName("0")
    ACTIVE,

    @SerializedName("1")
    DEACTIVATED;

    public int getValue(){
        switch(this){
            case ACTIVE:
                return 0;
            case DEACTIVATED:
                return 1;
            default:
                throw new IllegalArgumentException("Invalid Event State");
        }
    }

    public static EventState valueOf(int state){
        switch(state){
            case 0:
                return ACTIVE;
            case 1:
                return DEACTIVATED;
            default:
                throw new IllegalArgumentException("Invalid Event State");
        }
    }

    @Override
    public String toString(){
        switch(this){
            case ACTIVE:
                return "Active";
            case DEACTIVATED:
                return "Deactivated";
            default:
                throw new IllegalArgumentException("Invalid Event State");
        }
    }
}
