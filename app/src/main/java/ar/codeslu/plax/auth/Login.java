package ar.codeslu.plax.auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.lamudi.phonefield.PhoneEditText;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.Global;

public class Login extends AppCompatActivity {
    //views
    private RelativeLayout rellay1;
    private PhoneEditText phoneEditText;
    private Button next;
    //vars
    String phoneNumber;
    //View Animation
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        //Thread start
        handler.postDelayed(runnable, 2000); //2000 is the timeout for the splash

        phoneEditText  = (PhoneEditText) findViewById(R.id.phone_input_layout);
        next  = (Button) findViewById(R.id.nextL);

        //Phone format checker
        phoneEditText.setHint(R.string.phone_hint);
        phoneEditText.setTextColor(Color.WHITE);
// you can set the default country as follows
        phoneEditText.setDefaultCountry(getDeviceCountryCode(this));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checks if the field is valid
                if (phoneEditText.isValid()) {
                    phoneEditText.setError(null);
                    if(Global.check_int(Login.this))
                    authing();

                } else {
                    phoneEditText.setError(getString(R.string.invalid_phone));
                }
            }
        });
    }
    public void authing()
    {
        phoneNumber = phoneEditText.getPhoneNumber().trim();
        Intent intent = new Intent(Login.this, Verify.class);
        intent.putExtra("mobile", phoneNumber);
        startActivity(intent);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(Global.check_int(Login.this))
        {
            next.setText(getString(R.string.next));
            next.setEnabled(true);
        }else
        {
            next.setText(R.string.check_conn);
            next.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
            if(Global.check_int(Login.this))
        {
            next.setText(getString(R.string.next));
            next.setEnabled(true);
        }else
        {
            next.setText(R.string.check_conn);
            next.setEnabled(false);
        }
    }
    private static String getDeviceCountryCode(Context context) {
        String countryCode;

        // try to get country code from TelephonyManager service
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm != null) {
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
            return  countryCode.toLowerCase();

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
                case 330: return "PR";
                case 310: return "US";
                case 311: return "US";
                case 312: return "US";
                case 316: return "US";
                case 283: return "AM";
                case 460: return "CN";
                case 455: return "MO";
                case 414: return "MM";
                case 619: return "SL";
                case 450: return "KR";
                case 634: return "SD";
                case 434: return "UZ";
                case 232: return "AT";
                case 204: return "NL";
                case 262: return "DE";
                case 247: return "LV";
                case 255: return "UA";
            }
        } catch (ClassNotFoundException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        } catch (NullPointerException ignored) {
        }

        return null;
    }
}
