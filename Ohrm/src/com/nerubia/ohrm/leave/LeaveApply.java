package com.nerubia.ohrm.leave;

import com.nerubia.ohrm.R;
import com.nerubia.ohrm.fragments.DateTimePickerDialogFragment;
import com.nerubia.ohrm.fragments.PopUpDialogFragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LeaveApply extends Activity {
	private EditText fromDate;
	private EditText toDate;
	private EditText fromTime;
	private EditText toTime;
	private EditText leaveType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leave_apply);
		createNavigationTabs();
		addAllListeners();
	}

	private void addAllListeners() {
		final String[] values=new String[]{"sick leave","vacation leave","birthday leave"};
		leaveType=(EditText)findViewById(R.id.editLeaveType);
		leaveType.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				PopUpDialogFragment dialog=new PopUpDialogFragment(1,values);
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
}
