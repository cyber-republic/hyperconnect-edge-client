package com.hyper.connect.controller;

import com.hyper.connect.App;
import com.hyper.connect.model.Sensor;
import com.hyper.connect.model.Attribute;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import javafx.scene.control.TextArea;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.concurrent.Task;
import javafx.application.Platform;

import com.google.gson.JsonObject;



public class ScriptController{
	private App app;
	private Sensor sensor;
	private Attribute attribute;
	private String tempScriptContent;
	private boolean isInitialized=false;
	
	@FXML private StackPane scriptPane;
	@FXML private Text scriptTitleText;
	@FXML private Label parameterLabel;
	@FXML private TextField parameterTextField;
	@FXML private ChoiceBox templateChoiceBox;
	@FXML private TextArea codeTextArea;
	@FXML private Button editButton;
	@FXML private Button backButton;
	@FXML private Button validateButton;
	@FXML private Button saveButton;
	@FXML private Button cancelButton;
	
	public ScriptController(){}
	
	public void setApp(App app){
		this.app=app;
	}
	
	public void init(Sensor sensor, Attribute attribute){
		this.sensor=sensor;
		this.attribute=attribute;
		
		Task initTask=new Task<Void>(){
			@Override
			public Void call(){
				String scriptName=Integer.toString(attribute.getId());
				tempScriptContent=app.getScriptManager().getPythonScript(scriptName);
				
				Platform.runLater(() -> {
					scriptTitleText.setText("Script of Attribute '"+attribute.getName()+" ("+attribute.getId()+")'");
					if(attribute.getDirection().equals("input")){
						parameterLabel.setVisible(false);
						parameterTextField.setVisible(false);
					}
					else{
						parameterLabel.setVisible(true);
						parameterTextField.setVisible(true);
					}
					
					codeTextArea.textProperty().removeListener(codeListener);
					codeTextArea.setText(tempScriptContent);
					
					templateChoiceBox.getSelectionModel().selectedIndexProperty().removeListener(templateListener);
					templateChoiceBox.getItems().clear();
					if(attribute.getDirection().equals("input")){
						templateChoiceBox.getItems().addAll("-- Load Template --", "Basic", "randomGenerator (Test)");
					}
					else{
						templateChoiceBox.getItems().addAll("-- Load Template --", "Basic with parameter", "writeToFile (Test)");
					}
					templateChoiceBox.getSelectionModel().selectFirst();
					
					
					codeTextArea.textProperty().addListener(codeListener);
					
					templateChoiceBox.getSelectionModel().selectedIndexProperty().addListener(templateListener);
				});
				
				return null;
			}
		};
		this.app.executeAsyncTask(initTask, scriptPane);
	}
	
