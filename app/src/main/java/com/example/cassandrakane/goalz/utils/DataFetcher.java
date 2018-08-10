package com.example.cassandrakane.goalz.utils;

import android.content.Context;
import android.content.Intent;

import com.example.cassandrakane.goalz.MainActivity;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class DataFetcher {

    List<Goal> userGoals;
    List<Goal> friendGoals;
    List<ParseUser> userFriends;
    ParseUser user;
    Context context;

    public DataFetcher(ParseUser currentUser, Context mContext){
        this.user = currentUser;
        this.context = mContext;
        user.pinInBackground();
        setUserGoals();
        setUserFriends();
        if (!mContext.getClass().getSimpleName().equals(MainActivity.class.getSimpleName())) {
            Intent i = new Intent(context, MainActivity.class);
            i.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(user));
            context.startActivity(i);
        }
    }

    public void setUserGoals() {
        List<ParseObject> arr = new ArrayList<>();
        userGoals = new ArrayList<>();
        try {
            arr = user.fetch().getList("goals");
        } catch(ParseException e) {
            e.printStackTrace();
        }

        if (arr != null) {
            try {
                ParseObject.fetchAllIfNeeded(arr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for(int i = 0; i < arr.size(); i++) {
                Goal goal = null;
                try {
                    goal = arr.get(i).fetch();
                } catch(ParseException e) {
                    e.printStackTrace();
                }
                if (goal != null && goal.getCompleted()) {
                    userGoals.add(goal);
                } else {
                    userGoals.add(0, goal);
                }
            }
        }
        ParseObject.unpinAllInBackground(userGoals);
        ParseObject.pinAllInBackground(userGoals);
    }

    public void setUserFriends() {
        userFriends = user.getList("friends");
        ParseObject.unpinAllInBackground(userFriends);
        ParseObject.pinAllInBackground(userFriends);
        friendGoals = new ArrayList<>();
        for (ParseUser friend : userFriends){
            getFriendGoals(friend);
        }
    }

    public void getFriendGoals(ParseUser friend) {
        List<ParseObject> arr = new ArrayList<>();
        try {
            arr = friend.fetch().getList("goals");
        } catch(ParseException e) {
            e.printStackTrace();
        }
        if (arr != null) {
            for (int i = 0; i < arr.size(); i++) {
                Goal goal = null;
                try {
                    goal = arr.get(i).fetch();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (goal != null && goal.getCompleted()) {
                    friendGoals.add(goal);
                } else {
                    friendGoals.add(0, goal);
                }
            }

            ParseObject.pinAllInBackground(friendGoals);
        }
    }
}
