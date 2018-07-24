package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("RemovedRequests")
public class RemovedFriends extends ParseObject {

    public RemovedFriends() {
        super();
    }

    public RemovedFriends(ParseUser user) {
        setRemoved(user);
    }

    public void setRemoved(ParseUser user) {
        put("removedFriend", user);
    }

}
