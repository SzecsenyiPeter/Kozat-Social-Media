package com.mugi.peti.kozat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.database.ProfilePictureManager;
import com.mugi.peti.kozat.database.UserProfileManager;
import com.mugi.peti.kozat.model.UserProfile;

public class ShowProfileFragment extends Fragment
{
    ImageView profilePicture;
    TextView fullName;
    TextView birthDate;
    FloatingActionButton AddFriend;

    String userToDisplayUID;
    UserProfile userProfile;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_display_user_profile, container, false);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        profilePicture = view.findViewById(R.id.showProfileImage);
        fullName = view.findViewById(R.id.showProfileFullName);
        birthDate = view.findViewById(R.id.showProfileDate);
        AddFriend = view.findViewById(R.id.showProfileAddButton);
        AddFriend.setVisibility(View.GONE);
        userToDisplayUID = firebaseUser.getUid();
        UserProfileManager userProfileManager = new UserProfileManager();
        userProfileManager.readUserProfileFromUserId(userToDisplayUID, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(UserProfile.class);
                fullName.setText(userProfile.fullName);
                birthDate.setText(userProfile.birthDate);
                ProfilePictureManager profilePictureManager = new ProfilePictureManager();
                profilePictureManager.loadProfilePictureIntoImageView(userToDisplayUID, profilePicture, getContext());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }
}
