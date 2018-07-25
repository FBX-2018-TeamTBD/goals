package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("GoalRequests")
public class GoalRequests extends ParseObject {

    public GoalRequests() {
        super();
    }

    public GoalRequests(ParseUser user, SharedGoal goal) {
        setUser(user);
        setGoal(goal);
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public void setGoal(SharedGoal goal) {
        put("goal", goal);
    }

}
