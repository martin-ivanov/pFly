package com.unisofia.fmi.pfly.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
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
import com.unisofia.fmi.pfly.api.model.Project;
import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;
import com.unisofia.fmi.pfly.api.request.RequestErrorListener;
import com.unisofia.fmi.pfly.api.request.get.BaseGetRequest;
import com.unisofia.fmi.pfly.api.request.post.BasePostRequest;
import com.unisofia.fmi.pfly.api.util.CalendarUtil;
import com.unisofia.fmi.pfly.api.util.DateSerializer;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.unisofia.fmi.pfly.api.model.Task.TaskAction;
import static com.unisofia.fmi.pfly.api.model.Task.TaskStatus;


public class TaskFragment extends BaseMenuFragment {
    private TextView taskId;
    private EditText name;
    private EditText description;
    private TextView creator;

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
    TextView flyScoreView;
    private Long eventId;

    SimpleDateFormat sdf = new SimpleDateFormat(DatePickerFragment.DATE_FORMAT_PATTERN);
    private Map<String, ?> prefs = null;
    private View fragmentView = null;
    private boolean isRangeEnabled = false;
    private Task loadedTask = null;
    private List<Project> availableProjects;


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
        MenuItem saveOption = menu.findItem(R.id.save_task);
        MenuItem archiveOption = menu.findItem(R.id.archive_task);
        if (loadedTask != null && loadedTask.getEventId() != null) {
            eventOption.setVisible(true);
        }

        if (loadedTask.getTaskId() == null) {
            archiveOption.setVisible(false);
        }

        if (loadedTask.getStatus() == TaskStatus.CLOSED.getIndex()) {
            saveOption.setVisible(false);
            archiveOption.setVisible(false);
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
            case R.id.archive_task:
                archiveTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void archiveTask() {
        setTaskValues();
        loadedTask.setStatus(TaskStatus.CLOSED.getIndex());
        postTaskToServer();
        Toast.makeText(getActivity(), "Task archived.", Toast.LENGTH_SHORT).show();
    }

    private void setFieldsEnabled(boolean enabled) {
        name.setEnabled(enabled);
        description.setEnabled(enabled);
        desiredOutcome.setEnabled(enabled);
        notes.setEnabled(enabled);

        actionSpinner.setEnabled(enabled);
        dateCreated.setEnabled(enabled);
        dateFinished.setEnabled(enabled);
        deadline.setEnabled(enabled);
        lastResponsibleMoment.setEnabled(enabled);

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
        creator = (TextView) view.findViewById(R.id.creator);

        isRangeEnabled = Boolean.parseBoolean(prefs.get("fly_weight_switch").toString());
        setFlyCharacteristics();
        flyScoreView = (TextView) fragmentView.findViewById(R.id.fly_score);
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
            loadedTask.setStatus(TaskStatus.OPENED.getIndex());
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
                calculateValues(view);
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

    private void calculateValues(View view) {
        switch (view.getId()) {
            case R.id.intImportanceBar:
                intImportanceValue = (intImportance.isChecked()) ? (int) intImportanceBar.getExactValue() : 0;
                break;
            case R.id.extImportanceBar:
                extImportanceValue = (extImportance.isChecked()) ? (int) extImportanceBar.getExactValue() : 0;
                break;
            case R.id.simplicityBar:
                simplicityValue = (simplicity.isChecked()) ? (int) simplicityBar.getExactValue() : 0;
                break;
            case R.id.clearnessBar:
                clearnessValue = (clearness.isChecked()) ? (int) clearnessBar.getExactValue() : 0;
                break;
            case R.id.closenessBar:
                closenessValue = (closeness.isChecked()) ? (int) closenessBar.getExactValue() : 0;
                break;
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

    private void calculateFlyScore() {

        flyScore = intImportanceValue + extImportanceValue + simplicityValue +
                clearnessValue + closenessValue;
        showFlyScore(flyScore);
    }

    private void showFlyScore(int flyScore) {
        if (flyScoreView.getVisibility() == View.GONE) {
            flyScoreView.setVisibility(View.VISIBLE);
        }
        flyScoreView.setText("Fly score: " + flyScore);
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
        creator.setText("Creator: " + loadedTask.getAccount().getName() +
                " (" + loadedTask.getAccount().getEmail() + ")");
        actionSpinner.setSelection(loadedTask.getTakenAction());

        if (loadedTask.getTakenAction() == TaskAction.SCHEDULE_DEFER.getIndex()) {
            lastResponsibleMoment.setText(sdf.format(loadedTask.getLastResponsibleMoment()));
            performUiChangesOnActionChanged(loadedTask.getTakenAction(), null, lastResponsibleMoment);
        }

        eventId = loadedTask.getEventId();

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
        showFlyScore(flyScore);


        if (loadedTask.getDateCreated() != null) {
            dateCreated.setText(sdf.format(loadedTask.getDateCreated()));
        }

        if (loadedTask.getDateFinished() != null) {
            dateFinished.setText(sdf.format(loadedTask.getDateFinished()));
        }

        if (loadedTask.getDeadline() != null) {
            deadline.setText(sdf.format(loadedTask.getDeadline()));
        }

        if (loadedTask.getStatus() == TaskStatus.CLOSED.getIndex()) {
            setFieldsEnabled(false);
        }

    }

    private void setTaskValues() {
        loadedTask.setName(name.getText().toString());
        loadedTask.setDescription(description.getText().toString());
        loadedTask.setDesiredOutcome(desiredOutcome.getText().toString());
        loadedTask.setNotes(notes.getText().toString());

        loadedTask.setAccount(UserManager.getLoggedAccount());

        loadedTask.setIntImportance(intImportanceValue);
        loadedTask.setExtImportance(extImportanceValue);
        loadedTask.setCloseness(closenessValue);
        loadedTask.setClearness(clearnessValue);
        loadedTask.setSimplicity(simplicityValue);
        loadedTask.setFlyScore(flyScore);
        if (assignedUser != null) {
            String assignedUserId = getItemId(assignedUser.getSelectedItem());
            if (assignedUserId.length() > 0) {
                if (actionSpinner.getSelectedItem().toString().equals(TaskAction.DELEGATE_FOLLOW_UP.toString())) {
                    loadedTask.setDelegatedTo(Long.parseLong(assignedUserId));
                    loadedTask.setStatus(TaskStatus.DELEGATED.getIndex());
                }

                if (actionSpinner.getSelectedItem().toString().equals(TaskAction.TRANSFER_NOTIFY.toString())) {
                    loadedTask.setTransferedTo(Long.parseLong(assignedUserId));
                    loadedTask.setStatus(TaskStatus.TRANSFERRED.getIndex());
                }
            }
        }
        String dependentTaskId = getItemId(dependentTaskSpinner.getSelectedItem());
        if (dependentTaskId.length() > 0) {
            loadedTask.setDependOn(Long.parseLong(dependentTaskId));
        }

        String project = getItemId(projectSpinner.getSelectedItem());
        if (project.length() > 0) {
            for (Project prj : availableProjects) {
                if (prj.getProjectId() == Long.parseLong(project)) {
                    loadedTask.setProject(prj);
                    break;
                }
            }
        }

        loadedTask.setEventId(eventId);
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
                loadedTask.setStatus(TaskStatus.DEFERRED.getIndex());
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
            CalendarUtil.addTaskToCalendar(getActivity(), loadedTask);
            postTaskToServer();
            Toast.makeText(getActivity(), "Task saved.", Toast.LENGTH_SHORT).show();
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
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    Log.i("MainActivity", "popping backstack");
                    fm.popBackStack();
                } else {
                    Log.i("MainActivity", "nothing on backstack, calling super");
                    getActivity().onBackPressed();
                }
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.content_frame, new TasksFragment()).addToBackStack(null).commit();
            }
        });
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
//                        Toast.makeText(context, "Response successful", Toast.LENGTH_SHORT).show();
                        int position = 0;
                        strings.add(getResources().getString(R.string.no_user));
                        for (Account acc : mUsers) {
                            strings.add(acc != null ? acc.toString() : null);
                            if (acc != null && acc.getAccountId() == assignedUserId) {
                                position = mUsers.indexOf(acc) + 1;
                            }
                        }

