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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	public static final String SENDER_ID = "578763080585";

	public static final String URL_REGID = C.IP + "register_android_user.php";

	public GCMIntentService() {
		super(SENDER_ID);
	}

	protected void onRegistered(Context context, String registrationId) {
		Log.i("regitered", ":" + registrationId);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		new UploadId().execute(registrationId, prefs.getString("number", ""));
	}

	protected void onUnregistered(Context context, String registrationId) {
	}

	protected void onMessage(Context context, Intent intent) {
	}

	protected void onError(Context arg0, String arg1) {
	}

	private class UploadId extends AsyncTask<String, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL_REGID);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("phone", params[1]));
				nameValuePairs.add(new BasicNameValuePair("gmc_id", params[0]));
				nameValuePairs.add(new BasicNameValuePair("driver", "1"));
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
			return true;
		}

	}

}