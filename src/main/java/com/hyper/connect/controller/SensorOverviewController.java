package com.hyper.connect.controller;

import com.hyper.connect.App;
import com.hyper.connect.model.*;
import com.hyper.connect.model.enums.*;
import com.hyper.connect.util.CustomUtil;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.concurrent.Task;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;


public class SensorOverviewController{
	private App app;
	private Sensor sensor;
	private ObservableList<Attribute> inputObservableList;
	private ObservableList<Attribute> outputObservableList;
	
	@FXML private StackPane sensorOverviewPane;
	
	@FXML private Text inputTitleText;
	@FXML private TableView inputTableView;
	@FXML private TableColumn inputIdColumn;
	@FXML private TableColumn inputNameColumn;
	@FXML private TableColumn inputTypeColumn;
	@FXML private TableColumn inputLatestValueColumn;
	@FXML private TableColumn inputDateTimeColumn;
	
	@FXML private Text outputTitleText;
	@FXML private TableView outputTableView;
	@FXML private TableColumn outputIdColumn;
	@FXML private TableColumn outputNameColumn;
	@FXML private TableColumn outputTypeColumn;
	@FXML private TableColumn outputActionColumn;
	
	
	public SensorOverviewController(){}
	
	public void setApp(App app){
		this.app=app;
	}
	
	public void init(Sensor sensor){
		this.sensor=sensor;
		Task initTask=new Task<Void>(){
			@Override
			public Void call(){
				initInputList();
				initOutputList();
				return null;
			}
		};
		this.app.executeAsyncTask(initTask, sensorOverviewPane);
	}
	
	private void initInputList(){
		this.inputTitleText.setText("Input Attribute List (from sensor) of Sensor '"+this.sensor.getName()+" ("+this.sensor.getId()+")'");
		ArrayList<Attribute> attributeList=this.app.getDatabase().getAttributeListBySensorIdAndDirection(this.sensor.getId(), AttributeDirection.INPUT);
		this.inputObservableList=FXCollections.observableArrayList(attributeList);
		inputIdColumn.setCellValueFactory(new PropertyValueFactory("id"));
		inputNameColumn.setCellValueFactory(new PropertyValueFactory("name"));
		inputTypeColumn.setCellValueFactory(new PropertyValueFactory("type"));
		
		Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>> latestValueCellFactory=new Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>>(){
			@Override
			public TableCell call(final TableColumn<Attribute, String> param){
				final TableCell<Attribute, String> cell=new TableCell<Attribute, String>(){
					@Override
					public void updateItem(String item, boolean empty){
						super.updateItem(item, empty);
						if(empty){
							setGraphic(null);
							setText(null);
						}
						else{
							Attribute attribute=getTableView().getItems().get(getIndex());
							DataRecord dataRecord=app.getAttributeManager().getCurrentDataRecord(attribute.getId());
							String latestValue="No value";
							if(dataRecord==null){
								dataRecord=app.getDatabase().getDataRecordByAttributeId(attribute.getId());
								if(dataRecord!=null){
									latestValue=dataRecord.getValue();
								}
							}
							else{
								latestValue=dataRecord.getValue();
							}
							Text latestValueText=new Text(latestValue);
							
							setGraphic(latestValueText);
							setText(null);
						}
					}
				};
				return cell;
			}
		};
		
		Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>> dateTimeCellFactory=new Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>>(){
			@Override
			public TableCell call(final TableColumn<Attribute, String> param){
				final TableCell<Attribute, String> cell=new TableCell<Attribute, String>(){
					@Override
					public void updateItem(String item, boolean empty){
						super.updateItem(item, empty);
						if(empty){
							setGraphic(null);
							setText(null);
						}
						else{
							Attribute attribute=getTableView().getItems().get(getIndex());
							DataRecord dataRecord=app.getAttributeManager().getCurrentDataRecord(attribute.getId());
							String dateTime="No value";
							if(dataRecord==null){
								dataRecord=app.getDatabase().getDataRecordByAttributeId(attribute.getId());
								if(dataRecord!=null){
									dateTime=dataRecord.getDateTime();
								}
							}
							else{
								dateTime=dataRecord.getDateTime();
							}
							
							dateTime=CustomUtil.getDateTimeByTimeZone(dateTime, app.getTimeZone());
							
							Text dateTimeText=new Text(dateTime);
							
							setGraphic(dateTimeText);
							setText(null);
						}
					}
				};
				return cell;
			}
		};
		
		inputLatestValueColumn.setCellFactory(latestValueCellFactory);
		inputDateTimeColumn.setCellFactory(dateTimeCellFactory);
		inputTableView.setItems(inputObservableList);
	}
	
