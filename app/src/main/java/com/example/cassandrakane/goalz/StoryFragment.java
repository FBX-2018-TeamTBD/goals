package com.example.cassandrakane.goalz;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoryFragment extends Fragment {
    private static final String ARG_IMAGES = "images";
    private static final String ARG_INDEX = "index";

    private ArrayList<String> mImageURLs;
    private int mIndex;

    @BindView(R.id.ivImage) ImageView ivImage;
    @BindView(R.id.btnLeft) ImageButton btnLeft;
    @BindView(R.id.btnRight) ImageButton btnRight;

    public StoryFragment() { }

    public static StoryFragment newInstance(ArrayList<String> images, int index) {
        StoryFragment fragment = new StoryFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_IMAGES, images);
        args.putInt(ARG_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageURLs = getArguments().getStringArrayList(ARG_IMAGES);
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

        return view;
    }

    public void setImage() {
        if (mIndex < 0) {
            mIndex++;
        } else if (mIndex >= mImageURLs.size()) {
            mIndex--;
        } else {
            Glide.with(this)
                    .load(mImageURLs.get(mIndex))
                    .into(ivImage);
        }
    }
}
