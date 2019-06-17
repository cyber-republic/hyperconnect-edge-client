package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum ControllerState{
    @SerializedName("0")
    ACTIVE,

    @SerializedName("1")
    PENDING,

    @SerializedName("2")
    DEACTIVATED;

    public int getValue(){
        switch(this){
            case ACTIVE:
                return 0;
            case PENDING:
                return 1;
            case DEACTIVATED:
                return 2;
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
            case 2:
                return DEACTIVATED;
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
            case DEACTIVATED:
                return "Deactivated";
            default:
                throw new IllegalArgumentException("Invalid Controller State");
        }
    }
}