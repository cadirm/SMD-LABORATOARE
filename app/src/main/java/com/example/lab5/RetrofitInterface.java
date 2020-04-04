package com.example.lab5;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitInterface {
    @GET("astros")
    Call<Space> getPageContent();
}