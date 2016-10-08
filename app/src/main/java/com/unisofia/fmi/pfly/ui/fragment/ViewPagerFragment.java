package com.unisofia.fmi.pfly.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andraskindler.parallaxviewpager.ParallaxViewPager;
import com.unisofia.fmi.pfly.R;
import com.unisofia.fmi.pfly.ui.adapter.TaskPagerAdapter;
import com.unisofia.fmi.pfly.ui.adapter.WelcomePagerAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by martin.ivanov on 2016-06-07.
 */
public class ViewPagerFragment extends BaseMenuFragment {

    private PagerType type;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("pagerType", type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.pager, container, false);
        if (type == null){
            type = (PagerType) savedInstanceState.getSerializable("pagerType");
        }

        initPager(result);
        return (result);
    }

    public void setPagerType(PagerType type) {
        this.type = type;
    }

    private ViewPager initPager(View result){
        ParallaxViewPager pager = (ParallaxViewPager) result.findViewById(R.id.parallax_pager);
//        ParallaxViewPager pager = new ParallaxViewPager(getActivity());
//        ViewPager pager = (ViewPager) result.findViewById(R.id.pager);
        pager.setAdapter(buildAdapter());
        if (type == PagerType.WELCOME_PAGER){
            pager.setBackgroundResource(R.drawable.pfly_background);
        }
        return pager;
    }


    private PagerAdapter buildAdapter() {
        switch (type) {
            case WELCOME_PAGER:
                return new WelcomePagerAdapter(getActivity(), getChildFragmentManager());
            case TASK_PAGER:
                return new TaskPagerAdapter(getActivity(), getChildFragmentManager());
        }
        return null;
    }


    public enum PagerType {
        WELCOME_PAGER,
        TASK_PAGER
    }
}
