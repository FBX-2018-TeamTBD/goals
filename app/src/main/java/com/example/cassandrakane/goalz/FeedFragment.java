package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.cassandrakane.goalz.adapters.FriendAdapter;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.DividerItemDecoration.HORIZONTAL;

public class FeedFragment extends Fragment {

    MainActivity mainActivity;
    List<ParseUser> friends;
    FriendAdapter friendAdapter;

    @BindView(R.id.rvFriends) RecyclerView rvFriends;
    @BindView(R.id.noFriends) RelativeLayout noFriendsPage;
    @BindView(R.id.btnAdd) Button btnAdd;

    ParseUser user = ParseUser.getCurrentUser();

    public FeedFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        ButterKnife.bind(this, view);

        mainActivity = (MainActivity) getActivity();
        friends = new ArrayList<>();
        friendAdapter = new FriendAdapter(friends);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFriends.setAdapter(friendAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), HORIZONTAL);
        rvFriends.addItemDecoration(itemDecor);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.addFriend(view);
            }
        });

        populateFriends();

        return view;
    }

    public void populateFriends() {
        List<ParseUser> arr = null;
        arr = user.getList("friends");
        friends.clear();
        if (arr != null) {
            friends.addAll(arr);
        }
        ParseObject.unpinAllInBackground(arr);
        ParseObject.pinAllInBackground(arr);

        if (friends.size() == 0) {
            noFriendsPage.setVisibility(View.VISIBLE);
        } else {
            noFriendsPage.setVisibility(View.GONE);
        }
        friendAdapter.notifyDataSetChanged();
    }

}
