package com.example.smd_lab3;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Random;

import static com.example.smd_lab3.App.CHANNEL_ID;

public class MyStartedService extends Service {
    Boolean serviceIsForeground = false;
    public MyStartedService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("debug", "Service - onCreate | " + Thread.currentThread().getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("debug", "Service - onStartCommand | " + Thread.currentThread().getName());

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText("Notification Test")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        int rand = new Random().nextInt(Integer.MAX_VALUE);
        Toast.makeText(getApplicationContext(), "Bits saved: " + rand + ".", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("debug", "Service - onDestroy | " + Thread.currentThread().getName());
        super.onDestroy();
    }

    public static Intent getIntent(Context ctx) {
        Intent intent = new Intent(ctx, MyStartedService.class);
        return intent;
    }

    private MyStartedServiceBinder binder = new MyStartedServiceBinder();

    class MyStartedServiceBinder extends Binder {
        private MyStartedService myStartedService = MyStartedService.this;
        public MyStartedService get() {
            return myStartedService;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("debug", "Service - onBind | " + Thread.currentThread().getName());

        return binder;
    }
}
