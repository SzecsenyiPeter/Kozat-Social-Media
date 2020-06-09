package com.mugi.peti.kozat.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.adapter.BasicPageAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileMainFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public ProfileMainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_profile_main, container, false);
        tabLayout = rootView.findViewById(R.id.profileMainTabLayout);
        viewPager = rootView.findViewById(R.id.profileMainViewPager);
        BasicPageAdapter pageAdapter = new BasicPageAdapter(getChildFragmentManager());
        pageAdapter.addFragment(new FriendRequestFragment(), "Friend Requests");
        pageAdapter.addFragment(new FriendListFragment(), "Friends");
        //pageAdapter.addFragment(new ShowProfileFragment(), "My Profile");
        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return rootView;
    }

}
