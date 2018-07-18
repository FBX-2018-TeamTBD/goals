package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.cassandrakane.goalz.adapters.FriendAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import com.parse.ParseException;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.HORIZONTAL;

public class FeedActivity extends AppCompatActivity {

    public final static int ADD_FRIEND_ACTIVITY_REQUEST_CODE = 14;

    List<ParseUser> friends;
    RecyclerView rvFriends;
    FriendAdapter friendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

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
        rvFriends = findViewById(R.id.rvFriends);
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        rvFriends.setAdapter(friendAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, HORIZONTAL);
        rvFriends.addItemDecoration(itemDecor);
        populateFriends();
    }

    public void populateFriends() {
        List<ParseUser> arr = ParseUser.getCurrentUser().getList("friends");
        Log.i("sdf", arr.toString());
        if (arr != null) {
            try {
                ParseUser.fetchAllIfNeeded(arr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            friends.addAll(arr);
            friendAdapter.notifyDataSetChanged();
        }
    }

    public void addFriend(View v) {
        Intent i = new Intent(this, SearchFriendsActivity.class);
        startActivityForResult(i, ADD_FRIEND_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_FRIEND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ParseUser friend = data.getParcelableExtra(ParseUser.class.getSimpleName());
                friends.add(0, friend);
                friendAdapter.notifyItemInserted(0);
                rvFriends.scrollToPosition(0);
                ProfileActivity.numFriends += 1;
                ProfileActivity.setFriendsCount();
            }
        }
    }

}
