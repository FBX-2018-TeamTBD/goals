package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Video")
public class Video extends ParseObject {

    public Video() {
        super();
    }

    public Video(ParseFile video, String caption) {
        super();
        setVideo(video);
        setCaption(caption);
    }

    public ParseFile getVideo() {
        return getParseFile("image");
    }


    public void setVideo(ParseFile video) {
        put("video", video);
    }

    public void setCaption(String caption) {
        put("caption", caption);
    }

}
