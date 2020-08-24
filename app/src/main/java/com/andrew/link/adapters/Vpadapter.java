package com.andrew.link.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

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