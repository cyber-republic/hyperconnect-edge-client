package com.hyper.connect.controller;

import com.hyper.connect.App;
import com.hyper.connect.model.Sensor;
import com.hyper.connect.model.Attribute;
import com.hyper.connect.model.Event;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.StackPane;
import javafx.concurrent.Task;
import javafx.application.Platform;

import java.util.ArrayList;


public class AddEventController{
	private App app;
	private boolean isInitialized=false;
	
	private ArrayList<Sensor> sourceSensorList=null;
	private Sensor sourceSensor=null;
	private ArrayList<Attribute> sourceAttributeList=null;
	private Attribute sourceAttribute=null;
	private String average="";
	private String condition="";
	private String conditionValue="";
	
	private ArrayList<Sensor> actionSensorList=null;
	private Sensor actionSensor=null;
	private ArrayList<Attribute> actionAttributeList=null;
	private Attribute actionAttribute=null;
	private String triggerValue="";
	
	@FXML private StackPane addEventPane;
	
	@FXML private ChoiceBox sourceSensorChoiceBox;
	@FXML private ChoiceBox sourceAttributeChoiceBox;
	@FXML private ChoiceBox averageChoiceBox;
	@FXML private ChoiceBox conditionChoiceBox;
	@FXML private TextField valueTextField;
	@FXML private ChoiceBox valueChoiceBox;
	
	@FXML private ChoiceBox actionSensorChoiceBox;
	@FXML private ChoiceBox actionAttributeChoiceBox;
	@FXML private TextField triggerValueTextField;
	@FXML private ChoiceBox triggerValueChoiceBox;
	
	@FXML private TextField nameTextField;
	
	public AddEventController(){}
	
	public void setApp(App app){
		this.app=app;
	}
	
	public void init(){
		Task initTask=new Task<Void>(){
			@Override
			public Void call(){
				sourceSensorList=getSourceSensorList();
				actionSensorList=getActionSensorList();
				
				Platform.runLater(() -> {
					clearFields();
					initSourceSensor();
					initActionSensor();
				});
				return null;
			}
		};
		this.app.executeAsyncTask(initTask, addEventPane);
	}
	
	private ArrayList<Sensor> getSourceSensorList(){
		ArrayList<Sensor> sensorList=this.app.getDatabase().getSensorList();
		ArrayList<Sensor> newSensorList=new ArrayList<Sensor>();
		for(int i=0;i<sensorList.size();i++){
			Sensor sensor=sensorList.get(i);
			ArrayList<Attribute> attributeList=this.app.getDatabase().getValidAttributeListBySensorIdAndDirection(sensor.getId(), "input");
			if(!attributeList.isEmpty()){
				sensor.setAttributeList(attributeList);
				newSensorList.add(sensor);
			}
		}
		return newSensorList;
	}
	
	private ArrayList<Sensor> getActionSensorList(){
		ArrayList<Sensor> sensorList=this.app.getDatabase().getSensorList();
		ArrayList<Sensor> newSensorList=new ArrayList<Sensor>();
		for(int i=0;i<sensorList.size();i++){
			Sensor sensor=sensorList.get(i);
			ArrayList<Attribute> attributeList=this.app.getDatabase().getValidAttributeListBySensorIdAndDirection(sensor.getId(), "output");
			if(!attributeList.isEmpty()){
				sensor.setAttributeList(attributeList);
				newSensorList.add(sensor);
			}
		}
		return newSensorList;
	}
	
	private void clearFields(){
		this.clearSourceSensor();
		this.clearSourceAttribute();
		this.clearAverage();
		this.clearCondition();
		this.clearValue();
		
		this.clearActionSensor();
		this.clearActionAttribute();
		this.clearTriggerValue();
		
		this.nameTextField.clear();
	}
	
	private void clearSourceSensor(){
		this.sourceSensorChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(sourceSensorListener);
		this.sourceSensorChoiceBox.getItems().clear();
	}
	
	private void clearSourceAttribute(){
		this.sourceAttributeChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(sourceAttributeListener);
		this.sourceAttributeChoiceBox.getItems().clear();
		this.sourceAttributeChoiceBox.setDisable(true);
	}
	
	private void clearAverage(){
		this.averageChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(averageListener);
		this.averageChoiceBox.getItems().clear();
		this.averageChoiceBox.setDisable(true);
	}
	
	private void clearCondition(){
		this.conditionChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(conditionListener);
		this.conditionChoiceBox.getItems().clear();
		this.conditionChoiceBox.setDisable(true);
	}
	
