package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {

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

    public String getBody() {
        try {
            return fetchIfNeeded().getString("body");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setFromUser(ParseUser user) {
        put("fromUser", user);
    }

    public void setToUser(ParseUser user) {
        put("toUser", user);
    }

    public void setBody(String body) {
        put("body", body);
    }
}