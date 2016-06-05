package com.unisofia.fmi.pfly.ui.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public static final String DATE_FORMAT_PATTERN = "dd.MM.yy";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
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

    public static EditText setDatePicker(final Activity activity, View view, int editTextId) {
        final EditText editTextDate = (EditText) view.findViewById(editTextId);
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        updateLabel(editTextDate, year, month, day);
                    }
                };
                datePickerFragment.show(activity.getFragmentManager(), "datePicker");
            }
        });

        editTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextDate.callOnClick();
                }
            }
        });
        return editTextDate;
    }

    private static void updateLabel(EditText dateEditText, int year, int month, int day) {
        Log.d("Martin", "onDateSet");
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        String dateFormat = DATE_FORMAT_PATTERN; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        dateEditText.setText(sdf.format(c.getTime()));
    }
}