package com.hyper.connect.management.concurrent.processor;

import com.hyper.connect.management.concurrent.AttributeThread;
import com.hyper.connect.database.DatabaseInterface;
import com.hyper.connect.model.Attribute;
import com.hyper.connect.model.Event;
import com.hyper.connect.management.ScriptManagement;
import com.hyper.connect.management.HistoryManagement;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.TimerTask;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Properties;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.text.ParseException;
import java.util.ArrayList;


public class Processor1d extends TimerTask{
	private AttributeThread attributeThread;
	private Attribute attribute;
	private DatabaseInterface database;
	private HistoryManagement historyManager;
	private String fileName1d;
	private ArrayList<Event> eventList;
	
	public Processor1d(AttributeThread attributeThread){
		this.attributeThread=attributeThread;
		this.attribute=this.attributeThread.getAttribute();
		this.database=this.attributeThread.getDatabase();
		this.historyManager=this.attributeThread.getHistoryManager();
		this.fileName1d=attribute.getId()+"_1d_temp.json";
		this.initEvent();
	}
	
	public void initEvent(){
		this.eventList=this.database.getActiveEventListByAverageAndSourceAttributeId("1d", this.attribute.getId());
	}
	
	public void run(){
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy/MM/dd");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String currentDateTime=formatter.format(new Date());
		
		ConcurrentMap<String, Average> store1d=this.historyManager.getHistoryAverage(this.fileName1d);
		for(ConcurrentMap.Entry<String, Average> entry : store1d.entrySet()){
			String key=entry.getKey();
			if(!currentDateTime.equals(key)){
				Average avg=entry.getValue();
				double avgValue=(double)(avg.sum/avg.count);
				String valueString=new BigDecimal(Double.toString(avgValue)).setScale(2, RoundingMode.HALF_UP).toString();
				
				this.historyManager.addHistoryValue(this.attribute.getId(), key, valueString, "1d");
				this.eventCheck(valueString);
				store1d.remove(key);
			}
		}
		this.historyManager.saveHistoryAverage(this.fileName1d, store1d);
	}
	
	private void eventCheck(String value){
		for(int i=0;i<this.eventList.size();i++){
			Event event=this.eventList.get(i);
			boolean response=this.historyManager.eventConditionCheck(event, this.attribute.getType(), value);
			if(response){
				this.attributeThread.sendAction(event);
			}
		}
	}
}
