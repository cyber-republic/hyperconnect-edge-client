package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum PinnedChartWindow{
    @SerializedName("0")
    HOUR,

    @SerializedName("1")
    DAY,

    @SerializedName("2")
    MONTH;

    public int getValue(){
        switch(this){
            case HOUR:
                return 0;
            case DAY:
                return 1;
            case MONTH:
                return 2;
            default:
                throw new IllegalArgumentException("Invalid Pinned Chart Window");
        }
    }

    public static PinnedChartWindow valueOf(int window){
        switch(window){
            case 0:
                return HOUR;
            case 1:
                return DAY;
            case 2:
                return MONTH;
            default:
                throw new IllegalArgumentException("Invalid Pinned Chart Window");
        }
    }

    @Override
    public String toString(){
        switch(this){
            case HOUR:
                return "Hour";
            case DAY:
                return "Day";
            case MONTH:
                return "Month";
            default:
                throw new IllegalArgumentException("Invalid Pinned Chart Window");
        }
    }
}
