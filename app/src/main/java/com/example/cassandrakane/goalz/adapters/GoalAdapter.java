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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cassandrakane.goalz.CameraActivity;
import com.example.cassandrakane.goalz.FriendActivity;
import com.example.cassandrakane.goalz.NotificationHelper;
import com.example.cassandrakane.goalz.ProfileActivity;
import com.example.cassandrakane.goalz.R;
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

    public GoalAdapter(List<Goal> goals, boolean personal) {
        this.goals = goals;
        this.personal = personal;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(
                inflater.inflate(R.layout.item_goal, parent, false)
        );
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get the data according to position
        final Goal goal = goals.get(position);
        currentDate = new Date();

        Date updateBy = goal.getUpdateStoryBy();
        if (updateBy != null) {
            if (currentDate.getTime() >= updateBy.getTime()) {
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
        if (goal.getCompleted()) {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.grey));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.black));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.delete_goal)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NotificationHelper notificationHelper = new NotificationHelper(context.getApplicationContext());
                                notificationHelper.cancelReminder(goal);
                                goals.remove(goal);
                                if (goal.getCompleted()) {
                                    ((ProfileActivity) context).tvProgress.setText(String.valueOf(((ProfileActivity) context).completedGoals - 1));
                                } else {
                                    ((ProfileActivity) context).tvProgress.setText(String.valueOf(((ProfileActivity) context).progressGoals - 1));
                                }
                                notificationHelper.cancelReminder(goal);
                                removeGoal(goal.getObjectId());
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
            }
        });

        int timeRunningOutHours = context.getResources().getInteger(R.integer.TIME_RUNNING_OUT_HOURS);
        if (updateBy != null && (updateBy.getTime() - currentDate.getTime()) < TimeUnit.HOURS.toMillis(timeRunningOutHours) && !goal.getIsItemAdded()){
            holder.ivStar.setImageResource(R.drawable.clock);
        } else {
            holder.ivStar.setImageResource(R.drawable.star);
        }

        final ArrayList<String> imageUrls = goal.getStoryUrls();

        if (imageUrls.size() > 0) {
            Glide.with(context)
                    .load(imageUrls.get(imageUrls.size() - 1))
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivStory);

            holder.ivStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context.getClass().isAssignableFrom(ProfileActivity.class)) {
                        ProfileActivity activity = (ProfileActivity) context;
                        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
                        fragTransStory.add(R.id.drawer_layout, StoryFragment.newInstance(imageUrls, imageUrls.size() - 1)).commit();
                        activity.toolbar.setVisibility(View.INVISIBLE);
                    }
                    if (context.getClass().isAssignableFrom(FriendActivity.class)) {
                        FriendActivity activity = (FriendActivity) context;
                        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
                        fragTransStory.add(R.id.root_layout, StoryFragment.newInstance(imageUrls, imageUrls.size() - 1)).commit();
                        activity.ivProfile.setVisibility(View.INVISIBLE);
                        activity.cardView.setVisibility(View.INVISIBLE);
                        activity.btnBack.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } else {
            if (personal) {
                holder.ivStory.setImageDrawable(context.getResources().getDrawable(R.drawable.add_circle));
                holder.ivStory.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(context, CameraActivity.class);
                        intent.putExtra("goals", (Serializable) goals);
                        intent.putExtra(Goal.class.getSimpleName(), Parcels.wrap(goal));
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

    // create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvDescription) TextView tvDescription;
        @BindView(R.id.tvStreak) TextView tvStreak;
        @BindView(R.id.tvProgress) TextView tvProgress;
        @BindView(R.id.ivStory) ImageView ivStory;
        @BindView(R.id.ivStar) ImageView ivStar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}