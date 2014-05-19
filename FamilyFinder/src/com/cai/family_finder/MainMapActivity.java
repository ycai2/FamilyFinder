package com.cai.family_finder;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cai.map.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
/**
 * @file MainMapActivity
 * @author Yisheng Cai
 * @version 1.0.0 05/04/14
 * This is the main acitivity which creates the map and lets user interact with the location 
 * marker on the map
 */
public class MainMapActivity extends ActionBarActivity implements OnMapClickListener {
	public final static String EXTRA_MESSAGE = "com.cai.family_finder.MESSAGE";
	public final static String EXTRA_LOCATION = "com.cai.family_finder.LOCATION";
	public final static String TAG="message activity";
	private GoogleMap mMap;
	private Marker mMarker;
	private String mLocationInfo;
	Location myLocation;
	String mAddress;
	LatLng mLatLng;
	TextView tvLocInfo;
	String locationMessage;
	String sender;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main_map);

		tvLocInfo = (TextView)findViewById(R.id.locinfo);

		if (savedInstanceState == null) {
			if (mMap == null) {
				mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
						.getMap();
				// Check if we were successful in obtaining the map.
				if (mMap != null) {
					// The Map is verified. It is now safe to manipulate the map.

					// Other supported types include: MAP_TYPE_NORMAL,
					// MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE
					mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


					//Allows user to Locate the device.
					mMap.setMyLocationEnabled(true);
					//Enable indoor map
					mMap.setIndoorEnabled(true);
					//Setup a onMapClickListener
					mMap.setOnMapClickListener(this);


					//Find user's current location and zoomin by default
					mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

						@Override
						public void onMyLocationChange(Location arg0) {
							// TODO Auto-generated method stub
							mLatLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());
							mLocationInfo = mLatLng.toString();
							//add a marker at user's location.
							mMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(180))
									.position(mLatLng)
									.title("It's Me!")
									.snippet("I'm at " + mLocationInfo));

							//find user and zoomin
							mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng,15));
							mMap.setOnMyLocationChangeListener(null);
						}
					});
					Intent messageIntent = getIntent();
					locationMessage = messageIntent.getStringExtra(SmsReceiver.EXTRA_MESSAGE);
					Log.i(TAG,"message: "+locationMessage);
					
					//The piece below parse the coordinate information from a SMS 
					//and converts the string to numbers so that the app can plot 
					//sender's location on the map.
					if (locationMessage != null){
						String strLat = locationMessage.substring(
								locationMessage.indexOf("(")+1,
								locationMessage.indexOf(","));
						String strLng = locationMessage.substring(
								locationMessage.indexOf(",")+1,
								locationMessage.indexOf(")"));
						double lat = Double.parseDouble(strLat);
						double lng = Double.parseDouble(strLng);
						Log.i(TAG,"message: "+locationMessage.substring(9,locationMessage.length()));
						Log.i(TAG,"location_data: "+lat+", "+lng);
						mMap.addMarker(new MarkerOptions()
								.position(new LatLng(lat,lng))
								.title("Sender/'s location"));
						
					}
					
					sender = messageIntent.getStringExtra(SmsReceiver.EXTRA_SENDER);
					Log.i(TAG,"sender: "+sender);
					
					mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this));
					//Inflate a information window and allow user the click the window to go to 
					//DisplayMessageActivity so that the user can send the address to others.
					mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

						@Override
						public void onInfoWindowClick(Marker arg0) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(MainMapActivity.this, DisplayMessageActivity.class);
							//Gracefully handle the error if address or location is not found. 
							String message = "Cannot find your address...try again";
							String locationMsg = "Cannot find your location...try again";
							locationMsg = mMarker.getPosition().toString();
							intent.putExtra(EXTRA_LOCATION,locationMsg);
							if (mAddress != null){
								message = mAddress;
								intent.putExtra(EXTRA_MESSAGE, message);
							}
							
							//Going to DisplayMessageActivity here!
							startActivity(intent);
						}
					});

				}
			}

		}

	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_map,
					container, false);
			return rootView;
		}
	}



	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}




	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}




	@Override
	public void onMapClick(LatLng point) {
		tvLocInfo.setText(point.toString());
		mMap.animateCamera(CameraUpdateFactory.newLatLng(point));

	}


}
