package com.unisofia.fmi.pfly.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Task;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import static com.unisofia.fmi.pfly.api.model.Task.TaskAction;

public class TaskFragment extends BaseMenuFragment {
    private EditText name;
    private EditText description;

    private Spinner spinner;
    private EditText deadline;
    private EditText dateCreated;
    private EditText dateFinished;
    private CheckBox simplicity;
    private DiscreteSeekBar simplicityBar;
    private CheckBox intImportance;
    private DiscreteSeekBar intImportanceBar;
    private CheckBox extImportance;
    private DiscreteSeekBar extImportanceBar;
    private CheckBox closeness;
    private DiscreteSeekBar closenessBar;
    private CheckBox clearness;
    private DiscreteSeekBar clearnessBar;
    private SharedPreferences prefs = null;
    private View fragmentView = null;
    private boolean isRangeEnabled = false;
    private TableLayout rangesTable;

    private Task loadedTask = null;
    private Calendar calendar = Calendar.getInstance();

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

                return true;
            case R.id.reminder_task:
//                ReminderFragment reminderFragment = new ReminderFragment();
//                Bundle args = new Bundle();
//                args.putSerializable("task", task);
//                taskFragment.setArguments(args);
                ReminderFragment reminderFragment = new ReminderFragment();
                Bundle args = new Bundle();
                args.putString("taskName", name.getText().toString());
                args.putString("taskDeadline", deadline.getText().toString());
                reminderFragment.setArguments(args);
                reminderFragment.show(getActivity().getSupportFragmentManager(), "ReminderDialog");
//                reminderFragment.setContentView(R.layout.dialog_tasks_filter);
//                reminderFragment.setTitle("Filter by:");

//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.content_frame, reminderFragment)
//                        .addToBackStack(null)
//                        .commit();
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

        intImportance = (CheckBox) view.findViewById(R.id.intImportance);
        intImportance.setOnClickListener(new CheckBoxListener());

        extImportance = (CheckBox) view.findViewById(R.id.extImportance);
        extImportance.setOnClickListener(new CheckBoxListener());

        simplicity = (CheckBox) view.findViewById(R.id.simplicity);
        simplicity.setOnClickListener(new CheckBoxListener());

        closeness = (CheckBox) view.findViewById(R.id.closeness);
        closeness.setOnClickListener(new CheckBoxListener());

        clearness = (CheckBox) view.findViewById(R.id.clearness);
        clearness.setOnClickListener(new CheckBoxListener());

        isRangeEnabled = prefs.getBoolean("fly_weight_switch", true);

        simplicityBar = setDiscreteBar(view, R.id.simplicityBar, "simplicityMinPref",
                "simplicityMaxPref");
        closenessBar = setDiscreteBar(view, R.id.closenessBar, "closenessMinPref",
                "closenessMaxPref");
        intImportanceBar = setDiscreteBar(view, R.id.intImportanceBar, "intImportanceMinPref",
                "intImportanceMaxPref");
        extImportanceBar = setDiscreteBar(view, R.id.extImportanceBar, "extImportanceMinPref",
                "extImportanceMaxPref");

        clearnessBar = setDiscreteBar(view, R.id.clearnessBar, "clearnessMinPref",
                "clearnessMaxPref");

        if (isRangeEnabled) {
            rangesTable = (TableLayout) view.findViewById(R.id.rangesLayout);
            rangesTable.setVisibility(View.VISIBLE);
        }
        deadline = DatePickerFragment.setDatePicker(getActivity(), view, R.id.deadline);
        dateCreated = DatePickerFragment.setDatePicker(getActivity(),view, R.id.dateCreated);
        dateFinished = DatePickerFragment.setDatePicker(getActivity(),view, R.id.dateFinished);


        spinner = (Spinner) view.findViewById(R.id.dependOn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.tasks_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        actionSpinner = (Spinner) view.findViewById(R.id.actionSpinner);
        actionSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line,
                TaskAction.values()));

        Bundle args = this.getArguments();
        if (args != null) {
            loadedTask = (Task) args.getSerializable("task");
            if (loadedTask != null) {
                Toast.makeText(getActivity(), "task loaded", Toast.LENGTH_LONG).show();
                setTaskValues();
            }
            }
    }


    private DiscreteSeekBar setDiscreteBar(View view, int barId, String minPref, String maxPref) {
        DiscreteSeekBar bar = (DiscreteSeekBar) view.findViewById(barId);
        String minValue = prefs.getString(minPref, "");
        bar.setMin(Integer.parseInt(minValue));
        String maxValue = prefs.getString(maxPref, "");
        bar.setMax(Integer.parseInt(maxValue));
        bar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                calculateFlyScore();
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });
        return bar;
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
        int score = 0;
        if (isRangeEnabled) {
            score = intImportanceBar.getProgress() * booleanToInt(intImportance.isChecked()) +
                    extImportanceBar.getProgress() * booleanToInt(extImportance.isChecked()) +
                    simplicityBar.getProgress() * booleanToInt(simplicity.isChecked()) +
                    clearnessBar.getProgress() * booleanToInt(clearness.isChecked()) +
                    closenessBar.getProgress() * booleanToInt(closeness.isChecked());
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


    private TaskAction recommendAction(){
        if (!intImportance.isChecked() && !extImportance.isChecked()){
            return TaskAction.TRASH_NOTIFY;
        }
        if (!intImportance.isChecked()) {
            return TaskAction.TRANSFER_NOTIFY;
        }
        if (!simplicity.isChecked() && !closeness.isChecked()){
            return TaskAction.DELEGATE_FOLLOW_UP;
        }
        if (simplicity.isChecked() && !closeness.isChecked()){
            return TaskAction.SCHEDULE_DEFER;
        }
        if (!clearness.isChecked()){
            return TaskAction.CLARIFY;
        }
        if (!simplicity.isChecked() && closeness.isChecked()){
            return TaskAction.SIMPLIFY;
        }

        return TaskAction.EXECUTE;

    }

    private void setTaskValues() {
        name.setText(loadedTask.getName());
        description.setText(loadedTask.getDescription());
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