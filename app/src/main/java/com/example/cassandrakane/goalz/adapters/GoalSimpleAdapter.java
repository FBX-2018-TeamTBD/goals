package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.Goal;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalSimpleAdapter extends RecyclerView.Adapter<GoalSimpleAdapter.ViewHolder> {

    private List<Goal> mGoals;
    Context context;

    public GoalSimpleAdapter(List<Goal> goals) {
        this.mGoals = goals;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(
                inflater.inflate(R.layout.item_goal_simple, parent, false)
        );
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get the data according to position
        final Goal goal = mGoals.get(position);

        holder.tvTitle.setText(goal.getTitle());
    }

    @Override
    public int getItemCount() {
        return mGoals.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mGoals.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Goal> list) {
        mGoals.addAll(list);
        notifyDataSetChanged();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.ivStory) ImageView ivStory;
        @BindView(R.id.ivCheck) ImageView ivCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v){
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                Goal goal = mGoals.get(position);
                goal.setSelected(!(goal.isSelected()));
                //itemView.setBackgroundColor(goal.isSelected() ? context.getColor(R.color.orange) : Color.WHITE);
                //ivStory.setImageDrawable(goal.isSelected() ? context.getDrawable(R.drawable.check) : context.getDrawable(R.drawable.placeholder_user));
                ivCheck.setImageDrawable(goal.isSelected() ? context.getDrawable(R.drawable.check) : null);
            }
        }
    }

}
