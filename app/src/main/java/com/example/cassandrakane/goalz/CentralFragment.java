package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.cassandrakane.goalz.adapters.HorizontalPagerAdapter;
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

    public int pageNum = 1;

    public List<Fragment> pages;
    private HorizontalPagerAdapter horizontalPagerAdapter;

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

        horizontalPagerAdapter = new HorizontalPagerAdapter(getActivity().getSupportFragmentManager(), getActivity(), pages);
        horizontalPager.setAdapter(horizontalPagerAdapter);
        horizontalPager.setCurrentItem(1);

//        horizontalPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i1) {
//
//            }
//
//            @Override
//            public void onPageSelected(int i) {
//                Fragment fragment = ((HorizontalPagerAdapter)horizontalPager.getAdapter()).getFragment(i);
//
//                if (i ==1 && fragment != null)
//                {
//                    fragment.onResume();
//                } else if (i == 2 && fragment != null){
//                    fragment.onResume();
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//
//            }
//        });

        mainActivity = (MainActivity) getActivity();

        user = ParseUser.getCurrentUser();

        return view;
    }

}
