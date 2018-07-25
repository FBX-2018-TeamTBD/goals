package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("SharedGoal")
public class SharedGoal extends Goal {

    public SharedGoal() { }

    public SharedGoal(Goal goal, List<ParseUser> friends, List<ParseUser> pending, List<ParseUser> approved) {
        super(goal.getTitle(), goal.getDescription(), goal.getDuration(), goal.getFrequency(),
                goal.getProgress(), goal.getStreak(), goal.getStory(), goal.getUser(),
                goal.getIsItemAdded(), goal.getUpdateStoryBy());
        setFriends(friends);
        setPendingUsers(pending);
        setApprovedUsers(approved);
    }

    public List<ParseUser> getFriends() {
        try {
            return fetchIfNeeded().getList("friends");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ParseUser> getPendingUsers() {
        try {
            return fetchIfNeeded().getList("pendingUsers");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ParseUser> getApprovedUsers() {
        try {
            return fetchIfNeeded().getList("approvedUsers");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setFriends(List<ParseUser> friends) {
        put("friends", friends);
    }

    public void setPendingUsers(List<ParseUser> users) {
        put("pendingUsers", users);
    }

    public void setApprovedUsers(List<ParseUser> users) {
        put("approvedUsers", users);
    }
}
