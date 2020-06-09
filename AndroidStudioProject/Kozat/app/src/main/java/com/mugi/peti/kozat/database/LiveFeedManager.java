package com.mugi.peti.kozat.database;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class LiveFeedManager
{
    interface FeedItemLoadedListener
    {
        public void onOldFeedItemLoaded(String keyOfValueItemLoaded);
        public void onNewFeedItemLoaded(String keyOfValueItemLoaded);
    }

    private FirebaseDatabase database;
    private DatabaseReference feedItemsParentReference;
    FeedItemLoadedListener feedItemLoadedListener;

    private String lastFeedItemRetreivedKey;
    private String firstFeedItemRetreivedKey;

    public LiveFeedManager(DatabaseReference feedItemsParentReference, final FeedItemLoadedListener feedItemLoadedListener)
    {
        database = FirebaseDatabase.getInstance();
        this.feedItemsParentReference = feedItemsParentReference;
        this.feedItemLoadedListener = feedItemLoadedListener;

        feedItemsParentReference.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                final ArrayList<String> feedItemToLoadKeys = new ArrayList<>();
                Iterable<DataSnapshot> feedItemKeys = dataSnapshot.getChildren();
                for (DataSnapshot dataSnapshots: feedItemKeys){
                    feedItemToLoadKeys.add(dataSnapshots.getKey());
                }
                Collections.reverse(feedItemToLoadKeys);
                for(String feedItemToLoad : feedItemToLoadKeys)
                {
                   feedItemLoadedListener.onOldFeedItemLoaded(feedItemToLoad);
                }
                if(feedItemToLoadKeys.size() != 0)
                {
                    firstFeedItemRetreivedKey = feedItemToLoadKeys.get(0);
                } else {
                    firstFeedItemRetreivedKey = " ";
                }
                if (feedItemToLoadKeys.size() > 1){
                    lastFeedItemRetreivedKey = feedItemToLoadKeys.get(feedItemToLoadKeys.size() - 1);
                } else{
                    lastFeedItemRetreivedKey = " ";
                }
                setNewFeedItemLoadedCallback();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void setNewFeedItemLoadedCallback()
    {
        feedItemsParentReference.orderByKey().startAt(firstFeedItemRetreivedKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.exists() || dataSnapshot.getKey().equals(firstFeedItemRetreivedKey)) return;
                feedItemLoadedListener.onNewFeedItemLoaded(dataSnapshot.getKey());
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

    public void loadMoreFeedItems(int numberOfFeedItemsToLoad){

    }

}
