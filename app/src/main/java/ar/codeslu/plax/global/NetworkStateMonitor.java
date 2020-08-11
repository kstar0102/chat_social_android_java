package ar.codeslu.plax.global;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by CodeSlu on 02/03/19.
 */

public class NetworkStateMonitor extends BroadcastReceiver {
    public interface Listener {
        void onNetworkStateChange(boolean up);
    }

    Context mContext;
    boolean mIsUp;

    public NetworkStateMonitor(Context context) {
        mContext = context;
        //mListener = (Listener)context;
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this, intentFilter);
        mIsUp = isUp();
    }

    public void
    unregister() {  // call this when finished with it, and no later than onStop(): callback will crash if app has been destroyed
        mContext.unregisterReceiver(this);
    }

    public boolean isUp() {        // can be called at any time
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @Override
    public void
    onReceive(Context context, Intent intent) {     // registerReceiver callback, passed to mListener
        boolean upNow = isUp();
        if (upNow == mIsUp) return;     // no change
        mIsUp = upNow;
        ((Listener) mContext).onNetworkStateChange(mIsUp);
    }
}
