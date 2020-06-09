package com.mugi.peti.kozat.adapter;

import android.content.Context;
import android.content.res.Resources;
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
import com.mugi.peti.kozat.database.PictureManager;
import com.mugi.peti.kozat.database.ProfilePictureManager;
import com.mugi.peti.kozat.database.UpvoteManager;
import com.mugi.peti.kozat.database.UserProfileManager;
import com.mugi.peti.kozat.model.UserPost;
import com.mugi.peti.kozat.model.UserProfile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public interface UpvoteClickListener
    {
        void onUpvoteClicked(int position);
    }

    private ArrayList<UserPost> userPosts;
    private ArrayList<String> postKeys;
    private Runnable loadMoreCallback;
    private String userUid;
    private Context context;

    private RecyclerView parent;
    private UpvoteClickListener upvoteClickListener;
    static Resources mResources;

    public PostAdapter(ArrayList<UserPost> userPosts, ArrayList<String> postKeys, String userUid, Runnable loadMoreCallback, Context context)
    {
        this.userPosts = userPosts;
        this.postKeys = postKeys;
        this.loadMoreCallback = loadMoreCallback;
        this.userUid = userUid;
        this.context = context;
        mResources = context.getResources();

    }

    public void setOnUpvoteClickListener(UpvoteClickListener upvoteClickListener)
    {
        this.upvoteClickListener = upvoteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parent = recyclerView;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final int fixed = position;

        holder.postTextView.setText(userPosts.get(position).text);


        try {
            long timeStamp = Long.parseLong(userPosts.get(position).date);
            Date date = new Date(timeStamp);
            String niceDate = DateUtils.getRelativeTimeSpanString(
                    date.getTime(),
                    Calendar.getInstance().getTimeInMillis(),
                    DateUtils.MINUTE_IN_MILLIS)
                    .toString();
            holder.dateTextView.setText(niceDate);
        }
        catch (NumberFormatException e)
        {
            holder.dateTextView.setText(userPosts.get(position).date);
        }
        if(userPosts.get(position).withImage){
            PictureManager pictureManager = new PictureManager(PictureManager.POST_PICTURE_ROOT);
            holder.postPictureImageView.setVisibility(View.VISIBLE);
            pictureManager.loadPictureIntoImageView(postKeys.get(fixed), holder.postPictureImageView, context);
        }
        else {
            holder.postPictureImageView.setVisibility(View.GONE);
            }

        UserProfileManager userProfileManager = new UserProfileManager();
        userProfileManager.readUserProfileFromUserId(userPosts.get(position).posterUid, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                holder.profileNameTextView.setText(userProfile.fullName);
                ProfilePictureManager profilePictureManager = new ProfilePictureManager();
                profilePictureManager.loadProfilePictureIntoImageView(userPosts.get(fixed).posterUid, holder.profilePictureImageView, context );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO hibakezelés
            }
        });

        final UpvoteManager upvoteManager = new UpvoteManager();
        upvoteManager.getUpvoteCount(postKeys.get(position), new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int upvoteCount = 0;
                if(dataSnapshot.getValue() != null)
                {
                    upvoteCount = dataSnapshot.getValue(Integer.class);
                }
                holder.likesTextView.setText(upvoteCount + " upvotes");

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        upvoteManager.checkIfPersonAlreadyUpvoted(userUid, postKeys.get(position), new UpvoteManager.DidPersonUpvoteListener() {
            @Override
            public void onUpvoteChecked(boolean didUpvote) {
                holder.isAlreadyUpvoted = didUpvote;
                if (didUpvote){
                    holder.likeImageView.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                }
            }
        });

        if(position == this.getItemCount() - 1)
        {
            loadMoreCallback.run();
        }

        holder.setOnUpvoteClickedListener(upvoteClickListener);

    }


    @Override
    public int getItemCount() {
        return userPosts.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView postTextView;
        TextView profileNameTextView;
        TextView likesTextView;
        TextView dateTextView;
        ImageView profilePictureImageView;
        ImageView postPictureImageView;
        ImageView likeImageView;
        public boolean isAlreadyUpvoted;

        ViewHolder holder;
        ViewHolder(View itemView) {
            super(itemView);
            profilePictureImageView = itemView.findViewById(R.id.PostListProfilePicture);
            profileNameTextView= itemView.findViewById(R.id.PostListName);
            postTextView = itemView.findViewById(R.id.PostListText);
            likesTextView = itemView.findViewById(R.id.PostListLikesTextView);
            dateTextView = itemView.findViewById(R.id.PostListDateTextView);
            postPictureImageView = itemView.findViewById(R.id.PostListImage);
            postPictureImageView = itemView.findViewById(R.id.PostListImage);
            likeImageView = itemView.findViewById(R.id.PostListLikeImageView);
            holder = this;
        }

        public void setOnUpvoteClickedListener(final UpvoteClickListener onClickListener)
        {
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyUpvoteStateChanged();
                    onClickListener.onUpvoteClicked(holder.getAdapterPosition());
                }
            });


        }

        void notifyUpvoteStateChanged(){
            if (isAlreadyUpvoted){
                isAlreadyUpvoted = false;

                int newUpvoteCount = Integer.parseInt( likesTextView.getText().toString().split(" ")[0]) - 1;
                likeImageView.clearColorFilter();
                likesTextView.setText(Integer.toString(newUpvoteCount) + " upvotes");
            }
            else{
                isAlreadyUpvoted = true;
                holder.likeImageView.setColorFilter(mResources.getColor(R.color.colorAccent));
                int newUpvoteCount = Integer.parseInt( likesTextView.getText().toString().split(" ")[0]) + 1;
                likesTextView.setText(Integer.toString(newUpvoteCount) + " upvotes");
            }
        }

    }
}
