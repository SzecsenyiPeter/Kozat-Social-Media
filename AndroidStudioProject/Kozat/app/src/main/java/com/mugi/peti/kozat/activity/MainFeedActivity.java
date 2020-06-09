package com.mugi.peti.kozat.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mugi.peti.kozat.fragment.ConversationHeaderFragment;
import com.mugi.peti.kozat.fragment.FeedMainFragment;
import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.fragment.ProfileMainFragment;


public class MainFeedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    FrameLayout mainFrame;
    BottomNavigationView bottomNavigationView;

    ProfileMainFragment friendRequestFragment = new ProfileMainFragment();
    FeedMainFragment feedMainFragment = new FeedMainFragment();
    ConversationHeaderFragment conversationHeaderFragment = new ConversationHeaderFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main_feed);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.kozat_icon_accent_round);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null)
        {
            startLoginActivity();
            finish();
        }
        mainFrame = findViewById(R.id.main_frame);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_profile:
                        item.setChecked(true);
                        setFragment(friendRequestFragment);
                        return true;
                    case R.id.action_feed:
                        item.setChecked(true);
                        setFragment(feedMainFragment);
                        return true;
                    case R.id.action_messages:
                        item.setChecked(true);
                        setFragment(conversationHeaderFragment);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        switch (bottomNavigationView.getSelectedItemId()){
            case R.id.action_profile:
                setFragment(friendRequestFragment);
                break;
            case R.id.action_feed:

                setFragment(feedMainFragment);
                break;
            case R.id.action_messages:

                setFragment(conversationHeaderFragment);
                break;
        }

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_dashboard, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.dashboardMenuSearchUsers).getActionView();
        searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()) );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.dashboardMenuLogout:
                auth.signOut();
                startLoginActivity();
                return true;
            case R.id.dashboardMenuEditProfile:
                Intent loginIntent = new Intent(MainFeedActivity.this, CreateProfileActivity.class);
                startActivity(loginIntent);
                return true;
            case R.id.dashboardMenuSearchUsers:
                    onSearchRequested();
                    return true;
            case R.id.dashboardMenuOwnProfile:
                Intent displayProfileIntent = new Intent(MainFeedActivity.this, DisplayUserProfileActivity.class);
                displayProfileIntent.putExtra(DisplayUserProfileActivity.USER_TO_DISPLAY_KEY, firebaseUser.getUid());
                startActivity(displayProfileIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void startLoginActivity()
    {
        Intent loginIntent = new Intent(MainFeedActivity.this, LoginActivity.class);
        startActivity(loginIntent);

    }

    private void setFragment(android.support.v4.app.Fragment fragment)
    {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
