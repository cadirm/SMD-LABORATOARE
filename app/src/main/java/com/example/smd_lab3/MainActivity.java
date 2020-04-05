package com.example.smd_lab3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private MyStartedService myStartedService;
    private MyBoundService myBoundService;
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.start);
        Button stopButton = findViewById(R.id.stop);
        Button dateButton = findViewById(R.id.date);
        Button dollarButton = findViewById(R.id.dollar);

        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startMyService();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopMyService();
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = myBoundService.getCurrentDate();
                Toast.makeText(myBoundService, date, Toast.LENGTH_SHORT).show();
            }
        });
        final Intent intent = new Intent(this, MyIntentService.class);
        intent.setAction("com.example.smd_lab3.action.BAZ");
        IntentFilter intentFilter = new IntentFilter(MyBroadcastReceiver.class.getName());
        LocalBroadcastManager.getInstance(this).registerReceiver(myBroadcastReceiver, intentFilter);

        dollarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(intent);
            }
        });

        bindToService();
        bindToBoundService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myBroadcastReceiver);
        unbindService(serviceConnection);
        unbindService(boundServiceConnection);
    }

    private void stopMyService() {
        Intent intent = MyStartedService.getIntent(this);
        stopService(intent);
    }

    private void startMyService() {
        Intent intent = MyStartedService.getIntent(this);
        startService(intent);
    }

    private void bindToBoundService() {
        if (myBoundService == null) {
            Intent intent = new Intent(this, MyBoundService.class);
            bindService(intent, boundServiceConnection, BIND_AUTO_CREATE);
        }
    }

    private void bindToService() {
        if (myStartedService == null) {
            Intent intent = new Intent(this, MyStartedService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myStartedService = ((MyStartedService.MyStartedServiceBinder) service).get();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myStartedService = null;
        }
    };

    private ServiceConnection boundServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBoundService = ((MyBoundService.MyBoundServiceBinder) service).get();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myStartedService = null;
        }
    };
}
