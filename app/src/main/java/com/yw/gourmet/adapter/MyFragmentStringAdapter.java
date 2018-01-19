package com.yw.gourmet.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * auth: lewis-v
 * time: 2018/1/19.
 */

public class MyFragmentStringAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> listTitle;

    public MyFragmentStringAdapter(FragmentManager fm, List<Fragment> fragments, List<String> listTitle) {
        super(fm);
        this.fragmentList = fragments;
        this.listTitle = listTitle;
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
        return listTitle.get(position);
    }
}
