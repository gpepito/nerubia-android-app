package com.nerubia.ohrm.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.Period;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class OhrmTimeZone {

	private Date _date=null;		
	private Date _time=null;
	
	private DateFormat currentTimeFormatter=new SimpleDateFormat("HH:mm");
	private DateFormat serverformatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private DateFormat serverCurrentformatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	private DateFormat userDateFormatter=new SimpleDateFormat("yyyy-MM-dd");
	private DateFormat userFormatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String userDate="";
	private String currentTime="";	
	private String userPunchTime="";
	private String serverFormatTime="";
	private Period timeDiff;
	
	//return date in milliseconds
	public Period getTimeDiff() {
		return this.timeDiff;
	}

	public void setTimeDiff(Date prev, Date current) {
		DateTime mprev=new DateTime(prev.getTime());
		DateTime mcurrent=new DateTime(current.getTime());
		Period p=new Period(mprev, mcurrent);
		this.timeDiff=p;
	}
	public String getServerFormatTime() {
		return serverFormatTime;
	}
	public String getCurrentTime() {
		return currentTime;
	}
	public String getUserDate() {
		return userDate;
	}
	public String getUserPunchTime() {
		return userPunchTime;
	}

	public void setCurrentTime(String time) {
		try {
			_time=serverCurrentformatter.parse(time); 
			currentTimeFormatter.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
			currentTime=currentTimeFormatter.format(_time);
			userDate=userDateFormatter.format(_time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void setUserTime(String date,String time,int tz){
		switch (tz) {		
		//GMT+8
		case 8:					
			try {
				_date=serverformatter.parse(date);
//				userFormatter.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
				userPunchTime=userFormatter.format(_date);
				userDate=userDateFormatter.format(_date);
			} catch (ParseException e) {
				Log.d("parseException::::",e.toString());
			}
			break;

		default:
			Log.d("went here","...");
			break;
		}
	}
	public Date getDate(){
		return this._date;
	}

}
