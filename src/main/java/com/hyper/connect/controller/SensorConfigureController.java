package com.hyper.connect.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hyper.connect.App;
import com.hyper.connect.model.*;

import com.hyper.connect.model.enums.*;
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
import javafx.util.Callback;
import javafx.scene.text.Text;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.concurrent.Task;
import javafx.application.Platform;

import java.util.ArrayList;
import java.io.IOException;


public class SensorConfigureController{
	private App app;
	private Sensor sensor;
	private ObservableList<Attribute> attributeObservableList;
	private boolean isInitialized=false;
	
	@FXML private StackPane sensorConfigurePane;
	@FXML private Text attributeListTitleText;
	@FXML private AnchorPane addAttributePane;
	@FXML private Label nameLabel;
	@FXML private TextField nameTextField;
	@FXML private Label typeLabel;
	@FXML private ChoiceBox typeChoiceBox;
	@FXML private Label directionLabel;
	@FXML private ChoiceBox directionChoiceBox;
	@FXML private Label intervalLabel;
	@FXML private ChoiceBox intervalChoiceBox;
	@FXML private Label intervalText;
	@FXML private Button addButton;
	@FXML private TableView attributeListTableView;
	@FXML private TableColumn idColumn;
	@FXML private TableColumn nameColumn;
	@FXML private TableColumn directionColumn;
	@FXML private TableColumn typeColumn;
	@FXML private TableColumn intervalColumn;
	@FXML private TableColumn scriptStateColumn;
	@FXML private TableColumn stateColumn;
	@FXML private TableColumn actionsColumn;

	public SensorConfigureController(){}
	
	public void setApp(App app){
		this.app=app;
	}
	
