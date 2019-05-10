package com.hyper.connect.controller;

import com.hyper.connect.App;
import com.hyper.connect.model.Notification;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.util.Callback;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.ChoiceBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.StackPane;
import javafx.concurrent.Task;
import javafx.application.Platform;

import java.util.ArrayList;


public class NotificationsController{
	private App app;
	private ObservableList<Notification> notificationObservableList;
	private FilteredList<Notification> filteredList;
	
	@FXML private StackPane notificationsPane;
	@FXML private TableView notificationListTableView;
	@FXML private TableColumn idColumn;
	@FXML private TableColumn typeColumn;
	@FXML private TableColumn categoryColumn;
	@FXML private TableColumn dateTimeColumn;
	@FXML private TableColumn messageColumn;
	@FXML private ChoiceBox typeChoiceBox;
	@FXML private ChoiceBox categoryChoiceBox;
	
	public NotificationsController(){}
	
	public void setApp(App app){
		this.app=app;
	}
	
	public void init(){
		this.initTypeChoiceBox();
		this.initCategoryChoiceBox();
		
		Task initTask=new Task<Void>(){
			@Override
			public Void call(){
				ArrayList<Notification> notificationList=app.getDatabase().getNotificationList();
				notificationObservableList=FXCollections.observableArrayList(notificationList);
				filteredList=new FilteredList<Notification>(notificationObservableList, notification -> true);
				idColumn.setCellValueFactory(new PropertyValueFactory("id"));
				
				Callback<TableColumn<Notification, String>, TableCell<Notification, String>> typeCellFactory=new Callback<TableColumn<Notification, String>, TableCell<Notification, String>>(){
					@Override
					public TableCell call(final TableColumn<Notification, String> param){
						final TableCell<Notification, String> cell=new TableCell<Notification, String>(){
							@Override
							public void updateItem(String item, boolean empty){
								super.updateItem(item, empty);
								if(empty){
									setGraphic(null);
									setText(null);
								}
								else{
									Notification notification=getTableView().getItems().get(getIndex());
									String type=notification.getType();
									ImageView typeImageView=new ImageView();
									typeImageView.setFitWidth(18);
									typeImageView.setFitHeight(18);
									if(type.equals("Success")){
										Image doneImage=new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_done_green_24.png"));
										typeImageView.setImage(doneImage);
									}
									else if(type.equals("Warning")){
										Image warningImage=new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_warning_orange_24.png"));
										typeImageView.setImage(warningImage);
									}
									else if(type.equals("Error")){
										Image errorImage=new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_error_red_24.png"));
										typeImageView.setImage(errorImage);
									}

									setGraphic(typeImageView);
									setText(null);
								}
							}
						};
						return cell;
					}
				};
				
				typeColumn.setCellFactory(typeCellFactory);
				categoryColumn.setCellValueFactory(new PropertyValueFactory("category"));
				dateTimeColumn.setCellValueFactory(new PropertyValueFactory("dateTime"));
				messageColumn.setCellValueFactory(new PropertyValueFactory("message"));
				notificationListTableView.setItems(filteredList);
				
				return null;
			}
		};
		this.app.executeAsyncTask(initTask, notificationsPane);
	}
	
	private void initTypeChoiceBox(){
		this.typeChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(typeListener);
		this.typeChoiceBox.getItems().clear();
		this.typeChoiceBox.getItems().addAll("All", "Success", "Warning", "Error");
		this.typeChoiceBox.getSelectionModel().selectFirst();
		this.typeChoiceBox.getSelectionModel().selectedIndexProperty().addListener(typeListener);
	}
	
	private final ChangeListener<Number> typeListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			String type=(String)typeChoiceBox.getItems().get((Integer)number2);
			String category=(String)categoryChoiceBox.getSelectionModel().getSelectedItem();
			
			if(type.equals("All")){
				filteredList.setPredicate(notification -> {
					if(category.equals("All")){
						return true;
					}
					else{
						boolean matched=false;
						if(notification.getCategory().equals(category)){
							matched=true;
						}
						return matched;
					}
				});
			}
			else{
				if(category.equals("All")){
					filteredList.setPredicate(notification -> {
						boolean matched=false;
						if(notification.getType().equals(type)){
							matched=true;
						}
						return matched;
					});
				}
				else{
					filteredList.setPredicate(notification -> {
						boolean matched=false;
						if(notification.getType().equals(type) && notification.getCategory().equals(category)){
							matched=true;
						}
						return matched;
					});
				}
			}
		}
	};
	
	private void initCategoryChoiceBox(){
		this.categoryChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(categoryListener);
		this.categoryChoiceBox.getItems().clear();
		this.categoryChoiceBox.getItems().addAll("All", "Device", "Event", "System");
		this.categoryChoiceBox.getSelectionModel().selectFirst();
		this.categoryChoiceBox.getSelectionModel().selectedIndexProperty().addListener(categoryListener);
	}
	
	private final ChangeListener<Number> categoryListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			String category=(String)categoryChoiceBox.getItems().get((Integer)number2);
			String type=(String)typeChoiceBox.getSelectionModel().getSelectedItem();
			
			if(category.equals("All")){
				filteredList.setPredicate(notification -> {
					if(type.equals("All")){
						return true;
					}
					else{
						boolean matched=false;
						if(notification.getType().equals(type)){
							matched=true;
						}
						return matched;
					}
				});
			}
			else{
				if(type.equals("All")){
					filteredList.setPredicate(notification -> {
						boolean matched=false;
						if(notification.getCategory().equals(category)){
							matched=true;
						}
						return matched;
					});
				}
				else{
					filteredList.setPredicate(notification -> {
						boolean matched=false;
						if(notification.getType().equals(type) && notification.getCategory().equals(category)){
							matched=true;
						}
						return matched;
					});
				}
			}
		}
	};
}