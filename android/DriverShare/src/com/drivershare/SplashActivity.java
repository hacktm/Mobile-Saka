package com.drivershare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class SplashActivity extends Activity {

	private String mNumber;

	private SharedPreferences mPrefs;

	private static final String REGISTERED = "registered";
	private static final String URL_SEND_NUMBER = C.IP + "insert_valid.php";
	private static final String URL_VALIDATE = C.IP + "validate.php";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (mPrefs.getBoolean(REGISTERED, false)) {
			enterApp();
		} else {
			enterNumber();
		}
	}

	private void enterNumber() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Registration");
		alert.setMessage("Before you can use this app you have to register using your phone number");
		final EditText et = new EditText(this);
		et.setInputType(InputType.TYPE_CLASS_PHONE);
		alert.setView(et);
		alert.setCancelable(false);
		alert.setPositiveButton("Register",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mNumber = et.getText().toString();
						Toast.makeText(SplashActivity.this, mNumber,
								Toast.LENGTH_LONG).show();
						new EnterNumber().execute(mNumber);
					}
				});
		alert.show();
	}

	private void enterCode() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Registration");
		alert.setMessage("Please enter the code you received in the sms");
		final EditText et = new EditText(this);
		et.setInputType(InputType.TYPE_CLASS_PHONE);
		alert.setView(et);
		alert.setCancelable(false);
		alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(SplashActivity.this, et.getText().toString(),
						Toast.LENGTH_LONG).show();
				new EnterCode().execute(et.getText().toString());
			}
		});
		alert.show();
	}

	private void enterApp() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		startActivity(new Intent(SplashActivity.this, MainActivity.class));
	}

	private class EnterNumber extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(SplashActivity.this);
			pd.setTitle("Loading");
			pd.setMessage("Please wait");
			pd.setCancelable(false);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String number = params[0];

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL_SEND_NUMBER);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);
				nameValuePairs.add(new BasicNameValuePair("phone", number));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				String stringResult = EntityUtils.toString(entity);
				Log.i("result", stringResult);
				try {
					JSONObject json = new JSONObject(stringResult);
					int result = json.getInt("result");
					if (result == 1) {
						return true;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return false;
				}
			} catch (ClientProtocolException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			pd.dismiss();
			if (result) {
				enterCode();
			} else {
				Toast.makeText(SplashActivity.this,
						"Something went wrong. Please try again",
						Toast.LENGTH_LONG).show();
				enterNumber();
			}
			super.onPostExecute(result);
		}

	}

	private class EnterCode extends AsyncTask<String, Integer, Boolean> {

		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(SplashActivity.this);
			pd.setTitle("Loading");
			pd.setMessage("Please wait");
			pd.setCancelable(false);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String code = params[0];

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL_VALIDATE);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("phone", mNumber));
				nameValuePairs.add(new BasicNameValuePair("code", code));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				String stringResult = EntityUtils.toString(entity);
				Log.i("result", stringResult);
				try {
					JSONObject json = new JSONObject(stringResult);
					int result = json.getInt("result");
					if (result == 1) {
						return true;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return false;
				}
			} catch (ClientProtocolException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			pd.dismiss();
			if (result) {
				mPrefs.edit().putBoolean(REGISTERED, true).commit();
				enterApp();
			} else {
				Toast.makeText(SplashActivity.this,
						"Something went wrong. Please try again",
						Toast.LENGTH_LONG).show();
				enterNumber();
			}
			super.onPostExecute(result);
		}

	}

}
