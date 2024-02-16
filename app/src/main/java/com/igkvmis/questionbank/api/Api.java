package com.igkvmis.questionbank.api;

import com.igkvmis.questionbank.model.LoginRes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @FormUrlEncoded
    @POST("CheckAdminLogin")
    Call<LoginRes> getLogin(@Field("Admin_ID") String adminid,
                            @Field("Admin_Password") String pass
    );


    @FormUrlEncoded
    @POST("GetString")
    Call<LoginRes> temp(@Field("temp") String temp

    );

    @Multipart
    @POST("UploadFile")
    Call<String> uploadFile(
            @Part MultipartBody.Part file, @Part("filename") RequestBody name
    );
}

