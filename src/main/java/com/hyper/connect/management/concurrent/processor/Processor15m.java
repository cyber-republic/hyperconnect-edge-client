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


public class Processor15m extends TimerTask{
	private AttributeThread attributeThread;
	private Attribute attribute;
	private DatabaseInterface database;
	private HistoryManagement historyManager;
	private String fileName15m;
	private String fileName1h;
	private ArrayList<Event> eventList;
	
	public Processor15m(AttributeThread attributeThread){
		this.attributeThread=attributeThread;
		this.attribute=this.attributeThread.getAttribute();
		this.database=this.attributeThread.getDatabase();
		this.historyManager=this.attributeThread.getHistoryManager();
		this.fileName15m=attribute.getId()+"_15m_temp.json";
		this.fileName1h=attribute.getId()+"_1h_temp.json";
		this.initEvent();
	}
	
	public void initEvent(){
		this.eventList=this.database.getActiveEventListByAverageAndSourceAttributeId("15m", this.attribute.getId());
	}
	
	public void run(){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE)+14)/15*15);
		Date roundedDate=calendar.getTime();
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy/MM/dd HH:mm");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String currentDateTime=formatter.format(roundedDate);
		
		ConcurrentMap<String, Average> store15m=this.historyManager.getHistoryAverage(this.fileName15m);
		for(ConcurrentMap.Entry<String, Average> entry : store15m.entrySet()){
			String key=entry.getKey();
			if(!currentDateTime.equals(key)){
				Average avg=entry.getValue();
				double avgValue=(double)(avg.sum/avg.count);
				String valueString=new BigDecimal(Double.toString(avgValue)).setScale(2, RoundingMode.HALF_UP).toString();
				
				this.historyManager.addHistoryValue(this.attribute.getId(), key, valueString, "15m");
				this.eventCheck(valueString);
				this.sendToProcessor1h(key, valueString);
				store15m.remove(key);
			}
		}
		this.historyManager.saveHistoryAverage(this.fileName15m, store15m);
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
	
	private void sendToProcessor1h(String key, String value){
		ConcurrentMap<String, Average> store1h=this.historyManager.getHistoryAverage(this.fileName1h);
		try{
			SimpleDateFormat formatter=new SimpleDateFormat("yyyy/MM/dd HH:mm");
			Date keyDate=formatter.parse(key);
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(keyDate);
			calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE)+59)/60*60);
			Date roundedDate=calendar.getTime();
			SimpleDateFormat hourFormatter=new SimpleDateFormat("yyyy/MM/dd HH");
			key=hourFormatter.format(roundedDate);
		}
		catch(ParseException pe){}
		
		try{
			Average avg=new Average(0, 0.0);
			double valueDouble=Double.parseDouble(value);
			
			Average storedAvg=store1h.get(key);
			if(storedAvg!=null){
				avg=storedAvg;
			}
			
			avg.sum+=valueDouble;
			avg.count++;
			store1h.put(key, avg);
		}
		catch(NumberFormatException nfe){}
		this.historyManager.saveHistoryAverage(this.fileName1h, store1h);
	}
}
