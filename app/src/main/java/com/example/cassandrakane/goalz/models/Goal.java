package com.example.cassandrakane.goalz.models;

import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ParseClassName("Goal")
public class Goal extends ParseObject implements Parcelable {

    private boolean isSelected = false;
//    private Date updateStoryBy;

    public Goal() {
        super();
    }

    public Goal(String title, String description, int duration, int frequency, int progress, int streak, ArrayList<ParseObject> story, ParseUser user) {
        super();
        setTitle(title);
        setDescription(description);
        setDuration(duration);
        setFrequency(frequency);
        setProgress(progress);
        setStreak(streak);
        setStory(story);
        setUser(user);
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
        return getProgress() == getDuration();
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


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
    }

    public Date getUpdateStoryBy(){
        try {
            return fetchIfNeeded().getDate("updateBy");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public void setUpdateStoryBy(ArrayList<ParseObject> story){
        Date updateStoryBy = null;
        if (story.size() != 0){
            Image lastUpdate = (Image) story.get(story.size() - 1);
            long sum = lastUpdate.getCreatedAt().getTime() + TimeUnit.MINUTES.toMillis(getFrequency());
            updateStoryBy = new Date(sum);
        }
        put("updateBy", updateStoryBy);
    }
}
