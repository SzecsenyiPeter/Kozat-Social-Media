package com.mugi.peti.kozat.adapter;

import android.content.Context;
import java.text.SimpleDateFormat;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.BoringLayout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.database.ProfilePictureManager;
import com.mugi.peti.kozat.model.Message;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int USER_MESSAGE_VIEW_TYPE = 23;
    private static final int INCOMING_MESSAGE_VIEW_TYPE = 233;


    Context context;
    ArrayList<Message> messages;
    public ArrayList<Boolean> isLastIncomingMessage;
    String userUid;

    IncomingMessageHolder lastUserInfoView;
    RecyclerView parent;

    public MessageAdapter(Context context, ArrayList<Message> messages, String userUid) {
        this.context = context;
        this.messages = messages;
        this.userUid = userUid;
        this.isLastIncomingMessage = new ArrayList<>();
        Collections.fill(isLastIncomingMessage, Boolean.FALSE);
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).senderUid.equals(userUid))
        {
            return USER_MESSAGE_VIEW_TYPE;
        } else {
            return  INCOMING_MESSAGE_VIEW_TYPE;
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == USER_MESSAGE_VIEW_TYPE)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_layout, parent, false);
            return new UserMessageHolder(view);
        }else if(viewType == INCOMING_MESSAGE_VIEW_TYPE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incoming_message_layout, parent, false);
            return new IncomingMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message messageToBind = messages.get(position);
        switch (holder.getItemViewType())
        {
            case USER_MESSAGE_VIEW_TYPE:
                ((UserMessageHolder)holder).bind(messageToBind);
                break;
            case INCOMING_MESSAGE_VIEW_TYPE:
                ((IncomingMessageHolder)holder).bind(messageToBind);
                if (isLastIncomingMessage.get(position))
                {
                    ((IncomingMessageHolder)holder).displaySenderInfo();
                } else {
                    ((IncomingMessageHolder)holder).hideSenderInfo();
                }
                break;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void updateIsLastIncomingMessage()
    {
        for (int i = 0; i < messages.size();i++)
        {
            if (!messages.get(i).senderUid.equals(userUid)){
                if (i == 0){
                    isLastIncomingMessage.set(i, Boolean.TRUE);
                }
                else if (messages.get(i - 1).senderUid.equals(userUid)){
                    isLastIncomingMessage.set(i, Boolean.TRUE);
                }
                else{
                    isLastIncomingMessage.set(i, Boolean.FALSE);
                }
            }

        }
    }

    public void moveUserInfoDisplay()
    {

    }

    private void loadProfilePictureFromDatabase(ImageView profilePictureImageView, String userUidToLoad)
    {
        ProfilePictureManager profilePictureManager = new ProfilePictureManager();
        profilePictureManager.loadProfilePictureIntoImageView(
                userUidToLoad,
                profilePictureImageView,
                context);
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    private class UserMessageHolder extends RecyclerView.ViewHolder
    {
        TextView messageText;
        public UserMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.userMessageTextView);
        }

        void bind(Message message)
        {
            messageText.setText(message.text);
        }
    }

    private class IncomingMessageHolder extends RecyclerView.ViewHolder
    {
        TextView messageText;
        ImageView profilePicture;
        TextView dateTextView;
        Message message;
        public IncomingMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.incomingMessageTextView);
            profilePicture = itemView.findViewById(R.id.incomingMessageProfilePicture);
            dateTextView = itemView.findViewById(R.id.incomingMessageDate);
        }

        void bind(Message message)
        {
            this.message = message;
            messageText.setText(message.text);

        }

        void displaySenderInfo()
        {
            profilePicture.setVisibility(View.VISIBLE);
            dateTextView.setVisibility(View.VISIBLE);
            loadProfilePictureFromDatabase(profilePicture, message.senderUid);
            long timeStamp = Long.parseLong(message.timeStamp);
            Date date = new Date(timeStamp);


                String niceDate = DateUtils.getRelativeTimeSpanString(
                        date.getTime(),
                        Calendar.getInstance().getTimeInMillis(),
                        DateUtils.MINUTE_IN_MILLIS)
                        .toString();
                dateTextView.setText(niceDate);


        }
        void hideSenderInfo()
        {
            profilePicture.setVisibility(View.INVISIBLE);
            dateTextView.setVisibility(View.GONE);
        }
    }

}
