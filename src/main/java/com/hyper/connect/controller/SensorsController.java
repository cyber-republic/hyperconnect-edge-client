package com.hyper.connect.controller;

import com.hyper.connect.App;
import com.hyper.connect.model.Sensor;
import com.hyper.connect.model.Attribute;
import com.hyper.connect.model.Event;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.io.IOException;


public class SensorsController{
	private App app;
	private ObservableList<Sensor> sensorObservableList;


	@FXML private StackPane sensorsPane;
	@FXML private TableView sensorListTableView;
	@FXML private TableColumn idColumn;
	@FXML private TableColumn nameColumn;
	@FXML private TableColumn typeColumn;
	@FXML private TableColumn actionsColumn;
	@FXML private AnchorPane addSensorPane;
	@FXML private Label nameLabel;
	@FXML private TextField nameTextField;
	@FXML private Label typeLabel;
	@FXML private TextField typeTextField;
	@FXML private Button addButton;

	public SensorsController(){}
	
	public void setApp(App app){
		this.app=app;
	}
	
	public void init(){
		initFields();

		Task initTask=new Task<Void>(){
			@Override
			public Void call(){
				clearFields();
				
				ArrayList<Sensor> sensorList=app.getDatabase().getSensorList();
				sensorObservableList=FXCollections.observableArrayList(sensorList);
				idColumn.setCellValueFactory(new PropertyValueFactory("id"));
				nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
				typeColumn.setCellValueFactory(new PropertyValueFactory("type"));

				Callback<TableColumn<Sensor, String>, TableCell<Sensor, String>> cellFactory=new Callback<TableColumn<Sensor, String>, TableCell<Sensor, String>>(){
					@Override
					public TableCell call(final TableColumn<Sensor, String> param){
						final TableCell<Sensor, String> cell=new TableCell<Sensor, String>(){
							final Button configureButton=new Button("Configure");
							final Button overviewButton=new Button("Overview");
							final Button deleteButton=new Button();

							@Override
							public void updateItem(String item, boolean empty){
								super.updateItem(item, empty);

								configureButton.getStyleClass().add("simple_button");
								configureButton.setCursor(Cursor.HAND);
								overviewButton.getStyleClass().add("simple_button");
								overviewButton.setCursor(Cursor.HAND);
								deleteButton.getStyleClass().add("simple_button");
								deleteButton.setCursor(Cursor.HAND);

								if(empty){
									setGraphic(null);
									setText(null);
								}
								else{
									ImageView configureImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_build_white_24.png")));
									configureImageView.setFitWidth(18);
									configureImageView.setFitHeight(18);
									configureButton.setGraphic(configureImageView);
									configureButton.setOnAction(configureEvent -> {
										Sensor sensor=getTableView().getItems().get(getIndex());
										app.setSensorConfigureLayout(sensor);
									});
									
									ImageView overviewImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_list_alt_white_24.png")));
									overviewImageView.setFitWidth(18);
									overviewImageView.setFitHeight(18);
									overviewButton.setGraphic(overviewImageView);
									overviewButton.setOnAction(overviewEvent -> {
										Sensor sensor=getTableView().getItems().get(getIndex());
										app.setSensorOverviewLayout(sensor);
									});
									
									ImageView deleteImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_delete_white_24.png")));
									deleteImageView.setFitWidth(18);
									deleteImageView.setFitHeight(18);
									deleteButton.setGraphic(deleteImageView);
									deleteButton.setOnAction(deleteEvent -> {
										try{
											Region veil=new Region();
											veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");

											FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/dialog_delete_sensor.fxml"));
											AnchorPane deleteDialogContent=(AnchorPane)loader.load();

											AnchorPane titelPane=(AnchorPane)deleteDialogContent.lookup("#titelPane");
											Text dialogText=(Text)deleteDialogContent.lookup("#dialogText");
											HBox actionsHbox=(HBox)deleteDialogContent.lookup("#actionsHbox");
											double dialogHeight=dialogText.getBoundsInLocal().getHeight()+titelPane.getPrefHeight()+actionsHbox.getPrefHeight()+30;
											deleteDialogContent.setPrefHeight(dialogHeight);

											Button cancelButton=(Button)deleteDialogContent.lookup("#cancelButton");
											cancelButton.setOnAction(cancelEvent -> {
												app.closeDialog(veil, deleteDialogContent, sensorsPane);
											});
											Button confirmButton=(Button)deleteDialogContent.lookup("#confirmButton");
											confirmButton.setOnAction(confirmEvent -> {
												Task deleteTask=new Task<Void>(){
													@Override
													public Void call(){
														int errorCount=0;
														Sensor sensor=getTableView().getItems().get(getIndex());
														ArrayList<Attribute> attributeList=app.getDatabase().getAttributeListBySensorId(sensor.getId());
														for(int i=0;i<attributeList.size();i++){
															Attribute attribute=attributeList.get(i);
															ArrayList<Event> eventList=app.getDatabase().getEventListOnlyNameAndIdByAttributeId(attribute.getId());
															for(int j=0;j<eventList.size();j++){
																Event event=eventList.get(j);
																boolean deleteEventResult=app.getDatabase().deleteEventByEventId(event.getId());
																if(!deleteEventResult){
																	errorCount++;
																}
															}
															boolean deleteAttributeResult=app.getDatabase().deleteAttributeByAttributeId(attribute.getId());
															if(!deleteAttributeResult){
																errorCount++;
															}
														}
														boolean deleteSensorResult=app.getDatabase().deleteSensorBySensorId(sensor.getId());
														if(!deleteSensorResult){
															errorCount++;
														}
														
														if(errorCount==0){
															sensorObservableList.removeAll(sensor);
															app.showMessageStripAndSave("Success", "Device", "Sensor '"+sensor.getName()+" ("+sensor.getId()+")' has been deleted.", sensorsPane);
														}
														else{
															app.showMessageStripAndSave("Error", "Device", "Sorry, something went wrong deleting the sensor '"+sensor.getName()+" ("+sensor.getId()+")'.", sensorsPane);
														}

														return null;
													}
												};
												app.executeAsyncTask(deleteTask, sensorsPane);
												app.closeDialog(veil, deleteDialogContent, sensorsPane);
											});

											app.showDialog(veil, deleteDialogContent, sensorsPane);
										}
										catch(IOException ioe){}
									});

									HBox hbox=new HBox(configureButton, overviewButton, deleteButton);
									hbox.setSpacing(10);
									setGraphic(hbox);
									setText(null);
								}
							}
						};
						return cell;
					}
				};
				actionsColumn.setCellFactory(cellFactory);
				sensorListTableView.setItems(sensorObservableList);

				return null;
			}
		};
		this.app.executeAsyncTask(initTask, sensorsPane);
	}

