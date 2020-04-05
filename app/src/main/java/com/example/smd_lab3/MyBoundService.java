package com.example.smd_lab3;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyBoundService extends Service {
    public MyBoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("debug", "BoundService - onBind | " + Thread.currentThread().getName());
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("debug", "BoundService - onUnbind | " + Thread.currentThread().getName());
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("debug", "BoundService - onCreate | " + Thread.currentThread().getName());
    }

    @Override
    public void onDestroy() {
        Log.i("debug", "BoundService - onDestroy | " + Thread.currentThread().getName());
        super.onDestroy();
    }

    public String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formtedDate = df.format(c);
        return formtedDate;
    }

    private MyBoundService.MyBoundServiceBinder binder = new MyBoundService.MyBoundServiceBinder();

    class MyBoundServiceBinder extends Binder {
        private MyBoundService myBoundService = MyBoundService.this;
        public MyBoundService get() {
            return myBoundService;
        }
    }

}
