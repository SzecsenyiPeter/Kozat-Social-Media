package com.mugi.peti.kozat.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.adapter.BasicPageAdapter;
import com.mugi.peti.kozat.database.UserPostManager;
import com.mugi.peti.kozat.fragment.FriendListFragment;
import com.mugi.peti.kozat.fragment.FriendRequestFragment;
import com.mugi.peti.kozat.fragment.ShowProfileFragment;

public class FeedMainFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton newPostFab;
    public FeedMainFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_feed_main, container, false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        tabLayout = rootView.findViewById(R.id.feedMainTabLayout);
        viewPager = rootView.findViewById(R.id.feedMainViewPager);
        newPostFab  = rootView.findViewById(R.id.feedMainCreatePostFAB);

        BasicPageAdapter pageAdapter = new BasicPageAdapter(getChildFragmentManager());

        pageAdapter.addFragment(
                FriendFeedFragment.newInstance(UserPostManager.FEED_TYPE_FRIENDS, firebaseUser.getUid()),
                "Feed");
        pageAdapter.addFragment(
                FriendFeedFragment.newInstance(UserPostManager.FEED_TYPE_USER, firebaseUser.getUid()),
                "My posts");

        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        newPostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostDialogFragment postDialogFragment = new PostDialogFragment();
                postDialogFragment.show(getActivity().getFragmentManager(), "postDialog");
            }
        });
        return rootView;
    }
}
