package com.example.smd_lab8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Main2Activity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        tv = findViewById(R.id.textView);
        Intent main_intent = getIntent();
        byte[] data = main_intent.getByteArrayExtra("data");
        byte[] sentData = main_intent.getByteArrayExtra("sentData");

        SecretKey SK = Singleton.getInstance().getSK();
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SK.toString().getBytes(), "HmacSHA256"));
            byte[] hmacHere = mac.doFinal(data);

            Log.d("VARIABLE_2", new String (sentData));
            Log.d("VARIABLE_2", new String (hmacHere));
            if (Arrays.equals(sentData, hmacHere)) {
                tv.setText("Data is unmodified.");
            } else {
                tv.setText("Data is modified.");
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}
