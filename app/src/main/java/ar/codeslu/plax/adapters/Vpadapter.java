package ar.codeslu.plax.adapters;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import ar.codeslu.plax.R;
import ar.codeslu.plax.fragments.Calls;
import ar.codeslu.plax.fragments.Chats;
import ar.codeslu.plax.fragments.Groups;

public class Vpadapter extends FragmentPagerAdapter {
    private List<String> NamePage = new ArrayList<>(); // Fragment Name List
    public  ArrayList<androidx.fragment.app.Fragment> Fragment = new ArrayList<>();



    public Vpadapter(FragmentManager fm) {
        super(fm);


    }

    public void add(Fragment Frag, String Title) {
        Fragment.add(Frag);
        NamePage.add(Title);
    }
    @Override
    public Fragment getItem(int position) {
        return Fragment.get(position);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return NamePage.get(position);
    }
    @Override
    public int getCount() {
        return Fragment.size();
    }
}