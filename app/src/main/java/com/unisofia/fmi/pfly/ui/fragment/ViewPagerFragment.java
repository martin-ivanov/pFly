package com.unisofia.fmi.pfly.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.ui.adapter.TaskPagerAdapter;

/**
 * Created by martin.ivanov on 2016-06-07.
 */
public class ViewPagerFragment extends BaseMenuFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.pager, container, false);
        ViewPager pager=(ViewPager)result.findViewById(R.id.pager);

        pager.setAdapter(buildAdapter());

        return(result);
    }

    private PagerAdapter buildAdapter() {
        return new TaskPagerAdapter(getActivity(), getChildFragmentManager());
    }
}
