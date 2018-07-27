package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Video")
public class Video extends ParseObject {

    public Video() {
        super();
    }

    public Video(ParseFile video, String caption, ParseFile thumbnail, ParseUser user) {
        super();
        setVideo(video);
        setCaption(caption);
        setThumbnail(thumbnail);
        setUser(user);
    }

    public ParseFile getVideo() {
        return getParseFile("image");
    }

    public List<ParseUser> getViewedBy() { return getList("viewedBy"); }

    public void setVideo(ParseFile video) {
        put("video", video);
    }

    public void setCaption(String caption) {
        put("caption", caption);
    }

    public void setThumbnail(ParseFile thumbnail) { put("image", thumbnail);}

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public void setViewedBy(List<ParseUser> viewedBy) { put("viewedBy", viewedBy); }
}
