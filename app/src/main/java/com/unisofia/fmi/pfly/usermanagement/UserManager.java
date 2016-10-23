package com.unisofia.fmi.pfly.usermanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.unisofia.fmi.pfly.PFlyApp;
import com.unisofia.fmi.pfly.api.ApiConstants;
import com.unisofia.fmi.pfly.api.RequestManager;
import com.unisofia.fmi.pfly.api.model.Account;
import com.unisofia.fmi.pfly.api.request.BaseGsonRequest;
import com.unisofia.fmi.pfly.api.request.RequestErrorListener;
import com.unisofia.fmi.pfly.api.request.post.BasePostRequest;
import com.unisofia.fmi.pfly.notification.gcm.util.GcmConstants;
import com.unisofia.fmi.pfly.notification.gcm.util.GcmUtil;
import com.unisofia.fmi.pfly.ui.activity.BaseActivity;
import com.unisofia.fmi.pfly.ui.activity.WelcomeActivity;

import java.io.IOException;
import java.util.Map;

public class UserManager {

    private static final String USER_PREFS = "USER_PREFS";
    private static final String KEY_USER_ID = "KEY_USER_ID";
    private static final String KEY_USER_NAME = "KEY_USER_NAME";
    private static final String KEY_USER_EMAIL = "KEY_USER_EMAIL";
    private static final String KEY_USER_TOKEN = "KEY_USER_TOKEN";

    private static Context appContext = PFlyApp.getAppContext();
    private static SharedPreferences userPrefs = appContext.getSharedPreferences(USER_PREFS,
            Context.MODE_PRIVATE);

    private static SharedPreferences gcmPrefs = appContext.getSharedPreferences(
            GcmConstants.GCM_PREFS, Context.MODE_PRIVATE);
    private static User loggedUser;
    private static Long userId;
    private static String token;
    private static String userName;
    private static String userMail;
    private static String deviceId;
    private static GoogleCloudMessaging mGoogleCloudMessaging;
    private static String mRegId;

    static {
        userId = userPrefs.getLong(KEY_USER_ID, -1);
        userName = userPrefs.getString(KEY_USER_NAME, null);
        userMail = userPrefs.getString(KEY_USER_EMAIL, null);
        token = userPrefs.getString(KEY_USER_TOKEN, null);
        deviceId = GcmUtil.getRegistrationId(appContext);

        if (userId > -1 && userMail != null && deviceId != null) {
            loggedUser = new User();
            loggedUser.setAccountId(userId);
            loggedUser.setName(userName);
            loggedUser.setEmail(userMail);
            loggedUser.setDeviceId(deviceId);
            loggedUser.setToken(token);
        }
    }

    public static String getLoggedUserName() {
        return userPrefs.getString(KEY_USER_NAME, null);
    }

    public static User getLoggedUser(){
        return loggedUser;
    }


    public static String getLoggedUserMail() {
        return userPrefs.getString(KEY_USER_EMAIL, null);
    }


    private UserManager() {
        // forbid instantiation
    }

    public static long getUserId() {
        return userPrefs.getLong(KEY_USER_ID, 0);
    }

    public static boolean loginUser(User profile) {
        if (profile == null) {
            return false;
        }
        loggedUser = profile;
        Editor editor = userPrefs.edit();
        if (profile.getAccountId() != null) {
            editor.putLong(KEY_USER_ID, profile.getAccountId());
        }
        editor.putString(KEY_USER_NAME, profile.getName());
        editor.putString(KEY_USER_EMAIL, profile.getEmail());
        editor.putString(KEY_USER_TOKEN, profile.getToken());


        Editor gcmEditor = gcmPrefs.edit();
        gcmEditor.putString(GcmConstants.PROPERTY_REG_ID, profile.getDeviceId());
        gcmEditor.commit();
        return editor.commit();
    }

    public static boolean logoutUser() {
        SharedPreferences userPrefs = PFlyApp.getAppContext().getSharedPreferences(USER_PREFS,
                Context.MODE_PRIVATE);

        Editor editor = userPrefs.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_TOKEN);

        Editor gcmEditor = gcmPrefs.edit();
        gcmEditor.remove(GcmConstants.PROPERTY_REG_ID);
        gcmEditor.commit();

        loggedUser = null;
        return editor.commit();
    }

    public static boolean isUserLoggedIn() {
        return getUserId() != 0;
    }

    public static Account getLoggedAccount() {
        //use proxy object to hide fields from subclass object
        Account loggedAccount = new Account();
        loggedAccount.setAccountId(loggedUser.getAccountId());
        loggedAccount.setName(loggedUser.getName());
        loggedAccount.setUserId(loggedUser.getUserId());
        loggedAccount.setDeviceId(loggedUser.getDeviceId());
        loggedAccount.setEmail(loggedUser.getEmail());

        return loggedAccount;
    }

    public static void registerInBackground(final BaseActivity baseActivity, final User user) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                baseActivity.showProgress();
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
                baseActivity.hideProgress();
            }
        }.execute(null, null, null);
    }

    public static void saveAccount(WelcomeActivity context, User account) {
        addAccountToBackend(context, account);
    }

    public static void loginAccountToBackend(final WelcomeActivity context, final User user) {
        Gson gson = new Gson();
        user.setDeviceId(GcmUtil.getRegistrationId(PFlyApp.getAppContext()));
        BaseGsonRequest<String> accountPostRequest = new BasePostRequest<String>(context, ApiConstants.AUTH_API_METHOD + "/login",
                gson.toJson(user), new RequestErrorListener(context, null)) {
            @Override
            protected Map<String, String> getPostParams() {
                return null;
            }

            @Override
            protected Class<String> getResponseClass() {
                return String.class;
            }
        };

        RequestManager.sendRequest(context, null, accountPostRequest, new Response.Listener<String>() {
            @Override
            public void onResponse(String token) {
                Toast.makeText(context, "Account saved.", Toast.LENGTH_SHORT).show();
                Log.d("Response account: ", token);
                user.setToken(token);
                UserManager.loginUser(user);
                context.showHome();
            }
        });
    }


    public static void addAccountToBackend(final WelcomeActivity context, User user) {
        Gson gson = new Gson();
        user.setDeviceId(GcmUtil.getRegistrationId(PFlyApp.getAppContext()));
        BaseGsonRequest<User> accountPostRequest = new BasePostRequest<User>(context, ApiConstants.AUTH_API_METHOD + "/register",
                gson.toJson(user), new RequestErrorListener(context, null)) {
            @Override
            protected Map<String, String> getPostParams() {
                return null;
            }

            @Override
            protected Class<User> getResponseClass() {
                return User.class;
            }
        };

        RequestManager.sendRequest(context, null, accountPostRequest, new Response.Listener<User>() {
            @Override
            public void onResponse(User response) {
                Toast.makeText(context, "Account saved.", Toast.LENGTH_SHORT).show();
                Log.d("Response account: ", response.toString());
                UserManager.loginUser(response);
                context.showHome();
            }
        });
    }
}
