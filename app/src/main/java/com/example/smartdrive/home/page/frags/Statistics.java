package com.example.smartdrive.home.page.frags;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartdrive.R;
import com.example.smartdrive.home.page.frags.StatisticsFrags.MyPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class Statistics extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.statistics, container, false);


        TabLayout tabLayout = v.findViewById(R.id.tab_layout);
        ViewPager viewPager = v.findViewById(R.id.view_pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);



        return v;

    }





}