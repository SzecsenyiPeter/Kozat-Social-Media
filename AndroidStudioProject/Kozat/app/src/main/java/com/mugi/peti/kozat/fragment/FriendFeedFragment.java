package com.mugi.peti.kozat.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.adapter.PostAdapter;
import com.mugi.peti.kozat.database.PostFeedManager;
import com.mugi.peti.kozat.database.UpvoteManager;
import com.mugi.peti.kozat.database.UserPostManager;
import com.mugi.peti.kozat.model.UserPost;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFeedFragment extends Fragment {

    private  final static String FEED_TYPE_KEY = "FeedTypeKey";
    private  final static String USER_TO_DISPLAY_KEY = "UserToDisplayKey";

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private ArrayList<UserPost> userPosts = new ArrayList<>();
    private ArrayList<String> postKeys = new ArrayList<>();

    private PostAdapter postAdapter;
    private PostFeedManager postFeedManager;

    private RecyclerView friendFeedRecyclerView;
    private ImageView scrollToTopImageView;
    private TextView scollToTopTextView;
    private TextView noPostToShowTextView;

    private String feedType;

    public FriendFeedFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_friend_feed, container, false);

        friendFeedRecyclerView = rootView.findViewById(R.id.FriendFeedRecyclerView);
        scollToTopTextView = rootView.findViewById(R.id.ScrollToTopTextView);
        scrollToTopImageView = rootView.findViewById(R.id.ScrollToTopImageView);
        noPostToShowTextView = rootView.findViewById(R.id.noPostTextView);

        feedType = UserPostManager.FEED_TYPE_FRIENDS;
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        friendFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        friendFeedRecyclerView.setAdapter(null);

        feedType = getArguments().getString(FEED_TYPE_KEY, UserPostManager.FEED_TYPE_FRIENDS);
        String userUidToDisplay = getArguments().getString(USER_TO_DISPLAY_KEY, firebaseUser.getUid());

        postAdapter = new PostAdapter(
                userPosts,
                postKeys,
                userUidToDisplay,
                new Runnable() {
                    @Override
                    public void run() {
                        postFeedManager.loadMorePosts(5);
                    }
                },
                getContext());
        postFeedManager = new PostFeedManager(
                userUidToDisplay,
                feedType,
                new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null)
                {
                    userPosts.add(dataSnapshot.getValue(UserPost.class));
                    postKeys.add(dataSnapshot.getKey());
                    postAdapter.notifyDataSetChanged();
                    friendFeedRecyclerView.setAdapter(postAdapter);
                    noPostToShowTextView.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO hibakezelés
            }
        }, new onNewPostAddedListener());

        scrollToTopImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendFeedRecyclerView.scrollToPosition(0);
                scrollToTopImageView.setVisibility(View.GONE);
                scollToTopTextView.setVisibility(View.GONE);
            }
        });

        friendFeedRecyclerView.setAdapter(postAdapter);
        final UpvoteManager upvoteManager = new UpvoteManager();
        postAdapter.setOnUpvoteClickListener(new PostAdapter.UpvoteClickListener() {
            @Override
            public void onUpvoteClicked(int position) {
                upvoteManager.upvotePost(firebaseUser.getUid(), postKeys.get(position));
            }
        });
        return rootView;
    }

    public static FriendFeedFragment newInstance(String feedType, String userUid)
    {
        FriendFeedFragment friendFeedFragment = new FriendFeedFragment();
        Bundle arguments = new Bundle();
        arguments.putString(FEED_TYPE_KEY, feedType);
        arguments.putString(USER_TO_DISPLAY_KEY, userUid);
        friendFeedFragment.setArguments(arguments);
        return  friendFeedFragment;
    }


    class onNewPostAddedListener implements ValueEventListener
    {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            UserPost postToAdd = dataSnapshot.getValue(UserPost.class);
            userPosts.add(0, postToAdd);
            postKeys.add(0, dataSnapshot.getKey());
            postAdapter.notifyItemInserted(0);
            noPostToShowTextView.setVisibility(View.GONE);
            if (postToAdd.posterUid.equals(firebaseUser.getUid()))
            {
                friendFeedRecyclerView.scrollToPosition(0);

            } else {
                scrollToTopImageView.setVisibility(View.VISIBLE);
                scollToTopTextView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            }
    }

}
