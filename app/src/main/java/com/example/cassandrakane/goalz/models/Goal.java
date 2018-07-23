package com.example.cassandrakane.goalz.models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Goal")
public class Goal extends ParseObject implements Parcelable {

    private boolean isSelected = false;
    public boolean continueStreak;
    public boolean showClock = false;


    public Goal() {
        super();
    }

    public Goal(String title, String description, int duration, int frequency, int progress, int streak, ArrayList<ParseObject> story, ParseUser user, Boolean itemAdded, Date updateBy) {
        super();
        setTitle(title);
        setDescription(description);
        setDuration(duration);
        setFrequency(frequency);
        setProgress(progress);
        setStreak(streak);
        setStory(story);
        setUser(user);
        setItemAdded(itemAdded);
        setUpdateStoryBy(updateBy);
    }

    public ArrayList<ParseObject> getStory() {
        List<ParseObject> arr = null;
        try {
            arr = fetchIfNeeded().getList("images");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList<ParseObject> story = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            story.add(arr.get(i));
        }
        return story;
    }

    public ArrayList<String> getStoryUrls() {
        ArrayList<ParseObject> images = getStory();
        ArrayList<String> imageUrls = new ArrayList<String>();
        for (ParseObject i : images) {
            try {
                imageUrls.add(i.fetchIfNeeded().getParseFile("image").getUrl());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return imageUrls;
    }

    public String getTitle() {
        try {
            return fetchIfNeeded().getString("title");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getDescription() {
        try {
            return fetchIfNeeded().getString("description");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getDuration() {
        try {
            return fetchIfNeeded().getInt("duration");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getFrequency() {
        try {
            return fetchIfNeeded().getInt("frequency");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getProgress() {
        try {
            return fetchIfNeeded().getInt("progress");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getStreak() {
        try {
            return fetchIfNeeded().getInt("streak");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public ParseUser getUser() {
        try {
            return fetchIfNeeded().getParseUser("user");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getUpdateStoryBy(){
        try {
            return fetchIfNeeded().getDate("updateBy");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public int getIntId() {
        String id = getObjectId();
        return id.hashCode();
    }

    public boolean getIsItemAdded() {
        try {
            return fetchIfNeeded().getBoolean("itemAdded");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setStory(ArrayList<ParseObject> story) {
        put("images", story);
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public void setDuration(int duration) {
        put("duration", duration);
    }

    public void setFrequency(int frequency) {
        put("frequency", frequency);
    }

    public void setProgress(int progress) {
        put("progress", progress);
    }

    public void setStreak(int streak) {
        put("streak", streak);
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public boolean getCompleted() {
        return getProgress() >= getDuration();
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setItemAdded(boolean itemAdded) { put("itemAdded", itemAdded); }

    public boolean isSelected() {
        return isSelected;
    }

    public static class Query extends ParseQuery<Goal> {
        public Query() {
            super(Goal.class);
        }

        public Query getTop() {
            orderByDescending("createdAt");
            return this;
        }

        public Query withImages(){
            include("images");
            return this;
        }
    }

    public void setUpdateStoryBy(Date updateStoryBy){
        put("updateBy", updateStoryBy);
    }
}
