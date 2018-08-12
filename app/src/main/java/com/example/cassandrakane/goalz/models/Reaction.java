package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Reaction")
public class Reaction extends ParseObject{

    public Reaction() {
        super();
    }

    public Reaction(String type, ParseUser user) {
        super();
        setType(type);
        setUser(user);
    }

    public String getType() {
        try {
            return fetchIfNeeded().getString("type");
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

    public void setType(String type) {
        put("type", type);
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }
}
