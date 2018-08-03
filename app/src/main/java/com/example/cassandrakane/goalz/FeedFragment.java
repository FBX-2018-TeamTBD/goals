package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.example.cassandrakane.goalz.adapters.FriendAdapter;
import com.example.cassandrakane.goalz.adapters.StoryAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;

public class FeedFragment extends Fragment {

    MainActivity mainActivity;
    List<ParseUser> friends;
    List<ParseUser> suggestedFriends;
    FriendAdapter friendAdapter;

    StoryAdapter storyAdapter;
    List<Goal> goals;
    List<ParseUser> correspondingFriends;

    @BindView(R.id.rvFriends) RecyclerView rvFriends;
    @BindView(R.id.noFriends) RelativeLayout noFriendsPage;
    @BindView(R.id.btnAddFriend) FloatingActionButton btnAddFriend;
//    @BindView(R.id.btnAdd) Button btnAdd;
    @BindView(R.id.rvStory) RecyclerView rvStory;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    ParseUser user = ParseUser.getCurrentUser();

    public FeedFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        ButterKnife.bind(this, view);
        mainActivity = (MainActivity) getActivity();

        goals = new ArrayList<>();
        correspondingFriends = new ArrayList<>();
        storyAdapter = new StoryAdapter(goals, correspondingFriends);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvStory.setLayoutManager(layoutManager);
        rvStory.setAdapter(storyAdapter);

        friends = new ArrayList<>();
        friendAdapter = new FriendAdapter(friends, suggestedFriends);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFriends.setAdapter(friendAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), HORIZONTAL);
        rvFriends.addItemDecoration(itemDecor);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAsync();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light);


        friends = new ArrayList<>();
        friendAdapter = new FriendAdapter(friends, suggestedFriends);
            rvFriends.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvFriends.setAdapter(friendAdapter);

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.addFriend(view);
            }
        });

//        btnAdd.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                mainActivity.addFriend(view);
////            }
////        });

        populateFriends();
        populateStories();

        return view;
    }

    public void refreshAsync() {
        populateFriends();
        populateStories();
    }

    @Override
    public void onResume() {
        //do the data changes
        super.onResume();
        populateFriends();
        populateStories();
    }

    public void populateFriends() {
        List<ParseUser> arr = null;
        arr = user.getList("friends");
        friends.clear();
        if (arr != null) {
            friends.addAll(arr);
        }
        ParseObject.unpinAllInBackground(arr);
        ParseObject.pinAllInBackground(arr);

        if (friends.size() == 0) {
            noFriendsPage.setVisibility(View.VISIBLE);
        } else {
            noFriendsPage.setVisibility(View.GONE);
        }
        friendAdapter.notifyDataSetChanged();
    }

    public void populateStories() {
        goals.clear();
        for (int i = 0; i < friends.size(); i++) {
//            try {
                ParseUser friend = friends.get(i);
                List<Goal> friendGoals = friend.getList("goals");
                for (int j = 0; j < friendGoals.size(); j++) {
                    Goal goal = friendGoals.get(j);
                    if (goal.getStory().size() > 0 && !goal.getFriends().contains(ParseUser.getCurrentUser())
                            && goal.getUpdatedAt().compareTo(Util.yesterday()) >= 0 && !goals.contains(goal)) {
                        goals.add(goal);
                        correspondingFriends.add(friend);
                    }
                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            swipeContainer.setRefreshing(false);
        }
        storyAdapter.notifyDataSetChanged();

        if (goals.size() == 0) {
            rvStory.setVisibility(View.GONE);
        } else {
            rvStory.setVisibility(View.VISIBLE);
        }
    }


    public void animateStories() {
        rvStory.setVisibility(View.INVISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rvStory.setVisibility(View.VISIBLE);
                rvStory.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.menu_slide_up));
            }
        }, 100);
    }

    public void populateSuggestedFriends(){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                suggestedFriends.clear();
                if (objects != null) {
                    suggestedFriends.addAll(objects);
                }
                ParseObject.unpinAllInBackground(objects);
                ParseObject.pinAllInBackground(objects);

//                if (friends.size() == 0) {
//                    noFriendsPage.setVisibility(View.VISIBLE);
//                } else {
//                    noFriendsPage.setVisibility(View.GONE);
//                }
                friendAdapter.notifyDataSetChanged();
            }
        });
    }
}
