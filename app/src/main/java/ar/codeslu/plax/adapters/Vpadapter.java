package ar.codeslu.plax.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ar.codeslu.plax.R;
import ar.codeslu.plax.fragments.Calls;
import ar.codeslu.plax.fragments.Chats;
import ar.codeslu.plax.fragments.Groups;

public class Vpadapter extends FragmentPagerAdapter {
    Context conn;
    public Vpadapter(FragmentManager fm,Context context) {
        super(fm);
        this.conn = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                Calls calls =new Calls();
                return calls;
            case  1:
                Chats chats = new Chats();
                return chats;
            case 2:
                Groups groups = new Groups();
                return groups;
            default: return null ;



        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 :
                return conn.getString(R.string.calls);
            case 1 :
                return conn.getString(R.string.chats);
            case 2 :

                return conn.getString(R.string.groups);

            default:return null;




        }




    }
}
