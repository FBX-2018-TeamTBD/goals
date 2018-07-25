package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("RemovedRequests")
public class RemovedFriends extends ParseObject {

    public RemovedFriends() {
        super();
    }

    public RemovedFriends(ParseUser removed, ParseUser remover) {
        setRemoved(removed, remover);
    }

    public void setRemoved(ParseUser removed, ParseUser remover) {
        put("removedFriend", removed);
        put("remover", remover);
    }

}
