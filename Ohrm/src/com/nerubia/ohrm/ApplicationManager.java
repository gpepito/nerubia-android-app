package com.nerubia.ohrm;

import android.app.Application;
import android.content.Context;

public class ApplicationManager extends Application {
	private Context context;
	private int empId;
	

	public int getEmpId() {
		return empId;
	}

	public void setEmpId(int empId) {
		this.empId = empId;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	
}
