package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("SentFriendRequests")
public class SentFriendRequests extends ParseObject {

    public SentFriendRequests() {
        super();
    }

    public SentFriendRequests(ParseUser fromUser, ParseUser toUser) {
        setFromUser(fromUser);
        setToUser(toUser);
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

    public void setFromUser(ParseUser user) {
        put("fromUser", user);
    }

    public void setToUser(ParseUser user) {
        put("toUser", user);
    }

}
