package com.nerubia.ohrm.fragments;

import java.text.NumberFormat;
import com.nerubia.ohrm.R;
import com.nerubia.ohrm.util.OhrmTimeZone;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

public class DateTimePickerDialogFragment extends DialogFragment {
	static final int TIME_DIALOG_ID = 1;
	static final int DATE_DIALOG_ID = 2;
	private int viewId;
	private int id;
	private OhrmTimeZone otz=new OhrmTimeZone();
	
	private NumberFormat nf = NumberFormat.getNumberInstance();
	private OnDateSetListener dateListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			EditText date = (EditText) getActivity().findViewById(viewId);
			date.setText(year + "-" + dayOfMonth + "-"
					+ String.valueOf((int) monthOfYear + 1));
			EditText todate=(EditText)getActivity().findViewById(R.id.editToDate);
			EditText fromdate=(EditText)getActivity().findViewById(R.id.editFromDate);
			LinearLayout rel=(LinearLayout)getActivity().findViewById(R.id.relTimeDuration);
			
			if(!todate.getText().toString().trim().equals("")&& !fromdate.getText().toString().trim().equals("")){
				if(todate.getText().toString().trim().equals(fromdate.getText().toString().trim())){
					rel.setVisibility(View.VISIBLE);
				}else{
					rel.setVisibility(View.GONE);
				}
			}
		}
	};

	public DateTimePickerDialogFragment(int viewId, int id) {
		this.viewId = viewId;
		this.id = id;
	}

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			double total = 0;
			double from = 0;
			double to = 0;
			String data = "";

			EditText duration = (EditText) getActivity().findViewById(
					R.id.editDuration);
			EditText time = (EditText) getActivity().findViewById(viewId);
			time.setText(hourOfDay + ":" + nf.format(minute));

			if (time.getId() == R.id.editFromTime) {
				from = Double.parseDouble(time.getText().toString().trim()
						.replace(":", "."));
				EditText toTime = (EditText) getActivity().findViewById(
						R.id.editToTime);

				data = toTime.getText().toString().trim().replace(":", ".");
				if (!data.equals("")) {
					to = Double.parseDouble(data);
				}
			} else {
				to = Double.parseDouble(time.getText().toString().trim()
						.replace(":", "."));
				EditText fromTime = (EditText) getActivity().findViewById(
						R.id.editFromTime);

				data = fromTime.getText().toString().trim().replace(":", ".");
				if (!data.equals("")) {
					from = Double.parseDouble(data);
				}
			}
			total = to - from;

			if (total > 0 && total <= 8) {
				duration.setText(String.valueOf(total));
			} else {
				duration.setText("0.00");
			}

		}
	};

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		nf.setMaximumIntegerDigits(2);
		nf.setMinimumIntegerDigits(2);
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(getActivity(), timePickerListener, 00,
					00, true);
		default:
			return new DatePickerDialog(getActivity(), dateListener, 2013, 4,
					14);
		}
	}
}
