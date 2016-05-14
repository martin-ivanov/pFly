package com.unisofia.fmi.pfly;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class PFlyApp extends Application {


    private static Context mContext;

    public static Context getAppContext() {
        return mContext;
    }

}
