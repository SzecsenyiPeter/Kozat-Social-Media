package com.mugi.peti.kozat.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.adapter.AddFriendAdapter;
import com.mugi.peti.kozat.database.UserProfileManager;
import com.mugi.peti.kozat.model.UserProfile;

import java.util.ArrayList;

public class SearchUsersActivity extends AppCompatActivity
{
    RecyclerView userList;
    UserProfileManager userProfileManager;
    ArrayList<UserProfile> searchResults;
    ArrayList<String> userKeys;
    AddFriendAdapter addFriendAdapter;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        userList = findViewById(R.id.searchUsersList);
        context = this.getApplicationContext();
        userProfileManager = new UserProfileManager();
        searchResults = new ArrayList<>();
        userKeys = new ArrayList<>();
        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setAdapter(null);
        final Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            String fullNameToSearch = intent.getStringExtra(SearchManager.QUERY);
            userProfileManager.searchProfilesByName(fullNameToSearch, new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot userProfiles : dataSnapshot.getChildren())
                    {
                        searchResults.add(userProfiles.getValue(UserProfile.class));
                        userKeys.add(userProfiles.getKey());
                    }
                    addFriendAdapter = new AddFriendAdapter(
                        searchResults,
                        userKeys,
                        new AddFriendAdapter.AddButtonListener() {
                            @Override
                            public void onAddButtonClicked(int positionClicked)
                            {
                                Intent showUserIntent = new Intent(context, DisplayUserProfileActivity.class);
                                showUserIntent.putExtra(DisplayUserProfileActivity.USER_TO_DISPLAY_KEY, userKeys.get(positionClicked));
                                startActivity(showUserIntent);
                            }

                            @Override
                            public void onItemLongClicked(int positionClicked) {

                            }
                        },
                            "Show profile",
                        context);
                    userList.setAdapter(addFriendAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}
