package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum AttributeState{
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
                throw new IllegalArgumentException("Invalid Attribute State");
        }
    }

    public static AttributeState valueOf(int state){
        switch(state){
            case 0:
                return ACTIVE;
            case 1:
                return DEACTIVATED;
            default:
                throw new IllegalArgumentException("Invalid Attribute State");
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
                throw new IllegalArgumentException("Invalid Attribute State");
        }
    }
}
