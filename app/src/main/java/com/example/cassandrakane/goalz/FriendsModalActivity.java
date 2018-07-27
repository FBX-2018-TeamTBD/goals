package com.example.cassandrakane.goalz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cassandrakane.goalz.adapters.GoalFriendAdapter;
import com.example.cassandrakane.goalz.models.AddGoalForm;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.GoalRequests;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.DividerItemDecoration.HORIZONTAL;

public class FriendsModalActivity extends Activity {

    @BindView(R.id.rvGoalFriends) RecyclerView rvGoalFriends;
    @BindView(R.id.btnAdd) Button btnAdd;
    GoalFriendAdapter goalFriendAdapter;
    List<ParseUser> goalFriends;
    Goal goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.modal_friend);

        ButterKnife.bind(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = (int)(dm.widthPixels * 0.75);
        int height = (int)(dm.heightPixels * 0.65);

        getWindow().setLayout(width, height);

        goal = getIntent().getParcelableExtra(Goal.class.getSimpleName());
        goalFriends = goal.getApprovedUsers();
        goalFriends.remove(ParseUser.getCurrentUser());
        goalFriendAdapter = new GoalFriendAdapter(goalFriends);
        rvGoalFriends.setLayoutManager(new LinearLayoutManager(this));
        rvGoalFriends.setAdapter(goalFriendAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, HORIZONTAL);
        rvGoalFriends.addItemDecoration(itemDecor);
    }

    public void goBack(View v) {
        finish();
    }

    public void addFriendToGoal(View v) {
        Intent i = new Intent(getApplicationContext(), SearchFriendsActivity.class);
        i.putExtra("requestActivity", FriendsModalActivity.class.getSimpleName());
        i.putExtra(Goal.class.getSimpleName(), goal);
        startActivity(i);
        finish();
    }

}
