package com.mugi.peti.kozat.database;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mugi.peti.kozat.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class FriendManager
{
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    public FriendManager()
    {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("FriendInfo");
    }

    public void makeFriends(String userOneId, String userTwoId) {
        databaseReference.child(userOneId).child(userTwoId).setValue(true);
        databaseReference.child(userTwoId).child(userOneId).setValue(true);
        FriendRequestManager friendRequestManager = new FriendRequestManager();
        friendRequestManager.removeFriendRequest(userOneId, userTwoId);
        Conversation welcomeConversation = new Conversation(userTwoId, userOneId, new ConversationManager(userTwoId));
        String dateFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
        Calendar currentDate = Calendar.getInstance();

        Message messageToSend = new Message("You two are now friend on Kozat!" +
                " Say hello", Long.toString(currentDate.getTimeInMillis()), userOneId);

        welcomeConversation.sendMessage(messageToSend, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }


    public void retrieveFriendProfiles(String uid, final ValueEventListener callback)
    {
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfileManager userProfileManager = new UserProfileManager();
                Iterable<DataSnapshot> friends = dataSnapshot.getChildren();
                for (DataSnapshot friend : friends) {
                    if (friend.getValue(Boolean.class)){

                        String uid = friend.getKey();
                        userProfileManager.readUserProfileFromUserId(uid, callback);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void removeFriend(String userOneId, String userTwoId){
        databaseReference.child(userOneId).child(userTwoId).setValue(false);
        databaseReference.child(userTwoId).child(userOneId).setValue(false);
    }

    public void retrieveFriendUids(String uid, final OnFriendListRetrievedListener callback)
    {
        final ArrayList<String> friendUids = new ArrayList<>();
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfileManager userProfileManager = new UserProfileManager();
                Iterable<DataSnapshot> friends = dataSnapshot.getChildren();
                for (DataSnapshot friend : friends) {
                    friendUids.add(friend.getKey());

                }
                callback.onAllUidsGathered(friendUids);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public interface OnFriendListRetrievedListener
    {
        void onAllUidsGathered(ArrayList<String> friendUids);
    }

}
