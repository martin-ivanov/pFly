package com.unisofia.fmi.pfly;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Map;

public class PFlyApp extends Application {

    private static SharedPreferences prefs;
    private static Context mContext;

//    public static SharedPreferences getPrefs(Context context) {
//
//
//        Map generalPrefs = context.getSharedPreferences("prefs_general.xml", MODE_PRIVATE).getAll();
//
//
//
//
//        return prefs;
//    }

    public static Context getAppContext() {
        return mContext;
    }

}
