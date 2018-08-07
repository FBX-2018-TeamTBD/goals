package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.cassandrakane.goalz.models.Image;
import com.example.cassandrakane.goalz.models.Reaction;
import com.example.cassandrakane.goalz.models.Video;
import com.example.cassandrakane.goalz.utils.Util;
import com.example.cassandrakane.goalz.views.ReactionView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StoryFragment extends Fragment {
    private static final String ARG_STORY = "story";
    private static final String ARG_INDEX = "index";
    private static final String ARG_USER = "user";

    private ArrayList<ParseObject> mStory;
    private int mIndex;
    private ParseUser currentUser;

    @BindView(R.id.ivStory) ImageView ivImage;
    @BindView(R.id.viewStory) VideoView viewStory;
    @BindView(R.id.btnLeft) ImageButton btnLeft;
    @BindView(R.id.btnRight) ImageButton btnRight;
    @BindView(R.id.btnClose) ImageButton btnClose;
    @BindView(R.id.btnInfo) ImageButton btnInfo;
    @BindView(R.id.btnInfo2) ImageButton btnInfo2;
    @BindView(R.id.pbProgress) ProgressBar pbProgress;
    @BindView(R.id.tvCaption) TextView tvCaption;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvDateAdded) TextView tvDateAdded;
    @BindView(R.id.btnReaction) LinearLayout btnReaction;
    @BindView(R.id.ivReaction) public ImageView ivReaction;
    @BindView(R.id.btnTotalReactions) LinearLayout btnTotalReactions;
    @BindView(R.id.ivAllReactions) public ImageView ivAllReactions;
    @BindView(R.id.tvReactionCount) public TextView tvReactionCount;
    @BindView(R.id.root) RelativeLayout rootLayout;

    private Runnable runnable;
    private Handler mHandler;
    private ReactionView rv;

    private ParseObject object;

    private Integer thumbsCount = 0;
    private Integer goalsCount = 0;
    private Integer clapCount = 0;
    private Integer okCount = 0;
    private Integer bumpCount = 0;

    public StoryFragment() { }

    public static StoryFragment newInstance(ArrayList<ParseObject> story, int index, ParseUser currentUser) {
        StoryFragment fragment = new StoryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_STORY, story);
        args.putInt(ARG_INDEX, index);
        args.putParcelable(ARG_USER, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStory = getArguments().getParcelableArrayList(ARG_STORY);
            mIndex = getArguments().getInt(ARG_INDEX);
            currentUser = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Util.storyMode = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);
        ButterKnife.bind(this, view);

        setImage();

//        tvUsername.setVisibility(View.GONE);
//        tvDateAdded.setVisibility(View.GONE);

        btnReaction.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (rv != null) {
                            rootLayout.removeView(rv);
                        }

                        rv = new ReactionView(getActivity(), StoryFragment.this, object);
                        rootLayout.addView(rv);
                        /**
                         * stop story from moving
                         */
                        if (mHandler != null) {
                            mHandler.removeCallbacks(runnable);
                        }
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        btnLeft.setBackgroundColor(Color.TRANSPARENT);
        btnRight.setBackgroundColor(Color.TRANSPARENT);

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHandler != null) {
                    mHandler.removeCallbacks(runnable);
                }
                mIndex--;
                setImage();
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHandler != null) {
                    mHandler.removeCallbacks(runnable);
                }
                mIndex++;
                setImage();
            }
        });

        final StoryFragment storyFragment = this;
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHandler != null) {
                    mHandler.removeCallbacks(runnable);
                }

                Util.storyMode = false;

                getActivity().getSupportFragmentManager().beginTransaction().remove(storyFragment).commit();
                // TODO include animation?
//                activity.onBackPressed();

//                Activity activity = getActivity();
//                if (activity.getClass().isAssignableFrom(MainActivity.class)) {
//                    MainActivity mainActivity = (MainActivity) activity;
//                    mainActivity.getSupportFragmentManager().beginTransaction().remove(StoryFragment.this).commit();
//                    mainActivity.toolbar.setVisibility(View.VISIBLE);
//                }
                if (getActivity().getClass().isAssignableFrom(FriendActivity.class)) {
                    FriendActivity friendActivity = (FriendActivity) getActivity();
                    friendActivity.ivProfile.setVisibility(View.VISIBLE);
                    friendActivity.cardView.setVisibility(View.VISIBLE);
                    friendActivity.btnBack.setVisibility(View.VISIBLE);
                    friendActivity.btnUnfriend.setVisibility(View.VISIBLE);
                    friendActivity.btnMessage.setVisibility(View.VISIBLE);
                }
