package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.adapters.FriendAdapter;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import com.parse.ParseException;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.HORIZONTAL;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedActivity extends AppCompatActivity {

    public final static int ADD_FRIEND_ACTIVITY_REQUEST_CODE = 14;

    List<ParseUser> friends;
    FriendAdapter friendAdapter;

    ImageView ivProfile;
    TextView tvProgress;
    TextView tvCompleted;
    TextView tvFriends;
    TextView tvUsername;

    @BindView(R.id.rvFriends) RecyclerView rvFriends;
    @BindView(R.id.toolbar) public Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ButterKnife.bind(this);

        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(FeedActivity.this) {
            @Override
            public void onSwipeRight() {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        };

        drawerLayout = findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.menu));

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_camera:
                                toCamera();
                                break;
                            case R.id.nav_feed:
                                toFeed();
                                break;
                            case R.id.nav_logout:
                                logout();
                        }

                        return true;
                    }
                });

        ivProfile = navigationView.getHeaderView(0).findViewById(R.id.ivProfile);
        tvUsername = navigationView.getHeaderView(0).findViewById(R.id.tvUsername);
        tvFriends = navigationView.getHeaderView(0).findViewById(R.id.tvFriends);
        tvProgress = navigationView.getHeaderView(0).findViewById(R.id.tvProgress);
        tvCompleted = navigationView.getHeaderView(0).findViewById(R.id.tvCompleted);

        getWindow().getDecorView().getRootView().setOnTouchListener(onSwipeTouchListener);

        friends = new ArrayList<>();
        friendAdapter = new FriendAdapter(friends);
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        rvFriends.setAdapter(friendAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, HORIZONTAL);
        rvFriends.addItemDecoration(itemDecor);
        rvFriends.setOnTouchListener(onSwipeTouchListener);
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

    public void openDrawer(View v) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void toCamera() {
        Intent i = new Intent(getApplicationContext(), CameraActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void toFeed() {
        Intent i = new Intent(getApplicationContext(), FeedActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void logout() {
        ParseUser.logOut();
        Toast.makeText(this, "Successfully logged out.", Toast.LENGTH_LONG);
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_FRIEND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ParseUser friend = data.getParcelableExtra(ParseUser.class.getSimpleName());
                friends.add(0, friend);
                friendAdapter.notifyItemInserted(0);
                rvFriends.scrollToPosition(0);
            }
        }
    }

}
