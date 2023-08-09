package com.example.home.api;

import com.example.home.model.Modeltest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("/mydata/uploadImage")
    Call<Modeltest> uploadImage(
            @Part MultipartBody.Part image,
            @Part("description") RequestBody description
    );

}
