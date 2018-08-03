package com.example.cassandrakane.goalz.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.example.cassandrakane.goalz.LoginActivity;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class NavigationHelper {

    ViewPager viewPager;

    public NavigationHelper(ViewPager vp) {
        viewPager = vp;
    }

    public void toCamera() {
        viewPager.setCurrentItem(0);
    }

    public void toGoals() {
        viewPager.setCurrentItem(1);
    }

    public void toFeed() {
        viewPager.setCurrentItem(2);
    }

    public void toNotifications() {
        // TODO update when notifications fragment moved
        viewPager.setCurrentItem(4);
    }

    public void logout(final Activity activity) {
        List<Goal> goals = ParseUser.getCurrentUser().getList("goals");
        NotificationHelper notificationHelper = new NotificationHelper(activity);
        if (goals != null) {
            for (Goal goal : goals) {
                notificationHelper.cancelReminder(goal);
            }
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Goal");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                ParseObject.unpinAllInBackground(objects);
            }
        });

        ParseQuery<ParseUser> queryTwo = ParseUser.getQuery();
        queryTwo.fromLocalDatastore();
        queryTwo.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                ParseObject.unpinAllInBackground(objects);
            }
        });

        ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(activity, "Successfully logged out.", Toast.LENGTH_LONG);
                    Intent i = new Intent(activity, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(i);
                    activity.overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
                    activity.finish();
                } else {
                    e.printStackTrace();
                }

            }
        });

    }

}
