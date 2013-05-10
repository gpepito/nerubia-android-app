package com.nerubia.ohrm;

import java.util.Timer;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity {
	private Timer _timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (!getIntent().getBooleanExtra("LOGGED_IN", false)) {
			Intent intent = new Intent(MainActivity.this, Login.class);
			startActivity(intent);
			finish();
			
			
		} else {
			Log.d("emp_id",getIntent().getStringExtra("emp_id"));
			new ServerTask().execute(2,getIntent().getStringExtra("emp_id"),MainActivity.this);
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onStop() {
		_timer.cancel();
		super.onStop();
	}

}
