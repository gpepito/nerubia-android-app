package com.nerubia.ohrm.leave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nerubia.ohrm.R;
import com.nerubia.ohrm.fragments.DateTimePickerDialogFragment;
import com.nerubia.ohrm.fragments.PopUpDialogFragment;
import com.nerubia.ohrm.util.OhrmTimeZone;
import com.nerubia.ohrm.util.OhrmTimeZone;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class LeaveApply extends Activity {

	private static final int SEARCH_ALL_LEAVE_TYPES=1;
	private static final int SEARCH_BY_LEAVE_TYPE=2;
	private static final int UPDATE_LEAVE_ENTITLEMENT=3;
	private static final String SEARCH_ALL_LEAVE_TYPES_URL="http://gladys.nerubia.com:3000/leave/apply/search_all.json";
	private static final String SEARCH_BY_LEAVE_TYPE_URL="http://gladys.nerubia.com:3000/leave/apply/search_by_leave_type.json?";
	private static final String UPDATE_LEAVE_ENTITLEMENT_URL="http://gladys.nerubia.com:3000/leave/apply/update_leave_entitlement.json?";	
	
	private EditText fromDate;
	private EditText toDate;
	private EditText fromTime;
	private EditText toTime;
	private EditText leaveType;
	private EditText leaveBal;
	private EditText duration;
	private Button btnApply;	
	private LinearLayout rel;
	
	private int empId=-1;
	private int leaveId=-1;
	private double totalDeduct=0.0;
	private double daysUsed=0.0;
	private SparseArray<String> saleaves=new SparseArray<String>();
	private OhrmTimeZone otz=new OhrmTimeZone();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leave_apply);
		createNavigationTabs();
		//get empId after log in, for now im using static id for testing.
		empId=24;
		performAsyncTask(1);

	}

	private void addAllListeners() {
		duration=(EditText)findViewById(R.id.editDuration);
		rel=(LinearLayout)findViewById(R.id.relTimeDuration);
		
		leaveBal=(EditText)findViewById(R.id.editLeaveBal);
		leaveType=(EditText)findViewById(R.id.editLeaveType);
		leaveType.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				PopUpDialogFragment dialog=new PopUpDialogFragment(1,saleaves);
				dialog.show(getFragmentManager(), "LeaveType");
			}
		});
		
		fromDate = (EditText) findViewById(R.id.editFromDate);
		fromDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DateTimePickerDialogFragment datePicker = new DateTimePickerDialogFragment(
						fromDate.getId(), 2);
				datePicker.show(getFragmentManager(), "datePicker");
			}
		});

		toDate = (EditText) findViewById(R.id.editToDate);
		toDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DateTimePickerDialogFragment datePicker = new DateTimePickerDialogFragment(
						toDate.getId(), 2);
				datePicker.show(getFragmentManager(), "datePicker");
			}
		});

		fromTime=(EditText)findViewById(R.id.editFromTime);
		fromTime.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DateTimePickerDialogFragment timePicker = new DateTimePickerDialogFragment(
						fromTime.getId(), 1);
				timePicker.show(getFragmentManager(), "timePicker");
			}
		});
		
		toTime=(EditText)findViewById(R.id.editToTime);
		toTime.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DateTimePickerDialogFragment timePicker = new DateTimePickerDialogFragment(
						toTime.getId(), 1);
				timePicker.show(getFragmentManager(), "timePicker");
			}
		});
		
		btnApply=(Button)findViewById(R.id.btnApply);
		btnApply.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				totalDeduct=daysUsed;
				if(validateData()){
					if(rel.getVisibility()==View.VISIBLE){
						//compute deduct balance by hours
						totalDeduct+=(Double.parseDouble(duration.getText().toString().trim())/8.0);
					}else{
						//compute deduct balance by day
						otz.setDateInterval(fromDate.getText().toString().trim(), toDate.getText().toString().trim());
						totalDeduct+=otz.getDateInterval().toPeriod().getDays()+1.00;
					}
				}
				Log.d("total used days:",String.valueOf(totalDeduct));
				performAsyncTask(3);
			}
		});
	}

	
	private void createNavigationTabs() {
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {

			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {

			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {

			}
		};
		actionBar.setTitle(R.string.leave);
		actionBar.addTab(actionBar.newTab().setText(R.string.apply)
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.my_leave)
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.entitlements)
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.reports)
				.setTabListener(tabListener));
		actionBar.setSelectedNavigationItem(0);
	}
	
	public class LeaveApplyServerTask extends AsyncTask<Object, Void, String>{
		
		private HttpClient httpClient = new DefaultHttpClient();
		private HttpGet httpGet;
		private HttpResponse response;
		
		private String result="";
		private String url = "";
		private int type;
		private String paramString="";
		
		JSONArray jsonArray;
		JSONObject jsonObject=new JSONObject();
		private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		@Override
		protected String doInBackground(Object... params) {
			type=(Integer)params[0];
			switch (type) {
			case SEARCH_ALL_LEAVE_TYPES:	
				
				try {
					url=SEARCH_ALL_LEAVE_TYPES_URL;
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

			case SEARCH_BY_LEAVE_TYPE:
				try {
					nameValuePairs.add(new BasicNameValuePair("emp_id",params[1].toString()));
					nameValuePairs.add(new BasicNameValuePair("leave_id",params[2].toString()));	
					
					paramString = URLEncodedUtils.format(nameValuePairs,"utf-8");
					url=SEARCH_BY_LEAVE_TYPE_URL+paramString;
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
			case UPDATE_LEAVE_ENTITLEMENT:
				try {
					nameValuePairs.add(new BasicNameValuePair("emp_id",params[1].toString()));
					nameValuePairs.add(new BasicNameValuePair("leave_id",params[2].toString()));
					nameValuePairs.add(new BasicNameValuePair("days_used",params[3].toString()));
					
					paramString = URLEncodedUtils.format(nameValuePairs,"utf-8");
					url=UPDATE_LEAVE_ENTITLEMENT_URL+paramString;
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
			}
			return result;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			switch (type) {
			case SEARCH_ALL_LEAVE_TYPES:
				saleaves.clear();
				try {
					jsonArray=new JSONArray(result);

					for (int i=0; i<jsonArray.length();i++){					
						jsonObject=jsonArray.getJSONObject(i);
						saleaves.put(jsonObject.getInt("id"),jsonObject.getString("name"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				addAllListeners();
				break;

			case SEARCH_BY_LEAVE_TYPE:
				double bal=0.0;
				try {
					jsonArray=new JSONArray(result);
					for (int i=0; i<jsonArray.length();i++){					
						jsonObject=jsonArray.getJSONObject(i);
						bal+=jsonObject.getDouble("no_of_days")-jsonObject.getDouble("days_used");
						daysUsed=jsonObject.getDouble("days_used");
					}
					leaveBal.setText(String.valueOf(bal));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case UPDATE_LEAVE_ENTITLEMENT:
				Log.d("result",result);
				for(int i=0;i<saleaves.size();i++){
					if(saleaves.valueAt(i).equals(leaveType.getText().toString().trim())){
						performAsyncTask(2,saleaves.valueAt(i));
						break;
					}
				}				
				break;
			}
		}
	}
	
	public void performAsyncTask(Object...params){
		switch ((Integer)params[0]) {
		case SEARCH_ALL_LEAVE_TYPES:
			new LeaveApplyServerTask().execute(params[0]);
			break;

		case SEARCH_BY_LEAVE_TYPE:
			leaveId=saleaves.keyAt(saleaves.indexOfValue(String.valueOf(params[1])));
			
			new LeaveApplyServerTask().execute(params[0],empId,leaveId);
			break;
		case UPDATE_LEAVE_ENTITLEMENT:
			new LeaveApplyServerTask().execute(params[0],empId,leaveId,totalDeduct);
		}
	}
	
	public boolean validateData(){
		boolean valid=false;
		if(leaveId>=0 && Double.parseDouble(leaveBal.getText().toString().trim())>0){
			if( !fromDate.getText().toString().trim().equals("")
				&& !toDate.getText().toString().trim().equals("")
				){
				valid=true;
			}else if(Double.parseDouble(duration.getText().toString().trim())>0 
					&& Double.parseDouble(duration.getText().toString().trim())<=8){
				valid=true;
			}
		}
		return valid;
	}
}
