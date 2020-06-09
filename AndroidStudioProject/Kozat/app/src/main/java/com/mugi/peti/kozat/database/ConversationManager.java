package com.mugi.peti.kozat.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ConversationManager
{
    public final static String CONVERSATIONS_REFERENCE = "Conversations";
    public final static String CONVERSATIONS_FEED_REFERENCE = "ConversationsFeed";

    public final static String NEW_MESSAGE_COUNT_KEY = "NewMessageCount";
    public final static String CONVERSATIONS_TIMESTAMP_KEY = "ConversationTimeStamp";
    public final static String LAST_MESSAGE_KEY = "LastMessage";
    public final static int HEADER_MESSAGE_INCREMENT = 0;
    public final static int HEADER_MESSAGE_NULL = 1;

    FirebaseDatabase database;
    DatabaseReference conversationsReference;
    DatabaseReference conversationFeedReference;

    String userUid;
    public ConversationManager(String userUid)
    {
        database = FirebaseDatabase.getInstance();
        conversationsReference = database.getReference(CONVERSATIONS_REFERENCE);
        conversationFeedReference = database.getReference(CONVERSATIONS_FEED_REFERENCE);
        this.userUid = userUid;
    }

    public void updateConversationHeadersOnNewMessage(final String conversationPartnerUid)
    {
        updateConversationsTimeStamps(conversationPartnerUid);
        updateHeaderNewMessageCount(conversationPartnerUid, HEADER_MESSAGE_INCREMENT);
    }

    public void updateLastMessage(String conversationPartnerUid, String message)
    {
        Map<String, Object> feedUpdatesMap = new HashMap<>();
        feedUpdatesMap.put(userUid + "/" + conversationPartnerUid + "/" + LAST_MESSAGE_KEY, message);
        feedUpdatesMap.put(conversationPartnerUid + "/" + userUid + "/" + LAST_MESSAGE_KEY, message);
        conversationFeedReference.updateChildren(feedUpdatesMap);
    }

    private void updateConversationsTimeStamps(String conversationPartnerUid)
    {
        Map<String, Object> feedUpdatesMap = new HashMap<>();
        feedUpdatesMap.put(userUid + "/" + conversationPartnerUid + "/" + CONVERSATIONS_TIMESTAMP_KEY, ServerValue.TIMESTAMP);
        feedUpdatesMap.put(conversationPartnerUid + "/" + userUid + "/" + CONVERSATIONS_TIMESTAMP_KEY, ServerValue.TIMESTAMP);
        conversationFeedReference.updateChildren(feedUpdatesMap);
    }

    public void updateHeaderNewMessageCount(String conversationPartnerUid, final int operation)
    {
        conversationFeedReference.child(conversationPartnerUid).child(userUid).child(NEW_MESSAGE_COUNT_KEY).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                int newMessageCount = 0;
                if (mutableData.getValue() != null)
                {
                    newMessageCount = mutableData.getValue(Integer.class);
                }

                switch (operation)
                {
                    case HEADER_MESSAGE_INCREMENT:
                        newMessageCount++;
                        break;
                    case HEADER_MESSAGE_NULL:
                        newMessageCount = 0;
                        break;
                }
                mutableData.setValue(newMessageCount);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("incrementMessageCount", "postTransaction:onComplete: " + databaseError);
            }
        });
    }


    String getConversationId(String conversationPartnerUid)
    {
        if (userUid.compareTo(conversationPartnerUid) > 0) return userUid + " : " + conversationPartnerUid;
        else return  conversationPartnerUid + " : " + userUid;
    }

    public void getAllConversatioionHeaders(ValueEventListener callback )
    {
        conversationFeedReference.child(userUid).addListenerForSingleValueEvent(callback);
    }
}
