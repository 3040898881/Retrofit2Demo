package com.github.ikidou.util;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class RetrofitService {
    public static final String SERVER_BASE_URL = "http://localhost:4567/";

    private volatile static RetrofitService instance = null;
    private volatile static Retrofit retrofit = null;

    private static final long DEFAULT_TIMEOUT = 2000;

    private RetrofitService() {
    }

    public static RetrofitService getInstance() {
        if (instance == null) {
            synchronized (RetrofitService.class) {
                if (instance == null) {
                    instance = new RetrofitService();
                }
            }
        }
        return instance;
    }

    public Retrofit createRetrofit() {
        return createRetrofit(SERVER_BASE_URL);
    }

    public Retrofit createRetrofit(String baseUrl) {
        if (retrofit == null) {
            synchronized (RetrofitService.class) {
                if (retrofit == null) {
                    // OkHttp日志拦截器
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                    // OkHttp代理
                    SocketAddress ad = new InetSocketAddress("127.0.0.1", 8888);
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, ad);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .proxy(proxy)
                            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                            .build();

                    retrofit = new Retrofit.Builder()
                            .client(client)
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }
}