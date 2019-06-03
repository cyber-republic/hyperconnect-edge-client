package com.hyper.connect.management;

import com.hyper.connect.database.DatabaseInterface;
import com.hyper.connect.elastos.ElastosCarrier;
import com.hyper.connect.model.Attribute;
import com.hyper.connect.model.enums.AttributeState;
import com.hyper.connect.model.DataRecord;
import com.hyper.connect.management.concurrent.AttributeThread;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.JsonObject;
import com.hyper.connect.model.enums.EventAverage;

import java.util.ConcurrentModificationException;


public class AttributeManagement{
	private ElastosCarrier elastosCarrier;
	private DatabaseInterface database;
	private ScriptManagement scriptManager;
	private Map<Integer, Attribute> attributeMap;
	private Map<Integer, AttributeThread> attributeThreadMap;
	
	public AttributeManagement(ElastosCarrier elastosCarrier, DatabaseInterface database, ScriptManagement scriptManager){
		this.elastosCarrier=elastosCarrier;
		this.database=database;
		this.scriptManager=scriptManager;
		this.attributeMap=new HashMap<Integer, Attribute>();
		this.attributeThreadMap=new HashMap<Integer, AttributeThread>();
		this.startActives();
	}

	public ElastosCarrier getElastosCarrier(){
		return elastosCarrier;
	}

	public DatabaseInterface getDatabase(){
		return database;
	}
	
	public ScriptManagement getScriptManager(){
		return scriptManager;
	}
	
	public void startActives(){
		ArrayList<Attribute> attributeList=database.getAttributeList();
		for(Attribute attribute : attributeList){
			attributeMap.put(attribute.getId(), attribute);
			if(attribute.getState()==AttributeState.ACTIVE){
				startAttribute(attribute.getId());
			}
		}
	}
	
	public void stopAll(){
		try{
			for(Map.Entry<Integer, AttributeThread> entry : attributeThreadMap.entrySet()){
				stopAttribute(entry.getKey());
			}
			attributeThreadMap.clear();
		}
		catch(ConcurrentModificationException cme){}
	}
	
	public void addAttribute(Attribute attribute){
		attributeMap.put(attribute.getId(), attribute);
	}
	
	public void deleteAttribute(int id){
		AttributeThread attributeThread=attributeThreadMap.get(id);
		if(attributeThread!=null){
			//attributeThread.interrupt();
			attributeThread.stopThread();
		}
		attributeMap.remove(id);
		attributeThreadMap.remove(id);
	}
	
	public void startAttribute(int id){
		Attribute attribute=this.attributeMap.get(id);
		if(attribute!=null){
			AttributeThread attributeThread=new AttributeThread(attribute, this);
			attributeThread.start();
			attributeThreadMap.put(id, attributeThread);
		}
	}
	
	public void stopAttribute(int id){
		AttributeThread attributeThread=this.attributeThreadMap.get(id);
		if(attributeThread!=null){
			//attributeThread.interrupt();
			attributeThread.stopThread();
			/*try{
				attributeThread.join();
			}
			catch(InterruptedException ie){}*/
		}
		attributeThreadMap.remove(id);
	}
	
	public void updateAttributeInterval(int id){
		/*this.deleteAttribute(id);
		this.addAttribute(id);
		this.startAttribute(id);*/
	}
	
	public void updateEventState(int id, EventAverage average){
		AttributeThread attributeThread=attributeThreadMap.get(id);
		if(attributeThread!=null){
			attributeThread.updateEventState(average);
		}
	}
	
	public boolean executeAction(int id, String triggerValue){
		boolean response=false;
		Attribute attribute=attributeMap.get(id);
		if(attribute!=null){
			if(attribute.getState()==AttributeState.ACTIVE){
				String filename=id+".py";
				JsonObject resultObject=scriptManager.executePythonFileWithParameter(filename, triggerValue);
				String error=resultObject.get("error").getAsString();
				if(error.isEmpty()){
					response=true;
				}
			}
		}
		return response;
	}
	
	public DataRecord getCurrentDataRecord(int id){
		DataRecord dataRecord=null;
		AttributeThread attributeThread=attributeThreadMap.get(id);
		if(attributeThread!=null){
			dataRecord=attributeThread.getCurrentDataRecord();
		}
		return dataRecord;
	}
}