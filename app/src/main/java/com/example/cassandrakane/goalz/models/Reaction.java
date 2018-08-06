package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
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

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public String getType() {
        return getString("type");
    }

    public void setType(String type) {
        put("type", type);
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }
}
