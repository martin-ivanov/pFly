package com.unisofia.fmi.pfly.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.ui.fragment.LoginFragment;
import com.unisofia.fmi.pfly.ui.fragment.RegisterFragment;
import com.unisofia.fmi.pfly.ui.fragment.TasksFragment;

/**
 * Created by martin.ivanov on 2016-06-07.
 */
public class WelcomePagerAdapter extends FragmentStatePagerAdapter  {
    Context context;

    public WelcomePagerAdapter(Context context, FragmentManager fr) {
        super(fr);
        this.context = context;
    }

    @Override
    public int getCount() {
        return (2);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        Fragment fragment;
        if (position == 0) {
            fragment = new LoginFragment();
        } else {
            fragment = new RegisterFragment();
        }
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public String getPageTitle(int position) {
        if (position == 0) {
            return context.getResources().getString(R.string.login);
        } else {
            return context.getResources().getString(R.string.register);
        }
    }
}
