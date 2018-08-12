package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Video")
public class Video extends ParseObject {

    public Video() {
        super();
    }

    public Video(ParseFile video, String caption, ParseFile image, ParseUser user, List<ParseUser> viewedBy, List<Reaction> reactions) {
        super();
        setVideo(video);
        setCaption(caption);
        setUser(user);
        setImage(image);
        setViewedBy(viewedBy);
        setReactions(reactions);
    }

    public ParseFile getVideo() {
        try {
            return fetchIfNeeded().getParseFile("video");
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

    public ParseFile getImage() {
        try {
            return fetchIfNeeded().getParseFile("image");
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

    public void setVideo(ParseFile video) {
        put("video", video);
    }

    public void setCaption(String caption) {
        put("caption", caption);
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public void setImage(ParseFile image) { put("image", image);}

    public void setViewedBy(List<ParseUser> viewedBy) { put("viewedBy", viewedBy); }

    public void setReactions(List<Reaction> reactions) {put("reactions", reactions); }
}
