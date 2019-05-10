package com.hyper.connect.controller;

import com.hyper.connect.App;
import com.hyper.connect.model.Sensor;
import com.hyper.connect.model.Attribute;
import com.hyper.connect.model.DataRecord;
import com.hyper.connect.model.PinnedChart;
import com.hyper.connect.management.HistoryManagement;
import com.hyper.connect.util.CustomUtil;

import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.concurrent.Task;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.concurrent.ConcurrentMap;
import java.util.Collections;
import java.util.Comparator;
import java.text.SimpleDateFormat;
import java.text.ParseException;


public class SensorHistoryController{
	private App app;
	private Sensor sensor;
	private ArrayList<Attribute> attributeList=null;
	private String window=null;
	private final Image starImage=new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_star_blue_24.png"));
	private final Image starImageEmpty=new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_star_border_blue_24.png"));
	private PinnedChart currentPinnedChart=null;
	
	@FXML private StackPane sensorHistoryPane;
	@FXML private Text titleText;
	@FXML private ChoiceBox attributeChoiceBox;
	@FXML private ChoiceBox windowChoiceBox;
	@FXML private ChoiceBox averageChoiceBox;
	@FXML private ChoiceBox monthChoiceBox;
	@FXML private DatePicker datePicker;
	@FXML private ChoiceBox hourChoiceBox;
	@FXML private LineChart lineChart;
	@FXML private TableView tableView;
	@FXML private TableColumn valueColumn;
	@FXML private TableColumn dateTimeColumn;
	@FXML private Button chartButton;
	@FXML private Button tableButton;
	@FXML private Button favoriteButton;
	@FXML private ImageView favoriteImageView;
	
	public SensorHistoryController(){}
	
	public void setApp(App app){
		this.app=app;
	}
	
	public void init(Sensor sensor){
		this.sensor=sensor;
		
		Task initTask=new Task<Void>(){
			@Override
			public Void call(){
				attributeList=app.getDatabase().getAttributeListBySensorId(sensor.getId());
				Platform.runLater(() -> {
					titleText.setText("History of Sensor '"+sensor.getName()+" ("+sensor.getId()+")'");
					clearFields();
					lineChart.getData().clear();
					lineChart.setVisible(true);
					tableView.getItems().clear();
					tableView.setVisible(false);
					chartButton.setDisable(true);
					tableButton.setDisable(false);
					initAttribute();
				});
				return null;
			}
		};
		
		this.app.executeAsyncTask(initTask, sensorHistoryPane);
	}
	
	private void clearFields(){
		this.clearAttribute();
		this.clearWindow();
		this.clearAverage();
		this.clearMonth();
		this.clearDate();
		this.clearHour();
	}
	
	private void clearAttribute(){
		this.attributeChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(attributeListener);
		this.attributeChoiceBox.getItems().clear();
	}
	
	private void clearWindow(){
		this.windowChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(windowListener);
		this.windowChoiceBox.getItems().clear();
		this.windowChoiceBox.setDisable(true);
	}
	
	private void clearAverage(){
		this.averageChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(averageListener);
		this.averageChoiceBox.getItems().clear();
		this.averageChoiceBox.setDisable(true);
		this.initFavorite();
	}
	
	private void clearMonth(){
		this.monthChoiceBox.getItems().clear();
		this.monthChoiceBox.setVisible(false);
		this.monthChoiceBox.setManaged(false);
		this.monthChoiceBox.setDisable(true);
	}
	
	private void clearDate(){
		this.datePicker.valueProperty().removeListener(dateListener);
		this.datePicker.setValue(null);
		this.datePicker.setVisible(false);
		this.datePicker.setManaged(false);
		this.datePicker.setDisable(true);
	}
	
	private void clearHour(){
		this.hourChoiceBox.getItems().clear();
		this.hourChoiceBox.setVisible(false);
		this.hourChoiceBox.setManaged(false);
		this.hourChoiceBox.setDisable(true);
	}
	
	private void initAttribute(){
		this.attributeChoiceBox.getItems().clear();
		this.attributeChoiceBox.getItems().add("<Attribute>");
		for(int i=0;i<this.attributeList.size();i++){
			Attribute attribute=this.attributeList.get(i);
			if(!attribute.getType().equals("boolean") && !attribute.getType().equals("string")){
				this.attributeChoiceBox.getItems().add(attribute.getName());
			}
		}
		this.attributeChoiceBox.getSelectionModel().selectFirst();
		this.attributeChoiceBox.getSelectionModel().selectedIndexProperty().addListener(attributeListener);
	}
	
