package com.hyper.connect.elastos;

import com.hyper.connect.App;
import com.hyper.connect.model.Controller;
import com.hyper.connect.model.DataRecord;
import com.hyper.connect.elastos.common.Synchronizer;
import com.hyper.connect.elastos.common.TestOptions;
import com.hyper.connect.util.CustomUtil;
import org.elastos.carrier.exceptions.CarrierException;
import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.PresenceStatus;

import java.lang.Thread;
import java.util.List;
import java.io.File;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


public class ElastosCarrier extends Thread{
	private final static String CONNECTION_KEYWORD="hyperconnect";
	private static Synchronizer syncher=new Synchronizer();
	private static TestOptions options;
	private static CarrierHandler carrierHandler;
	private static Carrier carrier;
	private App app;
	private boolean isConnected=false;

	public ElastosCarrier(App app){
		this.app=app;
		File carrierDir=CustomUtil.getDirectoryByName("carrier");
		this.options=new TestOptions(carrierDir.getPath());
		this.carrierHandler=new CarrierHandler();
	}
	
	public void run(){
		try{
			Carrier.initializeInstance(this.options, this.carrierHandler);
			this.carrier=Carrier.getInstance();
			this.carrier.start(0);

			synchronized(this.carrier){
				this.carrier.wait();
			}
		}
		catch(CarrierException | InterruptedException e){

		}
	}
	
	public void kill(){
		this.carrier.kill();
	}
	
	public boolean isConnected(){
		return this.isConnected;
	}
	
	public String getAddress(){
		String address="undefined";
		try{
			address=this.carrier.getAddress();
		}
		catch(CarrierException ce){
			address="undefined";
		}
		this.syncher.wakeup();
		return address;
	}
	
	public String getUserId(){
		String userId="undefined";
		try{
			userId=this.carrier.getUserId();
		}
		catch(CarrierException ce){
			userId="undefined";
		}
		this.syncher.wakeup();
		return userId;
	}
	
	public boolean isValidAddress(String address){
		return this.carrier.isValidAddress(address);
	}
	
	public boolean isValidUserId(String userId){
		return this.carrier.isValidId(userId);
	}
	
	public boolean acceptFriend(String userId){
		boolean response=true;
		try{
			this.carrier.acceptFriend(userId);
		}
		catch(CarrierException ce){
			response=false;
		}
		this.syncher.wakeup();
		return response;
	}
	
	public boolean removeFriend(String userId){
		boolean response=true;
		try{
			this.carrier.removeFriend(userId);
		}
		catch(CarrierException ce){
			response=false;
		}
		this.syncher.wakeup();
		return response;
	}
	
	public boolean sendFriendMessage(String userId, String message){
		boolean response=true;
		try{
			this.carrier.sendFriendMessage(userId, message);
		}
		catch(CarrierException ce){
			response=false;
		}
		this.syncher.wakeup();
		return response;
	}
	
	public UserInfo getSelfInfo(){
		UserInfo userInfo=null;
		try{
			userInfo=this.carrier.getSelfInfo();
		}
		catch(CarrierException ce){
			userInfo=null;
		}
		this.syncher.wakeup();
		return userInfo;
	}
	
	public boolean setSelfInfo(UserInfo userInfo){
		boolean response=true;
		try{
			this.carrier.setSelfInfo(userInfo);
		}
		catch(CarrierException ce){
			response=false;
		}
		this.syncher.wakeup();
		return response;
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
			if(status==ConnectionStatus.Connected){
				isConnected=true;
				app.refreshConnectionState();
				String address=getAddress();
				String userId=getUserId();
			}
			else{
				isConnected=false;
			}
			syncher.wakeup();
		}
		
		@Override
		public void onFriends(Carrier carrier, List<FriendInfo> friends){
			syncher.wakeup();
		}

		@Override
		public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status){
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
			if(hello.equals(CONNECTION_KEYWORD)){
				Controller controller=app.getDatabase().getControllerByUserId(userId);
				if(controller==null){
					Controller newController=new Controller(0, userId, "pending");
					app.getDatabase().saveController(newController);
					app.refreshControllers();
				}
			}
			else{
				//System.out.println("wrong keyword");
			}
			syncher.wakeup();
		}

		@Override
		public void onFriendAdded(Carrier carrier, FriendInfo info){
			syncher.wakeup();
		}

		@Override
		public void onFriendRemoved(Carrier carrier, String friendId){
			syncher.wakeup();
		}

		@Override
		public void onFriendMessage(Carrier carrier, String from, byte[] message){
			String messageText=new String(message);
			try{
				JsonElement jsonTree=new JsonParser().parse(messageText);
				if(jsonTree.isJsonObject()){
					JsonObject messageObject=jsonTree.getAsJsonObject();
					String command=messageObject.get("command").getAsString();
					if(command.equals("getValue")){
						int attributeId=messageObject.get("attributeId").getAsInt();
						
						DataRecord dataRecord=app.getAttributeManager().getCurrentDataRecord(attributeId);
						String response=new Gson().toJson(dataRecord);
						sendFriendMessage(from, response);
					}
				}
			}
			catch(NullPointerException npe){}
			catch(JsonSyntaxException jse){}
			
			syncher.wakeup();
		}

		@Override
		public void onFriendInviteRequest(Carrier carrier, String from, String data){
			syncher.wakeup();
		}
	}
}