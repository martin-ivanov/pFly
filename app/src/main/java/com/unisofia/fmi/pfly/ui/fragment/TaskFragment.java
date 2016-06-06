package com.unisofia.fmi.pfly.ui.fragment;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rey.material.widget.Slider;
import com.rey.material.widget.Spinner;
import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.account.UserManager;
import com.unisofia.fmi.pfly.api.ApiConstants;
import com.unisofia.fmi.pfly.api.RequestManager;
import com.unisofia.fmi.pfly.api.model.Account;
import com.unisofia.fmi.pfly.api.util.DateSerializer;
import com.unisofia.fmi.pfly.api.model.Project;
import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;
import com.unisofia.fmi.pfly.api.request.RequestErrorListener;
import com.unisofia.fmi.pfly.api.request.get.BaseGetRequest;
import com.unisofia.fmi.pfly.api.request.post.BasePostRequest;
import com.unisofia.fmi.pfly.ui.activity.WelcomeActivity;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.unisofia.fmi.pfly.api.model.Task.TaskAction;
import static android.provider.CalendarContract.Events;
import static android.provider.CalendarContract.Reminders;
import static android.provider.CalendarContract.Calendars;

public class TaskFragment extends BaseMenuFragment {
    private TextView taskId;
    private EditText name;
    private EditText description;

    private EditText deadline;
    private EditText desiredOutcome;
    private EditText notes;
    private EditText dateCreated;
    private EditText dateFinished;
    private EditText lastResponsibleMoment;

    private CheckBox simplicity;
    private Slider simplicityBar;
    private int simplicityValue;

    private CheckBox intImportance;
    private Slider intImportanceBar;
    private int intImportanceValue;

    private CheckBox extImportance;
    private Slider extImportanceBar;
    private int extImportanceValue;

    private CheckBox closeness;
    private Slider closenessBar;
    private int closenessValue;

    private CheckBox clearness;
    private Slider clearnessBar;
    private int clearnessValue;

    private List<CheckBox> flyCharacteristics;
    private List<Slider> flyCharacteristicsRanges;

    private Spinner dependentTaskSpinner;
    private Spinner assignedUser;
    private Spinner projectSpinner;

    private Spinner actionSpinner;
    private int flyScore;
    private Long eventId;

