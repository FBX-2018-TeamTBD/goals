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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    List<ParseUser> friends;
    RecyclerView rvFriends;
    FriendAdapter friendAdapter;
    SearchView svSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        getSupportActionBar().hide();

        svSearch = findViewById(R.id.searchView);
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                ParseQuery<ParseUser> query = ParseQuery.getQuery("User");
                query.whereStartsWith("username", s);
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        // add users to list of users and update adapter
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

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
        rvFriends = findViewById(R.id.rvFriends);
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        rvFriends.setAdapter(friendAdapter);
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

    public void toSearch(View v) {

    }
}
