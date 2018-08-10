package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.Image;
import com.example.cassandrakane.goalz.models.Reaction;
import com.example.cassandrakane.goalz.models.Video;
import com.example.cassandrakane.goalz.utils.Util;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.OnBoomListener;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
    @BindView(R.id.pbProgress) ProgressBar pbProgress;
    @BindView(R.id.tvCaption) TextView tvCaption;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvDateAdded) TextView tvDateAdded;
    @BindView(R.id.root) RelativeLayout rootLayout;
    @BindView(R.id.rlInfo) RelativeLayout rlInfo;
    @BindView(R.id.bmb) BoomMenuButton bmb;
    @BindView(R.id.ivBmb) ImageView ivBmb;

    public Runnable runnable;
    public Handler mHandler;

    private ParseObject object;

    private Integer thumbsCount = 0;
    private Integer goalsCount = 0;
    private Integer clapCount = 0;
    private Integer okCount = 0;
    private Integer bumpCount = 0;

    private Goal mGoal;

    public static Goal goal;

    String type;

    public StoryFragment() { }

    public static StoryFragment newInstance(List<ParseObject> story, int index, ParseUser currentUser, Goal goal) {
        StoryFragment fragment = new StoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_STORY, (Serializable) story);
        args.putInt(ARG_INDEX, index);
        args.putParcelable(ARG_USER, currentUser);
        args.putParcelable("goal", goal);
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
            mGoal = getArguments().getParcelable("goal");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Util.storyMode = true;
        Util.storyFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);
        ButterKnife.bind(this, view);

        setImage();

        bmb.setOnBoomListener(new OnBoomListener() {
            @Override
            public void onClicked(int index, BoomButton boomButton) { }

            @Override
            public void onBackgroundClick() { }

            @Override
            public void onBoomWillHide() {

            }

            @Override
            public void onBoomDidHide() { }

            @Override
            public void onBoomWillShow() {
                if (mHandler != null) {
                    mHandler.removeCallbacks(runnable);
                }
            }

            @Override
            public void onBoomDidShow() { }
        });

        bmb.addBuilder(new SimpleCircleButton.Builder().normalImageRes(R.drawable.thumbs_react)
                .normalColorRes(R.color.white)
                .highlightedColorRes(R.color.orange)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        ivBmb.setImageResource(R.drawable.thumbs_react);
                        ivBmb.clearColorFilter();
                        addReaction(0);

                    }
                })
        );
        bmb.addBuilder(new SimpleCircleButton.Builder().normalImageRes(R.drawable.clap_react)
                .normalColorRes(R.color.white)
                .highlightedColorRes(R.color.orange)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        ivBmb.setImageResource(R.drawable.clap_react);
                        ivBmb.clearColorFilter();
                        addReaction(2);
                    }
                })
        );
        bmb.addBuilder(new SimpleCircleButton.Builder().normalImageRes(R.drawable.bump_react)
                .normalColorRes(R.color.white)
                .highlightedColorRes(R.color.orange)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        ivBmb.setImageResource(R.drawable.bump_react);
                        ivBmb.clearColorFilter();
                        addReaction(4);
                    }
                })
        );
        bmb.addBuilder(new SimpleCircleButton.Builder()
                .normalImageRes(R.drawable.notification)
                .normalColorRes(R.color.orange)
                .highlightedColorRes(R.color.white)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        final List<ParseObject> reactions = object.getList("reactions");
                        if (reactions != null) {
                            Integer reactionCount = reactions.size();
                            // TODO set text to reactionCount
//                            tvReactionCount.setText(Integer.toString(reactionCount));

                            if (reactionCount != 0) {
                                setReaction(reactions);
                                List<Integer> reactionCounts = Arrays.asList(thumbsCount, goalsCount, clapCount, okCount, bumpCount);
                                Intent intent = new Intent(getActivity(), ReactionModalActivity.class);
                                intent.putExtra("reactions", (Serializable) reactions);
                                intent.putExtra("reactionCounts", (Serializable) reactionCounts);
                                if (mHandler != null) {
                                    mHandler.removeCallbacks(runnable);
                                }
                                getActivity().startActivity(intent);
                            }
                        }
                    }
                })
        );
        bmb.addBuilder(new SimpleCircleButton.Builder().normalImageRes(R.drawable.ok_react)
                .normalColorRes(R.color.white)
                .highlightedColorRes(R.color.orange)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        ivBmb.setImageResource(R.drawable.ok_react);
                        ivBmb.clearColorFilter();
                        addReaction(3);
                    }
                })
        );
        bmb.addBuilder(new SimpleCircleButton.Builder().normalImageRes(R.drawable.rock_react)
                .normalColorRes(R.color.white)
                .highlightedColorRes(R.color.orange)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        ivBmb.setImageResource(R.drawable.rock_react);
                        ivBmb.clearColorFilter();
                        // TODO add reaction
                    }
                })
        );
        bmb.addBuilder(new SimpleCircleButton.Builder().normalImageRes(R.drawable.goals_react)
                .normalColorRes(R.color.white)
                .highlightedColorRes(R.color.orange)
                .listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        ivBmb.setImageResource(R.drawable.goals_react);
                        ivBmb.clearColorFilter();
                        addReaction(1);
                    }
                })
        );

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
                if (getActivity().getClass().isAssignableFrom(FriendActivity.class)) {
                    FriendActivity friendActivity = (FriendActivity) getActivity();
                    friendActivity.ivProfile.setVisibility(View.VISIBLE);
                    friendActivity.cardView.setVisibility(View.VISIBLE);
                    friendActivity.btnBack.setVisibility(View.VISIBLE);
                    friendActivity.btnUnfriend.setVisibility(View.VISIBLE);
                    friendActivity.btnMessage.setVisibility(View.VISIBLE);
                }

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
        try {
            object = mStory.get(mIndex).fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        ivAllReactions.setColorFilter(Color.argb(255, 255, 255, 255));
        ivBmb.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_round_thumb_up_24px));
        ivBmb.setColorFilter(ContextCompat.getColor(getActivity(), R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);

        ParseFile video = null;
        try {
            video = (ParseFile) object.fetchIfNeeded().get("video");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (video != null){
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

        }

        ParseUser user = null;
        try {
            user = object.getParseUser("user").fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (user != null) {
            tvUsername.setText(user .getUsername());
        }

        // TODO include hardcoding for ukulele videos
        // HARDCODE FOR DEMO
        if (!object.getObjectId().equals("pjJ33DVw3s") && !object.getObjectId().equals("ghmG65FNbI")) {
            tvDateAdded.setText("DAY 2");
        } else {
            tvDateAdded.setText("DAY 1");
        }

//        Date createdGoal = mGoal.getCreatedAt();
//        Date createdAt = object.getCreatedAt();
//        int dateDiff = (int) createdAt.getTime() - (int) createdGoal.getTime();
//        int day = dateDiff / (int) TimeUnit.DAYS.toMillis(1);
//        tvDateAdded.setText("DAY " + Integer.toString(day + 1));

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
                    ivBmb.clearColorFilter();
                    switch (type) {
                        case "thumbs":
                            ivBmb.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.thumbs_react));
                            break;
                        case "goals":
                            ivBmb.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.goals_react));
                            break;
                        case "clap":
                            ivBmb.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.clap_react));
                            break;
                        case "ok":
                            ivBmb.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ok_react));
                            break;
                        case "bump":
                            ivBmb.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.bump_react));
                            break;
                        default:
                            ivBmb.setVisibility(View.GONE);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void addReaction(int selectedIndex){
        switch (selectedIndex) {
            case 0:
                type = "thumbs";
                break;
            case 1:
                type = "goals";
                break;
            case 2:
                type = "clap";
                break;
            case 3:
                type = "ok";
                break;
            case 4:
                type = "bump";
                break;
            default:
                type = "";

        }

        if (object.get("video") != null) {
            final Video parseObject = (Video) object;
            final Reaction reaction = new Reaction(type, ParseUser.getCurrentUser());
            reaction.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    List<ParseObject> reactions = parseObject.getList("reactions");
                    if (reactions == null) {
                        reactions = new ArrayList<>();
                    }
                    reactions.add(reaction);
                    parseObject.setReactions(reactions);
                    parseObject.saveInBackground();
                    List<ParseObject> reacts = StoryFragment.goal.getReactions();
                    reacts.add(reaction);
                    StoryFragment.goal.setReactions(reacts);
                    StoryFragment.goal.saveInBackground();
                }
            });
        } else {
            final Image parseObject = (Image) object;
            final Reaction reaction = new Reaction(type, ParseUser.getCurrentUser());
            reaction.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    List<ParseObject> reactions = parseObject.getList("reactions");
                    if (reactions == null) {
                        reactions = new ArrayList<>();
                    }

                    reactions.add(reaction);
                    parseObject.setReactions(reactions);
                    parseObject.saveInBackground();
                    List<ParseObject> reacts = StoryFragment.goal.getReactions();
                    reacts.add(reaction);
                    StoryFragment.goal.setReactions(reacts);

                    StoryFragment.goal.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.i("sdf", "success");
                        }
                    });
                }
            });
        }
    }
}
