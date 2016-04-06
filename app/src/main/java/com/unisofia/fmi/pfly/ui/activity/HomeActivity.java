package com.unisofia.fmi.pfly.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.account.UserManager;
import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.ui.fragment.BaseMenuFragment;
import com.unisofia.fmi.pfly.ui.fragment.ProjectsFragment;
import com.unisofia.fmi.pfly.ui.fragment.MenuFragment;
import com.unisofia.fmi.pfly.ui.fragment.MenuFragment.MenuListener;
import com.unisofia.fmi.pfly.ui.fragment.TaskFragment;
import com.unisofia.fmi.pfly.ui.fragment.TasksFragment;

@SuppressWarnings("deprecation")
public class HomeActivity extends BaseActivity implements MenuListener, TasksFragment.OnTaskSelectedListener {

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

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
                R.string.app_name, /* "open drawer" description */
                R.string.app_name /* "close drawer" description */
        );

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
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, 0);
            default:
                return super.onOptionsItemSelected(item);
        }

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
            case PROJECTS:
                fragment = new ProjectsFragment();
                break;
            case TASKS:
                fragment = new TasksFragment();
                break;
            case SUBSCRIBE:
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pfly, menu);
        return true;
    }

    @Override
    public void onTaskSelected(Task task) {
        TaskFragment taskFragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putSerializable("task", task);
        taskFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, taskFragment)
                .addToBackStack(null)
                .commit();
    }
}
