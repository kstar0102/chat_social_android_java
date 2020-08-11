package ar.codeslu.plax;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ar.codeslu.plax.auth.VerifyDelete;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.adapters.SettingAdapter;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.settings.SettingList;
import ar.codeslu.plax.settingsitems.CallsSetting;
import ar.codeslu.plax.settingsitems.ChatSettings;
import ar.codeslu.plax.settingsitems.NotifSetting;
import ar.codeslu.plax.settingsitems.SecuSetting;
/**
 * Created by CodeSlu
 */
public class Setting extends AppCompatActivity {
    //view
    ListView listView;

    //data
    ArrayList<SettingList> list;
    ArrayList<String> langcodesA;
    SettingAdapter adapter;
    String[] datalist, langcodes;
    //dialog
    AlertDialog.Builder dialog;
    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mData;
    //vars
    String choosenLang = "";
    boolean prevent = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.mainL);
        Global.currentactivity = this;

        //firebase init
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);


        //dark mode init
        if (mAuth.getCurrentUser() != null) {
            if (!((AppBack) getApplication()).shared().getBoolean("dark" + mAuth.getCurrentUser().getUid(), false)) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }

        //arrays
        list = new ArrayList<>();
        langcodesA = new ArrayList<>();
        datalist = getResources().getStringArray(R.array.settingmain);
        langcodes = getResources().getStringArray(R.array.langCodes);
        //adding data
        for (int i = 0; i < datalist.length; i++)
            list.add(new SettingList(datalist[i], i));
        //add languages code
        langcodesA.addAll(Arrays.asList(langcodes));
        //adapter
        adapter = new SettingAdapter(list, this);
        listView.setAdapter(adapter);
        //main list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    //chat settings
                    case 0:
                        startActivity(new Intent(Setting.this, ChatSettings.class));

                        break;
                    //calls settings
                    case 1:
                        startActivity(new Intent(Setting.this, CallsSetting.class));

                        break;
                    //Security settings
                    case 2:
                        startActivity(new Intent(Setting.this, SecuSetting.class));

                        break;
                    //notification settings
                    case 3:
                        startActivity(new Intent(Setting.this, NotifSetting.class));
                        break;
                    // change lang
                    case 4:
                        dialog = new AlertDialog.Builder(Setting.this);
                        dialog.setTitle(getResources().getString(R.string.choose_lang));
                        //checked item is the default checked item when dialog open
                        choosenLang = ((AppBack) getApplication()).shared().getString("lang", "def");
                        dialog.setSingleChoiceItems(getResources().getStringArray(R.array.lang), langcodesA.indexOf(choosenLang), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //set choosen lang id
                                choosenLang = langcodes[i];
                                ((AppBack) getApplication()).changelang(choosenLang);
                                dialogInterface.dismiss();
                                Intent restart = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                                restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                restart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(restart);
                                finish();

                            }
                        });
                        dialog.show();
                        break;
                    //delete account
                    case 5:
                        dialog = new AlertDialog.Builder(Setting.this);
                        dialog.setTitle(getResources().getString(R.string.per_delete_acc));
                        dialog.setMessage(getResources().getString(R.string.acc_delete_hint));
                        dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(Global.check_int(Setting.this))
                           startActivity(new Intent(Setting.this, VerifyDelete.class));
                           else
                                Toast.makeText(Setting.this, R.string.check_int, Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.show();
                        break;
                    //privacy and policy
                    case 6:
                        dialog = new AlertDialog.Builder(Setting.this);
                        dialog.setTitle(getResources().getString(R.string.privacy));
                        dialog.setMessage(getResources().getString(R.string.lorem));
                        dialog.setNeutralButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    //about
                    case 7:
                        dialog = new AlertDialog.Builder(Setting.this);
                        dialog.setTitle(getResources().getString(R.string.about));
                        dialog.setMessage(getResources().getString(R.string.app_name) + " " + getResources().getString(R.string.description));
                        dialog.setNeutralButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                }
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        Global.currentactivity = this;
        AppBack myApp = (AppBack) this.getApplication();
        if (myApp.wasInBackground) {
            //init data
            Map<String, Object> map = new HashMap<>();
            map.put(Global.Online, true);
            if(mAuth.getCurrentUser() != null)
                mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map);
            Global.local_on = true;
            //lock screen
            ((AppBack) getApplication()).lockscreen(((AppBack) getApplication()).shared().getBoolean("lock", false));
        }

        myApp.stopActivityTransitionTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppBack) this.getApplication()).startActivityTransitionTimer();
        Global.currentactivity = null;
    }



}
