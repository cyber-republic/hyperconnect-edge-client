package com.hyper.connect.database;

import com.hyper.connect.model.*;
import com.hyper.connect.model.enums.*;
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
			"direction INTEGER NOT NULL,"+
			"type INTEGER NOT NULL,"+
			"interval INTEGER NOT NULL,"+
			"scriptState INTEGER NOT NULL,"+
			"state INTEGER NOT NULL,"+
			"sensorId INTEGER NOT NULL,"+
			"FOREIGN KEY(sensorId) REFERENCES sensors(id)"+
			");";
		String createEventsTableSql="CREATE TABLE IF NOT EXISTS events("+
			"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
			"globalEventId TEXT NOT NULL,"+
			"name TEXT NOT NULL,"+
			"type INTEGER NOT NULL,"+
			"state INTEGER NOT NULL,"+
			"average INTEGER NOT NULL,"+
			"condition INTEGER NOT NULL,"+
			"conditionValue TEXT NOT NULL,"+
			"triggerValue TEXT NOT NULL,"+
			"sourceDeviceUserId TEXT NOT NULL,"+
			"sourceEdgeSensorId INTEGER NOT NULL,"+
			"sourceEdgeAttributeId INTEGER NOT NULL,"+
			"actionDeviceUserId TEXT NOT NULL,"+
			"actionEdgeSensorId INTEGER NOT NULL,"+
			"actionEdgeAttributeId INTEGER NOT NULL,"+
			"edgeType INTEGER NOT NULL,"+
			"FOREIGN KEY(sourceEdgeSensorId) REFERENCES sensors(id),"+
			"FOREIGN KEY(sourceEdgeAttributeId) REFERENCES attributes(id),"+
			"FOREIGN KEY(actionEdgeSensorId) REFERENCES sensors(id),"+
			"FOREIGN KEY(actionEdgeAttributeId) REFERENCES attributes(id)"+
			");";
		String createControllersTableSql="CREATE TABLE IF NOT EXISTS controllers("+
			"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
			"userId TEXT NOT NULL,"+
			"state INTEGER NOT NULL,"+
			"connectionState INTEGER NOT NULL"+
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
			"window INTEGER NOT NULL,"+
			"average INTEGER NOT NULL"+
			");";
		String createDevicesTableSql="CREATE TABLE IF NOT EXISTS devices("+
			"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
			"userId TEXT NOT NULL,"+
			"connectionState INTEGER NOT NULL"+
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
			stmt.execute(createDevicesTableSql);
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
			pstmt.setInt(2, attribute.getDirection().getValue());
            pstmt.setInt(3, attribute.getType().getValue());
			pstmt.setInt(4, attribute.getInterval());
            pstmt.setInt(5, attribute.getScriptState().getValue());
			pstmt.setInt(6, attribute.getState().getValue());
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
			pstmt.setInt(2, attribute.getDirection().getValue());
            pstmt.setInt(3, attribute.getType().getValue());
			pstmt.setInt(4, attribute.getInterval());
            pstmt.setInt(5, attribute.getScriptState().getValue());
			pstmt.setInt(6, attribute.getState().getValue());
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
					AttributeDirection.valueOf(rs.getInt("direction")),
					AttributeType.valueOf(rs.getInt("type")),
					rs.getInt("interval"),
					AttributeScriptState.valueOf(rs.getInt("scriptState")),
					AttributeState.valueOf(rs.getInt("state")),
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
					AttributeDirection.valueOf(rs.getInt("direction")),
					AttributeType.valueOf(rs.getInt("type")),
					rs.getInt("interval"),
					AttributeScriptState.valueOf(rs.getInt("scriptState")),
					AttributeState.valueOf(rs.getInt("state")),
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
	
	public ArrayList<Attribute> getAttributeListBySensorIdAndDirection(int sensorId, AttributeDirection direction){
		ArrayList<Attribute> attributeList=new ArrayList<Attribute>();
		String selectSql="SELECT * FROM attributes WHERE sensorId=? AND direction=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, sensorId);
			pstmt.setInt(2, direction.getValue());
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Attribute attribute=new Attribute(rs.getInt("id"),
					rs.getString("name"),
					AttributeDirection.valueOf(rs.getInt("direction")),
					AttributeType.valueOf(rs.getInt("type")),
					rs.getInt("interval"),
					AttributeScriptState.valueOf(rs.getInt("scriptState")),
					AttributeState.valueOf(rs.getInt("state")),
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
	
	public ArrayList<Attribute> getActiveAttributeListBySensorIdAndDirection(int sensorId, AttributeDirection direction){
		ArrayList<Attribute> attributeList=new ArrayList<Attribute>();
		String selectSql="SELECT * FROM attributes WHERE sensorId=? AND direction=? AND state=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, sensorId);
			pstmt.setInt(2, direction.getValue());
			pstmt.setInt(3, AttributeState.ACTIVE.getValue());
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Attribute attribute=new Attribute(rs.getInt("id"),
					rs.getString("name"),
					AttributeDirection.valueOf(rs.getInt("direction")),
					AttributeType.valueOf(rs.getInt("type")),
					rs.getInt("interval"),
					AttributeScriptState.valueOf(rs.getInt("scriptState")),
					AttributeState.valueOf(rs.getInt("state")),
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
	
	public ArrayList<Attribute> getValidAttributeListBySensorIdAndDirection(int sensorId, AttributeDirection direction){
		ArrayList<Attribute> attributeList=new ArrayList<Attribute>();
		String selectSql="SELECT * FROM attributes WHERE sensorId=? AND direction=? AND scriptState=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, sensorId);
			pstmt.setInt(2, direction.getValue());
			pstmt.setInt(3, AttributeScriptState.VALID.getValue());
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Attribute attribute=new Attribute(rs.getInt("id"),
					rs.getString("name"),
					AttributeDirection.valueOf(rs.getInt("direction")),
					AttributeType.valueOf(rs.getInt("type")),
					rs.getInt("interval"),
					AttributeScriptState.valueOf(rs.getInt("scriptState")),
					AttributeState.valueOf(rs.getInt("state")),
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
					AttributeDirection.valueOf(rs.getInt("direction")),
					AttributeType.valueOf(rs.getInt("type")),
					rs.getInt("interval"),
					AttributeScriptState.valueOf(rs.getInt("scriptState")),
					AttributeState.valueOf(rs.getInt("state")),
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
	
	public int getAttributeCountByState(AttributeState state){
		int count=0;
		String selectSql="SELECT COUNT(*) AS total FROM attributes WHERE state=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, state.getValue());
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
		String insertSql="INSERT INTO events(globalEventId, name, type, state, average, condition, conditionValue, triggerValue, sourceDeviceUserId, sourceEdgeSensorId, sourceEdgeAttributeId, actionDeviceUserId, actionEdgeSensorId, actionEdgeAttributeId, edgeType) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql);
			pstmt.setString(1, event.getGlobalEventId());
			pstmt.setString(2, event.getName());
			pstmt.setInt(3, event.getType().getValue());
			pstmt.setInt(4, event.getState().getValue());
            pstmt.setInt(5, event.getAverage().getValue());
			pstmt.setInt(6, event.getCondition().getValue());
            pstmt.setString(7, event.getConditionValue());
			pstmt.setString(8, event.getTriggerValue());
            pstmt.setString(9, event.getSourceDeviceUserId());
			pstmt.setInt(10, event.getSourceEdgeSensorId());
			pstmt.setInt(11, event.getSourceEdgeAttributeId());
			pstmt.setString(12, event.getActionDeviceUserId());
			pstmt.setInt(13, event.getActionEdgeSensorId());
			pstmt.setInt(14, event.getActionEdgeAttributeId());
			pstmt.setInt(15, event.getEdgeType().getValue());
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
		String updateSql="UPDATE events SET globalEventId=?, name=?, type=?, state=?, average=?, condition=?, conditionValue=?, triggerValue=?, sourceDeviceUserId=?, sourceEdgeSensorId=?, sourceEdgeAttributeId=?, actionDeviceUserId=?, actionEdgeSensorId=?, actionEdgeAttributeId=?, edgeType=? WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
			pstmt.setString(1, event.getGlobalEventId());
			pstmt.setString(2, event.getName());
			pstmt.setInt(3, event.getType().getValue());
			pstmt.setInt(4, event.getState().getValue());
			pstmt.setInt(5, event.getAverage().getValue());
			pstmt.setInt(6, event.getCondition().getValue());
			pstmt.setString(7, event.getConditionValue());
			pstmt.setString(8, event.getTriggerValue());
			pstmt.setString(9, event.getSourceDeviceUserId());
			pstmt.setInt(10, event.getSourceEdgeSensorId());
			pstmt.setInt(11, event.getSourceEdgeAttributeId());
			pstmt.setString(12, event.getActionDeviceUserId());
			pstmt.setInt(13, event.getActionEdgeSensorId());
			pstmt.setInt(14, event.getActionEdgeAttributeId());
			pstmt.setInt(15, event.getEdgeType().getValue());
			pstmt.setInt(16, event.getId());
            pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}

	public boolean setEventListState(EventState state){
		boolean response=true;
		String updateSql="UPDATE events SET state=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
			pstmt.setInt(1, state.getValue());
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}
	
	public boolean setEventStateByEventId(int eventId, EventState state){
		boolean response=true;
		String updateSql="UPDATE events SET state=? WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
			pstmt.setInt(1, state.getValue());
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
					rs.getString("globalEventId"),
					rs.getString("name"),
					EventType.valueOf(rs.getInt("type")),
					EventState.valueOf(rs.getInt("state")),
					EventAverage.valueOf(rs.getInt("average")),
					EventCondition.valueOf(rs.getInt("condition")),
					rs.getString("conditionValue"),
					rs.getString("triggerValue"),
					rs.getString("sourceDeviceUserId"),
					rs.getInt("sourceEdgeSensorId"),
					rs.getInt("sourceEdgeAttributeId"),
					rs.getString("actionDeviceUserId"),
					rs.getInt("actionEdgeSensorId"),
					rs.getInt("actionEdgeAttributeId"),
					EventEdgeType.valueOf(rs.getInt("edgeType")));
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

	public Event getEventByGlobalEventId(String globalEventId){
		Event event=null;
		String selectSql="SELECT * FROM events WHERE globalEventId=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setString(1, globalEventId);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				event=new Event(rs.getInt("id"),
						rs.getString("globalEventId"),
						rs.getString("name"),
						EventType.valueOf(rs.getInt("type")),
						EventState.valueOf(rs.getInt("state")),
						EventAverage.valueOf(rs.getInt("average")),
						EventCondition.valueOf(rs.getInt("condition")),
						rs.getString("conditionValue"),
						rs.getString("triggerValue"),
						rs.getString("sourceDeviceUserId"),
						rs.getInt("sourceEdgeSensorId"),
						rs.getInt("sourceEdgeAttributeId"),
						rs.getString("actionDeviceUserId"),
						rs.getInt("actionEdgeSensorId"),
						rs.getInt("actionEdgeAttributeId"),
						EventEdgeType.valueOf(rs.getInt("edgeType")));
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
	
	public ArrayList<Event> getActiveEventListByAverageAndSourceAttributeId(EventAverage average, int sourceAttributeId){
		ArrayList<Event> eventList=new ArrayList<Event>();
		String selectSql="SELECT * FROM events WHERE average=? AND sourceEdgeAttributeId=? AND state=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, average.getValue());
			pstmt.setInt(2, sourceAttributeId);
			pstmt.setInt(3, EventState.ACTIVE.getValue());
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Event event=new Event(rs.getInt("id"),
						rs.getString("globalEventId"),
						rs.getString("name"),
						EventType.valueOf(rs.getInt("type")),
						EventState.valueOf(rs.getInt("state")),
						EventAverage.valueOf(rs.getInt("average")),
						EventCondition.valueOf(rs.getInt("condition")),
						rs.getString("conditionValue"),
						rs.getString("triggerValue"),
						rs.getString("sourceDeviceUserId"),
						rs.getInt("sourceEdgeSensorId"),
						rs.getInt("sourceEdgeAttributeId"),
						rs.getString("actionDeviceUserId"),
						rs.getInt("actionEdgeSensorId"),
						rs.getInt("actionEdgeAttributeId"),
						EventEdgeType.valueOf(rs.getInt("edgeType")));
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
						rs.getString("globalEventId"),
						rs.getString("name"),
						EventType.valueOf(rs.getInt("type")),
						EventState.valueOf(rs.getInt("state")),
						EventAverage.valueOf(rs.getInt("average")),
						EventCondition.valueOf(rs.getInt("condition")),
						rs.getString("conditionValue"),
						rs.getString("triggerValue"),
						rs.getString("sourceDeviceUserId"),
						rs.getInt("sourceEdgeSensorId"),
						rs.getInt("sourceEdgeAttributeId"),
						rs.getString("actionDeviceUserId"),
						rs.getInt("actionEdgeSensorId"),
						rs.getInt("actionEdgeAttributeId"),
						EventEdgeType.valueOf(rs.getInt("edgeType")));
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

	public ArrayList<Event> getEventListByType(EventType type){
		ArrayList<Event> eventList=new ArrayList<Event>();
		String selectSql="SELECT * FROM events WHERE type=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, type.getValue());
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Event event=new Event(rs.getInt("id"),
						rs.getString("globalEventId"),
						rs.getString("name"),
						EventType.valueOf(rs.getInt("type")),
						EventState.valueOf(rs.getInt("state")),
						EventAverage.valueOf(rs.getInt("average")),
						EventCondition.valueOf(rs.getInt("condition")),
						rs.getString("conditionValue"),
						rs.getString("triggerValue"),
						rs.getString("sourceDeviceUserId"),
						rs.getInt("sourceEdgeSensorId"),
						rs.getInt("sourceEdgeAttributeId"),
						rs.getString("actionDeviceUserId"),
						rs.getInt("actionEdgeSensorId"),
						rs.getInt("actionEdgeAttributeId"),
						EventEdgeType.valueOf(rs.getInt("edgeType")));
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
	
	public ArrayList<Event> getEventListOnlyNameAndIdByAttributeId(int attributeId){
		ArrayList<Event> eventList=new ArrayList<Event>();
		String selectSql="SELECT id, name FROM events WHERE sourceEdgeAttributeId=? OR actionEdgeAttributeId=?;";
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
		String selectSql="SELECT id, name FROM events WHERE state=? AND (sourceEdgeAttributeId=? OR actionEdgeAttributeId=?);";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, EventState.ACTIVE.getValue());
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
	
	public int getEventCountByState(EventState state){
		int count=0;
		String selectSql="SELECT COUNT(*) AS total FROM events WHERE state=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, state.getValue());
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
		String insertSql="INSERT INTO controllers(userId, state, connectionState) VALUES(?,?,?);";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, controller.getUserId());
            pstmt.setInt(2, controller.getState().getValue());
			pstmt.setInt(3, controller.getConnectionState().getValue());
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
		String updateSql="UPDATE controllers SET userId=?, state=?, connectionState=? WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
			pstmt.setString(1, controller.getUserId());
			pstmt.setInt(2, controller.getState().getValue());
			pstmt.setInt(3, controller.getConnectionState().getValue());
			pstmt.setInt(4, controller.getId());
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
				controller=new Controller(rs.getInt("id"),
						rs.getString("userId"),
						ControllerState.valueOf(rs.getInt("state")),
						ControllerConnectionState.valueOf(rs.getInt("connectionState")));
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
				controller=new Controller(rs.getInt("id"),
						rs.getString("userId"),
						ControllerState.valueOf(rs.getInt("state")),
						ControllerConnectionState.valueOf(rs.getInt("connectionState")));
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
				Controller controller=new Controller(rs.getInt("id"),
						rs.getString("userId"),
						ControllerState.valueOf(rs.getInt("state")),
						ControllerConnectionState.valueOf(rs.getInt("connectionState")));
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

	public ArrayList<Controller> getOnlineControllerList(){
		ArrayList<Controller> controllerList=new ArrayList<Controller>();
		String selectSql="SELECT * FROM controllers WHERE connectionState=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, ControllerConnectionState.ONLINE.getValue());
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Controller controller=new Controller(rs.getInt("id"),
						rs.getString("userId"),
						ControllerState.valueOf(rs.getInt("state")),
						ControllerConnectionState.valueOf(rs.getInt("connectionState")));
				controllerList.add(controller);
			}
			rs.close();
			pstmt.close();
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
			pstmt.setInt(3, pinnedChart.getWindow().getValue());
            pstmt.setInt(4, pinnedChart.getAverage().getValue());
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
				pinnedChart=new PinnedChart(rs.getInt("id"),
						rs.getInt("attributeId"),
						rs.getString("attributeName"),
						PinnedChartWindow.valueOf(rs.getInt("window")),
						EventAverage.valueOf(rs.getInt("average")));
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
	
	public PinnedChart getPinnedChartByParameters(int attributeId, PinnedChartWindow window, EventAverage average){
		PinnedChart pinnedChart=null;
		String selectSql="SELECT * FROM pinnedcharts WHERE attributeId=? AND window=? AND average=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, attributeId);
			pstmt.setInt(2, window.getValue());
			pstmt.setInt(3, average.getValue());
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				pinnedChart=new PinnedChart(rs.getInt("id"),
						rs.getInt("attributeId"),
						rs.getString("attributeName"),
						PinnedChartWindow.valueOf(rs.getInt("window")),
						EventAverage.valueOf(rs.getInt("average")));
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
					PinnedChartWindow.valueOf(rs.getInt("window")),
					EventAverage.valueOf(rs.getInt("average")));
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
					PinnedChartWindow.valueOf(rs.getInt("window")),
					EventAverage.valueOf(rs.getInt("average")));
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

	public Device saveDevice(Device device){
		Device newDevice=null;
		String insertSql="INSERT INTO devices(userId, connectionState) VALUES(?,?);";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, device.getUserId());
			pstmt.setInt(2, device.getConnectionState().getValue());
			pstmt.executeUpdate();

			ResultSet rs=pstmt.getGeneratedKeys();
			rs.next();
			int createdId=rs.getInt(1);
			newDevice=device;
			newDevice.setId(createdId);

			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			newDevice=null;
		}
		return newDevice;
	}

	public boolean updateDevice(Device device){
		boolean response=true;
		String updateSql="UPDATE devices SET userId=?, connectionState=? WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
			pstmt.setString(1, device.getUserId());
			pstmt.setInt(2, device.getConnectionState().getValue());
			pstmt.setInt(3, device.getId());
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}

	public boolean setDeviceListConnectionState(DeviceConnectionState connectionState){
		boolean response=true;
		String updateSql="UPDATE devices SET connectionState=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(updateSql);
			pstmt.setInt(1, connectionState.getValue());
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}

	public boolean deleteDeviceByDeviceId(int deviceId){
		boolean response=true;
		String deleteSql="DELETE FROM devices WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(deleteSql);
			pstmt.setInt(1, deviceId);
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			response=false;
		}
		return response;
	}

	public Device getDeviceByDeviceId(int deviceId){
		Device device=null;
		String selectSql="SELECT * FROM devices WHERE id=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, deviceId);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				device=new Device(rs.getInt("id"),
						rs.getString("userId"),
						DeviceConnectionState.valueOf(rs.getInt("connectionState")));
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			device=null;
		}
		return device;
	}

	public Device getDeviceByUserId(String userId){
		Device device=null;
		String selectSql="SELECT * FROM devices WHERE userId=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setString(1, userId);
			ResultSet rs=pstmt.executeQuery();
			if(rs.next()){
				device=new Device(rs.getInt("id"),
						rs.getString("userId"),
						DeviceConnectionState.valueOf(rs.getInt("connectionState")));
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
			device=null;
		}
		return device;
	}

	public ArrayList<Device> getDeviceList(){
		ArrayList<Device> deviceList=new ArrayList<Device>();
		String selectSql="SELECT * FROM devices;";
		try{
			Connection connection=this.getConnection();
			Statement stmt=connection.createStatement();
			ResultSet rs=stmt.executeQuery(selectSql);
			while(rs.next()){
				Device device=new Device(rs.getInt("id"),
						rs.getString("userId"),
						DeviceConnectionState.valueOf(rs.getInt("connectionState")));
				deviceList.add(device);
			}
			rs.close();
			stmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return deviceList;
	}

	public ArrayList<Device> getOnlineDeviceList(){
		ArrayList<Device> deviceList=new ArrayList<Device>();
		String selectSql="SELECT * FROM devices WHERE connectionState=?;";
		try{
			PreparedStatement pstmt=this.getConnection().prepareStatement(selectSql);
			pstmt.setInt(1, DeviceConnectionState.ONLINE.getValue());
			ResultSet rs=pstmt.executeQuery();
			while(rs.next()){
				Device device=new Device(rs.getInt("id"),
						rs.getString("userId"),
						DeviceConnectionState.valueOf(rs.getInt("connectionState")));
				deviceList.add(device);
			}
			rs.close();
			pstmt.close();
		}
		catch(SQLException e){
			System.out.println(e.getMessage());
		}
		return deviceList;
	}
}