package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum EventCondition{
    @SerializedName("0")
    EQUAL_TO,

    @SerializedName("1")
    NOT_EQUAL_TO,

    @SerializedName("2")
    GREATER_THAN,

    @SerializedName("3")
    LESS_THAN;

    public int getValue(){
        switch(this){
            case EQUAL_TO:
                return 0;
            case NOT_EQUAL_TO:
                return 1;
            case GREATER_THAN:
                return 2;
            case LESS_THAN:
                return 3;
            default:
                throw new IllegalArgumentException("Invalid Event Condition");
        }
    }

    public static EventCondition valueOf(int condition){
        switch(condition){
            case 0:
                return EQUAL_TO;
            case 1:
                return NOT_EQUAL_TO;
            case 2:
                return GREATER_THAN;
            case 3:
                return LESS_THAN;
            default:
                throw new IllegalArgumentException("Invalid Event Condition");
        }
    }

    @Override
    public String toString(){
        switch(this){
            case EQUAL_TO:
                return "equal to";
            case NOT_EQUAL_TO:
                return "not equal to";
            case GREATER_THAN:
                return "greater than";
            case LESS_THAN:
                return "less than";
            default:
                throw new IllegalArgumentException("Invalid Event Condition");
        }
    }
}
