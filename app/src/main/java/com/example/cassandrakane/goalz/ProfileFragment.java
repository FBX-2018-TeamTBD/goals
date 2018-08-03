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
import android.widget.ImageButton;
import android.widget.ViewFlipper;

import com.example.cassandrakane.goalz.adapters.GoalAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.utils.NavigationHelper;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

public class ProfileFragment extends Fragment {

    @BindView(R.id.rvGoals) public RecyclerView rvGoals;
    @BindView(R.id.viewFlipper) ViewFlipper viewFlipper;
    @BindView(R.id.btnAddGoal) public FloatingActionButton btnAddGoal;
    @BindView(R.id.btnCamera) ImageButton btnCamera;
    @BindView(R.id.btnFeed) ImageButton btnFeed;
    @BindView(R.id.btnRefresh) ImageButton btnRefresh;

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
        final NavigationHelper navigationHelper = new NavigationHelper(mainActivity.centralFragment.horizontalPager);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationHelper.toCamera();
            }
        });
        btnFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationHelper.toFeed();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                networkPopulateProfile();
            }
        });

        return view;
    }
    @Override
    public void onResume() {
        //do the data changes
        super.onResume();
        populateProfile();
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
            viewFlipper.setVisibility(VISIBLE);
        } else {
            viewFlipper.setVisibility(View.GONE);
        }
        goalAdapter.notifyDataSetChanged();
    }

    public void networkPopulateProfile(){
        mainActivity.centralFragment.progressBar.setVisibility(VISIBLE);
        List<ParseObject> arr = new ArrayList<>();
        goals.clear();
        try {
            arr = user.fetch().getList("goals");
        } catch(ParseException e) {
            e.printStackTrace();
        }

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
                if (goal.getCompleted()) {
                    goals.add(goal);
                } else {
                    goals.add(0, goal);
                }
            }
        }
        ParseObject.unpinAllInBackground(goals);
        ParseObject.pinAllInBackground(goals);
        mainActivity.centralFragment.progressBar.setVisibility(View.INVISIBLE);
    }
}
