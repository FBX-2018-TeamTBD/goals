package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
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

    public void setFrom(ParseUser user) {
        put("fromUser", user);
    }

    public void setTo(ParseUser user) {
        put("toUser", user);
    }
}

