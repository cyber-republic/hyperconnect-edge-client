package com.hyper.connect.elastos;

import com.google.gson.JsonArray;
import com.hyper.connect.App;
import com.hyper.connect.management.HistoryManagement;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.elastos.carrier.filetransfer.*;

import javax.xml.crypto.Data;


public class ElastosCarrier extends Thread{
	private final static String CONTROLLER_CONNECTION_KEYWORD="hyperconnect_controller";
	private final static String DEVICE_CONNECTION_KEYWORD="hyperconnect_device";
	private static Synchronizer syncher=new Synchronizer();
	private static TestOptions options;
	private static CarrierHandler carrierHandler;
	private static Carrier carrier;
	private App app;
	private boolean isConnected=false;

	private static Manager fileTransferManager;
	private static Map<String, FileTransfer> fileTransferMap;

	static{
		loadElastosLib();
	}

	public ElastosCarrier(App app){
		this.app=app;
		File carrierDir=CustomUtil.getDirectoryByName("carrier");
		options=new TestOptions(carrierDir.getPath());
		carrierHandler=new CarrierHandler();
		fileTransferMap=new HashMap<String, FileTransfer>();
	}
	
	public void run(){
		try{
			Carrier.initializeInstance(options, carrierHandler);
			carrier=Carrier.getInstance();
			carrier.start(0);

			FileTransferManagerHandler fileTransferManagerHandler=new FileTransferManagerHandler();
			Manager.initializeInstance(carrier, fileTransferManagerHandler);
			fileTransferManager=Manager.getInstance();

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
		ArrayList<Controller> controllerList=app.getDatabase().getControllerList();
		for(Controller controller : controllerList){
			sendFriendMessage(controller.getUserId(), jsonString);
		}
	}

	public void sendDataToController(String controllerUserId, JsonObject jsonObject){
		String jsonString=jsonObject.toString();
		sendFriendMessage(controllerUserId, jsonString);
	}

	public void sendDataToOnlineControllers(JsonObject jsonObject){
		String jsonString=jsonObject.toString();
		ArrayList<Controller> controllerList=app.getDatabase().getOnlineControllerList();
		for(Controller controller : controllerList){
			sendFriendMessage(controller.getUserId(), jsonString);
		}
	}

	public void sendDataToDevice(String deviceUserId, JsonObject jsonObject){
		String jsonString=jsonObject.toString();
		sendFriendMessage(deviceUserId, jsonString);
	}

	public void sendDataToOnlineDevice(String deviceUserId, JsonObject jsonObject){
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
			System.out.println("CarrierHandler - onFriends - "+friends);
			syncher.wakeup();
		}

		@Override
		public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status){
			System.out.println("CarrierHandler - onFriendConnection - "+friendId+" - "+status);
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
			app.refreshControllers();
			syncher.wakeup();
		}

		@Override
		public void onFriendRequest(Carrier carrier, String userId, UserInfo info, String hello){
			System.out.println("CarrierHandler - onFriendRequest - "+userId+" - "+hello);
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
			System.out.println("CarrierHandler - onFriendAdded - "+info);
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
			System.out.println("CarrierHandler - onFriendRemoved - "+friendId);
			syncher.wakeup();
		}

