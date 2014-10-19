package com.drivershare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {

	public static final String DISPLAY_MESSAGE_ACTION = "com.drivershare.DISPLAY_MESSAGE";
	public static final String EXTRA_MESSAGE = "message";

	private GoogleMap googleMap;

	private static final String URL_GET_REQ = C.IP + "get_requests.php";

	private ArrayList<ContentValues> mRequests;
	private WeakHashMap<Marker, ContentValues> mMarkers;

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
		try {
			initilizeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}

		new GetRequests().execute();

	}

	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
		}
		if (googleMap != null) {
			googleMap.setMyLocationEnabled(true);
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(17.385044, 78.486671)).zoom(12).build();
			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			googleMap.setOnMarkerClickListener(markerListener);
		}
	}

	private OnMarkerClickListener markerListener = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker arg0) {
			ContentValues cv = mMarkers.get(arg0);
			Intent intent = new Intent(MainActivity.this, ReqActivity.class);
			intent.putExtra("data", cv);
			startActivity(intent);
			return false;
		}

	};

	private void updateMarkers() {
		mMarkers = new WeakHashMap<Marker, ContentValues>();
		ContentValues cv;
		for (int i = 0; i < mRequests.size(); i++) {
			cv = mRequests.get(i);
			MarkerOptions marker = new MarkerOptions()
					.position(
							new LatLng(cv.getAsDouble("p_lat"), cv
									.getAsDouble("p_lon"))).title(
							cv.getAsString("name"));
			Marker m = googleMap.addMarker(marker);
			mMarkers.put(m, cv);
		}
	}

	private class GetRequests extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(MainActivity.this);
			pd.setTitle("Loading");
			pd.setMessage("Please wait");
			pd.setCancelable(false);
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			mRequests = new ArrayList<ContentValues>();
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL_GET_REQ);
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						0);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				String stringResult = EntityUtils.toString(entity);
				Log.i("result", stringResult);
				try {
					JSONObject json = new JSONObject(stringResult);
					int result = json.getInt("result");
					if (result == 1) {
						JSONArray jarr = json.getJSONArray("data");
						ContentValues cv;
						for (int i = 0; i < jarr.length(); i++) {
							json = jarr.getJSONObject(i);
							cv = new ContentValues();

							cv.put("phone", json.getString("phone"));
							cv.put("name", json.getString("name"));
							cv.put("price", json.getString("price"));
							cv.put("p_lat", json.getString("p_lat"));
							cv.put("p_lon", json.getString("p_lon"));
							cv.put("p_address", json.getString("p_address"));
							cv.put("d_lat", json.getString("d_lat"));
							cv.put("d_lon", json.getString("d_lon"));
							cv.put("d_address", json.getString("d_address"));
							cv.put("description", json.getString("description"));
							cv.put("smoker", json.getInt("smoker"));

							mRequests.add(cv);
						}
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
				updateMarkers();
			}
			super.onPostExecute(result);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
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
