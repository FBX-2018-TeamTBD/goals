package com.example.cassandrakane.goalz.models;

import android.os.Parcelable;

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
                Map<String, String> userAdded, List<Reaction> allReactions) {
        super();
        setTitle(title);
        setUser(user);
        setStory(story);
        setProgress(progress);
        setDuration(duration);
        setFrequency(frequency);
        setStreak(streak);
        setUpdateStoryBy(updateBy);
        setItemAdded(itemAdded);
        setFriends(friends);
        setApprovedUsers(approvedUsers);
        setPendingUsers(pendingUsers);
        setUserAdded(userAdded);
        setReactions(allReactions);
    }

//    public List<String> getStoryUrls() {
//        List<ParseObject> images = getStory();
//        List<String> imageUrls = new ArrayList<String>();
//        for (ParseObject i : images) {
//            try {
//                if (i.fetchIfNeeded().get("video") != null) {
//                    Video mediaObject = (Video) i;
//                    imageUrls.add(mediaObject.getParseFile("image").getUrl());
//                } else {
//                    Image mediaObject = (Image) i;
//                    imageUrls.add(mediaObject.getParseFile("image").getUrl());
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        return imageUrls;
//    }

    public String getTitle() {
        try {
            return fetchIfNeeded().getString("title");
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

    public List<ParseObject> getStory() {
        try {
            return fetchIfNeeded().getList("images");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public int getProgress() {
        try {
            return fetchIfNeeded().getInt("progress");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
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

    public int getStreak() {
        try {
            return fetchIfNeeded().getInt("streak");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Date getUpdateStoryBy(){
        try {
            return fetchIfNeeded().getDate("updateBy");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean getItemAdded() {
        try {
            return fetchIfNeeded().getBoolean("itemAdded");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ParseUser> getFriends() {
        try {
            return fetchIfNeeded().getList("friends");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<ParseUser> getApprovedUsers() {
        try {
            return fetchIfNeeded().getList("approvedUsers");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<ParseUser> getPendingUsers() {
        try {
            return fetchIfNeeded().getList("pendingUsers");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Map<String, String> getUserAdded(){
        try {
            return (Map) fetchIfNeeded().get("userAdded");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Reaction> getReactions() {
        try {
            return fetchIfNeeded().getList("allReactions");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean getCompleted() {
        return getProgress() >= getDuration();
    }

    public int getIntId() {
        String id = getObjectId();
        return id.hashCode();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public void setStory(List<ParseObject> story) {
        put("images", story);
    }

    public void setProgress(int progress) {
        put("progress", progress);
    }

    public void setDuration(int duration) {
        put("duration", duration);
    }

    public void setFrequency(int frequency) {
        put("frequency", frequency);
    }

    public void setStreak(int streak) {
        put("streak", streak);
    }

    public void setUpdateStoryBy(Date updateStoryBy){
        put("updateBy", updateStoryBy);
    }

    public void setItemAdded(boolean itemAdded) { put("itemAdded", itemAdded); }

    public void setFriends(List<ParseUser> friends) {
        put("friends", friends);
    }

    public void setApprovedUsers(List<ParseUser> users) {
        put("approvedUsers", users);
    }

    public void setPendingUsers(List<ParseUser> users) {
        put("pendingUsers", users);
    }

    public void setUserAdded(Map<String, String> userAdded){ put("userAdded", userAdded); }

    public void setReactions(List<Reaction> reactions) {
        put("allReactions", reactions);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
