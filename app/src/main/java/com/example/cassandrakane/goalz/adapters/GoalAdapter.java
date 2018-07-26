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
import com.example.cassandrakane.goalz.models.SharedGoal;
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

public class GoalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<SharedGoal> sharedGoals;
    private final List<Goal> individualGoals;
    private boolean personal; //for determining whether this is for user or for a friend
    Context context;
    Date currentDate;

    public GoalAdapter(List<SharedGoal> shGoals, List<Goal> indGoals, boolean personal) {
        this.sharedGoals = shGoals;
        this.individualGoals = indGoals;
        this.personal = personal;
    }

    @Override
    public int getItemViewType(int position) {
        return position < sharedGoals.size() ? 0 : 1;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case 0:
                return new SharedGoalViewHolder(inflater.inflate(R.layout.item_shared_goal, parent, false));
            case 1:
                return new IndividualGoalViewHolder(inflater.inflate(R.layout.item_goal, parent, false));
        }
        return null;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Goal goal = null;
        TextView tvTitle = null;
        TextView tvDescription = null;
        TextView tvProgress = null;
        TextView tvStreak = null;
        ImageView ivStar = null;
        ImageView ivStory = null;
        switch (holder.getItemViewType()) {
            case 0:
                goal = sharedGoals.get(position);
                SharedGoalViewHolder shHolder = (SharedGoalViewHolder) holder;
                tvTitle = shHolder.tvTitle;
                tvDescription = shHolder.tvDescription;
                tvProgress = shHolder.tvProgress;
                tvStreak = shHolder.tvStreak;
                ivStar = shHolder.ivStar;
                ivStory = shHolder.ivStory;
                break;
            case 1:
                goal = individualGoals.get(position - sharedGoals.size());
                IndividualGoalViewHolder indHolder = (IndividualGoalViewHolder) holder;
                tvTitle = indHolder.tvTitle;
                tvDescription = indHolder.tvDescription;
                tvProgress = indHolder.tvProgress;
                tvStreak = indHolder.tvStreak;
                ivStar = indHolder.ivStar;
                ivStory = indHolder.ivStory;
                break;
        }
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

        tvTitle.setText(goal.getTitle());
        tvDescription.setText(goal.getDescription());
        tvProgress.setText(goal.getProgress() + "/" + goal.getDuration());
        if (goal.getStreak() > 0) {
            tvStreak.setText(String.format("%d", goal.getStreak()));
            ivStar.setVisibility(View.VISIBLE);
        } else {
            tvStreak.setText("");
            ivStar.setVisibility(View.INVISIBLE);
        }
        if (goal.getCompleted()) {
            tvTitle.setTextColor(context.getResources().getColor(R.color.grey));
            tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvTitle.setTextColor(context.getResources().getColor(R.color.black));
            tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvTitle.setPaintFlags(tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        final Goal finalGoal = goal;
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.delete_goal)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NotificationHelper notificationHelper = new NotificationHelper(context.getApplicationContext());
                                notificationHelper.cancelReminder(finalGoal);
                                switch (holder.getItemViewType()) {
                                    case 0: sharedGoals.remove(finalGoal);
                                    case 1: individualGoals.remove(finalGoal);
                                }
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
                return true;
            }
        });

        int timeRunningOutHours = context.getResources().getInteger(R.integer.TIME_RUNNING_OUT_HOURS);
        if (updateBy != null && (updateBy.getTime() - currentDate.getTime()) < TimeUnit.HOURS.toMillis(timeRunningOutHours) && !goal.getIsItemAdded()){
            ivStar.setImageResource(R.drawable.clock);
        } else {
            ivStar.setImageResource(R.drawable.star);
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
                    .into(ivStory);

            ivStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context.getClass().isAssignableFrom(ProfileActivity.class)) {
                        ProfileActivity activity = (ProfileActivity) context;
                        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
                        fragTransStory.add(R.id.drawer_layout, StoryFragment.newInstance(story, story.size() - 1)).commit();
                        activity.toolbar.setVisibility(View.INVISIBLE);
                    }
                    if (context.getClass().isAssignableFrom(FriendActivity.class)) {
                        FriendActivity activity = (FriendActivity) context;
                        final FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
                        fragTransStory.add(R.id.root_layout, StoryFragment.newInstance(story, story.size() - 1)).commit();
                        activity.ivProfile.setVisibility(View.INVISIBLE);
                        activity.cardView.setVisibility(View.INVISIBLE);
                        activity.btnBack.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } else {
            if (personal) {
                ivStory.setImageDrawable(context.getResources().getDrawable(R.drawable.add_circle));
                final Goal finalGoal1 = goal;
                ivStory.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        Intent intent = new Intent(context, CameraActivity.class);
                        List<Goal> uncompleteGoals = new ArrayList<>();
                        for (Goal goal : sharedGoals){
                            if (!goal.getCompleted()){
                                uncompleteGoals.add(goal);
                            }
                        }
                        for (Goal goal : individualGoals){
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
                ivStory.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder_friend));
            }
        }
    }

    private void removeGoal(String id) {
        final ParseUser user = ParseUser.getCurrentUser();
        user.put("goals", individualGoals);
        user.put("sharedGoals", sharedGoals);
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
        return sharedGoals.size() + individualGoals.size();
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

    public static class IndividualGoalViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvDescription) TextView tvDescription;
        @BindView(R.id.tvStreak) TextView tvStreak;
        @BindView(R.id.tvProgress) TextView tvProgress;
        @BindView(R.id.ivStory) ImageView ivStory;
        @BindView(R.id.ivStar) ImageView ivStar;

        public IndividualGoalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public static class SharedGoalViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvDescription) TextView tvDescription;
        @BindView(R.id.tvStreak) TextView tvStreak;
        @BindView(R.id.tvProgress) TextView tvProgress;
        @BindView(R.id.ivStory) ImageView ivStory;
        @BindView(R.id.ivStar) ImageView ivStar;

        public SharedGoalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}