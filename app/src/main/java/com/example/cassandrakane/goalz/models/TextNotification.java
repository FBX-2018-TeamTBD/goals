package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("TextNotification")
public class TextNotification extends ParseObject {

    public TextNotification() {
        super();
    }

    public TextNotification(String text, ParseUser user, ParseFile image) {
        setText(text);
        setUser(user);
        setImage(image);
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

    public ParseFile getImage() {
        try {
            return fetchIfNeeded().getParseFile("image");
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

    public void setImage(ParseFile image) {
        put("image", image);
    }
}
