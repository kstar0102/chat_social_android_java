package ar.codeslu.plax.notify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;

import ar.codeslu.plax.R;
import ar.codeslu.plax.global.AppBack;


/**
 * Created by CodeSlu on 9/10/2018.
 */

public class NotificationChann extends ContextWrapper {
    // todo : doc dojnot forget
    public static  String PLAX_CHANNEL_ID;
    public static  String PLAX_CHANNEL_NAME;
    NotificationManager manager;
    int colorN;
    Uri sound;

    public NotificationChann(Context base,int color,Uri sound) {
        super(base);
        this.colorN = color;
        this.sound = sound;
        PLAX_CHANNEL_ID = System.currentTimeMillis()+getApplicationContext().getPackageName();
        PLAX_CHANNEL_NAME = getString(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            creatChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void creatChannel() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        NotificationChannel channel = new NotificationChannel(PLAX_CHANNEL_ID, PLAX_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setSound(sound,audioAttributes);
        channel.setLightColor(colorN);
        channel.setLockscreenVisibility(Notification.PRIORITY_HIGH);
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
                .setWhen(System.currentTimeMillis())
                .setLights(colorN, 300, 100) // To change Light Colors
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.logo);


    }


}
