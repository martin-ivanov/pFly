package com.unisofia.fmi.pfly.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.unisofia.fmi.pfly.PFlyApp;
import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.account.UserManager;
import com.unisofia.fmi.pfly.api.ApiConstants;
import com.unisofia.fmi.pfly.api.RequestManager;
import com.unisofia.fmi.pfly.api.model.Account;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;
import com.unisofia.fmi.pfly.api.request.RequestErrorListener;
import com.unisofia.fmi.pfly.api.request.post.BasePostRequest;
import com.unisofia.fmi.pfly.notification.gcm.util.GcmConstants;
import com.unisofia.fmi.pfly.notification.gcm.util.GcmUtil;
import com.unisofia.fmi.pfly.ui.fragment.BaseMenuFragment;
import com.unisofia.fmi.pfly.ui.fragment.ViewPagerFragment;

import java.io.IOException;
import java.util.Map;

public class WelcomeActivity extends BaseActivity {
    private static Context mContext;
    ViewPagerFragment viewPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_welcome);
//        viewPagerFragment = new ViewPagerFragment();
//        viewPagerFragment.setPagerType(ViewPagerFragment.PagerType.WELCOME_PAGER);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.content_frame, viewPagerFragment).commit();

        mContext = getApplicationContext();
        setPrefs();
        showHome();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (UserManager.getLoggedUser() != null) {
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
