package com.cai.family_finder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.view.View;
import android.widget.TextView;

import com.cai.map.R;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class MyInfoWindowAdapter implements InfoWindowAdapter{


	/**
	 * 
	 */
	private final MainMapActivity mainMapActivity;
	private final View mContentsView;

	MyInfoWindowAdapter(MainMapActivity mainMapActivity){
		this.mainMapActivity = mainMapActivity;
		mContentsView = this.mainMapActivity.getLayoutInflater().inflate(R.layout.custom_info_contents, null);
	}
	
	
	//This function creates the infowindow and specifies the properties
	@Override
	public View getInfoContents(Marker marker) {
		//Set the title of this marker
		TextView tvTitle = ((TextView)mContentsView.findViewById(R.id.title));
		tvTitle.setText(marker.getTitle());
		//Set the text content of this marker
		TextView tvSnippet = ((TextView)mContentsView.findViewById(R.id.snippet));
		tvSnippet.setText(marker.getPosition().toString());
		//Decode the coordinates, converting coordinates to physical address
		Geocoder geocoder = new Geocoder(this.mainMapActivity, Locale.ENGLISH);

		try {
			List<Address> addresses = geocoder.getFromLocation(
					marker.getPosition().latitude, 
					marker.getPosition().longitude, 
					1);

			if(addresses != null) {
				Address returnedAddress = addresses.get(0);
				StringBuilder strReturnedAddress = new StringBuilder("");
				for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
				}
				//try to build a readable string for address
				tvSnippet.setText("Address: \n"+strReturnedAddress.toString());
				this.mainMapActivity.mAddress = strReturnedAddress.toString();
			}
			else{
				tvSnippet.setText("No Address returned!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			tvSnippet.setText("Canont get Address!");
		}

		return mContentsView;
	}


	@Override
	public View getInfoWindow(Marker marker) {
		// TODO Auto-generated method stub
		return null;
	}
}