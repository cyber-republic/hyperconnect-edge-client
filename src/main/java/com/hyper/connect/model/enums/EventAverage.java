package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum EventAverage{
    @SerializedName("0")
    REAL_TIME,

    @SerializedName("1")
    ONE_MINUTE,

    @SerializedName("2")
    FIVE_MINUTES,

    @SerializedName("3")
    FIFTEEN_MINUTES,

    @SerializedName("4")
    ONE_HOUR,

    @SerializedName("5")
    THREE_HOURS,

    @SerializedName("6")
    SIX_HOURS,

    @SerializedName("7")
    ONE_DAY;

    public int getValue(){
        switch(this){
            case REAL_TIME:
                return 0;
            case ONE_MINUTE:
                return 1;
            case FIVE_MINUTES:
                return 2;
            case FIFTEEN_MINUTES:
                return 3;
            case ONE_HOUR:
                return 4;
            case THREE_HOURS:
                return 5;
            case SIX_HOURS:
                return 6;
            case ONE_DAY:
                return 7;
            default:
                throw new IllegalArgumentException("Invalid Event Average");
        }
    }

    public static EventAverage valueOf(int average){
        switch(average){
            case 0:
                return REAL_TIME;
            case 1:
                return ONE_MINUTE;
            case 2:
                return FIVE_MINUTES;
            case 3:
                return FIFTEEN_MINUTES;
            case 4:
                return ONE_HOUR;
            case 5:
                return THREE_HOURS;
            case 6:
                return SIX_HOURS;
            case 7:
                return ONE_DAY;
            default:
                throw new IllegalArgumentException("Invalid Event Average");
        }
    }

    public String getShortFilename(){
        switch(this){
            case REAL_TIME:
                return "";
            case ONE_MINUTE:
                return "1m";
            case FIVE_MINUTES:
                return "5m";
            case FIFTEEN_MINUTES:
                return "15m";
            case ONE_HOUR:
                return "1h";
            case THREE_HOURS:
                return "3h";
            case SIX_HOURS:
                return "6h";
            case ONE_DAY:
                return "1d";
            default:
                throw new IllegalArgumentException("Invalid Event Average");
        }
    }

    @Override
    public String toString(){
        switch(this){
            case REAL_TIME:
                return "Real-Time";
            case ONE_MINUTE:
                return "1 Minute";
            case FIVE_MINUTES:
                return "5 Minutes";
            case FIFTEEN_MINUTES:
                return "15 Minutes";
            case ONE_HOUR:
                return "1 Hour";
            case THREE_HOURS:
                return "3 Hours";
            case SIX_HOURS:
                return "6 Hours";
            case ONE_DAY:
                return "1 Day";
            default:
                throw new IllegalArgumentException("Invalid Event Average");
        }
    }
}