	private final ChangeListener<String> codeListener=new ChangeListener<String>(){
		@Override
		public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue){
			if(!saveButton.isDisable()){
				saveButton.setDisable(true);
				if(!tempScriptContent.equals(newValue)){
					app.showMessageStrip("Warning", "Changes to the script have been made. Please validate it again.", scriptPane);
				}
			}
		}
	};
	
	private final ChangeListener<Number> templateListener=new ChangeListener<Number>(){
		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2){
			String value=(String)templateChoiceBox.getItems().get((Integer)number2);
			
			int index=(Integer)number2;
			if(index!=0){
				String attributeType=attribute.getType();
				if(value.equals("Basic")){
					if(attributeType.equals("string")){
						codeTextArea.setText("#your value\nvalue=\"yourStringValue\"\nprint(value)");
					}
					else if(attributeType.equals("boolean")){
						codeTextArea.setText("#your value\nvalue=True\nprint(value)");
					}
					else if(attributeType.equals("integer")){
						codeTextArea.setText("#your value\nvalue=3\nprint(value)");
					}
					else if(attributeType.equals("double")){
						codeTextArea.setText("#your value\nvalue=3.14\nprint(value)");
					}
				}
				else if(value.equals("randomGenerator (Test)")){
					if(attributeType.equals("boolean")){
						codeTextArea.setText("from random import randint\nvalue=True\nrandomInt=randint(0, 1)\nif randomInt==0:\n\tvalue=False\nprint(value)");
					}
					else{
						codeTextArea.setText("from random import randint\nprint(randint(0, 9))");
					}
				}
				else if(value.equals("Basic with parameter")){
					codeTextArea.setText("import sys\nparameter=sys.argv[1]\n\n#your code here\n\nprint(\"DONE\")");
				}
				else if(value.equals("writeToFile (Test)")){
					codeTextArea.setText("import sys\nparameter=sys.argv[1]\n\nfile=open(\"test.txt\", \"w\")\nfile.write(parameter)\nfile.close()\n\nprint(\"DONE\")");
				}
			}
		}
	};
	
	@FXML
    private void onButtonEdit(){
		this.editButton.setVisible(false);
		this.backButton.setVisible(false);
		this.validateButton.setVisible(true);
		this.saveButton.setVisible(true);
		this.saveButton.setDisable(true);
		this.cancelButton.setVisible(true);
		this.parameterLabel.setDisable(false);
		this.parameterTextField.setDisable(false);
		this.templateChoiceBox.setDisable(false);
		this.codeTextArea.setEditable(true);
	}
	
	@FXML
    private void onButtonBack(){
		this.app.setSensorConfigureLayout(this.sensor);
	}
	
	@FXML
    private void onButtonValidate(){
		Task validateTask=new Task<Void>(){
			@Override
			public Void call(){
				String response="";
				String message="";
				String scriptName=attribute.getId()+"_temp";
				String scriptContent=codeTextArea.getText();
				if(attribute.getDirection().equals("input")){
					JsonObject resultObject=app.getScriptManager().validatePythonScript(scriptName, scriptContent, attribute.getType());
					response=resultObject.get("response").getAsString();
					message=resultObject.get("message").getAsString();
				}
				else{
					String parameter=parameterTextField.getText();
					if(parameter.equals("")){
						response="Warning";
						message="Please enter an input parameter.";
					}
					else{
						JsonObject resultObject=app.getScriptManager().validatePythonScriptWithParameter(scriptName, scriptContent, attribute.getType(), parameter);
						response=resultObject.get("response").getAsString();
						message=resultObject.get("message").getAsString();
					}
				}
				
				if(response.equals("Success")){
					saveButton.setDisable(false);
				}
				else{
					saveButton.setDisable(true);
				}
				app.showMessageStrip(response, message, scriptPane);
				return null;
			}
		};
		this.app.executeAsyncTask(validateTask, scriptPane);
	}
	
	@FXML
    private void onButtonSave(){
		Task saveTask=new Task<Void>(){
			@Override
			public Void call(){
				String scriptName=Integer.toString(attribute.getId());
				String scriptContent=codeTextArea.getText();
				app.getScriptManager().savePythonScript(scriptName, scriptContent);
				attribute.setScriptState("valid");
				boolean updateResult=app.getDatabase().updateAttribute(attribute);
				if(updateResult){
					app.showMessageStrip("Success", "The script of attribute '"+attribute.getName()+" ("+attribute.getId()+")' has been saved.", scriptPane);
					tempScriptContent=scriptContent;
					Platform.runLater(() -> onButtonCancel());
				}
				else{
					app.showMessageStripAndSave("Error", "Device", "Sorry, something went wrong saving the script of attribute '"+attribute.getName()+" ("+attribute.getId()+")'.", scriptPane);
				}
				return null;
			}
		};
		this.app.executeAsyncTask(saveTask, scriptPane);
	}
	
	@FXML
    private void onButtonCancel(){
		this.editButton.setVisible(true);
		this.backButton.setVisible(true);
		this.validateButton.setVisible(false);
		this.saveButton.setVisible(false);
		this.saveButton.setDisable(true);
		this.cancelButton.setVisible(false);
		this.parameterLabel.setDisable(true);
		this.parameterTextField.setDisable(true);
		this.parameterTextField.setText("");
		this.templateChoiceBox.setDisable(true);
		this.codeTextArea.setEditable(false);
		this.codeTextArea.setText(this.tempScriptContent);
		this.templateChoiceBox.getSelectionModel().selectFirst();
	}
}