                        if (isFragmentUIActive()) {
                            assignedUser = setSpinner(R.id.assignedUser, strings);
                            if (loadedTask != null) {
                                assignedUser.setSelection(position);
                                performUiChangesOnActionChanged(loadedTask.getTakenAction(), assignedUser, null);
                                if (loadedTask.getStatus() == TaskStatus.CLOSED.getIndex()) {
                                    assignedUser.setEnabled(false);
                                }
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
        RequestManager.sendRequest(
                context,
                null,
                projectGetRequest,
                new Response.Listener<Project[]>() {
                    @Override
                    public void onResponse(Project[] response) {
                        availableProjects = new ArrayList<>();
                        availableProjects.addAll(Arrays.asList(response));
                        List<String> strings = new ArrayList<>();
                        strings.add(getResources().getString(R.string.no_project));
                        int position = 0;

                        for (Project project : availableProjects) {
                            strings.add(project != null ? project.toString() : null);
                            if (project != null && loadedTask.getProject() != null
                                    && project.getProjectId() == loadedTask.getProject().getProjectId()) {
                                position = availableProjects.indexOf(project) + 1;
                            }
                        }
//                        Toast.makeText(context, "Response successful", Toast.LENGTH_SHORT).show();
                        if (isFragmentUIActive()) {
                            projectSpinner = setSpinner(R.id.project, strings);
                            projectSpinner.setSelection(position);
                            if (loadedTask.getStatus() == TaskStatus.CLOSED.getIndex()) {
                                projectSpinner.setEnabled(false);
                            }
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
                                position = list.indexOf(task) + 1;
                            }
                        }
//                        Toast.makeText(context, "Response successful", Toast.LENGTH_SHORT).show();
                        if (isFragmentUIActive()) {
                            dependentTaskSpinner = setSpinner(R.id.dependOn, strings);
                            dependentTaskSpinner.setSelection(position);
                            if (loadedTask.getStatus() == TaskStatus.CLOSED.getIndex()) {
                                dependentTaskSpinner.setEnabled(false);
                            }
                        }
                    }
                }
        );
    }

    public boolean isFragmentUIActive() {
        return isAdded() && !isDetached() && !isRemoving();
    }


    private void showRecommendedAction(String action) {
        TextView recommendedAction = (TextView) fragmentView.findViewById(R.id.recommendedAction);
        recommendedAction.setVisibility(View.VISIBLE);
        recommendedAction.setVisibility(View.VISIBLE);
        recommendedAction.setText("pFly recommendation is to " + action.toLowerCase());

    }

    private class CheckBoxListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            calculateValues(v);
            calculateFlyScore();
            TaskAction recommendAction = recommendAction();
            loadedTask.setRecommendedAction(recommendAction.getIndex());
            showRecommendedAction(recommendAction.toString());
        }
    }
}