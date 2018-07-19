package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.Goal;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    private final List<Goal> goals;
    Context context;
    Date currentDate;

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
        currentDate = new Date();

        Date updateBy = goal.getUpdateStoryBy();
        if (updateBy != null) {
            if (currentDate.getTime() >= updateBy.getTime()) {
                if (!goal.itemAdded) {
                    goal.setStreak(0);
                }
                long sum = updateBy.getTime() + TimeUnit.DAYS.toMillis(goal.getFrequency());
                Date newDate = new Date(sum);
                goal.setUpdateStoryBy(newDate);
                goal.itemAdded = false;
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
        }

        if (updateBy != null && (updateBy.getTime() - currentDate.getTime()) < TimeUnit.HOURS.toMillis(4) && !goal.itemAdded){
            holder.ivStar.setImageResource(R.drawable.clock);
        } else {
            holder.ivStar.setImageResource(R.drawable.star);
        }
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

    // create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvDescription;
        TextView tvStreak;
        TextView tvProgress;
        ImageView ivStar;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStreak = itemView.findViewById(R.id.tvStreak);
            tvProgress = itemView.findViewById(R.id.tvProgress);
            ivStar = itemView.findViewById(R.id.ivStar);
        }
    }

}