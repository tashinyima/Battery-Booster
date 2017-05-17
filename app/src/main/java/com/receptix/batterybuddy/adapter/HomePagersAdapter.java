package com.receptix.batterybuddy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by zero1 on 5/8/2017.
 */

public class HomePagersAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private ArrayList<String> tabTitleList = new ArrayList<>();

    public HomePagersAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragments(Fragment fragments,String titles){
        this.fragmentList.add(fragments);
        this.tabTitleList.add(titles);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitleList.get(position);
    }
}
