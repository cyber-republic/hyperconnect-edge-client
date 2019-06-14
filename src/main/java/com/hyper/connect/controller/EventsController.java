package com.hyper.connect.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hyper.connect.App;
import com.hyper.connect.model.*;

import com.hyper.connect.model.enums.*;
import com.hyper.connect.util.CustomUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.io.IOException;


public class EventsController{
	private App app;
	private ObservableList<Event> localEventObservableList;
	private ObservableList<Event> globalEventObservableList;
	
	@FXML private StackPane eventsPane;
	@FXML private TableView localEventListTableView;
	@FXML private TableColumn localIdColumn;
	@FXML private TableColumn localNameColumn;
	@FXML private TableColumn localSourceColumn;
	@FXML private TableColumn localActionColumn;
	@FXML private TableColumn localStateColumn;
	@FXML private TableColumn localActionsColumn;
	@FXML private TableView globalEventListTableView;
	@FXML private TableColumn globalIdColumn;
	@FXML private TableColumn globalNameColumn;
	@FXML private TableColumn globalSourceColumn;
	@FXML private TableColumn globalActionColumn;
	@FXML private TableColumn globalStateColumn;
	@FXML private TableColumn globalActionsColumn;
	
	public EventsController(){}
	
	public void setApp(App app){
		this.app=app;
	}
	
	public void init(){
		Task initTask=new Task<Void>(){
			@Override
			public Void call(){
				initLocalEventList();
				initGlobalEventList();
				return null;
			}
		};
		this.app.executeAsyncTask(initTask, eventsPane);
	}

