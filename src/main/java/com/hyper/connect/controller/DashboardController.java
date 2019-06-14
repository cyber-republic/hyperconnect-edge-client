package com.hyper.connect.controller;

import com.hyper.connect.App;
import com.hyper.connect.model.*;
import com.hyper.connect.management.HistoryManagement;
import com.hyper.connect.model.enums.AttributeState;
import com.hyper.connect.model.enums.EventState;
import com.hyper.connect.model.enums.NotificationType;
import com.hyper.connect.model.enums.PinnedChartWindow;
import com.hyper.connect.util.CustomUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.scene.control.Pagination;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.Collections;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.IOException;


public class DashboardController{
	private App app;
	
	@FXML private StackPane dashboardPane;
	@FXML private HBox connectedHbox;
	@FXML private HBox disconnectedHbox;
	@FXML private Text attributeText;
	@FXML private Text activeAttributeText;
	@FXML private Text eventText;
	@FXML private Text activeEventText;
	@FXML private Text successText;
	@FXML private Text warningText;
	@FXML private Text errorText;
	@FXML private Text controllerText;
	@FXML private Text activeControllerText;
	@FXML private Pagination pagination;
	@FXML private HBox placeholderHbox;
	
	public DashboardController(){}

	public void setApp(App app){
		this.app=app;
	}
	
