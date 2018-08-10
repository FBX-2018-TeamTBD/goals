package com.example.cassandrakane.goalz;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ViewFlipper;

import com.example.cassandrakane.goalz.adapters.GoalAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.utils.NavigationHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.parse.Parse.getApplicationContext;

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

        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);

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
                btnRefresh.startAnimation(animation);
                //networkPopulateProfile();
                runLayoutAnimation();
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
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Goal");
        query.whereEqualTo("approvedUsers", user);
        query.orderByAscending("updateBy");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                completedGoals = 0;
                progressGoals = 0;
                goals.clear();
                List<Goal> completed = new ArrayList<>();
                if (objects != null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Goal goal = (Goal) objects.get(i);
                        if (goal.getCompleted()) {
                            completedGoals += 1;
                            completed.add(0, goal);
                        } else {
                            progressGoals += 1;
                            if (goal.getStory().size() == 0){
                                goals.add(0, goal);
                            } else {
                                goals.add(goal);
                            }
                        }
                    }
                }
                goals.addAll(completed);
                if (completedGoals == 0 && progressGoals == 0) {
                    viewFlipper.setVisibility(View.VISIBLE);
                    btnRefresh.setVisibility(View.GONE);
                } else {
                    viewFlipper.setVisibility(View.GONE);
                    btnRefresh.setVisibility(View.VISIBLE);
                }

                goalAdapter.notifyDataSetChanged();
                btnRefresh.clearAnimation();
                ParseObject.unpinAllInBackground(goals);
                ParseObject.pinAllInBackground(goals);
                mainActivity.centralFragment.progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void networkPopulateProfile(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Goal");
        query.whereEqualTo("approvedUsers", user);
        query.orderByAscending("updateBy");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                completedGoals = 0;
                progressGoals = 0;
                goals.clear();
                List<Goal> completed = new ArrayList<>();
                if (objects != null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Goal goal = (Goal) objects.get(i);
                        if (goal.getCompleted()) {
                            completedGoals += 1;
                            completed.add(0, goal);
                        } else {
                            progressGoals += 1;
                            if (goal.getStory().size() == 0){
                                goals.add(0, goal);
                            } else {
                                goals.add(goal);
                            }
                        }
                    }
                }
                goals.addAll(completed);
            if (completedGoals == 0 && progressGoals == 0) {
                viewFlipper.setVisibility(View.VISIBLE);
                btnRefresh.setVisibility(View.GONE);
            } else {
                viewFlipper.setVisibility(View.GONE);
                btnRefresh.setVisibility(View.VISIBLE);
            }

            }
        });
    }

    public void runLayoutAnimation() {
        final Context context = rvGoals.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.bottom_grid_layout);

        rvGoals.setLayoutAnimation(controller);
        goalAdapter.notifyDataSetChanged();
        btnRefresh.clearAnimation();
        ParseObject.unpinAllInBackground(goals);
        ParseObject.pinAllInBackground(goals);
        mainActivity.centralFragment.progressBar.setVisibility(View.INVISIBLE);
        rvGoals.scheduleLayoutAnimation();
    }
}
