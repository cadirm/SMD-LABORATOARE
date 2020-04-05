package com.example.smd_lab4;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.concurrent.BlockingQueue;


public class MainActivity extends AppCompatActivity {    
    private static final int KEEP_ALIVE_TIME = 10;    
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;   
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                String messageText = (String) inputMessage.obj;
                Toast.makeText(MainActivity.this, messageText, Toast.LENGTH_SHORT).show();
            }
        };

        final Button button = findViewById(R.id.downloadButton);
        final Button buttonAll = findViewById(R.id.downloadAllButton);
        final String TAG = "Runnable";

        buttonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                
                BlockingQueue<Runnable> runnableQueue = new LinkedBlockingQueue<Runnable>();                
                ThreadPoolExecutor decodeThreadPool = new ThreadPoolExecutor(
                        NUMBER_OF_CORES,
                        NUMBER_OF_CORES,
                        KEEP_ALIVE_TIME,
                        KEEP_ALIVE_TIME_UNIT,
                        runnableQueue);
                Context context = MainActivity.this;
                for (int i = 0; i < 10; i++) {
                    MyDownloadRunnable runnable = new MyDownloadRunnable(context, i+1);
                    Thread thread = new Thread(runnable);
                    decodeThreadPool.execute(thread);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // Mock up download
                        Log.d(TAG, "Downloading");
                        Message messageStart = handler.obtainMessage();
                        messageStart.obj = "Downloading";
                        messageStart.sendToTarget();
                        Calendar calendar = Calendar.getInstance();
                        long start = calendar.getTimeInMillis();
                        try {
                            for (int i = 0; i < 5; i++) {
                                Thread.currentThread().sleep(100);
                                Log.d(TAG,"Progress:" + Integer.toString(( i + 1 ) * 20 ) + "%");
                                Message message_progress = handler.obtainMessage();
                                message_progress.obj = "Progress: " + Integer.toString(( i + 1 ) * 20 ) + "%";
                                message_progress.sendToTarget();
                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        long stop = calendar.getTimeInMillis();
                        long time = (stop - start);
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
                Message message = handler.obtainMessage();
                message.obj = "Starting thread";
                message.sendToTarget();
            }
        });
    }
}
