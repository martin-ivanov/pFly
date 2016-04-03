package com.unisofia.fmi.pfly.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.unisofia.fmi.pfly.PFlyApp;
import com.unisofia.fmi.pfly.api.model.Profile;
import com.unisofia.fmi.pfly.gcm.util.GcmConstants;

public class UserManager {

    private static final String USER_PREFS = "USER_PREFS";
    private static final String KEY_USER_ID = "KEY_USER_ID";
    private static final String KEY_USER_NAME = "KEY_USER_NAME";
    private static final String KEY_USER_EMAIL = "KEY_USER_EMAIL";

    private UserManager() {
        // forbid instantiation
    }

//    public static Profile getProfile() {
//        SharedPreferences userPrefs = PFlyApp.getAppContext().getSharedPreferences(USER_PREFS,
//                Context.MODE_PRIVATE);
//
//        Profile profile = new Profile();
//        profile.setId(userPrefs.getLong(KEY_USER_ID, 0));
//        profile.setName(userPrefs.getString(KEY_USER_NAME, ""));
//        profile.setEmail(userPrefs.getString(KEY_USER_EMAIL, ""));
//
//        return profile;
//    }

    public static long getUserId() {
        SharedPreferences userPrefs = PFlyApp.getAppContext().getSharedPreferences(USER_PREFS,
                Context.MODE_PRIVATE);

        return userPrefs.getLong(KEY_USER_ID, 0);
    }

    public static boolean loginUser(Profile profile) {
        if (profile == null) {
            return false;
        }

        SharedPreferences userPrefs = PFlyApp.getAppContext().getSharedPreferences(USER_PREFS,
                Context.MODE_PRIVATE);

        Editor editor = userPrefs.edit();

        editor.putLong(KEY_USER_ID, profile.getId());
        editor.putString(KEY_USER_NAME, profile.getName());
        editor.putString(KEY_USER_EMAIL, profile.getEmail());

        SharedPreferences gcmPreds = PFlyApp.getAppContext().getSharedPreferences(
                GcmConstants.GCM_PREFS, Context.MODE_PRIVATE);
        Editor gcmEditor = gcmPreds.edit();
        gcmEditor.remove(GcmConstants.PROPERTY_REG_ID);
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

        return editor.commit();
    }

    public static boolean isUserLoggedIn() {
        return getUserId() != 0;
    }
}
