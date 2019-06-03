package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum AttributeScriptState{
    @SerializedName("0")
    VALID,

    @SerializedName("1")
    INVALID;

    public int getValue(){
        switch(this){
            case VALID:
                return 0;
            case INVALID:
                return 1;
            default:
                throw new IllegalArgumentException("Invalid Attribute Script State");
        }
    }

    public static AttributeScriptState valueOf(int scriptState){
        switch(scriptState){
            case 0:
                return VALID;
            case 1:
                return INVALID;
            default:
                throw new IllegalArgumentException("Invalid Attribute Script State");
        }
    }

    @Override
    public String toString(){
        switch(this){
            case VALID:
                return "Valid";
            case INVALID:
                return "Invalid";
            default:
                throw new IllegalArgumentException("Invalid Attribute Script State");
        }
    }
}