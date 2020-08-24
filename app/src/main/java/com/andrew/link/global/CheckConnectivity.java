package com.andrew.link.global;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class CheckConnectivity extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent arg1) {

        Global.netconnect = arg1.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

    }
}