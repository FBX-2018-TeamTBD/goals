package com.example.cassandrakane.goalz.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.NotificationsFragment;
import com.example.cassandrakane.goalz.R;
import com.example.cassandrakane.goalz.models.ApprovedFriendRequests;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.GoalRequests;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.example.cassandrakane.goalz.models.TextNotification;
import com.example.cassandrakane.goalz.utils.Util;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TextNotification> mTextNotifications;
    private List<Goal> mGoals;
    private List<GoalRequests> goalRequests;
    private List<ParseUser> mFriends;
    private List<SentFriendRequests> friendRequests;
    Context context;
    NotificationsFragment notificationsFragment;

    public NotificationAdapter(List<TextNotification> texts, List<Goal> goals,
                               List<GoalRequests> goalReq, List<ParseUser> friends,
                               List<SentFriendRequests> friendReq, NotificationsFragment notifFrag) {
        this.mTextNotifications = texts;
        this.mGoals = goals;
        this.goalRequests = goalReq;
        this.mFriends = friends;
        this.friendRequests = friendReq;
        this.notificationsFragment = notifFrag;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (i) {
            case 0: return new TextViewHolder(inflater.inflate(R.layout.item_text_notif, viewGroup, false));
            case 1: return new GoalRequestViewHolder(inflater.inflate(R.layout.item_goal_request, viewGroup, false));
            case 2: return new FriendRequestViewHolder(inflater.inflate(R.layout.item_friend_request, viewGroup, false));
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0:
                TextViewHolder textViewHolder = (TextViewHolder) holder;
                textViewHolder.tvText.setText(mTextNotifications.get(position).getText());
                ParseFile image = mTextNotifications.get(position).getImage();
                Util.setImage(image, context.getResources(), textViewHolder.ivImage, R.color.orange);
                textViewHolder.btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notificationsFragment.progressBar.setVisibility(View.VISIBLE);
                        deleteTextNotification(position);
                        notificationsFragment.progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                break;
            case 1:
                final int updatedPos = position - mTextNotifications.size();
                final GoalRequestViewHolder goalRequestViewHolder = (GoalRequestViewHolder) holder;
                Goal goal2 = null;
                try {
                    goal2 = mGoals.get(updatedPos).fetchIfNeeded();
                    String text = goal2.getTitle();
                    ParseUser fromUser = goalRequests.get(updatedPos).getFromUser();
                    if (fromUser != null) {
                        text = "<b>" + fromUser.fetchIfNeeded().getUsername() + "</b> invited you to their goal: <b>" + goal2.getTitle() + "</b>";
                    }
                    goalRequestViewHolder.tvGoalTitle.setText(Html.fromHtml(text));
                    List<ParseObject> story = goal2.getStory();
                    if (story.size() > 0) {
                        Util.setImage(story.get(story.size() - 1).fetch().getParseFile("image"), context.getResources(), goalRequestViewHolder.ivStory, R.color.orange);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                final Goal goal = goal2;
                goalRequestViewHolder.btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notificationsFragment.progressBar.setVisibility(View.VISIBLE);
                        addGoal(goal, updatedPos);
                        notificationsFragment.setNotificationHeader();
                        notificationsFragment.progressBar.setVisibility(View.INVISIBLE);
                        goalRequestViewHolder.btnConfirm.setOnClickListener(null);
                    }
                });
                goalRequestViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notificationsFragment.progressBar.setVisibility(View.VISIBLE);
                        removeUserfromFriends(goal, updatedPos);
                        notificationsFragment.progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                goalRequestViewHolder.btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notificationsFragment.progressBar.setVisibility(View.VISIBLE);
                        removeUserfromFriends(goal, updatedPos);
                        notificationsFragment.progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                break;
            case 2:
                final int updatedPos2 = position - mTextNotifications.size() - mGoals.size();
                FriendRequestViewHolder friendRequestViewHolder = (FriendRequestViewHolder) holder;
                final ParseUser friend = mFriends.get(updatedPos2);
                try {
                    String sourceString = "<b>" + friend.fetchIfNeeded().getUsername() + "</b> " +
                            " added you as a friend!";
                    friendRequestViewHolder.tvUsername.setText(Html.fromHtml(sourceString));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                ParseFile pfile = friend.getParseFile("image");
                Util.setImage(pfile, context.getResources(), friendRequestViewHolder.ivProfile, R.color.orange);
                friendRequestViewHolder.btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notificationsFragment.progressBar.setVisibility(View.VISIBLE);
                        addFriend(friend, updatedPos2);
                        notificationsFragment.setNotificationHeader();
                        notificationsFragment.progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                friendRequestViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notificationsFragment.progressBar.setVisibility(View.VISIBLE);
                        deleteSentRequest(updatedPos2);
                        notificationsFragment.progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                friendRequestViewHolder.btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notificationsFragment.progressBar.setVisibility(View.VISIBLE);
                        deleteSentRequest(updatedPos2);
                        notificationsFragment.progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if (position >= mTextNotifications.size() + mGoals.size()) {
            return 2;
        } else if (position >= mTextNotifications.size()) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return mTextNotifications.size() + mGoals.size() + mFriends.size();
    }

    public void deleteTextNotification(final int position) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("TextNotification");
        query.whereEqualTo("objectId", mTextNotifications.get(position).getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                try {
                    if (object != null) {
                        object.delete();
                        object.saveInBackground();
                    }
                    mTextNotifications.remove(position);

                    notifyDataSetChanged();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
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
                        Toast.makeText(context, "You are now goal buddies!", Toast.LENGTH_LONG).show();
                        deleteGoalRequest(position, true);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.i("Search Friend Activity", "Failed to update object, with error code: " + e.toString());
                }
            }
        });
    }

    public void moveUser(Goal goal) {
        try {
            goal = goal.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<ParseUser> approved = goal.getApprovedUsers();
//        try {
//            ParseObject.fetchAll(approved);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        approved.add(ParseUser.getCurrentUser());
        goal.setApprovedUsers(approved);
        removeUserfromPending(goal);
    }

    public void deleteGoalRequest(final int position, final boolean sendNotif) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GoalRequests");
        query.whereEqualTo("objectId", goalRequests.get(position).getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                try {
                    GoalRequests request = (GoalRequests) object;
                    ParseUser fromUser = request.getFromUser();
                    if (sendNotif && fromUser != null) {
                        String textNotification = String.format("%s accepted your goal request!", ParseUser.getCurrentUser().fetchIfNeeded().getUsername());
                        List<ParseObject> story = mGoals.get(position).getStory();
                        ParseFile image = null;
                        if (story.size() > 0) {
                            image = story.get(story.size() - 1).getParseFile("image");
                        }
                        sendTextNotification(textNotification, fromUser, image);
                    }
                    object.delete();
                    object.saveInBackground();
                    goalRequests.remove(position);
                    mGoals.remove(position);
//                    GoalRequestsFragment frag = (GoalRequestsFragment) ((NotificationsActivity) context).pagerAdapter.getCurrentFragment();
//                    if (goalRequests.size() > 0) {
//                        frag.noGoalsPage.setVisibility(View.GONE);
//                    } else {
//                        frag.noGoalsPage.setVisibility(View.VISIBLE);
//                    }
//                    NavigationView navigationView = ((NotificationsActivity) context).navigationView;
                    notifyDataSetChanged();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void removeUserfromPending(Goal goal) {
        try {
            goal = goal.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<ParseUser> pending = goal.getPendingUsers();
//        try {
//            ParseObject.fetchAll(pending);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        pending.remove(ParseUser.getCurrentUser());
        goal.setPendingUsers(pending);
        goal.saveInBackground();
    }

    public void removeUserfromFriends(Goal goal, int position) {
        try {
            goal = goal.fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<ParseUser> friends = goal.getFriends();
//        try {
//            ParseObject.fetchAll(friends);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        friends.remove(ParseUser.getCurrentUser());
        goal.setFriends(friends);
        goal.saveInBackground();
        deleteGoalRequest(position, false);
        removeUserfromPending(goal);
    }

    public void addFriend(final ParseUser user, final int position) {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        List<ParseUser> friends = currentUser.getList("friends");
        friends.add(0, user);
        currentUser.put("friends", friends);
        ApprovedFriendRequests request = new ApprovedFriendRequests(user, currentUser);
        request.saveInBackground();
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
                        Toast.makeText(context, "You are now friends", Toast.LENGTH_LONG).show();
                        deleteSentRequest(position);
                        String textNotification = String.format("%s accepted your friend request!", currentUser.fetchIfNeeded().getUsername());
                        ParseFile profilePicture = currentUser.getParseFile("image");
                        sendTextNotification(textNotification, user, profilePicture);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.i("Search Friend Activity", "Failed to update object, with error code: " + e.toString());
                }
            }
        });
    }

    public void deleteSentRequest(final int position) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SentFriendRequests");
        query.whereEqualTo("objectId", friendRequests.get(position).getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                try {
                    object.delete();
                    object.saveInBackground();
                    friendRequests.remove(position);
                    mFriends.remove(position);
//                    FriendRequestsFragment frag = (FriendRequestsFragment) ((MainActivity) context).pagerAdapter.getCurrentFragment();
//                    if (friendRequests.size() > 0) {
//                        frag.noFriendsPage.setVisibility(View.GONE);
//                    } else {
//                        frag.noFriendsPage.setVisibility(View.VISIBLE);
//                    }
//                    NavigationView navigationView = ((MainActivity) context).navigationView;
                    notifyDataSetChanged();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void sendTextNotification(String text, ParseUser user, ParseFile image) {
        TextNotification notification = new TextNotification(text, user, image);
        notification.saveInBackground();
    }

    class GoalRequestViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvGoalTitle) TextView tvGoalTitle;
        @BindView(R.id.ivStory) ImageView ivStory;
        @BindView(R.id.btnConfirm) Button btnConfirm;
        @BindView(R.id.btnDelete) Button btnDelete;
        @BindView(R.id.btnClose) ImageButton btnClose;

        public GoalRequestViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.ivProfile) ImageView ivProfile;
        @BindView(R.id.btnConfirm) Button btnConfirm;
        @BindView(R.id.btnDelete) Button btnDelete;
        @BindView(R.id.btnClose) ImageButton btnClose;

        public FriendRequestViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TextViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvText) TextView tvText;
        @BindView(R.id.ivImage) ImageView ivImage;
        @BindView(R.id.btnClose) ImageButton btnClose;

        public TextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
