package com.example.cassandrakane.goalz;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.adapters.FriendAdapter;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import utils.NavigationHelper;
import utils.OnSwipeTouchListener;
import utils.Util;

import static android.support.v7.widget.DividerItemDecoration.HORIZONTAL;

public class FeedFragment extends Fragment {

    public final static int ADD_FRIEND_ACTIVITY_REQUEST_CODE = 14;

    List<ParseUser> friends;
    FriendAdapter friendAdapter;

    @BindView(R.id.rvFriends) RecyclerView rvFriends;
    @BindView(R.id.noFriends) RelativeLayout noFriendsPage;
    @BindView(R.id.btnAdd2) Button btnAdd2;

    ParseUser user = ParseUser.getCurrentUser();

    public FeedFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(getActivity());

        final NavigationHelper navigationHelper = new NavigationHelper(getActivity());
        OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeRight() {
                navigationHelper.toGoals(false);
            }
        };

        getActivity().getWindow().getDecorView().getRootView().setOnTouchListener(onSwipeTouchListener);

        friends = new ArrayList<>();
        friendAdapter = new FriendAdapter(friends);
        rvFriends.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFriends.setAdapter(friendAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), HORIZONTAL);
        rvFriends.addItemDecoration(itemDecor);
        rvFriends.setOnTouchListener(onSwipeTouchListener);

        btnAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriend();
            }
        });

        populateFriends();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
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

    public void addFriend() {
        Intent i = new Intent(getActivity(), SearchFriendsActivity.class);
        i.putExtra("requestActivity", this.getClass().getSimpleName());
        startActivityForResult(i, ADD_FRIEND_ACTIVITY_REQUEST_CODE);
    }

}
