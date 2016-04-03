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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.unisofia.fmi.pfly.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TaskFragment extends BaseMenuFragment {
    private Spinner spinner;
    private EditText deadline;
    private EditText dateCreated;
    private EditText dateFinished;
    private DiscreteSeekBar simplicityBar;
    private DiscreteSeekBar intImportanceBar;
    private DiscreteSeekBar extImportanceBar;
    private DiscreteSeekBar closenessBar;
    private DiscreteSeekBar clearnessBar;
    private SharedPreferences prefs = null;


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
        inflater.inflate(R.menu.menu_pfly_add_task, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = setPrefs();

        simplicityBar = setDiscreteBar(view, R.id.simplicity, "simplicityMinPref",
                "simplicityMaxPref");
        closenessBar = setDiscreteBar(view, R.id.closeness, "closenessMinPref",
                "closenessMaxPref");
        intImportanceBar = setDiscreteBar(view, R.id.intImportance, "intImportanceMinPref",
                "intImportanceMaxPref");
        extImportanceBar = setDiscreteBar(view, R.id.extImportance, "extImportanceMinPref",
                "extImportanceMaxPref");

        clearnessBar = setDiscreteBar(view, R.id.clearness, "clearnessMinPref",
                "clearnessMaxPref");

        deadline = setDatePicker(view, R.id.deadline);
        dateCreated = setDatePicker(view, R.id.dateCreated);
        dateFinished = setDatePicker(view, R.id.dateFinished);


        spinner = (Spinner) view.findViewById(R.id.dependOn);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.tasks_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
        return bar;
    }

    private EditText setDatePicker(View view, int editTextId){
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

    private SharedPreferences setPrefs(){
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_int_importance, false);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_ext_importance, false);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_closeness, false);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_clearness, false);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_simplicity, false);
        return PreferenceManager.getDefaultSharedPreferences(getActivity());
    }
}