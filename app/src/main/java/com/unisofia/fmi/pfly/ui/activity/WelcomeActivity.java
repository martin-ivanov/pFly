package com.unisofia.fmi.pfly.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.usermanagement.UserManager;
import com.unisofia.fmi.pfly.ui.fragment.ViewPagerFragment;

public class WelcomeActivity extends BaseActivity {
    private static Context mContext;
    ViewPagerFragment viewPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        viewPagerFragment = new ViewPagerFragment();
        viewPagerFragment.setPagerType(ViewPagerFragment.PagerType.WELCOME_PAGER);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, viewPagerFragment).commit();

        mContext = getApplicationContext();
        setPrefs();
//        showHome();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (UserManager.isUserLoggedIn()) {
            showHome();
        }
    }

    public void showHome() {
        Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
        startActivity(intent);
    }


    private void setPrefs() {
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_int_importance, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_ext_importance, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_closeness, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_clearness, true);
        PreferenceManager.setDefaultValues(this, R.xml.pref_simplicity, true);
    }

}
