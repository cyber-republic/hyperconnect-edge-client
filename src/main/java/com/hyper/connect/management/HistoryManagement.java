package com.hyper.connect.management;

import com.hyper.connect.management.concurrent.processor.Average;
import com.hyper.connect.model.enums.AttributeType;
import com.hyper.connect.model.Event;

import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.io.FileNotFoundException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hyper.connect.model.enums.EventCondition;
import com.hyper.connect.util.CustomUtil;

import java.lang.reflect.Type;


public class HistoryManagement{
	private String historyPath;
	
	public HistoryManagement(){
		this.historyPath=CustomUtil.getDirectoryByName("history").getPath();
		String osType=System.getProperty("os.name");
		if(osType.equals("Linux")){
			this.historyPath+="/";
		}
		else{
			this.historyPath+="\\";
		}
	}
	
	public ConcurrentMap<String, Average> getHistoryAverage(String fileName){
		ConcurrentMap<String, Average> historyAverageMap=null;
		try{
			Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
			BufferedReader br=new BufferedReader(new FileReader(this.historyPath+fileName));
			Type token=new TypeToken<ConcurrentHashMap<String, Average>>(){}.getType();
			historyAverageMap=gson.fromJson(br, token);
		}
		catch(FileNotFoundException fnfe){
			historyAverageMap=new ConcurrentHashMap<String, Average>();
			this.saveHistoryAverage(fileName, historyAverageMap);
		}
		return historyAverageMap;
	}
	
	public void saveHistoryAverage(String fileName, ConcurrentMap<String, Average> historyAverageMap){
		try{
			Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
			String jsonString=gson.toJson(historyAverageMap);
			FileWriter fileWriter=new FileWriter(this.historyPath+fileName);
			fileWriter.write(jsonString);
			fileWriter.close();
		}
		catch(IOException ioe){}
	}
	
	public void addHistoryValue(int attributeId, String key, String value, String average){
		String fileName=attributeId+"_"+average+".json";
		ConcurrentMap<String, String> historyMap=this.getHistory(fileName);
		historyMap.put(key, value);
		this.saveHistory(fileName, historyMap);
	}
	
	public ConcurrentMap<String, String> getHistory(String fileName){
		ConcurrentMap<String, String> historyMap=null;
		try{
			Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
			BufferedReader br=new BufferedReader(new FileReader(this.historyPath+fileName));
			Type token=new TypeToken<ConcurrentHashMap<String, String>>(){}.getType();
			historyMap=gson.fromJson(br, token);
		}
		catch(FileNotFoundException fnfe){
			historyMap=new ConcurrentHashMap<String, String>();
			this.saveHistory(fileName, historyMap);
		}
		return historyMap;
	}
	
	private void saveHistory(String fileName, ConcurrentMap<String, String> historyMap){
		try{
			Gson gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
			String jsonString=gson.toJson(historyMap);
			FileWriter fileWriter=new FileWriter(this.historyPath+fileName);
			fileWriter.write(jsonString);
			fileWriter.close();
		}
		catch(IOException ioe){}
	}
	
	public boolean eventConditionCheck(Event event, AttributeType type, String value){
		EventCondition condition=event.getCondition();
		String eventValue=event.getConditionValue();
		
		try{
			if(type==AttributeType.STRING){
				if(condition==EventCondition.EQUAL_TO){
					if(value.equals(eventValue)){
						return true;
					}
				}
				else if(condition==EventCondition.NOT_EQUAL_TO){
					if(!value.equals(eventValue)){
						return true;
					}
				}
			}
			else if(type==AttributeType.BOOLEAN){
				boolean valueBoolean=false;
				if(value.equals("true") || value.equals("1") || value.equals("True")){
					valueBoolean=true;
				}
				
				boolean eventValueBoolean=false;
				if(eventValue.equals("true") || eventValue.equals("1") || eventValue.equals("True")){
					eventValueBoolean=true;
				}
				
				if(condition==EventCondition.EQUAL_TO){
					if(valueBoolean==eventValueBoolean){
						return true;
					}
				}
				else if(condition==EventCondition.NOT_EQUAL_TO){
					if(valueBoolean!=eventValueBoolean){
						return true;
					}
				}
			}
			else if(type==AttributeType.INTEGER){
				int valueInt=Integer.parseInt(value);
				int eventValueInt=Integer.parseInt(eventValue);
				if(condition==EventCondition.EQUAL_TO){
					if(valueInt==eventValueInt){
						return true;
					}
				}
				else if(condition==EventCondition.NOT_EQUAL_TO){
					if(valueInt!=eventValueInt){
						return true;
					}
				}
				else if(condition==EventCondition.GREATER_THAN){
					if(valueInt>eventValueInt){
						return true;
					}
				}
				else if(condition==EventCondition.LESS_THAN){
					if(valueInt<eventValueInt){
						return true;
					}
				}
			}
			else if(type==AttributeType.DOUBLE){
				double valueDouble=Double.parseDouble(value);
				double eventValueDouble=Double.parseDouble(eventValue);
				if(condition==EventCondition.EQUAL_TO){
					if(valueDouble==eventValueDouble){
						return true;
					}
				}
				else if(condition==EventCondition.NOT_EQUAL_TO){
					if(valueDouble!=eventValueDouble){
						return true;
					}
				}
				else if(condition==EventCondition.GREATER_THAN){
					if(valueDouble>eventValueDouble){
						return true;
					}
				}
				else if(condition==EventCondition.LESS_THAN){
					if(valueDouble<eventValueDouble){
						return true;
					}
				}
			}
		}
		catch(NumberFormatException nfe){}
		return false;
	}
}