	private void clearValue(){
		this.valueTextField.clear();
		this.valueTextField.setDisable(true);
		this.valueTextField.setVisible(true);
		this.valueTextField.setManaged(true);
		
		this.valueChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(valueListener);
		this.valueChoiceBox.getItems().clear();
		this.valueChoiceBox.setDisable(true);
		this.valueChoiceBox.setVisible(false);
		this.valueChoiceBox.setManaged(false);
	}
	
	private void clearActionSensor(){
		this.actionSensorChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(actionSensorListener);
		this.actionSensorChoiceBox.getItems().clear();
	}
	
	private void clearActionAttribute(){
		this.actionAttributeChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(actionAttributeListener);
		this.actionAttributeChoiceBox.getItems().clear();
		this.actionAttributeChoiceBox.setDisable(true);
	}
	
	private void clearTriggerValue(){
		this.triggerValueTextField.clear();
		this.triggerValueTextField.setDisable(true);
		this.triggerValueTextField.setVisible(true);
		this.triggerValueTextField.setManaged(true);
		
		this.triggerValueChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(triggerValueListener);
		this.triggerValueChoiceBox.getItems().clear();
		this.triggerValueChoiceBox.setDisable(true);
		this.triggerValueChoiceBox.setVisible(false);
		this.triggerValueChoiceBox.setManaged(false);
	}
	
	/*** event source ***/
	private void initSourceSensor(){
		this.sourceSensorChoiceBox.getItems().clear();
		this.sourceSensorChoiceBox.getItems().add("-- Select Sensor --");
		for(int i=0;i<this.sourceSensorList.size();i++){
			Sensor sensor=this.sourceSensorList.get(i);
			String item=sensor.getName()+" ("+sensor.getType()+")";
			this.sourceSensorChoiceBox.getItems().add(item);
		}
		this.sourceSensorChoiceBox.getSelectionModel().selectFirst();
		this.sourceSensorChoiceBox.getSelectionModel().selectedIndexProperty().addListener(sourceSensorListener);
	}
	
