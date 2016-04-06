package com.unisofia.fmi.pfly.ui.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.api.model.Task;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
        deadline = setDatePicker(view, R.id.deadline);
        dateCreated = setDatePicker(view, R.id.dateCreated);
        dateFinished = setDatePicker(view, R.id.dateFinished);


        spinner = (Spinner) view.findViewById(R.id.dependOn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.tasks_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        loadedTask = (Task) this.getArguments().getSerializable("task");
        if (loadedTask != null) {
            Toast.makeText(getActivity(), "task loaded", Toast.LENGTH_LONG).show();
            setTaskValues();
        }
    }


    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        }
    }

    private void updateLabel(EditText dateEditText, int year, int month, int day) {
        Log.d("Martin", "onDateSet");
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        String dateFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        dateEditText.setText(sdf.format(calendar.getTime()));
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

    private EditText setDatePicker(View view, int editTextId) {
        final EditText editTextDate = (EditText) view.findViewById(editTextId);
        editTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogFragment datePickerFragment = new DatePickerFragment() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            updateLabel(editTextDate, year, month, day);
                        }
                    };
                    datePickerFragment.show(getActivity().getFragmentManager(), "datePicker");
                }
            }
        });
        return editTextDate;
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


    private void setTaskValues() {
        name.setText(loadedTask.getName());
        description.setText(loadedTask.getDescription());
    }

    private class CheckBoxListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            calculateFlyScore();
        }
    }
}