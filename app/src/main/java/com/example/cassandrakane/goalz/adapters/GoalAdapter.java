package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cassandrakane.goalz.CameraActivity;
import com.example.cassandrakane.goalz.FriendActivity;
import com.example.cassandrakane.goalz.FriendsModalActivity;
import com.example.cassandrakane.goalz.NotificationHelper;
import com.example.cassandrakane.goalz.ProfileActivity;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.SearchFriendsActivity;
import com.example.cassandrakane.goalz.StoryFragment;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.Serializable;
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
    Date currentDate;
    ParseUser currentUser;
    float startX = 0;
    float endX = 0;
    boolean longClick = false;
    int startIndex = 0;

    public GoalAdapter(List<Goal> gGoals, boolean personal) {
        this.goals = gGoals;
        this.personal = personal;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(inflater.inflate(R.layout.item_goal, parent, false));
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Goal goal = goals.get(position);
        currentDate = new Date();
        currentUser = ParseUser.getCurrentUser();

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
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                Log.i("scroll", startX + " " + endX);
                startX = motionEvent.getX();
                endX = motionEvent1.getX();
                if (endX >= startX + 60) {
                    ((ProfileActivity) context).toCamera();
                    startX = 0;
                    endX = 0;
                } else if (startX >= endX + 50) {
                    ((ProfileActivity) context).toFeed();
                    startX = 0;
                    endX = 0;
                }
                return false;
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
                                    if (finalGoal.getCompleted()) {
                                        ((ProfileActivity) context).tvProgress.setText(String.valueOf(((ProfileActivity) context).completedGoals - 1));
                                    } else {
                                        ((ProfileActivity) context).tvProgress.setText(String.valueOf(((ProfileActivity) context).progressGoals - 1));
                                    }
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
                Log.i("fling", startX + " " + endX);
                startX = motionEvent.getX();
                endX = motionEvent1.getX();
                if (endX >= startX + 60) {
                    ((ProfileActivity) context).toCamera();
                    startX = 0;
                    endX = 0;
                } else if (startX >= endX + 50) {
                    ((ProfileActivity) context).toFeed();
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

        Date updateBy = goal.getUpdateStoryBy();
        if (updateBy != null) {
            if (currentDate.getTime() >= updateBy.getTime()) {
                // check if all users have added here
                if (!goal.getIsItemAdded()) {
                    goal.setStreak(0);
                }
                long sum = updateBy.getTime() + TimeUnit.DAYS.toMillis(goal.getFrequency());
                Date newDate = new Date(sum);
                goal.setUpdateStoryBy(newDate);
                goal.setItemAdded(false);
                goal.saveInBackground();
            }
        }

        holder.tvTitle.setText(goal.getTitle());
        holder.tvDescription.setText(goal.getDescription());
        holder.tvProgress.setText(goal.getProgress() + "/" + goal.getDuration());
        if (goal.getStreak() > 0) {
            holder.tvStreak.setText(String.format("%d", goal.getStreak()));
            holder.ivStar.setVisibility(View.VISIBLE);
        } else {
            holder.tvStreak.setText("");
            holder.ivStar.setVisibility(View.INVISIBLE);
        }


        if (goal.getApprovedUsers().size() > 1) {
            holder.tvFriends.setText(String.valueOf(goal.getApprovedUsers().size() - 1));
            holder.ivFriends.setImageDrawable(context.getResources().getDrawable(R.drawable.friend));
            holder.ivFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, FriendsModalActivity.class);
                    i.putExtra(Goal.class.getSimpleName(), goal);
                    context.startActivity(i);
                }
            });
        } else {
            holder.tvFriends.setText("");
            holder.ivFriends.setImageDrawable(context.getResources().getDrawable(R.drawable.add));
            holder.ivFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, SearchFriendsActivity.class);
                    i.putExtra("requestActivity", FriendsModalActivity.class.getSimpleName());
                    i.putExtra(Goal.class.getSimpleName(), goal);
                    context.startActivity(i);
                }
            });
        }

        if (goal.getCompleted()) {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.grey));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.black));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        int timeRunningOutHours = context.getResources().getInteger(R.integer.TIME_RUNNING_OUT_HOURS);
        if (updateBy != null && (updateBy.getTime() - currentDate.getTime()) < TimeUnit.HOURS.toMillis(timeRunningOutHours) && !goal.getIsItemAdded() && !goal.getCompleted()){
            holder.ivStar.setImageResource(R.drawable.clock);
        } else {
            holder.ivStar.setImageResource(R.drawable.star);
        }

