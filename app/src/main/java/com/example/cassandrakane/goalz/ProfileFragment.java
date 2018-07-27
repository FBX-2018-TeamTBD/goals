package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.cassandrakane.goalz.adapters.GoalAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @BindView(R.id.rvGoals) RecyclerView rvGoals;
    @BindView(R.id.noGoals) RelativeLayout noGoalPage;
    @BindView(R.id.btnAdd) Button btnAdd;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    MainActivity mainActivity;
    private ParseUser user = ParseUser.getCurrentUser();

    private List<Goal> goals;
    private GoalAdapter goalAdapter;

    public int completedGoals = 0;
    public int progressGoals = 0;

    public ProfileFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        mainActivity = (MainActivity) getActivity();
        goals = new ArrayList<>();
        goalAdapter = new GoalAdapter(goals, true);
        rvGoals.setLayoutManager(new LinearLayoutManager(getContext()));
        rvGoals.setAdapter(goalAdapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainActivity.refreshAsync(swipeContainer);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.addGoal(view);
            }
        });

        populateProfile();

        return view;
    }

    public void populateProfile() {
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
        if (completedGoals == 0 && progressGoals == 0) {
            noGoalPage.setVisibility(View.VISIBLE);
        } else {
            noGoalPage.setVisibility(View.GONE);
        }
        goalAdapter.notifyDataSetChanged();
    }

}