//                Activity activity = getActivity();
//                if (activity.getClass().isAssignableFrom(MainActivity.class)) {
//                    MainActivity mainActivity = (MainActivity) activity;
//                    mainActivity.getSupportFragmentManager().beginTransaction().remove(StoryFragment.this).commit();
//                    mainActivity.centralFragment.toolbar.setVisibility(View.VISIBLE);
//                }
//                if (activity.getClass().isAssignableFrom(FriendActivity.class)) {
//                    FriendActivity friendActivity = (FriendActivity) activity;
//                    friendActivity.getSupportFragmentManager().beginTransaction().remove(StoryFragment.this).commit();
//                    friendActivity.ivProfile.setVisibility(View.VISIBLE);
//                    friendActivity.cardView.setVisibility(View.VISIBLE);
//                    friendActivity.btnBack.setVisibility(View.VISIBLE);
//                }
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHandler != null) {
                    mHandler.removeCallbacks(runnable);
                }

                tvUsername.setVisibility(View.INVISIBLE);
                tvDateAdded.setVisibility(View.INVISIBLE);
//                holder.rvStory.setVisibility(View.INVISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation infoAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.info_slide_left);
                        tvUsername.setVisibility(View.VISIBLE);
                        tvDateAdded.setVisibility(View.VISIBLE);
                        btnInfo.startAnimation(infoAnimation);
                        tvUsername.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.menu_slide_up));
                        tvDateAdded.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.menu_slide_up));

                        infoAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                btnInfo.setVisibility(View.GONE);
                                btnInfo2.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    }
                }, 100);
            }
        });

        btnInfo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation detailAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.menu_slide_back);
                Animation infoAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.info_slide_right);
                tvUsername.startAnimation(detailAnimation);
                tvDateAdded.startAnimation(detailAnimation);
                btnInfo2.startAnimation(infoAnimation);

                detailAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tvUsername.setVisibility(View.INVISIBLE);
                        tvDateAdded.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                infoAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        btnInfo.setVisibility(View.VISIBLE);
                        btnInfo2.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

        return view;

    }

    public void setImage(){

        thumbsCount = 0;
        goalsCount = 0;
        clapCount = 0;
        okCount = 0;
        bumpCount = 0;

        if (mIndex < 0) {
            mIndex = mStory.size() - 1;
        } else if (mIndex >= mStory.size()) {
            mIndex = 0;
        }
        object = mStory.get(mIndex);

        ivReaction.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.goals_react));
        ivReaction.setColorFilter(Color.argb(255, 255, 255, 255));
        ivAllReactions.setColorFilter(Color.argb(255, 255, 255, 255));