	private void initOutputList(){
		outputTitleText.setText("Output Attribute List (to sensor) of Sensor '"+this.sensor.getName()+" ("+this.sensor.getId()+")'");
		ArrayList<Attribute> attributeList=this.app.getDatabase().getAttributeListBySensorIdAndDirection(this.sensor.getId(), AttributeDirection.OUTPUT);
		this.outputObservableList=FXCollections.observableArrayList(attributeList);
		this.outputIdColumn.setCellValueFactory(new PropertyValueFactory("id"));
		this.outputNameColumn.setCellValueFactory(new PropertyValueFactory("name"));
		this.outputTypeColumn.setCellValueFactory(new PropertyValueFactory("type"));
		
		Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>> actionCellFactory=new Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>>(){
			@Override
			public TableCell call(final TableColumn<Attribute, String> param){
				final TableCell<Attribute, String> cell=new TableCell<Attribute, String>(){
					
					final TextField valueTextField=new TextField();
					final ChoiceBox valueChoiceBox=new ChoiceBox();
					final Button sendButton=new Button("Send");

					@Override
					public void updateItem(String item, boolean empty){
						super.updateItem(item, empty);

						sendButton.getStyleClass().add("simple_button");
						sendButton.setCursor(Cursor.HAND);
						valueTextField.setPromptText("Enter Value (required)");
						valueTextField.setPrefWidth(150);
						valueChoiceBox.getItems().addAll("True", "False");
						valueChoiceBox.getSelectionModel().selectFirst();
						valueChoiceBox.setPrefWidth(150);

						if(empty){
							setGraphic(null);
							setText(null);
						}
						else{
							Attribute attribute=getTableView().getItems().get(getIndex());
							AttributeType attributeType=attribute.getType();
							
							ImageView sendImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_send_white_24.png")));
							sendImageView.setFitWidth(18);
							sendImageView.setFitHeight(18);
							sendButton.setGraphic(sendImageView);
							sendButton.setOnAction(sendEvent -> {
								String value="";
								if(attributeType==AttributeType.BOOLEAN){
									value=valueChoiceBox.getSelectionModel().getSelectedItem().toString();
								}
								else{
									value=valueTextField.getText();
								}
								
								if(value.isEmpty()){
									app.showMessageStrip(NotificationType.WARNING, "Please fill out the required field for the attribute '"+attribute.getName()+" ("+attribute.getId()+")'.", sensorOverviewPane);
								}
								else if(value.contains(" ")){
									app.showMessageStrip(NotificationType.WARNING, "The input field cannot contain whitespace.", sensorOverviewPane);
								}
								else if(attribute.getState()==AttributeState.DEACTIVATED){
									app.showMessageStrip(NotificationType.WARNING, "Attribute '"+attribute.getName()+" ("+attribute.getId()+")' is not active.", sensorOverviewPane);
								}
								else{
									final String finalValue=value;
									Task actionTask=new Task<Void>(){
										@Override
										public Void call(){
											boolean executeResult=app.getAttributeManager().executeAction(attribute.getId(), finalValue);
											if(executeResult){
												valueTextField.clear();
												Notification notification=new Notification(0, NotificationType.SUCCESS, NotificationCategory.ATTRIBUTE, Integer.toString(attribute.getId()), "Action message with the value '"+finalValue+"' has been sent to attribute '"+attribute.getName()+" ("+attribute.getId()+")'.", CustomUtil.getCurrentDateTime());
												app.showAndSaveNotification(notification, sensorOverviewPane);
											}
											else{
												Notification notification=new Notification(0, NotificationType.ERROR, NotificationCategory.ATTRIBUTE, Integer.toString(attribute.getId()), "Sorry, something went wrong sending the value '"+finalValue+"' to the attribute '"+attribute.getName()+" ("+attribute.getId()+")'.", CustomUtil.getCurrentDateTime());
												app.showAndSaveNotification(notification, sensorOverviewPane);
											}
											return null;
										}
									};
									app.executeAsyncTask(actionTask, sensorOverviewPane);
								}
							});
							
							HBox hbox=new HBox(valueTextField, sendButton);
							if(attributeType==AttributeType.BOOLEAN){
								hbox=new HBox(valueChoiceBox, sendButton);
							}
							
							hbox.setSpacing(5);
							setGraphic(hbox);
							setText(null);
						}
					}
				};
				return cell;
			}
		};
		
		this.outputActionColumn.setCellFactory(actionCellFactory);
		this.outputTableView.setItems(this.outputObservableList);
	}
	
	@FXML
	private void onHistoryButton(){
		this.app.setSensorHistoryLayout(this.sensor);
	}
	
	@FXML
	private void onRefreshButton(){
		Task refreshTask=new Task<Void>(){
			@Override
			public Void call(){
				initInputList();
				return null;
			}
		};
		this.app.executeAsyncTask(refreshTask, sensorOverviewPane);
	}
}