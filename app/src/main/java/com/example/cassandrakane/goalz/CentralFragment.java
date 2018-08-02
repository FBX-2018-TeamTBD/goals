package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.cassandrakane.goalz.utils.HorizontalPagerAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CentralFragment extends Fragment {

    ParseUser user;

    @BindView(R.id.progressBar) public ProgressBar progressBar;
    @BindView(R.id.horizontal_pager) public ViewPager horizontalPager;

    Fragment profileFragment;
    CameraFragment cameraFragment;
    FeedFragment feedFragment;

    public List<Fragment> pages;

    private MainActivity mainActivity;

    public CentralFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pages = new ArrayList<>();
        profileFragment = new ProfileFragment();
        cameraFragment = new CameraFragment();
        feedFragment = new FeedFragment();

        pages.add(cameraFragment);
        pages.add(profileFragment);
        pages.add(feedFragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_central, container, false);

        ButterKnife.bind(this, view);

        horizontalPager.setAdapter(new HorizontalPagerAdapter(getActivity().getSupportFragmentManager(), getActivity(), pages));
        horizontalPager.setCurrentItem(1);

        mainActivity = (MainActivity) getActivity();

        user = ParseUser.getCurrentUser();

        return view;
    }

}
