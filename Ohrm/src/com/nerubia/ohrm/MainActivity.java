package com.nerubia.ohrm;

import com.nerubia.ohrm.leave.Leave;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity {
	private final  String LEAVE_TAG="LEAVE";
	private String current=LEAVE_TAG;
	
	private SharedPreferences.Editor _editor;
	private SharedPreferences _prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		_editor = PreferenceManager.getDefaultSharedPreferences(
				getApplicationContext()).edit();
		_prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		
		if (!_prefs.getBoolean("LOGGED_IN",false)) {
			Intent intent = new Intent(MainActivity.this, Login.class);
			startActivity(intent);
			finish();
			
			
		} else {		
	        createNavigationTabs();
		}
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
				Log.d("position",tab.getText().toString());
				switch (tab.getPosition()) {
				case 0:						
					if(!current.equals(LEAVE_TAG)){
						Intent intent=new Intent(MainActivity.this,Leave.class);
						startActivity(intent);
						finish();
					}
					current=LEAVE_TAG;
					break;
				case 1:
						new ServerTask().execute(2,
								_prefs.getString("emp_id",null),
								MainActivity.this);
						((ApplicationManager)getApplicationContext()).setContext(MainActivity.this);
//						finish();
					break;
				case 2:
					break;
				}
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				switch (tab.getPosition()) {
				case 0:						
						Intent intent=new Intent(MainActivity.this,Leave.class);
						startActivity(intent);
						finish();
					break;
				case 1:
						((ApplicationManager)getApplicationContext()).setContext(MainActivity.this);
						new ServerTask().execute(2,
								_prefs.getString("emp_id",null),
								MainActivity.this);
//						finish();
					break;
				case 2:
					break;
				}
			}
		};
		actionBar.setTitle(R.string.leave);
		actionBar.addTab(actionBar.newTab().setText(R.string.leave)
				.setTabListener(tabListener).setTag(""));
		actionBar.addTab(actionBar.newTab().setText(R.string.time)
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.my_info)
				.setTabListener(tabListener));
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent=new Intent();
		switch (item.getItemId()) {
		case R.id.main_menu:	
			return super.onOptionsItemSelected(item);

		case R.id.logout_menu:
			_editor.putBoolean("LOGGED_IN", true).commit();
			intent = new Intent(MainActivity.this,
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
	protected void onStop() {
//		_timer.cancel();
		super.onStop();
	}

}
