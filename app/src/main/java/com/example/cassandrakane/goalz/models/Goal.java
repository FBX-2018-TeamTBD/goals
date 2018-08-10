package com.example.cassandrakane.goalz.models;

import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ParseClassName("Goal")
public class Goal extends ParseObject implements Parcelable {

    private boolean isSelected = false;

    public Goal() {
        super();
    }

    public Goal(String title, int duration, int frequency, int progress,
                int streak, ArrayList<ParseObject> story, ParseUser user, Boolean itemAdded, Date updateBy,
                List<ParseUser> friends, List<ParseUser> approvedUsers, List<ParseUser> pendingUsers,
                Map<String, String> userAdded, List<ParseObject> reactions) {
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
        setReactions(reactions);
    }

    public List<ParseObject> getStory() {
        List<ParseObject> story = getList("images");
        return story;
    }

    public List<String> getStoryUrls() {
        List<ParseObject> images = getStory();
        List<String> imageUrls = new ArrayList<String>();
        for (ParseObject i : images) {
            try {
                if (i.fetchIfNeeded().get("video") != null) {
                    Video mediaObject = (Video) i;
                    imageUrls.add(mediaObject.getParseFile("image").getUrl());
                } else {
                    Image mediaObject = (Image) i;
                    imageUrls.add(mediaObject.getParseFile("image").getUrl());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return imageUrls;
    }

    public void setReactions(List<ParseObject> reactions) {
        Log.i("sdf", ""+reactions.size());
        put("allReactions", reactions);
    }

    public List<ParseObject> getReactions() {
        List<ParseObject> arr = getList("allReactions");
        if (arr == null) {
            return new ArrayList<>();
        }
        return arr;
    }

    public String getTitle() {
        return getString("title");
    }

    public int getDuration() {
        return getInt("duration");
    }

    public int getFrequency() {
        return getInt("frequency");
    }

    public int getProgress() {
        return getInt("progress");
    }

    public int getStreak() {
        return getInt("streak");
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public Date getUpdateStoryBy(){
        return getDate("updateBy");
    }

    public int getIntId() {
        String id = getObjectId();
        return id.hashCode();
    }

    public boolean getIsItemAdded() {
        return getBoolean("itemAdded");
    }

    public List<ParseUser> getFriends() {
        List<ParseUser> friends = getList("friends");
        if (friends == null) {
            return new ArrayList<ParseUser>();
        }
        return friends;
    }

    public List<ParseUser> getApprovedUsers() {
        List<ParseUser> users = getList("approvedUsers");

        if (users == null) {
            return new ArrayList<ParseUser>();
        }
        return users;
    }

    public List<ParseUser> getPendingUsers() {
        List<ParseUser> users = getList("pendingUsers");
        if (users == null) {
            return new ArrayList<ParseUser>();
        }
        return users;
    }

    public Map<String, String> getUserAdded(){
        return (Map) get("userAdded");
    }

    public void setUserAdded(Map<String, String> userAdded){ put("userAdded", userAdded); }

    public void setStory(List<ParseObject> story) {
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

    public void setUpdateStoryBy(Date updateStoryBy){
        put("updateBy", updateStoryBy);
    }
}
