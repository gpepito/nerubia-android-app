package com.nerubia.ohrm;

import org.json.JSONException;
import org.json.JSONObject;

import com.nerubia.ohrm.fragments.PopUpDialogFragment;
import com.nerubia.ohrm.util.EmailValidator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	private String _validateErrors = "";
	private EditText _username;
	private EditText _password;
	private Button _login;
	private PopUpDialogFragment progressDialog;
	private EmailValidator _validator;

	// private LoginInfo loginInfo=new LoginInfo();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		progressDialog=new PopUpDialogFragment(2);		
		_validator = new EmailValidator();		

		_username = (EditText) findViewById(R.id.username);
		_password = (EditText) findViewById(R.id.password);
		_login = (Button) findViewById(R.id.login);

		_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validate()) {
					progressDialog.show(getFragmentManager(), "progressDialog");
					login();
				} else {
					Toast.makeText(getApplicationContext(), _validateErrors,
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private void login() {
		new ServerTask().execute(1, _username.getText().toString(), _password
				.getText().toString(),progressDialog, Login.this);
	}

	// for parsing jsonString from server
	public void parseResult(String jsonString) {
		String emp_number = "";
		String username = "";
		String password = "";

		try {
			JSONObject mainObj = new JSONObject(jsonString);
			emp_number = mainObj.getString("emp_number");
			username = mainObj.getString("user_name");
			password = mainObj.getString("user_password");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("JsonParser Results::::", "empnumber:" + emp_number
				+ "\nusername:" + username + "\npassword:" + password);
	}

	private Boolean validate() {
		Boolean result = true;
		_validateErrors = "";
		if (!_username.getText().toString().trim().isEmpty()) {
			// uncomment to validate email
			// if (!_validator.validate(_username.getText().toString().trim()))
			// {
			// result = false;
			// _validateErrors += "Invalid Email\n";
			// }
		} else {
			result = false;
			_validateErrors += "Please input username\n";
		}
		if (!_password.getText().toString().trim().isEmpty()) {
			if (!(_password.getText().toString().trim().length() >= 8)) {
				result = false;
				_validateErrors += "Password must be 8 characters and above.\n";
			}
		} else {
			result = false;
			_validateErrors += "Please input password\n";
		}
		return result;
	}
}
