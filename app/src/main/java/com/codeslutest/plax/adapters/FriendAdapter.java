package com.codeslutest.plax.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.codeslutest.plax.fragments.ContactFragment;
import com.codeslutest.plax.fragments.HistroyFragment;

public class FriendAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public FriendAdapter(FragmentManager fm, int NoofTabs) {
        super(fm);
        this.mNumOfTabs = NoofTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ContactFragment contactFragment = new ContactFragment();
                return contactFragment;
            case 1:
                HistroyFragment histroyFragment = new HistroyFragment();
                return histroyFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
