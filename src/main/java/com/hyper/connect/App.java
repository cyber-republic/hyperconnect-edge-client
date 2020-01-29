package com.hyper.connect;

import com.google.gson.*;
import com.hyper.connect.database.DatabaseInterface;
import com.hyper.connect.database.DatabaseSQLite;
import com.hyper.connect.controller.RootController;
import com.hyper.connect.controller.DashboardController;
import com.hyper.connect.controller.SensorsController;
import com.hyper.connect.controller.SensorConfigureController;
import com.hyper.connect.controller.SensorOverviewController;
import com.hyper.connect.controller.SensorHistoryController;
import com.hyper.connect.controller.ScriptController;
import com.hyper.connect.controller.EventsController;
import com.hyper.connect.controller.AddEventController;
import com.hyper.connect.controller.NotificationsController;
import com.hyper.connect.controller.ControllersController;
import com.hyper.connect.controller.SettingsController;
import com.hyper.connect.controller.HelpController;
import com.hyper.connect.model.*;
import com.hyper.connect.management.ScriptManagement;
import com.hyper.connect.management.AttributeManagement;
import com.hyper.connect.elastos.ElastosCarrier;
import com.hyper.connect.model.enums.*;
import com.hyper.connect.util.CustomUtil;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.concurrent.Task;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.io.*;
import java.lang.Thread;
import java.util.ArrayList;


public class App extends Application{
	public boolean DEBUG=false;
	private static ElastosCarrier elastosCarrier;
	private DatabaseInterface database;
	private ScriptManagement scriptManager;
	private AttributeManagement attributeManager;
	private String timeZone;

	private Stage primaryStage;
	private BorderPane rootScene;
	private RootController rootController;
	private StackPane dashboardScene;
	private DashboardController dashboardController;
	private StackPane sensorsScene;
	private SensorsController sensorsController;
	private StackPane sensorConfigureScene;
	private SensorConfigureController sensorConfigureController;
	private StackPane scriptScene;
	private ScriptController scriptController;
	private StackPane sensorOverviewScene;
	private SensorOverviewController sensorOverviewController;
	private StackPane sensorHistoryScene;
	private SensorHistoryController sensorHistoryController;
	private StackPane eventsScene;
	private EventsController eventsController;
	private StackPane addEventScene;
	private AddEventController addEventController;
	private StackPane notificationsScene;
	private NotificationsController notificationsController;
	private StackPane controllersScene;
	private ControllersController controllersController;
	private StackPane settingsScene;
	private SettingsController settingsController;
	private AnchorPane helpScene;
	private HelpController helpController;

	@Override
	public void start(Stage primaryStage){
		startApp();
		this.primaryStage=primaryStage;
        this.primaryStage.setTitle("HyperConnect");
        this.primaryStage.setMinWidth(680);
        this.primaryStage.setMinHeight(520);

		this.initRootLayout();
		this.initDashboardLayout();
		this.initSensorsLayout();
		this.initSensorConfigureLayout();
		this.initScriptLayout();
		this.initSensorOverviewLayout();
		this.initSensorHistoryLayout();
		this.initEventsLayout();
		this.initAddEventLayout();
		this.initNotificationsLayout();
		this.initControllersLayout();
		this.initSettingsLayout();
		this.initHelpLayout();
		this.setDashboardLayout();

		database.setDeviceListConnectionState(DeviceConnectionState.OFFLINE);
		database.setEventListState(EventState.DEACTIVATED);
	}

	@Override
	public void stop(){
		stopApp();
		Platform.exit();
	}

	public void startApp(){
		if(!DEBUG){
			elastosCarrier=new ElastosCarrier(this);
			elastosCarrier.start();
		}
		CustomUtil.getDirectoryByName("history");
		database=new DatabaseSQLite("local.db");
		scriptManager=new ScriptManagement();
		attributeManager=new AttributeManagement(elastosCarrier, database, scriptManager);

		Setting timeZoneSetting=database.getSettingByKey("timeZone");
		if(timeZoneSetting==null){
			timeZoneSetting=new Setting(0, "timeZone", "UTC");
			database.saveSetting(timeZoneSetting);
		}
		timeZone=timeZoneSetting.getValue();
	}

