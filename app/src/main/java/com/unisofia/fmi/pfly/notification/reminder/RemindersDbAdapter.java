package com.unisofia.fmi.pfly.notification.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple reminder database access helper class.
 * Defines the basic CRUD operations (Create, Read, Update, Delete)
 * for the example, and gives the ability to list all reminders as well as
 * retrieve or modify a specific reminder.
 */
public class RemindersDbAdapter {

    private static final String TAG = "ReminderDbAdapter";
    private RemindersDbHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation SQL statement
     */



    private final Context mCtx;

    public RemindersDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public RemindersDbAdapter open() throws SQLException {
        mDbHelper = new RemindersDbHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }



}