	public void init(){
		Task initTask=new Task<Void>(){
			@Override
			public Void call(){
				String attributeCount=Integer.toString(app.getDatabase().getAttributeCount());
				String activeAttributeCount=Integer.toString(app.getDatabase().getAttributeCountByState(AttributeState.ACTIVE));
				attributeText.setText(attributeCount);
				activeAttributeText.setText(activeAttributeCount);
				
				String eventCount=Integer.toString(app.getDatabase().getEventCount());
				String activeEventCount=Integer.toString(app.getDatabase().getEventCountByState(EventState.ACTIVE));
				eventText.setText(eventCount);
				activeEventText.setText(activeEventCount);
				
				String successCount=Integer.toString(app.getDatabase().getNotificationCountByType(NotificationType.SUCCESS));
				successText.setText(successCount);
				String warningCount=Integer.toString(app.getDatabase().getNotificationCountByType(NotificationType.WARNING));
				warningText.setText(warningCount);
				String errorCount=Integer.toString(app.getDatabase().getNotificationCountByType(NotificationType.ERROR));
				errorText.setText(errorCount);
				
				String controllerCount=Integer.toString(app.getDatabase().getControllerCount());
				String activeControllerCount=Integer.toString(app.getDatabase().getControllerCountByState("active"));
				controllerText.setText(controllerCount);
				activeControllerText.setText(activeControllerCount);
				
				pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);

				ArrayList<PinnedChart> pinnedChartList=app.getDatabase().getPinnedChartList();
				Platform.runLater(() -> {
					if(pinnedChartList.isEmpty()){
						placeholderHbox.setVisible(true);
						pagination.setVisible(false);
					}
					else{
						placeholderHbox.setVisible(false);
						pagination.setVisible(true);
						pagination.setPageCount(pinnedChartList.size());
						pagination.setPageFactory((Integer pageIndex) -> createLineChart(pinnedChartList.get(pageIndex)));
					}
					
				});

				if(!app.DEBUG){
					refreshConnectionState();
				}
				
				return null;
			}
		};
		this.app.executeAsyncTask(initTask, dashboardPane);
	}
	
	public void refreshConnectionState(){
		boolean isConnected=this.app.getElastosCarrier().isConnected();
		if(isConnected){
			this.connectedHbox.setVisible(true);
			this.disconnectedHbox.setVisible(false);
		}
		else{
			this.connectedHbox.setVisible(false);
			this.disconnectedHbox.setVisible(true);
		}
	}
	
	private AnchorPane createLineChart(PinnedChart pinnedChart){
		String filename=pinnedChart.getAttributeId()+"_"+pinnedChart.getAverage().getShortFilename()+".json";
		HistoryManagement historyManager=new HistoryManagement();
		ConcurrentMap<String, String> valueMap=historyManager.getHistory(filename);

		String dateTime="";
		PinnedChartWindow window=pinnedChart.getWindow();
		if(window==PinnedChartWindow.HOUR){
			dateTime=CustomUtil.getCurrentDateTimeByPatternAndTimeZone("yyyy/MM/dd HH", app.getTimeZone());
		}
		else if(window==PinnedChartWindow.DAY){
			dateTime=CustomUtil.getCurrentDateTimeByPatternAndTimeZone("yyyy/MM/dd", app.getTimeZone());
		}
		else if(window==PinnedChartWindow.MONTH){
			dateTime=CustomUtil.getCurrentDateTimeByPatternAndTimeZone("yyyy/MM", app.getTimeZone());
		}
		final String finalDateTime=dateTime;

		
		List<DataRecord> sortedList=valueMap.entrySet().stream().map(
			r -> {
				DataRecord record=new DataRecord(CustomUtil.getDateTimeByTimeZone(r.getKey(), app.getTimeZone()), r.getValue());
				return record;
			}
		).filter(
			object -> object.getDateTime().contains(finalDateTime)
		).reduce(
			new ArrayList<DataRecord>(),
			(objectList, record) -> {
				objectList.add(record);
				return objectList;
			},
			(objectList, otherObjectList) -> {
				objectList.addAll(otherObjectList);
				return objectList;
			}
		);
		Collections.sort(sortedList, new Comparator<DataRecord>(){
			SimpleDateFormat f=new SimpleDateFormat("yyyy/MM/dd HH:mm");
			public int compare(DataRecord o1, DataRecord o2){
				try{
					return f.parse(o1.getDateTime()).before(f.parse(o2.getDateTime())) ? -1 : 1;
				}
				catch(ParseException pe){
					return 0;
				}
			}
		});
		
		final LineChart<String, Number> lineChart=new LineChart<String, Number>(new CategoryAxis(), new NumberAxis());
		XYChart.Series series=new XYChart.Series();
		series.setName("Attribute: "+pinnedChart.getAttributeName()+" ("+pinnedChart.getAttributeId()+") --- Window: "+pinnedChart.getWindow()+" --- Average: "+pinnedChart.getAverage());
		
		for(int i=0;i<sortedList.size();i++){
			series.getData().add(new XYChart.Data(sortedList.get(i).getDateTime(), Double.valueOf(sortedList.get(i).getValue())));
		}
		lineChart.getData().clear();
		lineChart.getData().add(series);
		
		final Button deleteButton=new Button();
		deleteButton.setPadding(new Insets(0, 0, 0, 0));
		deleteButton.setCursor(Cursor.HAND);

		ImageView deleteImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_close_blue_24.png")));
		deleteImageView.setFitWidth(24);
		deleteImageView.setFitHeight(24);
		deleteButton.setStyle("-fx-background-color: transparent;");
		deleteButton.setGraphic(deleteImageView);
		deleteButton.setOnAction(deleteEvent -> {
			try{
				Region veil=new Region();
				veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");

				FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/dialog_remove_chart.fxml"));
				AnchorPane deleteDialogContent=(AnchorPane)loader.load();

				AnchorPane titelPane=(AnchorPane)deleteDialogContent.lookup("#titelPane");
				Text dialogText=(Text)deleteDialogContent.lookup("#dialogText");
				HBox actionsHbox=(HBox)deleteDialogContent.lookup("#actionsHbox");
				double dialogHeight=dialogText.getBoundsInLocal().getHeight()+titelPane.getPrefHeight()+actionsHbox.getPrefHeight()+30;
				deleteDialogContent.setPrefHeight(dialogHeight);

				Button cancelButton=(Button)deleteDialogContent.lookup("#cancelButton");
				cancelButton.setOnAction(cancelEvent -> {
					app.closeDialog(veil, deleteDialogContent, dashboardPane);
				});
				Button confirmButton=(Button)deleteDialogContent.lookup("#confirmButton");
				confirmButton.setOnAction(confirmEvent -> {
					Task deleteTask=new Task<Void>(){
						@Override
						public Void call(){
							boolean deleteResult=app.getDatabase().deletePinnedChartByPinnedChartId(pinnedChart.getId());
							if(deleteResult){
								Platform.runLater(() -> init());
								app.showMessageStrip(NotificationType.SUCCESS, "Chart has been removed.", dashboardPane);
							}
							else{
								app.showMessageStrip(NotificationType.ERROR, "Sorry, something went wrong removing a chart from the dashboard.", dashboardPane);
							}
							
							return null;
						}
					};
					app.executeAsyncTask(deleteTask, dashboardPane);
					app.closeDialog(veil, deleteDialogContent, dashboardPane);
				});

				app.showDialog(veil, deleteDialogContent, dashboardPane);
			}
			catch(IOException ioe){}
		});
		
		AnchorPane anchorPane=new AnchorPane();
		AnchorPane.setTopAnchor(deleteButton, 0.0);
		AnchorPane.setRightAnchor(deleteButton, 10.0);

		AnchorPane.setTopAnchor(lineChart, 12.0);
		AnchorPane.setRightAnchor(lineChart, 0.0);
		AnchorPane.setBottomAnchor(lineChart, 0.0);
		AnchorPane.setLeftAnchor(lineChart, 0.0);
		
		anchorPane.getChildren().addAll(deleteButton, lineChart);
		
		return anchorPane;
	}
	
	/*private String getAverageKeyByAverage(String average){
		String averageKey="";
		if(average.equals("1m")){
			averageKey="1 Minute";
		}
		else if(average.equals("5m")){
			averageKey="5 Minutes";
		}
		else if(average.equals("15m")){
			averageKey="15 Minutes";
		}
		else if(average.equals("1h")){
			averageKey="1 Hour";
		}
		else if(average.equals("3h")){
			averageKey="3 Hours";
		}
		else if(average.equals("6h")){
			averageKey="6 Hours";
		}
		else if(average.equals("1d")){
			averageKey="1 Day";
		}
		return average;
	}*/
}