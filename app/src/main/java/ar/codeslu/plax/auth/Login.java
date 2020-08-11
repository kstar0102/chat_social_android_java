package ar.codeslu.plax.auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.lamudi.phonefield.PhoneEditText;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.Global;

public class Login extends AppCompatActivity {
    //views
    private PhoneEditText phoneEditText;
    private Button next, emailBtn;
    //vars
    String phoneNumber;
    boolean code = false;
    AlertDialog.Builder dialog;

    Handler mHandler;
    boolean isRunning = true;
    boolean prevstate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getIntent() != null) {
            try {
                code = getIntent().getExtras().getBoolean("code", false);
                if (code) {
                    code = false;
                    dialog = new AlertDialog.Builder(Login.this);
                    dialog.setTitle(getResources().getString(R.string.title_signout));
                    dialog.setMessage(getResources().getString(R.string.mess_signout));
                    dialog.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }

            } catch (NullPointerException e) {

            }

        }

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);

        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                //prompt the dialog to update google play
                googleAPI.getErrorDialog(this,result,777).show();

            }
        }
        else{
            //google play up to date
        }

        phoneEditText = (PhoneEditText) findViewById(R.id.phone_input_layout);
        next = (Button) findViewById(R.id.nextL);
        emailBtn = findViewById(R.id.login_email_btn);

        //Phone format checker
        phoneEditText.setHint(R.string.phone_hint);
        phoneEditText.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
// you can set the default country as follows
        phoneEditText.setDefaultCountry(getDeviceCountryCode(this));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checks if the field is valid
                if (phoneEditText.isValid()) {
                    phoneEditText.setError(null);
                    if (Global.check_int(Login.this))
                        authing();

                } else {
                    phoneEditText.setError(getString(R.string.invalid_phone));
                }
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, EmailLogin.class);
                startActivity(intent);
            }
        });

        mHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (isRunning) {
                    try {
                        Thread.sleep(500);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                if(prevstate != Global.check_int(Login.this)) {
                                    if (!Global.check_int(Login.this)) {
                                        next.setText(R.string.check_conn);
                                        next.setEnabled(false);
                                    }
                                    else{

                                        next.setText(getString(R.string.next));
                                        next.setEnabled(true);
                                    }

                                    prevstate = Global.check_int(Login.this);
                                }

                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }
        }).start();


        next.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            // checks if the field is valid
                            if (phoneEditText.isValid()) {
                                phoneEditText.setError(null);
                                if (Global.check_int(Login.this))
                                    authing();

                            } else {
                                phoneEditText.setError(getString(R.string.invalid_phone));
                            }
                            return true;
                        default:
                            break;
                    }

                }
                return false;
            }
        });
    }

    public void authing() {
        phoneNumber = phoneEditText.getPhoneNumber().trim();
        Intent intent = new Intent(Login.this, Verify.class);
        intent.putExtra("mobile", phoneNumber);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
    }

    private static String getDeviceCountryCode(Context context) {
        String countryCode;

        // try to get country code from TelephonyManager service
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            // query first getSimCountryIso()
            countryCode = tm.getSimCountryIso();

            if (countryCode != null && countryCode.length() == 2)
                return countryCode.toLowerCase();

            if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                // special case for CDMA Devices
                countryCode = getCDMACountryIso();
            } else {
                // for 3G devices (with SIM) query getNetworkCountryIso()
                countryCode = tm.getNetworkCountryIso();
            }

            if (countryCode != null && countryCode.length() == 2)
                return countryCode.toLowerCase();
        }

        // if network country not available (tablets maybe), get country code from Locale class
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            countryCode = context.getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            countryCode = context.getResources().getConfiguration().locale.getCountry();
        }

        if (countryCode != null && countryCode.length() == 2)
            return countryCode.toLowerCase();

        // general fallback to "us"
        return "us";
    }

    @SuppressLint("PrivateApi")
    private static String getCDMACountryIso() {
        try {
            // try to get country code from SystemProperties private class
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class);

            // get homeOperator that contain MCC + MNC
            String homeOperator = ((String) get.invoke(systemProperties,
                    "ro.cdma.home.operator.numeric"));

            // first 3 chars (MCC) from homeOperator represents the country code
            int mcc = Integer.parseInt(homeOperator.substring(0, 3));

            // mapping just countries that actually use CDMA networks
            switch (mcc) {
                case 330:
                    return "PR";
                case 310:
                    return "US";
                case 311:
                    return "US";
                case 312:
                    return "US";
                case 316:
                    return "US";
                case 283:
                    return "AM";
                case 460:
                    return "CN";
                case 455:
                    return "MO";
                case 414:
                    return "MM";
                case 619:
                    return "SL";
                case 450:
                    return "KR";
                case 634:
                    return "SD";
                case 434:
                    return "UZ";
                case 232:
                    return "AT";
                case 204:
                    return "NL";
                case 262:
                    return "DE";
                case 247:
                    return "LV";
                case 255:
                    return "UA";
            }
        } catch (ClassNotFoundException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        } catch (NullPointerException ignored) {
        }

        return null;
    }

//    @Override
//    public void onBackPressed() {
//        if (mPager.getCurrentItem() == 0) {
//            // If the user is currently looking at the first step, allow the system to handle the
//            // Back button. This calls finish() on this activity and pops the back stack.
//            super.onBackPressed();
//        } else {
//            // Otherwise, select the previous step.
//            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
//        }
//    }
//

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}