package com.example.cassandrakane.goalz;

import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.example.cassandrakane.goalz.adapters.SearchFriendAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.HORIZONTAL;

public class SearchFriendsActivity extends AppCompatActivity {

    SearchView searchView;
    List<ParseUser> searched;
    RecyclerView rvSearched;
    SearchFriendAdapter searchfriendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        getSupportActionBar().hide();

        searchView = findViewById(R.id.searchView);

        searched = getUsers();
        searchfriendAdapter = new SearchFriendAdapter(searched);
        rvSearched = findViewById(R.id.rvSearched);
        rvSearched.setLayoutManager(new LinearLayoutManager(this));
        rvSearched.setAdapter(searchfriendAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, HORIZONTAL);
        rvSearched.addItemDecoration(itemDecor);

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
        List<ParseUser> friends = ParseUser.getCurrentUser().getList("friends");
        List<String> friendUsernames = new ArrayList<>();
        for (int i = 0; i < friends.size(); i++) {
            friendUsernames.add(friends.get(i).getUsername());
        }
        final List<String> friendNames = friendUsernames;
        final List<ParseUser> users = new ArrayList<>();
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
