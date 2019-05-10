package com.hyper.connect.management.concurrent.processor;

import com.google.gson.annotations.Expose;

public class Average{
	@Expose
	public int count;
	
	@Expose
	public double sum;
	
	public Average(int count, double sum){
		this.count=count;
		this.sum=sum;
	}
}