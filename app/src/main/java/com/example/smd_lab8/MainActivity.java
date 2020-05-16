package com.example.smd_lab8;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    Button sendText;
    EditText message;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendText = findViewById(R.id.sendButton);
        message = findViewById(R.id.message);


        sendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    KeyGenerator KG = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA256, "AndroidKeyStore");
                    KeyGenParameterSpec KGPS = new KeyGenParameterSpec.Builder("EncryptKey", KeyProperties.PURPOSE_SIGN).setKeySize(2048).build();
                    KG.init(KGPS);

                    Singleton.getInstance().key(KG);
                    //KG.generateKey();
                    Mac mac = Mac.getInstance("HmacSHA256");
                    mac.init(new SecretKeySpec(Singleton.getInstance().getSK().toString().getBytes(), "HmacSHA256"));
                    byte[] data = message.getText().toString().getBytes();
                    byte[] sentData = mac.doFinal(data);


                    String text = message.getText().toString().trim();
                    if (TextUtils.isEmpty(text)) {
                        Toast.makeText(MainActivity.this, "Insert text", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(MainActivity.this, Main2Activity.class);
                        String value = message.getText().toString();
                        i.putExtra("data", data);
                        i.putExtra("sentData", sentData);
                        Log.d("VARIABLE_2", new String(data));
                        Log.d("VARIABLE_2", new String (sentData));
                        startActivity(i);
                    }

                } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | InvalidKeyException e) {e.printStackTrace();}
            }
        });
    }

}
