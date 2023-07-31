package com.example.smartdrive.home.page.frags.StatisticsFrags;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_COUNT = 2;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RadarChart();
            case 1:
                return new PieChart();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Radar Chart";
            case 1:
                return "Graphes en secteurs";
            default:
                return "";
        }
    }


}
