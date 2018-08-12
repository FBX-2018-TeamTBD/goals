package com.example.cassandrakane.goalz.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("RemovedRequests")
public class RemovedFriends extends ParseObject {

    public RemovedFriends() {
        super();
    }

    public RemovedFriends(ParseUser removedFriend, ParseUser remover) {
        setRemovedFriend(removedFriend);
        setRemover(remover);
    }

    public ParseUser getRemovedFriend() {
        try {
            return fetchIfNeeded().getParseUser("removedFriend");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ParseUser getRemover() {
        try {
            return fetchIfNeeded().getParseUser("remover");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setRemovedFriend(ParseUser removedFriend) {
        put("removedFriend", removedFriend);
    }

    public void setRemover(ParseUser remover) {
        put("remover", remover);
    }

}