	public void stopApp(){
		attributeManager.stopAll();
		database.closeConnection();
		if(!DEBUG){
			elastosCarrier.kill();
		}
	}

	public Stage getPrimaryStage(){
		return this.primaryStage;
	}

	public DatabaseInterface getDatabase(){
		return database;
	}

	public ScriptManagement getScriptManager(){
		return scriptManager;
	}

	public AttributeManagement getAttributeManager(){
		return attributeManager;
	}

	public ElastosCarrier getElastosCarrier(){
		return elastosCarrier;
	}

	public String getTimeZone(){
		return timeZone;
	}

	public boolean setTimeZone(String timeZone){
		Setting timeZoneSetting=this.database.getSettingByKey("timeZone");
		timeZoneSetting.setValue(timeZone);
		boolean updateResult=this.database.updateSetting(timeZoneSetting);
		if(updateResult){
			this.timeZone=timeZone;
			return true;
		}
		else{
			return false;
		}
	}

	public void initRootLayout(){
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/root.fxml"));
			this.rootScene=(BorderPane)loader.load();
			Scene scene=new Scene(this.rootScene);
			this.primaryStage.setScene(scene);
			this.rootController=loader.getController();
			this.rootController.setApp(this);
			this.primaryStage.show();
		}
		catch(IOException ioe){}
	}

	public void initDashboardLayout(){
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/dashboard.fxml"));
			this.dashboardScene=(StackPane)loader.load();
			this.dashboardController=loader.getController();
			this.dashboardController.setApp(this);
		}
		catch(IOException ioe){}
	}

	public void initSensorsLayout(){
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/sensors.fxml"));
			this.sensorsScene=(StackPane)loader.load();
			this.sensorsController=loader.getController();
			this.sensorsController.setApp(this);
		}
		catch(IOException ioe){}
	}

	public void initSensorConfigureLayout(){
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/sensor_configure.fxml"));
			this.sensorConfigureScene=(StackPane)loader.load();
			this.sensorConfigureController=loader.getController();
			this.sensorConfigureController.setApp(this);
		}
		catch(IOException ioe){}
	}

	public void initScriptLayout(){
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/script.fxml"));
			this.scriptScene=(StackPane)loader.load();
			this.scriptController=loader.getController();
			this.scriptController.setApp(this);
		}
		catch(IOException ioe){}
	}

	public void initSensorOverviewLayout(){
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/sensor_overview.fxml"));
			this.sensorOverviewScene=(StackPane)loader.load();
			this.sensorOverviewController=loader.getController();
			this.sensorOverviewController.setApp(this);
		}
		catch(IOException ioe){}
	}

	public void initSensorHistoryLayout(){
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/sensor_history.fxml"));
			this.sensorHistoryScene=(StackPane)loader.load();
			this.sensorHistoryController=loader.getController();
			this.sensorHistoryController.setApp(this);
		}
		catch(IOException ioe){}
	}

	public void initEventsLayout(){
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/events.fxml"));
			this.eventsScene=(StackPane)loader.load();
			this.eventsController=loader.getController();
			this.eventsController.setApp(this);
		}
		catch(IOException ioe){}
	}

	public void initAddEventLayout(){
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/add_event.fxml"));
			this.addEventScene=(StackPane)loader.load();
			this.addEventController=loader.getController();
			this.addEventController.setApp(this);
		}
		catch(IOException ioe){}
	}

	public void initNotificationsLayout(){
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/notifications.fxml"));
			this.notificationsScene=(StackPane)loader.load();
			this.notificationsController=loader.getController();
			this.notificationsController.setApp(this);
		}
		catch(IOException ioe){}
	}

	public void initControllersLayout(){
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/controllers.fxml"));
			this.controllersScene=(StackPane)loader.load();
			this.controllersController=loader.getController();
			this.controllersController.setApp(this);
		}
		catch(IOException ioe){}
	}

	public void initSettingsLayout(){
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/settings.fxml"));
			this.settingsScene=(StackPane)loader.load();
			this.settingsController=loader.getController();
			this.settingsController.setApp(this);
		}
		catch(IOException ioe){}
	}

	public void initHelpLayout(){
		try{
			FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/help.fxml"));
			this.helpScene=(AnchorPane)loader.load();
			this.helpController=loader.getController();
			this.helpController.setApp(this);
		}
		catch(IOException ioe){}
	}

	public void setRootLayout(){
		Scene scene=new Scene(this.rootScene);
		Platform.runLater(() -> this.primaryStage.setScene(scene));
	}

	public void setDashboardLayout(){
		this.rootScene.setCenter(this.dashboardScene);
		this.dashboardController.init();
		this.initDashboardBreadcrumb();
	}

	public void refreshConnectionState(){
		this.dashboardController.refreshConnectionState();
	}

	public void setSensorsLayout(){
		this.rootScene.setCenter(this.sensorsScene);
		this.sensorsController.init();
		this.initSensorsBreadcrumb();
	}

	public void setSensorConfigureLayout(Sensor sensor){
		this.rootScene.setCenter(this.sensorConfigureScene);
		this.sensorConfigureController.init(sensor);
		this.initSensorConfigureBreadcrumb(sensor);
	}

	public void setScriptLayout(Sensor sensor, Attribute attribute){
		this.rootScene.setCenter(this.scriptScene);
		this.scriptController.init(sensor, attribute);
		this.initScriptBreadcrumb(sensor, attribute);
	}

	public void setSensorOverviewLayout(Sensor sensor){
		this.rootScene.setCenter(this.sensorOverviewScene);
		this.sensorOverviewController.init(sensor);
		this.initSensorOverviewBreadcrumb(sensor);
	}

	public void setSensorHistoryLayout(Sensor sensor){
		this.rootScene.setCenter(this.sensorHistoryScene);
		this.sensorHistoryController.init(sensor);
		this.initSensorHistoryBreadcrumb(sensor);
	}

	public void setEventsLayout(){
		this.rootScene.setCenter(this.eventsScene);
		this.eventsController.init();
		this.initEventsBreadcrumb();
	}

	public void setAddEventLayout(){
		this.rootScene.setCenter(this.addEventScene);
		this.addEventController.init();
		this.initAddEventBreadcrumb();
	}

	public void setNotificationsLayout(){
		this.rootScene.setCenter(this.notificationsScene);
		this.notificationsController.init();
		this.initNotificationsBreadcrumb();
	}

	public void setControllersLayout(){
		this.rootScene.setCenter(this.controllersScene);
		if(!DEBUG){
			this.controllersController.init(elastosCarrier.getAddress(), elastosCarrier.getUserId());
		}
		else{
			this.controllersController.init("address", "userId");
		}
		this.initControllersBreadcrumb();
	}

	public void refreshControllers(){
		this.controllersController.refreshControllerList();
	}

	public void setSettingsLayout(){
		this.rootScene.setCenter(this.settingsScene);
		this.settingsController.init();
		this.initSettingsBreadcrumb();
	}

	public void setHelpLayout(){
		this.rootScene.setCenter(this.helpScene);
	}

	private void initDashboardBreadcrumb(){
		ImageView dashboardImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_dashboard_white_24.png")));
		dashboardImageView.setFitWidth(18);
		dashboardImageView.setFitHeight(18);
		Hyperlink dashboardLink=new Hyperlink("Dashboard");
		dashboardLink.getStyleClass().add("breadcrumb_link");
		dashboardLink.setOnAction(linkEvent -> {
			setDashboardLayout();
		});
		HBox dashboardHbox=new HBox(dashboardImageView, dashboardLink);
		dashboardHbox.setAlignment(Pos.CENTER_LEFT);

		ArrayList<Node> nodeList=new ArrayList<Node>();
		nodeList.add(dashboardHbox);
		this.rootController.setBreadcrumb(nodeList);
	}

	private void initSensorsBreadcrumb(){
		ImageView sensorsImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_developer_board_white_24.png")));
		sensorsImageView.setFitWidth(18);
		sensorsImageView.setFitHeight(18);
		Hyperlink sensorsLink=new Hyperlink("Sensors");
		sensorsLink.getStyleClass().add("breadcrumb_link");
		sensorsLink.setOnAction(linkEvent -> {
			setSensorsLayout();
		});
		HBox sensorsHbox=new HBox(sensorsImageView, sensorsLink);
		sensorsHbox.setAlignment(Pos.CENTER_LEFT);

		ArrayList<Node> nodeList=new ArrayList<Node>();
		nodeList.add(sensorsHbox);
		this.rootController.setBreadcrumb(nodeList);
	}

	private void initSensorConfigureBreadcrumb(Sensor sensor){
		ImageView sensorsImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_developer_board_white_24.png")));
		sensorsImageView.setFitWidth(18);
		sensorsImageView.setFitHeight(18);
		Hyperlink sensorsLink=new Hyperlink("Sensors");
		sensorsLink.getStyleClass().add("breadcrumb_link");
		sensorsLink.setOnAction(linkEvent -> {
			setSensorsLayout();
		});
		HBox sensorsHbox=new HBox(sensorsImageView, sensorsLink);
		sensorsHbox.setAlignment(Pos.CENTER_LEFT);

		Text speratorText=new Text("-");
		speratorText.setFill(Color.WHITE);
		speratorText.setStyle("-fx-font-weight: bold");

		ImageView configureImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_build_white_24.png")));
		configureImageView.setFitWidth(18);
		configureImageView.setFitHeight(18);
		Hyperlink configureLink=new Hyperlink("Configure of Sensor '"+sensor.getName()+" ("+sensor.getId()+")'");
		configureLink.getStyleClass().add("breadcrumb_link");
		configureLink.setOnAction(linkEvent -> {
			setSensorConfigureLayout(sensor);
		});
		HBox configureHbox=new HBox(configureImageView, configureLink);
		configureHbox.setAlignment(Pos.CENTER_LEFT);

		ArrayList<Node> nodeList=new ArrayList<Node>();
		nodeList.add(sensorsHbox);
		nodeList.add(speratorText);
		nodeList.add(configureHbox);
		this.rootController.setBreadcrumb(nodeList);
	}

	private void initScriptBreadcrumb(Sensor sensor, Attribute attribute){
		ImageView sensorsImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_developer_board_white_24.png")));
		sensorsImageView.setFitWidth(18);
		sensorsImageView.setFitHeight(18);
		Hyperlink sensorsLink=new Hyperlink("Sensors");
		sensorsLink.getStyleClass().add("breadcrumb_link");
		sensorsLink.setOnAction(linkEvent -> {
			setSensorsLayout();
		});
		HBox sensorsHbox=new HBox(sensorsImageView, sensorsLink);
		sensorsHbox.setAlignment(Pos.CENTER_LEFT);

		Text speratorText=new Text("-");
		speratorText.setFill(Color.WHITE);
		speratorText.setStyle("-fx-font-weight: bold");

		ImageView configureImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_build_white_24.png")));
		configureImageView.setFitWidth(18);
		configureImageView.setFitHeight(18);
		Hyperlink configureLink=new Hyperlink("Configure of Sensor '"+sensor.getName()+" ("+sensor.getId()+")'");
		configureLink.getStyleClass().add("breadcrumb_link");
		configureLink.setOnAction(linkEvent -> {
			setSensorConfigureLayout(sensor);
		});
		HBox configureHbox=new HBox(configureImageView, configureLink);
		configureHbox.setAlignment(Pos.CENTER_LEFT);

		Text speratorText2=new Text("-");
		speratorText2.setFill(Color.WHITE);
		speratorText2.setStyle("-fx-font-weight: bold");

		ImageView scriptImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_code_white_24.png")));
		scriptImageView.setFitWidth(18);
		scriptImageView.setFitHeight(18);
		Hyperlink scriptLink=new Hyperlink("Script of Attribute '"+attribute.getName()+" ("+attribute.getId()+")'");
		scriptLink.getStyleClass().add("breadcrumb_link");
		scriptLink.setOnAction(linkEvent -> {
			setScriptLayout(sensor, attribute);
		});
		HBox scriptHbox=new HBox(scriptImageView, scriptLink);
		scriptHbox.setAlignment(Pos.CENTER_LEFT);

		ArrayList<Node> nodeList=new ArrayList<Node>();
		nodeList.add(sensorsHbox);
		nodeList.add(speratorText);
		nodeList.add(configureHbox);
		nodeList.add(speratorText2);
		nodeList.add(scriptHbox);
		this.rootController.setBreadcrumb(nodeList);
	}

	private void initSensorOverviewBreadcrumb(Sensor sensor){
		ImageView sensorsImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_developer_board_white_24.png")));
		sensorsImageView.setFitWidth(18);
		sensorsImageView.setFitHeight(18);
		Hyperlink sensorsLink=new Hyperlink("Sensors");
		sensorsLink.getStyleClass().add("breadcrumb_link");
		sensorsLink.setOnAction(linkEvent -> {
			setSensorsLayout();
		});
		HBox sensorsHbox=new HBox(sensorsImageView, sensorsLink);
		sensorsHbox.setAlignment(Pos.CENTER_LEFT);

		Text speratorText=new Text("-");
		speratorText.setFill(Color.WHITE);
		speratorText.setStyle("-fx-font-weight: bold");

		ImageView overviewImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_list_alt_white_24.png")));
		overviewImageView.setFitWidth(18);
		overviewImageView.setFitHeight(18);
		Hyperlink overviewLink=new Hyperlink("Overview of Sensor '"+sensor.getName()+" ("+sensor.getId()+")'");
		overviewLink.getStyleClass().add("breadcrumb_link");
		overviewLink.setOnAction(linkEvent -> {
			setSensorOverviewLayout(sensor);
		});
		HBox overviewHbox=new HBox(overviewImageView, overviewLink);
		overviewHbox.setAlignment(Pos.CENTER_LEFT);

		ArrayList<Node> nodeList=new ArrayList<Node>();
		nodeList.add(sensorsHbox);
		nodeList.add(speratorText);
		nodeList.add(overviewHbox);
		this.rootController.setBreadcrumb(nodeList);
	}

	private void initSensorHistoryBreadcrumb(Sensor sensor){
		ImageView sensorsImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_developer_board_white_24.png")));
		sensorsImageView.setFitWidth(18);
		sensorsImageView.setFitHeight(18);
		Hyperlink sensorsLink=new Hyperlink("Sensors");
		sensorsLink.getStyleClass().add("breadcrumb_link");
		sensorsLink.setOnAction(linkEvent -> {
			setSensorsLayout();
		});
		HBox sensorsHbox=new HBox(sensorsImageView, sensorsLink);
		sensorsHbox.setAlignment(Pos.CENTER_LEFT);

		Text speratorText=new Text("-");
		speratorText.setFill(Color.WHITE);
		speratorText.setStyle("-fx-font-weight: bold");

		ImageView overviewImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_list_alt_white_24.png")));
		overviewImageView.setFitWidth(18);
		overviewImageView.setFitHeight(18);
		Hyperlink overviewLink=new Hyperlink("Overview of Sensor '"+sensor.getName()+" ("+sensor.getId()+")'");
		overviewLink.getStyleClass().add("breadcrumb_link");
		overviewLink.setOnAction(linkEvent -> {
			setSensorOverviewLayout(sensor);
		});
		HBox overviewHbox=new HBox(overviewImageView, overviewLink);
		overviewHbox.setAlignment(Pos.CENTER_LEFT);

		Text speratorText2=new Text("-");
		speratorText2.setFill(Color.WHITE);
		speratorText2.setStyle("-fx-font-weight: bold");

		ImageView historyImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_bar_chart_white_24.png")));
		historyImageView.setFitWidth(18);
		historyImageView.setFitHeight(18);
		Hyperlink historyLink=new Hyperlink("History of Sensor '"+sensor.getName()+" ("+sensor.getId()+")'");
		historyLink.getStyleClass().add("breadcrumb_link");
		historyLink.setOnAction(linkEvent -> {
			setSensorHistoryLayout(sensor);
		});
		HBox historyHbox=new HBox(historyImageView, historyLink);
		historyHbox.setAlignment(Pos.CENTER_LEFT);

		ArrayList<Node> nodeList=new ArrayList<Node>();
		nodeList.add(sensorsHbox);
		nodeList.add(speratorText);
		nodeList.add(overviewHbox);
		nodeList.add(speratorText2);
		nodeList.add(historyHbox);
		this.rootController.setBreadcrumb(nodeList);
	}

	private void initEventsBreadcrumb(){
		ImageView eventsImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_event_white_24.png")));
		eventsImageView.setFitWidth(18);
		eventsImageView.setFitHeight(18);
		Hyperlink eventsLink=new Hyperlink("Events");
		eventsLink.getStyleClass().add("breadcrumb_link");
		eventsLink.setOnAction(linkEvent -> {
			setEventsLayout();
		});
		HBox eventsHbox=new HBox(eventsImageView, eventsLink);
		eventsHbox.setAlignment(Pos.CENTER_LEFT);

		ArrayList<Node> nodeList=new ArrayList<Node>();
		nodeList.add(eventsHbox);
		this.rootController.setBreadcrumb(nodeList);
	}

	private void initAddEventBreadcrumb(){
		ImageView eventsImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_event_white_24.png")));
		eventsImageView.setFitWidth(18);
		eventsImageView.setFitHeight(18);
		Hyperlink eventsLink=new Hyperlink("Events");
		eventsLink.getStyleClass().add("breadcrumb_link");
		eventsLink.setOnAction(linkEvent -> {
			setEventsLayout();
		});
		HBox eventsHbox=new HBox(eventsImageView, eventsLink);
		eventsHbox.setAlignment(Pos.CENTER_LEFT);

		Text speratorText=new Text("-");
		speratorText.setFill(Color.WHITE);
		speratorText.setStyle("-fx-font-weight: bold");

		ImageView addEventImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_add_white_24.png")));
		addEventImageView.setFitWidth(18);
		addEventImageView.setFitHeight(18);
		Hyperlink addEventLink=new Hyperlink("Add Event");
		addEventLink.getStyleClass().add("breadcrumb_link");
		addEventLink.setOnAction(linkEvent -> {
			setAddEventLayout();
		});
		HBox addEventHbox=new HBox(addEventImageView, addEventLink);
		addEventHbox.setAlignment(Pos.CENTER_LEFT);

		ArrayList<Node> nodeList=new ArrayList<Node>();
		nodeList.add(eventsHbox);
		nodeList.add(speratorText);
		nodeList.add(addEventHbox);
		this.rootController.setBreadcrumb(nodeList);
	}

	private void initNotificationsBreadcrumb(){
		ImageView notificationImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_notifications_white_24.png")));
		notificationImageView.setFitWidth(18);
		notificationImageView.setFitHeight(18);
		Hyperlink notificationLink=new Hyperlink("Notification");
		notificationLink.getStyleClass().add("breadcrumb_link");
		notificationLink.setOnAction(linkEvent -> {
			setNotificationsLayout();
		});
		HBox notificationHbox=new HBox(notificationImageView, notificationLink);
		notificationHbox.setAlignment(Pos.CENTER_LEFT);

		ArrayList<Node> nodeList=new ArrayList<Node>();
		nodeList.add(notificationHbox);
		this.rootController.setBreadcrumb(nodeList);
	}

	private void initControllersBreadcrumb(){
		ImageView controllersImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_devices_other_white_24.png")));
		controllersImageView.setFitWidth(18);
		controllersImageView.setFitHeight(18);
		Hyperlink controllersLink=new Hyperlink("Controllers");
		controllersLink.getStyleClass().add("breadcrumb_link");
		controllersLink.setOnAction(linkEvent -> {
			setControllersLayout();
		});
		HBox controllersHbox=new HBox(controllersImageView, controllersLink);
		controllersHbox.setAlignment(Pos.CENTER_LEFT);

		ArrayList<Node> nodeList=new ArrayList<Node>();
		nodeList.add(controllersHbox);
		this.rootController.setBreadcrumb(nodeList);
	}

	private void initSettingsBreadcrumb(){
		ImageView settingsImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_settings_white_24.png")));
		settingsImageView.setFitWidth(18);
		settingsImageView.setFitHeight(18);
		Hyperlink settingsLink=new Hyperlink("Settings");
		settingsLink.getStyleClass().add("breadcrumb_link");
		settingsLink.setOnAction(linkEvent -> {
			setSettingsLayout();
		});
		HBox settingsHbox=new HBox(settingsImageView, settingsLink);
		settingsHbox.setAlignment(Pos.CENTER_LEFT);

		ArrayList<Node> nodeList=new ArrayList<Node>();
		nodeList.add(settingsHbox);
		this.rootController.setBreadcrumb(nodeList);
	}

	public void showMessageStrip(NotificationType type, String message, StackPane stackPane){
		Platform.runLater(() -> {
			Text text=new Text(message);
			text.setFill(Color.WHITE);
			text.setFont(Font.font(null, FontWeight.BOLD, 12));
			double textWidth=text.getBoundsInLocal().getWidth()+20;
			HBox toast=new HBox(text);
			toast.setAlignment(Pos.CENTER);
			toast.setMaxSize(textWidth, 30);

			if(type==NotificationType.SUCCESS){
				toast.getStyleClass().add("success_strip");
			}
			else if(type==NotificationType.WARNING){
				toast.getStyleClass().add("warning_strip");
			}
			else if(type==NotificationType.ERROR){
				toast.getStyleClass().add("error_strip");
			}

			stackPane.getChildren().add(toast);
			StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
			StackPane.setMargin(toast, new Insets(0, 0, 5, 0));
			new Timeline(new KeyFrame(Duration.millis(3000), ae -> {
				stackPane.getChildren().remove(toast);
			})).play();
		});
	}

	public void showAndSaveNotification(Notification notification, StackPane stackPane){
		Notification newNotification=this.database.saveNotification(notification);
		if(newNotification!=null){
			this.showMessageStrip(notification.getType(), notification.getMessage(), stackPane);
			JsonElement jsonNotification=new Gson().toJsonTree(newNotification);
			JsonObject jsonObject=new JsonObject();
			jsonObject.addProperty("command", "addNotification");
			jsonObject.add("notification", jsonNotification);
			elastosCarrier.sendDataToControllers(jsonObject);
		}
		else{
			this.showMessageStrip(NotificationType.ERROR, "Sorry, something went wrong.", stackPane);
		}
	}

	public void executeAsyncTask(Task task, StackPane stackPane){
		Region veil=new Region();
        veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
		ProgressIndicator indicator=new ProgressIndicator();
		indicator.setMaxSize(42, 42);
		indicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
		indicator.setStyle("-fx-progress-color: white;");
		stackPane.getChildren().addAll(veil, indicator);

		veil.visibleProperty().bind(task.runningProperty());
		indicator.visibleProperty().bind(task.runningProperty());
		new Thread(task).start();
	}

	public void showDialog(Region veil, AnchorPane dialog, StackPane stackPane){
		stackPane.getChildren().addAll(veil, dialog);
	}

	public void closeDialog(Region veil, AnchorPane dialog, StackPane stackPane){
		stackPane.getChildren().removeAll(veil, dialog);
	}


	public static void main(String[] arguments){
		launch(arguments);
	}
}
