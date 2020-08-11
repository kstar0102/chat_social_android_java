package ar.codeslu.plax.auth;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.pm.ShortcutManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.me.MessageIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ar.codeslu.plax.MainActivity;
import ar.codeslu.plax.R;
import ar.codeslu.plax.adapters.ScreenSlidePageFragment;
import ar.codeslu.plax.custom.ZoomOutPageTransformer;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import ar.codeslu.plax.global.encryption;
import ar.codeslu.plax.settingsitems.ChatSettings;
import me.leolin.shortcutbadger.ShortcutBadger;


public class VerifyDelete extends AppCompatActivity {

    //view
    EditText verifyE;
    Button next;
    ImageView resend;
    TextView txt, tickT;
    //vars
    String number, mVerificationId = "", code;
    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mData;

    //firebase delete
    FirebaseUser user;
    DatabaseReference mchat, mCalls, mGroup, mPhone, mTokens;
    PhoneAuthCredential credential;

    String myId, called;
    int countit = 0;
    Handler mHandler;


    long duration = 60 * 1000;
    long tick = 1000;
    int count = 0;
    private static final int NUM_PAGES = 5;


    private ViewPager mPager;

    private PagerAdapter pagerAdapter;


    Timer timer;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_delete);

        mPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        verifyE = findViewById(R.id.verifyE);
        next = findViewById(R.id.nextV);
        txt = findViewById(R.id.txtR);
        tickT = findViewById(R.id.tick);
        resend = findViewById(R.id.resend);

        number = Global.phoneLocal;

        //firebase init
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);


        mchat = FirebaseDatabase.getInstance().getReference(Global.CHATS);
        mCalls = FirebaseDatabase.getInstance().getReference(Global.CALLS);
        mGroup = FirebaseDatabase.getInstance().getReference(Global.GROUPS);
        mPhone = FirebaseDatabase.getInstance().getReference(Global.Phones);
        mTokens = FirebaseDatabase.getInstance().getReference(Global.tokens);

        mHandler = new Handler();


        //verify
        if(!Global.phoneLocal.isEmpty())
        sendVerificationCode(number);
        else
            Toast.makeText(this, R.string.error+" "+R.string.restarttheapp, Toast.LENGTH_SHORT).show();

        resend.setVisibility(View.GONE);
        tickT.setVisibility(View.VISIBLE);
        new CountDownTimer(duration, tick) {

            public void onTick(long millisUntilFinished) {
                tickT.setText(timeSec(count));
                count = count + 1;
            }

            public void onFinish() {
                resend.setVisibility(View.VISIBLE);
                tickT.setVisibility(View.GONE);
                count = 0;
            }
        }.start();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Global.check_int(VerifyDelete.this)) {
                    if (!TextUtils.isEmpty(verifyE.getText().toString().trim())) {

                        verifyVerificationCode(verifyE.getText().toString().trim());
                        next.setEnabled(false);

                    } else {
                        verifyE.setError(getString(R.string.enter_code));
                        verifyE.requestFocus();
                    }
                } else
                    Toast.makeText(VerifyDelete.this, R.string.check_int, Toast.LENGTH_SHORT).show();
            }
        });


        next.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if (Global.check_int(VerifyDelete.this)) {
                                if (!TextUtils.isEmpty(verifyE.getText().toString().trim())) {

                                    verifyVerificationCode(verifyE.getText().toString().trim());
                                    next.setEnabled(false);

                                } else {
                                    verifyE.setError(getString(R.string.enter_code));
                                    verifyE.requestFocus();
                                }
                            } else
                                Toast.makeText(VerifyDelete.this, R.string.check_int, Toast.LENGTH_SHORT).show();

                            return true;
                        default:
                            break;
                    }

                }
                return false;
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resend.setVisibility(View.GONE);
                tickT.setVisibility(View.VISIBLE);

                if(!Global.phoneLocal.isEmpty())
                    sendVerificationCode(number);
                else
                    Toast.makeText(VerifyDelete.this, R.string.error+" "+R.string.restarttheapp, Toast.LENGTH_SHORT).show();                new CountDownTimer(duration, tick) {

                    public void onTick(long millisUntilFinished) {
                        tickT.setText(timeSec(count));
                        count = count + 1;
                    }

                    public void onFinish() {
                        resend.setVisibility(View.VISIBLE);
                        tickT.setVisibility(View.GONE);
                        count = 0;
                    }
                }.start();
            }
        });
        pageSwitcher(3);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                verifyE.setText(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyDelete.this, R.string.cannt_verify, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }
    };

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private void verifyVerificationCode(String otp) {
        if (!mVerificationId.isEmpty() && mVerificationId != null)
            //creating the credential
            credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        else {
            Toast.makeText(VerifyDelete.this, getString(R.string.fix_it), Toast.LENGTH_LONG).show();
            next.setEnabled(true);

        }

        if (credential != null)
            deletephoto(credential);
    }

    public void deletephoto(PhoneAuthCredential credential) {
        myId = mAuth.getCurrentUser().getUid();

        ((AppBack) getApplication()).getCalls();
        ((AppBack) getApplication()).getdialogdbG(myId);
        ((AppBack) getApplication()).getdialogdb(myId);

        Map<String, Object> map = new HashMap<>();
        map.put(Global.Online, false);
        mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Global.local_on = false;
                mTokens.child(mAuth.getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mPhone.child(Global.phoneLocal).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                final Map<String, Object> map = new HashMap<>();
                                map.put("avatar", "no");
                                map.put("phone", "t88848992hisuseri9483828snothereri9949ghtnow009933");
                                mData.child(myId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            try {
                                                for (int i = 0; i < Global.diaG.size(); i++) {

                                                    int finalI = i;
                                                    mchat.child(Global.diaG.get(i).getId()).child(myId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (finalI == Global.diaG.size() - 1)
                                                                countit = countit + 1;

                                                        }
                                                    });

                                                }

                                                //change calls
                                                Map<String, Object> map33 = new HashMap<>();
                                                map33.put("ava", String.valueOf("no"));
                                                if (Global.callList != null) {
                                                    for (int i = 0; i < Global.callList.size(); i++) {
                                                        if (!Global.callList.get(i).getFrom().equals(myId))
                                                            called = Global.callList.get(i).getFrom();
                                                        else
                                                            called = Global.callList.get(i).getTo();
                                                        int finalI = i;
                                                        mCalls.child(called).child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                                        data.getRef().updateChildren(map33);
                                                                    }
                                                                    if (finalI == Global.callList.size() - 1)
                                                                        countit = countit + 1;


                                                                } else {
                                                                    if (finalI == Global.callList.size() - 1)
                                                                        countit = countit + 1;
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                if (finalI == Global.callList.size() - 1)
                                                                    countit = countit + 1;
                                                            }
                                                        });

                                                    }
                                                }

                                                //change all last messages in group
                                                Map<String, Object> map2 = new HashMap<>();
                                                map2.put("lastsenderava", String.valueOf("no"));
                                                if (Global.diaGGG != null) {
                                                    for (int i = 0; i < Global.diaGGG.size(); i++) {
                                                        if (Global.diaGGG.get(i).getLastsender().equals(myId)) {
                                                            int finalI = i;
                                                            mGroup.child(Global.diaGGG.get(i).getId()).updateChildren(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (finalI == Global.diaGGG.size() - 1)
                                                                        countit = countit + 1;
                                                                }
                                                            });
                                                        }
                                                    }
                                                }

                                                //update group messages

                                                Map<String, Object> map3 = new HashMap<>();
                                                map3.put("avatar", encryption.encryptOrNull(String.valueOf("no")));
                                                if (Global.diaGGG != null) {
                                                    for (int i = 0; i < Global.diaGGG.size(); i++) {
                                                        int finalI = i;
                                                        mGroup.child(Global.diaGGG.get(i).getId()).child(Global.Messages).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                                    MessageIn message = data.getValue(MessageIn.class);
                                                                    if (message.getFrom().equals(myId)) {
                                                                        data.getRef().updateChildren(map3);
                                                                    }
                                                                }

                                                                if (finalI == Global.diaGGG.size() - 1)
                                                                    countit = countit + 1;

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                if (finalI == Global.diaGGG.size() - 1)
                                                                    countit = countit + 1;
                                                            }
                                                        });
                                                    }
                                                }
                                            } catch (NullPointerException e) {

                                            }

                                            Global.avaLocal = "no";


                                            if (Global.diaGGG.size() == 0)
                                                countit = countit + 2;


                                            if (Global.diaG.size() == 0)
                                                countit = countit + 1;


                                            if (Global.callList.size() == 0)
                                                countit = countit + 1;


                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // TODO Auto-generated method stub
                                                    while (countit <= 4) {
                                                        try {
                                                            Thread.sleep(500);
                                                            mHandler.post(new Runnable() {

                                                                @Override
                                                                public void run() {
                                                                    if (countit == 4) {

                                                                        countit = countit + 5;

                                                                        user = FirebaseAuth.getInstance().getCurrentUser();

                                                                        // Prompt the user to re-provide their sign-in credentials
                                                                        if (user != null) {
                                                                            user.reauthenticate(credential)
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (user != null) {
                                                                                                user.delete()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
                                                                                                                    disableshourtcuts();


                                                                                                                //clear all notifications
                                                                                                                NotificationManager notificationManager = (NotificationManager) VerifyDelete.this.getSystemService(Context.NOTIFICATION_SERVICE);
                                                                                                                try {
                                                                                                                    if (notificationManager != null) {
                                                                                                                        notificationManager.cancelAll();
                                                                                                                        int count = 0;
                                                                                                                        //store it again
                                                                                                                        ((AppBack) getApplication()).editSharePrefs().putInt("numN" + mAuth.getCurrentUser().getUid(), count);
                                                                                                                        ((AppBack) getApplication()).editSharePrefs().apply();
                                                                                                                        ShortcutBadger.applyCount(VerifyDelete.this, count);
                                                                                                                    }
                                                                                                                } catch (NullPointerException e) {
                                                                                                                    //nothing
                                                                                                                }

                                                                                                                Intent restart = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                                                                                                                restart.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                                                restart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                                                startActivity(restart);
                                                                                                                finish();


                                                                                                            }
                                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                                    @Override
                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                    }
                                                                                                });
                                                                                            }

                                                                                        }
                                                                                    });
                                                                        } else {
                                                                            String message = getString(R.string.fix_it);
                                                                            Toast.makeText(VerifyDelete.this, message, Toast.LENGTH_LONG).show();
                                                                        }

                                                                    }

                                                                }
                                                            });
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }
                                            }).start();


                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }

    public static String timeSec(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds - minutes * 60;

        String formattedTime = "";

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds;

        return formattedTime;
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new ScreenSlidePageFragment(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay

    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            runOnUiThread(new Runnable() {
                public void run() {

                    if (page > 4) { // In my case the number of pages are 5
                        page = 0;
                        mPager.setCurrentItem(page++);
                    } else {
                        mPager.setCurrentItem(page++);
                    }
                }
            });

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void disableshourtcuts()
    {
        List<String> idds = new ArrayList<>();
        idds.add("addstory");
        idds.add("group");
        idds.add("user1");
        idds.add("user2");
        ShortcutManager shortcutManager2 = this.getSystemService(ShortcutManager.class);
        shortcutManager2.disableShortcuts(idds);
    }
}
