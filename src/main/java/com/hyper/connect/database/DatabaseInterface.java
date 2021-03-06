package com.hyper.connect.database;

import com.hyper.connect.model.*;
import com.hyper.connect.model.enums.*;

import java.util.ArrayList;


public interface DatabaseInterface{

	public void closeConnection();


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
	
	public ArrayList<Attribute> getAttributeListBySensorIdAndDirection(int sensorId, AttributeDirection direction);
	
	public ArrayList<Attribute> getActiveAttributeListBySensorIdAndDirection(int sensorId, AttributeDirection direction);
	
	public ArrayList<Attribute> getValidAttributeListBySensorIdAndDirection(int sensorId, AttributeDirection direction);
	
	public ArrayList<Attribute> getAttributeList();
	
	public int getAttributeCount();
	
	public int getAttributeCountByState(AttributeState state);
	
	
	
	public Event saveEvent(Event event);
	
	public boolean updateEvent(Event event);

	public boolean setEventListState(EventState state);
	
	public boolean setEventStateByEventId(int eventId, EventState state);
	
	public boolean deleteEventByEventId(int eventId);
	
	public Event getEventByEventId(int eventId);

	public Event getEventByGlobalEventId(String globalEventId);
	
	public ArrayList<Event> getActiveEventListByAverageAndSourceAttributeId(EventAverage average, int sourceAttributeId);
	
	public ArrayList<Event> getEventList();

	public ArrayList<Event> getEventListByType(EventType type);

	public ArrayList<Event> getEventListByAttributeId(int attributeId);

	public ArrayList<Event> getEventListOnlyNameAndIdByAttributeId(int attributeId);
	
	public ArrayList<Event> getActiveEventListOnlyNameAndIdByAttributeId(int attributeId);
	
	public int getEventCount();
	
	public int getEventCountByState(EventState state);
	
	
	
	public Controller saveController(Controller controller);
	
	public boolean updateController(Controller controller);
	
	public boolean deleteControllerByControllerId(int controllerId);
	
	public Controller getControllerByControllerId(int controllerId);
	
	public Controller getControllerByUserId(String userId);
	
	public ArrayList<Controller> getControllerList();

	public ArrayList<Controller> getOnlineControllerList();
	
	public int getControllerCount();
	
	public int getControllerCountByState(String state);
	
	
	
	public DataRecord saveDataRecord(DataRecord dataRecord);
	
	public boolean updateDataRecord(DataRecord dataRecord);
	
	public boolean deleteDataRecordByAttributeId(int attributeId);
	
	public DataRecord getDataRecordByAttributeId(int attributeId);
	
	
	
	public Notification saveNotification(Notification notification);
	
	public ArrayList<Notification> getNotificationList();
	
	public int getNotificationCountByType(NotificationType type);
	
	
	
	public Setting saveSetting(Setting setting);
	
	public boolean updateSetting(Setting setting);
	
	public Setting getSettingByKey(String key);
	
	
	
	public PinnedChart savePinnedChart(PinnedChart pinnedChart);
	
	public boolean deletePinnedChartByPinnedChartId(int pinnedChartId);
	
	public PinnedChart getPinnedChartByAttributeId(int attributeId);
	
	public PinnedChart getPinnedChartByParameters(int attributeId, PinnedChartWindow window, EventAverage average);
	
	public ArrayList<PinnedChart> getPinnedChartList();
	
	public ArrayList<PinnedChart> getPinnedChartListByAttributeId(int attributeId);



	public Device saveDevice(Device device);

	public boolean updateDevice(Device device);

	public boolean setDeviceListConnectionState(DeviceConnectionState connectionState);

	public boolean deleteDeviceByDeviceId(int deviceId);

	public Device getDeviceByDeviceId(int deviceId);

	public Device getDeviceByUserId(String userId);

	public ArrayList<Device> getDeviceList();

	public ArrayList<Device> getOnlineDeviceList();
	
}