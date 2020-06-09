package com.mugi.peti.kozat.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.adapter.AddFriendAdapter;
import com.mugi.peti.kozat.database.FriendManager;
import com.mugi.peti.kozat.database.FriendRequestManager;
import com.mugi.peti.kozat.model.UserProfile;

import java.util.ArrayList;


public class FriendRequestFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private RecyclerView friendRequestList;
    private AddFriendAdapter addFriendAdapter;
    private ArrayList<UserProfile> userProfiles;
    private ArrayList<String> uids;
    FriendRequestManager friendRequestManager;

    private TextView nothingToShowTextView;
    private View rootView;
    public FriendRequestFragment() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        rootView = getView();
        friendRequestList = view.findViewById(R.id.friend_request_list);
        userProfiles = new ArrayList<UserProfile>();
        uids = new ArrayList<String>();
        nothingToShowTextView = view.findViewById(R.id.nothingToShowTextView);
        friendRequestList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        friendRequestManager = new FriendRequestManager();
        addFriendAdapter = new AddFriendAdapter(
                userProfiles,
                uids,
                new AddFriendAdapter.AddButtonListener() {
                    @Override
                    public void onAddButtonClicked(int positionClicked) {
                        FriendManager friendManager = new FriendManager();
                        friendManager.makeFriends(uids.get(positionClicked), firebaseUser.getUid());
                        userProfiles.remove(positionClicked);
                        uids.remove(positionClicked);
                        addFriendAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onItemLongClicked(int positionClicked) {
                        showRemoveRequestConfirmation(positionClicked, uids.get(positionClicked), firebaseUser.getUid());
                    }
                },
                "Accept",
                getContext());
        friendRequestList.setAdapter(addFriendAdapter);

        friendRequestManager.retrieveReceivedFriendRequests(firebaseUser.getUid(), new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile profiletoAdd = dataSnapshot.getValue(UserProfile.class);
                String uidToAdd = dataSnapshot.getRef().getKey();
                userProfiles.add(profiletoAdd);
                uids.add(uidToAdd);
                addFriendAdapter.notifyDataSetChanged();
                nothingToShowTextView.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
}

    void showRemoveRequestConfirmation(final int position, final String sender, final String receiver)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Remove friend request?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        friendRequestManager.removeFriendRequest(sender, receiver);
                        userProfiles.remove(position);
                        userProfiles.remove(position);
                        uids.remove(position);
                        addFriendAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id) { }
                });

        builder.show();
    }

}
