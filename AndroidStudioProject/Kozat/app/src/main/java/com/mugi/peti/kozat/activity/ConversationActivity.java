package com.mugi.peti.kozat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.adapter.MessageAdapter;
import com.mugi.peti.kozat.database.Conversation;
import com.mugi.peti.kozat.database.ConversationManager;
import com.mugi.peti.kozat.model.Message;
import com.mugi.peti.kozat.utilities.StringFormatter;

import java.util.ArrayList;
import java.util.Calendar;

public class ConversationActivity extends AppCompatActivity {


    public static final String CONVERSATION_PARTNER_UID_KEY = "conversationPartnerUidKey";



    RecyclerView messageList;
    EditText messageText;
    Button sendButton;

    String conversationPartnerUid;
    String userUid;

    Conversation conversation;

    ArrayList<com.mugi.peti.kozat.model.Message> messages;
    MessageAdapter messageAdapter;

    Activity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        context = this;
        messageList = findViewById(R.id.messagesList);
        messageText = findViewById(R.id.sendMessageEditText);
        sendButton = findViewById(R.id.sendMessageButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageString = messageText.getText().toString();
                if (StringFormatter.checkIfStringIsNullOrEmpty(messageString)) return;
                messageString =  StringFormatter.removeUnnecessaryLinesFromString(messageString);
                Calendar currentDate = Calendar.getInstance();
                Message messageToSend = new Message(messageString, Long.toString(currentDate.getTimeInMillis()), userUid);
                //hideKeyboard(context);
                messageText.getText().clear();
                conversation.sendMessage(messageToSend, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }
        });

        Intent intent = getIntent();
        conversationPartnerUid = intent.getStringExtra(CONVERSATION_PARTNER_UID_KEY);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        userUid = firebaseUser.getUid();

        conversation = new Conversation(userUid, conversationPartnerUid, new ConversationManager(userUid));

        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messages, userUid);
        messageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        messageList.setAdapter(null);
        messageList.setAdapter(messageAdapter);

        conversation.setMessageFeedCallbacks(new OnOldMessageLoaded(), new OnNewMessageLoaded());

        ConversationManager nullNewMessageCount = new ConversationManager(conversationPartnerUid);
        nullNewMessageCount.updateHeaderNewMessageCount(userUid, ConversationManager.HEADER_MESSAGE_NULL);
        conversation.loadMoreMessages(30);

    }




    class OnOldMessageLoaded implements ValueEventListener
    {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            messages.add(dataSnapshot.getValue(com.mugi.peti.kozat.model.Message.class));
            messageAdapter.isLastIncomingMessage.add(false);
            messageAdapter.updateIsLastIncomingMessage();
            messageAdapter.notifyDataSetChanged();
            messageAdapter.moveUserInfoDisplay();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //TODO hibakezelés
        }
    }

    class OnNewMessageLoaded implements ValueEventListener
    {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            messages.add(0, dataSnapshot.getValue(com.mugi.peti.kozat.model.Message.class));
            messageAdapter.isLastIncomingMessage.add(0, false);
            messageAdapter.updateIsLastIncomingMessage();
            messageAdapter.notifyItemInserted(0);
            messageList.scrollToPosition(0);
            messageAdapter.moveUserInfoDisplay();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //TODO hibakezelés
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
