package com.example.home.api;

import com.example.home.model.Taskword_ph19997;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TaskWork {
    @GET("/mydata/taskwork_PH19997")
    Call<List<Taskword_ph19997>> getAllData();

    @POST("/mydata/taskwork_PH19997")
    Call<Taskword_ph19997> postDataToServer(@Body Taskword_ph19997 task);



    @DELETE("/mydata/taskwork_PH19997/{id}")
    Call<List<Taskword_ph19997>> deleteDataToServer(@Path("id") int id);

    @PATCH("/mydata/taskwork_PH19997/{id}")
    Call<Taskword_ph19997> fixDataOfServer(@Path("id") int id, @Body Taskword_ph19997 task);

}
