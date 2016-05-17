package com.unisofia.fmi.pfly.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.unisofia.fmi.pfly.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.unisofia.fmi.pfly.account.UserManager;
import com.unisofia.fmi.pfly.api.model.Profile;
import com.unisofia.fmi.pfly.ui.fragment.TaskFragment;

public class WelcomeActivity extends BaseActivity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    private static Context mContext;


    public static Context getAppContext() {
        return mContext;
    }

    private static final int RC_SIGN_IN = 0;

    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean signedInUser;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private SignInButton signinButton;
    private TextView username, emailLabel;
    private LinearLayout profileFrame, signinFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*		if (UserManager.isUserLoggedIn()) {
            Intent intent = new Intent(this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);

			return;
		}*/

        setContentView(R.layout.activity_welcome);
        mContext = getApplicationContext();
        setPrefs();


        signinButton = (SignInButton) findViewById(R.id.signin);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googlePlusLogin();
            }
        });

        username = (TextView) findViewById(R.id.username);
        emailLabel = (TextView) findViewById(R.id.email);
        profileFrame = (LinearLayout) findViewById(R.id.profileFrame);
        signinFrame = (LinearLayout) findViewById(R.id.signinFrame);
        showHome();

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(Plus.API, Plus.PlusOptions.builder().build())
//                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (UserManager.getLoggedUser() != null){
            showHome();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        signedInUser = false;
        Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
        getProfileInformation();
        showHome();
    }


    private void showHome(){
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

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            return;
        }
        if (!mIntentInProgress) {
            // store mConnectionResult
            mConnectionResult = connectionResult;
            if (signedInUser) {
                resolveSignInError();
            }
        }
    }



    private void googlePlusLogin() {
        mGoogleApiClient.connect();
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }


    }


    private void getProfileInformation() {
        try {

            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                username.setText(personName);
                emailLabel.setText(email);

                Profile profile = new Profile();
                profile.setName(personName);
                profile.setEmail(email);
                UserManager.loginUser(profile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resolveSignInError() {

        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }


}
