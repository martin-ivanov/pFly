package com.unisofia.fmi.pfly.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.unisofia.fmi.pfly.api.model.Account;
import com.unisofia.fmi.pfly.notification.gcm.util.GcmConstants;
import com.unisofia.fmi.pfly.notification.gcm.util.GcmUtil;
import com.unisofia.fmi.pfly.ui.activity.WelcomeActivity;

public class UserManager {

    private static final String USER_PREFS = "USER_PREFS";
    private static final String KEY_USER_ID = "KEY_USER_ID";
    private static final String KEY_USER_NAME = "KEY_USER_NAME";
    private static final String KEY_USER_EMAIL = "KEY_USER_EMAIL";

    private static Context appContext = WelcomeActivity.getAppContext();
    private static SharedPreferences userPrefs = appContext.getSharedPreferences(USER_PREFS,
            Context.MODE_PRIVATE);

    private static SharedPreferences gcmPrefs = appContext.getSharedPreferences(
            GcmConstants.GCM_PREFS, Context.MODE_PRIVATE);
    private static Account loggedAccount;
    private static Long accountId;
    private static String accountName;
    private static String accountMail;
    private static String deviceId;

    static{
        accountId = userPrefs.getLong(KEY_USER_ID, -1);
        accountName = userPrefs.getString(KEY_USER_NAME, null);
        accountMail = userPrefs.getString(KEY_USER_EMAIL, null);
        deviceId = GcmUtil.getRegistrationId(appContext);

        if (accountId > -1 && accountMail != null && deviceId != null){
            loggedAccount = new Account();
            loggedAccount.setAccountId(accountId);
            loggedAccount.setName(accountName);
            loggedAccount.setEmail(accountMail);
            loggedAccount.setDeviceId(deviceId);
        }
    }

    public static String getLoggedUser() {
        return userPrefs.getString(KEY_USER_NAME, null);
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

    public static boolean loginUser(Account profile) {
        if (profile == null) {
            return false;
        }
        loggedAccount = profile;
        Editor editor = userPrefs.edit();
        editor.putLong(KEY_USER_ID, profile.getAccountId());
        editor.putString(KEY_USER_NAME, profile.getName());
        editor.putString(KEY_USER_EMAIL, profile.getEmail());


        Editor gcmEditor = gcmPrefs.edit();
        gcmEditor.putString(GcmConstants.PROPERTY_REG_ID, profile.getDeviceId());
        gcmEditor.commit();
        return editor.commit();
    }

    public static boolean logoutUser() {
        SharedPreferences userPrefs = WelcomeActivity.getAppContext().getSharedPreferences(USER_PREFS,
                Context.MODE_PRIVATE);

        Editor editor = userPrefs.edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);

        Editor gcmEditor = gcmPrefs.edit();
        gcmEditor.remove(GcmConstants.PROPERTY_REG_ID);
        gcmEditor.commit();

        loggedAccount = null;
        return editor.commit();
    }

    public static boolean isUserLoggedIn() {
        return getUserId() != 0;
    }

    public static Account getLoggedAccount() {
        return loggedAccount;
    }
}
