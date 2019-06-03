package com.hyper.connect.elastos;

import com.hyper.connect.App;
import com.hyper.connect.model.*;
import com.hyper.connect.elastos.common.Synchronizer;
import com.hyper.connect.elastos.common.TestOptions;
import com.hyper.connect.model.enums.*;
import com.hyper.connect.util.CustomUtil;
import org.elastos.carrier.exceptions.CarrierException;
import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.PresenceStatus;

import java.io.*;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class ElastosCarrier extends Thread{
	private final static String CONTROLLER_CONNECTION_KEYWORD="hyperconnect_controller";
	private final static String DEVICE_CONNECTION_KEYWORD="hyperconnect_device";
	private static Synchronizer syncher=new Synchronizer();
	private static TestOptions options;
	private static CarrierHandler carrierHandler;
	private static Carrier carrier;
	private App app;
	private boolean isConnected=false;

	static{
		loadElastosLib();
	}

	public ElastosCarrier(App app){
		this.app=app;
		File carrierDir=CustomUtil.getDirectoryByName("carrier");
		options=new TestOptions(carrierDir.getPath());
		carrierHandler=new CarrierHandler();
	}
	
	public void run(){
		try{
			Carrier.initializeInstance(options, carrierHandler);
			carrier=Carrier.getInstance();
			carrier.start(0);

			synchronized(carrier){
				carrier.wait();
			}
		}
		catch(CarrierException | InterruptedException e){}
	}
	
	public void kill(){
		carrier.kill();
	}
	
	public boolean isConnected(){
		return this.isConnected;
	}
	
	public String getAddress(){
		String address="undefined";
		try{
			address=carrier.getAddress();
		}
		catch(CarrierException ce){
			address="undefined";
		}
		syncher.wakeup();
		return address;
	}
	
	public String getUserId(){
		String userId="undefined";
		try{
			userId=carrier.getUserId();
		}
		catch(CarrierException ce){
			userId="undefined";
		}
		syncher.wakeup();
		return userId;
	}
	
	public boolean isValidAddress(String address){
		return Carrier.isValidAddress(address);
	}
	
	public boolean isValidUserId(String userId){
		return Carrier.isValidId(userId);
	}

	public boolean isFriend(String userId){
		boolean response=true;
		try{
			response=carrier.isFriend(userId);
		}
		catch(CarrierException ce){
			response=false;
		}
		syncher.wakeup();
		return response;
	}
	
	public boolean acceptFriend(String userId){
		boolean response=true;
		try{
			carrier.acceptFriend(userId);
		}
		catch(CarrierException ce){
			response=false;
		}
		syncher.wakeup();
		return response;
	}
	
	public boolean removeFriend(String userId){
		boolean response=true;
		try{
			carrier.removeFriend(userId);
		}
		catch(CarrierException ce){
			response=false;
		}
		syncher.wakeup();
		return response;
	}

	public boolean addFriend(String address){
		boolean response=true;
		try{
			carrier.addFriend(address, DEVICE_CONNECTION_KEYWORD);
		}
		catch(CarrierException ce){
			response=false;
		}
		syncher.wakeup();
		return response;
	}
	
	public boolean sendFriendMessage(String userId, String message){
		boolean response=true;
		try{
			carrier.sendFriendMessage(userId, message);
		}
		catch(CarrierException ce){
			response=false;
		}
		syncher.wakeup();
		return response;
	}
	
	public UserInfo getSelfInfo(){
		UserInfo userInfo=null;
		try{
			userInfo=carrier.getSelfInfo();
		}
		catch(CarrierException ce){
			userInfo=null;
		}
		syncher.wakeup();
		return userInfo;
	}
	
	public boolean setSelfInfo(UserInfo userInfo){
		boolean response=true;
		try{
			carrier.setSelfInfo(userInfo);
		}
		catch(CarrierException ce){
			response=false;
		}
		syncher.wakeup();
		return response;
	}

	public void sendDataToControllers(JsonObject jsonObject){
		String jsonString=jsonObject.toString();
		ArrayList<Controller> controllerList=app.getDatabase().getOnlineControllerList();
		for(Controller controller : controllerList){
			sendFriendMessage(controller.getUserId(), jsonString);
		}
	}

	public void sendDataToDevice(String deviceUserId, JsonObject jsonObject){
		Device device=app.getDatabase().getDeviceByUserId(deviceUserId);
		if(device.getConnectionState()==DeviceConnectionState.ONLINE){
			String jsonString=jsonObject.toString();
			sendFriendMessage(deviceUserId, jsonString);
		}
	}
	
	private class CarrierHandler extends AbstractCarrierHandler{
		@Override
		public void onReady(Carrier carrier){
			synchronized(carrier){
				carrier.notify();
			}
		}
		
		@Override
		public void onConnection(Carrier carrier, ConnectionStatus status){
			System.out.println("CarrierHandler - onConnection - "+status);
			if(status==ConnectionStatus.Connected){
				isConnected=true;
				app.refreshConnectionState();
				String address=getAddress();
				String userId=getUserId();
				System.out.println("address: "+address);
				System.out.println("userId: "+userId);
			}
			else{
				isConnected=false;
			}
			syncher.wakeup();
		}
		
		@Override
		public void onFriends(Carrier carrier, List<FriendInfo> friends){
			//System.out.println("CarrierHandler - onFriends - "+friends);
			syncher.wakeup();
		}

		@Override
		public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status){
			//System.out.println("CarrierHandler - onFriendConnection - "+friendId+" - "+status);
			Device device=app.getDatabase().getDeviceByUserId(friendId);
			if(device!=null){
				if(status==ConnectionStatus.Connected){
					device.setConnectionState(DeviceConnectionState.ONLINE);
				}
				else if(status==ConnectionStatus.Disconnected){
					device.setConnectionState(DeviceConnectionState.OFFLINE);
				}
				app.getDatabase().updateDevice(device);
			}
			Controller controller=app.getDatabase().getControllerByUserId(friendId);
			if(controller!=null){
				if(status==ConnectionStatus.Connected){
					controller.setConnectionState(ControllerConnectionState.ONLINE);
				}
				else if(status==ConnectionStatus.Disconnected){
					controller.setConnectionState(ControllerConnectionState.OFFLINE);
				}
				app.getDatabase().updateController(controller);
			}
			syncher.wakeup();
		}

		@Override
		public void onFriendInfoChanged(Carrier carrier, String friendId, FriendInfo info){
			syncher.wakeup();
		}

		@Override
		public void onFriendPresence(Carrier carrier, String friendId, PresenceStatus presence){
			syncher.wakeup();
		}

		@Override
		public void onFriendRequest(Carrier carrier, String userId, UserInfo info, String hello){
			//System.out.println("CarrierHandler - onFriendRequest - "+userId+" - "+hello);
			if(hello.equals(CONTROLLER_CONNECTION_KEYWORD)){
				Controller controller=app.getDatabase().getControllerByUserId(userId);
				if(controller==null){
					Controller newController=new Controller(0, userId, ControllerState.PENDING, ControllerConnectionState.OFFLINE);
					app.getDatabase().saveController(newController);
					app.refreshControllers();
				}
			}
			else if(hello.equals(DEVICE_CONNECTION_KEYWORD)){
				acceptFriend(userId);
			}
			else{
				//System.out.println("wrong keyword");
			}
			syncher.wakeup();
		}

		@Override
		public void onFriendAdded(Carrier carrier, FriendInfo info){
			//System.out.println("CarrierHandler - onFriendAdded - "+info);
			Controller controller=app.getDatabase().getControllerByUserId(info.getUserId());
			if(controller==null){
				Device device=app.getDatabase().getDeviceByUserId(info.getUserId());
				if(device==null){
					Device newDevice=new Device(0, info.getUserId(), DeviceConnectionState.OFFLINE);
					app.getDatabase().saveDevice(newDevice);
				}
			}
			syncher.wakeup();
		}

		@Override
		public void onFriendRemoved(Carrier carrier, String friendId){
			//System.out.println("CarrierHandler - onFriendRemoved - "+friendId);
			syncher.wakeup();
		}

		@Override
		public void onFriendMessage(Carrier carrier, String from, byte[] message){
			String messageText=new String(message);
			//System.out.println("CarrierHandler - onFriendMessage - "+from+" - "+messageText);
			Gson gson=new Gson();
			JsonObject resultObject=gson.fromJson(messageText, JsonObject.class);
			String command=resultObject.get("command").getAsString();
			if(command.equals("getData")){
				ArrayList<Sensor> sensorList=app.getDatabase().getSensorList();
				ArrayList<Attribute> attributeList=app.getDatabase().getAttributeList();
				ArrayList<Event> eventList=app.getDatabase().getEventList();
				for(Sensor sensor : sensorList){
					JsonElement jsonSensor=gson.toJsonTree(sensor);
					JsonObject responseObject=new JsonObject();
					responseObject.addProperty("command", "addSensor");
					responseObject.add("sensor", jsonSensor);
					String jsonString=responseObject.toString();
					sendFriendMessage(from, jsonString);
				}
				for(Attribute attribute : attributeList){
					JsonElement jsonAttribute=gson.toJsonTree(attribute);
					JsonObject responseObject=new JsonObject();
					responseObject.addProperty("command", "addAttribute");
					responseObject.add("attribute", jsonAttribute);
					String jsonString=responseObject.toString();
					sendFriendMessage(from, jsonString);
				}
				for(Event event : eventList){
					JsonElement jsonEvent=gson.toJsonTree(event);
					JsonObject responseObject=new JsonObject();
					responseObject.addProperty("command", "addEvent");
					responseObject.add("event", jsonEvent);
					String jsonString=responseObject.toString();
					sendFriendMessage(from, jsonString);
				}
			}
			else if(command.equals("changeAttributeState")){
				int attributeId=resultObject.get("id").getAsInt();
				boolean state=resultObject.get("state").getAsBoolean();
				Attribute attribute=app.getDatabase().getAttributeByAttributeId(attributeId);
				if(state){
					attribute.setState(AttributeState.ACTIVE);
					app.getDatabase().updateAttribute(attribute);
					app.getAttributeManager().startAttribute(attributeId);
				}
				else{
					attribute.setState(AttributeState.DEACTIVATED);
					app.getDatabase().updateAttribute(attribute);
					app.getAttributeManager().stopAttribute(attributeId);
				}
			}
			else if(command.equals("executeAttributeAction")){
				int attributeId=resultObject.get("id").getAsInt();
				String triggerValue=resultObject.get("triggerValue").getAsString();
				app.getAttributeManager().executeAction(attributeId, triggerValue);
			}
			else if(command.equals("changeEventState")){
				String globalEventId=resultObject.get("globalEventId").getAsString();
				boolean state=resultObject.get("state").getAsBoolean();
				EventEdgeType edgeType=EventEdgeType.valueOf(resultObject.get("edgeType").getAsInt());
				Event event=app.getDatabase().getEventByGlobalEventId(globalEventId);
				if(state){
					event.setState(EventState.ACTIVE);
				}
				else{
					event.setState(EventState.DEACTIVATED);
				}
				app.getDatabase().updateEvent(event);

				if(edgeType==EventEdgeType.SOURCE || edgeType==EventEdgeType.SOURCE_AND_ACTION){
					app.getAttributeManager().updateEventState(event.getSourceEdgeAttributeId(), event.getAverage());
				}
			}
			else if(command.equals("addDevice")){
				String address=resultObject.get("address").getAsString();
				String userId=resultObject.get("userId").getAsString();
				if(!isFriend(userId)){
					addFriend(address);
				}
			}
			else if(command.equals("addEvent")){
				JsonObject eventObject=resultObject.getAsJsonObject("event");
				String globalEventId=eventObject.get("globalEventId").getAsString();
				String name=eventObject.get("name").getAsString();
				EventType type=EventType.valueOf(eventObject.get("type").getAsInt());
				EventState state=EventState.valueOf(eventObject.get("state").getAsInt());
				EventAverage average=EventAverage.valueOf(eventObject.get("average").getAsInt());
				EventCondition condition=EventCondition.valueOf(eventObject.get("condition").getAsInt());
				String conditionValue=eventObject.get("conditionValue").getAsString();
				String triggerValue=eventObject.get("triggerValue").getAsString();
				String sourceDeviceUserId=eventObject.get("sourceDeviceUserId").getAsString();
				int sourceEdgeSensorId=eventObject.get("sourceEdgeSensorId").getAsInt();
				int sourceEdgeAttributeId=eventObject.get("sourceEdgeAttributeId").getAsInt();
				String actionDeviceUserId=eventObject.get("actionDeviceUserId").getAsString();
				int actionEdgeSensorId=eventObject.get("actionEdgeSensorId").getAsInt();
				int actionEdgeAttributeId=eventObject.get("actionEdgeAttributeId").getAsInt();
				EventEdgeType edgeType=EventEdgeType.valueOf(resultObject.get("edgeType").getAsInt());

				Event newEvent=new Event(0, globalEventId, name, type, state, average, condition, conditionValue, triggerValue, sourceDeviceUserId, sourceEdgeSensorId, sourceEdgeAttributeId, actionDeviceUserId, actionEdgeSensorId, actionEdgeAttributeId, edgeType);
				app.getDatabase().saveEvent(newEvent);
			}
			else if(command.equals("getValue")){
				int attributeId=resultObject.get("attributeId").getAsInt();
				DataRecord dataRecord=app.getAttributeManager().getCurrentDataRecord(attributeId);
				if(dataRecord==null){
					dataRecord=app.getDatabase().getDataRecordByAttributeId(attributeId);
					if(dataRecord==null){
						dataRecord=new DataRecord("No Value", "No Value");
						dataRecord.setAttributeId(attributeId);
					}
				}
				JsonElement jsonDataRecord=gson.toJsonTree(dataRecord);
				JsonObject responseObject=new JsonObject();
				responseObject.addProperty("command", "dataValue");
				responseObject.add("dataRecord", jsonDataRecord);
				String jsonString=responseObject.toString();
				sendFriendMessage(from, jsonString);
			}
			syncher.wakeup();
		}

		@Override
		public void onFriendInviteRequest(Carrier carrier, String from, String data){
			syncher.wakeup();
		}
	}

	private static void loadElastosLib(){
		try{
			String resourcePath="/lib/";
			ArrayList<String> libList=new ArrayList<String>();
			String osName=System.getProperty("os.name");
			if(osName.contains("Linux")){
				String osArch=System.getProperty("os.arch");
				if(osArch.contains("arm")){
					resourcePath+="rpi";
				}
				else{
					resourcePath+="linux";
				}

				libList.add("libcrystal.so");
				libList.add("libelacarrier.so");
				libList.add("libelasession.so");
				libList.add("libcarrierjni.so");
				libList.add("libelafiletrans.so");

				for(int i=0;i<libList.size();i++){
					String libFile=libList.get(i);
					InputStream is=ElastosCarrier.class.getResourceAsStream(resourcePath+"/"+libFile);
					File file=new File("/usr/lib/"+libFile);
					OutputStream os=new FileOutputStream(file);
					byte[] buffer=new byte[1024];
					int length;
					while((length=is.read(buffer))!=-1){
						os.write(buffer, 0, length);
					}
					is.close();
					os.close();
				}
			}
			else if(osName.contains("Windows")){
				resourcePath+="windows";
				libList.add("crystal.dll");
				libList.add("elacarrier.dll");
				libList.add("elafiletrans.dll");
				libList.add("elasession.dll");
				libList.add("libcarrierjni.dll");
				libList.add("pthreadVC2.dll");
				libList.add("libgcc_s_seh-1.dll");
				libList.add("ucrtbased.dll");
				libList.add("vcruntime140d.dll");

				for(int i=0;i<libList.size();i++){
					String libFile=libList.get(i);
					InputStream is=ElastosCarrier.class.getResourceAsStream(resourcePath+"/"+libFile);
					File file=new File(libFile);
					OutputStream os=new FileOutputStream(file);
					byte[] buffer=new byte[1024];
					int length;
					while((length=is.read(buffer))!=-1){
						os.write(buffer, 0, length);
					}
					is.close();
					os.close();
				}
			}
			else if(osName.contains("Mac")){
				resourcePath+="mac";
				libList.add("libcrystal.dylib");
				libList.add("libelacarrier.dylib");
				libList.add("libelasession.dylib");
				libList.add("libcarrierjni.dylib");
				libList.add("libelafiletrans.dylib");

				for(int i=0;i<libList.size();i++){
					String libFile=libList.get(i);
					InputStream is=ElastosCarrier.class.getResourceAsStream(resourcePath+"/"+libFile);
					File file=new File(libFile);
					OutputStream os=new FileOutputStream(file);
					byte[] buffer=new byte[1024];
					int length;
					while((length=is.read(buffer))!=-1){
						os.write(buffer, 0, length);
					}
					is.close();
					os.close();
				}
			}
		}
		catch(IOException ioe){}
	}
}