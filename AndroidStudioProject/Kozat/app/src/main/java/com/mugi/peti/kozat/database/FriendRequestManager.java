package com.mugi.peti.kozat.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendRequestManager
{
    FirebaseDatabase database;
    DatabaseReference friendRequestReference;
    public FriendRequestManager()
    {
        database = FirebaseDatabase.getInstance();
        friendRequestReference = database.getReference().child("FriendRequest");
    }

    public void sendFriendRequest(String senderUid, String receiverUid)
    {
        friendRequestReference.child(senderUid).child("Sent").child(receiverUid).setValue(true);
        friendRequestReference.child(receiverUid).child("Received").child(senderUid).setValue(true);
    }

    public void removeFriendRequest(String senderUid, String receiverUid)
    {
        friendRequestReference.child(senderUid).child("Sent").child(receiverUid).removeValue();
        friendRequestReference.child(receiverUid).child("Received").child(senderUid).removeValue();
    }

    public void retrieveReceivedFriendRequests(String uid, final ValueEventListener listener)
    {
        friendRequestReference.child(uid).child("Received").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfileManager userProfileManager = new UserProfileManager();
                Iterable<DataSnapshot> senders = dataSnapshot.getChildren();
                for (DataSnapshot sender : senders) {
                    String uid = sender.getKey();
                    userProfileManager.readUserProfileFromUserId(uid, listener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
