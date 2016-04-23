
package com.unisofia.fmi.pfly.ui.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.notification.reminder.ReminderManager;
import com.unisofia.fmi.pfly.notification.reminder.RemindersContentProvider;
import com.unisofia.fmi.pfly.notification.reminder.RemindersDbAdapter;
import com.unisofia.fmi.pfly.notification.reminder.RemindersDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReminderFragment extends DialogFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {


    //    private static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";


    RemindersContentProvider contentProvider = new RemindersContentProvider();

    private EditText mReminderName;
    private EditText mReminderDate;
    private Button createReminderButton;
    private Button cancelButton;

    private String taskName;
    private String taskDeadline;

    private Long mRowId;
    private RemindersDbAdapter mDbHelper;
//    private Calendar mCalendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskName = getArguments().getString("taskName");
        taskDeadline = getArguments().getString("taskDeadline");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.new_reminder);
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(1, null, this);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mReminderName = (EditText) view.findViewById(R.id.reminderName);
        mReminderDate = DatePickerFragment.setDatePicker(getActivity(), view, R.id.reminderDate);

        if (taskName !=null && !"".equals(taskName)){
            mReminderName.setText(taskName);
        }

        if (taskDeadline !=null && !"".equals(taskDeadline)){
            mReminderDate.setText(taskDeadline);
        }

        createReminderButton = (Button) view.findViewById(R.id.create_reminder);
        createReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReminder(mReminderName.getText().toString(), "", mReminderDate.getText().toString());
                dismiss();
            }
        });
        cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setRowIdFromIntent() {
        if (mRowId == null) {
            Bundle extras = getActivity().getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(RemindersDbHelper.KEY_ROWID)
                    : null;

        }
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(RemindersDbHelper.KEY_ROWID, mRowId);
    }


    private void saveState(String reminderName, String reminderDate) {
        String title = mReminderName.getText().toString();
//        String body = mBodyText.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(reminderDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (mRowId == null) {

            Uri id = createReminder(title, "", reminderDate);
//            if (id > 0) {
//                mRowId = id;
//            }
        } else {
            updateReminder(mRowId, title, "", reminderDate);
        }


        new ReminderManager(getActivity()).setReminder(mRowId, c);
    }


    /**
     * Create a new reminder using the title, body and reminder date time provided.
     * If the reminder is  successfully created return the new rowId
     * for that reminder, otherwise return a -1 to indicate failure.
     *
     * @param title            the title of the reminder
     * @param body             the body of the reminder
     * @param reminderDateTime the date and time the reminder should remind the user
     * @return rowId or -1 if failed
     */
    public Uri createReminder(String title, String body, String reminderDateTime) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(RemindersDbHelper.KEY_TITLE, title);
        initialValues.put(RemindersDbHelper.KEY_BODY, body);
        initialValues.put(RemindersDbHelper.KEY_DATE_TIME, reminderDateTime);

        return contentProvider.insert(RemindersContentProvider.CONTENT_URI, initialValues);

    }

    /**
     * Delete the reminder with the given rowId
     *
     * @param rowId id of reminder to delete
     * @return true if deleted, false otherwise
     */
    public int deleteReminder(long rowId) {

        String mSelectionClause = RemindersDbHelper.KEY_ROWID + "=?";
        String[] mSelectionArgs = {rowId + ""};
        return contentProvider.delete(
                RemindersContentProvider.CONTENT_URI,
                mSelectionClause,
                mSelectionArgs);

//        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all reminders in the database
     *
     * @return Cursor over all reminders
     */
    public Cursor fetchAllReminders() {

        String[] mProjection =
                {
                        RemindersDbHelper.KEY_ROWID,
                        RemindersDbHelper.KEY_TITLE,
                        RemindersDbHelper.KEY_DATE_TIME
                };
        return contentProvider.query(
                RemindersContentProvider.CONTENT_URI,
                mProjection, null, null, null);
    }

    /**
     * Return a Cursor positioned at the reminder that matches the given rowId
     *
     * @param rowId id of reminder to retrieve
     * @return Cursor positioned to matching reminder, if found
     * @throws SQLException if reminder could not be found/retrieved
     */
    public Cursor fetchReminder(long rowId) throws SQLException {
        String[] mProjection =
                {
                        RemindersDbHelper.KEY_ROWID,
                        RemindersDbHelper.KEY_TITLE,
                        RemindersDbHelper.KEY_DATE_TIME
                };

        String mSelectionClause = RemindersDbHelper.KEY_ROWID + "=?";
        String[] mSelectionArgs = {rowId + ""};
        Cursor mCursor = contentProvider.query(
                RemindersContentProvider.CONTENT_URI,
                mProjection, mSelectionClause, mSelectionArgs, null);


        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the reminder using the details provided. The reminder to be updated is
     * specified using the rowId, and it is altered to use the title, body and reminder date time
     * values passed in
     *
     * @param rowId            id of reminder to update
     * @param title            value to set reminder title to
     * @param body             value to set reminder body to
     * @param reminderDateTime value to set the reminder time.
     * @return true if the reminder was successfully updated, false otherwise
     */
    public boolean updateReminder(long rowId, String title, String body, String reminderDateTime) {
        ContentValues mUpdateValues = new ContentValues();
        mUpdateValues.put(RemindersDbHelper.KEY_TITLE, title);
        mUpdateValues.put(RemindersDbHelper.KEY_DATE_TIME, reminderDateTime);

        String mSelectionClause = RemindersDbHelper.KEY_ROWID + "=?";
        String[] mSelectionArgs = {rowId + ""};

        return contentProvider.update(
                RemindersContentProvider.CONTENT_URI,
                mUpdateValues,
                mSelectionClause,
                mSelectionArgs) > 0;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] mProjection =
                {
                        RemindersDbHelper.KEY_ROWID,
                        RemindersDbHelper.KEY_TITLE,
                        RemindersDbHelper.KEY_DATE_TIME
                };
        String mSelectionClause = RemindersDbHelper.KEY_ROWID + "=?";
        String[] mSelectionArgs = {id + ""};
        return new CursorLoader(getActivity(), RemindersContentProvider.CONTENT_URI, mProjection, mSelectionClause, mSelectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data!=null && data.moveToFirst()) {
            mReminderName.setText(
                    data.getString(
                            data.getColumnIndexOrThrow(RemindersDbHelper.KEY_TITLE)));
            mReminderDate.setText(
                    data.getString(
                        data.getColumnIndexOrThrow(RemindersDbHelper.KEY_DATE_TIME)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
