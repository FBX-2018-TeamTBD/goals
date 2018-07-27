package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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

public class GoalRequestsFragment extends Fragment {

    @BindView(R.id.rvGoalRequests) RecyclerView rvGoalRequests;
    @BindView(R.id.noGoalRequests) public RelativeLayout noGoalsPage;

    List<Goal> goalRequests;
    List<GoalRequests> allRequests;
    GoalRequestAdapter goalRequestAdapter;

    public GoalRequestsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_requests, container, false);
        ButterKnife.bind(this, view);

        goalRequests = new ArrayList<>();
        allRequests = new ArrayList<>();
        goalRequestAdapter = new GoalRequestAdapter(goalRequests, allRequests);
        rvGoalRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGoalRequests.setAdapter(goalRequestAdapter);

        getGoalRequests();

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
            }
        });
    }
}
