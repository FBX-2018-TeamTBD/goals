package com.example.cassandrakane.goalz;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
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
import com.parse.ParseFile;
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
import utils.Util;

public class FriendActivity extends AppCompatActivity {

    @BindView(R.id.ivProfile) public ImageView ivProfile;
    @BindView(R.id.btnBack) public Button btnBack;
    @BindView(R.id.cardview) public CardView cardView;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.info_layout) View relativeLayout;
    @BindView(R.id.noGoals) RelativeLayout noGoalPage;
    @BindView(R.id.btnUnfriend) Button btnUnfriend;

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
        goalAdapter = new GoalAdapter(goals, false);
        rvGoals.setLayoutManager(new LinearLayoutManager(this));
        rvGoals.setAdapter(goalAdapter);

        ParseFile file = (ParseFile) user.get("image");
        Util.setImage(user, file, getResources(), ivProfile, 16.0f);
//        Util.setImage(user, "image", getResources(), ivProfile, 16.0f);
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

        ParseFile pfile = (ParseFile) user.get("image");
        Util.setImage(user, pfile, getResources(), ivProfile, 16.0f);
        populateProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
//        populateProfile();
    }

//    public void populateProfile(){
//        ParseQuery<ParseObject> localQuery = ParseQuery.getQuery("Goal");
//        localQuery.fromPin("friendGoals");
//        localQuery.whereEqualTo("user", user);
//        localQuery.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> objects, ParseException e) {
//                if (e == null){
//                    goals.clear();
//                    for (int i=0; i <objects.size(); i++){
//                        Goal goal = (Goal) objects.get(i);
//
//                        if (goal.getCompleted()) {
//                            completedGoals += 1;
//                            goals.add(goal);
//                        } else {
//                            progressGoals += 1;
//                            goals.add(0, goal);
//                        }
//                    }
//                    tvProgress.setText(String.valueOf(progressGoals));
//                    tvCompleted.setText(String.valueOf(completedGoals));
//                    tvUsername.setText(user.getUsername());
//                    goalAdapter.notifyDataSetChanged();
//                }
//            }
//        });
//        ParseQuery<ParseUser> localUserQuery = ParseUser.getQuery();
//        localUserQuery.fromPin("friends");
//        localQuery.whereNotEqualTo("objectId", user.getObjectId());
//        localUserQuery.findInBackground(new FindCallback<ParseUser>() {
//            @Override
//            public void done(List<ParseUser> objects, ParseException e) {
//                tvFriends.setText(String.valueOf(objects.size() - 1));
//                progressBar.setVisibility(ProgressBar.INVISIBLE);
//            }
//        });
//    }
    public void populateProfile() {
        List<ParseObject> arr = new ArrayList<>();
//        try {
        arr = user.getList("goals");
//        } catch(ParseException e) {
//            e.printStackTrace();
//        }
        completedGoals = 0;
        progressGoals = 0;
        goals.clear();
        if (arr != null) {
//            try {
//                ParseObject.fetchAllIfNeeded(arr);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            for(int i = 0; i < arr.size(); i++) {
                Goal goal = null;
//                try {
                goal = (Goal) arr.get(i);
//                } catch(ParseException e) {
//                    e.printStackTrace();
//                }
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
            tvUsername.setText(user.getUsername());
        }
        goalAdapter.notifyDataSetChanged();
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        if (completedGoals == 0 && progressGoals == 0) {
            noGoalPage.setVisibility(View.VISIBLE);
        } else {
            noGoalPage.setVisibility(View.GONE);
        }
    }

    public void goBack(View v) {
        finish();
    }

    public void unfriend() {
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
