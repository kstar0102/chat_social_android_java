package ar.codeslu.plax;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;

import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.settings.SettingAdapter;
import ar.codeslu.plax.settings.SettingList;
import ar.codeslu.plax.settingsitems.ChatSettings;

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
    //vars
    String choosenLang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        listView = findViewById(R.id.mainL);
        //firebase init
        mAuth = FirebaseAuth.getInstance();
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
                    //notification settings
                    case 1:
                        break;
                    //change lang
                    case 2:
                        dialog = new AlertDialog.Builder(Setting.this);
                        dialog.setTitle(getResources().getString(R.string.choose_lang));
                        //checked item is the default checked item when dialog open
                        choosenLang = ((AppBack) getApplication()).shared().getString("lang", "en");
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
                    case 3:
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
                                //delete account
                                dialogInterface.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    //privacy and policy
                    case 4:
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
                    case 5:
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


}
