package com.nerubia.ohrm.time;

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
import com.nerubia.ohrm.ServerTask;
import com.nerubia.ohrm.fragments.PopUpDialogFragment;
import com.nerubia.ohrm.util.OhrmTimeZone;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;

@SuppressLint("NewApi")
public class AttendanceRecord extends Activity {
//	private SharedPreferences.Editor _editor;
	private SharedPreferences _prefs;

	private CalendarView calendar;
	private Button search;
	private String date = "";
	private String emp_id = "";
	private TableLayout tableLayout;
	private LayoutInflater inflater;	
	
	private PopUpDialogFragment progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attendance_record);
		
		progressDialog=new PopUpDialogFragment(2);
//		_editor = PreferenceManager.getDefaultSharedPreferences(
//				getApplicationContext()).edit();
		_prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		tableLayout =(TableLayout)findViewById(R.id.tblRecord);	
		calendar = (CalendarView) findViewById(R.id.calendar);			
		calendar.setOnDateChangeListener(calendarListener);
	
		createNavigationTabs();
		
//		Calendar calen=Calendar.getInstance();
//		calen.setTime(new Date(calendar.getDate()));
//		
//		Date localDateObj = calen.getTime(); 
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
//		String converteddate = sdf.format(localDateObj );
//		
//		Log.d("initial time:",converteddate);
		
		emp_id=_prefs.getString("emp_id",null);
		date=_prefs.getString("CURRENT_DATE",null);
		
		search = (Button) findViewById(R.id.searchAttendanceByDate);
		search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				progressDialog.show(getFragmentManager(), "progressDialog");
				Log.d("id and date:::",emp_id+"  "+date);
				tableLayout.removeAllViews();
				new AttendanceRecordServerTask().execute(1,date, emp_id,getApplicationContext(),progressDialog);
			}
		});

	}

	private CalendarView.OnDateChangeListener calendarListener = new OnDateChangeListener() {
		@Override
		public void onSelectedDayChange(CalendarView calender, int year,
				int month, int day) {
			date = String.valueOf(year) + "-" + String.valueOf(month+1) + "-"
					+ String.valueOf(day);
			Log.d("date:::", date);
			// Log.d("date selected:",String.valueOf(year)+"   "+String.valueOf(month)+"   "+String.valueOf(day));
		}
	};

	private void createNavigationTabs() {
		final ActionBar actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				if(tab.getPosition()==1){
					new ServerTask().execute(2,emp_id,AttendanceRecord.this);
				}
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				Log.d("onTabReselected", tab.getText().toString());
			}
		};
		actionBar.setTitle(R.string.attendance);
		actionBar.addTab(actionBar.newTab().setText(R.string.punch_in_out)
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.my_records)
				.setTabListener(tabListener));
		actionBar.setSelectedNavigationItem(1);
	}


	@SuppressWarnings("deprecation")
	private void addResults(Object...params) {		
		
		TableRow trFirstHeader=(TableRow)inflater.inflate(R.layout.record_first_header,null);
		TableRow trSecondHeader=(TableRow)inflater.inflate(R.layout.record_second_header,null);
		TableRow trFirstResult=(TableRow)inflater.inflate(R.layout.record_first_result, null);
		TableRow trSecondResult=(TableRow)inflater.inflate(R.layout.record_second_result, null);
		TableRow trBottom=new TableRow(getApplicationContext());
		
		TextView tvPunchIn = (TextView)trFirstResult.findViewById(R.id.punchInResult);
		TextView tvPunchOut = (TextView)trFirstResult.findViewById(R.id.punchOutResult);
		TextView tvDuration =(TextView)trFirstResult.findViewById(R.id.durationResult);			
		TextView tvPunchInNote =(TextView)trSecondResult.findViewById(R.id.punchInNoteResult);
		TextView tvPunchOutNote =(TextView)trSecondResult.findViewById(R.id.punchOutNoteResult);

		//add header template
		tableLayout.addView(trFirstHeader);

		tvPunchIn.setText(params[0].toString());
		tvPunchOut.setText(params[2].toString());
		tvDuration.setText(params[4].toString());
	
		tableLayout.addView(trFirstResult);		
		//add header template
		tableLayout.addView(trSecondHeader);

		tvPunchInNote.setText(params[1].toString());
		tvPunchOutNote.setText(params[3].toString());
		
		tableLayout.addView(trSecondResult);
		LayoutParams trBottomLay=new LayoutParams();
		trBottomLay.width=LayoutParams.FILL_PARENT;
		trBottomLay.height=LayoutParams.WRAP_CONTENT;
		trBottom.setLayoutParams(trBottomLay);
		trBottom.setBackgroundResource(R.xml.bottom_border);
		trBottom.addView(new TextView(getApplicationContext()));
		
		tableLayout.addView(trBottom);
		
	}
	
	private class AttendanceRecordServerTask extends AsyncTask<Object,Void,String>{
		private final String GET_RECORD_PER_DAY="http://gladys.nerubia.com:3000/ohrmattendancerecord/get_record_per_day.json?";
		private String paramString="";
		
		private String result = "";
		private String url = "";
		private PopUpDialogFragment pd = null;
		private int type=0;
		
		private HttpClient httpClient = new DefaultHttpClient();
		private HttpGet httpGet;
		private HttpResponse response;
		
		private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		@Override
		protected String doInBackground(Object... params) {
			type=(Integer)params[0];
			switch (type) {

			case 1:	
				pd=(PopUpDialogFragment)params[4];
				nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("date",params[1].toString()));
				nameValuePairs.add(new BasicNameValuePair("employee_id",params[2].toString()));
				paramString=URLEncodedUtils.format(nameValuePairs,"utf-8");
				url=GET_RECORD_PER_DAY+paramString;
				
				
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
			default:
				break;
			}
			Log.d("RESULTS::::",result);
			return result;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			int hours=0;
			int minutes=0;
			String currentDuration="0:0";
			switch (type) {
			case 1:			
				pd.dismissProgressDialog();
				try {
					JSONArray jsonArray=new JSONArray(result);
					JSONObject jsonObject=new JSONObject();
					OhrmTimeZone punchIn=new OhrmTimeZone();
					OhrmTimeZone punchOut=new OhrmTimeZone();
					
					for (int i=0; i<jsonArray.length();i++){
						
						jsonObject=jsonArray.getJSONObject(i);
						punchIn.setUserTime(jsonObject.get("punch_in_user_time").toString(),null,8);
						punchOut.setUserTime(jsonObject.get("punch_out_user_time").toString(),null,8);
						
						if (!jsonObject.get("punch_in_user_time").equals(null)
								&& !jsonObject.get("punch_out_user_time")
										.equals(null)) {
							punchIn.setTimeDiff(punchIn.getDate(),
									punchOut.getDate());
							hours += punchIn.getTimeDiff().getHours();
							minutes += punchIn.getTimeDiff().getMinutes();
							currentDuration = punchIn.getTimeDiff().getHours()
									+ ":" + punchIn.getTimeDiff().getMinutes();
						}
						
						addResults(punchIn.getUserPunchTime(),jsonObject.get("punch_in_note"),punchOut.getUserPunchTime(),jsonObject.get("punch_out_note"),currentDuration);
					}
					
					TableRow trTotal=(TableRow)inflater.inflate(R.layout.record_total,null);
					TextView tvTotal=(TextView)trTotal.findViewById(R.id.total);
					tvTotal.setText(String.valueOf(hours)+":"+String.valueOf(minutes));
					tableLayout.addView(trTotal);
					
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("onPostExecute JsonArray",e.toString());
				}
				break;
			default:
				break;
			}
			this.cancel(true);
		}
	}
}
