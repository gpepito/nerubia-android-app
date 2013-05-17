package com.nerubia.ohrm.fragments;

import java.util.ArrayList;

import com.nerubia.ohrm.R;
import com.nerubia.ohrm.leave.LeaveApply;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.ArrayAdapter;
import android.widget.EditText;

public class PopUpDialogFragment extends DialogFragment {
	private final int LEAVE_TYPE_DIALOG=1;
	private final int LOADER_SPINNER=2;
	private int type=0;
	private ProgressDialog progressDialog;
	private ArrayList<String> items=new ArrayList<String>();
	private SparseArray<String> arr=new SparseArray<String>();
	
	@SuppressWarnings("unchecked")
	public PopUpDialogFragment(Object...params) {
		this.type=(Integer) params[0];
		switch (type) {
		case LEAVE_TYPE_DIALOG:
			arr=(SparseArray<String>)params[1];
			
			for(int i=0;i<arr.size();i++){
				items.add(arr.valueAt(i).toString());	
			}			
			break;
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		switch (type) {
		case LEAVE_TYPE_DIALOG:

			AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
			builder.setAdapter(new ArrayAdapter<String>((Context)getActivity(),android.R.layout.simple_list_item_1,items),new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int pos) {
					EditText text=(EditText)getActivity().findViewById(R.id.editLeaveType);
					text.setText(arr.valueAt(pos));
					((LeaveApply)getActivity()).performAsyncTask(2,arr.valueAt(pos));
					
				}
			});
			return builder.create();
			case LOADER_SPINNER:	
				progressDialog=new ProgressDialog(getActivity());
				progressDialog
				.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setMessage("processing...");
				progressDialog.show();
				return progressDialog;
		}
		return null;
	}
	public void dismissProgressDialog(){
		progressDialog.dismiss();
	}
	
	
}
