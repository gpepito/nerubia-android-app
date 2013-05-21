package com.nerubia.ohrm.leave;

import com.nerubia.ohrm.Login;
import com.nerubia.ohrm.MainActivity;
import com.nerubia.ohrm.R;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class Leave extends Activity {
	private SharedPreferences.Editor _editor;
//	private SharedPreferences _prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 _editor=PreferenceManager.getDefaultSharedPreferences(
					getApplicationContext()).edit();
//			_prefs = PreferenceManager
//					.getDefaultSharedPreferences(getApplicationContext());
					
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setTitle(R.string.main);
		actionBar.addTab(actionBar
				.newTab()
				.setText(R.string.apply)
				.setTabListener(
						new TabListener<LeaveApply>(this, "LEAVE_APPLY", LeaveApply.class)));
		actionBar.addTab(actionBar
				.newTab()
				.setText(R.string.entitlements)
				.setTabListener(
						new TabListener<LeaveList>(this, "LEAVE_LIST",LeaveList.class)));
		actionBar.setSelectedNavigationItem(0);
	}

	
	public static class TabListener<T extends Fragment> implements
			ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		/**
		 * Constructor used each time a new tab is created.
		 * 
		 * @param activity
		 *            The host Activity, used to instantiate the fragment
		 * @param tag
		 *            The identifier tag for the fragment
		 * @param clz
		 *            The fragment's Class, used to instantiate the fragment
		 */
		public TabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		/* The following are each of the ActionBar.TabListener callbacks */

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Check if the fragment is already initialized
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				// If it exists, simply attach it in order to show it
				ft.attach(mFragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// User selected the already selected tab. Usually do nothing.
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent=new Intent();
		switch (item.getItemId()) {
		case R.id.main_menu:	
			intent=new Intent(Leave.this,MainActivity.class);
			startActivity(intent);
			finish();
			return super.onOptionsItemSelected(item);

		case R.id.logout_menu:
			_editor.putBoolean("LOGGED_IN", true).commit();
			intent = new Intent(Leave.this,
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
