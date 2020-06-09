package com.mugi.peti.kozat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.activity.ConversationActivity;
import com.mugi.peti.kozat.database.PictureManager;
import com.mugi.peti.kozat.database.ProfilePictureManager;
import com.mugi.peti.kozat.database.UserProfileManager;
import com.mugi.peti.kozat.model.ConversationHeader;
import com.mugi.peti.kozat.model.UserProfile;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import de.hdodenhof.circleimageview.CircleImageView;


public class ConversationHeaderAdapter extends RecyclerView.Adapter<ConversationHeaderAdapter.ViewHolder>
{
    private ArrayList<ConversationHeader> conversationHeaders;
    private Context context;
    public ConversationHeaderAdapter(ArrayList<ConversationHeader> conversationHeaders, Context context)
    {
        this.conversationHeaders = conversationHeaders;
        this.context = context;
    }

    private final View.OnClickListener onClickListener = new OnConversationClickedListener();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_conversation_header, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    private RecyclerView recyclerView;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    class OnConversationClickedListener implements View.OnClickListener{
        @Override
        public void onClick(final View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
            Intent conversationIntent = new Intent(context, ConversationActivity.class);
            conversationIntent.putExtra(ConversationActivity.CONVERSATION_PARTNER_UID_KEY, conversationHeaders.get(itemPosition).userUId);
            context.startActivity(conversationIntent);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        setTextViewsAtPosition(holder, conversationHeaders.get(position));
        loadProfilePictureFromDatabase(holder.profilePictureImageView, conversationHeaders.get(position).userUId);
        if(conversationHeaders.get(position).numberOFUnread != 0){
            changeHeaderToIndicateUnreadness(holder);
        }
    }

    private void setTextViewsAtPosition(final ViewHolder holder, ConversationHeader conversationHeader)
    {
        UserProfileManager userProfileManager = new UserProfileManager();
        userProfileManager.readUserProfileFromUserId(conversationHeader.userUId, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                holder.nameTextView.setText(userProfile.fullName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO hibakezelés
            }
        });

        long timeStamp = Long.parseLong(conversationHeader.dateToDisplay);
        Date date = new Date(timeStamp);
        String niceDate = DateUtils.getRelativeTimeSpanString(
                date.getTime(),
                Calendar.getInstance().getTimeInMillis(),
                DateUtils.MINUTE_IN_MILLIS)
                .toString();
        holder.dateTextView.setText(niceDate);
        holder.lastTextView.setText(conversationHeader.lastMessage);
    }

    private void changeHeaderToIndicateUnreadness(ViewHolder holder)
    {
        holder.lastTextView.setTypeface(null, Typeface.BOLD);
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
        return conversationHeaders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameTextView;
        TextView dateTextView;
        ImageView profilePictureImageView;
        TextView lastTextView;
        public ViewHolder(View itemView)
        {
            super(itemView);
            getAllChildViewsFromParentView();
        }

        private void getAllChildViewsFromParentView()
        {
            nameTextView = itemView.findViewById(R.id.ConversationHeaderName);
            dateTextView = itemView.findViewById(R.id.ConversationHeaderDate);
            profilePictureImageView = itemView.findViewById(R.id.ConversationHeaderProfilePicture);
            lastTextView = itemView.findViewById(R.id.ConversationHeaderLastText);
        }
    }
}
