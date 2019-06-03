package com.hyper.connect.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.util.TimeZone;
import java.io.IOException;
import java.text.ParseException;


public class CustomUtil{
	public CustomUtil(){}

	public static File getDirectoryByName(String dirName){
		String hyperFilesDirName="hyper_files";
		File hyperFilesDir=new File(hyperFilesDirName);
		if(!hyperFilesDir.exists()){
			hyperFilesDir.mkdir();
		}
		File dir=new File(hyperFilesDir, dirName);
		if(!dir.exists()){
			dir.mkdir();
		}
		return dir;
	}

	public static String getCurrentDateTime(){
		DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String stringDatetime=dateFormat.format(new Date());
		return stringDatetime;
	}

	public static String getCurrentDateTimeByPattern(String pattern){
		DateFormat dateFormat=new SimpleDateFormat(pattern);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String stringDatetime=dateFormat.format(new Date());
		return stringDatetime;
	}
	
	public static String getDateTimeByTimeZone(String dateTime, String timeZone){
		DateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		long utcLong=0;
		try{
			Date utcDate=dateFormat.parse(dateTime);
			utcLong=utcDate.getTime();
		}
		catch(ParseException pe){}
		dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		String newDateTime=dateFormat.format(new Date(utcLong));
		return newDateTime;
	}

	public static String getCurrentDateTimeByPatternAndTimeZone(String pattern, String timeZone){
		String dateTime=getCurrentDateTimeByPattern(pattern);
		DateFormat dateFormat=new SimpleDateFormat(pattern);
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		long utcLong=0;
		try{
			Date utcDate=dateFormat.parse(dateTime);
			utcLong=utcDate.getTime();
		}
		catch(ParseException pe){}
		dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
		String newDateTime=dateFormat.format(new Date(utcLong));
		return newDateTime;
	}

	public static String getRandomGlobalEventId(){
		int length=12;
		boolean useLetters=true;
		boolean useNumbers=true;
		String globalEventId=RandomStringUtils.random(length, useLetters, useNumbers);
		return globalEventId;
	}
}