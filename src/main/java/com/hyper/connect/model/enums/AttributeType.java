package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum AttributeType{
    @SerializedName("0")
    STRING,

    @SerializedName("1")
    BOOLEAN,

    @SerializedName("2")
    INTEGER,

    @SerializedName("3")
    DOUBLE;

    public int getValue(){
        switch(this){
            case STRING:
                return 0;
            case BOOLEAN:
                return 1;
            case INTEGER:
                return 2;
            case DOUBLE:
                return 3;
            default:
                throw new IllegalArgumentException("Invalid Attribute Type");
        }
    }

    public static AttributeType valueOf(int type){
        switch(type){
            case 0:
                return STRING;
            case 1:
                return BOOLEAN;
            case 2:
                return INTEGER;
            case 3:
                return DOUBLE;
            default:
                throw new IllegalArgumentException("Invalid Attribute Type");
        }
    }

    @Override
    public String toString(){
        switch(this){
            case STRING:
                return "String";
            case BOOLEAN:
                return "Boolean";
            case INTEGER:
                return "Integer";
            case DOUBLE:
                return "Double";
            default:
                throw new IllegalArgumentException("Invalid Attribute Type");
        }
    }
}