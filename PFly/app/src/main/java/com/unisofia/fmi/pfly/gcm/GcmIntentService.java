package com.unisofia.fmi.pfly.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {

	public static final String TAG = "GCM Demo";
	public static final int NOTIFICATION_ID = 1;

	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("pFlyGcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

//		TODO handle GCM payload
//		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
//			/*
//			 * Filter messages based on message type. Since it is likely that
//			 * GCM will be extended in the future with new message types, just
//			 * ignore any message types you're not interested in, or that you
//			 * don't recognize.
//			 */
//			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//				//				sendNotification("Send error: " + extras.toString());
//			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
//				//				sendNotification("Deleted messages on server: " + extras.toString());
//				// If it's a regular GCM message, do some work.
//			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//				DataManager.getInstance().refreshUserData(this, null);
//
//				// Post notification of received message.
//				String type = extras.getString("type", "trade");
//				String title = extras.getString("title", "Work Table");
//				String message = extras.getString("message", "Message was not decoded!");
//				sendNotification(type, title, message);
//			}
//		}
//		// Release the wake lock provided by the WakefulBroadcastReceiver.
//		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	// Put the message into a notification and post it.
	private void sendNotification(String type, String title, String message) {
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//		TODO
//		Intent intent = new Intent(this, WorkTableActivity.class);
//		intent.putExtra(WorkTableActivity.ARG_NOTIFICATION_TYPE, type);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//				.setSmallIcon(R.drawable.ic_launcher).setContentTitle(title)
//				.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//				.setContentText(message).setAutoCancel(true).setTicker(message);
//		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//		mBuilder.setSound(alarmSound);
//
//		mBuilder.setContentIntent(contentIntent);
//		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}
