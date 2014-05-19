package com.cai.family_finder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.cai.map.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * @file SmsReceiver.java
 * @author yishengcai
 * @version 1.0.0   05/04/14
 * This class contains the sms receiver function which receives the message and 
 * inform the notifier in android system. It also contains a method that process 
 * the message to store them in an intent and pass it to the main class. 
 */
public class SmsReceiver extends BroadcastReceiver {
	private static final String TAG = "SmsReceiver";
	public final static String EXTRA_MESSAGE="com.cai.family_finder.SMS";
	public final static String EXTRA_SENDER="com.cai.family_finder.PHONENO";
	private static int mNextNotificationId = 1;
	private Context mContext;
	private NotificationCompat.Builder mBuilder; 


	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mContext = context;
		Bundle bundle = intent.getExtras();
		Map<String, String> msgTexts = new LinkedHashMap<String, String>();
		if (bundle != null){
			Object[] pdus = (Object[]) bundle.get("pdus");

			// Get the received messages -- a PDU is a 7-bit protocol 
			//  description unit

			for (Object pdu : pdus) {
				SmsMessage message =SmsMessage.createFromPdu((byte[]) pdu);			  
				String incomingMsg = message.getMessageBody();
				String originatingNumber = message.getOriginatingAddress();
				Log.i(TAG, "Rcvd: from " + originatingNumber + " Msg: " + 
						incomingMsg);
				if (incomingMsg.contains("lat/lng: (")){
					msgTexts.put(originatingNumber, incomingMsg);
				}

			}
		}
		
			processMessages(msgTexts, mNextNotificationId);
		
	}
	
	//This function process messages: put messages in the intent and leave the intent for 
	//Main class to execute. 
	public void processMessages( Map<String, String> msgTexts, 
            int notificationId ){		
	  for (Entry<String, String> entry : msgTexts.entrySet()) {
		Log.i(TAG, "Processing message: " + entry.getValue());
			
		// Set up intent for processing message in SmsViewActivity
		Intent notificationIntent = 
              new Intent(mContext, MainMapActivity.class);
		notificationIntent.putExtra(EXTRA_MESSAGE, entry.getValue());
		notificationIntent.putExtra(EXTRA_SENDER, entry.getKey());
		notificationIntent.putExtra("notificationid", notificationId);
			
		// Set up pending intent
		PendingIntent resultPendingIntent = 
			PendingIntent.getActivity(mContext, 
					0, 
					notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);  
			
		// Build the notification
		mBuilder = new NotificationCompat.Builder(mContext)
			.setSmallIcon(R.drawable.ic_launcher)
		      .setTicker("SMS received")
		      .setContentTitle("SMS Received")
		      .setContentIntent(resultPendingIntent)
		      .setContentText("from " + entry.getKey())
		      .setWhen(System.currentTimeMillis());
						
		// Notify the user
		NotificationManager notificationMgr = 
			(NotificationManager)           
                   mContext.getSystemService(Context.NOTIFICATION_SERVICE);	
		notificationMgr.notify(notificationId++, mBuilder.build());		
	   }
	}

}
