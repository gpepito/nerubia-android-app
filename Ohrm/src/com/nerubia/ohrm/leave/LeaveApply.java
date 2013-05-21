package com.nerubia.ohrm.leave;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nerubia.ohrm.R;
import com.nerubia.ohrm.R.id;
import com.nerubia.ohrm.fragments.DateTimePickerDialogFragment;
import com.nerubia.ohrm.fragments.PopUpDialogFragment;
import com.nerubia.ohrm.util.OhrmTimeZone;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class LeaveApply extends Fragment {
//	private SharedPreferences.Editor _editor;
	private SharedPreferences _prefs;

	private static final int SEARCH_ALL_LEAVE_TYPES=1;
	private static final int SEARCH_BY_LEAVE_TYPE=2;
	private static final int UPDATE_LEAVE_ENTITLEMENT=3;
	private static final String SEARCH_ALL_LEAVE_TYPES_URL="http://gladys.nerubia.com:3000/leave/apply/search_all.json";
	private static final String SEARCH_BY_LEAVE_TYPE_URL="http://gladys.nerubia.com:3000/leave/apply/search_by_leave_type.json?";
	private static final String UPDATE_LEAVE_ENTITLEMENT_URL="http://gladys.nerubia.com:3000/leave/apply/add.json?";	
	
	private EditText fromDate;
	private EditText toDate;
	private EditText fromTime;
	private EditText toTime;
	private EditText leaveType;
	private EditText leaveBal;
	private EditText duration;
	private EditText comments;
	private Button btnApply;	
	private LinearLayout rel;
	private PopUpDialogFragment progressDialog;
	
	private int empId=-1;
	private int leaveId=-1;
	private double totalDeduct=0.0;
	private double daysUsed=0.0;
	private String dateApplied="";
	private View view;
	
	private SparseArray<String> saleaves=new SparseArray<String>();
	private OhrmTimeZone otz=new OhrmTimeZone();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.leave_apply, container,false);
//		_editor = PreferenceManager.getDefaultSharedPreferences(
//				getActivity()).edit();
		_prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		empId = Integer.parseInt(_prefs.getString("emp_id","1"));
		Log.d("leave apply empid  ", String.valueOf(empId));
		performAsyncTask(1);
		return view;
	}
	
	private void addAllListeners() {
		comments=(EditText)view.findViewById(id.editComment);
		duration=(EditText)view.findViewById(R.id.editDuration);
		rel=(LinearLayout)view.findViewById(R.id.relTimeDuration);
		
		leaveBal=(EditText)view.findViewById(R.id.editLeaveBal);
		leaveType=(EditText)view.findViewById(R.id.editLeaveType);
		leaveType.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				PopUpDialogFragment dialogFragment=new PopUpDialogFragment(1,saleaves);
				dialogFragment.show(getFragmentManager(), "LeaveType");
			}
		});
		
		fromDate = (EditText) view.findViewById(R.id.editFromDate);
		fromDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DateTimePickerDialogFragment datePicker = new DateTimePickerDialogFragment(
						fromDate.getId(), 2);
				datePicker.show(getFragmentManager(), "datePicker");
			}
		});

		toDate = (EditText) view.findViewById(R.id.editToDate);
		toDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DateTimePickerDialogFragment datePicker = new DateTimePickerDialogFragment(
						toDate.getId(), 2);
				datePicker.show(getFragmentManager(), "datePicker");
			}
		});

		fromTime=(EditText)view.findViewById(R.id.editFromTime);
		fromTime.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DateTimePickerDialogFragment timePicker = new DateTimePickerDialogFragment(
						fromTime.getId(), 1);
				timePicker.show(getFragmentManager(), "timePicker");
			}
		});
		
		toTime=(EditText)view.findViewById(R.id.editToTime);
		toTime.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DateTimePickerDialogFragment timePicker = new DateTimePickerDialogFragment(
						toTime.getId(), 1);
				timePicker.show(getFragmentManager(), "timePicker");
			}
		});
		
		btnApply=(Button)view.findViewById(R.id.btnApply);
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
				progressDialog=new PopUpDialogFragment(2);
				progressDialog.show(getFragmentManager(), "progressDialog");
				performAsyncTask(3);
			}
		});
		
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
					nameValuePairs.add(new BasicNameValuePair("from_date",params[4].toString()));
					nameValuePairs.add(new BasicNameValuePair("comments",params[5].toString()));
					
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
			Log.d("url",url);
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
				progressDialog.dismissProgressDialog();
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
			Log.d("date applied",fromDate.getText().toString().trim());
			String[] splitDate=fromDate.getText().toString().trim().split("-");
			dateApplied=splitDate[0]+"-"+splitDate[2]+"-"+splitDate[1];
			new LeaveApplyServerTask().execute(params[0],empId,leaveId,totalDeduct,dateApplied,comments.getText().toString());
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
