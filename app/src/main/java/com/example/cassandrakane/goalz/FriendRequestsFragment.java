package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.cassandrakane.goalz.adapters.FriendRequestAdapter;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendRequestsFragment extends Fragment {

    @BindView(R.id.rvFriendRequests) RecyclerView rvFriendRequests;
    @BindView(R.id.noFriendRequests) public RelativeLayout noFriendsPage;

    List<ParseUser> friendRequests;
    List<SentFriendRequests> allRequests;
    FriendRequestAdapter friendRequestAdapter;

    public FriendRequestsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        ButterKnife.bind(this, view);

        friendRequests = new ArrayList<>();
        allRequests = new ArrayList<>();
        friendRequestAdapter = new FriendRequestAdapter(friendRequests, allRequests);
        rvFriendRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFriendRequests.setAdapter(friendRequestAdapter);

        getFriendRequests();

        return view;
    }

    public void getFriendRequests() {
        ParseQuery<SentFriendRequests> query = ParseQuery.getQuery("SentFriendRequests");
        query.include("toUser");
        query.include("fromUser");
        query.whereEqualTo("toUser", ParseUser.getCurrentUser());
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<SentFriendRequests>() {
            @Override
            public void done(List<SentFriendRequests> objects, ParseException e) {
                friendRequests.clear();
                allRequests.clear();
                for (int i = 0; i < objects.size(); i++) {
                    SentFriendRequests request = objects.get(i);
                    try {
                        friendRequests.add(request.getParseUser("fromUser").fetch());
                        allRequests.add(request);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
                if (objects.size() > 0) {
                    noFriendsPage.setVisibility(View.GONE);
                } else {
                    noFriendsPage.setVisibility(View.VISIBLE);
                }
                friendRequestAdapter.notifyDataSetChanged();
            }
        });
    }
}
