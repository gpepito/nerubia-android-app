package com.nerubia.ohrm.util;

import java.util.regex.Pattern;

import android.util.Log;

public class EmailValidator {

	private final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9+._%-+]{1,256}" +
		              "@" +
		              "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
		              "(" +
		              "." +
		              "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
		              ")+");
	public Boolean validate(String email) {
		Log.d("emailValidate:",String.valueOf(EMAIL_ADDRESS_PATTERN.matcher(email).matches()));
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}
}
