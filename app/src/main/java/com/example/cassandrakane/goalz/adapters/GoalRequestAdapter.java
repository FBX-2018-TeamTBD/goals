package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.GoalRequestsActivity;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.GoalRequests;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoalRequestAdapter extends RecyclerView.Adapter<GoalRequestAdapter.ViewHolder> {


    private List<Goal> mGoals;
    private List<GoalRequests> requests;
    Context context;

    public GoalRequestAdapter(List<Goal> goals, List<GoalRequests> requests) {
        this.mGoals = goals;
        this.requests = requests;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(
                inflater.inflate(R.layout.item_goal_request, parent, false)
        );
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // get the data according to position
        Goal goal2 = null;
        try {
            goal2 = mGoals.get(position).fetchIfNeeded();
            holder.tvGoalTitle.setText(goal2.getTitle());
            holder.tvGoalUsers.setText(getSharedFriendsListString(goal2.getFriends()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Goal goal = goal2;
        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GoalRequestsActivity) context).progressBar.setVisibility(View.VISIBLE);
                addGoal(goal, position);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GoalRequestsActivity) context).progressBar.setVisibility(View.VISIBLE);
                removeUserfromFriends(goal, position);
            }
        });
    }

    public String getSharedFriendsListString(List<ParseUser> selectedFriends) {
        if (selectedFriends.size() > 0) {
            String str = "Shared with ";
            for (ParseUser friend : selectedFriends) {
                try {
                    if (!friend.fetch().getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                        str = str + friend.fetch().getUsername() + ", ";
                    }
                } catch(ParseException e) {
                    e.printStackTrace();
                }
            }
            return str.substring(0, str.length() - 2);
        }
        return "";
    }

    public void decreaseGoalRequests() {
        ParseQuery<SentFriendRequests> query2 = ParseQuery.getQuery("GoalRequests");
        query2.whereEqualTo("user", ParseUser.getCurrentUser());
        try {
            int count = query2.count();
            if(count > 0) {
                ((GoalRequestsActivity) context).navigationView.getMenu().getItem(4).setTitle("goal requests (" + count + ")");
            } else {
                ((GoalRequestsActivity) context).navigationView.getMenu().getItem(4).setTitle("goal requests");
            }
            ((GoalRequestsActivity) context).progressBar.setVisibility(View.INVISIBLE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mGoals.size();
    }

    public void deleteGoalRequest(final int position) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GoalRequests");
        query.whereEqualTo("objectId", requests.get(position).getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                try {
                    object.delete();
                    object.saveInBackground();
                    requests.remove(position);
                    mGoals.remove(position);
                    if (requests.size() > 0) {
                        ((GoalRequestsActivity) context).noGoalsPage.setVisibility(View.GONE);
                    } else {
                        ((GoalRequestsActivity) context).noGoalsPage.setVisibility(View.VISIBLE);
                    }
                    decreaseGoalRequests();
                    notifyDataSetChanged();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void removeUserfromFriends(Goal goal, int position) {
        try {
            goal = goal.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<ParseUser> friends = goal.getFriends();
        try {
            ParseObject.fetchAll(friends);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        friends.remove(ParseUser.getCurrentUser());
        goal.setFriends(friends);
        goal.saveInBackground();
        deleteGoalRequest(position);
        removeUserfromPending(goal);
    }

    public void removeUserfromPending(Goal goal) {
        try {
            goal = goal.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<ParseUser> pending = goal.getPendingUsers();
        try {
            ParseObject.fetchAll(pending);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        pending.remove(ParseUser.getCurrentUser());
        goal.setPendingUsers(pending);
        goal.saveInBackground();
    }

    public void moveUser(Goal goal) {
        try {
            goal = goal.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<ParseUser> approved = goal.getApprovedUsers();
        try {
            ParseObject.fetchAll(approved);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        approved.add(ParseUser.getCurrentUser());
        goal.setApprovedUsers(approved);
        removeUserfromPending(goal);
    }

    public void addGoal(final Goal goal, final int position) {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        List<Goal> goals = currentUser.getList("goals");
        goals.add(0, goal);
        currentUser.put("goals", goals);
        // move this user to the approved section of the shared goal
        ParseACL acl = currentUser.getACL();
        if (!acl.getPublicWriteAccess()) {
            acl.setPublicWriteAccess(true);
            currentUser.setACL(acl);
        }
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    try {
                        currentUser.fetch();
                        moveUser(goal);
                        ((GoalRequestsActivity) context).tvProgress.setText(String.valueOf(Integer.parseInt(((GoalRequestsActivity) context).tvProgress.getText().toString()) + 1));
                        Toast.makeText(context, "You are now goal buddies!", Toast.LENGTH_LONG).show();
                        deleteGoalRequest(position);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.i("Search Friend Activity", "Failed to update object, with error code: " + e.toString());
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvGoalTitle) TextView tvGoalTitle;
        @BindView(R.id.tvGoalUsers) TextView tvGoalUsers;
        @BindView(R.id.btnConfirm) Button btnConfirm;
        @BindView(R.id.btnDelete) Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }


}
