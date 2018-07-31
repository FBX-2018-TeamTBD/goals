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

import com.example.cassandrakane.goalz.adapters.MainPagerAdapter;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CentralFragment extends Fragment {

    ParseUser user;

    @BindView(R.id.progressBar) public ProgressBar progressBar;
    @BindView(R.id.pager) public ViewPager viewPager;
    @BindView(R.id.toolbar) public Toolbar toolbar;
    @BindView(R.id.btnAddFriend) public Button btnAddFriend;
    @BindView(R.id.btnAddGoal) public Button btnAddGoal;

    public MainPagerAdapter pagerAdapter;
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

        pagerAdapter = new MainPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);

        mainActivity = (MainActivity) getActivity();

        user = ParseUser.getCurrentUser();
        Util.setNotifications(user);

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
