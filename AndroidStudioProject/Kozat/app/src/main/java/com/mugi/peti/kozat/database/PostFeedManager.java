package com.mugi.peti.kozat.database;

import android.provider.ContactsContract;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class PostFeedManager {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private ValueEventListener oldPostLoadedCallBack;
    private ValueEventListener newPostLoadedCallBack;
    private String userId;
    private String feedType;

    private String lastPostRetreivedKey;
    private String firstPostRetreivedKey;
    public PostFeedManager(final String userId, String feedType, final ValueEventListener oldPostLoadedCallBack, ValueEventListener newPostLoadedCallBack)
    {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("PostFeed").child(userId).child(feedType);
        this.oldPostLoadedCallBack = oldPostLoadedCallBack;
        this.newPostLoadedCallBack = newPostLoadedCallBack;
        this.userId = userId;
        this.feedType = feedType;

        databaseReference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final ArrayList<String> postsToLoadKeys = new ArrayList<>();
                UserPostManager userPostManager = new UserPostManager();
                Iterable<DataSnapshot> postKeys = dataSnapshot.getChildren();
                for (DataSnapshot dataSnapshots: postKeys){
                    postsToLoadKeys.add(dataSnapshots.getKey());
                }
                Collections.reverse(postsToLoadKeys);
                for(String postToLoad : postsToLoadKeys)
                {
                    userPostManager.readPost(postToLoad, oldPostLoadedCallBack);
                }
                if(postsToLoadKeys.size() != 0)
                {
                    firstPostRetreivedKey = postsToLoadKeys.get(0);
                } else {
                    firstPostRetreivedKey = " ";
                }
                if (postsToLoadKeys.size() > 1){
                    lastPostRetreivedKey = postsToLoadKeys.get(postsToLoadKeys.size() - 1);
                } else{
                    lastPostRetreivedKey = " ";
                }
                setNewPostLoadedCallback();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

   private void setNewPostLoadedCallback()
   {
       databaseReference.orderByKey().startAt(firstPostRetreivedKey).addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               if (!dataSnapshot.exists() || dataSnapshot.getKey().equals(firstPostRetreivedKey)) return;
               UserPostManager userPostManager = new UserPostManager();
               userPostManager.readPost(dataSnapshot.getKey(), newPostLoadedCallBack);
           }

           @Override
           public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

           @Override
           public void onChildRemoved(DataSnapshot dataSnapshot) {}

           @Override
           public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

           @Override
           public void onCancelled(DatabaseError databaseError) {}
       });
   }

    public void loadMorePosts(int numberOfPostsToLoad){

       }

}
