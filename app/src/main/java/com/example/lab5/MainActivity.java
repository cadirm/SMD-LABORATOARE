package com.example.lab5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Button mainGetResults;
    TextView mainResults;
    EditText mainUrlInput;



    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            URL url = null;
            try {
                url = new URL(urls[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                return result.toString("UTF-8");
            } catch (IOException e) {
            } finally {
                if(connection != null) connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mainResults = (TextView)findViewById(R.id.show_result);
            mainResults.setText(s);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainUrlInput = findViewById(R.id.url_input);
        mainResults = findViewById(R.id.show_result);
        mainGetResults = findViewById(R.id.get_result);

        mainGetResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mainUrlInput.getText().toString();
                String apiUrl = "http://api.open-notify.org/";
                //String result = null;

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(apiUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                RetrofitInterface service = retrofit.create(RetrofitInterface.class);

                Call<Space> retroCall = service.getPageContent();

                retroCall.enqueue(new Callback<Space>() {
                    @Override
                    public void onResponse(Call<Space> call, Response<Space> response) {

                        if(response.isSuccessful()) {
                            mainResults.setText(String.valueOf(response.body().getNumber()));
                        }
                    }

                    @Override
                    public void onFailure(Call<Space> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Something went wrong. Please try again later!", Toast.LENGTH_SHORT).show();
                    }
                });
//                DownloadTask task = new DownloadTask();
//                try {
//                    result = task.execute(s).get();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

//                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                // ARE WE CONNECTED TO THE NET
//                if (networkInfo != null && networkInfo.isConnected() &&
//                        networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
//                        networkInfo.getType() != ConnectivityManager.TYPE_MOBILE) {
//                    System.out.println("WIFI ON!");
//                    Log.d("INTERNET", "WIFI ON!");
//                    DownloadTask task = new DownloadTask();
//                    try {
//                        result = task.execute(s).get();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                else {
//                    Log.d("Debug", "No WIFI!");
//                    Log.e("Error","NO WIFI!");
//                    Toast.makeText(getApplicationContext(), "WIFI!", Toast.LENGTH_SHORT).show();
//
//                }

            }

        });
    }
}
