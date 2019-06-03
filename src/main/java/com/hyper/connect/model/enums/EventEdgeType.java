package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum EventEdgeType{
    @SerializedName("0")
    SOURCE,

    @SerializedName("1")
    ACTION,

    @SerializedName("2")
    SOURCE_AND_ACTION;

    public int getValue(){
        switch(this){
            case SOURCE:
                return 0;
            case ACTION:
                return 1;
            case SOURCE_AND_ACTION:
                return 2;
            default:
                throw new IllegalArgumentException("Invalid Event Edge Type");
        }
    }

    public static EventEdgeType valueOf(int edgeType){
        switch(edgeType){
            case 0:
                return SOURCE;
            case 1:
                return ACTION;
            case 2:
                return SOURCE_AND_ACTION;
            default:
                throw new IllegalArgumentException("Invalid Event Edge Type");
        }
    }

    @Override
    public String toString(){
        switch(this){
            case SOURCE:
                return "Source";
            case ACTION:
                return "Action";
            case SOURCE_AND_ACTION:
                return "Source and Action";
            default:
                throw new IllegalArgumentException("Invalid Event Edge Type");
        }
    }
}
