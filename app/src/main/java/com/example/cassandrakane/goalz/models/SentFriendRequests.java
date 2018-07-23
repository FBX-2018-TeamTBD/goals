package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("SentFriendRequests")
public class SentFriendRequests extends ParseObject {

    public SentFriendRequests() {
        super();
    }

    public SentFriendRequests(ParseUser from, ParseUser to) {
        setFrom(from);
        setTo(to);
    }

    public void setTo(ParseUser user) {
        put("toUser", user);
    }

    public void setFrom(ParseUser user) {
        put("fromUser", user);
    }

}