//        List<Image> imageList = goal.getList("images");
////        Image im = imageList.get(0);
////        String url = im.getImage().getUrl();
//        ArrayList<String> imageUrls = new ArrayList<>();
//        if (imageList.size() != 0) {
//            imageUrls = getImageUrls(imageList);
//        }
        final ArrayList<String> imageUrls = goal.getStoryUrls();
        final ArrayList<ParseObject> story = goal.getStory();


        if (imageUrls.size() > 0) {
            Glide.with(context)
                    .load(imageUrls.get(imageUrls.size() - 1))
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivStory);

            holder.ivStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                            break;
                        }
                    }
                    if (context.getClass().isAssignableFrom(ProfileActivity.class)) {
                        ProfileActivity activity = (ProfileActivity) context;
                        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
                        fragTransStory.add(R.id.drawer_layout, StoryFragment.newInstance(story, startIndex, currentUser)).commit();
                        activity.toolbar.setVisibility(View.INVISIBLE);
                    }
                    if (context.getClass().isAssignableFrom(FriendActivity.class)) {
                        FriendActivity activity = (FriendActivity) context;
                        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
                        fragTransStory.add(R.id.root_layout, StoryFragment.newInstance(story, startIndex, currentUser)).commit();
                        activity.ivProfile.setVisibility(View.INVISIBLE);
                        activity.cardView.setVisibility(View.INVISIBLE);
                        activity.btnBack.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } else {
            if (personal) {
                holder.ivStory.setImageDrawable(context.getResources().getDrawable(R.drawable.add_circle));
                final Goal finalGoal1 = goal;
                holder.ivStory.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(context, CameraActivity.class);
                        List<Goal> uncompleteGoals = new ArrayList<>();
                        for (Goal goal : goals){
                            if (!goal.getCompleted()){
                                uncompleteGoals.add(goal);
                            }
                        }
                        intent.putExtra("goals", (Serializable) uncompleteGoals);
                        intent.putExtra(Goal.class.getSimpleName(), Parcels.wrap(finalGoal1));
                        context.startActivity(intent);
                    }
                });
            } else {
                holder.ivStory.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder_friend));
            }
        }
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
                    object.delete();
                    object.saveInBackground();
                    notifyDataSetChanged();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

//    public ArrayList<String> getImageUrls(List<Image> imageList) {
//        ArrayList<Image> images = new ArrayList<Image>();
//        if (imageList != null) {
//            images.addAll(imageList);
//        }
//        ArrayList<String> imageUrls = new ArrayList<String>();
//        for (int i = 0; i<imageList.size(); i++) {
////            try {
//                Image im = imageList.get(i);
//            String url = null;
//            try {
//                url = im.fetchIfNeeded().getParseFile("image").getUrl();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            imageUrls.add(url);
////            } catch (ParseException e) {
////                e.printStackTrace();
////            }
//        }
//        return imageUrls;
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvDescription) TextView tvDescription;
        @BindView(R.id.tvStreak) TextView tvStreak;
        @BindView(R.id.tvProgress) TextView tvProgress;
        @BindView(R.id.ivStory) ImageView ivStory;
        @BindView(R.id.ivStar) ImageView ivStar;
        @BindView(R.id.ivFriends) ImageView ivFriends;
        @BindView(R.id.tvFriends) TextView tvFriends;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}