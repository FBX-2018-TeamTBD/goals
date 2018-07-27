package com.example.cassandrakane.goalz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.cassandrakane.goalz.adapters.GoalAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

import butterknife.BindView;

public class ProfileFragment extends Fragment {

    @BindView(R.id.rvGoals) RecyclerView rvGoals;
    @BindView(R.id.noGoals) RelativeLayout noGoalPage;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    MainActivity mainActivity = (MainActivity) getActivity();
    private ParseUser user = ParseUser.getCurrentUser();

    private List<Goal> goals;
    private GoalAdapter goalAdapter;

    private ParseFile imageFile;
    private String photoFileName;
    private File photoFile;

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
//        ButterKnife.bind(this, view);
//
//        goals = new ArrayList<>();
//        goalAdapter = new GoalAdapter(goals, true);
//        rvGoals.setLayoutManager(new LinearLayoutManager(getContext()));
//        rvGoals.setAdapter(goalAdapter);
//
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mainActivity.refreshAsync(swipeContainer);
//            }
//        });
//        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_light,
//                android.R.color.holo_green_light,
//                android.R.color.holo_blue_light,
//                android.R.color.holo_red_light);
//
//        updateFriends();

        return view;
    }
//
//    public void updateFriends() {
//        ParseQuery<ApprovedFriendRequests> query = ParseQuery.getQuery("ApprovedFriendRequests");
//        query.include("toUser");
//        query.include("fromUser");
//        query.whereEqualTo("fromUser", user);
//
//        Util.setNotifications(user, mainActivity.navigationView);
//
//        final List<ParseUser> friends = user.getList("friends");
//        final List<ParseUser> newFriends = new ArrayList<>();
//        query.findInBackground(new FindCallback<ApprovedFriendRequests>() {
//            @Override
//            public void done(List<ApprovedFriendRequests> objects, ParseException e) {
//                newFriends.clear();
//                if (objects != null) {
//                    for (int i = 0; i < objects.size(); i++) {
//                        ApprovedFriendRequests request = objects.get(i);
//                        try {
//                            deleteApprovedRequest(request.getObjectId());
//                            ParseUser user = request.getParseUser("toUser").fetch();
//                            newFriends.add(user);
//                        } catch (ParseException e1) {
//                            e1.printStackTrace();
//                        }
//                    }
//                }
//                ParseQuery<RemovedFriends> query3 = ParseQuery.getQuery("RemovedRequests");
//                query3.include("removedFriend");
//                query3.include("remover");
//                query3.whereEqualTo("removedFriend", user);
//                final List<String> removedFriends = new ArrayList<>();
//                query3.findInBackground(new FindCallback<RemovedFriends>() {
//                    @Override
//                    public void done(List<RemovedFriends> objects, ParseException e) {
//                        if (objects != null) {
//                            for (int i = 0; i < objects.size(); i++) {
//                                RemovedFriends request = objects.get(i);
//                                deleteRemoveRequest(request.getObjectId());
//                                removedFriends.add(request.getParseUser("remover").getUsername());
//                            }
//                        }
//                        friends.addAll(newFriends);
//                        for (int i = friends.size() - 1; i >= 0; i--) {
//                            try {
//                                if (removedFriends.contains(friends.get(i).fetch().getUsername())) {
//                                    friends.remove(i);
//                                }
//                            } catch (ParseException e1) {
//                                e1.printStackTrace();
//                            }
//                        }
//                        user.put("friends", friends);
//
//                        user.saveInBackground(new SaveCallback() {
//                            @Override
//                            public void done(ParseException e) {
//                                if (e == null) {
//                                    try {
//                                        user.fetch();
//                                    } catch (ParseException e1) {
//                                        e1.printStackTrace();
//                                    }
//                                } else {
//                                    Log.i("Profile Activity", "Failed to update object, with error code: " + e.toString());
//                                }
//                            }
//                        });
//                        mainActivity.tvFriends.setText(String.valueOf(friends.size()));
//                        swipeContainer.setRefreshing(false);
//                    }
//                });
//            }
//        });
//    }
//
//    public void deleteApprovedRequest(String id) {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("ApprovedFriendRequests");
//        query.whereEqualTo("objectId", id);
//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                try {
//                    object.delete();
//                    object.saveInBackground();
//                } catch (ParseException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        });
//    }
//
//    public void deleteRemoveRequest(String id) {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("RemovedRequests");
//        query.whereEqualTo("objectId", id);
//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                try {
//                    object.delete();
//                    object.saveInBackground();
//                } catch (ParseException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        });
//    }

}
