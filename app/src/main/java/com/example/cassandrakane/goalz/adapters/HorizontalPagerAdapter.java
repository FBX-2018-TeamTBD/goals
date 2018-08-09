package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HorizontalPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private List<Fragment> mPages;
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;

    public HorizontalPagerAdapter(FragmentManager fragmentManager, Context context, List<Fragment> pages) {
        super(fragmentManager);
        mContext = context;
        mPages = pages;
        mFragmentTags = new HashMap<Integer,String>();
        mFragmentManager = fragmentManager;
    }

     @Override
    public Fragment getItem(int position) {
         switch (position){
             case 0:
                 return mPages.get(0);
             case 1:
                 return mPages.get(1);
             case 2:
                 return mPages.get(2);
             default: return null;
         }
    }

    @Override
    public int getCount() {
        return 3;
    }

}
