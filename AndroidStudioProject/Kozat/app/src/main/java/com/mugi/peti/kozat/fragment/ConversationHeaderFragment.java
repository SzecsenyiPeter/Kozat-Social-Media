package com.mugi.peti.kozat.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.adapter.ConversationHeaderAdapter;
import com.mugi.peti.kozat.database.ConversationManager;
import com.mugi.peti.kozat.database.UserProfileManager;
import com.mugi.peti.kozat.model.ConversationHeader;

import java.util.ArrayList;

import static com.mugi.peti.kozat.database.ConversationManager.CONVERSATIONS_TIMESTAMP_KEY;
import static com.mugi.peti.kozat.database.ConversationManager.LAST_MESSAGE_KEY;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationHeaderFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private RecyclerView headerList;

    private ArrayList<ConversationHeader> conversationHeadersToAdapt;
    private ConversationHeaderAdapter conversationHeaderAdapter;

    public ConversationHeaderFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation_header, container, false);
        headerList = view.findViewById(R.id.conversationHeaderList);
        conversationHeadersToAdapt = new ArrayList<>();
        getDataFromServer();
        return view;

    }

    private void getDataFromServer()
    {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        ConversationManager conversationManager = new ConversationManager(firebaseUser.getUid());
        conversationManager.getAllConversatioionHeaders(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadConversationHeaderFromDatasnapshot(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO baj
            }
        });
    }

    private void loadConversationHeaderFromDatasnapshot(DataSnapshot dataSnapshot)
    {
        for (DataSnapshot child :
             dataSnapshot.getChildren()) {
            conversationHeadersToAdapt.add(parseConversationHeaderFromChild(child));
        }
        setupListWithAdapter();
    }

    private void setupListWithAdapter()
    {
        conversationHeaderAdapter = new ConversationHeaderAdapter(conversationHeadersToAdapt, getContext());
        headerList.setLayoutManager(new LinearLayoutManager(getContext()));
        headerList.setAdapter(conversationHeaderAdapter);
    }

    private ConversationHeader parseConversationHeaderFromChild(DataSnapshot snapshot)
    {
        String partnerKey = snapshot.getKey();
        String serverTimeStamp = "0";
        if (snapshot.child(CONVERSATIONS_TIMESTAMP_KEY).getValue() != null)
        {
            serverTimeStamp = snapshot.child(CONVERSATIONS_TIMESTAMP_KEY).getValue().toString();
        }
        String lastText = " ";
        if (snapshot.child(LAST_MESSAGE_KEY).getValue() != null)
        {
            lastText = snapshot.child(LAST_MESSAGE_KEY).getValue().toString();
        }

        int numberOfUnreadMessages = 0;
        if (snapshot.child(ConversationManager.NEW_MESSAGE_COUNT_KEY).getValue() != null)
        {
            numberOfUnreadMessages = snapshot.child(ConversationManager.NEW_MESSAGE_COUNT_KEY).getValue(Integer.class);
        }
        return new ConversationHeader(partnerKey, serverTimeStamp,numberOfUnreadMessages, lastText);
    }

}
