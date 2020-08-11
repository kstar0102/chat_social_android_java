package ar.codeslu.plax.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ar.codeslu.plax.fragments.Chats;
import ar.codeslu.plax.fragments.FriendFragment;
import ar.codeslu.plax.fragments.Nofragment;
import ar.codeslu.plax.fragments.VoiceFragment;

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
                VoiceFragment voiceFragment = new VoiceFragment();
                return voiceFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
