package com.example.cassandrakane.goalz.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.transition.Transition;
import android.support.transition.TransitionInflater;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.cassandrakane.goalz.CameraFragment;
import com.example.cassandrakane.goalz.FriendActivity;
import com.example.cassandrakane.goalz.FriendsModalActivity;
import com.example.cassandrakane.goalz.MainActivity;
import com.example.cassandrakane.goalz.ProfileFragment;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.SearchFriendsActivity;
import com.example.cassandrakane.goalz.StoryFragment;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.Reaction;
import com.example.cassandrakane.goalz.utils.NavigationHelper;
import com.example.cassandrakane.goalz.utils.NotificationHelper;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    private final List<Goal> goals;
    private boolean personal; //for determining whether this is for user or for a friend
    Context context;
    Activity activity;
    Date currentDate;
    ParseUser currentUser;
    float startX = 0;
    float endX = 0;
    int startIndex = 0;
    NavigationHelper navigationHelper;

    public GoalAdapter(List<Goal> gGoals, boolean personal) {
        this.goals = gGoals;
        this.personal = personal;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        //activity = (MainActivity) parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(inflater.inflate(R.layout.item_goal, parent, false));
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Goal goal = goals.get(position);
        currentDate = new Date();
        currentUser = ParseUser.getCurrentUser();

        List<ParseObject> reax = goal.getReactions();
        int thumbs = 0;
        int goaled = 0;
        int claps = 0;
        int oks = 0;
        int bumps = 0;
        int total = reax.size();
        for (int i = 0; i < reax.size(); i++) {
            Reaction react = (Reaction) reax.get(i);
            String type = null;
            try {
                type = react.fetchIfNeeded().getString("type");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (type.equals("thumbs")) {
                thumbs += 1;
            } else if (type.equals("goals")) {
                goaled += 1;
            } else if (type.equals("clap")) {
                claps += 1;
            } else if (type.equals("ok")) {
                oks += 1;
            } else if (type.equals("bump")) {
                bumps += 1;
            }
        }
        holder.tvReaction.setText(String.valueOf(total));
        holder.tvThumb.setText(String.valueOf(thumbs));
        holder.tvClap.setText(String.valueOf(claps));
        holder.tvGoals.setText(String.valueOf(goaled));
        holder.tvOk.setText(String.valueOf(oks));
        holder.tvBump.setText(String.valueOf(bumps));
        holder.btnReaction.setTag(context.getResources().getColor(R.color.white));
        holder.btnReaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btnReaction.getTag().equals(context.getResources().getColor(R.color.white))) {
                    slideLeft(holder.reactionView);
                    holder.btnReaction.setTag(context.getResources().getColor(R.color.orange));
                } else {
                    holder.reactionView.setVisibility(View.INVISIBLE);
                    holder.btnReaction.setTag(context.getResources().getColor(R.color.white));
                    slideRight(holder.reactionView);
                }
            }
        });

        if (personal) {
            final MainActivity activity = (MainActivity) context;

            navigationHelper = new NavigationHelper(activity.centralFragment.horizontalPager);

            final Goal finalGoal = goal;
            final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent motionEvent) {
                    return true;
                }

                @Override
                public void onShowPress(MotionEvent motionEvent) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {

                    final ArrayList<String> imageUrls = goal.getStoryUrls();
                    final ArrayList<ParseObject> story = goal.getStory();

                    if (imageUrls.size() > 0) {
                        for (int i =0; i<story.size(); i++){
                            boolean seen = false;
                            ParseObject image = story.get(i);
                            List<ParseUser> users = image.getList("viewedBy");
                            if (users != null) {
                                if (users.contains(currentUser)){
                                    seen = true;
                                }
                            }
                            if (!seen) {
                                startIndex = i;
                                // TODO - show blue dot under story
                                break;
                            }
                        }
                        if (context.getClass().isAssignableFrom(MainActivity.class)) {
                            MainActivity activity = (MainActivity) context;
//                        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
//                        FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
//                        fragTransStory.add(R.id.drawer_layout, StoryFragment.newInstance(story, startIndex, currentUser)).commit();
//                        activity.toolbar.setVisibility(View.INVISIBLE);
                            ProfileFragment fragmentOne = new ProfileFragment();
                            StoryFragment fragmentTwo = StoryFragment.newInstance(story, startIndex, currentUser);
                            Transition changeTransform = TransitionInflater.from(context).
                                    inflateTransition(R.transition.change_image_transform);
                            Transition changeBoundsTransform = TransitionInflater.from(context).
                                    inflateTransition(R.transition.change_bounds);
                            Transition explodeTransform = TransitionInflater.from(context).
                                    inflateTransition(android.R.transition.fade);

//                        fragmentTwo.setSharedElementEnterTransition(new DetailsTransition());
//                        fragmentTwo.setEnterTransition(new Fade());
//                        fragmentTwo.setExitTransition(new Fade());
//                        fragmentTwo.setSharedElementReturnTransition(new DetailsTransition());

                            fragmentOne.setSharedElementReturnTransition(changeTransform);
                            fragmentOne.setExitTransition(explodeTransform);

                            fragmentTwo.setSharedElementEnterTransition(changeTransform);
                            fragmentTwo.setEnterTransition(explodeTransform);

                            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_central_fragment, fragmentTwo)
                                    .addToBackStack("transaction")
                                    .addSharedElement(holder.ivStory, "story");
                            ft.commit();

//                        ((MainActivity) context).storyTransition(story, startIndex, currentUser);
                        }
                        if (context.getClass().isAssignableFrom(FriendActivity.class)) {
                            FriendActivity activity = (FriendActivity) context;
                            final FragmentManager fragmentManager = activity.getSupportFragmentManager();
                            FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
                            fragTransStory.add(R.id.root_layout, StoryFragment.newInstance(story, startIndex, currentUser)).commit();
                            activity.ivProfile.setVisibility(View.INVISIBLE);
                            activity.cardView.setVisibility(View.INVISIBLE);
                            activity.btnBack.setVisibility(View.INVISIBLE);
                            activity.btnUnfriend.setVisibility(View.INVISIBLE);
                        }
                    }

                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                    startX = motionEvent.getX();
                    endX = motionEvent1.getX();
                    if (endX >= startX + 60) {
                        navigationHelper.toCamera();
                        startX = 0;
                        endX = 0;
                    } else if (startX >= endX + 50) {
                        navigationHelper.toFeed();
                        startX = 0;
                        endX = 0;
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent motionEvent) {
                    if (Math.abs(endX - startX) < 10) {
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.delete_goal)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        NotificationHelper notificationHelper = new NotificationHelper(context.getApplicationContext());
                                        notificationHelper.cancelReminder(finalGoal);
                                        goals.remove(finalGoal);
                                        finalGoal.unpinInBackground();
                                        notificationHelper.cancelReminder(finalGoal);
                                        removeGoal(finalGoal.getObjectId());
                                    }
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();
                    }
                }

                @Override
                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                    startX = motionEvent.getX();
                    endX = motionEvent1.getX();
                    if (endX >= startX + 60) {
                        navigationHelper.toCamera();
                        startX = 0;
                        endX = 0;
                    } else if (startX >= endX + 50) {
                        navigationHelper.toFeed();
                        startX = 0;
                        endX = 0;
                    }
                    return false;
                }

            });

            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return gestureDetector.onTouchEvent(motionEvent);
                }
            });

            holder.btnStory.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return gestureDetector.onTouchEvent(motionEvent);
                }
            });

        }

        Date updateBy = goal.getUpdateStoryBy();
        if (updateBy != null) {
            if (currentDate.getTime() >= updateBy.getTime()) {
                // check if all users have added here
                if (!goal.getIsItemAdded()) {
                    goal.setStreak(0);

                    // stores the objectId of users who lost a streak
//                    ArrayList<ParseUser> streakLostBy = new ArrayList<>();
//                    Map<String, String> userAdded = goal.getUserAdded();
//
//                    if (userAdded != null) {
//                        for (Map.Entry<String, String> entry : userAdded.entrySet()) {
//                            String userId = entry.getKey();
//                            String value = entry.getValue();
//                            if (value.equals("false")) {
//                                ParseUser user = getParseUserFromId(userId);
//                                streakLostBy.add(user);
//                            }
//                        }
//                    }
//
//
//                    sendTextNotifications(goal, streakLostBy);

                }
                long sum = updateBy.getTime() + TimeUnit.DAYS.toMillis(goal.getFrequency());
                Date newDate = new Date(sum);
                goal.setUpdateStoryBy(newDate);
                goal.setItemAdded(false);
                goal.saveInBackground();
            }
        }

        holder.tvTitle.setText(goal.getTitle());
        holder.tvProgress.setText((goal.getDuration() - goal.getProgress()) + " DAYS LEFT");
        if (goal.getStreak() > 0) {
            holder.tvStreak.setText(String.format("%d", goal.getStreak()));
            holder.ivStar.setVisibility(View.VISIBLE);
        } else {
            holder.tvStreak.setText("");
            holder.ivStar.setVisibility(View.INVISIBLE);
        }


        if (goal.getApprovedUsers().size() > 1) {
            holder.tvFriends.setText(String.valueOf(goal.getApprovedUsers().size() - 1));
            holder.btnFriends.setBackground(context.getResources().getDrawable(R.drawable.friend));
            holder.btnFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, FriendsModalActivity.class);
                    i.putExtra(Goal.class.getSimpleName(), goal);
                    i.putExtra("personal", personal);
                    context.startActivity(i);
                }
            });
        } else {
            holder.tvFriends.setText("");
            if (personal) {
                holder.btnFriends.setBackground(context.getResources().getDrawable(R.drawable.larger_add));
                holder.btnFriends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, SearchFriendsActivity.class);
                        i.putExtra("requestActivity", FriendsModalActivity.class.getSimpleName());
                        i.putExtra(Goal.class.getSimpleName(), goal);
                        context.startActivity(i);
                    }
                });
            } else {
                holder.btnFriends.setBackground(null);
            }
        }

        if (goal.getCompleted()) {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.grey));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        int timeRunningOutHours = context.getResources().getInteger(R.integer.TIME_RUNNING_OUT_HOURS);
        if (updateBy != null && (updateBy.getTime() - currentDate.getTime()) < TimeUnit.HOURS.toMillis(timeRunningOutHours) && !goal.getIsItemAdded() && !goal.getCompleted()){
            holder.ivStar.setImageResource(R.drawable.clock);
        } else {
            holder.ivStar.setImageResource(R.drawable.star);
        }

        final ArrayList<String> imageUrls = goal.getStoryUrls();
        final ArrayList<ParseObject> story = goal.getStory();

        if (imageUrls.size() > 0) {
            for (int i = 0; i<story.size(); i++){
                boolean seen = false;
                ParseObject image = story.get(i);
                List<ParseUser> users = image.getList("viewedBy");
                if (users != null) {
                    if (users.contains(currentUser)){
                        seen = true;
                    }
                }

                if (!seen) {
                    startIndex = i;
                    break;
                }
            }

            holder.ibAdd.setVisibility(View.GONE);
            holder.btnFriends.setBackgroundTintList(context.getResources().getColorStateList(R.color.white));
            holder.tvFriends.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvReaction.setVisibility(View.VISIBLE);
            holder.btnReaction.setVisibility(View.VISIBLE);
            holder.tvAdd.setVisibility(View.INVISIBLE);

            Glide.with(context)
                    .load(imageUrls.get(startIndex))
                    .apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(10)))
                    .into(holder.ivStory);

            holder.btnStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (context.getClass().isAssignableFrom(MainActivity.class)) {
                        MainActivity activity = (MainActivity) context;
                        ProfileFragment fragmentOne = new ProfileFragment();
                        StoryFragment fragmentTwo = StoryFragment.newInstance(story, startIndex, currentUser);
                        fragmentTwo.goal = goal;
                        Log.i("sdf", fragmentTwo.goal.getTitle());
                        Transition changeTransform = TransitionInflater.from(context).
                                inflateTransition(R.transition.change_image_transform);
                        Transition explodeTransform = TransitionInflater.from(context).
                                inflateTransition(android.R.transition.fade);

                        fragmentOne.setSharedElementReturnTransition(changeTransform);
                        fragmentOne.setExitTransition(explodeTransform);

                        fragmentTwo.setSharedElementEnterTransition(changeTransform);
                        fragmentTwo.setEnterTransition(explodeTransform);

                        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_central_fragment, fragmentTwo)
                                .addToBackStack("transaction")
                                .addSharedElement(holder.ivStory, "story");
                        ft.commit();