	private final ChangeListener<Number> attributeListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			clearWindow();
			clearAverage();
			clearMonth();
			clearDate();
			clearHour();
			int attributeIndex=(Integer)number2;
			if(attributeIndex!=0){
				initWindow();
			}
		}
	};
	
	private void initWindow(){
		this.windowChoiceBox.setDisable(false);
		this.windowChoiceBox.getItems().clear();
		this.windowChoiceBox.getItems().addAll("<Window>", "Hour", "Day", "Month");
		this.windowChoiceBox.getSelectionModel().selectFirst();
		this.windowChoiceBox.getSelectionModel().selectedIndexProperty().addListener(windowListener);
	}
	
	private final ChangeListener<Number> windowListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			clearAverage();
			clearMonth();
			clearDate();
			clearHour();
			int windowIndex=(Integer)number2;
			if(windowIndex!=0){
				window=(String)windowChoiceBox.getItems().get((Integer)number2);
				initAverage();
				showDateTime();
			}
		}
	};
	
	private void initAverage(){
		this.averageChoiceBox.setDisable(false);
		this.averageChoiceBox.getItems().clear();
		this.averageChoiceBox.getItems().add("<Average>");
		if(this.window.equals("Hour")){
			this.averageChoiceBox.getItems().addAll("1 Minute", "5 Minutes");
		}
		else if(this.window.equals("Day")){
			this.averageChoiceBox.getItems().addAll("1 Minute", "5 Minutes", "15 Minutes", "1 Hour");
		}
		else if(this.window.equals("Month")){
			this.averageChoiceBox.getItems().addAll("1 Hour", "3 Hours", "6 Hours", "1 Day");
		}
		this.averageChoiceBox.getSelectionModel().selectFirst();
		this.averageChoiceBox.getSelectionModel().selectedIndexProperty().addListener(averageListener);
	}
	
	private final ChangeListener<Number> averageListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			disableDateTime();
			int averageIndex=(Integer)number2;
			if(averageIndex!=0){
				initDateTime();
			}
			initFavorite();
		}
	};
	
	private void showDateTime(){
		if(this.window.equals("Hour")){
			this.datePicker.setVisible(true);
			this.datePicker.setManaged(true);
			
			this.hourChoiceBox.setVisible(true);
			this.hourChoiceBox.setManaged(true);
		}
		else if(this.window.equals("Day")){
			this.datePicker.setVisible(true);
			this.datePicker.setManaged(true);
		}
		else if(this.window.equals("Month")){
			this.monthChoiceBox.setVisible(true);
			this.monthChoiceBox.setManaged(true);
		}
	}
	
	private void disableDateTime(){
		this.datePicker.valueProperty().removeListener(dateListener);
		this.datePicker.setValue(null);
		this.datePicker.setDisable(true);
		this.hourChoiceBox.setDisable(true);
		this.hourChoiceBox.getItems().clear();
		this.hourChoiceBox.getSelectionModel().selectFirst();
		this.monthChoiceBox.setDisable(true);
		this.monthChoiceBox.getItems().clear();
		this.monthChoiceBox.getSelectionModel().selectFirst();
	}
	
	private void initDateTime(){
		if(this.window.equals("Hour")){
			this.datePicker.setVisible(true);
			this.datePicker.setManaged(true);
			this.datePicker.setDisable(false);
			this.datePicker.valueProperty().addListener(dateListener);
		}
		else if(this.window.equals("Day")){
			this.datePicker.setVisible(true);
			this.datePicker.setManaged(true);
			this.datePicker.setDisable(false);
		}
		else if(this.window.equals("Month")){
			this.monthChoiceBox.setVisible(true);
			this.monthChoiceBox.setManaged(true);
			this.monthChoiceBox.setDisable(false);
			this.monthChoiceBox.getItems().clear();
			this.monthChoiceBox.getItems().addAll("<Month>", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
			this.monthChoiceBox.getSelectionModel().selectFirst();
		}
	}
	
	private final ChangeListener<LocalDate> dateListener=new ChangeListener<LocalDate>(){
		@Override
		public void changed(ObservableValue<? extends LocalDate> observableValue, LocalDate oldValue, LocalDate newValue){
			if(newValue==null){
				hourChoiceBox.setDisable(true);
				hourChoiceBox.getItems().clear();
				hourChoiceBox.getSelectionModel().selectFirst();
			}
			else{
				initHour();
			}
		}
	};
	
	private void initHour(){
		this.hourChoiceBox.setVisible(true);
		this.hourChoiceBox.setManaged(true);
		this.hourChoiceBox.setDisable(false);
		this.hourChoiceBox.getItems().clear();
		this.hourChoiceBox.getItems().addAll("<Hour>", "00 h", "01 h", "02 h", "03 h", "04 h", "05 h", "06 h", "07 h", "08 h", "09 h", "10 h", "11 h", "12 h", "13 h", "14 h", "15 h", "16 h", "17 h", "18 h", "19 h", "20 h", "21 h", "22 h", "23 h");
		this.hourChoiceBox.getSelectionModel().selectFirst();
	}
	
	@FXML
	private void onViewButton(){
		Task chartTask=new Task<Void>(){
			@Override
			public Void call(){
				int attributeIndex=attributeChoiceBox.getSelectionModel().getSelectedIndex();
				int windowIndex=windowChoiceBox.getSelectionModel().getSelectedIndex();
				int averageIndex=averageChoiceBox.getSelectionModel().getSelectedIndex();
				
				int errorCount=0;
				Attribute attribute=null;
				String filename="";
				String dateTime="";
				if(attributeIndex!=0 && windowIndex!=0 && averageIndex!=0){
					attribute=attributeList.get(attributeIndex-1);
					String windowKey=(String)windowChoiceBox.getSelectionModel().getSelectedItem();
					String averageKey=(String)averageChoiceBox.getSelectionModel().getSelectedItem();
					String average=getAverageByKey(averageKey);
					filename=attribute.getId()+"_"+average+".json";
					
					if(windowKey.equals("Hour")){
						LocalDate localDate=datePicker.getValue();
						if(localDate!=null){
							String dateKey=localDate.toString();
							String date=getDateByKey(dateKey);
							int hourIndex=hourChoiceBox.getSelectionModel().getSelectedIndex();
							if(hourIndex!=0){
								String hourKey=(String)hourChoiceBox.getSelectionModel().getSelectedItem();
								String hour=getHourByKey(hourKey);
								dateTime=date+" "+hour;
							}
							else{
								errorCount++;
							}
						}
						else{
							errorCount++;
						}
					}
					else if(windowKey.equals("Day")){
						LocalDate localDate=datePicker.getValue();
						if(localDate!=null){
							String dateKey=localDate.toString();
							String date=getDateByKey(dateKey);
							dateTime=date;
						}
						else{
							errorCount++;
						}
					}
					else if(windowKey.equals("Month")){
						int monthIndex=monthChoiceBox.getSelectionModel().getSelectedIndex();
						if(monthIndex!=0){
							String monthKey=(String)monthChoiceBox.getSelectionModel().getSelectedItem();
							String month=getMonthByKey(monthKey);
							int year=Calendar.getInstance().get(Calendar.YEAR);
							dateTime=year+"/"+month;
						}
						else{
							errorCount++;
						}
					}
				}
				else{
					errorCount++;
				}
				
				if(errorCount==0){
					showData(filename, attribute, dateTime);
				}
				else{
					app.showMessageStrip("Warning", "All options must be selected.", sensorHistoryPane);
				}
				
				return null;
			}
		};
		this.app.executeAsyncTask(chartTask, sensorHistoryPane);
	}
	
	private void showData(String filename, Attribute attribute, String dateTime){
		HistoryManagement historyManager=new HistoryManagement();
		ConcurrentMap<String, String> valueMap=historyManager.getHistory(filename);
		
		List<DataRecord> sortedList=valueMap.entrySet().stream().map(
			r -> {
				DataRecord record=new DataRecord(CustomUtil.getDateTimeByTimeZone(r.getKey(), app.getTimeZone()), r.getValue());
				return record;
			}
		).filter(
			object -> object.getDateTime().contains(dateTime)
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
		
		Platform.runLater(() -> {
			XYChart.Series series=new XYChart.Series();
			series.setName(attribute.getName());
			for(int i=0;i<sortedList.size();i++){
				series.getData().add(new XYChart.Data(sortedList.get(i).getDateTime(), Double.valueOf(sortedList.get(i).getValue())));
			}
			this.lineChart.getData().clear();
			this.lineChart.getData().add(series);
			
			Collections.reverse(sortedList);
			this.tableView.getItems().clear();
			ObservableList<DataRecord> tableObservableList=FXCollections.observableArrayList(sortedList);
			this.valueColumn.setCellValueFactory(new PropertyValueFactory("value"));
			this.dateTimeColumn.setCellValueFactory(new PropertyValueFactory("dateTime"));
			this.tableView.setItems(tableObservableList);
		});
	}
	
	private String getAverageByKey(String averageKey){
		String average="";
		if(averageKey.equals("1 Minute")){
			average="1m";
		}
		else if(averageKey.equals("5 Minutes")){
			average="5m";
		}
		else if(averageKey.equals("15 Minutes")){
			average="15m";
		}
		else if(averageKey.equals("1 Hour")){
			average="1h";
		}
		else if(averageKey.equals("3 Hours")){
			average="3h";
		}
		else if(averageKey.equals("6 Hours")){
			average="6h";
		}
		else if(averageKey.equals("1 Day")){
			average="1d";
		}
		return average;
	}
	
	private String getDateByKey(String dateKey){
		String date=dateKey.replace("-", "/");
		return date;
	}
	
	private String getHourByKey(String hourKey){
		String hour=hourKey.substring(0, hourKey.length()-2);
		return hour;
	}
	
	private String getMonthByKey(String monthKey){
		String month="";
		if(monthKey.equals("January")){
			month="01";
		}
		else if(monthKey.equals("February")){
			month="02";
		}
		else if(monthKey.equals("March")){
			month="03";
		}
		else if(monthKey.equals("April")){
			month="04";
		}
		else if(monthKey.equals("May")){
			month="05";
		}
		else if(monthKey.equals("June")){
			month="06";
		}
		else if(monthKey.equals("July")){
			month="07";
		}
		else if(monthKey.equals("August")){
			month="08";
		}
		else if(monthKey.equals("September")){
			month="09";
		}
		else if(monthKey.equals("October")){
			month="10";
		}
		else if(monthKey.equals("November")){
			month="11";
		}
		else if(monthKey.equals("December")){
			month="12";
		}
		return month;
	}
	
	private void initFavorite(){
		int attributeIndex=attributeChoiceBox.getSelectionModel().getSelectedIndex();
		int windowIndex=windowChoiceBox.getSelectionModel().getSelectedIndex();
		int averageIndex=averageChoiceBox.getSelectionModel().getSelectedIndex();
		
		if(attributeIndex>0 && windowIndex>0 && averageIndex>0){
			Attribute attribute=attributeList.get(attributeIndex-1);
			String windowKey=(String)windowChoiceBox.getSelectionModel().getSelectedItem();
			averageChoiceBox.getSelectionModel().select(averageIndex);
			String averageKey=(String)averageChoiceBox.getSelectionModel().getSelectedItem();
			String average=getAverageByKey(averageKey);
			
			favoriteButton.setDisable(false);
			this.currentPinnedChart=this.app.getDatabase().getPinnedChartByParameters(attribute.getId(), windowKey, average);
			if(this.currentPinnedChart==null){
				favoriteImageView.setImage(starImageEmpty);
			}
			else{
				favoriteImageView.setImage(starImage);
			}
		}
		else{
			this.currentPinnedChart=null;
			favoriteImageView.setImage(starImageEmpty);
			favoriteButton.setDisable(true);
		}
	}
	
	@FXML
	private void onFavorite(){
		Task favoriteTask=new Task<Void>(){
			@Override
			public Void call(){
				if(currentPinnedChart==null){
					int attributeIndex=attributeChoiceBox.getSelectionModel().getSelectedIndex();
					int windowIndex=windowChoiceBox.getSelectionModel().getSelectedIndex();
					int averageIndex=averageChoiceBox.getSelectionModel().getSelectedIndex();
					if(attributeIndex>0 && windowIndex>0 && averageIndex>0){
						Attribute attribute=attributeList.get(attributeIndex-1);
						String windowKey=(String)windowChoiceBox.getSelectionModel().getSelectedItem();
						String averageKey=(String)averageChoiceBox.getSelectionModel().getSelectedItem();
						String average=getAverageByKey(averageKey);
						PinnedChart newPinnedChart=app.getDatabase().savePinnedChart(new PinnedChart(0, attribute.getId(), attribute.getName(), window, average));
						if(newPinnedChart!=null){
							currentPinnedChart=newPinnedChart;
							favoriteImageView.setImage(starImage);
							favoriteButton.setDisable(false);
							app.showMessageStrip("Success", "Chart has been pinned to the dashboard.", sensorHistoryPane);
						}
						else{
							app.showMessageStripAndSave("Error", "System", "Sorry, something went wrong pinning a chart to the dashboard.", sensorHistoryPane);
						}
					}
				}
				else{
					boolean deleteResult=app.getDatabase().deletePinnedChartByPinnedChartId(currentPinnedChart.getId());
					if(deleteResult){
						currentPinnedChart=null;
						favoriteImageView.setImage(starImageEmpty);
						favoriteButton.setDisable(false);
						app.showMessageStrip("Success", "Chart has been unpinned from the dashboard.", sensorHistoryPane);
					}
					else{
						app.showMessageStripAndSave("Error", "System", "Sorry, something went wrong unpinning a chart from the dashboard.", sensorHistoryPane);
					}
				}
				return null;
			}
		};
		this.app.executeAsyncTask(favoriteTask, sensorHistoryPane);
	}
	
	@FXML
	private void onChartButton(){
		lineChart.setVisible(true);
		tableView.setVisible(false);
		chartButton.setDisable(true);
		tableButton.setDisable(false);
	}
	
	@FXML
	private void onTableButton(){
		lineChart.setVisible(false);
		tableView.setVisible(true);
		chartButton.setDisable(false);
		tableButton.setDisable(true);
	}
}