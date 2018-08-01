package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.cassandrakane.goalz.utils.HorizontalPagerAdapter;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CentralFragment extends Fragment {

    ParseUser user;

    @BindView(R.id.progressBar) public ProgressBar progressBar;
    @BindView(R.id.toolbar) public Toolbar toolbar;
    @BindView(R.id.btnAddFriend) public Button btnAddFriend;
    @BindView(R.id.btnAddGoal) public Button btnAddGoal;
    @BindView(R.id.horizontal_pager) public ViewPager horizontalPager;

    Fragment profileFragment;
    CameraFragment cameraFragment;
    FeedFragment feedFragment;

    private MainActivity mainActivity;

    public CentralFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_central, container, false);

        ButterKnife.bind(this, view);

        profileFragment = new ProfileFragment();
        cameraFragment = new CameraFragment();
        feedFragment = new FeedFragment();

        horizontalPager.setAdapter(new HorizontalPagerAdapter(getActivity().getSupportFragmentManager(), getActivity()));
        horizontalPager.setCurrentItem(1);

        mainActivity = (MainActivity) getActivity();

        user = ParseUser.getCurrentUser();

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.addFriend(view);
            }
        });

        btnAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.addGoal(view);
            }
        });

        return view;
    }

}
