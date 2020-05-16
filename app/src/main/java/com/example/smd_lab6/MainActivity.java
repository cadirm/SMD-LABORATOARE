package com.example.smd_lab6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button Download_Image;
    Button Load_Image;
    ImageView iv;
    EditText ImageUrl;
    Spinner spinner;
    final int STORAGE_WRITE_PERMISSION = 1231;
    final int STORAGE_READ_PERMISSION = 1232;
    SharedPreferences LastSelect;
    SharedPreferences.Editor editor;
    int var;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LastSelect = getSharedPreferences("LastSetting", Context.MODE_PRIVATE);
        editor = LastSelect.edit();
        final int LastClick = LastSelect.getInt("LastClick",0);

        Download_Image = findViewById(R.id.downloadButton);
        Load_Image = findViewById(R.id.loadButton);
        iv = findViewById(R.id.imageView);
        ImageUrl = findViewById(R.id.urlEditText);
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.choices,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(LastClick);


        Download_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("PERMISSION", "WRITE PERMISION DENIED");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_PERMISSION);
                } else {
                    Log.d("PERMISSION", "WRITE PERMISSION");
                }
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("PERMISSION", "READ PERMISION DENIED");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_PERMISSION);
                } else {
                    Log.d("PERMISSION", "READ PERMISSION");
                }

                String s = ImageUrl.getText().toString();
                if (var != 3) {
                    imageDownload image = new imageDownload(MainActivity.this, iv);
                    image.execute(s);
                    // new DownloadFile().execute(s);
                    Log.d("DEBUG", "FINAL DE DOWNLOAD");
                }
                if (var == 3){
                    String text = "Please choose where to store!";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                }
            }
        });

        Load_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DEBUG", "IN BUTON");
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("PERMISSION", "WRITE PERMISION DENIED");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_WRITE_PERMISSION);
                } else {
                    Log.d("PERMISSION", "WRITE PERMISSION");
                }
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("PERMISSION", "READ PERMISION DENIED");
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_READ_PERMISSION);
                } else {
                    Log.d("PERMISSION", "READ PERMISSION");
                }

                //ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageView);
                String image_path = "";
                if(var == 1) {
                    image_path = String.valueOf(Environment.getExternalStorageDirectory()) + "/downloadimage.png";  //External Storage
                }else if (var == 2) {
                    image_path = "/data/user/0/com.example.smd_lab6/files" + "/downloadimage2.png";  //Internal Storage
                }
                iv.setImageBitmap(BitmapFactory.decodeFile(image_path));
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        editor.putInt("LastClick",position).commit();
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(),text,Toast.LENGTH_SHORT).show();
        if (text.equals("External") ) {
            var = 1;
        }
        if (text.equals("Internal") ){
            var = 2;
        }
        if (text.equals("Undefined") ){
            var = 3;
        }
        Log.d("CHOICE", String.valueOf(var));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class imageDownload extends AsyncTask<String, Integer, Bitmap> {
        Context context;
        ImageView imageView;
        Bitmap bitmap;
        InputStream in = null;
        int responseCode = -1;
        //constructor.
        public imageDownload(Context context, ImageView imageView) {
            this.context = context;
            this.imageView = imageView;
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();
                responseCode = httpURLConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    in = httpURLConnection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    in.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap data) {
            //imageView.setImageBitmap(data);
            saveImage(data);
        }

        private void saveImage(Bitmap data) {
            //File createFolder = new File(Environment.getExternalStorageDirectory().toString());
            //createFolder.mkdir();
            File saveImage = null;
            if (var == 1) {
                saveImage = new File(Environment.getExternalStorageDirectory().toString(), "downloadimage.png"); //External Storage
            }else if(var == 2) {
                saveImage = new File(context.getFilesDir(), "downloadimage2.png"); //Internal Storage
            }

            try {
                OutputStream outputStream = new FileOutputStream(saveImage);
                data.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                outputStream.flush();
                outputStream.close();

                MediaStore.Images.Media.insertImage(
                        context.getContentResolver(),
                        saveImage.getAbsolutePath(),
                        saveImage.getName(),
                        saveImage.getName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
