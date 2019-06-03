package com.hyper.connect.model.enums;

import com.google.gson.annotations.SerializedName;

public enum AttributeDirection{
    @SerializedName("0")
    INPUT,

    @SerializedName("1")
    OUTPUT;

    public int getValue(){
        switch(this){
            case INPUT:
                return 0;
            case OUTPUT:
                return 1;
            default:
                throw new IllegalArgumentException("Invalid Attribute Direction");
        }
    }

    public static AttributeDirection valueOf(int direction){
        switch(direction){
            case 0:
                return INPUT;
            case 1:
                return OUTPUT;
            default:
                throw new IllegalArgumentException("Invalid Attribute Direction");
        }
    }

    @Override
    public String toString(){
        switch(this){
            case INPUT:
                return "Input";
            case OUTPUT:
                return "Output";
            default:
                throw new IllegalArgumentException("Invalid Attribute Direction");
        }
    }
}