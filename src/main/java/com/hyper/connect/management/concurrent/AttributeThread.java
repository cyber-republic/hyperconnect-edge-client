package com.hyper.connect.management.concurrent;

import com.hyper.connect.database.DatabaseInterface;
import com.hyper.connect.model.Attribute;
import com.hyper.connect.model.Event;
import com.hyper.connect.model.DataRecord;
import com.hyper.connect.management.AttributeManagement;
import com.hyper.connect.management.ScriptManagement;
import com.hyper.connect.management.HistoryManagement;
import com.hyper.connect.management.concurrent.processor.Average;
import com.hyper.connect.management.concurrent.processor.Processor1m;
import com.hyper.connect.management.concurrent.processor.Processor5m;
import com.hyper.connect.management.concurrent.processor.Processor15m;
import com.hyper.connect.management.concurrent.processor.Processor1h;
import com.hyper.connect.management.concurrent.processor.Processor3h;
import com.hyper.connect.management.concurrent.processor.Processor6h;
import com.hyper.connect.management.concurrent.processor.Processor1d;

import java.lang.Thread;
import java.util.Properties;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.google.gson.JsonObject;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Timer;


public class AttributeThread extends Thread{
	private volatile boolean isRunning=true;
	private AttributeManagement attributeManager;
	private Attribute attribute;
	private DatabaseInterface database;
	private ScriptManagement scriptManager;
	private HistoryManagement historyManager;
	
	private String filename;
	private int interval;
	private String topicName;
	private SimpleDateFormat formatter;
	private boolean saveHistory;
	public ConcurrentMap<String, Average> store;
	private ArrayList<Event> eventList;
	private Timer timer;
	private Processor1m processor1m;
	private Processor5m processor5m;
	private Processor15m processor15m;
	private Processor1h processor1h;
	private Processor3h processor3h;
	private Processor6h processor6h;
	private Processor1d processor1d;
	private String currentValue;
	private String currentDateTime;
	
	public AttributeThread(Attribute attribute, AttributeManagement attributeManager){
		this.attribute=attribute;
		this.attributeManager=attributeManager;
		this.database=this.attributeManager.getDatabase();
		this.scriptManager=this.attributeManager.getScriptManager();
		this.historyManager=new HistoryManagement();
		this.filename=this.attribute.getId()+".py";
		this.interval=this.attribute.getInterval()*1000;
		this.formatter=new SimpleDateFormat("yyyy/MM/dd HH:mm");
		this.formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public Attribute getAttribute(){
		return this.attribute;
	}
	
	public DatabaseInterface getDatabase(){
		return this.database;
	}
	
	public HistoryManagement getHistoryManager(){
		return this.historyManager;
	}
	
	public DataRecord getCurrentDataRecord(){
		DataRecord dataRecord=null;
		if(this.currentDateTime!=null){
			dataRecord=new DataRecord(this.currentDateTime, this.currentValue);
		}
		return dataRecord;
	}
	
	public void run(){
		if(this.attribute.getDirection().equals("input")){
			this.saveHistory=true;
			if(this.attribute.getType().equals("string") || this.attribute.getType().equals("boolean")){
				this.saveHistory=false;
			}
			this.initHistory();
		
			while(this.isRunning){
				try{
					String key=this.formatter.format(new Date());
					String value=this.scriptManager.executePythonFile(this.filename).get("result").getAsString();
					this.process(key, value);
					this.currentValue=value;
					this.currentDateTime=key;
					
					Thread.sleep(this.interval);
				}
				catch(NullPointerException npe){}
				catch(InterruptedException ie){
					break;
				}
			}
		}
	}
	
	public void stopThread(){
		this.isRunning=false;
		this.stopScheduler();
		if(this.currentDateTime!=null){
			DataRecord dataRecord=this.database.getDataRecordByAttributeId(this.attribute.getId());
			if(dataRecord==null){
				dataRecord=new DataRecord(0, this.attribute.getId(), this.currentDateTime, this.currentValue);
				this.database.saveDataRecord(dataRecord);
			}
			else{
				dataRecord.setDateTime(this.currentDateTime);
				dataRecord.setValue(this.currentValue);
				this.database.updateDataRecord(dataRecord);
			}
		}
	}
	
	private void initHistory(){
		this.store=new ConcurrentHashMap<String, Average>();
		if(this.saveHistory){
			this.initScheduler();
		}
		this.initEvent();
	}
	
	private void initScheduler(){
		this.timer=new Timer();
		this.processor1m=new Processor1m(this);
		this.timer.schedule(this.processor1m, 0, 1000*60);
		this.processor5m=new Processor5m(this);
		this.timer.schedule(this.processor5m, 0, 1000*60);
		this.processor15m=new Processor15m(this);
		this.timer.schedule(this.processor15m, 0, 1000*60*5);
		this.processor1h=new Processor1h(this);
		this.timer.schedule(this.processor1h, 0, 1000*60*15);
		this.processor3h=new Processor3h(this);
		this.timer.schedule(this.processor3h, 0, 1000*60*60);
		this.processor6h=new Processor6h(this);
		this.timer.schedule(this.processor6h, 0, 1000*60*60*3);
		this.processor1d=new Processor1d(this);
		this.timer.schedule(this.processor1d, 0, 1000*60*60*6);
	}
	
	
	
	public void initEvent(){
		this.eventList=this.database.getActiveEventListByAverageAndSourceAttributeId("real-time", this.attribute.getId());
	}
	
	private void process(String key, String value){
		this.eventCheck(value);
		try{
			double valueDouble=Double.parseDouble(value);
			
			Average avg=this.store.get(key);
			if(avg==null){
				avg=new Average(0, 0.0);
			}
			
			avg.sum+=valueDouble;
			avg.count++;
			this.store.put(key, avg);
		}
		catch(NumberFormatException nfe){}
	}
	
	private void eventCheck(String value){
		for(int i=0;i<this.eventList.size();i++){
			Event event=this.eventList.get(i);
			boolean response=this.historyManager.eventConditionCheck(event, this.attribute.getType(), value);
			if(response){
				this.sendAction(event);
			}
		}
	}
	
	public void sendAction(Event event){
		this.attributeManager.executeAction(event.getActionAttributeId(), event.getTriggerValue());
	}
	
	public void stopScheduler(){
		if(this.saveHistory){
			this.processor1m.cancel();
			this.processor5m.cancel();
			this.processor15m.cancel();
			this.processor1h.cancel();
			this.processor3h.cancel();
			this.processor6h.cancel();
			this.processor1d.cancel();
			this.timer.cancel();
			this.timer.purge();
		}
	}
	
	public void updateEventState(String average){
		if(average.equals("real-time")){
			this.initEvent();
		}
		else if(average.equals("1m")){
			this.processor1m.initEvent();
		}
		else if(average.equals("5m")){
			this.processor5m.initEvent();
		}
		else if(average.equals("15m")){
			this.processor15m.initEvent();
		}
		else if(average.equals("1h")){
			this.processor1h.initEvent();
		}
		else if(average.equals("3h")){
			this.processor3h.initEvent();
		}
		else if(average.equals("6h")){
			this.processor6h.initEvent();
		}
		else if(average.equals("1d")){
			this.processor1d.initEvent();
		}
	}
	
	
}