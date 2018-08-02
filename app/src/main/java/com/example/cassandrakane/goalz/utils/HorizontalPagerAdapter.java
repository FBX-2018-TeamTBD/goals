package com.example.cassandrakane.goalz.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class HorizontalPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private List<Fragment> mPages;

    public HorizontalPagerAdapter(FragmentManager fragmentManager, Context context, List<Fragment> pages) {
        super(fragmentManager);
        mContext = context;
        mPages = pages;
    }

     @Override
    public Fragment getItem(int position) {
         switch (position){
             case 0: return mPages.get(0);
             case 1: return mPages.get(1);
             case 2: return mPages.get(2);
             default: return null;
         }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
