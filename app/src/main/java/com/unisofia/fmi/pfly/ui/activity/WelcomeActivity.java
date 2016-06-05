package com.unisofia.fmi.pfly.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
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

import java.io.IOException;
import java.util.Map;

public class WelcomeActivity extends BaseActivity{

    private static Context mContext;


    public static Context getAppContext() {
        return mContext;
    }

    private GoogleCloudMessaging mGoogleCloudMessaging;
    private String mRegId;

    private Button loginButton;
    private TextView username, emailLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        mContext = getApplicationContext();
        setPrefs();

        username = (TextView) findViewById(R.id.username);
        emailLabel = (TextView) findViewById(R.id.email);
        loginButton = (Button) findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerInBackground();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (UserManager.getLoggedUser() != null) {
            showHome();
        }
    }

    private void saveAccountToBackend() {
        Gson gson = new Gson();
        BaseGsonRequest<Account> accountPostRequest = new BasePostRequest<Account>(this, ApiConstants.ACCOUNT_API_METHOD,
                gson.toJson(UserManager.getLoggedAccount()), new RequestErrorListener(this, null)) {
            @Override
            protected Map<String, String> getPostParams() {
                return null;
            }

            @Override
            protected Class<Account> getResponseClass() {
                return Account.class;
            }
        };

        RequestManager.sendRequest(this, null, accountPostRequest, new Response.Listener<Account>() {
            @Override
            public void onResponse(Account response) {
                Toast.makeText(WelcomeActivity.this, "Account saved.", Toast.LENGTH_SHORT).show();
                Log.d("Response account: ", response.toString());
                UserManager.loginUser(response);
                showHome();
            }
        });
    }

    private void showHome() {
        Intent intent = new Intent(this, HomeActivity.class);
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

//    private void registerGcmIfNeeded() {
//        if (GcmUtil.checkPlayServices(this)) {
//            mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(this);
//
//            //Always register on google login
//            registerInBackground();
//        } else {
//            Log.i(GcmConstants.DEBUG_TAG, "No valid Google play APK found");
//        }
//    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                showProgress();
            }

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (mGoogleCloudMessaging == null) {
                        mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(PFlyApp.getAppContext());
                    }
                    int retryCount = 0;
                    while (mRegId == null  && retryCount < 5) {
                        mRegId = mGoogleCloudMessaging.register(GcmConstants.SENDER_ID);
                        retryCount++;
                    }
                    msg = "Device registered, regId = " + mRegId;
                    GcmUtil.storeRegistrationId(PFlyApp.getAppContext(), mRegId);
                } catch (IOException ex) {
                    msg = "Error: " + ex.getMessage();
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String s) {
                hideProgress();
                getAccountInformation();
                saveAccountToBackend();
            }
        }.execute(null, null, null);
    }

    private void getAccountInformation() {
        try {
            String personName = "";
            String email = "";

            personName = "Martin Ivanov";
            email = "ivanov9237@gmail.com";

            username.setText(personName);
            emailLabel.setText(email);

            Account account = new Account();
            account.setName(personName);
            account.setEmail(email);
            account.setDeviceId(mRegId);

            UserManager.loginUser(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
