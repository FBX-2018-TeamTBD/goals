package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cassandrakane.goalz.adapters.FriendRequestAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utils.NavigationHelper;
import utils.Util;

public class FriendRequestsActivity extends AppCompatActivity {

    ImageView ivProfile;
    TextView tvProgress;
    TextView tvCompleted;
    public TextView tvFriends;
    TextView tvUsername;
    List<ParseUser> friendRequests;
    List<SentFriendRequests> allRequests;
    FriendRequestAdapter friendRequestAdapter;
    ParseUser user;

    List<Goal> goals;
    List<Goal> incompleted;

    @BindView(R.id.rvFriendRequests) RecyclerView rvFriendRequests;
    @BindView(R.id.toolbar) public Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.progressBar) public ProgressBar progressBar;
    @BindView(R.id.nav_view) public NavigationView navigationView;
    @BindView(R.id.noFriendRequests) public RelativeLayout noFriendsPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        progressBar.setVisibility(ProgressBar.VISIBLE);
        setSupportActionBar(toolbar);

        final NavigationHelper navigationHelper = new NavigationHelper(this);
        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(3).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.nav_camera:
                                navigationHelper.toCamera();
                                break;
                            case R.id.nav_goals:
                                navigationHelper.toGoals();
                                break;
                            case R.id.nav_feed:
                                navigationHelper.toFeed();
                                break;
                            case R.id.nav_friend_request:
                                break;
                            case R.id.nav_goal_request:
                                navigationHelper.toGoalRequests();
                                break;
                            case R.id.nav_logout:
                                navigationHelper.logout();
                                break;
                        }

                        return true;
                    }
                });

        ivProfile = navigationView.getHeaderView(0).findViewById(R.id.ivProfile);
        tvUsername = navigationView.getHeaderView(0).findViewById(R.id.tvUsername);
        tvFriends = navigationView.getHeaderView(0).findViewById(R.id.info_layout).findViewById(R.id.tvFriends);
        tvProgress = navigationView.getHeaderView(0).findViewById(R.id.info_layout).findViewById(R.id.tvProgress);
        tvCompleted = navigationView.getHeaderView(0).findViewById(R.id.info_layout).findViewById(R.id.tvCompleted);

        user = ParseUser.getCurrentUser();

        friendRequests = new ArrayList<>();
        allRequests = new ArrayList<>();
        goals = new ArrayList<>();
        incompleted = new ArrayList<>();
        friendRequestAdapter = new FriendRequestAdapter(friendRequests, allRequests);
        rvFriendRequests.setLayoutManager(new LinearLayoutManager(this));
        rvFriendRequests.setAdapter(friendRequestAdapter);

        Util.populateGoals(this, user, tvProgress, tvCompleted, tvFriends, tvUsername, ivProfile, goals, incompleted);
        getFriendRequests();
    }

    public void getFriendRequests() {
        progressBar.setVisibility(View.VISIBLE);
        ParseQuery<SentFriendRequests> query = ParseQuery.getQuery("SentFriendRequests");
        query.include("toUser");
        query.include("fromUser");
        query.whereEqualTo("toUser", user);
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
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

    public void openDrawer(View v) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}
