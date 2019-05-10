package com.hyper.connect.management;

import com.hyper.connect.database.DatabaseInterface;
import com.hyper.connect.model.Attribute;
import com.hyper.connect.model.DataRecord;
import com.hyper.connect.management.concurrent.AttributeThread;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.google.gson.JsonObject;
import java.util.ConcurrentModificationException;


public class AttributeManagement{
	private DatabaseInterface database;
	private ScriptManagement scriptManager;
	private Map<Integer, Attribute> attributeMap;
	private Map<Integer, AttributeThread> attributeThreadMap;
	
	public AttributeManagement(DatabaseInterface database, ScriptManagement scriptManager){
		this.database=database;
		this.scriptManager=scriptManager;
		this.attributeMap=new HashMap<Integer, Attribute>();
		this.attributeThreadMap=new HashMap<Integer, AttributeThread>();
		this.startActives();
	}
	
	public DatabaseInterface getDatabase(){
		return this.database;
	}
	
	public ScriptManagement getScriptManager(){
		return this.scriptManager;
	}
	
	public void startActives(){
		ArrayList<Attribute> attributeList=this.database.getAttributeList();
		for(int j=0;j<attributeList.size();j++){
			Attribute attribute=attributeList.get(j);
			this.attributeMap.put(attribute.getId(), attribute);
			if(attribute.getState().equals("active")){
				this.startAttribute(attribute.getId());
			}
		}
	}
	
	public void stopAll(){
		try{
			for(Map.Entry<Integer, AttributeThread> entry : this.attributeThreadMap.entrySet()){
				this.stopAttribute(entry.getKey());
			}
			attributeThreadMap.clear();
		}
		catch(ConcurrentModificationException cme){}
	}
	
	public void addAttribute(Attribute attribute){
		this.attributeMap.put(attribute.getId(), attribute);
	}
	
	public void deleteAttribute(int id){
		AttributeThread attributeThread=this.attributeThreadMap.get(id);
		if(attributeThread!=null){
			//attributeThread.interrupt();
			attributeThread.stopThread();
		}
		this.attributeMap.remove(id);
		this.attributeThreadMap.remove(id);
	}
	
	public void startAttribute(int id){
		Attribute attribute=this.attributeMap.get(id);
		if(attribute!=null){
			AttributeThread attributeThread=new AttributeThread(attribute, this);
			attributeThread.start();
			this.attributeThreadMap.put(id, attributeThread);
		}
		attribute.setState("active");
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
		this.attributeThreadMap.remove(id);
		this.attributeMap.get(id).setState("deactivated");
	}
	
	public void updateAttributeInterval(int id){
		/*this.deleteAttribute(id);
		this.addAttribute(id);
		this.startAttribute(id);*/
	}
	
	public void updateEventState(int id, String average){
		AttributeThread attributeThread=this.attributeThreadMap.get(id);
		if(attributeThread!=null){
			attributeThread.updateEventState(average);
		}
	}
	
	public boolean executeAction(int id, String triggerValue){
		boolean response=false;
		Attribute attribute=this.attributeMap.get(id);
		if(attribute!=null){
			if(attribute.getState().equals("active")){
				String filename=id+".py";
				JsonObject resultObject=this.scriptManager.executePythonFileWithParameter(filename, triggerValue);
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
		AttributeThread attributeThread=this.attributeThreadMap.get(id);
		if(attributeThread!=null){
			dataRecord=attributeThread.getCurrentDataRecord();
		}
		return dataRecord;
	}
}