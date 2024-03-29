package com.nerubia.ohrm.time;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.nerubia.ohrm.ApplicationManager;
import com.nerubia.ohrm.Login;
import com.nerubia.ohrm.MainActivity;
import com.nerubia.ohrm.R;
import com.nerubia.ohrm.ServerTask;
import com.nerubia.ohrm.fragments.PopUpDialogFragment;
import com.nerubia.ohrm.util.OhrmTimeZone;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AttendancePunchIn extends Activity{
	private SharedPreferences.Editor _editor;
	private SharedPreferences _prefs;

	private RelativeLayout _mainContent;
	private Button _logout;
	private Button _btnTimeIn;

	private EditText _txtNote;
	private TextView _lblPunchTime;
	private TextView _txtPunchTime;
	private TextView _txtDate;
	private TextView _txtTime;
	private String _currentTime;
	private OhrmTimeZone _tz = new OhrmTimeZone();

	private JSONObject _records;
	private ServerTask servertask = new ServerTask();
	private Boolean noRecords = false;
	private PopUpDialogFragment progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attendance_punch_in);
	
		((Activity)(((ApplicationManager)getApplicationContext()).getContext())).finish();
		
		progressDialog=new PopUpDialogFragment(2);
		
		_editor = PreferenceManager.getDefaultSharedPreferences(
				getApplicationContext()).edit();
		_prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String record = getIntent().getStringExtra("ATTENDANCE_RECORDS");

		_logout = (Button) findViewById(R.id.logout);
		_logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				_editor.putBoolean("LOGGED_IN", false).commit();
				Intent intent = new Intent(AttendancePunchIn.this,
						Login.class);
				intent.putExtra("login", true);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});

		_mainContent = (RelativeLayout) findViewById(R.id.mainContent);
		_mainContent.setVisibility(View.VISIBLE);

		_lblPunchTime = (TextView) findViewById(R.id.lblPunchTime);
		_txtPunchTime = (TextView) findViewById(R.id.txtPunchTime);
		_txtDate = (TextView) findViewById(R.id.txtDate);
		_txtTime = (TextView) findViewById(R.id.txtTime);
		_btnTimeIn=(Button) findViewById(R.id.btnTimeIn);
		_txtNote=(EditText)findViewById(R.id.txtNote);
		if (!record.toString().equals("null")) {
			try {
				_records = new JSONObject(record);
				_btnTimeIn.setText("OUT");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
		noRecords = true;
		_lblPunchTime.setVisibility(View.INVISIBLE);
		_txtPunchTime.setVisibility(View.INVISIBLE);
		_btnTimeIn.setText("IN");
		}
		
		_btnTimeIn = (Button) findViewById(R.id.btnTimeIn);
		_btnTimeIn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String userTime=_txtDate.getText().toString()+" "+_txtTime.getText().toString()+":00";
				if (_btnTimeIn.getText().equals("IN")) {
					progressDialog.show(getFragmentManager(), "progressDialog");
					_editor.putBoolean("PAUSE_TIMER",true).commit();
					new ServerTask().execute(5,_prefs.getString("emp_id",null),_txtNote.getText().toString(),"8", userTime,"PUNCHED IN",progressDialog,getApplicationContext());
					_lblPunchTime.setVisibility(View.VISIBLE);
					_txtPunchTime.setVisibility(View.VISIBLE);
					_lblPunchTime.setText("Punch Out");
					_txtPunchTime.setText(userTime);
					_btnTimeIn.setText("OUT");
				} else {
					progressDialog.show(getFragmentManager(), "progressDialog");
						_editor.putBoolean("PAUSE_TIMER",true).commit();
						new ServerTask().execute(4,_prefs.getString("emp_id",null),userTime,_txtNote.getText().toString(),"PUNCHED OUT",progressDialog,getApplicationContext());
						_lblPunchTime.setVisibility(View.INVISIBLE);
						_txtPunchTime.setVisibility(View.INVISIBLE);
						_lblPunchTime.setText("Punch In");
						_btnTimeIn.setText("IN");
				}
			}
		});
		createNavigationTabs();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!noRecords) {
			try {

				_tz.setUserTime(_records.getString("punch_in_user_time")
						.toString(), _records.getString("time").toString(), 8);
				_tz.setCurrentTime(_records.getString("time"));

				_txtPunchTime.setText(_tz.getUserPunchTime().toString());
				_txtDate.setText(_tz.getUserDate().toString());
				_txtTime.setText(_tz.getCurrentTime().toString());
				

				startTimer();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else{
			try {
				String res=_prefs.getString("USER_RECORD",null);
				if(!res.equals(null)){
				JSONObject json=new JSONObject(res);
				_tz.setCurrentTime(json.getString("time"));
				_txtDate.setText(_tz.getUserDate().toString());
				_txtTime.setText(_tz.getCurrentTime().toString());
				startTimer();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		_editor.putString("CURRENT_DATE", _txtDate.getText().toString()).commit();
		
	}

	private void startTimer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (_prefs.getBoolean("LOGGED_IN", false) && !_prefs.getBoolean("PAUSE_TIMER", false)) {
					_currentTime = _prefs.getString("CURRENT_TIME", "NULL");
					Log.d("time::::", _currentTime);
					if (_currentTime != "NULL") {
						AttendancePunchIn.this
								.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										_txtTime.setText(_currentTime);
									}
								});
					}
					servertask.cancel(true);
					new ServerTask().execute(3, getApplicationContext());
				}
			}
		}, 0, 5000);
		
	}
	@Override
	protected void onStop() {
		_editor.putBoolean("PAUSE_TIMER", true).commit();
		servertask.cancel(true);
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		_editor.putBoolean("PAUSE_TIMER", true).commit();
		servertask.cancel(true);
		super.onDestroy();
	}
	
	private void createNavigationTabs(){
		final ActionBar actionBar=getActionBar();
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			
			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				Log.d("onTabUnselected", tab.getText().toString());
			}
			
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				Log.d("onTabselected", tab.getText().toString());
				if(tab.getPosition()==1){
					Log.d("selected:","myrecords");
					Intent i=new Intent(getApplicationContext(), AttendanceRecord.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(i);
					finish();
				}
			}
			
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				Log.d("onTabReselected", tab.getText().toString());
			}
		};
		actionBar.setTitle(R.string.attendance);
		actionBar.addTab(actionBar.newTab().setText(R.string.punch_in_out).setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.my_records).setTabListener(tabListener));
		actionBar.setSelectedNavigationItem(0);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent=null;
		switch (item.getItemId()) {
		case R.id.main_menu:	
			intent=new Intent(AttendancePunchIn.this,MainActivity.class);
			startActivity(intent);
			finish();
			return super.onOptionsItemSelected(item);

		case R.id.logout_menu:
			_editor.putBoolean("LOGGED_IN", true).commit();
			intent = new Intent(AttendancePunchIn.this,
					Login.class);
			intent.putExtra("login", true);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
