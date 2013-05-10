package com.nerubia.ohrm.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {
	private String _encrypt;

	public String get_encrypt() {
		return this._encrypt;
	}

	public void set_encrypt(String encrypt) {
		try {
			
			MessageDigest mdEnc=MessageDigest.getInstance("MD5");
			mdEnc.update(encrypt.getBytes(),0,encrypt.length());			
			this._encrypt =new BigInteger(1,mdEnc.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
	}
}
