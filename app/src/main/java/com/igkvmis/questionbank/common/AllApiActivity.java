package com.igkvmis.questionbank.common;

import com.igkvmis.questionbank.api.Api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllApiActivity {


    // this is for mobile network
//    private static final String BASE_URL = "http://192.168.43.2/softdeals_app/";
//    private static final String WEB_URL = "http://192.168.43.2/softdeals.in/";

    private static AllApiActivity mInstance;
    private static Retrofit retrofit;

    private AllApiActivity() {

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized AllApiActivity getInstance() {
        if (mInstance == null) {
            mInstance = new AllApiActivity();
        }
        return mInstance;
    }

    public Api getApi() {
        return retrofit.create(Api.class);
    }

//    public static String GET_SERVER_STATUS_URL = config.BASE_URL + "get_server_status.php";
//    public static String GET_FROM_LOGIN_VALUE_URL = config.BASE_URL + "getIsCheckFromLogin";
//    public static String GET_APPOINTMENT_LIST_URL = config.BASE_URL + "getBookAppointmentList";

}