    SimpleDateFormat sdf = new SimpleDateFormat(DatePickerFragment.DATE_FORMAT_PATTERN);
    private Map<String, ?> prefs = null;
    private View fragmentView = null;
    private boolean isRangeEnabled = false;
    private Task loadedTask = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getProjects(getActivity());
        getUsers(getActivity());
        getTasks(getActivity());
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_pfly_save_task, menu);
        MenuItem eventOption = menu.findItem(R.id.open_calendar_event);
        if (loadedTask != null && loadedTask.getEventId() != null) {
            eventOption.setVisible(true);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_calendar_event:
                Intent eventIntent = new Intent(Intent.ACTION_VIEW);
                eventIntent.setData(Uri.parse("content://com.android.calendar/events/"
                        + String.valueOf(loadedTask.getEventId())));
                startActivity(eventIntent);
                return true;
            case R.id.save_task:
                saveTask();
                return true;
//            case R.id.reminder_task:
//                setReminder();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentView = view;
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()).getAll();
        taskId = (TextView) view.findViewById(R.id.taskId);
        name = (EditText) view.findViewById(R.id.name);
        name.requestFocus();

        description = (EditText) view.findViewById(R.id.description);
        notes = (EditText) view.findViewById(R.id.notes);
        desiredOutcome = (EditText) view.findViewById(R.id.desired_outcome);

        isRangeEnabled = Boolean.parseBoolean(prefs.get("fly_weight_switch").toString());
        setFlyCharacteristics();
        setDatePickers();

        actionSpinner = (Spinner) view.findViewById(R.id.actionSpinner);
        actionSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                TaskAction.values()));


        actionSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                performUiChangesOnActionChanged(position, assignedUser, lastResponsibleMoment);
                performUiChangesOnActionTaken();

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

        if (loadedTask == null) {
            loadedTask = new Task();
        }
    }

    private void performUiChangesOnActionChanged(Integer position, Spinner assignedUser, EditText lastResponsibleMoment) {
        if (position != null) {
            if (assignedUser != null) {
                assignedUser.setVisibility(View.GONE);
                if (position == 2 || position == 3) {
                    assignedUser.setVisibility(View.VISIBLE);
                }
            }


            if (lastResponsibleMoment != null) {
                lastResponsibleMoment.setVisibility(View.GONE);

                if (position == 4) {
                    lastResponsibleMoment.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void performUiChangesOnActionTaken() {
        for (CheckBox flyCharacteristic : flyCharacteristics) {
            flyCharacteristic.setEnabled(false);
        }

        for (Slider slider : flyCharacteristicsRanges) {
            slider.setVisibility(View.GONE);
        }
        if (loadedTask != null && loadedTask.getDateFinished() != null) {
            dateFinished.setText(sdf.format(loadedTask.getDateFinished()));
        } else {
            dateFinished.setText(sdf.format(Calendar.getInstance().getTime()));
        }
    }

    private Spinner setSpinner(int spinnerId, List<String> objects) {
        Spinner spinner = (Spinner) fragmentView.findViewById(spinnerId);
        if (objects != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_item, objects);
            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            spinner.setAdapter(adapter);
        }
        return spinner;
    }

    private void setDatePickers() {
        deadline = DatePickerFragment.setDatePicker(getActivity(), fragmentView, R.id.deadline);
        dateCreated = DatePickerFragment.setDatePicker(getActivity(), fragmentView, R.id.dateCreated);
        dateCreated.setText(sdf.format(Calendar.getInstance().getTime()));
        dateFinished = DatePickerFragment.setDatePicker(getActivity(), fragmentView, R.id.dateFinished);
        lastResponsibleMoment = DatePickerFragment.setDatePicker(getActivity(), fragmentView, R.id.lastResponsibleMoment);
    }

    private Slider setSlider(View view, int barId, String minPref, String maxPref) {
        Slider bar = (Slider) view.findViewById(barId);
        int minValue = Integer.parseInt(prefs.get(minPref).toString());
        int maxValue = Integer.parseInt(prefs.get(maxPref).toString());
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
        flyCharacteristics = new ArrayList<>();

        intImportance = (CheckBox) fragmentView.findViewById(R.id.intImportance);
        intImportance.setOnClickListener(new CheckBoxListener());
        flyCharacteristics.add(intImportance);
        extImportance = (CheckBox) fragmentView.findViewById(R.id.extImportance);
        extImportance.setOnClickListener(new CheckBoxListener());
        flyCharacteristics.add(extImportance);
        simplicity = (CheckBox) fragmentView.findViewById(R.id.simplicity);
        simplicity.setOnClickListener(new CheckBoxListener());
        flyCharacteristics.add(simplicity);
        closeness = (CheckBox) fragmentView.findViewById(R.id.closeness);
        closeness.setOnClickListener(new CheckBoxListener());
        flyCharacteristics.add(closeness);
        clearness = (CheckBox) fragmentView.findViewById(R.id.clearness);
        clearness.setOnClickListener(new CheckBoxListener());
        flyCharacteristics.add(clearness);

        flyCharacteristicsRanges = new ArrayList<>();
        simplicityBar = setSlider(fragmentView, R.id.simplicityBar, "simplicityMinPref",
                "simplicityMaxPref");
        flyCharacteristicsRanges.add(simplicityBar);
        closenessBar = setSlider(fragmentView, R.id.closenessBar, "closenessMinPref",
                "closenessMaxPref");
        flyCharacteristicsRanges.add(closenessBar);
        intImportanceBar = setSlider(fragmentView, R.id.intImportanceBar, "intImportanceMinPref",
                "intImportanceMaxPref");
        flyCharacteristicsRanges.add(intImportanceBar);
        extImportanceBar = setSlider(fragmentView, R.id.extImportanceBar, "extImportanceMinPref",
                "extImportanceMaxPref");
        flyCharacteristicsRanges.add(extImportanceBar);
        clearnessBar = setSlider(fragmentView, R.id.clearnessBar, "clearnessMinPref",
                "clearnessMaxPref");
        flyCharacteristicsRanges.add(clearnessBar);

        if (isRangeEnabled) {
            for (Slider slider : flyCharacteristicsRanges) {
                slider.setVisibility(View.VISIBLE);
            }
        }
    }

    private void calculateValues(View checkBoxView) {
        if (isRangeEnabled) {
            switch (checkBoxView.getId()) {
                case R.id.intImportance:
                    intImportanceValue = (intImportance.isChecked()) ? (int) intImportanceBar.getExactValue() : 0;
                    break;
                case R.id.extImportance:
                    extImportanceValue = (extImportance.isChecked()) ? (int) extImportanceBar.getExactValue() : 0;
                    break;
                case R.id.simplicity:
                    simplicityValue = (simplicity.isChecked()) ? (int) simplicityBar.getExactValue() : 0;
                    break;
                case R.id.clearness:
                    clearnessValue = (clearness.isChecked()) ? (int) clearnessBar.getExactValue() : 0;
                    break;
                case R.id.closeness:
                    closenessValue = (closeness.isChecked()) ? (int) closenessBar.getExactValue() : 0;
            }
        } else {
            switch (checkBoxView.getId()) {
                case R.id.intImportance:
                    intImportanceValue = (intImportance.isChecked()) ? Integer.parseInt(prefs.get("intImportanceWeightPref").toString()) : 0;
                    break;
                case R.id.extImportance:
                    extImportanceValue = (extImportance.isChecked()) ? Integer.parseInt(prefs.get("extImportanceWeightPref").toString()) : 0;
                    break;
                case R.id.simplicity:
                    simplicityValue = (simplicity.isChecked()) ? Integer.parseInt(prefs.get("simplicityWeightPref").toString()) : 0;
                    break;
                case R.id.clearness:
                    clearnessValue = (clearness.isChecked()) ? Integer.parseInt(prefs.get("clearnessWeightPref").toString()) : 0;
                    break;
                case R.id.closeness:
                    closenessValue = (closeness.isChecked()) ? Integer.parseInt(prefs.get("closenessWeightPref").toString()) : 0;
            }
        }
    }

    private void calculateFlyScore() {
        TextView flyScoreView = (TextView) fragmentView.findViewById(R.id.fly_score);
        flyScore = intImportanceValue + extImportanceValue + simplicityValue +
                clearnessValue + closenessValue;

        flyScoreView.setText(flyScore + "");
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
        taskId.setText("ID: " + loadedTask.getTaskId());
        name.setText(loadedTask.getName());
        description.setText(loadedTask.getDescription());
        desiredOutcome.setText(loadedTask.getDesiredOutcome());
        notes.setText(loadedTask.getNotes());
        actionSpinner.setSelection(loadedTask.getTakenAction());

        if (loadedTask.getTakenAction() == TaskAction.SCHEDULE_DEFER.getIndex()) {
            lastResponsibleMoment.setText(sdf.format(loadedTask.getLastResponsibleMoment()));
            performUiChangesOnActionChanged(loadedTask.getTakenAction(), null, lastResponsibleMoment);
        }

//        //fly characteristics
        intImportanceValue = (loadedTask.getIntImportance() != null) ? loadedTask.getIntImportance() : 0;
        if (intImportanceValue > 0) {
            intImportance.setChecked(true);
            intImportanceBar.setValue(intImportanceValue, false);
        }

        extImportanceValue = (loadedTask.getExtImportance() != null) ? loadedTask.getExtImportance() : 0;
        if (extImportanceValue > 0) {
            extImportance.setChecked(true);
            intImportanceBar.setValue(extImportanceValue, false);
        }

        simplicityValue = (loadedTask.getSimplicity() != null) ? loadedTask.getSimplicity() : 0;
        if (simplicityValue > 0) {
            simplicity.setChecked(true);
            simplicityBar.setValue(simplicityValue, false);
        }

        clearnessValue = (loadedTask.getClearness() != null) ? loadedTask.getClearness() : 0;
        if (clearnessValue > 0) {
            clearness.setChecked(true);
            clearnessBar.setValue(clearnessValue, false);
        }

        closenessValue = (loadedTask.getCloseness() != null) ? loadedTask.getCloseness() : 0;
        if (closenessValue > 0) {
            closeness.setChecked(true);
            closenessBar.setValue(closenessValue, false);
        }

        flyScore = loadedTask.getFlyScore();


        if (loadedTask.getDateCreated() != null) {
            dateCreated.setText(sdf.format(loadedTask.getDateCreated()));
        }

        if (loadedTask.getDateFinished() != null) {
            dateFinished.setText(sdf.format(loadedTask.getDateFinished()));
        }

        if (loadedTask.getDeadline() != null) {
            deadline.setText(sdf.format(loadedTask.getDeadline()));
        }


    }

    private void setTaskValues() {
        loadedTask.setName(name.getText().toString());
        loadedTask.setDescription(description.getText().toString());
        loadedTask.setDesiredOutcome(desiredOutcome.getText().toString());
        loadedTask.setNotes(notes.getText().toString());

        loadedTask.setAccount(UserManager.getLoggedAccount());
//        loadedTask.setProject((Project) projectSpinner.getSelectedItem());
//

        //int & ext importance
        loadedTask.setIntImportance(intImportanceValue);
        loadedTask.setExtImportance(extImportanceValue);
        loadedTask.setCloseness(closenessValue);
        loadedTask.setClearness(clearnessValue);
        loadedTask.setSimplicity(simplicityValue);
        loadedTask.setFlyScore(flyScore);
//        assignedUserId = assignedUserId.substring(assignedUserId.indexOf("[") + 1, assignedUserId.indexOf("]"));
        if (assignedUser != null) {
            String assignedUserId = getItemId(assignedUser.getSelectedItem());
            if (assignedUserId.length() > 0) {
                if (actionSpinner.getSelectedItem().toString().equals(TaskAction.DELEGATE_FOLLOW_UP.toString())) {
                    loadedTask.setDelegatedTo(Long.parseLong(assignedUserId));
                }

                if (actionSpinner.getSelectedItem().toString().equals(TaskAction.TRANSFER_NOTIFY.toString())) {
                    loadedTask.setTransferedTo(Long.parseLong(assignedUserId));
                }
            }
        }
        String dependentTaskId = getItemId(dependentTaskSpinner.getSelectedItem());
        if (dependentTaskId.length() > 0) {
            loadedTask.setDependOn(Long.parseLong(dependentTaskId));
        }

        loadedTask.setEventId(eventId);
//        loadedTask.setStatus();
        loadedTask.setTakenAction(actionSpinner.getSelectedItemPosition());


        try {
            if (!dateCreated.getText().toString().equals("")) {
                loadedTask.setDateCreated(sdf.parse(dateCreated.getText().toString()));
            }
            if (!dateFinished.getText().toString().equals("")) {
                loadedTask.setDateFinished(sdf.parse(dateFinished.getText().toString()));
            }
            if (!deadline.getText().toString().equals("")) {
                loadedTask.setDeadline(sdf.parse(deadline.getText().toString()));
            }

            if (!lastResponsibleMoment.getText().toString().equals("")) {
                loadedTask.setLastResponsibleMoment(sdf.parse(lastResponsibleMoment.getText().toString()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getItemId(Object object) {
        String item = (String) object;
        if (item.indexOf("[") > -1 && item.indexOf("]") > -1) {
            item = item.substring(item.indexOf("[") + 1, item.indexOf("]"));
            return item;
        } else {
            return "";
        }
    }

    private void saveTask() {
        if (validateFields()) {
            setTaskValues();
            addTaskToCalendar();
            postTaskToServer();
        }
    }

    private boolean validateFields() {
        String msg = "";
        if (name.getText().toString().trim().equals("")) {
            msg = "Name is requered";
            name.setError(msg);
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (deadline.getText().toString().trim().equals("")) {
            msg = "Deadline is required";
            deadline.setError(msg);
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void postTaskToServer() {

        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(Date.class, new DateSerializer());
        Gson gson = gsonBuilder.create();


        BaseGsonRequest<Task> taskPostRequest = new BasePostRequest<Task>(getActivity(), ApiConstants.TASK_API_METHOD,
                gson.toJson(loadedTask), new RequestErrorListener(getActivity(), null)) {
            @Override
            protected Map<String, String> getPostParams() {
                return null;
            }

            @Override
            protected Class<Task> getResponseClass() {
                return Task.class;
            }
        };

        RequestManager.sendRequest(getActivity(), null, taskPostRequest, new Response.Listener<Task>() {
            @Override
            public void onResponse(Task response) {
                Toast.makeText(getActivity(), "Task saved.", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new TasksFragment()).addToBackStack(null).commit();
            }
        });
    }

//    private void setReminder() {
//        ReminderFragment reminderFragment = new ReminderFragment();
//        Bundle args = new Bundle();
//        args.putString("taskName", name.getText().toString());
//        args.putString("taskDeadline", deadline.getText().toString());
//        reminderFragment.setArguments(args);
//        reminderFragment.show(getActivity().getSupportFragmentManager(), "ReminderDialog");
//    }

    private void addTaskToCalendar() {

        //TODO: add condition for task to not be closed
        if (loadedTask.getEventId() == null) {
            ContentResolver cr = getActivity().getContentResolver();
            ContentValues values = new ContentValues();

            values.put(Events.DTSTART, loadedTask.getDateCreated().getTime());

            LocalDate d1 = new LocalDate(loadedTask.getDateCreated());
            LocalDate d2 = new LocalDate(loadedTask.getDeadline());
            Period period = new Period(d1, d2);
            Log.d("Marto", "days between " + d1 + " and " + d2 + " is " + period.getDays() + "days");
            int duration = period.getDays() + 2;
            values.put(CalendarContract.Events.DURATION, "P" + duration + "D");
            values.put(Events.ALL_DAY, 1);
            values.put(Events.TITLE, loadedTask.getName());
            if (loadedTask.getDescription() != null) {
                values.put(Events.DESCRIPTION, loadedTask.getDescription());
            }
            values.put(Events.CALENDAR_ID, 1);
//            values.put(Events.EVENT_END_TIMEZONE, Calendars.CALENDAR_TIME_ZONE);
            values.put(Events.EVENT_TIMEZONE, Calendars.CALENDAR_TIME_ZONE);
            values.put(Events.ACCESS_LEVEL, Events.ACCESS_PUBLIC);

            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_CALENDAR);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Uri insertUri = cr.insert(Events.CONTENT_URI, values);
                eventId = Long.parseLong(insertUri.getLastPathSegment());
                loadedTask.setEventId(eventId);
                Toast.makeText(getActivity(), "Created Calendar Event " + eventId, Toast.LENGTH_SHORT).show();


//                ContentValues reminderValues = new ContentValues();
//                reminderValues.put(Reminders.MINUTES, 600);
//                reminderValues.put(Reminders.EVENT_ID, eventId);
//                reminderValues.put(Reminders.METHOD, Reminders.METHOD_DEFAULT);
//                Uri uri = cr.insert(Reminders.CONTENT_URI, values);


                forceSync();
            }
        }
    }

    private void forceSync() {
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

    private void getUsers(final Context context) {
        BaseGsonRequest<Account[]> userGetRequest = new BaseGetRequest<>(
                context,
                ApiConstants.ACCOUNT_API_METHOD,
                null,
                Account[].class,
                new RequestErrorListener(context, null)
        );

        final List<Account> mUsers = new ArrayList<>();
        final List<String> strings = new ArrayList<>();
        RequestManager.sendRequest(
                context,
                null,
                userGetRequest,
                new Response.Listener<Account[]>() {
                    @Override
                    public void onResponse(Account[] response) {
                        Long assignedUserId = -1l;

                        if (loadedTask != null && loadedTask.getTakenAction() != null && loadedTask.getTakenAction() > 0) {
                            if (loadedTask.getTakenAction() == TaskAction.TRANSFER_NOTIFY.getIndex()) {
                                assignedUserId = loadedTask.getTransferedTo();
                            } else if (loadedTask.getTakenAction() == TaskAction.DELEGATE_FOLLOW_UP.getIndex()) {
                                assignedUserId = loadedTask.getDelegatedTo();
                            }
                        }

                        mUsers.addAll(Arrays.asList(response));
                        Toast.makeText(context, "Response successful", Toast.LENGTH_SHORT).show();
                        int position = 0;
                        strings.add(getResources().getString(R.string.no_user));
                        for (Account acc : mUsers) {
                            strings.add(acc != null ? acc.toString() : null);
                            if (acc != null && acc.getAccountId() == assignedUserId) {
                                position = mUsers.indexOf(acc);
                            }
                        }

                        if (isFragmentUIActive()) {
                            assignedUser = setSpinner(R.id.assignedUser, strings);
                            if (loadedTask != null) {
                                assignedUser.setSelection(position);
                                performUiChangesOnActionChanged(loadedTask.getTakenAction(), assignedUser, null);
                            }
                        }
                    }
                });
    }

    private void getProjects(final Context context) {
        BaseGsonRequest<Project[]> projectGetRequest = new BaseGetRequest<>(
                context,
                ApiConstants.PROJECT_API_METHOD,
                null,
                Project[].class,
                new RequestErrorListener(context, null)
        );
        final List<Project> list = new ArrayList<>();
        RequestManager.sendRequest(
                context,
                null,
                projectGetRequest,
                new Response.Listener<Project[]>() {
                    @Override
                    public void onResponse(Project[] response) {
                        list.addAll(Arrays.asList(response));
                        List<String> strings = new ArrayList<>();
                        strings.add(getResources().getString(R.string.no_project));
                        int position = 0;

                        for (Project project : list) {
                            strings.add(project != null ? project.toString() : null);
                            if (project != null && loadedTask.getProject() != null
                                    && project.getProjectId() == loadedTask.getProject().getProjectId()) {
                                position = list.indexOf(project);
                            }
                        }
                        Toast.makeText(context, "Response successful", Toast.LENGTH_SHORT).show();
                        if (isFragmentUIActive()) {
                            projectSpinner = setSpinner(R.id.project, strings);
                            projectSpinner.setSelection(position);
                        }
                    }
                }
        );
    }

    private void getTasks(final Context context) {
        BaseGsonRequest<Task[]> taskGetRequest = new BaseGetRequest<>(
                context,
                ApiConstants.TASK_API_METHOD,
                null,
                Task[].class,
                new RequestErrorListener(context, null)
        );
        final List<Task> list = new ArrayList<>();
        RequestManager.sendRequest(
                context,
                null,
                taskGetRequest,
                new Response.Listener<Task[]>() {
                    @Override
                    public void onResponse(Task[] response) {
                        list.addAll(Arrays.asList(response));
                        List<String> strings = new ArrayList<>();
                        strings.add(getResources().getString(R.string.no_task));
                        int position = 0;
                        for (Task task : list) {
                            strings.add(task != null ? task.toString() : null);
                            if (task != null && task.getTaskId() == loadedTask.getDependOn()) {
                                position = list.indexOf(task);
                            }
                        }
                        Toast.makeText(context, "Response successful", Toast.LENGTH_SHORT).show();
                        if (isFragmentUIActive()) {
                            dependentTaskSpinner = setSpinner(R.id.dependOn, strings);
                            dependentTaskSpinner.setSelection(position);
                        }
                    }
                }
        );
    }

    public boolean isFragmentUIActive() {
        return isAdded() && !isDetached() && !isRemoving();
    }

    private class CheckBoxListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            calculateValues(v);
            calculateFlyScore();
            TaskAction recommendAction = recommendAction();
            loadedTask.setRecommendedAction(recommendAction.getIndex());
            TextView recommendedAction = (TextView) fragmentView.findViewById(R.id.recommendedAction);
            recommendedAction.setText(recommendAction.toString());
//            Spinner actionSpinner = (Spinner) fragmentView.findViewById(R.id.actionSpinner);
//            actionSpinner.setSelection(recommendAction.getIndex());
        }
    }

}