package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String FROM_USER_KEY = "fromUser";
    public static final String TO_USER_KEY = "toUser";
    public static final String BODY_KEY = "body";

    public ParseUser getFromUser() {
        return getParseUser(FROM_USER_KEY);
    }

    public ParseUser getToUser() {
        return getParseUser(TO_USER_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setFromUser(ParseUser user) {
        put(FROM_USER_KEY, user);
    }

    public void setToUser(ParseUser user) {
        put(TO_USER_KEY, user);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }
}