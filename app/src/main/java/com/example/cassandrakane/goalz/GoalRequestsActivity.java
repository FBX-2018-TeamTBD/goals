package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.example.cassandrakane.goalz.adapters.FriendRequestAdapter;
import com.example.cassandrakane.goalz.adapters.GoalRequestAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.GoalRequests;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.example.cassandrakane.goalz.models.SharedGoal;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Util;

public class GoalRequestsActivity extends AppCompatActivity {

    ImageView ivProfile;
    public TextView tvProgress;
    TextView tvCompleted;
    public TextView tvFriends;
    TextView tvUsername;
    List<SharedGoal> goalRequests;
    List<GoalRequests> allRequests;
    GoalRequestAdapter goalRequestAdapter;
    ParseUser user;

    @BindView(R.id.rvGoalRequests) RecyclerView rvFriendRequests;
    @BindView(R.id.toolbar) public Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.nav_view) public NavigationView navigationView;
    @BindView(R.id.noGoalRequests) public RelativeLayout noGoalsPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_requests);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        progressBar.setVisibility(ProgressBar.VISIBLE);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(4).setChecked(true);
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
                            case R.id.nav_goals:
                                toGoals();
                                break;
                            case R.id.nav_feed:
                                toFeed();
                                break;
                            case R.id.nav_friend_request:
                                toFriendRequests();
                                break;
                            case R.id.nav_goal_request:
                                break;
                            case R.id.nav_logout:
                                logout();
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

        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(GoalRequestsActivity.this) {
            @Override
            public void onSwipeRight() {
                toFriendRequests();
            }
        };

        getWindow().getDecorView().getRootView().setOnTouchListener(onSwipeTouchListener);

        user = ParseUser.getCurrentUser();

        goalRequests = new ArrayList<>();
        allRequests = new ArrayList<>();
        goalRequestAdapter = new GoalRequestAdapter(goalRequests, allRequests);
        rvFriendRequests.setLayoutManager(new LinearLayoutManager(this));
        rvFriendRequests.setAdapter(goalRequestAdapter);
        rvFriendRequests.setOnTouchListener(onSwipeTouchListener);

        ParseQuery<SentFriendRequests> query2 = ParseQuery.getQuery("SentFriendRequests");
        query2.whereEqualTo("toUser", user);
        try {
            int count = query2.count();
            if(count > 0) {
                navigationView.getMenu().getItem(3).setTitle("friend requests (" + count + ")");
            } else {
                navigationView.getMenu().getItem(3).setTitle("friend requests");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<SentFriendRequests> query3 = ParseQuery.getQuery("GoalRequests");
        query2.whereEqualTo("user", user);
        try {
            int count = query3.count();
            if(count > 0) {
                navigationView.getMenu().getItem(4).setTitle("goal requests (" + count + ")");
            } else {
                navigationView.getMenu().getItem(4).setTitle("goal requests");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        populateGoals();
        getGoalRequests();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateGoals();
        getGoalRequests();
    }

    public void populateGoals() {
        List<ParseObject> arr = user.getList("goals");
        int completedGoals = 0;
        int progressGoals = 0;
        if (arr != null) {
            try {
                ParseObject.fetchAllIfNeeded(arr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for(int i = 0; i < arr.size(); i++) {
                Goal goal = (Goal) arr.get(i);
                if (goal.getCompleted()) {
                    completedGoals += 1;
                } else {
                    progressGoals += 1;
                }
            }
            tvProgress.setText(String.valueOf(progressGoals));
            tvCompleted.setText(String.valueOf(completedGoals));
            tvFriends.setText(String.valueOf(user.getList("friends").size()));
            tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        }
        ParseFile pfile = (ParseFile) user.get("image");
        Util.setImage(user, pfile, getResources(), ivProfile, 16.0f);
    }

    public void getGoalRequests() {
        ParseQuery<GoalRequests> query = ParseQuery.getQuery("GoalRequests");
        query.include("goal");
        query.whereEqualTo("user", user);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<GoalRequests>() {
            @Override
            public void done(List<GoalRequests> objects, ParseException e) {
                goalRequests.clear();
                allRequests.clear();
                for (int i = 0; i < objects.size(); i++) {
                    GoalRequests request = objects.get(i);
                    try {
                        goalRequests.add(((SharedGoal) request.getParseObject("goal").fetch()));
                        allRequests.add(request);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
                if (objects.size() > 0) {
                    noGoalsPage.setVisibility(View.GONE);
                } else {
                    noGoalsPage.setVisibility(View.VISIBLE);
                }
                goalRequestAdapter.notifyDataSetChanged();
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

    public void toCamera() {
        Intent i = new Intent(getApplicationContext(), CameraActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void toGoals() {
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void toFeed() {
        Intent i = new Intent(getApplicationContext(), FeedActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void toFriendRequests() {
        Intent i = new Intent(getApplicationContext(), FriendRequestsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void logout() {
        ParseUser.logOut();
        Toast.makeText(this, "Successfully logged out.", Toast.LENGTH_LONG);
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_top, R.anim.slide_to_bottom);
        finish();
    }

    public void openDrawer(View v) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}
