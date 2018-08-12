package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


@ParseClassName("Image")
public class Image extends ParseObject{

    public Image() {
        super();
    }

    public Image(ParseFile image, String caption, ParseUser user, List<ParseUser> viewedBy, List<Reaction> reactions) {
        super();
        setImage(image);
        setCaption(caption);
        setUser(user);
        setViewedBy(viewedBy);
        setReactions(reactions);
    }

    public ParseFile getImage() {
        try {
            return fetchIfNeeded().getParseFile("image");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCaption() {
        try {
            return fetchIfNeeded().getString("caption");
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

    public List<ParseUser> getViewedBy() {
        try {
            return fetchIfNeeded().getList("viewedBy");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Reaction> getReactions() {
        try {
            return fetchIfNeeded().getList("reactions");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void setImage(ParseFile image) {
        put("image", image);
    }

    public void setCaption(String caption) { put("caption", caption); }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public void setViewedBy(List<ParseUser> viewedBy) { put("viewedBy", viewedBy); }

    public void setReactions(List<Reaction> reactions) { put("reactions", reactions); }
}
