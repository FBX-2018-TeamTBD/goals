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

import com.example.cassandrakane.goalz.adapters.GoalRequestAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.GoalRequests;
import com.parse.FindCallback;
import com.parse.ParseException;
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
    List<Goal> goalRequests;
    List<GoalRequests> allRequests;
    GoalRequestAdapter goalRequestAdapter;
    ParseUser user;

    List<Goal> goals;
    List<Goal> incompleted;

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

        final NavigationHelper navigationHelper = new NavigationHelper(this);
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
                                navigationHelper.toCamera();
                                break;
                            case R.id.nav_goals:
                                navigationHelper.toGoals();
                                break;
                            case R.id.nav_feed:
                                navigationHelper.toFeed();
                                break;
                            case R.id.nav_friend_request:
                                navigationHelper.toFriendRequests();
                                break;
                            case R.id.nav_goal_request:
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

        goalRequests = new ArrayList<>();
        allRequests = new ArrayList<>();
        goalRequestAdapter = new GoalRequestAdapter(goalRequests, allRequests);
        rvFriendRequests.setLayoutManager(new LinearLayoutManager(this));
        rvFriendRequests.setAdapter(goalRequestAdapter);

        goals = new ArrayList<>();
        incompleted = new ArrayList<>();

        Util.setRequests(user, navigationView);
        Util.populateGoals(this, user, tvProgress, tvCompleted, tvFriends, tvUsername, ivProfile, goals, incompleted);
        getGoalRequests();
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
                        goalRequests.add(((Goal) request.getParseObject("goal").fetch()));
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

    public void openDrawer(View v) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}
