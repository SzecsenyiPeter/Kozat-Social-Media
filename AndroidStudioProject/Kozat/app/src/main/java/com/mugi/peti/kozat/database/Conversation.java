package com.mugi.peti.kozat.database;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mugi.peti.kozat.model.Message;

public class Conversation
{
    FirebaseDatabase database;
    DatabaseReference conversationsReference;
    ConversationManager conversationManager;
    LiveFeedManager messageFeedManager;
    String userUid;
    String conversationParnterUid;
    public Conversation(String userUid, String conversationPartnerUid, ConversationManager conversationManager)
    {
        database = FirebaseDatabase.getInstance();
        this.conversationManager = conversationManager;
        conversationsReference = database
                .getReference(ConversationManager.CONVERSATIONS_REFERENCE)
                .child(conversationManager.getConversationId(conversationPartnerUid));
        this.userUid = userUid;
        this.conversationParnterUid = conversationPartnerUid;
    }


    public void sendMessage(Message messageToSend, OnCompleteListener<Void> callback)
    {
        conversationsReference.push().setValue(messageToSend).addOnCompleteListener(callback);
        conversationManager.updateConversationHeadersOnNewMessage(conversationParnterUid);
        conversationManager.updateLastMessage(conversationParnterUid, messageToSend.text);
    }

    public void setMessageFeedCallbacks(final ValueEventListener oldMessageLoadedCallback, final ValueEventListener newMessageLoadedCallback)
    {
        messageFeedManager = new LiveFeedManager(
                conversationsReference,
                new LiveFeedManager.FeedItemLoadedListener() {
            @Override
            public void onOldFeedItemLoaded(String keyOfValueItemLoaded) {
                readMessage(keyOfValueItemLoaded, oldMessageLoadedCallback);
            }

            @Override
            public void onNewFeedItemLoaded(String keyOfValueItemLoaded) {
                readMessage(keyOfValueItemLoaded, newMessageLoadedCallback);
            }
        });
    }
    public void loadMoreMessages(int numberOfMessagesToLoad)
    {
        messageFeedManager.loadMoreFeedItems(numberOfMessagesToLoad);
    }


    private void readMessage(String messageId, ValueEventListener callback)
    {
        conversationsReference
                .child(messageId)
                .addListenerForSingleValueEvent(callback);
    }
}
