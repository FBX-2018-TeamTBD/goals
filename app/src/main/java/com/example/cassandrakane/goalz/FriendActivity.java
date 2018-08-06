package com.example.cassandrakane.goalz;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cassandrakane.goalz.adapters.GoalAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.RemovedFriends;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendActivity extends AppCompatActivity {

    @BindView(R.id.ivProfile) public ImageView ivProfile;
    @BindView(R.id.btnBack) public Button btnBack;
    @BindView(R.id.cardview) public CardView cardView;
    @BindView(R.id.btnUnfriend) public Button btnUnfriend;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.info_layout) View relativeLayout;
    @BindView(R.id.noGoals) RelativeLayout noGoalPage;
    @BindView(R.id.btnMessage) public Button btnMessage;

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
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_friend);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        progressBar.setVisibility(ProgressBar.VISIBLE);

        tvCompleted = relativeLayout.findViewById(R.id.tvCompleted);
        tvProgress = relativeLayout.findViewById(R.id.tvProgress);
        tvFriends = relativeLayout.findViewById(R.id.tvFriends);

        user = getIntent().getParcelableExtra(ParseUser.class.getSimpleName());

        goals = new ArrayList<>();
        goalAdapter = new GoalAdapter(goals, false);
        rvGoals.setLayoutManager(new GridLayoutManager(this, 2));
        rvGoals.setAdapter(goalAdapter);

        ParseFile file = user.getParseFile("image");
        Util.setImage(file, getResources(), ivProfile, R.color.orange);
        user.unpinInBackground();

        user.pinInBackground("friends");

        btnUnfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Are you sure you want to remove this friend?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                unfriend();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FriendActivity.this, ChatActivity.class);
                startActivity(i);
            }
        });

        populateProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void populateProfile() {
        progressBar.setVisibility(View.VISIBLE);
        List<ParseObject> lGoals = user.getList("goals");
        completedGoals = 0;
        progressGoals = 0;
        goals.clear();
        List<Goal> completed = new ArrayList<>();
        if (lGoals != null) {
            for (int i = 0; i < lGoals.size(); i++) {
                Goal goal = (Goal) lGoals.get(i);
                if (goal.getCompleted()) {
                    completedGoals += 1;
                    completed.add(0, goal);
                } else {
                    progressGoals += 1;
                    goals.add(0, goal);
                }
            }
            goals.addAll(completed);
        }
        tvProgress.setText(String.valueOf(progressGoals));
        tvCompleted.setText(String.valueOf(completedGoals));
        tvFriends.setText(String.valueOf(user.getList("friends").size()));
        tvUsername.setText(user.getUsername());
        if (completedGoals == 0 && progressGoals == 0) {
            noGoalPage.setVisibility(View.VISIBLE);
        } else {
            noGoalPage.setVisibility(View.GONE);
        }
        goalAdapter.notifyDataSetChanged();
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    public void goBack(View v) {
        finish();
    }

    public void unfriend() {
        progressBar.setVisibility(View.VISIBLE);
        final ParseUser me = ParseUser.getCurrentUser();
        List<ParseUser> arr = new ArrayList<>();
        try {
            arr = me.fetch().getList("friends");
        } catch(ParseException e) {
            e.printStackTrace();
        }
        List<ParseUser> friends = new ArrayList<>();
        if (arr.size() > 0) {
            try {
                ParseUser.fetchAllIfNeeded(arr);
                for(int i = 0; i < arr.size(); i++) {
                    ParseUser friend = null;
                    try {
                        friend = arr.get(i).fetch();
                        if (!friend.getObjectId().equals(user.getObjectId())) {
                            friends.add(friend);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        me.put("friends", friends);
        ParseACL acl = me.getACL();
        if (!acl.getPublicReadAccess()) {
            acl.setPublicReadAccess(true);
            me.setACL(acl);
        }
        me.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    try {
                        me.fetch();
                        progressBar.setVisibility(View.INVISIBLE);
                        RemovedFriends remove = new RemovedFriends(user, me);
                        remove.saveInBackground();
                        finish();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.i("Profile Activity", "Failed to remove friend, with error code: " + e.toString());
                }
            }
        });
    }

}
