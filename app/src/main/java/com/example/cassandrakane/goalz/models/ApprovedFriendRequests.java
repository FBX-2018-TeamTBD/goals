package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ApprovedFriendRequests")
public class ApprovedFriendRequests extends ParseObject {

    public ApprovedFriendRequests() {
        super();
    }

    public ApprovedFriendRequests(ParseUser from, ParseUser to) {
        setFrom(from);
        setTo(to);
    }

    public ParseUser getFromUser() {
        try {
            return fetchIfNeeded().getParseUser("fromUser");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ParseUser getToUser() {
        try {
            return fetchIfNeeded().getParseUser("toUser");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setFrom(ParseUser user) {
        put("fromUser", user);
    }

    public void setTo(ParseUser user) {
        put("toUser", user);
    }
}

