package com.codeslutest.plax.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.codeslutest.plax.fragments.Chats;
import com.codeslutest.plax.fragments.FollowFragment;
import com.codeslutest.plax.fragments.FriendFragment;
import com.codeslutest.plax.fragments.Nofragment;

public class MainAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public MainAdapter(FragmentManager fm, int NoofTabs) {
        super(fm);
        this.mNumOfTabs = NoofTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Chats chat = new Chats();
                return chat;
            case 1:
                FriendFragment friendFragment = new FriendFragment();
                return friendFragment;
            case 2:
                Nofragment nofragment = new Nofragment();
                return nofragment;
            case 3:
                FollowFragment followFragment = new FollowFragment();
                return followFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
