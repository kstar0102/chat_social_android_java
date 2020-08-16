package ar.codeslu.plax.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ar.codeslu.plax.fragments.Calls;
import ar.codeslu.plax.fragments.Chats;
import ar.codeslu.plax.fragments.ContactFragment;
import ar.codeslu.plax.fragments.DocumentFragment;
import ar.codeslu.plax.fragments.HistroyFragment;
import ar.codeslu.plax.fragments.VoiceFragment;

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
