package com.example.cassandrakane.goalz.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.cassandrakane.goalz.FriendRequestsFragment;
import com.example.cassandrakane.goalz.GoalRequestsFragment;

public class NotificationsPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment mCurrentFragment;

    public NotificationsPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new GoalRequestsFragment();
            case 1: return new FriendRequestsFragment();
        }
        return null;
    }
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "Goal Requests";
            case 1: return "Friend Requests";
            default: return null;
        }
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }
}
