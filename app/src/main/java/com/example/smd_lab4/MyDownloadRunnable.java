package com.example.smd_lab4;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;

import java.util.Random;

public class MyDownloadRunnable implements Runnable {

    final String TAG = "Tag";
    int id;
    Context context;

    final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message inputMessage) {
            String messageText = (String) inputMessage.obj;
            Toast.makeText(context, messageText, Toast.LENGTH_SHORT).show();
        }
    };

    MyDownloadRunnable(Context context, int id) {
        this.id = id;
        this.context = context;
    }


    @Override
    public void run() {

        // Mock up download
        Log.d(TAG, "Downloading...");

        Message messageStart = handler.obtainMessage();
        messageStart.obj = "Downloading... Image " + id;
        messageStart.sendToTarget();

        try {

            Random random = new Random();
            Thread.currentThread().sleep(random.nextInt(100)+100);

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        Message messageFinish = handler.obtainMessage();
        Thread.currentThread();
        messageFinish.obj = "Image-" + id + ". DONE!";
        messageFinish.sendToTarget();
    }
}

