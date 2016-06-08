package com.unisofia.fmi.pfly.notification.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.ui.fragment.ReminderFragment;
import com.unisofia.fmi.pfly.ui.fragment.TaskFragment;

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
        Log.d("Marto", messageType);
        Log.d("marto", extras + "");

//		TODO handle GCM payload
        if (!extras.isEmpty()) { // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
//			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//				//				sendNotification("Send error: " + extras.toString());
//			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
//				//				sendNotification("Deleted messages on server: " + extras.toString());
//				// If it's a regular GCM message, do some work.
//			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//				DataManager.getInstance().refreshUserData(this, null);
//
//				// Post notification of received message.
            String title = extras.getString("title", "pFly");
            String message = extras.getString("message", "No description");
            String taskId = extras.getString("taskId");
            String taskActionType = extras.getString("taskActionType");
            sendNotification(taskId, title, message, taskActionType);
//			}
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private String buildNotificationTitle(String taskName, String taskActionType){
        StringBuilder sb = new StringBuilder();
        sb.append("Task \"").append(taskName).append("\" was ");
        if (taskActionType.equalsIgnoreCase("delegate")){
            sb.append("delegated");
        } else {
            sb.append("transferred");
        }
        sb.append(" to you");
        return sb.toString();
    }

    private String buildNotificationMessage(String taskId, String message){
        StringBuilder sb = new StringBuilder();
        sb.append("Task id: ").append(taskId).append("; ")
                .append("Description: ").append(message);
        return sb.toString();
    }

    // Put the message into a notification and post it.
    private void sendNotification(String taskId, String taskName, String message, String taskActionType) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(buildNotificationTitle(taskName, taskActionType))
                .setContentText(buildNotificationMessage(taskId, message))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setOngoing(true)
                .setSmallIcon(R.drawable.pfly_logo);

        //Accept intent
        Intent acceptIntent = new Intent();
        acceptIntent.setAction("acceptIntent");
        acceptIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent pendingAcceptIntent = PendingIntent.getBroadcast(this, 12345, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action acceptAction = new NotificationCompat.Action.Builder(R.drawable.confirm, "Accept", pendingAcceptIntent).build();
        builder.addAction(acceptAction);

        //Decline intent
        Intent declineIntent = new Intent();
        declineIntent.setAction("declineIntent");
        declineIntent.putExtra("taskId", taskId);
        declineIntent.putExtra("taskActionType", taskActionType);
        declineIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent pendingDeclineIntent = PendingIntent.getBroadcast(this, 12345, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action declineAction = new NotificationCompat.Action.Builder(R.drawable.close, "Decline", pendingDeclineIntent).build();
        builder.addAction(declineAction);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }
}
