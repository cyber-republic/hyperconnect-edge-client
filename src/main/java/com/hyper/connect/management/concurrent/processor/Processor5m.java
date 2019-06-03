package com.hyper.connect.management.concurrent.processor;

import com.hyper.connect.management.concurrent.AttributeThread;
import com.hyper.connect.database.DatabaseInterface;
import com.hyper.connect.model.Attribute;
import com.hyper.connect.model.Event;
import com.hyper.connect.management.HistoryManagement;

import java.util.concurrent.ConcurrentMap;
import java.util.TimerTask;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.hyper.connect.model.enums.EventAverage;

import java.text.ParseException;
import java.util.ArrayList;


public class Processor5m extends TimerTask{
	private AttributeThread attributeThread;
	private Attribute attribute;
	private DatabaseInterface database;
	private HistoryManagement historyManager;
	private String fileName5m;
	private String fileName15m;
	private ArrayList<Event> eventList;
	
	public Processor5m(AttributeThread attributeThread){
		this.attributeThread=attributeThread;
		this.attribute=this.attributeThread.getAttribute();
		this.database=this.attributeThread.getDatabase();
		this.historyManager=this.attributeThread.getHistoryManager();
		this.fileName5m=attribute.getId()+"_5m_temp.json";
		this.fileName15m=attribute.getId()+"_15m_temp.json";
		this.initEvent();
	}
	
	public void initEvent(){
		this.eventList=this.database.getActiveEventListByAverageAndSourceAttributeId(EventAverage.FIVE_MINUTES, this.attribute.getId());
	}
	
	public void run(){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE)+5)/5*5);
		Date roundedDate=calendar.getTime();
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy/MM/dd HH:mm");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String currentDateTime=formatter.format(roundedDate);
		
		ConcurrentMap<String, Average> store5m=this.historyManager.getHistoryAverage(this.fileName5m);
		for(ConcurrentMap.Entry<String, Average> entry : store5m.entrySet()){
			String key=entry.getKey();
			if(!currentDateTime.equals(key)){
				Average avg=entry.getValue();
				double avgValue=(double)(avg.sum/avg.count);
				String valueString=new BigDecimal(Double.toString(avgValue)).setScale(2, RoundingMode.HALF_UP).toString();
				
				this.historyManager.addHistoryValue(this.attribute.getId(), key, valueString, "5m");
				this.eventCheck(valueString);
				this.sendToProcessor15m(key, valueString);
				store5m.remove(key);
			}
		}
		this.historyManager.saveHistoryAverage(this.fileName5m, store5m);
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
	
	private void sendToProcessor15m(String key, String value){
		ConcurrentMap<String, Average> store15m=this.historyManager.getHistoryAverage(this.fileName15m);
		try{
			SimpleDateFormat formatter=new SimpleDateFormat("yyyy/MM/dd HH:mm");
			Date keyDate=formatter.parse(key);
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(keyDate);
			calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE)+14)/15*15);
			Date roundedDate=calendar.getTime();
			key=formatter.format(roundedDate);
		}
		catch(ParseException pe){}
		
		try{
			Average avg=new Average(0, 0.0);
			double valueDouble=Double.parseDouble(value);
			
			Average storedAvg=store15m.get(key);
			if(storedAvg!=null){
				avg=storedAvg;
			}
			
			avg.sum+=valueDouble;
			avg.count++;
			store15m.put(key, avg);
		}
		catch(NumberFormatException nfe){}
		this.historyManager.saveHistoryAverage(this.fileName15m, store15m);
	}
}
