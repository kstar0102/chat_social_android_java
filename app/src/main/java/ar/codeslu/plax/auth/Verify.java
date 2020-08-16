package ar.codeslu.plax.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ar.codeslu.plax.R;
import ar.codeslu.plax.adapters.ScreenSlidePageFragment;
import ar.codeslu.plax.custom.ZoomOutPageTransformer;
import ar.codeslu.plax.global.AppBack;
import ar.codeslu.plax.global.Global;
import dmax.dialog.SpotsDialog;

public class Verify extends AppCompatActivity {

    //view
    EditText verifyE;
    Button next;
    ImageView resend;
    TextView txt,tickT, phonenumber1, phonenumber2, wrongbtn;
    //vars
    String number, mVerificationId="",code;
    int coo = 0;
    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mData,mPhone;
    PhoneAuthCredential credential;



    long duration = 60*1000;
    long tick = 1000;
    int count=0;

    Timer timer;

    android.app.AlertDialog dialogg;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);


        verifyE = findViewById(R.id.verifyE);
        next = findViewById(R.id.nextV);
        txt = findViewById(R.id.txtR);
        tickT = findViewById(R.id.tick);
        resend = findViewById(R.id.resend);
        wrongbtn = findViewById(R.id.wrongnumber);
        phonenumber1 = findViewById(R.id.phoneNumber_txt_1);
        phonenumber2 = findViewById(R.id.phoneNumber_txt_2);


        if (getIntent() != null){
            number = getIntent().getExtras().getString("mobile");
            phonenumber1.setText(number);
            phonenumber2.setText(number);
        }


        //firebase init
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);
        mPhone = FirebaseDatabase.getInstance().getReference(Global.Phones);

        dialogg = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage(R.string.pleasW)
                .setCancelable(true)
                .build();

        //verify
        sendVerificationCode(number);

        resend.setVisibility(View.GONE);
        tickT.setVisibility(View.VISIBLE);
        new CountDownTimer(duration, tick) {

            public void onTick(long millisUntilFinished) {
                tickT.setText(timeSec(count));
                count= count+1;
            }

            public void onFinish() {
                resend.setVisibility(View.VISIBLE);
                tickT.setVisibility(View.GONE);
                count=0;
            }
        }.start();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(verifyE.getText().toString().trim())) {

                    verifyVerificationCode(verifyE.getText().toString().trim());
                        dialogg.show();
                        next.setEnabled(false);

                } else
                {
                    verifyE.setError(getString(R.string.enter_code));
                    verifyE.requestFocus();
                }
            }
        });


        next.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:
                                if (!TextUtils.isEmpty(verifyE.getText().toString().trim())) {

                                    verifyVerificationCode(verifyE.getText().toString().trim());
                                    dialogg.show();
                                    next.setEnabled(false);

                                } else
                                {
                                    verifyE.setError(getString(R.string.enter_code));
                                    verifyE.requestFocus();
                                }
                                return true;
                            default:
                                break;
                        }

                }
                return false;
            }
        });

        wrongbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resend.setVisibility(View.GONE);
                tickT.setVisibility(View.VISIBLE);

                sendVerificationCode(number);
                new CountDownTimer(duration, tick) {

                    public void onTick(long millisUntilFinished) {
                       tickT.setText(timeSec(count));
                        count= count+1;
                    }

                    public void onFinish() {
                        resend.setVisibility(View.VISIBLE);
                        tickT.setVisibility(View.GONE);
                        count=0;
                    }
                }.start();
            }
        });

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
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            dialogg.dismiss();
            Toast.makeText(Verify.this, R.string.cannt_verify, Toast.LENGTH_LONG).show();
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
        if(!mVerificationId.isEmpty() && mVerificationId != null)
            //creating the credential
            credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        else {
            Toast.makeText(Verify.this, getString(R.string.fix_it), Toast.LENGTH_LONG).show();
            next.setEnabled(true);
        }

        //signing the user
        if(credential != null)
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Verify.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Map<String, Object> map = new HashMap<>();
                            map.put("phone", number);
                            map.put("androidid", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                            mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Map<String, Object> map2 = new HashMap<>();
                                    map2.put("id", mAuth.getCurrentUser().getUid());
                                    mPhone.child(number).updateChildren(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(Verify.this,DataSet.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialogg.dismiss();
                                            //verification unsuccessful.. display an error message
                                            String message = getString(R.string.fix_it);
                                            Toast.makeText(Verify.this, message, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialogg.dismiss();
                                            //verification unsuccessful.. display an error message
                                            String message = getString(R.string.fix_it);
                                            Toast.makeText(Verify.this, message, Toast.LENGTH_LONG).show();
                                        }
                                    });

                        } else {

                            //verification unsuccessful.. display an error message
                            dialogg.dismiss();

                            String message = getString(R.string.fix_it);

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = getString(R.string.invalid_code);
                            }
                            Toast.makeText(Verify.this, message, Toast.LENGTH_LONG).show();

                        }
                        next.setEnabled(true);

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
    @Override
    protected void onDestroy() {
//        timer.cancel();
        super.onDestroy();
    }

}
