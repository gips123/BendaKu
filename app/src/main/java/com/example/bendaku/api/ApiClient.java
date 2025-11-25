package com.example.bendaku.api;

import android.content.Context;

import com.example.bendaku.utils.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Strapi base URL - use 10.0.2.2 for Android emulator to access localhost
    private static final String BASE_URL = "http://10.0.2.2:1338/";
    // For real device, use your computer's local IP: "http://192.168.1.xxx:1338/"

    private static Retrofit retrofit = null;
    private static Context appContext = null;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(logging);

            // Add Authorization interceptor if context is available
            if (appContext != null) {
                clientBuilder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        SessionManager sessionManager = new SessionManager(appContext);
                        String jwt = sessionManager.getJwtToken();

                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Content-Type", "application/json");

                        // Add Authorization header if JWT token exists
                        if (jwt != null && !jwt.isEmpty()) {
                            requestBuilder.header("Authorization", "Bearer " + jwt);
                        }

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });
            }

            OkHttpClient client = clientBuilder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}
