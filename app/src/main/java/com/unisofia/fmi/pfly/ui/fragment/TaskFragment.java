package com.unisofia.fmi.pfly.ui.fragment;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.Spinner;
import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.ui.activity.WelcomeActivity;

import com.rey.material.widget.Slider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.unisofia.fmi.pfly.api.model.Task.TaskAction;

public class TaskFragment extends BaseMenuFragment {
    private EditText name;
    private EditText description;

    private Spinner dependentTask;
    private Spinner assignedUser;
    private Spinner project;
    private EditText deadline;
    private EditText dateCreated;
    private EditText dateFinished;
    private EditText lastResponsibleMoment;
    private CheckBox simplicity;
    private Slider simplicityBar;
    private CheckBox intImportance;
    private Slider intImportanceBar;
    private CheckBox extImportance;
    private Slider extImportanceBar;
    private CheckBox closeness;
    private Slider closenessBar;
    private CheckBox clearness;
    private Slider clearnessBar;
    private SharedPreferences prefs = null;
    private View fragmentView = null;
    private boolean isRangeEnabled = false;
    private TableLayout rangesTable;

    private Task loadedTask = null;
//    private Calendar calendar = Calendar.getInstance();

    private Spinner actionSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_pfly_save_task, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_task:
                setTaskValues();
                addTaskToCalendar(loadedTask);
                return true;
            case R.id.reminder_task:
                ReminderFragment reminderFragment = new ReminderFragment();
                Bundle args = new Bundle();
                args.putString("taskName", name.getText().toString());
                args.putString("taskDeadline", deadline.getText().toString());
                reminderFragment.setArguments(args);
                reminderFragment.show(getActivity().getSupportFragmentManager(), "ReminderDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentView = view;
        prefs = setPrefs();

        name = (EditText) view.findViewById(R.id.name);
        description = (EditText) view.findViewById(R.id.description);

        isRangeEnabled = prefs.getBoolean("fly_weight_switch", true);
        setFlyCharacteristics();

        setDatePickers();

        dependentTask = setSpinner(R.id.dependOn, R.array.tasks_array);
        assignedUser = setSpinner(R.id.assignedUser, R.array.users_array);
        project = setSpinner(R.id.project, R.array.projects_array);


        actionSpinner = (Spinner) view.findViewById(R.id.actionSpinner);
        actionSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                TaskAction.values()));

        actionSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                assignedUser.setVisibility(View.GONE);
                lastResponsibleMoment.setVisibility(View.GONE);

                if (position == 1 || position == 2) {
                    assignedUser.setVisibility(View.VISIBLE);
                } else if (position == 3) {
                    lastResponsibleMoment.setVisibility(View.VISIBLE);
                }
            }
        });


        Bundle args = this.getArguments();
        if (args != null) {
            loadedTask = (Task) args.getSerializable("task");
            if (loadedTask != null) {
                Toast.makeText(getActivity(), "task loaded", Toast.LENGTH_LONG).show();
                getTaskValues();
            }
        }
    }

    private Spinner setSpinner(int spinnerId, int arrayId) {
        Spinner spinner = (Spinner) fragmentView.findViewById(spinnerId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        return spinner;
    }

    private void setDatePickers() {
        deadline = DatePickerFragment.setDatePicker(getActivity(), fragmentView, R.id.deadline);
        dateCreated = DatePickerFragment.setDatePicker(getActivity(), fragmentView, R.id.dateCreated);
        dateFinished = DatePickerFragment.setDatePicker(getActivity(), fragmentView, R.id.dateFinished);
        lastResponsibleMoment = DatePickerFragment.setDatePicker(getActivity(), fragmentView, R.id.lastResponsibleMoment);
    }

    private Slider setSlider(View view, int barId, String minPref, String maxPref) {
        Slider bar = (Slider) view.findViewById(barId);
        int minValue = Integer.parseInt(prefs.getString(minPref, "0"));
        int maxValue = Integer.parseInt(prefs.getString(maxPref, "40"));
        bar.setValueRange(minValue, maxValue, true);
        bar.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                calculateFlyScore();
            }
        });
        return bar;
    }

    private void setFlyCharacteristics() {
        intImportance = (CheckBox) fragmentView.findViewById(R.id.intImportance);
        intImportance.setOnClickListener(new CheckBoxListener());

        extImportance = (CheckBox) fragmentView.findViewById(R.id.extImportance);
        extImportance.setOnClickListener(new CheckBoxListener());

        simplicity = (CheckBox) fragmentView.findViewById(R.id.simplicity);
        simplicity.setOnClickListener(new CheckBoxListener());

        closeness = (CheckBox) fragmentView.findViewById(R.id.closeness);
        closeness.setOnClickListener(new CheckBoxListener());

        clearness = (CheckBox) fragmentView.findViewById(R.id.clearness);
        clearness.setOnClickListener(new CheckBoxListener());

        simplicityBar = setSlider(fragmentView, R.id.simplicityBar, "simplicityMinPref",
                "simplicityMaxPref");
        closenessBar = setSlider(fragmentView, R.id.closenessBar, "closenessMinPref",
                "closenessMaxPref");
        intImportanceBar = setSlider(fragmentView, R.id.intImportanceBar, "intImportanceMinPref",
                "intImportanceMaxPref");
        extImportanceBar = setSlider(fragmentView, R.id.extImportanceBar, "extImportanceMinPref",
                "extImportanceMaxPref");

        clearnessBar = setSlider(fragmentView, R.id.clearnessBar, "clearnessMinPref",
                "clearnessMaxPref");

        if (isRangeEnabled) {
            rangesTable = (TableLayout) fragmentView.findViewById(R.id.rangesLayout);
            rangesTable.setVisibility(View.VISIBLE);
        }
    }

    private SharedPreferences setPrefs() {
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_int_importance, false);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_ext_importance, false);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_closeness, false);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_clearness, false);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_simplicity, false);
        return PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    private int booleanToInt(boolean isChecked) {
        int isCheckedInt = (isChecked) ? 1 : 0;
        return isCheckedInt;
    }

    private void calculateFlyScore() {
        TextView flyScore = (TextView) fragmentView.findViewById(R.id.fly_score);
        float score = 0;
        if (isRangeEnabled) {
            score = intImportanceBar.getExactValue() * booleanToInt(intImportance.isChecked()) +
                    extImportanceBar.getExactValue() * booleanToInt(extImportance.isChecked()) +
                    simplicityBar.getExactValue() * booleanToInt(simplicity.isChecked()) +
                    clearnessBar.getExactValue() * booleanToInt(clearness.isChecked()) +
                    closenessBar.getExactValue() * booleanToInt(closeness.isChecked());
        } else {
            score = Integer.parseInt(prefs.getString("intImportanceWeightPref", ""))
                    * booleanToInt(intImportance.isChecked()) +
                    Integer.parseInt(prefs.getString("extImportanceWeightPref", ""))
                            * booleanToInt(extImportance.isChecked()) +
                    Integer.parseInt(prefs.getString("simplicityWeightPref", ""))
                            * booleanToInt(simplicity.isChecked()) +
                    Integer.parseInt(prefs.getString("clearnessWeightPref", ""))
                            * booleanToInt(clearness.isChecked()) +
                    Integer.parseInt(prefs.getString("closenessWeightPref", ""))
                            * booleanToInt(closeness.isChecked());
        }

        if (score > 0) {
            flyScore.setText(score + "");
        }
    }


    private TaskAction recommendAction() {
        if (!intImportance.isChecked() && !extImportance.isChecked()) {
            return TaskAction.TRASH_NOTIFY;
        }
        if (!intImportance.isChecked()) {
            return TaskAction.TRANSFER_NOTIFY;
        }
        if (!simplicity.isChecked() && !closeness.isChecked()) {
            return TaskAction.DELEGATE_FOLLOW_UP;
        }
        if (simplicity.isChecked() && !closeness.isChecked()) {
            return TaskAction.SCHEDULE_DEFER;
        }
        if (!clearness.isChecked()) {
            return TaskAction.CLARIFY;
        }
        if (!simplicity.isChecked() && closeness.isChecked()) {
            return TaskAction.SIMPLIFY;
        }

        return TaskAction.EXECUTE;

    }

    private void getTaskValues() {
        name.setText(loadedTask.getName());
        description.setText(loadedTask.getDescription());
    }

    private void setTaskValues() {
        loadedTask.setName(name.getText().toString());
        loadedTask.setDescription(description.getText().toString());

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        try {
            loadedTask.setTaskDeadline(sdf.parse(deadline.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void addTaskToCalendar(Task task) {
        ContentResolver cr = getActivity().getContentResolver();

        if (task.getName() != null && task.getTaskDeadline() != null) {
//            String[] projection = new String[]{
//                            CalendarContract.Calendars._ID,
//                            CalendarContract.Calendars.NAME,
//                            CalendarContract.Calendars.ACCOUNT_NAME,
//                            CalendarContract.Calendars.ACCOUNT_TYPE};
//
//            Cursor calCursor = cr.query(CalendarContract.Calendars.CONTENT_URI, projection,
//                    CalendarContract.Calendars.VISIBLE + " = 1", null, CalendarContract.Calendars._ID + " ASC");
//            if (calCursor.moveToFirst()) {
//                do {
//                    long id = calCursor.getLong(0);
//                    String displayName = calCursor.getString(1);
//                    Log.d("calendarName", displayName);
//                } while (calCursor.moveToNext());
//            }


            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis());
//            values.put(CalendarContract.Events.DURATION, "P3D");
            Calendar c = Calendar.getInstance();
            c.setTime(task.getTaskDeadline());
            c.add(Calendar.DATE, 2);
            values.put(CalendarContract.Events.DTEND, c.getTime().getTime());
            values.put(CalendarContract.Events.ALL_DAY, 1);
            values.put(CalendarContract.Events.TITLE, task.getName());
            if (task.getDescription() != null) {
                values.put(CalendarContract.Events.DESCRIPTION, task.getDescription());
            }
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_END_TIMEZONE, CalendarContract.Calendars.CALENDAR_TIME_ZONE);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, CalendarContract.Calendars.CALENDAR_TIME_ZONE);
            values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PUBLIC);

            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_CALENDAR);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Uri insertUri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                int id = Integer.parseInt(insertUri.getLastPathSegment());
                Toast.makeText(getActivity(), "Created Calendar Event " + id, Toast.LENGTH_SHORT).show();
                forceSync();
            }
        }
    }

    private void forceSync(){
        Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        AccountManager am = AccountManager.get(WelcomeActivity.getAppContext());
        Account[] acc = am.getAccountsByType("com.google");
        Account account = null;
        if (acc.length>0) {
            account=acc[0];
            ContentResolver.requestSync(account, "com.android.calendar", extras);
        }

    }

    private class CheckBoxListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            calculateFlyScore();
            TextView recommendedAction = (TextView) fragmentView.findViewById(R.id.recommended_action);
            recommendedAction.setText(recommendAction().toString());
        }
    }

}