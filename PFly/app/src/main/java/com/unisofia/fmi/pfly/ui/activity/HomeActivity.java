package com.unisofia.fmi.pfly.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.account.UserManager;
import com.unisofia.fmi.pfly.ui.fragment.AddTaskFragment;
import com.unisofia.fmi.pfly.ui.fragment.BaseMenuFragment;
import com.unisofia.fmi.pfly.ui.fragment.ProjectsFragment;
import com.unisofia.fmi.pfly.ui.fragment.MenuFragment;
import com.unisofia.fmi.pfly.ui.fragment.MenuFragment.MenuListener;
import com.unisofia.fmi.pfly.ui.fragment.SubscribeFragment;

@SuppressWarnings("deprecation")
public class HomeActivity extends BaseActivity implements MenuListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuFragment mMenuFragment;
    private com.unisofia.fmi.pfly.ui.fragment.MenuFragment.MenuItem mCurrentitem;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
                R.string.app_name, /* "open drawer" description */
                R.string.app_name /* "close drawer" description */
        );
//        ){
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
//                getSupportActionBar().setTitle(mTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//                syncState();
//            }
//
//            /** Called when a drawer has settled in a completely open state. */
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                getSupportActionBar().setTitle(mDrawerTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//                syncState();
//            }
//        };
//        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        // Set the drawer toggle as the DrawerListener


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Set up menu
        mMenuFragment = new MenuFragment();
        mMenuFragment.setMenuListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_menu, mMenuFragment).commit();

        // Set up initial fragment
        BaseMenuFragment fragment = new ProjectsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment).commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMenuItemSelected(
            com.unisofia.fmi.pfly.ui.fragment.MenuFragment.MenuItem item) {
        if (mCurrentitem == item) {
            mDrawerLayout.closeDrawers();
            return;
        }

        BaseMenuFragment fragment = null;
        switch (item) {
            case HOME:
                fragment = new ProjectsFragment();
                break;
            case ADD_RECIPE:
                fragment = new AddTaskFragment();
                break;
            case SUBSCRIBE:
                fragment = new SubscribeFragment();
                break;
            case LOGOUT:
            default:
                onLogout();
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();
            mMenuFragment.setSelectedItem(item);
            mDrawerLayout.closeDrawers();
        }
    }

    private void onLogout() {
        UserManager.logoutUser();
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
