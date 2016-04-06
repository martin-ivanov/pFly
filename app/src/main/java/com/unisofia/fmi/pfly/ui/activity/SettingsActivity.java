package com.unisofia.fmi.pfly.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.unisofia.fmi.pfly.R;

import java.util.List;

public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            preference.setSummary(value.toString());
            return true;
        }
    };


    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || IntImportancePreferenceFragment.class.getName().equals(fragmentName)
                || ExtImportancePreferenceFragment.class.getName().equals(fragmentName)
                || ClosenessPreferenceFragment.class.getName().equals(fragmentName)
                || SimplicityPreferenceFragment.class.getName().equals(fragmentName)
                || ClearnessPreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }


    public static class FlyPreferenceFragment extends PreferenceFragment {

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class IntImportancePreferenceFragment extends FlyPreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_int_importance);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("intImportanceMinPref"));
            bindPreferenceSummaryToValue(findPreference("intImportanceMaxPref"));
            bindPreferenceSummaryToValue(findPreference("intImportanceWeightPref"));
        }
    }

    public static class ExtImportancePreferenceFragment extends FlyPreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_ext_importance);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("extImportanceMinPref"));
            bindPreferenceSummaryToValue(findPreference("extImportanceMaxPref"));
            bindPreferenceSummaryToValue(findPreference("extImportanceWeightPref"));
        }
    }

    public static class ClosenessPreferenceFragment extends FlyPreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_closeness);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("closenessMinPref"));
            bindPreferenceSummaryToValue(findPreference("closenessMaxPref"));
            bindPreferenceSummaryToValue(findPreference("closenessWeightPref"));
        }
    }

    public static class SimplicityPreferenceFragment extends FlyPreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_simplicity);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("simplicityMinPref"));
            bindPreferenceSummaryToValue(findPreference("simplicityMaxPref"));
            bindPreferenceSummaryToValue(findPreference("simplicityWeightPref"));
        }
    }

    public static class ClearnessPreferenceFragment extends FlyPreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_clearness);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("clearnessMinPref"));
            bindPreferenceSummaryToValue(findPreference("clearnessMaxPref"));
            bindPreferenceSummaryToValue(findPreference("clearnessWeightPref"));
        }
    }

    public static class GeneralPreferenceFragment extends FlyPreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            Preference preference = findPreference("fly_weight_switch");
        }
    }


}

