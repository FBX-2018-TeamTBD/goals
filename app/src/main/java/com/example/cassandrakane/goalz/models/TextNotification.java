package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("TextNotification")
public class TextNotification extends ParseObject {

    public TextNotification() {
        super();
    }

    public TextNotification(String text, ParseUser user) {
        setText(text);
        setUser(user);
    }

    public String getText() {
        try {
            return fetchIfNeeded().getString("text");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public ParseUser getUser() {
        try {
            return fetchIfNeeded().getParseUser("user");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setText(String text) {
        put("text", text);
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }
}
