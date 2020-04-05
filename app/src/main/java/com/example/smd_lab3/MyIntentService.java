package com.example.smd_lab3;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_BAZ = "com.example.smd_lab3.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.smd_lab3.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.smd_lab3.extra.PARAM2";

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.i("debug", "IntentService | " + Thread.currentThread().getName());
            final String action = intent.getAction();
            if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        int rand = new Random().nextInt(Integer.MAX_VALUE);
        Log.i("debug", "You've got: " + rand + "$$$.");
        Intent i = new Intent(this, MyBroadcastReceiver.class);
        i.putExtra("text", Integer.valueOf(rand).toString());
       this.sendBroadcast(i);
    }
}
