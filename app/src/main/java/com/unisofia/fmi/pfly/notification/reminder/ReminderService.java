package com.unisofia.fmi.pfly.notification.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.ui.fragment.ReminderFragment;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by martin on 4/22/16.
 */
public class ReminderService extends WakeReminderIntentService {


    public ReminderService() {
        super("ReminderSvc");
    }

    @Override
    void doReminderWork(Intent intent) {
//        Long rowId = intent.getExtras().getLong(RemindersDbHelper.KEY_ROWID);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, ReminderFragment.class);
//        notificationIntent.putExtra(RemindersDbHelper.KEY_ROWID, rowId);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder builder;
        builder = new Notification.Builder(getApplicationContext())
                .setContentTitle("Title")
                .setContentText("notification")
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher);
        Notification notification = builder.build();

        notificationManager.notify(1, notification);
    }
}
