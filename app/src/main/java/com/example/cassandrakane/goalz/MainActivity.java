package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.example.cassandrakane.goalz.adapters.MainPagerAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public MainPagerAdapter pagerAdapter;
    List<Goal> goals;
    List<Goal> completed;
    List<Goal> incompleted;
    ParseUser user;

    @BindView(R.id.progressBar) public ProgressBar progressBar;
    @BindView(R.id.pager) public ViewPager viewPager;
    @BindView(R.id.toolbar) public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);

        goals = new ArrayList<>();
        completed = new ArrayList<>();
        incompleted = new ArrayList<>();

        user = ParseUser.getCurrentUser();
    }

    public void refreshAsync(SwipeRefreshLayout swipeContainer) {
        Util.populateGoalsAsync(user, goals, completed, swipeContainer);
    }

    public void addGoal(View v) {
        Intent i = new Intent(this, AddGoalActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
    }

    public void addFriend(View v) {
        Intent i = new Intent(this, SearchFriendsActivity.class);
        i.putExtra("requestActivity", this.getClass().getSimpleName());
        startActivity(i);
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slide_to_top);
    }
}
