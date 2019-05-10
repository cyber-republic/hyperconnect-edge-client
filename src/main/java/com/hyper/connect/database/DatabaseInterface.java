package com.hyper.connect.database;

import com.hyper.connect.model.Sensor;
import com.hyper.connect.model.Attribute;
import com.hyper.connect.model.Event;
import com.hyper.connect.model.Controller;
import com.hyper.connect.model.DataRecord;
import com.hyper.connect.model.Notification;
import com.hyper.connect.model.Setting;
import com.hyper.connect.model.PinnedChart;
import java.util.ArrayList;


public interface DatabaseInterface{
	
	public Sensor saveSensor(Sensor sensor);
	
	public boolean updateSensor(Sensor sensor);
	
	public boolean deleteSensorBySensorId(int sensorId);
	
	public Sensor getSensorBySensorId(int sensorId);
	
	public ArrayList<Sensor> getSensorList();
	
	
	
	public Attribute saveAttribute(Attribute attribute);
	
	public boolean updateAttribute(Attribute attribute);
	
	public boolean deleteAttributeByAttributeId(int attributeId);
	
	public Attribute getAttributeByAttributeId(int attributeId);
	
	public ArrayList<Attribute> getAttributeListBySensorId(int sensorId);
	
	public ArrayList<Attribute> getAttributeListBySensorIdAndDirection(int sensorId, String direction);
	
	public ArrayList<Attribute> getActiveAttributeListBySensorIdAndDirection(int sensorId, String direction);
	
	public ArrayList<Attribute> getValidAttributeListBySensorIdAndDirection(int sensorId, String direction);
	
	public ArrayList<Attribute> getAttributeList();
	
	public int getAttributeCount();
	
	public int getAttributeCountByState(String state);
	
	
	
	public Event saveEvent(Event event);
	
	public boolean updateEvent(Event event);
	
	public boolean setEventStateByEventId(int eventId, String state);
	
	public boolean deleteEventByEventId(int eventId);
	
	public Event getEventByEventId(int eventId);
	
	public ArrayList<Event> getActiveEventListByAverageAndSourceAttributeId(String average, int sourceAttributeId);
	
	public ArrayList<Event> getEventList();
	
	public ArrayList<Event> getEventListOnlyNameAndIdByAttributeId(int attributeId);
	
	public ArrayList<Event> getActiveEventListOnlyNameAndIdByAttributeId(int attributeId);
	
	public int getEventCount();
	
	public int getEventCountByState(String state);
	
	
	
	public Controller saveController(Controller controller);
	
	public boolean updateController(Controller controller);
	
	public boolean deleteControllerByControllerId(int controllerId);
	
	public Controller getControllerByControllerId(int controllerId);
	
	public Controller getControllerByUserId(String userId);
	
	public ArrayList<Controller> getControllerList();
	
	public int getControllerCount();
	
	public int getControllerCountByState(String state);
	
	
	
	public DataRecord saveDataRecord(DataRecord dataRecord);
	
	public boolean updateDataRecord(DataRecord dataRecord);
	
	public boolean deleteDataRecordByAttributeId(int attributeId);
	
	public DataRecord getDataRecordByAttributeId(int attributeId);
	
	
	
	public Notification saveNotification(Notification notification);
	
	public ArrayList<Notification> getNotificationList();
	
	public int getNotificationCountByType(String type);
	
	
	
	public Setting saveSetting(Setting setting);
	
	public boolean updateSetting(Setting setting);
	
	public Setting getSettingByKey(String key);
	
	
	
	public PinnedChart savePinnedChart(PinnedChart pinnedChart);
	
	public boolean deletePinnedChartByPinnedChartId(int pinnedChartId);
	
	public PinnedChart getPinnedChartByAttributeId(int attributeId);
	
	public PinnedChart getPinnedChartByParameters(int attributeId, String window, String average);
	
	public ArrayList<PinnedChart> getPinnedChartList();
	
	public ArrayList<PinnedChart> getPinnedChartListByAttributeId(int attributeId);
	
}