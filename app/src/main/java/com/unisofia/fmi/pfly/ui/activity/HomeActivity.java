package com.unisofia.fmi.pfly.ui.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.account.UserManager;
import com.unisofia.fmi.pfly.api.model.Project;
import com.unisofia.fmi.pfly.api.model.Task;
import com.unisofia.fmi.pfly.ui.fragment.AdvancedSearchFragment;
import com.unisofia.fmi.pfly.ui.fragment.BaseMenuFragment;
import com.unisofia.fmi.pfly.ui.fragment.ProjectsFragment;
import com.unisofia.fmi.pfly.ui.fragment.TaskFragment;
import com.unisofia.fmi.pfly.ui.fragment.TasksFragment;
import com.unisofia.fmi.pfly.ui.fragment.ViewPagerFragment;

@SuppressWarnings("deprecation")
public class HomeActivity extends BaseActivity implements TasksFragment.OnTaskSelectedListener,
        ProjectsFragment.OnProjectSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private MenuItem mCurrentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawerLayout();
        initNavigationView();
        initPager();
    }

    private void initDrawerLayout(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void initNavigationView(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setItemIconTintList(null);

        TextView userName = (TextView) headerView.findViewById(R.id.userName);
        userName.setText(UserManager.getLoggedUser());
        TextView userMail = (TextView) headerView.findViewById(R.id.userMail);
        userMail.setText(UserManager.getLoggedUserMail());

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initPager(){
        ViewPagerFragment fragment = new ViewPagerFragment();
        fragment.setPagerType(ViewPagerFragment.PagerType.TASK_PAGER);
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, 0);
            default:
                return super.onOptionsItemSelected(item);
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
        mCurrentItem = null;
    }

    @Override
    public void onProjectSelected(Project project) {
        TasksFragment tasksFragment = new TasksFragment();
        Bundle args = new Bundle();
        args.putSerializable("projectId", project.getProjectId());
        tasksFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, tasksFragment)
                .addToBackStack(null)
                .commit();
        mCurrentItem = null;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (mCurrentItem == item) {
            mDrawerLayout.closeDrawers();
            return false;
        }

        BaseMenuFragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_tasks:
                fragment = new ViewPagerFragment();
                break;
            case R.id.nav_projects:
                fragment = new ProjectsFragment();
                break;
//            case R.id.nav_scale:
//                fragment = new ScaleFragment();
//                break;
            case R.id.nav_advanced_search:
                fragment = new AdvancedSearchFragment();
                break;
            case R.id.nav_calendar:
                startAndroidCalendarApp();
                break;

            case R.id.nav_logout:
                onLogout();
            default:
                onLogout();
                break;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment).addToBackStack(null).commit();
            mCurrentItem = item;
            mDrawerLayout.closeDrawers();
        }

        return false;
    }

    private void startAndroidCalendarApp(){
        Uri.Builder builder = Uri.parse("content://com.android.calendar").buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, System.currentTimeMillis());
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        startActivity(intent);
    }
}
