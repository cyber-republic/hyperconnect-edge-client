package com.hyper.connect.controller;

import com.hyper.connect.App;
import com.hyper.connect.model.enums.ControllerState;
import com.hyper.connect.util.QRCodeUtil;
import com.hyper.connect.model.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.io.IOException;


public class ControllersController{
	private App app;
	private ObservableList<Controller> controllerObservableList;
	
	@FXML private StackPane controllersPane;
	@FXML private ImageView qrCodeImageView;
	@FXML private Text addressText;
	@FXML private Text userIdText;
	@FXML private TableView controllerListTableView;
	@FXML private TableColumn idColumn;
	@FXML private TableColumn userIdColumn;
	@FXML private TableColumn stateColumn;
	@FXML private TableColumn actionsColumn;
	
	public ControllersController(){}
	
	public void setApp(App app){
		this.app=app;
	}
	
	public void init(String address, String userId){
		this.addressText.setText(address);
		this.userIdText.setText(userId);
		Image qrCode=QRCodeUtil.getQRCodeImage(address, 150, 150);
		qrCodeImageView.setImage(qrCode);
		this.refreshControllerList();
	}
	
	public void refreshControllerList(){
		/*Task refreshTask=new Task<Void>(){
			@Override
			public Void call(){*/
				ArrayList<Controller> controllerList=app.getDatabase().getControllerList();
				controllerObservableList=FXCollections.observableArrayList(controllerList);
				idColumn.setCellValueFactory(new PropertyValueFactory("id"));
				userIdColumn.setCellValueFactory(new PropertyValueFactory("userId"));
				
				Callback<TableColumn<Controller, String>, TableCell<Controller, String>> stateCellFactory=new Callback<TableColumn<Controller, String>, TableCell<Controller, String>>(){
					@Override
					public TableCell call(final TableColumn<Controller, String> param){
						final TableCell<Controller, String> cell=new TableCell<Controller, String>(){
							@Override
							public void updateItem(String item, boolean empty){
								super.updateItem(item, empty);
								if(empty){
									setGraphic(null);
									setText(null);
								}
								else{
									Controller controller=getTableView().getItems().get(getIndex());
									ControllerState state=controller.getState();
									Text stateText=new Text(state.toString());
									if(state==ControllerState.ACTIVE){
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
				
				Callback<TableColumn<Controller, String>, TableCell<Controller, String>> actionsCellFactory=new Callback<TableColumn<Controller, String>, TableCell<Controller, String>>(){
					@Override
					public TableCell call(final TableColumn<Controller, String> param){
						final TableCell<Controller, String> cell=new TableCell<Controller, String>(){
							final Button acceptButton=new Button("Accept");
							final Button deleteButton=new Button();
							
							@Override
							public void updateItem(String item, boolean empty){
								super.updateItem(item, empty);
								
								acceptButton.getStyleClass().add("accept_button");
								acceptButton.setCursor(Cursor.HAND);
								deleteButton.getStyleClass().add("simple_button");
								deleteButton.setCursor(Cursor.HAND);
								
								if(empty){
									setGraphic(null);
									setText(null);
								}
								else{
									Controller controller=getTableView().getItems().get(getIndex());
									
									ImageView acceptImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_done_white_24.png")));
									acceptImageView.setFitWidth(18);
									acceptImageView.setFitHeight(18);
									acceptButton.setGraphic(acceptImageView);
									acceptButton.setOnAction(acceptEvent -> {
										Task acceptTask=new Task<Void>(){
											@Override
											public Void call(){
												boolean acceptResult=app.getElastosCarrier().acceptFriend(controller.getUserId());
												if(acceptResult){
													controller.setState(ControllerState.ACTIVE);
													boolean updateResult=app.getDatabase().updateController(controller);
													if(updateResult){
														controllerListTableView.getItems().set(getIndex(), controller);
														app.showMessageStripAndSave("Success", "Controller", "Controller '"+controller.getUserId()+"' has been accepted.", controllersPane);
													}
													else{
														app.showMessageStripAndSave("Error", "Controller", "Sorry, something went wrong accepting the controller '"+controller.getUserId()+"'.", controllersPane);
													}
												}
												else{
													app.showMessageStripAndSave("Error", "Controller", "Sorry, something went wrong accepting the controller '"+controller.getUserId()+"'.", controllersPane);
												}
												return null;
											}
										};
										app.executeAsyncTask(acceptTask, controllersPane);
									});
									
									ImageView deleteImageView=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("icons/baseline_delete_white_24.png")));
									deleteImageView.setFitWidth(18);
									deleteImageView.setFitHeight(18);
									deleteButton.setGraphic(deleteImageView);
									deleteButton.setOnAction(deleteEvent -> {
										try{
											Region veil=new Region();
											veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");

											FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/dialog_delete_controller.fxml"));
											AnchorPane deleteDialogContent=(AnchorPane)loader.load();

											AnchorPane titelPane=(AnchorPane)deleteDialogContent.lookup("#titelPane");
											Text dialogText=(Text)deleteDialogContent.lookup("#dialogText");
											HBox actionsHbox=(HBox)deleteDialogContent.lookup("#actionsHbox");
											double dialogHeight=dialogText.getBoundsInLocal().getHeight()+titelPane.getPrefHeight()+actionsHbox.getPrefHeight()+30;
											deleteDialogContent.setPrefHeight(dialogHeight);

											Button cancelButton=(Button)deleteDialogContent.lookup("#cancelButton");
											cancelButton.setOnAction(cancelEvent -> {
												app.closeDialog(veil, deleteDialogContent, controllersPane);
											});
											Button confirmButton=(Button)deleteDialogContent.lookup("#confirmButton");
											confirmButton.setOnAction(confirmEvent -> {
												Task deleteTask=new Task<Void>(){
													@Override
													public Void call(){
														boolean carrierDeleteResult=true;
														if(controller.getState().equals("active")){
															carrierDeleteResult=app.getElastosCarrier().removeFriend(controller.getUserId());
														}
														if(carrierDeleteResult){
															boolean deleteResult=app.getDatabase().deleteControllerByControllerId(controller.getId());
															if(deleteResult){
																controllerObservableList.removeAll(controller);
																app.showMessageStripAndSave("Success", "Controller", "Controller '"+controller.getUserId()+"' has been deleted.", controllersPane);
															}
															else{
																app.showMessageStripAndSave("Error", "Controller", "Sorry, something went wrong deleting the controller '"+controller.getUserId()+"'.", controllersPane);
															}
														}
														else{
															app.showMessageStripAndSave("Error", "Controller", "Sorry, something went wrong deleting the controller '"+controller.getUserId()+"'.", controllersPane);
														}
														return null;
													}
												};
												app.executeAsyncTask(deleteTask, controllersPane);
												app.closeDialog(veil, deleteDialogContent, controllersPane);
											});

											app.showDialog(veil, deleteDialogContent, controllersPane);
										}
										catch(IOException ioe){}
									});
									
									HBox hbox=new HBox(deleteButton);
									if(controller.getState()==ControllerState.PENDING){
										hbox=new HBox(acceptButton, deleteButton);
										hbox.setSpacing(10);
									}
									
									setGraphic(hbox);
									setText(null);
								}
							}
						};
						return cell;
					}
				};
				
				stateColumn.setCellFactory(stateCellFactory);
				actionsColumn.setCellFactory(actionsCellFactory);
				
				controllerListTableView.setItems(controllerObservableList);
				
				/*return null;
			}
		};
		this.app.executeAsyncTask(refreshTask, controllersPane);*/
	}
}