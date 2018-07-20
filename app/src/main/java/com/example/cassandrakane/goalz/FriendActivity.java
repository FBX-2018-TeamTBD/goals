package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.adapters.GoalAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Util;

public class FriendActivity extends AppCompatActivity {

    @BindView(R.id.ivProfile) ImageView ivProfile;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.info_layout) View relativeLayout;

    TextView tvProgress;
    TextView tvCompleted;
    TextView tvFriends;

    @BindView(R.id.rvGoals) RecyclerView rvGoals;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private ParseUser user;

    private List<Goal> goals;
    private GoalAdapter goalAdapter;

    private int completedGoals = 0;
    private int progressGoals = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        progressBar.setVisibility(ProgressBar.VISIBLE);

        tvCompleted = relativeLayout.findViewById(R.id.tvCompleted);
        tvProgress = relativeLayout.findViewById(R.id.tvProgress);
        tvFriends = relativeLayout.findViewById(R.id.tvFriends);

        user = getIntent().getParcelableExtra(ParseUser.class.getSimpleName());

        goals = new ArrayList<>();
        goalAdapter = new GoalAdapter(goals);
        rvGoals.setLayoutManager(new LinearLayoutManager(this));
        rvGoals.setAdapter(goalAdapter);

        Util.setImage(user, "image", getResources(), ivProfile, 16.0f);
        populateProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateProfile();
    }

    public void populateProfile() {
        List<ParseObject> arr = new ArrayList<>();
        try {
            arr = user.fetch().getList("goals");
        } catch(ParseException e) {
            e.printStackTrace();
        }
        completedGoals = 0;
        progressGoals = 0;
        goals.clear();
        if (arr != null) {
            try {
                ParseObject.fetchAllIfNeeded(arr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for(int i = 0; i < arr.size(); i++) {
                Goal goal = null;
                try {
                    goal = arr.get(i).fetch();
                } catch(ParseException e) {
                    e.printStackTrace();
                }
                goals.add(0, goal);
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
        goalAdapter.notifyDataSetChanged();
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    public void goBack(View v) {
        finish();
    }

}
