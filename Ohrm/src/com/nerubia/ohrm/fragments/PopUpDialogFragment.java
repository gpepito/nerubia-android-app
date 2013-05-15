package com.nerubia.ohrm.fragments;

import com.nerubia.ohrm.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

public class PopUpDialogFragment extends DialogFragment {
	private final int LEAVE_TYPE_DIALOG=1;
	private int type=0;
	private String[] items=null;
	
	public PopUpDialogFragment(Object...params) {
		this.type=(Integer) params[0];
		items=(String[])params[1];
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		switch (type) {
		case LEAVE_TYPE_DIALOG:

			AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
			builder.setItems(this.items, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int pos) {
					EditText text=(EditText)getActivity().findViewById(R.id.editLeaveType);
					text.setText(items[pos]);
				}
			});
			return builder.create();
		}
		return null;
	}
}
