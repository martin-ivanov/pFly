package com.unisofia.fmi.pfly.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.io.IOException;
import java.util.Map;

public class WelcomeActivity extends BaseActivity {

    private static Context mContext;

    private GoogleCloudMessaging mGoogleCloudMessaging;
    private String mRegId;

    private Button loginButton;
    private TextView username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);
        mContext = getApplicationContext();
        setPrefs();

        username = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        loginButton = (Button) findViewById(R.id.login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = username.getText().toString().trim();
                String mail = email.getText().toString().trim();
                if (validateFields(name, mail)){
                    registerInBackground();
                }
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

    private boolean validateFields(String name, String mail){
        String msg;
        if (name.equals("")) {
            msg = "Name is requered";
            username.setError(msg);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mail.equals("")) {
            msg = "Email is required";
            email.setError(msg);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                showProgress();
            }

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                if (mGoogleCloudMessaging == null) {
                    mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(PFlyApp.getAppContext());
                }
                int retryCount = 0;
                while (mRegId == null && retryCount < 5) {
                    try {
                        mRegId = mGoogleCloudMessaging.register(GcmConstants.SENDER_ID);
                    } catch (IOException ex) {
                        msg = "Error: " + ex.getMessage();
                    }

                    retryCount++;
                }
                msg = "Device registered, regId = " + mRegId;
                GcmUtil.storeRegistrationId(PFlyApp.getAppContext(), mRegId);
                Log.d("Marto", msg);
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
            String personName = username.getText().toString();
            String email = this.email.getText().toString();

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
