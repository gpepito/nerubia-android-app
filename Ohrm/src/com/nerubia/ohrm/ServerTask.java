package com.nerubia.ohrm;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.nerubia.ohrm.fragments.PopUpDialogFragment;
import com.nerubia.ohrm.time.AttendancePunchIn;
import com.nerubia.ohrm.util.Encryption;
import com.nerubia.ohrm.util.OhrmTimeZone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ServerTask extends AsyncTask<Object, Void, String> {
	private Encryption enc = new Encryption();
	public final String LOGIN_URL = "http://gladys.nerubia.com:3000/login.json?";
	public final String OHRM_ATTENDANCE_RECORD_GET_RECORD_URL = "http://gladys.nerubia.com:3000/ohrmattendancerecord/get_records.json?";
	public final String OHRM_ATTENDANCE_RECORD_GET_TIME_URL = "http://gladys.nerubia.com:3000/ohrmattendancerecord/get_time.json";
	public final String OHRM_ATTENDANCE_RECORD_PUNCH_OUT="http://gladys.nerubia.com:3000/ohrmattendancerecord/punch_out.json?";
	public final String OHRM_ATTENDANCE_RECORD_PUNCH_IN="http://gladys.nerubia.com:3000/ohrmattendancerecord/punch_in.json?";
	
	private String result = "";
	private String url = "";
	private Context context = null;
	private PopUpDialogFragment pd;
	private int type=0;
	private SharedPreferences.Editor _editor;
	private String paramString="";
	
	private HttpClient httpClient = new DefaultHttpClient();
	private HttpGet httpGet;
	private HttpResponse response;
	
	private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	
	
	@Override
	protected String doInBackground(Object... params) {
		
		type=(Integer)params[0];
		switch (type) {

		case 1:
			nameValuePairs = new ArrayList<NameValuePair>();
			pd = (PopUpDialogFragment) params[3];
			context = (Context) params[4];						
			_editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			
			enc.set_encrypt(params[2].toString());
			try {
				nameValuePairs.add(new BasicNameValuePair("user_name",params[1].toString()));
				nameValuePairs.add(new BasicNameValuePair("password", enc.get_encrypt()));	
				
				// using httpGet
				paramString = URLEncodedUtils.format(nameValuePairs,"utf-8");
				url = LOGIN_URL + paramString;

				httpGet = new HttpGet(url);
				response = httpClient.execute(httpGet);

				String responseBody = EntityUtils.toString(response.getEntity());
				result = responseBody;

			} catch (Exception e) {
				Log.e("login Exception:::::::::", e.toString());
			}
			break;
		case 2:
			context = (Context) params[2];	
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("emp_id",params[1].toString()));
			paramString=URLEncodedUtils.format(nameValuePairs,"utf-8");
			url=OHRM_ATTENDANCE_RECORD_GET_RECORD_URL+paramString;
			
			
			try {
				httpGet = new HttpGet(url);
				response = httpClient.execute(httpGet);

				String responseBody = EntityUtils.toString(response.getEntity());
				result = responseBody;
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			break;
		case 3:			
			context=(Context)params[1];
			_editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			try {
				url=OHRM_ATTENDANCE_RECORD_GET_TIME_URL;
				httpGet = new HttpGet(url);
				response = httpClient.execute(httpGet);

				String responseBody = EntityUtils.toString(response.getEntity());
				result = responseBody;
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case 4:	
			context=(Context)params[6];
			_editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			pd = (PopUpDialogFragment) params[5];
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("emp_id",params[1].toString()));
			nameValuePairs.add(new BasicNameValuePair("punch_out_user_time",params[2].toString()));
			nameValuePairs.add(new BasicNameValuePair("punch_out_note",params[3].toString()));
			nameValuePairs.add(new BasicNameValuePair("state",params[4].toString()));
			paramString=URLEncodedUtils.format(nameValuePairs,"utf-8");
			try {
				url=OHRM_ATTENDANCE_RECORD_PUNCH_OUT+paramString;
				Log.d("PUNCHED OUT url::",url);
				httpGet=new HttpGet(url);
				response=httpClient.execute(httpGet);
				String responseBody = EntityUtils.toString(response.getEntity());
				result = responseBody;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case 5:
			context=(Context)params[7];
			_editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			pd = (PopUpDialogFragment) params[6];
			nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("employee_id",params[1].toString()));
			nameValuePairs.add(new BasicNameValuePair("punch_in_note",params[2].toString()));
			nameValuePairs.add(new BasicNameValuePair("punch_in_time_offset",params[3].toString()));
			nameValuePairs.add(new BasicNameValuePair("punch_in_user_time",params[4].toString()));
			nameValuePairs.add(new BasicNameValuePair("state",params[5].toString()));
			paramString=URLEncodedUtils.format(nameValuePairs,"utf-8");
			
			try {
				url=OHRM_ATTENDANCE_RECORD_PUNCH_IN+paramString;
				httpGet=new HttpGet(url);
				response=httpClient.execute(httpGet);
				String responseBody = EntityUtils.toString(response.getEntity());
				result = responseBody;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		Log.d("RESULTS::::",result);
		Log.d("successful????",String.valueOf(result.toString().equals("true")));
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);		
		switch (type) {
		case 1:
			pd.dismissProgressDialog();
			if (!result.equals("null")) {
					 _editor.putBoolean("LOGGED_IN", true).commit();
					 _editor.putBoolean("PAUSE_TIMER", false).commit();
						Intent i = new Intent(context,MainActivity.class);
						try {
							_editor.putString("USER_RECORD",result).commit();
							JSONObject jsonObject=new JSONObject(result.replace("\"","\'"));	
							_editor.putString("emp_id",jsonObject.getString("emp_number")).commit();
							
							i.putExtra("LOGGED_IN", true);
							i.putExtra("emp_id",jsonObject.getString("emp_number"));
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i);
						} catch (JSONException e) {
							e.printStackTrace();
						}
			} else {
				Toast.makeText(context,"Invalid Credentials", Toast.LENGTH_LONG).show();
			}
			break;
		case 2:

			Intent i = new Intent(context,AttendancePunchIn.class);
			i.putExtra("ATTENDANCE_RECORDS",(result!="null"?result.replace("\"","\'"):result));
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
			((Activity)context).finish();
			break;
		case 3:
			OhrmTimeZone tz=new OhrmTimeZone();
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(result.replace("\"","\'"));
				tz.setCurrentTime(jsonObject.getString("time"));
				Log.d("TIME FROM SERVER:",jsonObject.getString("time"));
				_editor.putString("CURRENT_TIME",tz.getCurrentTime().toString()).commit();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case 4:
			_editor.putBoolean("PAUSE_TIMER",false).commit();
			pd.dismissProgressDialog();
			Log.d("onPostExecute","PUNCHED OUT");
			this.cancel(true);
			break;
		case 5:
			_editor.putBoolean("PAUSE_TIMER",false).commit();
			pd.dismissProgressDialog();
			Log.d("onPostExecute","PUNCHED_IN");
			this.cancel(true);
			break;
		}
		
	}

}
