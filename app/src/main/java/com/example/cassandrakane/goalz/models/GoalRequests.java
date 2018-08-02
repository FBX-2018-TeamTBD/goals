package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("GoalRequests")
public class GoalRequests extends ParseObject {

    public GoalRequests() {
        super();
    }

    public GoalRequests(ParseUser user, ParseUser fromUser, Goal goal) {
        setUser(user);
        setFromUser(fromUser);
        setGoal(goal);
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public void setFromUser(ParseUser fromUser) {
        put("fromUser", fromUser);
    }

    public void setGoal(Goal goal) {
        put("goal", goal);
    }

    public ParseUser getFromUser() {
        try {
            return fetchIfNeeded().getParseUser("fromUser");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
