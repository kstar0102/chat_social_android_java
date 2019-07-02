package ar.codeslu.plax.auth;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.Global;

public class Verify extends AppCompatActivity {

    //view
    EditText verifyE;
    Button next;
    ImageView resend;
    TextView txt;
    //vars
    String number, mVerificationId, code;
    int coo = 0;
    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        verifyE = findViewById(R.id.verifyE);
        next = findViewById(R.id.nextV);
        txt = findViewById(R.id.txtR);
        resend = findViewById(R.id.resend);
        resend.setEnabled(false);
        resend.setVisibility(View.GONE);
        txt.setEnabled(false);
        txt.setVisibility(View.GONE);
        if (getIntent() != null)
            number = getIntent().getExtras().getString("mobile");

        //firebase init
        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference(Global.USERS);

        //verify
        sendVerificationCode(number);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(verifyE.getText().toString())) {
                    code = verifyE.getText().toString();
                    verifyVerificationCode(code);
                    next.setEnabled(false);
                } else
                    verifyE.setError(getString(R.string.enter_code));
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resend.setEnabled(false);
                resend.setVisibility(View.GONE);
                txt.setEnabled(false);
                txt.setVisibility(View.GONE);
                sendVerificationCode(number);
                coo = 1;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sleep(60000);
                            resend.setEnabled(true);
                            resend.setVisibility(View.VISIBLE);
                            txt.setEnabled(true);
                            txt.setVisibility(View.VISIBLE);
                        } catch (InterruptedException e) {
                        }

                    }
                };
                thread.start();
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
        if (coo == 0) {
            resend.setEnabled(true);
            resend.setVisibility(View.VISIBLE);
            txt.setEnabled(true);
            txt.setVisibility(View.VISIBLE);
        }
    }

    private void verifyVerificationCode(String otp) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);

        //signing the user
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
                            mData.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                 Intent intent = new Intent(Verify.this,DataSet.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                  finish();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //verification unsuccessful.. display an error message
                                            String message = getString(R.string.fix_it);
                                            Toast.makeText(Verify.this, message, Toast.LENGTH_LONG).show();
                                        }
                                    });

                        } else {

                            //verification unsuccessful.. display an error message

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
}
