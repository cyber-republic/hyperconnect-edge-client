package com.hyper.connect.controller;

import com.hyper.connect.App;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;

import java.util.ArrayList;


public class RootController{
	private App app;
	private Button selectedButton;
	
	@FXML private BorderPane mainPane;
	@FXML private Button dashboardButton;
	@FXML private Button sensorsButton;
	@FXML private Button eventsButton;
	@FXML private Button notificationsButton;
	@FXML private Button controllersButton;
	@FXML private Button settingsButton;
	@FXML private HBox breadCrumbHBox;
	
	public RootController(){}

	@FXML
	public void initialize(){
		this.selectedButton=dashboardButton;
		this.selectedButton.getStyleClass().add("custom_button_selected");
	}
	
	public void setApp(App app){
		this.app=app;
	}
	
	private void setSelectedButton(Button newSelectedButton){
		this.selectedButton.getStyleClass().remove("custom_button_selected");
		this.selectedButton=newSelectedButton;
		this.selectedButton.getStyleClass().add("custom_button_selected");
	}
	
	public void setBreadcrumb(ArrayList<Node> nodeList){
		this.breadCrumbHBox.getChildren().clear();
		this.breadCrumbHBox.getChildren().addAll(nodeList);
	}
	
	@FXML
    private void onButtonDashboard(){
		this.app.setDashboardLayout();
		this.setSelectedButton(this.dashboardButton);
	}
	
	@FXML
    private void onButtonSensors(){
		this.app.setSensorsLayout();
		this.setSelectedButton(this.sensorsButton);
	}
	
	@FXML
    private void onButtonEvents(){
		this.app.setEventsLayout();
		this.setSelectedButton(this.eventsButton);
	}
	
	@FXML
    private void onButtonNotifications(){
		this.app.setNotificationsLayout();
		this.setSelectedButton(this.notificationsButton);
	}
	
	@FXML
    private void onButtonControllers(){
		this.app.setControllersLayout();
		this.setSelectedButton(this.controllersButton);
	}
	
	@FXML
    private void onButtonSettings(){
		this.app.setSettingsLayout();
		this.setSelectedButton(this.settingsButton);
	}
	
	@FXML
    private void onButtonHelp(){
		this.app.setHelpLayout();
	}
	
}