package com.example.cassandrakane.goalz;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cassandrakane.goalz.adapters.SearchFriendAdapter;
import com.example.cassandrakane.goalz.models.AddGoalForm;
import com.example.cassandrakane.goalz.models.Goal;
import com.example.cassandrakane.goalz.models.GoalRequests;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.DividerItemDecoration.HORIZONTAL;

public class SearchFriendsActivity extends AppCompatActivity {

    List<ParseUser> searched;
    SearchFriendAdapter searchfriendAdapter;
    AddGoalForm form;

    @BindView(R.id.searchView) SearchView searchView;
    @Nullable @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.rvSearched) RecyclerView rvSearched;
    @BindView(R.id.ivConfirmBackground) ImageView ivConfirmBackground;
    @BindView(R.id.btnConfirm) ImageButton btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        ButterKnife.bind(this);

        String requestActivityName = getIntent().getStringExtra("requestActivity");
        form = Parcels.unwrap(getIntent().getParcelableExtra("form"));
        List<ParseUser> selectedUsers = new ArrayList<ParseUser>();
        if (form != null && form.getSelectedFriends() != null) {
            selectedUsers = form.getSelectedFriends();
        }

        if (requestActivityName.equals(AddGoalActivity.class.getSimpleName())) {
            searched = getFriends();
            ivConfirmBackground.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.VISIBLE);

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), AddGoalActivity.class);
                    form.setSelectedFriends(searchfriendAdapter.selectedFriends);
                    i.putExtra("form", Parcels.wrap(form));
                    startActivity(i);
                    finish();
                }
            });
        }
        if (requestActivityName.equals(FriendsModalActivity.class.getSimpleName())) {
            final Goal goal = getIntent().getParcelableExtra(Goal.class.getSimpleName());

            searched = getNonPendingFriends(goal);
            ivConfirmBackground.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.VISIBLE);

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<ParseUser> newPending = goal.getPendingUsers();
                    newPending.addAll(searchfriendAdapter.selectedFriends);
                    goal.setPendingUsers(newPending);
                    List<ParseUser> newFriends = goal.getFriends();
                    newFriends.addAll(searchfriendAdapter.selectedFriends);
                    goal.setFriends(newFriends);
                    goal.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            sendGoalRequest(goal, searchfriendAdapter.selectedFriends);
                            Toast.makeText(SearchFriendsActivity.this,  searchfriendAdapter.selectedFriends.size() > 1 ? "Sent requests to friends!" : "Sent request to friend!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
            });
        }
        if (requestActivityName.equals(FeedActivity.class.getSimpleName())) {
            searched = getUsers();
            ivConfirmBackground.setVisibility(View.INVISIBLE);
            btnConfirm.setVisibility(View.INVISIBLE);
        }
        searchfriendAdapter = new SearchFriendAdapter(searched, selectedUsers, requestActivityName);
        rvSearched.setLayoutManager(new LinearLayoutManager(this));
        rvSearched.setAdapter(searchfriendAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, HORIZONTAL);
        rvSearched.addItemDecoration(itemDecor);

        TextView searchText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        Typeface typeface = getResources().getFont(R.font.quicksand_regular);
        searchText.setTypeface(typeface);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchfriendAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    public List<ParseUser> getFriends() {
        List<ParseUser> friends = null;
        try {
            friends = ParseUser.getCurrentUser().fetch().getList("friends");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public List<ParseUser> getNonPendingFriends(Goal goal) {
        List<ParseUser> friends = getFriends();
        List<ParseUser> pending = goal.getPendingUsers();
        for(int i = friends.size() - 1; i >= 0; i--) {
            if (pending.contains(friends.get(i)) || friends.get(i).getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                friends.remove(i);
            }
        }
        return friends;
    }

    public void sendGoalRequest(Goal goal, List<ParseUser> pending) {
        for (int i = 0; i < pending.size(); i++) {
            Log.i("sdf", pending.get(i).getUsername());
            GoalRequests request = new GoalRequests(pending.get(i), (Goal) goal);
            request.saveInBackground();
        }
    }

    public List<ParseUser> getUsers() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        List<ParseUser> friends = getFriends();
        final List<ParseUser> users = new ArrayList<>();
        List<String> friendUsernames = new ArrayList<>();
        if (friends != null) {
            for (int i = 0; i < friends.size(); i++) {
                friendUsernames.add(friends.get(i).getUsername());
            }
        }
        final List<String> friendNames = friendUsernames;
        ParseQuery<SentFriendRequests> query2 = ParseQuery.getQuery("SentFriendRequests");
        query2.include("toUser");
        query2.include("fromUser");
        query2.whereEqualTo("fromUser", ParseUser.getCurrentUser());
        query2.findInBackground(new FindCallback<SentFriendRequests>() {
            @Override
            public void done(List<SentFriendRequests> objects, ParseException e) {
                if (objects != null) {
                    for (int i = 0; i < objects.size(); i++) {
                        SentFriendRequests request = objects.get(i);
                        try {
                            friendNames.add(request.getParseUser("toUser").fetch().getUsername());
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        ParseQuery<SentFriendRequests> query3 = ParseQuery.getQuery("SentFriendRequests");
        query3.include("toUser");
        query3.include("fromUser");
        query3.whereEqualTo("toUser", ParseUser.getCurrentUser());
        query3.findInBackground(new FindCallback<SentFriendRequests>() {
            @Override
            public void done(List<SentFriendRequests> objects, ParseException e) {
                if (objects != null) {
                    for (int i = 0; i < objects.size(); i++) {
                        SentFriendRequests request = objects.get(i);
                        try {
                            friendNames.add(request.getParseUser("fromUser").fetch().getUsername());
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (objects != null) {
                    for (int i = 0; i < objects.size(); i++) {
                        if (!ParseUser.getCurrentUser().getUsername().equals(objects.get(i).getUsername())
                                && !friendNames.contains(objects.get(i).getUsername())) {
                            users.add(objects.get(i));
                        }
                    }
                }
            }
        });
        return users;
    }

    @Override
    public void onBackPressed() {
        if (form != null) {
            Intent i = new Intent(getApplicationContext(), AddGoalActivity.class);
            i.putExtra("form", Parcels.wrap(form));
            startActivity(i);
        }
        finish();
    }
}
