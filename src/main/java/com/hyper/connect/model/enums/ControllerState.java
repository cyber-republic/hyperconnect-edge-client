package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum ControllerState{
    @SerializedName("0")
    ACTIVE,

    @SerializedName("1")
    PENDING;

    public int getValue(){
        switch(this){
            case ACTIVE:
                return 0;
            case PENDING:
                return 1;
            default:
                throw new IllegalArgumentException("Invalid Controller State");
        }
    }

    public static ControllerState valueOf(int state){
        switch(state){
            case 0:
                return ACTIVE;
            case 1:
                return PENDING;
            default:
                throw new IllegalArgumentException("Invalid Controller State");
        }
    }

    @Override
    public String toString(){
        switch(this){
            case ACTIVE:
                return "Active";
            case PENDING:
                return "Pending";
            default:
                throw new IllegalArgumentException("Invalid Controller State");
        }
    }
}