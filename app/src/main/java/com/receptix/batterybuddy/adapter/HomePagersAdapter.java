package com.receptix.batterybuddy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by zero1 on 5/8/2017.
 */

public class HomePagersAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabtitles = new ArrayList<>();

    public HomePagersAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragments(Fragment fragments,String titles){

        this.fragments.add(fragments);
        this.tabtitles.add(titles);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles.get(position);
    }
}