	private void initFields(){
		GridPane gridPane=new GridPane();
		gridPane.add(nameLabel, 0, 0);
		gridPane.add(nameTextField, 1, 0);
		gridPane.add(typeLabel, 0, 1);
		gridPane.add(typeTextField, 1, 1);
		gridPane.add(addButton, 1, 2);
		GridPane.setMargin(addButton, new Insets(5, 0, 0, 0));
		gridPane.setHgap(5);
		gridPane.setVgap(5);

		HBox hbox=new HBox(gridPane);
		hbox.setAlignment(Pos.CENTER);
		addSensorPane.getChildren().add(hbox);
		AnchorPane.setRightAnchor(hbox, 10.0);
		AnchorPane.setLeftAnchor(hbox, 10.0);
		AnchorPane.setTopAnchor(hbox, 50.0);
		AnchorPane.setBottomAnchor(hbox, 10.0);
	}

	private void clearFields(){
		nameTextField.clear();
		typeTextField.clear();
	}

	@FXML
    private void onAddSensor(){
		String name=nameTextField.getText();
		String type=typeTextField.getText();
		if(name.equals("") || type.equals("")){
			this.app.showMessageStrip("Warning", "Please fill out all required fields.", sensorsPane);
		}
		else{
			nameTextField.clear();
			typeTextField.clear();

			Task addTask=new Task<Void>(){
				@Override
				public Void call(){
					Sensor newSensor=app.getDatabase().saveSensor(new Sensor(0, name, type));
					if(newSensor!=null){
						sensorObservableList.addAll(newSensor);
						app.showMessageStripAndSave("Success", "Device", "Sensor '"+newSensor.getName()+" ("+newSensor.getId()+")' has been added.", sensorsPane);
					}
					else{
						app.showMessageStripAndSave("Error", "Device", "Sorry, something went wrong adding the sensor '"+name+"'.", sensorsPane);
					}
					return null;
				}
			};
			this.app.executeAsyncTask(addTask, sensorsPane);
		}
	}


}