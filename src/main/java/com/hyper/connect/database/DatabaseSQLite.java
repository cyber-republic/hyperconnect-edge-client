package com.hyper.connect.database;

import com.hyper.connect.model.Sensor;
import com.hyper.connect.model.Attribute;
import com.hyper.connect.model.Event;
import com.hyper.connect.model.Controller;
import com.hyper.connect.model.DataRecord;
import com.hyper.connect.model.Notification;
import com.hyper.connect.model.Setting;
import com.hyper.connect.model.PinnedChart;
import com.hyper.connect.util.CustomUtil;

import java.io.File;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class DatabaseSQLite implements DatabaseInterface{
	private String dbUrl;
	
	public DatabaseSQLite(String dbName){
		File databaseDir=CustomUtil.getDirectoryByName("database");
		File dbFile=new File(databaseDir, dbName);
		this.dbUrl="jdbc:sqlite:"+dbFile.getPath();
		this.createTables();
	}
	
	private Connection getConnection(){
		Connection connection=null;
		try{
			connection=DriverManager.getConnection(this.dbUrl);
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return connection;
	}
	
	private void createTables(){
		String createSensorsTableSql="CREATE TABLE IF NOT EXISTS sensors("+
			"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
			"name TEXT NOT NULL,"+
			"type TEXT NOT NULL"+
			");";
		String createAttributesTableSql="CREATE TABLE IF NOT EXISTS attributes("+
			"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
			"name TEXT NOT NULL,"+
			"direction TEXT NOT NULL,"+
			"type TEXT NOT NULL,"+
			"interval INTEGER NOT NULL,"+
			"scriptState TEXT NOT NULL,"+
			"state TEXT NOT NULL,"+
			"sensorId INTEGER NOT NULL,"+
			"FOREIGN KEY(sensorId) REFERENCES sensors(id)"+
			");";
		String createEventsTableSql="CREATE TABLE IF NOT EXISTS events("+
			"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
			"name TEXT NOT NULL,"+
			"state TEXT NOT NULL,"+
			"average TEXT NOT NULL,"+
			"condition TEXT NOT NULL,"+
			"conditionValue TEXT NOT NULL,"+
			"triggerValue TEXT NOT NULL,"+
			"sourceSensorId INTEGER NOT NULL,"+
			"sourceAttributeId INTEGER NOT NULL,"+
			"actionSensorId INTEGER NOT NULL,"+
			"actionAttributeId INTEGER NOT NULL,"+
			"FOREIGN KEY(sourceSensorId) REFERENCES sensors(id),"+
			"FOREIGN KEY(sourceAttributeId) REFERENCES attributes(id),"+
			"FOREIGN KEY(actionSensorId) REFERENCES sensors(id),"+
			"FOREIGN KEY(actionAttributeId) REFERENCES attributes(id)"+
			");";
		String createControllersTableSql="CREATE TABLE IF NOT EXISTS controllers("+
			"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
			"userId TEXT NOT NULL,"+
			"state TEXT NOT NULL"+
			");";
		String createDataRecordsTableSql="CREATE TABLE IF NOT EXISTS datarecords("+
			"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
			"attributeId INTEGER NOT NULL,"+
			"dateTime TEXT NOT NULL,"+
			"value TEXT NOT NULL,"+
			"FOREIGN KEY(attributeId) REFERENCES attributes(id)"+
			");";
		String createNotificationsTableSql="CREATE TABLE IF NOT EXISTS notifications("+
			"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
			"type TEXT NOT NULL,"+
			"category TEXT NOT NULL,"+
			"message TEXT NOT NULL,"+
			"dateTime TEXT NOT NULL"+
			");";
		String createSettingsTableSql="CREATE TABLE IF NOT EXISTS settings("+
			"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
			"key TEXT NOT NULL,"+
			"value TEXT NOT NULL"+
			");";
		String createPinnedChartsTableSql="CREATE TABLE IF NOT EXISTS pinnedcharts("+
			"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
			"attributeId INTEGER NOT NULL,"+
			"attributeName TEXT NOT NULL,"+
			"window TEXT NOT NULL,"+
			"average TEXT NOT NULL"+
			");";
		try{
			Connection connection=this.getConnection();
			Statement stmt=connection.createStatement();
			stmt.execute(createSensorsTableSql);
			stmt.execute(createAttributesTableSql);
			stmt.execute(createEventsTableSql);
			stmt.execute(createControllersTableSql);
			stmt.execute(createDataRecordsTableSql);
			stmt.execute(createNotificationsTableSql);
			stmt.execute(createSettingsTableSql);
			stmt.execute(createPinnedChartsTableSql);
			stmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
	public Sensor saveSensor(Sensor sensor){
		Sensor newSensor=null;
		String insertSql="INSERT INTO sensors(name, type) VALUES(?,?);";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, sensor.getName());
            pstmt.setString(2, sensor.getType());
            pstmt.executeUpdate();
			
			ResultSet rs=pstmt.getGeneratedKeys();
			rs.next();
			int createdId=rs.getInt(1);
			newSensor=sensor;
			newSensor.setId(createdId);

			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			newSensor=null;
		}
		return newSensor;
	}
	
	public boolean updateSensor(Sensor sensor){
		boolean response=true;
		String updateSql="UPDATE sensors SET name=?, type=? WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
			pstmt.setString(1, sensor.getName());
            pstmt.setString(2, sensor.getType());
			pstmt.setInt(3, sensor.getId());
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public boolean deleteSensorBySensorId(int sensorId){
		boolean response=true;
		String deleteSql="DELETE FROM sensors WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(deleteSql);
			pstmt.setInt(1, sensorId);
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public Sensor getSensorBySensorId(int sensorId){
		Sensor sensor=null;
		String selectSql="SELECT * FROM sensors WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, sensorId);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				sensor=new Sensor(rs.getInt("id"), rs.getString("name"), rs.getString("type"));
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			sensor=null;
		}
		return sensor;
	}
	
	public ArrayList<Sensor> getSensorList(){
		ArrayList<Sensor> sensorList=new ArrayList<Sensor>();
		String selectSql="SELECT * FROM sensors;";
		try{
			Connection connection=this.getConnection();
			Statement stmt=connection.createStatement();
			ResultSet rs=stmt.executeQuery(selectSql);
			while(rs.next()){
				Sensor sensor=new Sensor(rs.getInt("id"), rs.getString("name"), rs.getString("type"));
				sensorList.add(sensor);
			}
			rs.close();
			stmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return sensorList;
	}
	
	public Attribute saveAttribute(Attribute attribute){
		Attribute newAttribute=null;
		String insertSql="INSERT INTO attributes(name, direction, type, interval, scriptState, state, sensorId) VALUES(?,?,?,?,?,?,?);";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, attribute.getName());
			pstmt.setString(2, attribute.getDirection());
            pstmt.setString(3, attribute.getType());
			pstmt.setInt(4, attribute.getInterval());
            pstmt.setString(5, attribute.getScriptState());
			pstmt.setString(6, attribute.getState());
            pstmt.setInt(7, attribute.getSensorId());
            pstmt.executeUpdate();
			
			ResultSet rs=pstmt.getGeneratedKeys();
			rs.next();
			int createdId=rs.getInt(1);
			newAttribute=attribute;
			newAttribute.setId(createdId);
			
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			newAttribute=null;
		}
		return newAttribute;
	}
	
	public boolean updateAttribute(Attribute attribute){
		boolean response=true;
		String updateSql="UPDATE attributes SET name=?, direction=?, type=?, interval=?, scriptState=?, state=?, sensorId=? WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
			pstmt.setString(1, attribute.getName());
			pstmt.setString(2, attribute.getDirection());
            pstmt.setString(3, attribute.getType());
			pstmt.setInt(4, attribute.getInterval());
            pstmt.setString(5, attribute.getScriptState());
			pstmt.setString(6, attribute.getState());
            pstmt.setInt(7, attribute.getSensorId());
			pstmt.setInt(8, attribute.getId());
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public boolean deleteAttributeByAttributeId(int attributeId){
		boolean response=true;
		String deleteSql="DELETE FROM attributes WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(deleteSql);
			pstmt.setInt(1, attributeId);
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public Attribute getAttributeByAttributeId(int attributeId){
		Attribute attribute=null;
		String selectSql="SELECT * FROM attributes WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, attributeId);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				attribute=new Attribute(rs.getInt("id"),
					rs.getString("name"),
					rs.getString("direction"),
					rs.getString("type"),
					rs.getInt("interval"),
					rs.getString("scriptState"),
					rs.getString("state"),
					rs.getInt("sensorId"));
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			attribute=null;
		}
		return attribute;
	}
	
	public ArrayList<Attribute> getAttributeListBySensorId(int sensorId){
		ArrayList<Attribute> attributeList=new ArrayList<Attribute>();
		String selectSql="SELECT * FROM attributes WHERE sensorId=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, sensorId);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Attribute attribute=new Attribute(rs.getInt("id"),
					rs.getString("name"),
					rs.getString("direction"),
					rs.getString("type"),
					rs.getInt("interval"),
					rs.getString("scriptState"),
					rs.getString("state"),
					rs.getInt("sensorId"));
				attributeList.add(attribute);
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return attributeList;
	}
	
	public ArrayList<Attribute> getAttributeListBySensorIdAndDirection(int sensorId, String direction){
		ArrayList<Attribute> attributeList=new ArrayList<Attribute>();
		String selectSql="SELECT * FROM attributes WHERE sensorId=? AND direction=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, sensorId);
			pstmt.setString(2, direction);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Attribute attribute=new Attribute(rs.getInt("id"),
					rs.getString("name"),
					rs.getString("direction"),
					rs.getString("type"),
					rs.getInt("interval"),
					rs.getString("scriptState"),
					rs.getString("state"),
					rs.getInt("sensorId"));
				attributeList.add(attribute);
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return attributeList;
	}
	
	public ArrayList<Attribute> getActiveAttributeListBySensorIdAndDirection(int sensorId, String direction){
		ArrayList<Attribute> attributeList=new ArrayList<Attribute>();
		String selectSql="SELECT * FROM attributes WHERE sensorId=? AND direction=? AND state=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, sensorId);
			pstmt.setString(2, direction);
			pstmt.setString(3, "active");
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Attribute attribute=new Attribute(rs.getInt("id"),
					rs.getString("name"),
					rs.getString("direction"),
					rs.getString("type"),
					rs.getInt("interval"),
					rs.getString("scriptState"),
					rs.getString("state"),
					rs.getInt("sensorId"));
				attributeList.add(attribute);
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return attributeList;
	}
	
	public ArrayList<Attribute> getValidAttributeListBySensorIdAndDirection(int sensorId, String direction){
		ArrayList<Attribute> attributeList=new ArrayList<Attribute>();
		String selectSql="SELECT * FROM attributes WHERE sensorId=? AND direction=? AND scriptState=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, sensorId);
			pstmt.setString(2, direction);
			pstmt.setString(3, "valid");
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Attribute attribute=new Attribute(rs.getInt("id"),
					rs.getString("name"),
					rs.getString("direction"),
					rs.getString("type"),
					rs.getInt("interval"),
					rs.getString("scriptState"),
					rs.getString("state"),
					rs.getInt("sensorId"));
				attributeList.add(attribute);
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return attributeList;
	}
	
	public ArrayList<Attribute> getAttributeList(){
		ArrayList<Attribute> attributeList=new ArrayList<Attribute>();
		String selectSql="SELECT * FROM attributes;";
		try{
			Connection connection=this.getConnection();
			Statement stmt=connection.createStatement();
			ResultSet rs=stmt.executeQuery(selectSql);
			while(rs.next()){
				Attribute attribute=new Attribute(rs.getInt("id"),
					rs.getString("name"),
					rs.getString("direction"),
					rs.getString("type"),
					rs.getInt("interval"),
					rs.getString("scriptState"),
					rs.getString("state"),
					rs.getInt("sensorId"));
				attributeList.add(attribute);
			}
			rs.close();
			stmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return attributeList;
	}
	
	public int getAttributeCount(){
		int count=0;
		String selectSql="SELECT COUNT(*) AS total FROM attributes;";
		try{
			Connection connection=this.getConnection();
			Statement stmt=connection.createStatement();
			ResultSet rs=stmt.executeQuery(selectSql);
			rs.next();
			count=rs.getInt("total");
			
			rs.close();
			stmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			count=0;
		}
		return count;
	}
	
	public int getAttributeCountByState(String state){
		int count=0;
		String selectSql="SELECT COUNT(*) AS total FROM attributes WHERE state=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setString(1, state);
			ResultSet rs=pstmt.executeQuery();
			rs.next();
			count=rs.getInt("total");
			
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			count=0;
		}
		return count;
	}
	
	public Event saveEvent(Event event){
		Event newEvent=null;
		String insertSql="INSERT INTO events(name, state, average, condition, conditionValue, triggerValue, sourceSensorId, sourceAttributeId, actionSensorId, actionAttributeId) VALUES(?,?,?,?,?,?,?,?,?,?);";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql);
			pstmt.setString(1, event.getName());
			pstmt.setString(2, event.getState());
            pstmt.setString(3, event.getAverage());
			pstmt.setString(4, event.getCondition());
            pstmt.setString(5, event.getConditionValue());
			pstmt.setString(6, event.getTriggerValue());
            pstmt.setInt(7, event.getSourceSensorId());
			pstmt.setInt(8, event.getSourceAttributeId());
			pstmt.setInt(9, event.getActionSensorId());
			pstmt.setInt(10, event.getActionAttributeId());
            pstmt.executeUpdate();
			
			ResultSet rs=pstmt.getGeneratedKeys();
			rs.next();
			int createdId=rs.getInt(1);
			newEvent=event;
			newEvent.setId(createdId);
			
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			newEvent=null;
		}
		return newEvent;
	}
	
	public boolean updateEvent(Event event){
		boolean response=true;
		String updateSql="UPDATE events SET name=?, state=?, average=?, condition=?, conditionValue=?, triggerValue=?, sourceSensorId=?, sourceAttributeId=?, actionSensorId=?, actionAttributeId=? WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
			pstmt.setString(1, event.getName());
			pstmt.setString(2, event.getState());
            pstmt.setString(3, event.getAverage());
			pstmt.setString(4, event.getCondition());
            pstmt.setString(5, event.getConditionValue());
			pstmt.setString(6, event.getTriggerValue());
            pstmt.setInt(7, event.getSourceSensorId());
			pstmt.setInt(8, event.getSourceAttributeId());
			pstmt.setInt(9, event.getActionSensorId());
			pstmt.setInt(10, event.getActionAttributeId());
			pstmt.setInt(11, event.getId());
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public boolean setEventStateByEventId(int eventId, String state){
		boolean response=true;
		String updateSql="UPDATE events SET state=? WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
			pstmt.setString(1, state);
			pstmt.setInt(2, eventId);
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public boolean deleteEventByEventId(int eventId){
		boolean response=true;
		String deleteSql="DELETE FROM events WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(deleteSql);
			pstmt.setInt(1, eventId);
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public Event getEventByEventId(int eventId){
		Event event=null;
		String selectSql="SELECT * FROM events WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, eventId);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				event=new Event(rs.getInt("id"),
					rs.getString("name"),
					rs.getString("state"),
					rs.getString("average"),
					rs.getString("condition"),
					rs.getString("conditionValue"),
					rs.getString("triggerValue"),
					rs.getInt("sourceSensorId"),
					rs.getInt("sourceAttributeId"),
					rs.getInt("actionSensorId"),
					rs.getInt("actionAttributeId"));
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			event=null;
		}
		return event;
	}
	
	public ArrayList<Event> getActiveEventListByAverageAndSourceAttributeId(String average, int sourceAttributeId){
		ArrayList<Event> eventList=new ArrayList<Event>();
		String selectSql="SELECT * FROM events WHERE average=? AND sourceAttributeId=? AND state=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setString(1, average);
			pstmt.setInt(2, sourceAttributeId);
			pstmt.setString(3, "active");
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Event event=new Event(rs.getInt("id"),
					rs.getString("name"),
					rs.getString("state"),
					rs.getString("average"),
					rs.getString("condition"),
					rs.getString("conditionValue"),
					rs.getString("triggerValue"),
					rs.getInt("sourceSensorId"),
					rs.getInt("sourceAttributeId"),
					rs.getInt("actionSensorId"),
					rs.getInt("actionAttributeId"));
				eventList.add(event);
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return eventList;
	}
	
	public ArrayList<Event> getEventList(){
		ArrayList<Event> eventList=new ArrayList<Event>();
		String selectSql="SELECT * FROM events;";
		try{
			Connection connection=this.getConnection();
			Statement stmt=connection.createStatement();
			ResultSet rs=stmt.executeQuery(selectSql);
			while(rs.next()){
				Event event=new Event(rs.getInt("id"),
					rs.getString("name"),
					rs.getString("state"),
					rs.getString("average"),
					rs.getString("condition"),
					rs.getString("conditionValue"),
					rs.getString("triggerValue"),
					rs.getInt("sourceSensorId"),
					rs.getInt("sourceAttributeId"),
					rs.getInt("actionSensorId"),
					rs.getInt("actionAttributeId"));
				eventList.add(event);
			}
			rs.close();
			stmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return eventList;
	}
	
	public ArrayList<Event> getEventListOnlyNameAndIdByAttributeId(int attributeId){
		ArrayList<Event> eventList=new ArrayList<Event>();
		String selectSql="SELECT id, name FROM events WHERE sourceAttributeId=? OR actionAttributeId=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, attributeId);
			pstmt.setInt(2, attributeId);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				int id=rs.getInt("id");
				String name=rs.getString("name");
				eventList.add(new Event(id, name));
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return eventList;
	}
	
	public ArrayList<Event> getActiveEventListOnlyNameAndIdByAttributeId(int attributeId){
		ArrayList<Event> eventList=new ArrayList<Event>();
		String selectSql="SELECT id, name FROM events WHERE state=? AND (sourceAttributeId=? OR actionAttributeId=?);";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setString(1, "active");
			pstmt.setInt(2, attributeId);
			pstmt.setInt(3, attributeId);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				int id=rs.getInt("id");
				String name=rs.getString("name");
				eventList.add(new Event(id, name));
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return eventList;
	}
	
	public int getEventCount(){
		int count=0;
		String selectSql="SELECT COUNT(*) AS total FROM events;";
		try{
			Connection connection=this.getConnection();
			Statement stmt=connection.createStatement();
			ResultSet rs=stmt.executeQuery(selectSql);
			rs.next();
			count=rs.getInt("total");
			
			rs.close();
			stmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			count=0;
		}
		return count;
	}
	
	public int getEventCountByState(String state){
		int count=0;
		String selectSql="SELECT COUNT(*) AS total FROM events WHERE state=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setString(1, state);
			ResultSet rs=pstmt.executeQuery();
			rs.next();
			count=rs.getInt("total");
			
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			count=0;
		}
		return count;
	}
	
	public Controller saveController(Controller controller){
		Controller newController=null;
		String insertSql="INSERT INTO controllers(userId, state) VALUES(?,?);";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, controller.getUserId());
            pstmt.setString(2, controller.getState());
            pstmt.executeUpdate();
			
			ResultSet rs=pstmt.getGeneratedKeys();
			rs.next();
			int createdId=rs.getInt(1);
			newController=controller;
			newController.setId(createdId);

			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			newController=null;
		}
		return newController;
	}
	
	public boolean updateController(Controller controller){
		boolean response=true;
		String updateSql="UPDATE controllers SET userId=?, state=? WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
			pstmt.setString(1, controller.getUserId());
            pstmt.setString(2, controller.getState());
			pstmt.setInt(3, controller.getId());
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public boolean deleteControllerByControllerId(int controllerId){
		boolean response=true;
		String deleteSql="DELETE FROM controllers WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(deleteSql);
			pstmt.setInt(1, controllerId);
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public Controller getControllerByControllerId(int controllerId){
		Controller controller=null;
		String selectSql="SELECT * FROM controllers WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, controllerId);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				controller=new Controller(rs.getInt("id"), rs.getString("userId"), rs.getString("state"));
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			controller=null;
		}
		return controller;
	}
	
	public Controller getControllerByUserId(String userId){
		Controller controller=null;
		String selectSql="SELECT * FROM controllers WHERE userId=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setString(1, userId);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				controller=new Controller(rs.getInt("id"), rs.getString("userId"), rs.getString("state"));
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			controller=null;
		}
		return controller;
	}
	
	public ArrayList<Controller> getControllerList(){
		ArrayList<Controller> controllerList=new ArrayList<Controller>();
		String selectSql="SELECT * FROM controllers;";
		try{
			Connection connection=this.getConnection();
			Statement stmt=connection.createStatement();
			ResultSet rs=stmt.executeQuery(selectSql);
			while(rs.next()){
				Controller controller=new Controller(rs.getInt("id"), rs.getString("userId"), rs.getString("state"));
				controllerList.add(controller);
			}
			rs.close();
			stmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return controllerList;
	}
	
	public int getControllerCount(){
		int count=0;
		String selectSql="SELECT COUNT(*) AS total FROM controllers;";
		try{
			Connection connection=this.getConnection();
			Statement stmt=connection.createStatement();
			ResultSet rs=stmt.executeQuery(selectSql);
			rs.next();
			count=rs.getInt("total");
			
			rs.close();
			stmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			count=0;
		}
		return count;
	}
	
	public int getControllerCountByState(String state){
		int count=0;
		String selectSql="SELECT COUNT(*) AS total FROM controllers WHERE state=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setString(1, state);
			ResultSet rs=pstmt.executeQuery();
			rs.next();
			count=rs.getInt("total");
			
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			count=0;
		}
		return count;
	}
	
	public DataRecord saveDataRecord(DataRecord dataRecord){
		DataRecord newDataRecord=null;
		String insertSql="INSERT INTO datarecords(attributeId, dateTime, value) VALUES(?,?,?);";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, dataRecord.getAttributeId());
            pstmt.setString(2, dataRecord.getDateTime());
			pstmt.setString(3, dataRecord.getValue());
            pstmt.executeUpdate();
			
			ResultSet rs=pstmt.getGeneratedKeys();
			rs.next();
			int createdId=rs.getInt(1);
			newDataRecord=dataRecord;
			newDataRecord.setId(createdId);

			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			newDataRecord=null;
		}
		return newDataRecord;
	}
	
	public boolean updateDataRecord(DataRecord dataRecord){
		boolean response=true;
		String updateSql="UPDATE datarecords SET attributeId=?, dateTime=?, value=? WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
			pstmt.setInt(1, dataRecord.getAttributeId());
			pstmt.setString(2, dataRecord.getDateTime());
            pstmt.setString(3, dataRecord.getValue());
			pstmt.setInt(4, dataRecord.getId());
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public boolean deleteDataRecordByAttributeId(int attributeId){
		boolean response=true;
		String deleteSql="DELETE FROM datarecords WHERE attributeId=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(deleteSql);
			pstmt.setInt(1, attributeId);
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public DataRecord getDataRecordByAttributeId(int attributeId){
		DataRecord dataRecord=null;
		String selectSql="SELECT * FROM datarecords WHERE attributeId=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, attributeId);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				dataRecord=new DataRecord(rs.getInt("id"), rs.getInt("attributeId"), rs.getString("dateTime"), rs.getString("value"));
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			dataRecord=null;
		}
		return dataRecord;
	}
	
	public Notification saveNotification(Notification notification){
		Notification newNotification=null;
		String insertSql="INSERT INTO notifications(type, category, message, dateTime) VALUES(?,?,?,?);";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, notification.getType());
            pstmt.setString(2, notification.getCategory());
			pstmt.setString(3, notification.getMessage());
			pstmt.setString(4, notification.getDateTime());
            pstmt.executeUpdate();
			
			ResultSet rs=pstmt.getGeneratedKeys();
			rs.next();
			int createdId=rs.getInt(1);
			newNotification=notification;
			newNotification.setId(createdId);

			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			newNotification=null;
		}
		return newNotification;
	}
	
	public ArrayList<Notification> getNotificationList(){
		ArrayList<Notification> notificationList=new ArrayList<Notification>();
		String selectSql="SELECT * FROM notifications;";
		try{
			Connection connection=this.getConnection();
			Statement stmt=connection.createStatement();
			ResultSet rs=stmt.executeQuery(selectSql);
			while(rs.next()){
				Notification notification=new Notification(rs.getInt("id"),
					rs.getString("type"),
					rs.getString("category"),
					rs.getString("message"),
					rs.getString("dateTime"));
				notificationList.add(notification);
			}
			rs.close();
			stmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return notificationList;
	}
	
	public int getNotificationCountByType(String type){
		int count=0;
		String selectSql="SELECT COUNT(*) AS total FROM notifications WHERE type=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setString(1, type);
			ResultSet rs=pstmt.executeQuery();
			rs.next();
			count=rs.getInt("total");
			
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			count=0;
		}
		return count;
	}
	
	public Setting saveSetting(Setting setting){
		Setting newSetting=null;
		String insertSql="INSERT INTO settings(key, value) VALUES(?,?);";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, setting.getKey());
            pstmt.setString(2, setting.getValue());
            pstmt.executeUpdate();
			
			ResultSet rs=pstmt.getGeneratedKeys();
			rs.next();
			int createdId=rs.getInt(1);
			newSetting=setting;
			newSetting.setId(createdId);

			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			newSetting=null;
		}
		return newSetting;
	}
	
	public boolean updateSetting(Setting setting){
		boolean response=true;
		String updateSql="UPDATE settings SET key=?, value=? WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
			pstmt.setString(1, setting.getKey());
			pstmt.setString(2, setting.getValue());
			pstmt.setInt(3, setting.getId());
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public Setting getSettingByKey(String key){
		Setting setting=null;
		String selectSql="SELECT * FROM settings WHERE key=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setString(1, key);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				setting=new Setting(rs.getInt("id"), rs.getString("key"), rs.getString("value"));
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			setting=null;
		}
		return setting;
	}
	
	public PinnedChart savePinnedChart(PinnedChart pinnedChart){
		PinnedChart newPinnedChart=null;
		String insertSql="INSERT INTO pinnedcharts(attributeId, attributeName, window, average) VALUES(?,?,?,?);";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, pinnedChart.getAttributeId());
            pstmt.setString(2, pinnedChart.getAttributeName());
			pstmt.setString(3, pinnedChart.getWindow());
            pstmt.setString(4, pinnedChart.getAverage());
            pstmt.executeUpdate();
			
			ResultSet rs=pstmt.getGeneratedKeys();
			rs.next();
			int createdId=rs.getInt(1);
			newPinnedChart=pinnedChart;
			newPinnedChart.setId(createdId);

			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			newPinnedChart=null;
		}
		return newPinnedChart;
	}
	
	public boolean deletePinnedChartByPinnedChartId(int pinnedChartId){
		boolean response=true;
		String deleteSql="DELETE FROM pinnedcharts WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(deleteSql);
			pstmt.setInt(1, pinnedChartId);
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public PinnedChart getPinnedChartByAttributeId(int attributeId){
		PinnedChart pinnedChart=null;
		String selectSql="SELECT * FROM pinnedcharts WHERE attributeId=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, attributeId);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				pinnedChart=new PinnedChart(rs.getInt("id"), rs.getInt("attributeId"), rs.getString("attributeName"), rs.getString("window"), rs.getString("average"));
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			pinnedChart=null;
		}
		return pinnedChart;
	}
	
	public PinnedChart getPinnedChartByParameters(int attributeId, String window, String average){
		PinnedChart pinnedChart=null;
		String selectSql="SELECT * FROM pinnedcharts WHERE attributeId=? AND window=? AND average=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, attributeId);
			pstmt.setString(2, window);
			pstmt.setString(3, average);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				pinnedChart=new PinnedChart(rs.getInt("id"), rs.getInt("attributeId"), rs.getString("attributeName"), rs.getString("window"), rs.getString("average"));
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			pinnedChart=null;
		}
		return pinnedChart;
	}
	
	public ArrayList<PinnedChart> getPinnedChartListByAttributeId(int attributeId){
		ArrayList<PinnedChart> pinnedChartList=new ArrayList<PinnedChart>();
		String selectSql="SELECT * FROM pinnedcharts WHERE attributeId=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, attributeId);
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				PinnedChart pinnedChart=new PinnedChart(rs.getInt("id"),
					rs.getInt("attributeId"),
					rs.getString("attributeName"),
					rs.getString("window"),
					rs.getString("average"));
				pinnedChartList.add(pinnedChart);
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return pinnedChartList;
	}
	
	public ArrayList<PinnedChart> getPinnedChartList(){
		ArrayList<PinnedChart> pinnedChartList=new ArrayList<PinnedChart>();
		String selectSql="SELECT * FROM pinnedcharts;";
		try{
			Connection connection=this.getConnection();
			Statement stmt=connection.createStatement();
			ResultSet rs=stmt.executeQuery(selectSql);
			while(rs.next()){
				PinnedChart pinnedChart=new PinnedChart(rs.getInt("id"),
					rs.getInt("attributeId"),
					rs.getString("attributeName"),
					rs.getString("window"),
					rs.getString("average"));
				pinnedChartList.add(pinnedChart);
			}
			rs.close();
			stmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return pinnedChartList;
	}
}