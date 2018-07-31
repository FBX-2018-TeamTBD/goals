package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cassandrakane.goalz.adapters.NotificationAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.GoalRequests;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.example.cassandrakane.goalz.utils.NavigationHelper;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsFragment extends Fragment {

    MainActivity mainActivity;

    NotificationAdapter notificationAdapter;
    List<Goal> goalRequests;
    List<GoalRequests> allGoalRequests;
    List<ParseUser> friendRequests;
    List<SentFriendRequests> allFriendRequests;

    List<Goal> goals;
    List<Goal> completed;
    List<Goal> incompleted;


    @BindView(R.id.tvProgress) TextView tvProgress;
    @BindView(R.id.tvCompleted) TextView tvCompleted;
    @BindView(R.id.tvFriends) TextView tvFriends;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.ivProfile) ImageView ivProfile;
    @BindView(R.id.btnLogout) Button btnLogout;
    @BindView(R.id.rvNotifications) RecyclerView rvNotifications;

    public NotificationsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, view);

        ParseUser user = ParseUser.getCurrentUser();
        mainActivity = (MainActivity) getActivity();
        goals = new ArrayList<>();
        completed = new ArrayList<>();
        incompleted = new ArrayList<>();
        Util.populateGoals(getContext(), user, tvProgress, tvCompleted, tvFriends, tvUsername, ivProfile, goals, incompleted);
        Util.setImage(user, (ParseFile) user.get("image"), getResources(), ivProfile, 16.0f);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationHelper navigationHelper = new NavigationHelper(mainActivity.centralFragment.viewPager);
                navigationHelper.logout(mainActivity);
            }
        });

        goalRequests = new ArrayList<>();
        allGoalRequests = new ArrayList<>();
        friendRequests = new ArrayList<>();
        allFriendRequests = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(goalRequests, allGoalRequests, friendRequests, allFriendRequests);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNotifications.setAdapter(notificationAdapter);

        getGoalRequests();
        getFriendRequests();

        return view;
    }

    public void getGoalRequests() {
        ParseQuery<GoalRequests> query = ParseQuery.getQuery("GoalRequests");
        query.include("goal");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<GoalRequests>() {
            @Override
            public void done(List<GoalRequests> objects, ParseException e) {
                goalRequests.clear();
                allGoalRequests.clear();
                if (objects != null) {
                    for (int i = 0; i < objects.size(); i++) {
                        GoalRequests request = objects.get(i);
                        try {
                            goalRequests.add(((Goal) request.getParseObject("goal").fetch()));
                            allGoalRequests.add(request);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                notificationAdapter.notifyDataSetChanged();
            }
        });
    }

    public void getFriendRequests() {
        ParseQuery<SentFriendRequests> query = ParseQuery.getQuery("SentFriendRequests");
        query.include("toUser");
        query.include("fromUser");
        query.whereEqualTo("toUser", ParseUser.getCurrentUser());
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<SentFriendRequests>() {
            @Override
            public void done(List<SentFriendRequests> objects, ParseException e) {
                friendRequests.clear();
                allFriendRequests.clear();
                if (objects != null) {
                    for (int i = 0; i < objects.size(); i++) {
                        SentFriendRequests request = objects.get(i);
                        try {
                            friendRequests.add(request.getParseUser("fromUser").fetch());
                            allFriendRequests.add(request);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                notificationAdapter.notifyDataSetChanged();
            }
        });
    }
}
