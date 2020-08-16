package ar.codeslu.plax.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ar.codeslu.plax.fragments.Chats;
import ar.codeslu.plax.fragments.FollowFragment;
import ar.codeslu.plax.fragments.FriendFragment;
import ar.codeslu.plax.fragments.Nofragment;
import ar.codeslu.plax.fragments.VoiceFragment;

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
