//package com.nerubia.ohrm.time;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.utils.URLEncodedUtils;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.preference.PreferenceManager;
//import android.util.Log;
//
//public class AttendanceRecordServerTask extends AsyncTask<Object, Void,String>{
//
//	private final String GET_RECORD_PER_DAY="http://gladys.nerubia.com:3000/ohrmattendancerecord/get_record_per_day.json?";
//	private SharedPreferences.Editor _editor;
//	private String paramString="";
//	
//	private String result = "";
//	private String url = "";
//	private Context context = null;
//	private ProgressDialog pd = null;
//	private int type=0;
//	
//	private HttpClient httpClient = new DefaultHttpClient();
//	private HttpGet httpGet;
//	private HttpResponse response;
//	
//	private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//	@Override
//	protected String doInBackground(Object... params) {
//		type=(Integer)params[0];
//		switch (type) {
//
//		case 1:
//			context = (Context) params[3];	
//			nameValuePairs = new ArrayList<NameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair("date",params[1].toString()));
//			nameValuePairs.add(new BasicNameValuePair("employee_id",params[2].toString()));
//			paramString=URLEncodedUtils.format(nameValuePairs,"utf-8");
//			url=GET_RECORD_PER_DAY+paramString;
//			
//			
//			try {
//				httpGet = new HttpGet(url);
//				response = httpClient.execute(httpGet);
//
//				String responseBody = EntityUtils.toString(response.getEntity());
//				result = responseBody;
//				
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			break;
//		default:
//			break;
//		}
//		Log.d("RESULTS::::",result);
//		return result;
//	}
//	@Override
//	protected void onPostExecute(String result) {
//		super.onPostExecute(result);
//		switch (type) {
//		case 1:			
//			
//			break;
//		default:
//			break;
//		}
//	}
//
//}