//        tvReaction.setText("Like");

        if (object.get("video") != null){
            viewStory.setVisibility(View.VISIBLE);
            Video videoObject = (Video) object;

            List<ParseUser> viewedBy = object.getList("viewedBy");
            if (viewedBy == null){
                viewedBy = new ArrayList<>();
            }
            if (!viewedBy.contains(currentUser)) {
                viewedBy.add(currentUser);
                videoObject.setViewedBy(viewedBy);
                videoObject.saveInBackground();
            }

            ParseFile video = (ParseFile) object.get("video");

            String caption = (String) object.get("caption");
            if (caption.length() != 0){
                tvCaption.setText(caption);
                tvCaption.setVisibility(View.VISIBLE);
            } else {
                tvCaption.setVisibility(View.GONE);
            }

            File file = null;
            try {
                file = video.getFile();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Uri fileUri = Uri.fromFile(file);
            viewStory.setVideoURI(fileUri);
            viewStory.start();
            viewStory.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mIndex++;
                    setImage();
                }
            });

            final List<ParseObject> reactions = object.getList("reactions");
            if (reactions != null) {
                Integer reactionCount = reactions.size();
                tvReactionCount.setText(Integer.toString(reactionCount));

                setReaction(reactions);

                if (reactionCount != 0){
                    ivAllReactions.clearColorFilter();
                    btnTotalReactions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            List<Integer> reactionCounts = Arrays.asList(thumbsCount, goalsCount, clapCount, okCount, bumpCount);
                            Intent intent = new Intent(getActivity(), ReactionModalActivity.class);
                            intent.putExtra("reactions", (Serializable) reactions);
                            intent.putExtra("reactionCounts", (Serializable) reactionCounts);
                            if (mHandler != null) {
                                mHandler.removeCallbacks(runnable);
                            }
                            getActivity().startActivity(intent);
                        }
                    });
                } else {
                    btnTotalReactions.setOnClickListener(null);
                }

            }

            ParseUser user = object.getParseUser("user");
            if (user != null) {
                tvUsername.setText(user.getUsername());
            }
            DateFormat dateFormat = new SimpleDateFormat("mm//dd/yyyy");
            Date createdAt = object.getCreatedAt();
            tvDateAdded.setText(dateFormat.format(createdAt));

        } else {
            viewStory.setVisibility(View.GONE);
            Image imageObject = (Image) object;

            List<ParseUser> viewedBy = object.getList("viewedBy");
            if (viewedBy == null){
                viewedBy = new ArrayList<>();
            }
            if (!viewedBy.contains(currentUser)) {
                viewedBy.add(currentUser);
                imageObject.setViewedBy(viewedBy);
                imageObject.saveInBackground();
            }

            ParseFile image = (ParseFile) object.get("image");

            String caption = (String) object.get("caption");
            if (caption.length() != 0){
                tvCaption.setText(caption);
                tvCaption.setVisibility(View.VISIBLE);
            } else {
                tvCaption.setVisibility(View.GONE);
            }

            String url = image.getUrl();
            Glide.with(this)
                    .load(url)
                    .into(ivImage);

            mHandler = new Handler();
            mHandler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    mIndex++;
                    setImage();
                    }
            }, 5000);

            final List<ParseObject> reactions = object.getList("reactions");
            if (reactions != null) {
                Integer reactionCount = reactions.size();
                tvReactionCount.setText(Integer.toString(reactionCount));

                setReaction(reactions);

                if (reactionCount != 0) {
                    ivAllReactions.clearColorFilter();
                    btnTotalReactions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            List<Integer> reactionCounts = Arrays.asList(thumbsCount, goalsCount, clapCount, okCount, bumpCount);
                            Intent intent = new Intent(getActivity(), ReactionModalActivity.class);
                            intent.putExtra("reactions", (Serializable) reactions);
                            intent.putExtra("reactionCounts", (Serializable) reactionCounts);
                            if (mHandler != null) {
                                mHandler.removeCallbacks(runnable);
                            }
                            getActivity().startActivity(intent);
                        }
                    });
                } else {
                    btnTotalReactions.setOnClickListener(null);
                }
            }
        }

        ParseUser user = object.getParseUser("user");
        if (user != null) {
            tvUsername.setText(user.getUsername());
        }
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date createdAt = object.getCreatedAt();
        tvDateAdded.setText(dateFormat.format(createdAt));
        pbProgress.setProgress((mIndex + 1) * 100 / mStory.size());
    }

    public void setReaction(List<ParseObject> reactions){
        for (ParseObject reaction : reactions){
            Reaction reactionObject = (Reaction) reaction;
            String type = null;
            try {
                type = reactionObject.fetchIfNeeded().getString("type");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            switch (type) {
                case "thumbs":
                    thumbsCount += 1;
                    break;
                case "goals":
                    goalsCount += 1;
                    break;
                case "clap":
                    clapCount += 1;
                    break;
                case "ok":
                    okCount += 1;
                    break;
                case "bump":
                    bumpCount += 1;
                    break;
                default:
                    break;
            }

            try {
                if (reactionObject.fetchIfNeeded().getParseUser("user") == currentUser){
                    ivReaction.clearColorFilter();
                    switch (type) {
                        case "thumbs":
                            ivReaction.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.thumbs_react));
//                            tvReaction.setText("Liked");
                            break;
                        case "goals":
                            ivReaction.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.goals_react));
//                            tvReaction.setText("Liked");
                            break;
                        case "clap":
                            ivReaction.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.clap_react));
//                            tvReaction.setText("Liked");
                            break;
                        case "ok":
                            ivReaction.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ok_react));
//                            tvReaction.setText("Liked");
                            break;
                        case "bump":
                            ivReaction.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.bump_react));
//                            tvReaction.setText("Liked");
                            break;
                        default:
                            ivReaction.setVisibility(View.GONE);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
