package com.hyper.connect.controller;

import com.google.gson.JsonObject;
import com.hyper.connect.App;
import com.hyper.connect.elastos.ElastosCarrier;
import com.hyper.connect.model.enums.NotificationType;
import com.hyper.connect.util.BackupUtil;
import com.hyper.connect.util.ChoiceItem;
import com.hyper.connect.util.TaskUtil;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.elastos.carrier.UserInfo;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipOutputStream;


public class SettingsController{
	private App app;
	private final ObservableList<ChoiceItem> timeZoneList=FXCollections.observableArrayList(
		new ChoiceItem("Etc/GMT+12", "GMT-12:00"),
		new ChoiceItem("Etc/GMT+11", "GMT-11:00"),
		new ChoiceItem("Etc/GMT+10", "GMT-10:00"),
		new ChoiceItem("Etc/GMT+9", "GMT-9:00"),
		new ChoiceItem("Etc/GMT+8", "GMT-8:00"),
		new ChoiceItem("Etc/GMT+7", "GMT-7:00"),
		new ChoiceItem("Etc/GMT+6", "GMT-6:00"),
		new ChoiceItem("Etc/GMT+5", "GMT-5:00"),
		new ChoiceItem("Etc/GMT+4", "GMT-4:00"),
		new ChoiceItem("Etc/GMT+3", "GMT-3:00"),
		new ChoiceItem("Etc/GMT+2", "GMT-2:00"),
		new ChoiceItem("Etc/GMT+1", "GMT-1:00"),
		new ChoiceItem("UTC", "GMT 0:00"),
		new ChoiceItem("Etc/GMT-1", "GMT+1:00"),
		new ChoiceItem("Etc/GMT-2", "GMT+2:00"),
		new ChoiceItem("Etc/GMT-3", "GMT+3:00"),
		new ChoiceItem("Etc/GMT-4", "GMT+4:00"),
		new ChoiceItem("Etc/GMT-5", "GMT+5:00"),
		new ChoiceItem("Etc/GMT-6", "GMT+6:00"),
		new ChoiceItem("Etc/GMT-7", "GMT+7:00"),
		new ChoiceItem("Etc/GMT-8", "GMT+8:00"),
		new ChoiceItem("Etc/GMT-9", "GMT+9:00"),
		new ChoiceItem("Etc/GMT-10", "GMT+10:00"),
		new ChoiceItem("Etc/GMT-11", "GMT+11:00"),
		new ChoiceItem("Etc/GMT-12", "GMT+12:00"),
		new ChoiceItem("Etc/GMT-13", "GMT+13:00"),
		new ChoiceItem("Etc/GMT-14", "GMT+14:00"));
	private UserInfo userInfo;
	private File selectedImportFile;
	
	@FXML private StackPane settingsPane;
	@FXML private AnchorPane generalPane;
	@FXML private AnchorPane profilePane;
	@FXML private Label timeZoneLabel;
	@FXML private ChoiceBox timeZoneChoiceBox;
	@FXML private Label nameLabel;
	@FXML private TextField nameInput;
	@FXML private Label descriptionLabel;
	@FXML private TextField descriptionInput;
	@FXML private RadioButton importRadioButton;
	@FXML private RadioButton exportRadioButton;
	@FXML private AnchorPane importPane;
	@FXML private AnchorPane exportPane;
	@FXML private TextField importTextField;
	@FXML private TextField exportTextField;
	
	public SettingsController(){}
	
	public void setApp(App app){
		this.app=app;
	}
	
	public void init(){
		//initFields();
		initBackup();
		Task<Void> initTask=TaskUtil.create(() -> {
			if(!app.DEBUG){
				initProfile();
			}
			Platform.runLater(this::initTimeZone);

			return null;
		});
		this.app.executeAsyncTask(initTask, settingsPane);
	}
	
	private void initFields(){
		GridPane generalGrid=new GridPane();
		generalGrid.add(timeZoneLabel, 0, 0);
		generalGrid.add(timeZoneChoiceBox, 1, 0);
		generalGrid.setHgap(5);
		HBox generalHbox=new HBox(generalGrid);
		generalHbox.setAlignment(Pos.CENTER);
		generalPane.getChildren().add(generalHbox);
		AnchorPane.setRightAnchor(generalHbox, 10.0);
		AnchorPane.setLeftAnchor(generalHbox, 10.0);
		AnchorPane.setTopAnchor(generalHbox, 50.0);
		AnchorPane.setBottomAnchor(generalHbox, 10.0);


		GridPane profileGrid=new GridPane();
		profileGrid.add(nameLabel, 0, 0);
		profileGrid.add(nameInput, 1, 0);
		profileGrid.add(descriptionLabel, 0, 1);
		profileGrid.add(descriptionInput, 1, 1);
		profileGrid.setHgap(5);
		profileGrid.setVgap(5);
		HBox profileHbox=new HBox(profileGrid);
		profileHbox.setAlignment(Pos.CENTER);
		profilePane.getChildren().add(profileHbox);
		AnchorPane.setRightAnchor(profileHbox, 10.0);
		AnchorPane.setLeftAnchor(profileHbox, 10.0);
		AnchorPane.setTopAnchor(profileHbox, 50.0);
		AnchorPane.setBottomAnchor(profileHbox, 10.0);
	}

