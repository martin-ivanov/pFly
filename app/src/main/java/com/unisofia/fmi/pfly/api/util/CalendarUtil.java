package com.unisofia.fmi.pfly.api.util;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.ui.activity.WelcomeActivity;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import static android.provider.CalendarContract.Events;
import static android.provider.CalendarContract.Reminders;
import static android.provider.CalendarContract.Calendars;

/**
 * Created by martin.ivanov on 2016-06-07.
 */
public class CalendarUtil {

    public static void addTaskToCalendar(Context context, Task task) {
        Long eventId = null;
        //TODO: add condition for task to not be closed
        if (task.getEventId() == null) {
            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();

            values.put(Events.DTSTART, task.getDateCreated().getTime());
            values.put(Events.DURATION, calculateDuration(task).toString());
            values.put(Events.ALL_DAY, 1);
            values.put(Events.TITLE, task.getName());
            if (task.getDescription() != null) {
                values.put(CalendarContract.Events.DESCRIPTION, task.getDescription());
            }
            values.put(Events.CALENDAR_ID, 1);
            values.put(Events.EVENT_TIMEZONE, Calendars.CALENDAR_TIME_ZONE);
            values.put(Events.ACCESS_LEVEL, Events.ACCESS_PUBLIC);

            int permissionCheck = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_CALENDAR);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Uri insertUri = cr.insert(Events.CONTENT_URI, values);
                eventId = Long.parseLong(insertUri.getLastPathSegment());
                task.setEventId(eventId);
                Toast.makeText(context, "Created Calendar Event " + eventId, Toast.LENGTH_SHORT).show();
                addReminders(context, task);
                forceSync();
            }
        } else {
            updateTaskEvent(context, task);
        }
    }

    private static Period calculateDuration(Task task){
        LocalDate localStart = new LocalDate(task.getDateCreated());
//        localStart.plusDays(1);
        LocalDate localDeadline = new LocalDate(task.getDeadline());
        Period period = new Period(localStart, localDeadline);
        Log.d("Marto", "days between " + localStart + " and " + localDeadline + " is " + period.getDays() + "days");
        return period.plusDays(2);
    }

    private static void addReminders(Context context, Task task) {
        if (task.getLastResponsibleMoment() != null) {
            ContentResolver cr = context.getContentResolver();
            ContentValues reminderValues = new ContentValues();
            LocalDate localDeadline = new LocalDate(task.getDeadline());
            LocalDate localLastResponsibleMoment = new LocalDate(task.getLastResponsibleMoment());
            Period period = new Period(localLastResponsibleMoment, localDeadline);
            int gap = period.getDays();
            if (gap > 0) {
                reminderValues.put(Reminders.MINUTES, (gap-1) * 1440 + 840);
            }

            reminderValues.put(Reminders.EVENT_ID, task.getEventId());
            reminderValues.put(Reminders.METHOD, Reminders.METHOD_DEFAULT);
            int permissionCheck = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_CALENDAR);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);
                Log.i("Marto", "reminders added: " + uri.toString());
            }
        }
    }


    public static void deleteTaskFromCalendar(Context context, Task task){
        ContentResolver cr = context.getContentResolver();
        Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, task.getEventId());
        int rows = cr.delete(deleteUri, null, null);
        Log.i("Marto", "Rows deleted: " + rows);
        forceSync();
    }

    private static void updateTaskEvent(Context context, Task task){
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.TITLE, task.getName());
        if (task.getDescription() != null) {
            values.put(Events.DESCRIPTION, task.getDescription());
        }
        values.put(Events.DURATION, calculateDuration(task).toString());
        values.put(Events.DTEND, (String)null);
        Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, task.getEventId());
        int rows = cr.update(updateUri, values, null, null);
        Log.i("marto", "Rows updated: " + rows);
        addReminders(context, task);
        forceSync();
    }

    private static void forceSync() {
        Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        AccountManager am = AccountManager.get(WelcomeActivity.getAppContext());
        android.accounts.Account[] acc = am.getAccountsByType("com.google");
        android.accounts.Account account = null;
        if (acc.length > 0) {
            account = acc[0];
            ContentResolver.requestSync(account, "com.android.calendar", extras);
        }
    }
}
