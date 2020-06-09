package com.mugi.peti.kozat.activity;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.database.FriendManager;
import com.mugi.peti.kozat.database.FriendRequestManager;
import com.mugi.peti.kozat.database.ProfilePictureManager;
import com.mugi.peti.kozat.database.UserPostManager;
import com.mugi.peti.kozat.database.UserProfileManager;
import com.mugi.peti.kozat.fragment.FriendFeedFragment;
import com.mugi.peti.kozat.model.UserProfile;

import java.util.ArrayList;


public class DisplayUserProfileActivity extends AppCompatActivity
{
    public static String USER_TO_DISPLAY_KEY = "userID";
    public static String IS_FRIEND_KEY = "isFriend";

    ImageView profilePicture;
    ImageView blurryBackground;
    TextView fullName;
    TextView birthDate;
    FloatingActionButton AddFriend;
    FloatingActionButton sendMessage;
    FrameLayout displayFeedFragmentFrame;

    String userToDisplayUID;
    UserProfile userProfile;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_user_profile);

        auth = FirebaseAuth.getInstance();
        context = this;
        firebaseUser = auth.getCurrentUser();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.kozat_icon_accent_round);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        profilePicture = findViewById(R.id.showProfileImage);
        fullName = findViewById(R.id.showProfileFullName);
        birthDate = findViewById(R.id.showProfileDate);
        AddFriend = findViewById(R.id.showProfileAddButton);
        sendMessage = findViewById(R.id.sendMessageToFriendButton);
        blurryBackground = findViewById(R.id.blurryProfileImage);
        displayFeedFragmentFrame = findViewById(R.id.displayUserProfileFrame);
        getSupportActionBar().setElevation(0);
        Intent intent = getIntent();
        userToDisplayUID = intent.getStringExtra(USER_TO_DISPLAY_KEY);
        boolean isFriend = intent.getBooleanExtra(IS_FRIEND_KEY, false);

        Fragment newFragment = FriendFeedFragment.newInstance(UserPostManager.FEED_TYPE_USER, userToDisplayUID);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.displayUserProfileFrame, newFragment)
                .commit();


        UserProfileManager userProfileManager = new UserProfileManager();

        if (userToDisplayUID.equals(firebaseUser.getUid()))
        {
            sendMessage.setVisibility(View.GONE);
            AddFriend.setVisibility(View.GONE);
        }
        else{
            FriendManager friendManager = new FriendManager();
            friendManager.retrieveFriendUids(firebaseUser.getUid(), new FriendManager.OnFriendListRetrievedListener() {
                @Override
                public void onAllUidsGathered(ArrayList<String> friendUids)
                {
                    if(friendUids.contains(userToDisplayUID)){
                        sendMessage.setVisibility(View.VISIBLE);
                        AddFriend.setVisibility(View.GONE);
                        sendMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent conversationIntent = new Intent(context, ConversationActivity.class);
                                conversationIntent.putExtra(ConversationActivity.CONVERSATION_PARTNER_UID_KEY, userToDisplayUID);
                                context.startActivity(conversationIntent);
                            }
                        });
                    }

                    else{
                        sendMessage.setVisibility(View.GONE);
                        AddFriend.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        userProfileManager.readUserProfileFromUserId(userToDisplayUID, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(UserProfile.class);
                fullName.setText(userProfile.fullName);
                birthDate.setText(userProfile.birthDate);
                ProfilePictureManager profilePictureManager = new ProfilePictureManager();
                profilePictureManager.loadProfilePictureIntoImageView(userToDisplayUID, profilePicture, getApplicationContext());

                Glide.with(context)
                        .using(new FirebaseImageLoader())
                        .load(profilePictureManager.profilePicturesStorageReference.child(userToDisplayUID))
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                setUpBackground(resource);
                            }


                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        AddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Friend request sent!",
                        Toast.LENGTH_LONG).show();
                AddFriend.setEnabled(false);
                AddFriend.setBackgroundColor(Color.GRAY);
                FriendRequestManager friendRequestManager = new FriendRequestManager();
                friendRequestManager.sendFriendRequest(firebaseUser.getUid(), userToDisplayUID);

            }
        });

    }

    private void setUpBackground(Bitmap resource)
    {
    }
}
