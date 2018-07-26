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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cassandrakane.goalz.adapters.SearchFriendAdapter;
import com.example.cassandrakane.goalz.models.AddGoalForm;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

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
                for (int i = 0; i < objects.size(); i++) {
                    SentFriendRequests request = objects.get(i);
                    try {
                        friendNames.add(request.getParseUser("toUser").fetch().getUsername());
                    } catch (ParseException e1) {
                        e1.printStackTrace();
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
                for (int i = 0; i < objects.size(); i++) {
                    SentFriendRequests request = objects.get(i);
                    try {
                        friendNames.add(request.getParseUser("fromUser").fetch().getUsername());
                    } catch (ParseException e1) {
                        e1.printStackTrace();
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