	private void initLocalEventList(){
		ArrayList<Event> eventList=app.getDatabase().getEventListByType(EventType.LOCAL);
		localEventObservableList=FXCollections.observableArrayList(eventList);
		localIdColumn.setCellValueFactory(new PropertyValueFactory("id"));
		localNameColumn.setCellValueFactory(new PropertyValueFactory("name"));

		Callback<TableColumn<Event, String>, TableCell<Event, String>> stateCellFactory=new Callback<TableColumn<Event, String>, TableCell<Event, String>>(){
			@Override
			public TableCell call(final TableColumn<Event, String> param){
				final TableCell<Event, String> cell=new TableCell<Event, String>(){
					@Override
					public void updateItem(String item, boolean empty){
						super.updateItem(item, empty);
						if(empty){
							setGraphic(null);
							setText(null);
						}
						else{
							Event event=getTableView().getItems().get(getIndex());
							EventState state=event.getState();
							Text stateText=new Text(state.toString());
							if(state==EventState.ACTIVE){
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

		Callback<TableColumn<Event, String>, TableCell<Event, String>> eventSourceCellFactory=new Callback<TableColumn<Event, String>, TableCell<Event, String>>(){
			@Override
			public TableCell call(final TableColumn<Event, String> param){
				final TableCell<Event, String> cell=new TableCell<Event, String>(){
					@Override
					public void updateItem(String item, boolean empty){
						super.updateItem(item, empty);
						if(empty){
							setGraphic(null);
							setText(null);
						}
						else{
							Event event=getTableView().getItems().get(getIndex());

							Sensor sensor=app.getDatabase().getSensorBySensorId(event.getSourceEdgeSensorId());
							Attribute attribute=app.getDatabase().getAttributeByAttributeId(event.getSourceEdgeAttributeId());
							Hyperlink attributeLink=new Hyperlink(attribute.getName());
							attributeLink.setOnAction(linkEvent -> {
								try{
									Region veil=new Region();
									veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");

									FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/dialog_event_source.fxml"));
									AnchorPane eventSourceContent=(AnchorPane)loader.load();

									Label titelLabel=(Label)eventSourceContent.lookup("#titelLabel");
									titelLabel.setText("Source for Event '"+event.getName()+"'");
									Label sensorLabel=(Label)eventSourceContent.lookup("#sensorLabel");
									sensorLabel.setText(sensor.getName());
									Label attributeLabel=(Label)eventSourceContent.lookup("#attributeLabel");
									attributeLabel.setText(attribute.getName());
									Label averageLabel=(Label)eventSourceContent.lookup("#averageLabel");
									averageLabel.setText(event.getAverage().toString());
									Label conditionLabel=(Label)eventSourceContent.lookup("#conditionLabel");
									conditionLabel.setText(event.getCondition().toString());
									Label valueLabel=(Label)eventSourceContent.lookup("#valueLabel");
									valueLabel.setText(event.getConditionValue());

									Button closeButton=(Button)eventSourceContent.lookup("#closeButton");
									closeButton.setOnAction(closeEvent -> {
										app.closeDialog(veil, eventSourceContent, eventsPane);
									});

									app.showDialog(veil, eventSourceContent, eventsPane);
								}
								catch(IOException ioe){}
							});

							setGraphic(attributeLink);
							setText(null);
						}
					}
				};
				return cell;
			}
		};

		Callback<TableColumn<Event, String>, TableCell<Event, String>> eventActionCellFactory=new Callback<TableColumn<Event, String>, TableCell<Event, String>>(){
			@Override
			public TableCell call(final TableColumn<Event, String> param){
				final TableCell<Event, String> cell=new TableCell<Event, String>(){
					@Override
					public void updateItem(String item, boolean empty){
						super.updateItem(item, empty);
						if(empty){
							setGraphic(null);
							setText(null);
						}
						else{
							Event event=getTableView().getItems().get(getIndex());

							Sensor sensor=app.getDatabase().getSensorBySensorId(event.getActionEdgeSensorId());
							Attribute attribute=app.getDatabase().getAttributeByAttributeId(event.getActionEdgeAttributeId());
							Hyperlink attributeLink=new Hyperlink(attribute.getName());
							attributeLink.setOnAction(linkEvent -> {
								try{
									Region veil=new Region();
									veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");

									FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/dialog_event_action.fxml"));
									AnchorPane eventActionContent=(AnchorPane)loader.load();

									Label titelLabel=(Label)eventActionContent.lookup("#titelLabel");
									titelLabel.setText("Action for Event '"+event.getName()+"'");
									Label sensorLabel=(Label)eventActionContent.lookup("#sensorLabel");
									sensorLabel.setText(sensor.getName());
									Label attributeLabel=(Label)eventActionContent.lookup("#attributeLabel");
									attributeLabel.setText(attribute.getName());
									Label valueLabel=(Label)eventActionContent.lookup("#valueLabel");
									valueLabel.setText(event.getTriggerValue());

									Button closeButton=(Button)eventActionContent.lookup("#closeButton");
									closeButton.setOnAction(closeEvent -> {
										app.closeDialog(veil, eventActionContent, eventsPane);
									});

									app.showDialog(veil, eventActionContent, eventsPane);
								}
								catch(IOException ioe){}
							});

							setGraphic(attributeLink);
							setText(null);
						}
					}
				};
				return cell;
			}
		};

		Callback<TableColumn<Event, String>, TableCell<Event, String>> actionsCellFactory=new Callback<TableColumn<Event, String>, TableCell<Event, String>>(){
			@Override
			public TableCell call(final TableColumn<Event, String> param){
				final TableCell<Event, String> cell=new TableCell<Event, String>(){
					final ToggleButton stateButton=new ToggleButton();
					final Button deleteButton=new Button();

					@Override
					public void updateItem(String item, boolean empty){
						super.updateItem(item, empty);

						stateButton.setStyle("-fx-background-color: transparent;");
						stateButton.setPadding(new Insets(0, 0, 0, 0));
						stateButton.setCursor(Cursor.HAND);
						deleteButton.getStyleClass().add("simple_button");
						deleteButton.setCursor(Cursor.HAND);

						if(empty){
							setGraphic(null);
							setText(null);
						}
						else{
							Event event=getTableView().getItems().get(getIndex());
							Attribute sourceAttribute=app.getDatabase().getAttributeByAttributeId(event.getSourceEdgeAttributeId());
							Attribute actionAttribute=app.getDatabase().getAttributeByAttributeId(event.getActionEdgeAttributeId());

							if(event.getState()==EventState.ACTIVE){
								setToggleButtonState(stateButton, true);
							}
							else{
								setToggleButtonState(stateButton, false);
							}
							stateButton.setOnAction(stateEvent -> {
								boolean canContinue=false;

								if(sourceAttribute.getState()==AttributeState.DEACTIVATED && actionAttribute.getState()==AttributeState.DEACTIVATED){
									app.showMessageStrip(NotificationType.WARNING, "The source and action attributes for this event must be activated.", eventsPane);
								}
								else if(sourceAttribute.getState()==AttributeState.DEACTIVATED){
									app.showMessageStrip(NotificationType.WARNING, "The source attribute for this event must be activated.", eventsPane);
								}
								else if(actionAttribute.getState()==AttributeState.DEACTIVATED){
									app.showMessageStrip(NotificationType.WARNING, "The action attribute for this event must be activated.", eventsPane);
								}
								else{
									canContinue=true;
								}

								if(canContinue){
									Task stateChangeTask=new Task<Void>(){
										@Override
										public Void call(){
											boolean isSelected=stateButton.isSelected();
											if(isSelected){
												event.setState(EventState.ACTIVE);
											}
											else{
												event.setState(EventState.DEACTIVATED);
											}
											boolean updateResult=app.getDatabase().updateEvent(event);
											if(updateResult){
												localEventListTableView.getItems().set(getIndex(), event);
												app.getAttributeManager().updateEventState(event.getSourceEdgeAttributeId(), event.getAverage());
												if(event.getState()==EventState.ACTIVE){
													Notification notification=new Notification(0, NotificationType.SUCCESS, NotificationCategory.EVENT, event.getGlobalEventId(), "Event '"+event.getName()+" ("+event.getId()+")' has been activated.", CustomUtil.getCurrentDateTime());
													app.showAndSaveNotification(notification, eventsPane);
												}
												else{
													Notification notification=new Notification(0, NotificationType.SUCCESS, NotificationCategory.EVENT, event.getGlobalEventId(), "Event '"+event.getName()+" ("+event.getId()+")' has been deactivated.", CustomUtil.getCurrentDateTime());
													app.showAndSaveNotification(notification, eventsPane);
												}

												JsonObject jsonObject=new JsonObject();
												jsonObject.addProperty("command", "changeEventState");
												jsonObject.addProperty("globalEventId", event.getGlobalEventId());
												jsonObject.addProperty("state", isSelected);
												app.getElastosCarrier().sendDataToOnlineControllers(jsonObject);
											}
											else{
												Notification notification=new Notification(0, NotificationType.ERROR, NotificationCategory.EVENT, event.getGlobalEventId(), "Sorry, something went wrong changing the state of event '"+event.getName()+" ("+event.getId()+")'.", CustomUtil.getCurrentDateTime());
												app.showAndSaveNotification(notification, eventsPane);
											}
											return null;
										}
									};
									app.executeAsyncTask(stateChangeTask, eventsPane);
								}
								else{
									setToggleButtonState(stateButton, false);
								}
							});

							ImageView deleteImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_delete_white_24.png")));
							deleteImageView.setFitWidth(18);
							deleteImageView.setFitHeight(18);
							deleteButton.setGraphic(deleteImageView);
							deleteButton.setOnAction(deleteEvent -> {
								try{
									Region veil=new Region();
									veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");

									FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/dialog_delete_event.fxml"));
									AnchorPane deleteDialogContent=(AnchorPane)loader.load();

									AnchorPane titelPane=(AnchorPane)deleteDialogContent.lookup("#titelPane");
									Text dialogText=(Text)deleteDialogContent.lookup("#dialogText");
									HBox actionsHbox=(HBox)deleteDialogContent.lookup("#actionsHbox");
									double dialogHeight=dialogText.getBoundsInLocal().getHeight()+titelPane.getPrefHeight()+actionsHbox.getPrefHeight()+30;
									deleteDialogContent.setPrefHeight(dialogHeight);

									Button cancelButton=(Button)deleteDialogContent.lookup("#cancelButton");
									cancelButton.setOnAction(cancelEvent -> {
										app.closeDialog(veil, deleteDialogContent, eventsPane);
									});
									Button confirmButton=(Button)deleteDialogContent.lookup("#confirmButton");
									confirmButton.setOnAction(confirmEvent -> {
										Task deleteTask=new Task<Void>(){
											@Override
											public Void call(){
												app.getDatabase().deleteEventByEventId(event.getId());
												localEventObservableList.removeAll(event);
												JsonObject jsonObject=new JsonObject();
												jsonObject.addProperty("command", "deleteEvent");
												jsonObject.addProperty("globalEventId", event.getGlobalEventId());
												app.getElastosCarrier().sendDataToControllers(jsonObject);
												Notification notification=new Notification(0, NotificationType.SUCCESS, NotificationCategory.EVENT, event.getGlobalEventId(), "Event '"+event.getName()+" ("+event.getId()+")' has been deleted.", CustomUtil.getCurrentDateTime());
												app.showAndSaveNotification(notification, eventsPane);
												app.getAttributeManager().updateEventState(event.getSourceEdgeAttributeId(), event.getAverage());
												return null;
											}
										};
										app.executeAsyncTask(deleteTask, eventsPane);
										app.closeDialog(veil, deleteDialogContent, eventsPane);
									});

									app.showDialog(veil, deleteDialogContent, eventsPane);
								}
								catch(IOException ioe){}
							});

							HBox hbox=new HBox(stateButton, deleteButton);
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

		localSourceColumn.setCellFactory(eventSourceCellFactory);
		localActionColumn.setCellFactory(eventActionCellFactory);
		localStateColumn.setCellFactory(stateCellFactory);
		localActionsColumn.setCellFactory(actionsCellFactory);
		localEventListTableView.setItems(localEventObservableList);
	}

	private void initGlobalEventList(){
		ArrayList<Event> eventList=app.getDatabase().getEventListByType(EventType.GLOBAL);
		globalEventObservableList=FXCollections.observableArrayList(eventList);
		globalIdColumn.setCellValueFactory(new PropertyValueFactory("id"));
		globalNameColumn.setCellValueFactory(new PropertyValueFactory("name"));

		Callback<TableColumn<Event, String>, TableCell<Event, String>> stateCellFactory=new Callback<TableColumn<Event, String>, TableCell<Event, String>>(){
			@Override
			public TableCell call(final TableColumn<Event, String> param){
				final TableCell<Event, String> cell=new TableCell<Event, String>(){
					@Override
					public void updateItem(String item, boolean empty){
						super.updateItem(item, empty);
						if(empty){
							setGraphic(null);
							setText(null);
						}
						else{
							Event event=getTableView().getItems().get(getIndex());
							EventState state=event.getState();
							Text stateText=new Text(state.toString());
							if(state==EventState.ACTIVE){
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

		Callback<TableColumn<Event, String>, TableCell<Event, String>> eventSourceCellFactory=new Callback<TableColumn<Event, String>, TableCell<Event, String>>(){
			@Override
			public TableCell call(final TableColumn<Event, String> param){
				final TableCell<Event, String> cell=new TableCell<Event, String>(){
					@Override
					public void updateItem(String item, boolean empty){
						super.updateItem(item, empty);
						if(empty){
							setGraphic(null);
							setText(null);
						}
						else{
							Event event=getTableView().getItems().get(getIndex());

							if(event.getEdgeType()==EventEdgeType.SOURCE){
								Sensor sensor=app.getDatabase().getSensorBySensorId(event.getSourceEdgeSensorId());
								Attribute attribute=app.getDatabase().getAttributeByAttributeId(event.getSourceEdgeAttributeId());
								Hyperlink attributeLink=new Hyperlink(attribute.getName());
								attributeLink.setOnAction(linkEvent -> {
									try{
										Region veil=new Region();
										veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");

										FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/dialog_event_source.fxml"));
										AnchorPane eventSourceContent=(AnchorPane)loader.load();

										Label titelLabel=(Label)eventSourceContent.lookup("#titelLabel");
										titelLabel.setText("Source for Event '"+event.getName()+"'");
										Label sensorLabel=(Label)eventSourceContent.lookup("#sensorLabel");
										sensorLabel.setText(sensor.getName());
										Label attributeLabel=(Label)eventSourceContent.lookup("#attributeLabel");
										attributeLabel.setText(attribute.getName());
										Label averageLabel=(Label)eventSourceContent.lookup("#averageLabel");
										averageLabel.setText(event.getAverage().toString());
										Label conditionLabel=(Label)eventSourceContent.lookup("#conditionLabel");
										conditionLabel.setText(event.getCondition().toString());
										Label valueLabel=(Label)eventSourceContent.lookup("#valueLabel");
										valueLabel.setText(event.getConditionValue());

										Button closeButton=(Button)eventSourceContent.lookup("#closeButton");
										closeButton.setOnAction(closeEvent -> {
											app.closeDialog(veil, eventSourceContent, eventsPane);
										});

										app.showDialog(veil, eventSourceContent, eventsPane);
									}
									catch(IOException ioe){}
								});
								setGraphic(attributeLink);
							}
							else{
								Text text=new Text("Remote Device Source");
								setGraphic(text);
							}
							setText(null);
						}
					}
				};
				return cell;
			}
		};

		Callback<TableColumn<Event, String>, TableCell<Event, String>> eventActionCellFactory=new Callback<TableColumn<Event, String>, TableCell<Event, String>>(){
			@Override
			public TableCell call(final TableColumn<Event, String> param){
				final TableCell<Event, String> cell=new TableCell<Event, String>(){
					@Override
					public void updateItem(String item, boolean empty){
						super.updateItem(item, empty);
						if(empty){
							setGraphic(null);
							setText(null);
						}
						else{
							Event event=getTableView().getItems().get(getIndex());

							if(event.getEdgeType()==EventEdgeType.ACTION){
								Sensor sensor=app.getDatabase().getSensorBySensorId(event.getActionEdgeSensorId());
								Attribute attribute=app.getDatabase().getAttributeByAttributeId(event.getActionEdgeAttributeId());
								Hyperlink attributeLink=new Hyperlink(attribute.getName());
								attributeLink.setOnAction(linkEvent -> {
									try{
										Region veil=new Region();
										veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");

										FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/dialog_event_action.fxml"));
										AnchorPane eventActionContent=(AnchorPane)loader.load();

										Label titelLabel=(Label)eventActionContent.lookup("#titelLabel");
										titelLabel.setText("Action for Event '"+event.getName()+"'");
										Label sensorLabel=(Label)eventActionContent.lookup("#sensorLabel");
										sensorLabel.setText(sensor.getName());
										Label attributeLabel=(Label)eventActionContent.lookup("#attributeLabel");
										attributeLabel.setText(attribute.getName());
										Label valueLabel=(Label)eventActionContent.lookup("#valueLabel");
										valueLabel.setText(event.getTriggerValue());

										Button closeButton=(Button)eventActionContent.lookup("#closeButton");
										closeButton.setOnAction(closeEvent -> {
											app.closeDialog(veil, eventActionContent, eventsPane);
										});

										app.showDialog(veil, eventActionContent, eventsPane);
									}
									catch(IOException ioe){}
								});

								setGraphic(attributeLink);
							}
							else{
								Text text=new Text("Remote Device Action");
								setGraphic(text);
							}
							setText(null);
						}
					}
				};
				return cell;
			}
		};

		Callback<TableColumn<Event, String>, TableCell<Event, String>> actionsCellFactory=new Callback<TableColumn<Event, String>, TableCell<Event, String>>(){
			@Override
			public TableCell call(final TableColumn<Event, String> param){
				final TableCell<Event, String> cell=new TableCell<Event, String>(){
					final ToggleButton stateButton=new ToggleButton();
					final Button deleteButton=new Button();

					@Override
					public void updateItem(String item, boolean empty){
						super.updateItem(item, empty);

						stateButton.setStyle("-fx-background-color: transparent;");
						stateButton.setPadding(new Insets(0, 0, 0, 0));
						stateButton.setCursor(Cursor.HAND);
						deleteButton.getStyleClass().add("simple_button");
						deleteButton.setCursor(Cursor.HAND);

						if(empty){
							setGraphic(null);
							setText(null);
						}
						else{
							Event event=getTableView().getItems().get(getIndex());
							Attribute sourceAttribute=app.getDatabase().getAttributeByAttributeId(event.getSourceEdgeAttributeId());
							Attribute actionAttribute=app.getDatabase().getAttributeByAttributeId(event.getActionEdgeAttributeId());

							if(event.getState()==EventState.ACTIVE){
								setToggleButtonState(stateButton, true);
							}
							else{
								setToggleButtonState(stateButton, false);
							}
							stateButton.setOnAction(stateEvent -> {
								boolean canContinue=false;

								if(sourceAttribute.getState()==AttributeState.DEACTIVATED && actionAttribute.getState()==AttributeState.DEACTIVATED){
									app.showMessageStrip(NotificationType.WARNING, "The source and action attributes for this event must be activated.", eventsPane);
								}
								else if(sourceAttribute.getState()==AttributeState.DEACTIVATED){
									app.showMessageStrip(NotificationType.WARNING, "The source attribute for this event must be activated.", eventsPane);
								}
								else if(actionAttribute.getState()==AttributeState.DEACTIVATED){
									app.showMessageStrip(NotificationType.WARNING, "The action attribute for this event must be activated.", eventsPane);
								}
								else{
									canContinue=true;
								}

								if(canContinue){
									Task stateChangeTask=new Task<Void>(){
										@Override
										public Void call(){
											boolean isSelected=stateButton.isSelected();
											if(isSelected){
												setToggleButtonState(stateButton, false);
												app.showMessageStrip(NotificationType.WARNING, "Global Event can be activated only by a controller.", eventsPane);
											}
											else{
												event.setState(EventState.DEACTIVATED);
												boolean updateResult=app.getDatabase().updateEvent(event);
												if(updateResult){
													globalEventListTableView.getItems().set(getIndex(), event);
													app.getAttributeManager().updateEventState(event.getSourceEdgeAttributeId(), event.getAverage());
													Notification notification=new Notification(0, NotificationType.SUCCESS, NotificationCategory.EVENT, event.getGlobalEventId(), "Event '"+event.getName()+" ("+event.getId()+")' has been deactivated.", CustomUtil.getCurrentDateTime());
													app.showAndSaveNotification(notification, eventsPane);
													JsonObject jsonObject=new JsonObject();
													jsonObject.addProperty("command", "changeEventState");
													jsonObject.addProperty("globalEventId", event.getGlobalEventId());
													jsonObject.addProperty("state", false);
													app.getElastosCarrier().sendDataToOnlineControllers(jsonObject);
													if(event.getEdgeType()==EventEdgeType.SOURCE){
														jsonObject.addProperty("edgeType", EventEdgeType.ACTION.getValue());
														app.getElastosCarrier().sendDataToDevice(event.getActionDeviceUserId(), jsonObject);
													}
													else if(event.getEdgeType()==EventEdgeType.ACTION){
														jsonObject.addProperty("edgeType", EventEdgeType.SOURCE.getValue());
														app.getElastosCarrier().sendDataToDevice(event.getSourceDeviceUserId(), jsonObject);
													}
												}
												else{
													Notification notification=new Notification(0, NotificationType.ERROR, NotificationCategory.EVENT, event.getGlobalEventId(), "Sorry, something went wrong changing the state of event '"+event.getName()+" ("+event.getId()+")'.", CustomUtil.getCurrentDateTime());
													app.showAndSaveNotification(notification, eventsPane);
												}
											}
											return null;
										}
									};
									app.executeAsyncTask(stateChangeTask, eventsPane);
								}
								else{
									setToggleButtonState(stateButton, false);
								}
							});

							ImageView deleteImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_delete_white_24.png")));
							deleteImageView.setFitWidth(18);
							deleteImageView.setFitHeight(18);
							deleteButton.setGraphic(deleteImageView);
							deleteButton.setOnAction(deleteEvent -> {
								try{
									Region veil=new Region();
									veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");

									FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/dialog_delete_event.fxml"));
									AnchorPane deleteDialogContent=(AnchorPane)loader.load();

									AnchorPane titelPane=(AnchorPane)deleteDialogContent.lookup("#titelPane");
									Text dialogText=(Text)deleteDialogContent.lookup("#dialogText");
									HBox actionsHbox=(HBox)deleteDialogContent.lookup("#actionsHbox");
									double dialogHeight=dialogText.getBoundsInLocal().getHeight()+titelPane.getPrefHeight()+actionsHbox.getPrefHeight()+30;
									deleteDialogContent.setPrefHeight(dialogHeight);

									Button cancelButton=(Button)deleteDialogContent.lookup("#cancelButton");
									cancelButton.setOnAction(cancelEvent -> {
										app.closeDialog(veil, deleteDialogContent, eventsPane);
									});
									Button confirmButton=(Button)deleteDialogContent.lookup("#confirmButton");
									confirmButton.setOnAction(confirmEvent -> {
										Task deleteTask=new Task<Void>(){
											@Override
											public Void call(){
												app.getDatabase().deleteEventByEventId(event.getId());
												globalEventObservableList.removeAll(event);
												JsonObject jsonObject=new JsonObject();
												jsonObject.addProperty("command", "deleteEvent");
												jsonObject.addProperty("globalEventId", event.getGlobalEventId());
												app.getElastosCarrier().sendDataToControllers(jsonObject);
												Notification notification=new Notification(0, NotificationType.SUCCESS, NotificationCategory.EVENT, event.getGlobalEventId(), "Event '"+event.getName()+" ("+event.getId()+")' has been deleted.", CustomUtil.getCurrentDateTime());
												app.showAndSaveNotification(notification, eventsPane);
												if(event.getEdgeType()==EventEdgeType.SOURCE){
													app.getAttributeManager().updateEventState(event.getSourceEdgeAttributeId(), event.getAverage());
												}
												return null;
											}
										};
										app.executeAsyncTask(deleteTask, eventsPane);
										app.closeDialog(veil, deleteDialogContent, eventsPane);
									});

									app.showDialog(veil, deleteDialogContent, eventsPane);
								}
								catch(IOException ioe){}
							});

							HBox hbox=new HBox(stateButton, deleteButton);
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

		globalSourceColumn.setCellFactory(eventSourceCellFactory);
		globalActionColumn.setCellFactory(eventActionCellFactory);
		globalStateColumn.setCellFactory(stateCellFactory);
		globalActionsColumn.setCellFactory(actionsCellFactory);
		globalEventListTableView.setItems(globalEventObservableList);
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
	
	@FXML
	private void onAddEvent(){
		this.app.setAddEventLayout();
	}
}