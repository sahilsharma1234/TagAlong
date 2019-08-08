package com.carpool.tagalong.retrofit;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    //Testing server

//    public   static final String BASE_URL = "http://ec2-54-68-223-134.us-west-2.compute.amazonaws.com:8000/";

    public   static final String BASE_URL = "https://api.tagalongride.com/";

    public   static final String IFRAMEURL = "https://stage.wepayapi.com/v2/";

//    public   static final String BASE_URL = "https://www.tagalongride.com:8443/";

    private  static  Retrofit retrofit = null;

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

    public HttpLoggingInterceptor getInterceptor() {
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout   (30,    TimeUnit.SECONDS)
            .writeTimeout  (30,   TimeUnit.SECONDS).addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    requestBuilder.header("Content-Type", "application/json");
                    return chain.proceed(requestBuilder.build());
                }
            })
            .addInterceptor(getInterceptor())
            .build();

    public RestClientInterface getApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestClientInterface service = retrofit.create(RestClientInterface.class);
        return service;
    }

    public RestClientInterface getIframeApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IFRAMEURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestClientInterface service = retrofit.create(RestClientInterface.class);
        return service;
    }
}