package com.mugi.peti.kozat.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class UpvoteManager
{
    public interface  DidPersonUpvoteListener
    {
        void onUpvoteChecked(boolean didUpvote);
    }

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    public final static String POST_UPVOTE_REFERENCE = "Upvotes";
    public final static String UPVOTE_COUNT_REFERENCE = "UpvoteCount";

    public UpvoteManager()
    {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child(POST_UPVOTE_REFERENCE);
    }

    public void upvotePost(final String userToCheckUid, String postToCheckId)
    {
        databaseReference.child(postToCheckId).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                int upvoteCount = 0;
                if (mutableData.getValue() != null)
                {
                    upvoteCount = mutableData.child(UPVOTE_COUNT_REFERENCE).getValue(Integer.class);
                }
                if(mutableData.hasChild(userToCheckUid))
                {
                    upvoteCount -= 1;
                    mutableData.child(userToCheckUid).setValue(null);
                }
                else{
                    upvoteCount += 1;
                    mutableData.child(userToCheckUid).setValue(true);
                }
                mutableData.child(UPVOTE_COUNT_REFERENCE).setValue(upvoteCount);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) { }
        });
    }

    public void checkIfPersonAlreadyUpvoted(final String userToCheckUid, String postToCheckId, final DidPersonUpvoteListener didPersonUpvoteListener)
    {
        databaseReference.child(postToCheckId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                didPersonUpvoteListener.onUpvoteChecked(dataSnapshot.hasChild(userToCheckUid));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void getUpvoteCount(String postToCheckId, ValueEventListener callback)
    {
        databaseReference.child(postToCheckId).child(UPVOTE_COUNT_REFERENCE).addListenerForSingleValueEvent(callback);
    }



}