	public void init(Sensor sensor){
		this.sensor=sensor;
		initFields();

		Task initTask=new Task<Void>(){
			@Override
			public Void call(){
				if(!isInitialized){
					directionChoiceBox.getItems().addAll("input (from sensor)", "output (to sensor)");
					directionChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>(){
						@Override
						public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
							String value=(String)directionChoiceBox.getItems().get((Integer)number2);
							if(value.contains("input")){
								intervalText.setVisible(false);
								intervalText.setManaged(false);
								intervalChoiceBox.setVisible(true);
								intervalChoiceBox.setManaged(true);
							}
							else{
								intervalText.setVisible(true);
								intervalText.setManaged(true);
								intervalChoiceBox.setVisible(false);
								intervalChoiceBox.setManaged(false);
							}
						}
					});
					for(int i=5;i<60;i++){
						intervalChoiceBox.getItems().add(i+" sec");
					}
					typeChoiceBox.getItems().addAll("string", "boolean", "integer", "double");
					isInitialized=true;
				}
				
				Platform.runLater(() -> clearFields());
				
				attributeListTitleText.setText("Attribute List of Sensor '"+sensor.getName()+" ("+sensor.getId()+")'");
				ArrayList<Attribute> attributeList=app.getDatabase().getAttributeListBySensorId(sensor.getId());
				attributeObservableList=FXCollections.observableArrayList(attributeList);
				idColumn.setCellValueFactory(new PropertyValueFactory("id"));
				nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
				directionColumn.setCellValueFactory(new PropertyValueFactory("direction"));
				typeColumn.setCellValueFactory(new PropertyValueFactory("type"));
				
				Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>> intervalCellFactory=new Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>>(){
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
									int interval=attribute.getInterval();
									Text intervalText=new Text();
									if(interval>0){
										intervalText.setText(interval+" sec");
									}
									else{
										intervalText.setText("Event-driven");
									}
									setGraphic(intervalText);
									setText(null);
								}
							}
						};
						return cell;
					}
				};
				
				Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>> scriptStateCellFactory=new Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>>(){
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
									AttributeScriptState scriptState=attribute.getScriptState();
									Text scriptStateText=new Text(scriptState.toString());
									if(scriptState==AttributeScriptState.VALID){
										scriptStateText.setFill(Color.GREEN);
									}
									else{
										scriptStateText.setFill(Color.RED);
									}
									setGraphic(scriptStateText);
									setText(null);
								}
							}
						};
						return cell;
					}
				};
				
				Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>> stateCellFactory=new Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>>(){
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
									AttributeState state=attribute.getState();
									Text stateText=new Text(state.toString());
									if(state==AttributeState.ACTIVE){
										stateText.setFill(Color.GREEN);
									}
									else{
										stateText.setFill(Color.RED);
									}
									setGraphic(stateText);
									setText(null);
								}
							}
						};
						return cell;
					}
				};
				
				Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>> actionsCellFactory=new Callback<TableColumn<Attribute, String>, TableCell<Attribute, String>>(){
					@Override
					public TableCell call(final TableColumn<Attribute, String> param){
						final TableCell<Attribute, String> cell=new TableCell<Attribute, String>(){
							final ToggleButton stateButton=new ToggleButton();
							final Button scriptButton=new Button();
							final Button deleteButton=new Button();
							
							@Override
							public void updateItem(String item, boolean empty){
								super.updateItem(item, empty);

                                stateButton.setStyle("-fx-background-color: transparent;");
                                stateButton.setPadding(new Insets(0, 0, 0, 0));
                                stateButton.setCursor(Cursor.HAND);
								scriptButton.getStyleClass().add("simple_button");
								scriptButton.setCursor(Cursor.HAND);
								deleteButton.getStyleClass().add("simple_button");
								deleteButton.setCursor(Cursor.HAND);
								
								if(empty){
									setGraphic(null);
									setText(null);
								}
								else{
									Attribute attribute=getTableView().getItems().get(getIndex());

									if(attribute.getState()==AttributeState.ACTIVE){
                                        setToggleButtonState(stateButton, true);
									}
									else{
                                        setToggleButtonState(stateButton, false);
									}
									stateButton.setOnAction(event -> {
										if(attribute.getScriptState()==AttributeScriptState.VALID){
											boolean isSelected=stateButton.isSelected();
											if(isSelected){
												Task stateChangeTask=new Task<Void>(){
													@Override
													public Void call(){
														attribute.setState(AttributeState.ACTIVE);
														boolean updateResult=app.getDatabase().updateAttribute(attribute);
														if(updateResult){
															attributeListTableView.getItems().set(getIndex(), attribute);
															app.showMessageStripAndSave("Success", "Device", "Attribute '"+attribute.getName()+" ("+attribute.getId()+")' has been activated.", sensorConfigurePane);
															app.getAttributeManager().startAttribute(attribute.getId());
															JsonObject jsonObject=new JsonObject();
															jsonObject.addProperty("command", "changeAttributeState");
															jsonObject.addProperty("id", attribute.getId());
															jsonObject.addProperty("state", isSelected);
															app.getElastosCarrier().sendDataToControllers(jsonObject);
														}
														else{
															stateButton.setSelected(false);
															app.showMessageStripAndSave("Error", "Device", "Sorry, something went wrong changing the state of attribute '"+attribute.getName()+" ("+attribute.getId()+")'.", sensorConfigurePane);
														}
														return null;
													}
												};
												app.executeAsyncTask(stateChangeTask, sensorConfigurePane);
											}
											else{
												ArrayList<Event> eventList=app.getDatabase().getActiveEventListOnlyNameAndIdByAttributeId(attribute.getId());
												if(eventList.isEmpty()){
													Task stateChangeTask=new Task<Void>(){
														@Override
														public Void call(){
															attribute.setState(AttributeState.DEACTIVATED);
															boolean updateResult=app.getDatabase().updateAttribute(attribute);
															if(updateResult){
																attributeListTableView.getItems().set(getIndex(), attribute);
																app.showMessageStripAndSave("Success", "Device", "Attribute '"+attribute.getName()+" ("+attribute.getId()+")' has been deactivated.", sensorConfigurePane);
																app.getAttributeManager().stopAttribute(attribute.getId());
															}
															else{
																stateButton.setSelected(true);
																app.showMessageStripAndSave("Error", "Device", "Sorry, something went wrong changing the state of attribute '"+attribute.getName()+" ("+attribute.getId()+")'.", sensorConfigurePane);
															}
															return null;
														}
													};
													app.executeAsyncTask(stateChangeTask, sensorConfigurePane);
													JsonObject jsonObject=new JsonObject();
													jsonObject.addProperty("command", "changeAttributeState");
													jsonObject.addProperty("id", attribute.getId());
													jsonObject.addProperty("state", false);
													app.getElastosCarrier().sendDataToControllers(jsonObject);
												}
												else{
													stateButton.setSelected(true);
													try{
														Region veil=new Region();
														veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");

														FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/dialog_deactivate_attribute.fxml"));
														AnchorPane deactivateDialogContent=(AnchorPane)loader.load();

														AnchorPane titelPane=(AnchorPane)deactivateDialogContent.lookup("#titelPane");
														Text dialogText=(Text)deactivateDialogContent.lookup("#dialogText");
														HBox actionsHbox=(HBox)deactivateDialogContent.lookup("#actionsHbox");
														double dialogHeight=dialogText.getBoundsInLocal().getHeight()+titelPane.getPrefHeight()+actionsHbox.getPrefHeight()+30;
														deactivateDialogContent.setPrefHeight(dialogHeight);

														Button cancelButton=(Button)deactivateDialogContent.lookup("#cancelButton");
														cancelButton.setOnAction(cancelEvent -> {
															app.closeDialog(veil, deactivateDialogContent, sensorConfigurePane);
														});

														Button confirmButton=(Button)deactivateDialogContent.lookup("#confirmButton");
														confirmButton.setOnAction(confirmEvent -> {
															Task stateChangeTask=new Task<Void>(){
																@Override
																public Void call(){
																	attribute.setState(AttributeState.DEACTIVATED);
																	int errorCount=0;
																	boolean updateResult=app.getDatabase().updateAttribute(attribute);
																	if(updateResult){
																		for(Event event : eventList){
																			boolean stateChangeResult=app.getDatabase().setEventStateByEventId(event.getId(), EventState.DEACTIVATED);
																			if(!stateChangeResult){
																				errorCount++;
																			}
																			if(event.getType()==EventType.GLOBAL){
																				JsonObject jsonObject=new JsonObject();
																				jsonObject.addProperty("command", "changeEventState");
																				jsonObject.addProperty("globalEventId", event.getGlobalEventId());
																				jsonObject.addProperty("state", false);
																				app.getElastosCarrier().sendDataToControllers(jsonObject);

																				if(event.getEdgeType()==EventEdgeType.SOURCE){
																					jsonObject.addProperty("edgeType", EventEdgeType.ACTION.getValue());
																					app.getElastosCarrier().sendDataToDevice(event.getActionDeviceUserId(), jsonObject);
																				}
																				else if(event.getEdgeType()==EventEdgeType.ACTION){
																					jsonObject.addProperty("edgeType", EventEdgeType.SOURCE.getValue());
																					app.getElastosCarrier().sendDataToDevice(event.getSourceDeviceUserId(), jsonObject);
																				}
																			}
																		}
																	}
																	else{
																		errorCount++;
																	}
																	
																	if(errorCount==0){
																		attributeListTableView.getItems().set(getIndex(), attribute);
																		app.showMessageStripAndSave("Success", "Device", "Attribute '"+attribute.getName()+" ("+attribute.getId()+")' has been deactivated.", sensorConfigurePane);
																		app.getAttributeManager().stopAttribute(attribute.getId());
																	}
																	else{
																		stateButton.setSelected(true);
																		app.showMessageStripAndSave("Error", "Device", "Sorry, something went wrong changing the state of attribute '"+attribute.getName()+" ("+attribute.getId()+")'.", sensorConfigurePane);
																	}

																	return null;
																}
															};
															app.executeAsyncTask(stateChangeTask, sensorConfigurePane);
															app.closeDialog(veil, deactivateDialogContent, sensorConfigurePane);
														});

														app.showDialog(veil, deactivateDialogContent, sensorConfigurePane);
													}
													catch(IOException ioe){}
												}
											}
										}
										else{
											stateButton.setSelected(false);
											app.showMessageStrip("Warning", "You have to validate the script.", sensorConfigurePane);
										}
										
									});
									
									ImageView codeImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_code_white_24.png")));
									codeImageView.setFitWidth(18);
									codeImageView.setFitHeight(18);
									scriptButton.setGraphic(codeImageView);
									scriptButton.setOnAction(scriptEvent -> {
										app.setScriptLayout(sensor, attribute);
									});
									
									ImageView deleteImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_delete_white_24.png")));
									deleteImageView.setFitWidth(18);
									deleteImageView.setFitHeight(18);
									deleteButton.setGraphic(deleteImageView);
									deleteButton.setOnAction(deleteEvent -> {
										try{
											Region veil=new Region();
											veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");

											FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/dialog_delete_attribute.fxml"));
											AnchorPane deleteDialogContent=(AnchorPane)loader.load();

											AnchorPane titelPane=(AnchorPane)deleteDialogContent.lookup("#titelPane");
											Text dialogText=(Text)deleteDialogContent.lookup("#dialogText");
											HBox actionsHbox=(HBox)deleteDialogContent.lookup("#actionsHbox");
											double dialogHeight=dialogText.getBoundsInLocal().getHeight()+titelPane.getPrefHeight()+actionsHbox.getPrefHeight()+30;
											deleteDialogContent.setPrefHeight(dialogHeight);

											Button cancelButton=(Button)deleteDialogContent.lookup("#cancelButton");
											cancelButton.setOnAction(cancelEvent -> {
												app.closeDialog(veil, deleteDialogContent, sensorConfigurePane);
											});
											Button confirmButton=(Button)deleteDialogContent.lookup("#confirmButton");
											confirmButton.setOnAction(confirmEvent -> {
												Task deleteTask=new Task<Void>(){
													@Override
													public Void call(){
														int errorCount=0;
														ArrayList<Event> eventList=app.getDatabase().getEventListOnlyNameAndIdByAttributeId(attribute.getId());
														for(int i=0;i<eventList.size();i++){
															Event event=eventList.get(i);
															boolean deleteEventResult=app.getDatabase().deleteEventByEventId(event.getId());
															if(!deleteEventResult){
																errorCount++;
															}
														}
														boolean deleteAttributeResult=app.getDatabase().deleteAttributeByAttributeId(attribute.getId());
														if(!deleteAttributeResult){
															errorCount++;
														}
														
														if(errorCount==0){
															attributeObservableList.removeAll(attribute);
															app.showMessageStripAndSave("Success", "Device", "Attribute '"+attribute.getName()+" ("+attribute.getId()+")' has been deleted.", sensorConfigurePane);
															app.getAttributeManager().deleteAttribute(attribute.getId());
														}
														else{
															app.showMessageStripAndSave("Error", "Device", "Sorry, something went wrong deleting the attribute '"+attribute.getName()+" ("+attribute.getId()+")'.", sensorConfigurePane);
														}
														return null;
													}
												};
												app.executeAsyncTask(deleteTask, sensorConfigurePane);
												app.closeDialog(veil, deleteDialogContent, sensorConfigurePane);
											});

											app.showDialog(veil, deleteDialogContent, sensorConfigurePane);
										}
										catch(IOException ioe){}
									});
									
									HBox hbox=new HBox(stateButton, scriptButton, deleteButton);
									hbox.setSpacing(10);
									hbox.setAlignment(Pos.CENTER_LEFT);
									setGraphic(hbox);
									setText(null);
								}
							}
						};
						return cell;
					}
				};
				intervalColumn.setCellFactory(intervalCellFactory);
				scriptStateColumn.setCellFactory(scriptStateCellFactory);
				stateColumn.setCellFactory(stateCellFactory);
				actionsColumn.setCellFactory(actionsCellFactory);
				attributeListTableView.setItems(attributeObservableList);
				
				return null;
			}
		};
		this.app.executeAsyncTask(initTask, sensorConfigurePane);
	}

	private void initFields(){
		GridPane firstGrid=new GridPane();
		firstGrid.add(nameLabel, 0, 0);
		firstGrid.add(nameTextField, 1, 0);
		firstGrid.add(typeLabel, 0, 1);
		firstGrid.add(typeChoiceBox, 1, 1);
		firstGrid.add(addButton, 1, 2);
		GridPane.setMargin(addButton, new Insets(5, 0, 0, 0));
		firstGrid.setHgap(5);
		firstGrid.setVgap(5);

		GridPane secondGrid=new GridPane();
		secondGrid.add(directionLabel, 0, 0);
		secondGrid.add(directionChoiceBox, 1, 0);
		secondGrid.add(intervalLabel, 0, 1);
		secondGrid.add(intervalChoiceBox, 1, 1);

		HBox intervalBox=new HBox(intervalText);
		intervalBox.setPrefSize(149, 25);
		intervalBox.setMaxSize(149, 25);
		intervalBox.setAlignment(Pos.CENTER_LEFT);
		secondGrid.add(intervalBox, 1, 1);

		secondGrid.setHgap(5);
		secondGrid.setVgap(5);

		HBox hbox=new HBox(firstGrid, secondGrid);
		hbox.setAlignment(Pos.CENTER);
		hbox.setSpacing(20);
		addAttributePane.getChildren().add(hbox);
		AnchorPane.setRightAnchor(hbox, 0.0);
		AnchorPane.setLeftAnchor(hbox, 0.0);
		AnchorPane.setTopAnchor(hbox, 50.0);
		AnchorPane.setBottomAnchor(hbox, 0.0);
	}

	private void setToggleButtonState(ToggleButton toggleButton, boolean state){
	    if(state){
            ImageView onImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_toggle_on_green_24.png")));
            toggleButton.setGraphic(onImageView);
        }
	    else{
            ImageView offImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_toggle_off_gray_24.png")));
            toggleButton.setGraphic(offImageView);
        }
	    toggleButton.setSelected(state);
    }
	
	private void clearFields(){
		nameTextField.clear();
		directionChoiceBox.getSelectionModel().selectFirst();
		intervalChoiceBox.getSelectionModel().selectFirst();
		typeChoiceBox.getSelectionModel().selectFirst();
	}
	
	@FXML
    private void onAddAttribute(){
		String name=nameTextField.getText();
		
		if(name.equals("")){
			this.app.showMessageStrip("Warning", "Please fill out all required fields.", sensorConfigurePane);
		}
		else{
			nameTextField.clear();
			
			Task addTask=new Task<Void>(){
				@Override
				public Void call(){
					AttributeType type=null;
					String stringType=(String)typeChoiceBox.getValue();
					if(stringType.equals("string")){
						type=AttributeType.STRING;
					}
					else if(stringType.equals("boolean")){
						type=AttributeType.BOOLEAN;
					}
					else if(stringType.equals("integer")){
						type=AttributeType.INTEGER;
					}
					else if(stringType.equals("double")){
						type=AttributeType.DOUBLE;
					}
					String directionValue=(String)directionChoiceBox.getValue();
					AttributeDirection direction=AttributeDirection.INPUT;
					String intervalValue=(String)intervalChoiceBox.getValue();
					int interval=0;
					if(directionValue.contains("output")){
						direction=AttributeDirection.OUTPUT;
					}
					else{
						interval=Integer.parseInt(intervalValue.substring(0, intervalValue.length()-4));
					}
			
					Attribute newAttribute=app.getDatabase().saveAttribute(new Attribute(0, name, direction, type, interval, AttributeScriptState.INVALID, AttributeState.DEACTIVATED, sensor.getId()));
					if(newAttribute!=null){
						attributeObservableList.addAll(newAttribute);
						app.showMessageStripAndSave("Success", "Device", "Attribute '"+newAttribute.getName()+" ("+newAttribute.getId()+")' has been added.", sensorConfigurePane);
						app.getAttributeManager().addAttribute(newAttribute);
						JsonElement jsonAttribute=new Gson().toJsonTree(newAttribute);
						JsonObject jsonObject=new JsonObject();
						jsonObject.addProperty("command", "addAttribute");
						jsonObject.add("attribute", jsonAttribute);
						app.getElastosCarrier().sendDataToControllers(jsonObject);
					}
					else{
						app.showMessageStripAndSave("Error", "Device", "Sorry, something went wrong adding the attribute '"+name+"'.", sensorConfigurePane);
					}
					return null;
				}
			};
			this.app.executeAsyncTask(addTask, sensorConfigurePane);
		}
	}
	
}