	private final ChangeListener<Number> sourceSensorListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			clearSourceAttribute();
			clearAverage();
			clearCondition();
			clearValue();
			int sensorIndex=(Integer)number2;
			if(sensorIndex!=0){
				sensorIndex--;
				sourceSensor=sourceSensorList.get(sensorIndex);
				sourceAttributeList=sourceSensor.getAttributeList();
				initSourceAttribute();
			}
		}
	};
	
	private void initSourceAttribute(){
		this.sourceAttributeChoiceBox.setDisable(false);
		this.sourceAttributeChoiceBox.getItems().clear();
		this.sourceAttributeChoiceBox.getItems().add("-- Select Attribute --");
		for(int i=0;i<this.sourceAttributeList.size();i++){
			Attribute attribute=this.sourceAttributeList.get(i);
			String item=attribute.getName()+" ("+attribute.getType()+")";
			this.sourceAttributeChoiceBox.getItems().add(item);
		}
		this.sourceAttributeChoiceBox.getSelectionModel().selectFirst();
		this.sourceAttributeChoiceBox.getSelectionModel().selectedIndexProperty().addListener(sourceAttributeListener);
	}
	
	private final ChangeListener<Number> sourceAttributeListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			clearAverage();
			clearCondition();
			clearValue();
			int attributeIndex=(Integer)number2;
			if(attributeIndex!=0){
				attributeIndex--;
				sourceAttribute=sourceAttributeList.get(attributeIndex);
				initAverage();
			}
		}
	};
	
	private void initAverage(){
		this.averageChoiceBox.setDisable(false);
		this.averageChoiceBox.getItems().clear();
		
		if(this.sourceAttribute.getType().equals("string") || this.sourceAttribute.getType().equals("boolean")){
			this.averageChoiceBox.getItems().addAll("-- Select Average --", "Real-Time");
		}
		else{
			this.averageChoiceBox.getItems().addAll("-- Select Average --", "Real-Time", "1 Minute", "5 Minutes", "15 Minutes", "1 Hour", "3 Hours", "6 Hours", "1 Day");
		}
		
		this.averageChoiceBox.getSelectionModel().selectFirst();
		this.averageChoiceBox.getSelectionModel().selectedIndexProperty().addListener(averageListener);
	}
	
	private final ChangeListener<Number> averageListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			clearCondition();
			clearValue();
			int averageIndex=(Integer)number2;
			if(averageIndex!=0){
				String value=(String)averageChoiceBox.getItems().get((Integer)number2);
				if(value.equals("Real-Time")){
					average="real-time";
				}
				else if(value.equals("1 Minute")){
					average="1m";
				}
				else if(value.equals("5 Minutes")){
					average="5m";
				}
				else if(value.equals("15 Minutes")){
					average="15m";
				}
				else if(value.equals("1 Hour")){
					average="1h";
				}
				else if(value.equals("3 Hours")){
					average="3h";
				}
				else if(value.equals("6 Hours")){
					average="6h";
				}
				else if(value.equals("1 Day")){
					average="1d";
				}
				initCondition();
			}
		}
	};
	
	private void initCondition(){
		this.conditionChoiceBox.setDisable(false);
		this.conditionChoiceBox.getItems().clear();
		this.conditionChoiceBox.getItems().addAll("-- Select Condition --", "equal to", "not equal to");
		if(!this.sourceAttribute.getType().equals("string") && !this.sourceAttribute.getType().equals("boolean")){
			this.conditionChoiceBox.getItems().addAll("greater than", "less than");
		}
		this.conditionChoiceBox.getSelectionModel().selectFirst();
		this.conditionChoiceBox.getSelectionModel().selectedIndexProperty().addListener(conditionListener);
	}
	
	private final ChangeListener<Number> conditionListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			clearValue();
			int conditionIndex=(Integer)number2;
			if(conditionIndex!=0){
				String value=(String)conditionChoiceBox.getItems().get((Integer)number2);
				condition=value;
				initValue();
			}
		}
	};
	
	private void initValue(){
		if(this.sourceAttribute.getType().equals("boolean")){
			this.valueTextField.setVisible(false);
			this.valueTextField.setManaged(false);
			this.valueTextField.setDisable(true);
			
			this.valueChoiceBox.setVisible(true);
			this.valueChoiceBox.setManaged(true);
			this.valueChoiceBox.setDisable(false);
			
			this.valueChoiceBox.getItems().clear();
			this.valueChoiceBox.getItems().addAll("-- Select Value --", "True", "False");
			this.valueChoiceBox.getSelectionModel().selectFirst();
			this.valueChoiceBox.getSelectionModel().selectedIndexProperty().addListener(valueListener);
		}
		else{
			this.valueTextField.setVisible(true);
			this.valueTextField.setManaged(true);
			this.valueTextField.setDisable(false);
			
			this.valueChoiceBox.setVisible(false);
			this.valueChoiceBox.setManaged(false);
			this.valueChoiceBox.setDisable(true);
		}
	}
	
	private final ChangeListener<Number> valueListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			int valueIndex=(Integer)number2;
			if(valueIndex!=0){
				String value=(String)valueChoiceBox.getItems().get((Integer)number2);
				if(value.equals("True")){
					conditionValue="true";
				}
				else{
					conditionValue="false";
				}
			}
		}
	};
	
	
	/*** event action ***/
	private void initActionSensor(){
		this.actionSensorChoiceBox.getItems().clear();
		this.actionSensorChoiceBox.getItems().add("-- Select Sensor --");
		for(int i=0;i<this.actionSensorList.size();i++){
			Sensor sensor=this.actionSensorList.get(i);
			String item=sensor.getName()+" ("+sensor.getType()+")";
			this.actionSensorChoiceBox.getItems().add(item);
		}
		this.actionSensorChoiceBox.getSelectionModel().selectFirst();
		this.actionSensorChoiceBox.getSelectionModel().selectedIndexProperty().addListener(actionSensorListener);
	}
	
	private final ChangeListener<Number> actionSensorListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			clearActionAttribute();
			clearTriggerValue();
			int sensorIndex=(Integer)number2;
			if(sensorIndex!=0){
				sensorIndex--;
				actionSensor=actionSensorList.get(sensorIndex);
				actionAttributeList=actionSensor.getAttributeList();
				initActionAttribute();
			}
		}
	};
	
	private void initActionAttribute(){
		this.actionAttributeChoiceBox.setDisable(false);
		this.actionAttributeChoiceBox.getItems().clear();
		this.actionAttributeChoiceBox.getItems().add("-- Select Attribute --");
		for(int i=0;i<this.actionAttributeList.size();i++){
			Attribute attribute=this.actionAttributeList.get(i);
			String item=attribute.getName()+" ("+attribute.getType()+")";
			this.actionAttributeChoiceBox.getItems().add(item);
		}
		this.actionAttributeChoiceBox.getSelectionModel().selectFirst();
		this.actionAttributeChoiceBox.getSelectionModel().selectedIndexProperty().addListener(actionAttributeListener);
	}
	
	private final ChangeListener<Number> actionAttributeListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			clearTriggerValue();
			int attributeIndex=(Integer)number2;
			if(attributeIndex!=0){
				attributeIndex--;
				actionAttribute=actionAttributeList.get(attributeIndex);
				initTriggerValue();
			}
		}
	};
	
	private void initTriggerValue(){
		if(this.actionAttribute.getType().equals("boolean")){
			this.triggerValueTextField.setVisible(false);
			this.triggerValueTextField.setManaged(false);
			this.triggerValueTextField.setDisable(true);
			
			this.triggerValueChoiceBox.setVisible(true);
			this.triggerValueChoiceBox.setManaged(true);
			this.triggerValueChoiceBox.setDisable(false);
			
			this.triggerValueChoiceBox.getItems().clear();
			this.triggerValueChoiceBox.getItems().addAll("-- Select Value --", "True", "False");
			this.triggerValueChoiceBox.getSelectionModel().selectFirst();
			this.triggerValueChoiceBox.getSelectionModel().selectedIndexProperty().addListener(triggerValueListener);
		}
		else{
			this.triggerValueTextField.setVisible(true);
			this.triggerValueTextField.setManaged(true);
			this.triggerValueTextField.setDisable(false);
			
			this.triggerValueChoiceBox.setVisible(false);
			this.triggerValueChoiceBox.setManaged(false);
			this.triggerValueChoiceBox.setDisable(true);
		}
	}
	
	private final ChangeListener<Number> triggerValueListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			int valueIndex=(Integer)number2;
			if(valueIndex!=0){
				String value=(String)triggerValueChoiceBox.getItems().get((Integer)number2);
				if(value.equals("True")){
					triggerValue="true";
				}
				else{
					triggerValue="false";
				}
			}
		}
	};
	
	@FXML private void onAddEvent(){
		boolean canContinue=true;
		String name=nameTextField.getText();
		if(name.equals("")){
			this.app.showMessageStrip("Warning", "Please fill out all required fields.", addEventPane);
			canContinue=false;
		}
		
		if(valueTextField.isVisible()){
			this.conditionValue=valueTextField.getText();
		}
		else{
			int index=this.valueChoiceBox.getSelectionModel().getSelectedIndex();
			if(index==0){
				this.conditionValue="";
			}
		}
		if(this.conditionValue.equals("")){
			this.app.showMessageStrip("Warning", "Please fill out all required fields.", addEventPane);
			canContinue=false;
		}
		else if(this.conditionValue.contains(" ")){
			this.app.showMessageStrip("Warning", "Event Value cannot contain whitespace.", addEventPane);
			canContinue=false;
		}
		
		if(this.triggerValueTextField.isVisible()){
			this.triggerValue=triggerValueTextField.getText();
		}
		else{
			int index=this.triggerValueChoiceBox.getSelectionModel().getSelectedIndex();
			if(index==0){
				this.triggerValue="";
			}
		}
		if(this.triggerValue.equals("")){
			this.app.showMessageStrip("Warning", "Please fill out all required fields.", addEventPane);
			canContinue=false;
		}
		else if(this.triggerValue.contains(" ")){
			this.app.showMessageStrip("Warning", "Trigger Value cannot contain whitespace.", addEventPane);
			canContinue=false;
		}
		
		if(canContinue){
			Task addTask=new Task<Void>(){
				@Override
				public Void call(){
					Event newEvent=new Event(0, name, "deactivated", average, condition, conditionValue, triggerValue, sourceSensor.getId(), sourceAttribute.getId(), actionSensor.getId(), actionAttribute.getId());
					Event event=app.getDatabase().saveEvent(newEvent);
					if(event!=null){
						Platform.runLater(() -> app.setEventsLayout());
						app.showMessageStripAndSave("Success", "Event", "Event '"+event.getName()+" ("+event.getId()+")' has been added.", addEventPane);
					}
					else{
						app.showMessageStripAndSave("Error", "Event", "Sorry, something went wrong adding the event '"+newEvent.getName()+"'.", addEventPane);
					}
					return null;
				}
			};
			this.app.executeAsyncTask(addTask, addEventPane);
		}
	}
}