	private int getTimeZoneIndexByKey(String key){
		for(int i=0;i<this.timeZoneList.size();i++){
			ChoiceItem item=this.timeZoneList.get(i);
			if(item.getKey().equals(key)){
				return i;
			}
		}
		return 12;
	}

	private void initTimeZone(){
		int timeZoneIndex=getTimeZoneIndexByKey(app.getTimeZone());
		timeZoneChoiceBox.getItems().clear();
		timeZoneChoiceBox.getItems().addAll(timeZoneList);
		timeZoneChoiceBox.getSelectionModel().select(timeZoneIndex);
	}
	
	private void initProfile(){
		userInfo=app.getElastosCarrier().getSelfInfo();
		if(userInfo!=null){
			String name=userInfo.getName();
			String description=userInfo.getDescription();
			nameInput.setText(name);
			descriptionInput.setText(description);
		}
	}

	private ChangeListener<Toggle> importExportListener=(observable, oldValue, newValue) -> {
		if(newValue.getUserData().toString().equals("import")){
			importPane.setVisible(true);
			exportPane.setVisible(false);
		}
		else{
			importPane.setVisible(false);
			exportPane.setVisible(true);
		}
	};

	private void initBackup(){
		importPane.setVisible(false);
		exportPane.setVisible(true);
		exportRadioButton.setSelected(true);
		importRadioButton.setUserData("import");
		exportRadioButton.setUserData("export");
		ToggleGroup toggleGroup=importRadioButton.getToggleGroup();
		toggleGroup.selectedToggleProperty().removeListener(importExportListener);
		toggleGroup.selectedToggleProperty().addListener(importExportListener);
		selectedImportFile=null;
		importTextField.setText("");
		exportTextField.setText("");
	}

	@FXML
	private void onUpdateGeneral(){
		Task<Void> updateTask=TaskUtil.create(() -> {
			String timeZone=((ChoiceItem)timeZoneChoiceBox.getSelectionModel().getSelectedItem()).getKey();
			boolean timeZoneSave=app.setTimeZone(timeZone);
			if(timeZoneSave){
				app.showMessageStrip(NotificationType.SUCCESS, "Settings(General) have been saved.", settingsPane);
			}
			else{
				app.showMessageStrip(NotificationType.ERROR, "Sorry, something went wrong saving the settings.", settingsPane);
			}
			return null;
		});
		this.app.executeAsyncTask(updateTask, settingsPane);
	}

	@FXML
	private void onUpdateCarrierProfile(){
		Task<Void> updateTask=TaskUtil.create(() -> {
			String name=nameInput.getText();
			String description=descriptionInput.getText();
			userInfo.setName(name);
			userInfo.setDescription(description);
			if(!app.DEBUG){
				boolean profileSave=app.getElastosCarrier().setSelfInfo(userInfo);
				if(profileSave){
					app.showMessageStrip(NotificationType.SUCCESS, "Settings(Carrier Profile) have been saved.", settingsPane);
				}
				else{
					app.showMessageStrip(NotificationType.ERROR, "Sorry, something went wrong saving the settings.", settingsPane);
				}
			}
			return null;
		});
		this.app.executeAsyncTask(updateTask, settingsPane);
	}

	@FXML
	private void onSelectFile(){
		FileChooser fileChooser=new FileChooser();
		File file=fileChooser.showOpenDialog(app.getPrimaryStage());
		if(file!=null){
			String fileExtension=BackupUtil.getFileExtension(file);
			if(fileExtension.equals("hcb")){
				selectedImportFile=file;
				importTextField.setText(file.getAbsolutePath());
			}
			else{
				selectedImportFile=null;
				importTextField.setText("");
				app.showMessageStrip(NotificationType.WARNING, "Please select a valid backup file.", settingsPane);
			}
		}
	}

	@FXML
	private void onImport(){
		if(selectedImportFile!=null){
			app.stopApp();
			File hyperFilesDir=new File("hyper_files");
			DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
			String tempDirName=LocalDateTime.now().format(formatter)+"_hyper_files";
			File tempHyperFilesDir=new File(tempDirName);
			boolean renameResult=hyperFilesDir.renameTo(tempHyperFilesDir);
			if(renameResult){
				BackupUtil.unzipFile(selectedImportFile.getAbsoluteFile());
				importTextField.setText("");
				app.showMessageStrip(NotificationType.SUCCESS, "Import has been executed.", settingsPane);
				app.startApp();
			}
		}
		else{
			app.showMessageStrip(NotificationType.WARNING, "Please select a valid backup file.", settingsPane);
		}
	}

	@FXML
	private void onExport(){
		String fileName=exportTextField.getText();
		if(fileName.isEmpty()){
			app.showMessageStrip(NotificationType.WARNING, "File name is empty.", settingsPane);
		}
		else{
			try{
				DirectoryChooser directoryChooser=new DirectoryChooser();
				File fileDir=directoryChooser.showDialog(app.getPrimaryStage());
				if(fileDir!=null){
					File backupFile=new File(fileDir, fileName+".hcb");
					File fileToZip=new File("hyper_files");
					FileOutputStream fos=new FileOutputStream(backupFile);
					ZipOutputStream zipOut=new ZipOutputStream(fos);
					BackupUtil.zipFile(fileToZip, fileToZip.getName(), zipOut);
					zipOut.close();
					fos.close();
					exportTextField.setText("");
					app.showMessageStrip(NotificationType.SUCCESS, "Backup file has been exported.", settingsPane);
				}
			}
			catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}
		}
	}
}