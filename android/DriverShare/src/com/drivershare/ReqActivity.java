package com.drivershare;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ReqActivity extends Activity {
	private GoogleMap googleMap;
	private ContentValues mReq;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request);

		mReq = getIntent().getParcelableExtra("data");

		try {
			initilizeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			if (mReq != null) {
				MarkerOptions marker = new MarkerOptions()
						.position(
								new LatLng(mReq.getAsDouble("p_lat"), mReq
										.getAsDouble("p_lon")))
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				;
				googleMap.addMarker(marker);
				marker = new MarkerOptions()
						.position(
								new LatLng(mReq.getAsDouble("d_lat"), mReq
										.getAsDouble("d_lon")))
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
				googleMap.addMarker(marker);
				String url = makeURL(mReq.getAsDouble("p_lat"),
						mReq.getAsDouble("p_lon"), mReq.getAsDouble("d_lat"),
						mReq.getAsDouble("d_lon"));
				new connectAsyncTask().execute(url);
			}
		}
	}

	public void drawPath(String result) {

		try {
			// Tranform the string into a json object
			final JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONArray("routes");
			JSONObject routes = routeArray.getJSONObject(0);
			JSONObject overviewPolylines = routes
					.getJSONObject("overview_polyline");
			String encodedString = overviewPolylines.getString("points");
			List<LatLng> list = decodePoly(encodedString);

			for (int z = 0; z < list.size() - 1; z++) {
				LatLng src = list.get(z);
				LatLng dest = list.get(z + 1);
				Polyline line = googleMap.addPolyline(new PolylineOptions()
						.add(new LatLng(src.latitude, src.longitude),
								new LatLng(dest.latitude, dest.longitude))
						.width(5).color(Color.RED).geodesic(true));
			}

		} catch (JSONException e) {

		}
	}

	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	public String makeURL(double sourcelat, double sourcelog, double destlat,
			double destlog) {
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.googleapis.com/maps/api/directions/json");
		urlString.append("?origin=");// from
		urlString.append(Double.toString(sourcelat));
		urlString.append(",");
		urlString.append(Double.toString(sourcelog));
		urlString.append("&destination=");// to
		urlString.append(Double.toString(destlat));
		urlString.append(",");
		urlString.append(Double.toString(destlog));
		urlString.append("&sensor=false&mode=driving&alternatives=true");
		return urlString.toString();
	}

	private class connectAsyncTask extends AsyncTask<String, Void, String> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ReqActivity.this);
			progressDialog.setMessage("Fetching route, Please wait...");
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			JSONParser jParser = new JSONParser();
			String json = jParser.getJSONFromUrl(params[0]);
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			progressDialog.hide();
			if (result != null) {
				drawPath(result);
			}
		}
	}
}