		@Override
		public void onFriendMessage(Carrier carrier, String from, byte[] message){
			String messageText=new String(message);
			System.out.println("CarrierHandler - onFriendMessage - "+from+" - "+messageText);
			Gson gson=new Gson();
			JsonObject resultObject=gson.fromJson(messageText, JsonObject.class);
			String command=resultObject.get("command").getAsString();
			if(command.equals("removeMe")){
				Controller controller=app.getDatabase().getControllerByUserId(from);
				app.getDatabase().deleteControllerByControllerId(controller.getId());
				removeFriend(from);
				app.refreshControllers();
			}
			else if(command.equals("getData")){
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
				sendDataToOnlineControllers(resultObject);
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
				sendDataToOnlineControllers(resultObject);
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
			else if(command.equals("deleteEvent")){
				String globalEventId=resultObject.get("globalEventId").getAsString();
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
			else if(command.equals("getHistoryValue")){
				int attributeId=resultObject.get("attributeId").getAsInt();
				ArrayList<DataRecord> latestDataRecordList=app.getAttributeManager().getLatestDataRecordList(attributeId);
				if(latestDataRecordList!=null && latestDataRecordList.size()>0){
					JsonElement jsonElements=new Gson().toJsonTree(latestDataRecordList);
					JsonObject responseObject=new JsonObject();
					responseObject.addProperty("command", "historyValue");
					responseObject.add("dataRecordList", jsonElements);
					String jsonString=responseObject.toString();
					sendFriendMessage(from, jsonString);
				}
			}
			else if(command.equals("getHistoryFiles")){
				int attributeId=resultObject.get("attributeId").getAsInt();
				File file1m=new HistoryManagement().getHistoryFile(attributeId+"_1m.json");
				if(file1m.exists()){
					sendFile(from, file1m.getAbsolutePath(), attributeId, EventAverage.ONE_MINUTE);
				}
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
                libList.add("msvcp140d.dll");

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

	private class FileTransferManagerHandler implements ManagerHandler{
		@Override
		public void onConnectRequest(Carrier carrier, String from, FileTransferInfo info){
			System.out.println("FileTransferManagerHandler - onConnectRequest - from: "+from);
			try{
				String fileId=info.getFileId();
				FileTransfer fileTransfer=fileTransferManager.newFileTransfer(from, info, new TransferHandler());
				fileTransfer.acceptConnect();
			}
			catch(CarrierException e){}
		}
	}

	private class TransferHandler implements FileTransferHandler{
		private String userId="";
		private String sendFilePath="";
		private int attributeId;
		EventAverage average=null;

		private TransferHandler(){}

		private TransferHandler(String userId, String sendFilePath, int attributeId, EventAverage average){
			this.userId=userId;
			this.sendFilePath=sendFilePath;
			this.attributeId=attributeId;
			this.average=average;
		}

		@Override
		public void onStateChanged(FileTransfer filetransfer, FileTransferState state){
			System.out.println("TransferHandler - onStateChanged - "+state);
		}

		@Override
		public void onFileRequest(FileTransfer filetransfer, String fileId, String filename, long size){
			System.out.println("TransferHandler - onFileRequest - "+fileId+" - "+filename);
		}

		@Override
		public void onPullRequest(FileTransfer filetransfer, String fileId, long offset){
			System.out.println("TransferHandler - onPullRequest - "+fileId);
			sendData(userId, fileId, sendFilePath, attributeId, average);
		}

		@Override
		public boolean onData(FileTransfer filetransfer, String fileId, byte[] data){
			return false;
		}

		@Override
		public void onDataFinished(FileTransfer filetransfer, String fileId){
			System.out.println("TransferHandler - onDataFinished - "+fileId);
		}

		@Override
		public void onPending(FileTransfer filetransfer, String fileId){
			System.out.println("TransferHandler - onPending - "+fileId);
		}

		@Override
		public void onResume(FileTransfer filetransfer, String fileId){
			System.out.println("TransferHandler - onResume - "+fileId);
		}

		@Override
		public void onCancel(FileTransfer filetransfer, String fileId, int status, String reason){
			System.out.println("TransferHandler - onCancel - "+fileId);
		}
	}

	public void sendFile(String userId, String filePath, int attributeId, EventAverage average){
		try{
			String fileId=FileTransfer.generateFileId();
			File file=new File(filePath);
			FileTransferInfo currentFileTransferInfo=new FileTransferInfo(filePath, fileId, file.length());

			FileTransfer fileTransfer=fileTransferMap.get(userId);
			if(fileTransfer==null){
				fileTransfer=fileTransferManager.newFileTransfer(userId, currentFileTransferInfo, new TransferHandler(userId, filePath, attributeId, average));
				fileTransfer.connect();
				fileTransferMap.put(userId, fileTransfer);
			}


		}
		catch(CarrierException e){}
	}

	public void sendAdditionalFile(String userId, String filePath, int attributeId, EventAverage average){
		try{
			String fileId=FileTransfer.generateFileId();
			File file=new File(filePath);
			FileTransferInfo currentFileTransferInfo=new FileTransferInfo(filePath, fileId, file.length());

			FileTransfer fileTransfer=fileTransferMap.get(userId);
			if(fileTransfer!=null){
				fileTransfer.addFile(currentFileTransferInfo);
			}


		}
		catch(CarrierException e){}
	}

	private void sendData(String userId, String fileId, String sendFilePath, int attributeId, EventAverage average){
		SentDataThread sentDataThread=new SentDataThread(userId, fileId, sendFilePath, attributeId, average);
		sentDataThread.start();
	}

	private class SentDataThread{
		private ExecutorService executorService;
		private Future<?> future;
		private boolean isRunning;
		private String userId;
		private String fileId;
		private String sendFilePath;
		private int attributeId;
		private EventAverage average;

		private SentDataThread(String userid, String fileId, String sendFilePath, int attributeId, EventAverage average){
			this.userId=userid;
			this.fileId=fileId;
			this.sendFilePath=sendFilePath;
			this.attributeId=attributeId;
			this.average=average;
		}

		public void start(){
			executorService=Executors.newSingleThreadExecutor();
			Runnable runnable=new Runnable(){
				@Override
				public void run(){
					isRunning=true;
					FileTransfer fileTransfer=fileTransferMap.get(userId);
					try{
						if(!sendFilePath.isEmpty()){
							File file=new File(sendFilePath);
							if(file.isFile()){
								final long fileSize=file.length();
								FileInputStream fis;
								try{
									final int size=1024;
									fis=new FileInputStream(file);
									byte[] buffer=new byte[size];
									int len;
									long sent=0;
									String percentString;
									while((len=fis.read(buffer))!=-1){
										if(isRunning){
											_writeData(fileId, buffer, len, fileTransfer);
											sent+=len;
											int percent=(int)(sent*100/fileSize);
											System.out.println("SentDataThread - "+percent+"%");
										}
										else{
											System.out.println("SentDataThread - close");
											//fileTransfer.close();
											return;
										}
									}
								}
								catch(IOException e){}
								//System.out.println("SentDataThread - close");
								//fileTransfer.close();

								System.out.println("SentDataThread - Data finished");
								System.out.println(average);
								if(average==EventAverage.ONE_MINUTE){
									File file5m=new HistoryManagement().getHistoryFile(attributeId+"_5m.json");
									if(file5m.exists()){
										sendAdditionalFile(userId, file5m.getAbsolutePath(), attributeId, EventAverage.FIVE_MINUTES);
									}

									File file15m=new HistoryManagement().getHistoryFile(attributeId+"_15m.json");
									if(file15m.exists()){
										sendAdditionalFile(userId, file15m.getAbsolutePath(), attributeId, EventAverage.FIFTEEN_MINUTES);
									}

									File file1h=new HistoryManagement().getHistoryFile(attributeId+"_1h.json");
									if(file1h.exists()){
										sendAdditionalFile(userId, file1h.getAbsolutePath(), attributeId, EventAverage.ONE_HOUR);
									}

									File file3h=new HistoryManagement().getHistoryFile(attributeId+"_3h.json");
									if(file3h.exists()){
										sendAdditionalFile(userId, file3h.getAbsolutePath(), attributeId, EventAverage.THREE_HOURS);
									}

									File file6h=new HistoryManagement().getHistoryFile(attributeId+"_6h.json");
									if(file6h.exists()){
										sendAdditionalFile(userId, file6h.getAbsolutePath(), attributeId, EventAverage.SIX_HOURS);
									}

									File file1d=new HistoryManagement().getHistoryFile(attributeId+"_1d.json");
									if(file1d.exists()){
										sendAdditionalFile(userId, file1d.getAbsolutePath(), attributeId, EventAverage.ONE_DAY);
									}
								}
							}
						}
					}
					catch(Exception e){}
				}
			};
			future=executorService.submit(runnable);
		}

		public void stop(){
			future.cancel(true);
			executorService.shutdown();
		}

		private void _writeData(String fileId, byte[] data, int len, FileTransfer fileTransfer){
			int pos=0;
			int rc;
			int left=len;
			while(left>0){
				try{
					rc=fileTransfer.writeData(fileId, data, pos, left);
					pos+=rc;
					left-=rc;
				}
				catch(CarrierException e){
					break;
				}
			}
		}
	}
}