package com.example.cassandrakane.goalz.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.cassandrakane.goalz.CameraFragment;
import com.example.cassandrakane.goalz.FeedFragment;
import com.example.cassandrakane.goalz.ProfileFragment;

public class HorizontalPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public HorizontalPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        mContext = context;
    }

     @Override
    public Fragment getItem(int position) {
         switch (position){
             case 0: return new CameraFragment();
             case 1: return new ProfileFragment();
             case 2: return new FeedFragment();
             default: return null;
         }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
