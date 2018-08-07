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
import java.util.Map;

@ParseClassName("Goal")
public class Goal extends ParseObject implements Parcelable {

    private boolean isSelected = false;
    public boolean continueStreak;
    public boolean showClock = false;

    public Goal() {
        super();
    }

    public Goal(String title, int duration, int frequency, int progress,
                int streak, ArrayList<ParseObject> story, ParseUser user, Boolean itemAdded, Date updateBy,
                List<ParseUser> friends, List<ParseUser> approvedUsers, List<ParseUser> pendingUsers,
                Map<String, String> userAdded) {
        super();
        setTitle(title);
        setDuration(duration);
        setFrequency(frequency);
        setProgress(progress);
        setStreak(streak);
        setStory(story);
        setUser(user);
        setItemAdded(itemAdded);
        setUpdateStoryBy(updateBy);
        setFriends(friends);
        setApprovedUsers(approvedUsers);
        setPendingUsers(pendingUsers);
        setUserAdded(userAdded);
    }

    public ArrayList<ParseObject> getStory() {
        List<ParseObject> arr = null;
//        try {
        arr = getList("images");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        ArrayList<ParseObject> story = new ArrayList<>();
        if (arr != null) {
            for (int i = 0; i < arr.size(); i++) {
                story.add(arr.get(i));
            }
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

    public void setReactions(List<ParseObject> reactions) { put("allReactions", reactions); }

    public List<ParseObject> getReactions() {
        List<ParseObject> arr = null;
//        try {
        arr = getList("allReactions");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        ArrayList<ParseObject> reacts = new ArrayList<>();
        if (arr != null) {
            for (int i = 0; i < arr.size(); i++) {
                reacts.add(arr.get(i));
            }
        }
        return reacts;
    }

    public String getTitle() {
//        try {
        return getString("title");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return "";
    }

    public int getDuration() {
//        try {
            return getInt("duration");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return 0;
    }

    public int getFrequency() {
//        try {
            return getInt("frequency");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return 0;
    }

    public int getProgress() {
//        try {
            return getInt("progress");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return 0;
    }

    public int getStreak() {
//        try {
            return getInt("streak");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return 0;
    }

    public ParseUser getUser() {
//        try {
            return getParseUser("user");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    public Date getUpdateStoryBy(){
//        try {
            return getDate("updateBy");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return new Date();
    }

    public int getIntId() {
        String id = getObjectId();
        return id.hashCode();
    }

    public boolean getIsItemAdded() {
//        try {
            return getBoolean("itemAdded");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return false;
    }

    public List<ParseUser> getFriends() {
        List<ParseUser> friends = null;
//        try {
            friends = getList("friends");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        if (friends == null) {
            return new ArrayList<ParseUser>();
        }
        return friends;
    }

    public List<ParseUser> getApprovedUsers() {
        List<ParseUser> users = null;
//        try {
            users = getList("approvedUsers");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        if (users == null) {
            return new ArrayList<ParseUser>();
        }
        return users;
    }

    public List<ParseUser> getPendingUsers() {
        List<ParseUser> users = null;
//        try {
            users = getList("pendingUsers");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        if (users == null) {
            return new ArrayList<ParseUser>();
        }
        return users;
    }

    public Map<String, String> getUserAdded(){
        return (Map) get("userAdded");
    }

    public void setUserAdded(Map<String, String> userAdded){ put("userAdded", userAdded); }

    public void setStory(ArrayList<ParseObject> story) {
        put("images", story);
    }

    public void setTitle(String title) {
        put("title", title);
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

    public void setFriends(List<ParseUser> friends) {
        put("friends", friends);
    }

    public void setApprovedUsers(List<ParseUser> users) {
        put("approvedUsers", users);
    }

    public void setPendingUsers(List<ParseUser> users) {
        put("pendingUsers", users);
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
