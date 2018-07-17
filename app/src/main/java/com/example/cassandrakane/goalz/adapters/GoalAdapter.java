package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.Goal;

import java.util.List;

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

        holder.title.setText(goal.getTitle());
        holder.description.setText(goal.getDescription());
        holder.progress.setText(goal.getProgress() + "/" + goal.getDuration());
        if (goal.getStreak() > 0) {
            holder.streak.setText(String.format("%d", goal.getStreak()));
        } else {
            holder.streak.setText("");
        }
        if (goal.getCompleted()) {
            holder.title.setTextColor(context.getResources().getColor(R.color.grey));
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
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

        TextView title;
        TextView description;
        TextView streak;
        TextView progress;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvTitle);
            description = itemView.findViewById(R.id.tvDescription);
            streak = itemView.findViewById(R.id.tvStreak);
            progress = itemView.findViewById(R.id.tvProgress);
        }
    }

}