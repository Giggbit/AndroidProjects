package com.example.weatherapp.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.weatherapp.utils.Constants;

public class RetrofitClient {
    private static Retrofit weatherRetrofit = null;
    private static Retrofit geoRetrofit = null;

    public static Retrofit getClient() {
        if (weatherRetrofit == null) {
            weatherRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.WEATHER_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return weatherRetrofit;
    }

    public static Retrofit getGeoClient() {
        if (geoRetrofit == null) {
            geoRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.GEO_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return geoRetrofit;
    }
}