//                        ((MainActivity) context).storyTransition(story, startIndex, currentUser);
                    }
                    if (context.getClass().isAssignableFrom(FriendActivity.class)) {
                        FriendActivity activity = (FriendActivity) context;
                        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
                        StoryFragment fragmentTwo = StoryFragment.newInstance(story, startIndex, currentUser);
                        fragmentTwo.goal = goal;
                        fragTransStory.add(R.id.root_layout, fragmentTwo).commit();

                        activity.ivProfile.setVisibility(View.GONE);
                        activity.cardView.setVisibility(View.GONE);
                        activity.btnBack.setVisibility(View.GONE);
                        activity.btnUnfriend.setVisibility(View.GONE);
                        activity.btnMessage.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            holder.ivStory.setImageDrawable(null);
            holder.btnFriends.setBackgroundTintList(context.getResources().getColorStateList(R.color.orange));
            holder.tvFriends.setTextColor(context.getResources().getColor(R.color.orange));
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.orange));
            holder.tvProgress.setTextColor(context.getResources().getColor(R.color.orange));
            holder.btnReaction.setVisibility(View.INVISIBLE);
            holder.vGradient.setVisibility(View.INVISIBLE);
            holder.tvAdd.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.btnFriends.getLayoutParams();
            params.setMargins(0, 25, 20, 0);
            holder.btnFriends.setLayoutParams(params);
            if (personal) {
                holder.ibAdd.setVisibility(View.VISIBLE);
                holder.ibAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        navigationHelper.toCamera();
                    }
                });
                final Goal finalGoal1 = goal;
                holder.btnStory.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        if (context.getClass().isAssignableFrom(MainActivity.class)) {
                            MainActivity activity = (MainActivity) context;
                            CameraFragment cameraFragment = CameraFragment.newInstance(finalGoal1);
                            final FragmentManager fragmentManager = activity.getSupportFragmentManager();
                            FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
                            fragTransStory.add(R.id.main_central_fragment, cameraFragment).commit();
                        }
                    }
                });
            } else {
                holder.ibAdd.setVisibility(View.GONE);
            }
        }

        startIndex = 0;
    }

    // slide the view from below itself to the current position
    public void slideRight(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                -view.getWidth()-7,                 // fromXDelta
                 0,                 // toXDelta
                0,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideLeft(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                -view.getWidth()-7,                 // toXDelta
                0,                 // fromYDelta
                0); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    private void removeGoal(String id) {
        final ParseUser user = ParseUser.getCurrentUser();
        user.put("goals", goals);
        ParseACL acl = user.getACL();
        if (!acl.getPublicReadAccess()) {
            acl.setPublicReadAccess(true);
            user.setACL(acl);
        }
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    try {
                        user.fetch();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.i("Profile Activity", "Failed to delete object, with error code: " + e.toString());
                }
            }
        });
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Goal");
        query.whereEqualTo("objectId", id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                try {
                    if (object != null) {
                        object.delete();
                        object.saveInBackground();
                        notifyDataSetChanged();
                    }
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public String getTextNotificationString(String goalTitle, List<ParseUser> users) {
        String text = String.format("Oh no! You lost your streak for \"%s\"! ", goalTitle);
        for (int i = 0; i < users.size(); i++) {
            ParseUser user = users.get(i);
            String username = "";
            if (user != null) {
                try {
                    username = user.fetchIfNeeded().getUsername();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (i == users.size() - 1) {
                text += String.format("%s ", username);
            } else if (i == users.size() - 2){
                text += String.format("%s and ", username);
            } else {
                text += String.format("%s, ", username);
            }
        }
        if (users.size() == 0) {
            text += "Someone ";
        }
        text += "forgot to post.";
        return text;
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvStreak) TextView tvStreak;
        @BindView(R.id.tvProgress) TextView tvProgress;
        @BindView(R.id.ivStory) ImageView ivStory;
        @BindView(R.id.ivStar) ImageView ivStar;
        @BindView(R.id.btnFriends) Button btnFriends;
        @BindView(R.id.tvFriends) TextView tvFriends;
        @BindView(R.id.btnStory) Button btnStory;
        @BindView(R.id.ibAdd) Button ibAdd;
        @BindView(R.id.tvAdd) TextView tvAdd;
        @BindView(R.id.btnReaction) Button btnReaction;
        @BindView(R.id.reaction_view) RelativeLayout reactionView;
        @BindView(R.id.tvThumb) TextView tvThumb;
        @BindView(R.id.tvGoals) TextView tvGoals;
        @BindView(R.id.tvClap) TextView tvClap;
        @BindView(R.id.tvOk) TextView tvOk;
        @BindView(R.id.tvBump) TextView tvBump;
        @BindView(R.id.tvReaction) TextView tvReaction;
        @BindView(R.id.vGradient) View vGradient;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}