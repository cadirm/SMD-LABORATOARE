package com.example.lab7;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String ALIAS = "ENC_DEC_KEYS";
    public static String savedPin = "SavePin";

    EditText pinEditText;
    TextView showPinTextView;
    Button saveButton;
    Button showButton;

    SharedPreferences sharedpreferences;

    private KeyStore initializeKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return keyStore;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void generateKeysIfNoExistent() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");

        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT
                | KeyProperties.PURPOSE_DECRYPT)
                .setKeySize(2048)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build();

        generator.initialize(keyGenParameterSpec);
        generator.generateKeyPair();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KeyguardManager keyguardService = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        if (!keyguardService.isDeviceSecure()) {
            Log.d("AYAYAY", "Phone no secured");

            new AlertDialog.Builder(this)
                    .setMessage(R.string.alert_dialog_msg)
                    .setTitle(R.string.alert_dialog_title)
                    .setPositiveButton(R.string.ok_string, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d("AYAYAY", "Ok selected, redirecting");
                            Intent mapIntent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                            startActivity(mapIntent);
                        }
                    })
                    .setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d("AYAYAY", "Cancel selected, closing");
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
        Log.d("AYAYAY", "Phone is secured");

        final KeyStore keyStore = initializeKeyStore();

        if(keyStore == null) {
            Log.d("AYAYAY", "Failed to initilize keyStore");
            finish();
        }

        try {
            if (keyStore.containsAlias(ALIAS)) {
                Log.d("AYAYAY", "[keyStore contains alias]");
            }
            else {
                Log.d("AYAYAY", "[keyStore doesn't contain alias]");
                generateKeysIfNoExistent();
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        pinEditText = (EditText)findViewById(R.id.pinEditText);
        showPinTextView = (TextView)findViewById(R.id.showPinTextView);
        saveButton = (Button)findViewById(R.id.saveButton);
        showButton = (Button)findViewById(R.id.showButton);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = sharedpreferences.edit();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("AYAYAY", "Saving this ");
                    String pin = pinEditText.getText().toString();
                    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

                    cipher.init(Cipher.ENCRYPT_MODE, keyStore.getCertificate(ALIAS).getPublicKey());
                    byte[] bytes = cipher.doFinal(pin.getBytes());
                    String encodedData = Base64.encodeToString(bytes, Base64.DEFAULT);

                    Log.d("AYAYAY", "Saving this "+encodedData);
                    editor.putString(savedPin, encodedData);
                    editor.commit();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException | KeyStoreException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }

            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myStrValue = sharedpreferences.getString(savedPin, "defaultStringIfNothingFound");
                Log.d("AYAYAY", "Got this from shared "+myStrValue);

                try {
                    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

                    cipher.init(Cipher.DECRYPT_MODE, keyStore.getKey(ALIAS, null));
                    byte[] encryptedData = Base64.decode(myStrValue, Base64.DEFAULT);
                    String data = new String(cipher.doFinal(encryptedData));
                    showPinTextView.setText(data);

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (UnrecoverableKeyException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
