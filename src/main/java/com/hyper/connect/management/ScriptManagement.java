package com.hyper.connect.management;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.google.gson.JsonObject;
import com.hyper.connect.model.enums.AttributeType;
import com.hyper.connect.util.CustomUtil;


public class ScriptManagement{
	private String pythonDirPath;
	
	public ScriptManagement(){
		this.pythonDirPath=CustomUtil.getDirectoryByName("python").getPath();
		String osType=System.getProperty("os.name");
		if(osType.equals("Linux")){
			this.pythonDirPath+="/";
		}
		else{
			this.pythonDirPath+="\\";
		}
	}
	
	public String getPythonScript(String scriptName){
		String filename=scriptName+".py";
		String content=this.getContentFromFile(this.pythonDirPath+filename).trim();
		return content;
	}
	
	public JsonObject validatePythonScript(String scriptName, String scriptContent, AttributeType attributeType){
		String filename=this.createPythonFile(scriptName, scriptContent);
		JsonObject jsonObject=this.executePythonFile(filename);
		
		String stringValue=jsonObject.get("result").getAsString();
		String error=jsonObject.get("error").getAsString();
		if(error.equals("")){
			boolean typeValid=this.checkValueWithType(stringValue, attributeType);
			if(typeValid){
				jsonObject.addProperty("response", "Success");
				jsonObject.addProperty("message", "The script has been successfully validated. The measured test value was "+stringValue);
			}
			else{
				jsonObject.addProperty("response", "Error");
				jsonObject.addProperty("message", "The measured test value does not match the attribute type "+attributeType+".");
			}
		}
		else{
			jsonObject.addProperty("response", "Error");
			jsonObject.addProperty("message", error);
		}
		
		return jsonObject;
	}
	
	public JsonObject validatePythonScriptWithParameter(String scriptName, String scriptContent, AttributeType attributeType, String parameter){
		boolean isParameterValid=this.checkValueWithType(parameter, attributeType);
		JsonObject jsonObject=new JsonObject();
		if(isParameterValid){
			String filename=this.createPythonFile(scriptName, scriptContent);
			jsonObject=this.executePythonFileWithParameter(filename, parameter);
			
			String stringValue=jsonObject.get("result").getAsString();
			String error=jsonObject.get("error").getAsString();
			if(error.equals("")){
				boolean isScriptDone=this.checkScriptDone(stringValue);
				if(isScriptDone){
					jsonObject.addProperty("response", "Success");
					jsonObject.addProperty("message", "The script has been successfully validated.");
				}
				else{
					jsonObject.addProperty("response", "Error");
					jsonObject.addProperty("message", "The code: print(\"DONE\") is missing from the end of the script.");
				}
			}
			else{
				jsonObject.addProperty("response", "Error");
				jsonObject.addProperty("message", error);
			}
		}
		else{
			jsonObject.addProperty("response", "Warning");
			jsonObject.addProperty("message", "The 'Test Parameter' value does not match the attribute type "+attributeType+".");
		}
		
		return jsonObject;
	}
	
	public void savePythonScript(String scriptName, String scriptContent){
		this.createPythonFile(scriptName, scriptContent);
	}
	
	public JsonObject executePythonFile(String filename){
		JsonObject jsonObject=new JsonObject();
		try{
			String command="python "+this.pythonDirPath+filename;
			Process process=Runtime.getRuntime().exec(command);
			InputStream resultStream=process.getInputStream();
			InputStream errorStream=process.getErrorStream();
			
			String result=this.getStringFromInputStream(resultStream);
			String error=this.getStringFromInputStream(errorStream);
			
			jsonObject.addProperty("result", result);
			jsonObject.addProperty("error", error);
		}
		catch(IOException e){}
		return jsonObject;
	}
	
	public JsonObject executePythonFileWithParameter(String filename, String parameter){
		JsonObject jsonObject=new JsonObject();
		try{
			String command="python "+this.pythonDirPath+filename+" "+parameter;
			Process process=Runtime.getRuntime().exec(command);
			InputStream resultStream=process.getInputStream();
			InputStream errorStream=process.getErrorStream();
			
			String result=this.getStringFromInputStream(resultStream);
			String error=this.getStringFromInputStream(errorStream);
			
			jsonObject.addProperty("result", result);
			jsonObject.addProperty("error", error);
		}
		catch(IOException e){}
		return jsonObject;
	}
	
	private String createPythonFile(String scriptName, String scriptContent){
		String filename=scriptName+".py";
		BufferedWriter bw=null;
		FileWriter fw=null;
		try{
			fw=new FileWriter(this.pythonDirPath+filename);
			bw=new BufferedWriter(fw);
			bw.write(scriptContent);
		}
		catch(IOException e){} 
		finally{
			try{
				if(bw!=null){
					bw.close();
				}
				if(fw!=null){
					fw.close();
				}
			}
			catch(IOException ex){}
		}
		return filename;
	}
	
	private String getContentFromFile(String filename){
		String result="";
		BufferedReader br=null;
		FileReader fr=null;
		try{
			fr=new FileReader(filename);
			br=new BufferedReader(fr);
			String sCurrentLine;
			while((sCurrentLine=br.readLine())!=null){
				result+=sCurrentLine+"\n";
			}
		}
		catch(IOException e){}
		finally{
			try{
				if(br!=null){
					br.close();
				}
				if(fr!=null){
					fr.close();
				}
			}
			catch(IOException ex){}
		}
		return result;
	}
	
	private String getStringFromInputStream(InputStream is){
		BufferedReader br=null;
		StringBuilder sb=new StringBuilder();
		String line;
		try{
			br=new BufferedReader(new InputStreamReader(is));
			while((line=br.readLine())!=null){
				sb.append(line);
			}
		}
		catch(IOException e){}
		finally{
			if(br!=null){
				try{
					br.close();
				}
				catch(IOException e){}
			}
		}
		return sb.toString();
	}
	
	private boolean checkValueWithType(String stringValue, AttributeType attributeType){
		if(attributeType==AttributeType.STRING){
			return true;
		}
		else if(attributeType==AttributeType.BOOLEAN){
			if(stringValue.equals("True") || stringValue.equals("False")){
				return true;
			}
			else{
				return false;
			}
		}
		else if(attributeType==AttributeType.INTEGER){
			try{
				int value=Integer.parseInt(stringValue);
			}
			catch(NumberFormatException nfe){
				return false;
			}
		}
		else if(attributeType==AttributeType.DOUBLE){
			try{
				double value=Double.parseDouble(stringValue);
			}
			catch(NumberFormatException nfe){
				return false;
			}
		}
		return true;
	}

	private boolean checkScriptDone(String stringValue){
		if(stringValue.equals("DONE")){
			return true;
		}
		else{
			return false;
		}
	}
}