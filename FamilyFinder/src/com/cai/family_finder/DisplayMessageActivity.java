package com.cai.family_finder;

import com.cai.map.R;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.telephony.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.*;

public class DisplayMessageActivity extends ActionBarActivity {
	public final static String TAG="message activity";
	private final static int CONTACT_PICKER_RESULT = 1001;
	String message;
	String locationMessage;
	Button sendAddressButton;
	Button sendLocationButton;
	EditText numberEntry;
	EditText editText;
	TextView locationText;

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		View v = super.onCreateView(name, context, attrs);

		Intent intent = getIntent();
		//get location and address from MainMapActivity
		message = intent.getStringExtra(MainMapActivity.EXTRA_MESSAGE);
		locationMessage = intent.getStringExtra(MainMapActivity.EXTRA_LOCATION);

		//This field is the location coordinates
		locationText = (TextView)findViewById(R.id.locationText);
		if (locationText != null){
			locationText.setText(locationMessage);
		}
		// Create the text view of message
		editText = (EditText)findViewById(R.id.editText1);
		Log.i(TAG,"message="+message);
		if (editText != null){
			Log.i(TAG,"Success!");
			editText.setText("I am currently at: "+message+". (Sent by FamilyFinder)");
		}
		//Check if there number field already has something in it. If not, allow user 
		//to input a phone number
		if (numberEntry == null){
			numberEntry = (EditText)findViewById(R.id.editNumber);
		}


		//By using this button, user would be able to send his/her physical location
		//to some other number. 
		sendAddressButton = (Button)findViewById(R.id.sendSMS);
		if (sendAddressButton != null){
			sendAddressButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (numberEntry.getText().toString().matches("")){
						Toast.makeText(getApplicationContext(), 
								"You need a receiver!", Toast.LENGTH_LONG).show();
					}else{
						String phoneNo = numberEntry.getText().toString();
						String sms = editText.getText().toString();

						try {
							SmsManager smsManager = SmsManager.getDefault();
							smsManager.sendTextMessage(phoneNo, null, sms, null, null);
							Toast.makeText(getApplicationContext(), "SMS Sent!",
									Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(),
									"SMS faild, please try again later!",
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}
				}

			});
		}
		
		
		//By using this button, user would be able to send location coordinates with SMS
		sendLocationButton = (Button)findViewById(R.id.sendLocation);
		if (sendLocationButton != null){
			sendLocationButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Log.i(TAG,"this is working here.");

					if (numberEntry.getText().toString().matches("")){
						Toast.makeText(getApplicationContext(), 
								"You need a receiver!", Toast.LENGTH_LONG).show();
					}else{
						String phoneNo = numberEntry.getText().toString();
						String sms = locationText.getText().toString();

						try {
							//set up a smsManager to send message
							SmsManager smsManager = SmsManager.getDefault();
							smsManager.sendTextMessage(phoneNo, null, sms, null, null);
							Toast.makeText(getApplicationContext(), "SMS Sent!",
									Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(),
									"SMS faild, please try again later!",
									Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}
				}

			});
		}

		return v;
	}

	public void launchContactPicker(View view) {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
	}

	
	
	//This method is a contact handler which picks certain fields in the contact
	//list in device and lets user choose which contact to pick. It also pass the
	//wanted information (in this case, phone number) to an intent. 
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		switch (reqCode) {
		case (CONTACT_PICKER_RESULT) :
			if (resultCode == RESULT_OK) {
				switch (reqCode) {
				case CONTACT_PICKER_RESULT:
					// handle contact results
					Cursor cursor = null;
					String phoneNum="";
					try{
						Uri result = data.getData();
						Log.v(TAG, "Got a contact result: "
								+ result.toString());
						// get the contact id from the Uri
						String id = result.getLastPathSegment();
						// query for everything email
						cursor = getContentResolver().query(Phone.CONTENT_URI,
								null, Phone.CONTACT_ID + "=?", new String[] { id },
								null);

						int idx = cursor.getColumnIndex(Phone.DATA);

						// let's just get the first email
						if (cursor.moveToFirst()) {
							phoneNum = cursor.getString(idx);
							Log.v(TAG, "Got number: " + phoneNum);
						} else {
							Log.w(TAG, "No results");
						}                  
					}catch (Exception e) {
						Log.e(TAG, "Failed to get number data", e);

					}finally{
						if (cursor != null) {
							cursor.close();
						}
						//put the number chosen from contact to textEdit view
						numberEntry = (EditText) findViewById(R.id.editNumber);
						numberEntry.setText(phoneNum);
						if (phoneNum.length() == 0) {
							Toast.makeText(this, "No phone number found for contact.",
									Toast.LENGTH_LONG).show();
						}
					}
					break;
				}

			}else{
				// gracefully handle failure
				Log.w(TAG, "Warning: activity result not ok.");
			}

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_display_message);
		//setContentView(R.layout.fragment_display_message);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();




		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_display_message,
					container, false);
			return rootView;
		}
	}

}
