package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;


@ParseClassName("Image")
public class Image extends ParseObject{

    public Image() {
        super();
    }

    public Image(ParseFile image, String caption) {
        super();
        setImage(image);
        setCaption(caption);
    }

    public ParseFile getImage() {
        return getParseFile("image");
    }

    public String getCaption() {
        return getString("caption");
    }


    public void setImage(ParseFile image) {
        put("image", image);
    }

    public void setCaption(String caption) {
        put("caption", caption);
    }
}
