package com.example.cassandrakane.goalz;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoryFragment extends Fragment {
    private static final String ARG_STORY = "story";
    private static final String ARG_INDEX = "index";

    private ArrayList<ParseObject> mStory;
    private int mIndex;

    @BindView(R.id.ivImage) ImageView ivImage;
    @BindView(R.id.viewStory)
    VideoView viewStory;
    @BindView(R.id.btnLeft) ImageButton btnLeft;
    @BindView(R.id.btnRight) ImageButton btnRight;
    @BindView(R.id.btnClose) ImageButton btnClose;
    @BindView(R.id.pbProgress) ProgressBar pbProgress;

    private URL url;
    private File file;

    public StoryFragment() { }

    public static StoryFragment newInstance(ArrayList<ParseObject> story, int index) {
        StoryFragment fragment = new StoryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_STORY, story);
        args.putInt(ARG_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStory = getArguments().getParcelableArrayList(ARG_STORY);
            mIndex = getArguments().getInt(ARG_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);
        ButterKnife.bind(this, view);

        setImage();

        btnLeft.setBackgroundColor(Color.TRANSPARENT);
        btnRight.setBackgroundColor(Color.TRANSPARENT);

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIndex--;
                setImage();
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIndex++;
                setImage();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = getActivity();
                if (activity.getClass().isAssignableFrom(ProfileActivity.class)) {
                    ProfileActivity profileActivity = (ProfileActivity) activity;
                    profileActivity.getSupportFragmentManager().beginTransaction().remove(StoryFragment.this).commit();
                    profileActivity.toolbar.setVisibility(View.VISIBLE);
                }
                if (activity.getClass().isAssignableFrom(FeedActivity.class)) {
                    FeedActivity feedActivity = (FeedActivity) activity;
                    feedActivity.getSupportFragmentManager().beginTransaction().remove(StoryFragment.this).commit();
                    feedActivity.toolbar.setVisibility(View.VISIBLE);
                }
                if (activity.getClass().isAssignableFrom(FriendActivity.class)) {
                    FriendActivity friendActivity = (FriendActivity) activity;
                    friendActivity.getSupportFragmentManager().beginTransaction().remove(StoryFragment.this).commit();
                    friendActivity.ivProfile.setVisibility(View.VISIBLE);
                    friendActivity.cardView.setVisibility(View.VISIBLE);
                    friendActivity.btnBack.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    public void setImage(){
        if (mIndex < 0) {
            mIndex = mStory.size() - 1;
        } else if (mIndex >= mStory.size()) {
            mIndex = 0;
        }
        ParseObject object = mStory.get(mIndex);
        if (object.get("video") != null){
            viewStory.setVisibility(View.VISIBLE);
            ParseFile video = (ParseFile) object.get("video");

            File file = null;
            try {
                file = video.getFile();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Uri fileUri = Uri.fromFile(file);
            viewStory.setVideoURI(fileUri);
            viewStory.start();
        } else {
            viewStory.setVisibility(View.GONE);
            ParseFile image = (ParseFile) object.get("image");
            String url = image.getUrl();
            Glide.with(this)
                    .load(url)
                    .into(ivImage);
        }
        pbProgress.setProgress((mIndex + 1) * 100 / mStory.size());
    }
}
