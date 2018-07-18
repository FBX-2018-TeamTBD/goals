package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cassandrakane.goalz.ProfileActivity;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.StoryFragment;
import com.example.cassandrakane.goalz.models.Goal;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    private final List<Goal> goals;
    Context context;

    public GoalAdapter(List<Goal> goals) {
        this.goals = goals;
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
        }

        holder.ivStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<ParseFile> imageList = goal.getList("images");
                ArrayList<String> imageUrls = getImageUrls(imageList);
                ProfileActivity activity = (ProfileActivity) context;
                final FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragTransStory = fragmentManager.beginTransaction();
                fragTransStory.add(R.id.root_layout, StoryFragment.newInstance(imageUrls, 0)).commit();
                activity.toolbar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        goals.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Goal> list) {
        goals.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<String> getImageUrls(List<ParseFile> imageList) {
        ArrayList<ParseFile> images = new ArrayList<ParseFile>();
        if (imageList != null) {
            images.addAll(imageList);
        }
        ArrayList<String> imageUrls = new ArrayList<String>();
        for (ParseFile f : images) {
            imageUrls.add(f.getUrl());
        }
        return imageUrls;
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