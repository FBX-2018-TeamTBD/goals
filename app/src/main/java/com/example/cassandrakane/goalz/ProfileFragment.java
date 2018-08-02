package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.example.cassandrakane.goalz.adapters.GoalAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @BindView(R.id.rvGoals) public RecyclerView rvGoals;
    @BindView(R.id.viewFlipper) ViewFlipper viewFlipper;
    @BindView(R.id.btnAddGoal) public FloatingActionButton btnAddGoal;


    MainActivity mainActivity;
    private ParseUser user = ParseUser.getCurrentUser();

    private List<Goal> goals;
    private GoalAdapter goalAdapter;

    public int completedGoals = 0;
    public int progressGoals = 0;

    public ProfileFragment() {
    }

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
        rvGoals.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvGoals.setAdapter(goalAdapter);

        populateProfile();

        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(5000);
        viewFlipper.startFlipping();

        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.in_from_right));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.out_from_left));

        btnAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.addGoal(view);
            }
        });

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
                    goals.add(goal);
                }
            }
            goals.addAll(completed);
        }
        if (completedGoals == 0 && progressGoals == 0) {
            viewFlipper.setVisibility(View.VISIBLE);
        } else {
            viewFlipper.setVisibility(View.GONE);
        }
        goalAdapter.notifyDataSetChanged();
    }

}
