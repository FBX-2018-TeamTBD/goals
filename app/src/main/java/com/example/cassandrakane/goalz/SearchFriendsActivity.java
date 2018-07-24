package com.example.cassandrakane.goalz;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cassandrakane.goalz.adapters.SearchFriendAdapter;
import com.example.cassandrakane.goalz.models.ApprovedFriendRequests;
import com.example.cassandrakane.goalz.models.SentFriendRequests;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.HORIZONTAL;

public class SearchFriendsActivity extends AppCompatActivity {

    SearchView searchView;
    List<ParseUser> searched;
    RecyclerView rvSearched;
    SearchFriendAdapter searchfriendAdapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);


        searchView = findViewById(R.id.searchView);
        progressBar = findViewById(R.id.progressBar);

        searched = getUsers();
        searchfriendAdapter = new SearchFriendAdapter(searched);
        rvSearched = findViewById(R.id.rvSearched);
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

    public List<ParseUser> getUsers() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        List<ParseUser> friends = null;
        try {
            friends = ParseUser.getCurrentUser().fetch().getList("friends");
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
}
