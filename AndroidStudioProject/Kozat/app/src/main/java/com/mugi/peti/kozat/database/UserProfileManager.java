package com.mugi.peti.kozat.database;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mugi.peti.kozat.model.UserProfile;

public class UserProfileManager
{
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    public UserProfileManager()
    {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
    }

    public void writeUserProfile(UserProfile userProfile, String userId, OnCompleteListener<Void> callback)
    {
        databaseReference.child("UserProfiles")
                .child(userId)
                .setValue(userProfile)
                .addOnCompleteListener(callback);
    }

    public void readUserProfileFromUserId(String userId, ValueEventListener callback)
    {
        databaseReference.child("UserProfiles")
                .child(userId).
                addListenerForSingleValueEvent(callback);
    }

    public void searchProfilesByName(String name, ValueEventListener callback)
    {
        databaseReference.child("UserProfiles")
                .orderByChild("fullName")
                .startAt(name).endAt(name + "\\uf8ff")
                .addValueEventListener(callback);
    }

    public void readUserProfileFromEmailAddress(String emailAddress, ValueEventListener callback)
    {
        databaseReference.child("UserProfiles")
                .orderByChild("emailAddress")
                .equalTo(emailAddress)
                .addListenerForSingleValueEvent(callback);
    }


}
