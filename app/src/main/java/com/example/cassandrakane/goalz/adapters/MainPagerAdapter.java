package com.example.cassandrakane.goalz.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.cassandrakane.goalz.CameraFragment;
import com.example.cassandrakane.goalz.FeedFragment;
import com.example.cassandrakane.goalz.ProfileFragment;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    public MainPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new CameraFragment();
            case 1: return new ProfileFragment();
            case 2: return new FeedFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "Camera";
            case 1: return "Profile";
            case 2: return "Feed";
            default: return null;
    }
    }
}
