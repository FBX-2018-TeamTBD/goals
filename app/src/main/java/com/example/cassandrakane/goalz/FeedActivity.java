package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.example.cassandrakane.goalz.adapters.FriendAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedActivity extends AppCompatActivity {

    List<ParseUser> friends;
    FriendAdapter friendAdapter;
    SearchView svSearch;

    @BindView(R.id.rvFriends) RecyclerView rvFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        ButterKnife.bind(this);

        getSupportActionBar().hide();

        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(FeedActivity.this) {
            @Override
            public void onSwipeRight() {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        };

        getWindow().getDecorView().getRootView().setOnTouchListener(onSwipeTouchListener);

        friends = new ArrayList<>();
        friendAdapter = new FriendAdapter(friends);
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        rvFriends.setAdapter(friendAdapter);
        rvFriends.setOnTouchListener(onSwipeTouchListener);
        populateFriends();
    }

    public void populateFriends() {
        List<ParseUser> arr = ParseUser.getCurrentUser().getList("friends");
        Log.i("sdf", arr.toString());
        if (arr != null) {
            friends.addAll(arr);
            friendAdapter.notifyDataSetChanged();
        }
    }

    public void addFriend(View v) {

    }

}
