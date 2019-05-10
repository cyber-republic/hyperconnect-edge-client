package com.hyper.connect.controller;

import com.hyper.connect.App;
import com.hyper.connect.model.Sensor;
import com.hyper.connect.model.Attribute;
import com.hyper.connect.model.Event;

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
	private ObservableList<Event> eventObservableList;
	
	@FXML private StackPane eventsPane;
	@FXML private TableView eventListTableView;
	@FXML private TableColumn idColumn;
	@FXML private TableColumn nameColumn;
	@FXML private TableColumn sourceColumn;
	@FXML private TableColumn actionColumn;
	@FXML private TableColumn stateColumn;
	@FXML private TableColumn actionsColumn;
	
	public EventsController(){}
	
	public void setApp(App app){
		this.app=app;
	}
	
	public void init(){
		Task initTask=new Task<Void>(){
			@Override
			public Void call(){
				ArrayList<Event> eventList=app.getDatabase().getEventList();
				eventObservableList=FXCollections.observableArrayList(eventList);
				idColumn.setCellValueFactory(new PropertyValueFactory("id"));
				nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
				
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
									String state=event.getState();
									Text stateText=new Text(state);
									if(state.equals("active")){
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
									String state=event.getState();
									
									Sensor sensor=app.getDatabase().getSensorBySensorId(event.getSourceSensorId());
									Attribute attribute=app.getDatabase().getAttributeByAttributeId(event.getSourceAttributeId());
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
											averageLabel.setText(getAverageLabel(event.getAverage()));
											Label conditionLabel=(Label)eventSourceContent.lookup("#conditionLabel");
											conditionLabel.setText(event.getCondition());
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
									String state=event.getState();
									
									Sensor sensor=app.getDatabase().getSensorBySensorId(event.getActionSensorId());
									Attribute attribute=app.getDatabase().getAttributeByAttributeId(event.getActionAttributeId());
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
									Attribute sourceAttribute=app.getDatabase().getAttributeByAttributeId(event.getSourceAttributeId());
									Attribute actionAttribute=app.getDatabase().getAttributeByAttributeId(event.getActionAttributeId());
									
									if(event.getState().equals("active")){
										setToggleButtonState(stateButton, true);
									}
									else{
										setToggleButtonState(stateButton, false);
									}
									stateButton.setOnAction(stateEvent -> {
										boolean canContinue=false;
									
										if(sourceAttribute.getState().equals("deactivated") && actionAttribute.getState().equals("deactivated")){
											app.showMessageStrip("Warning", "The source and action attributes for this event must be activated.", eventsPane);
										}
										else if(sourceAttribute.getState().equals("deactivated")){
											app.showMessageStrip("Warning", "The source attribute for this event must be activated.", eventsPane);
										}
										else if(actionAttribute.getState().equals("deactivated")){
											app.showMessageStrip("Warning", "The action attribute for this event must be activated.", eventsPane);
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
														event.setState("active");
													}
													else{
														event.setState("deactivated");
													}
													boolean updateResult=app.getDatabase().updateEvent(event);
													if(updateResult){
														eventListTableView.getItems().set(getIndex(), event);
														app.getAttributeManager().updateEventState(event.getSourceAttributeId(), event.getAverage());
														if(event.getState().equals("active")){
															app.showMessageStripAndSave("Success", "Event", "Event '"+event.getName()+" ("+event.getId()+")' has been activated.", eventsPane);
														}
														else{
															app.showMessageStripAndSave("Success", "Event", "Event '"+event.getName()+" ("+event.getId()+")' has been deactivated.", eventsPane);
														}
													}
													else{
														app.showMessageStripAndSave("Error", "Event", "Sorry, something went wrong changing the state of event '"+event.getName()+" ("+event.getId()+")'.", eventsPane);
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
														eventObservableList.removeAll(event);
														app.showMessageStripAndSave("Success", "Event", "Event '"+event.getName()+" ("+event.getId()+")' has been deleted.", eventsPane);
														//TODO: stop event if deleted
														//app.getAttributeManager().deleteAttribute(sourceAttribute.getId());
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
				
				sourceColumn.setCellFactory(eventSourceCellFactory);
				actionColumn.setCellFactory(eventActionCellFactory);
				stateColumn.setCellFactory(stateCellFactory);
				actionsColumn.setCellFactory(actionsCellFactory);
				eventListTableView.setItems(eventObservableList);
				
				return null;
			}
		};
		this.app.executeAsyncTask(initTask, eventsPane);
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
	
	private String getAverageLabel(String average){
		String averageLabel="";
		if(average.equals("real-time")){
			averageLabel="Real-Time";
		}
		else if(average.equals("1m")){
			averageLabel="1 Minute";
		}
		else if(average.equals("5m")){
			averageLabel="5 Minutes";
		}
		else if(average.equals("15m")){
			averageLabel="15 Minutes";
		}
		else if(average.equals("1h")){
			averageLabel="1 Hour";
		}
		else if(average.equals("3h")){
			averageLabel="3 Hours";
		}
		else if(average.equals("6h")){
			averageLabel="6 Hours";
		}
		else if(average.equals("1d")){
			averageLabel="1 Day";
		}
		return averageLabel;
	}
}