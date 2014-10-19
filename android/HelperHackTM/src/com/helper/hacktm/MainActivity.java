package com.helper.hacktm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.helper.hacktm.helper.WakeLocker;

public class MainActivity extends Activity {
	public static final String DISPLAY_MESSAGE_ACTION = "com.helper.hacktm.DISPLAY_MESSAGE";
	public static final String EXTRA_MESSAGE = "message";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GCMRegistrar.checkDevice(this);

		GCMRegistrar.checkManifest(this);
		if (GCMRegistrar.getRegistrationId(this).equals("")) {

			registerReceiver(mHandleMessageReceiver, new IntentFilter(
					DISPLAY_MESSAGE_ACTION));
		}
		String regId = GCMRegistrar.getRegistrationId(this);

		if (regId.equals("")) {
			GCMRegistrar.register(this, GCMIntentService.SENDER_ID);
		}

		Log.i("regid", regId);

	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			WakeLocker.acquire(getApplicationContext());
			WakeLocker.release();
		}
	};
}
