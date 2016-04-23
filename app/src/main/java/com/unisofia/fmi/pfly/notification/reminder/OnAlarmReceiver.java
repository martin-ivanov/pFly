package com.unisofia.fmi.pfly.notification.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.util.Log;

/**
 * Created by martin on 4/23/16.
 */
public class OnAlarmReceiver extends BroadcastReceiver{

    private static final String TAG = ComponentInfo.class.getCanonicalName();


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received wake up from alarm manager.");

        long rowid = intent.getExtras().getLong(RemindersDbHelper.KEY_ROWID);

        WakeReminderIntentService.acquireStaticLock(context);

        Intent i = new Intent(context, ReminderService.class);
        i.putExtra(RemindersDbHelper.KEY_ROWID, rowid);
        context.startService(i);

    }
}
