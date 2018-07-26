package com.example.cassandrakane.goalz.models;

import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class AddGoalForm {

    String title;
    String description;
    String duration;
    int frequency;
    List<ParseUser> selectedFriends;

    public AddGoalForm() {}

    public AddGoalForm(String t, String d, String dur, int f, List<ParseUser> sFriends) {
        title = t;
        description = d;
        duration = dur;
        frequency = f;
        selectedFriends = sFriends;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDuration() {
        return duration;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<ParseUser> getSelectedFriends() {
        return selectedFriends;
    }

    public void setSelectedFriends(List<ParseUser> selectedFriends) {
        this.selectedFriends = selectedFriends;
    }
}
