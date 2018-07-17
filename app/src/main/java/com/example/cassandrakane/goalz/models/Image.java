package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


@ParseClassName("Image")
public class Image extends ParseObject{

    public Image() {
        super();
    }

    public Image(ParseFile image, String caption, ParseObject goal) {
        super();
        setImage(image);
        setCaption(caption);
        setGoal(goal);
    }

    public ParseFile getImage() {
        return getParseFile("image");
    }

    public String getDescription() {
        return getString("description");
    }


    public void setImage(ParseFile image) {
        put("image", image);
    }

    public void setCaption(String caption) {
        put("caption", caption);
    }

    public void setGoal(ParseObject goal) {
        put("goal", goal);
    }

    public static class Query extends ParseQuery<Image> {
        public Query() {
            super(Image.class);
        }

        public Image.Query getTop(){
            setLimit(20);
            return this;
        }

        public Image.Query withGoal(){
            include("goal");
            return this;
        }
    }
}
