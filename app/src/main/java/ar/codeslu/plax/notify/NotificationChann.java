package ar.codeslu.plax.notify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import ar.codeslu.plax.R;


/**
 * Created by Cryp2Code on 9/10/2018.
 */

public class NotificationChann extends ContextWrapper {
    // todo : doc dojnot forget
    public static  String PLAX_CHANNEL_ID;
    public static  String PLAX_CHANNEL_NAME;
    NotificationManager manager;

    public NotificationChann(Context base) {
        super(base);
        PLAX_CHANNEL_ID = getApplicationContext().getPackageName();
        PLAX_CHANNEL_NAME = getString(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            creatChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void creatChannel() {
        NotificationChannel channel = new NotificationChannel(PLAX_CHANNEL_ID, PLAX_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  Notification.Builder getPLAXNot(String title, String body, PendingIntent pendingIntent , Uri sound)
    {
        return new Notification.Builder(getApplicationContext(),PLAX_CHANNEL_ID)
                .setContentText(body)
                .setContentTitle(title)
                .setSound(sound)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.logo);

    }


}
