package com.codeslutest.plax.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.codeslutest.plax.fragments.Chats;

public class FollowAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public FollowAdapter(FragmentManager fm, int NoofTabs) {
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
              Chats chat1 = new Chats();
                return chat1;
            case 2:
                Chats chat2 = new Chats();
                return chat2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
