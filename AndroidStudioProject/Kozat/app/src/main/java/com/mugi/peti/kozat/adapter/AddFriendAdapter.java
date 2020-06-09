package com.mugi.peti.kozat.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.database.ProfilePictureManager;
import com.mugi.peti.kozat.model.UserProfile;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.ViewHolder>
{

    ArrayList<UserProfile> userProfiles;
    ArrayList<String> userUids;
    AddButtonListener addButtonListener;
    String buttonTitle;
    Context context;

    public interface AddButtonListener
    {
        void onAddButtonClicked(int positionClicked);
        void onItemLongClicked(int positionClicked);
    }

    public AddFriendAdapter(ArrayList<UserProfile> userProfiles, ArrayList<String> userUids, AddButtonListener addButtonListener, String buttonTitle, Context context)
    {
        this.userProfiles = userProfiles;
        this.addButtonListener = addButtonListener;
        this.userUids = userUids;
        this.buttonTitle = buttonTitle;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_add_friend, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, buttonTitle, addButtonListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position)
    {
        ProfilePictureManager profilePictureManager = new ProfilePictureManager();
        holder.fullNameTextView.setText(userProfiles.get(position).fullName);
        profilePictureManager.loadProfilePictureIntoImageView(userUids.get(position), holder.profileImageView, context);
    }

    @Override
    public int getItemCount() {
        return userProfiles.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView fullNameTextView;
        ImageView profileImageView;
        ConstraintLayout parent;
        Button addFriendButton;
        View itemView;

        public ViewHolder(View itemView, final String buttonTitle, final AddButtonListener addButtonListener)
        {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.addFriendItemFullName);
            profileImageView = itemView.findViewById(R.id.addFriendItemProfilePicture);
            addFriendButton = itemView.findViewById(R.id.addFriendItemAdd);
            parent = itemView.findViewById(R.id.addFriendItemParent);
            addFriendButton.setText(buttonTitle);
            addFriendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addButtonListener.onAddButtonClicked(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    addButtonListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
