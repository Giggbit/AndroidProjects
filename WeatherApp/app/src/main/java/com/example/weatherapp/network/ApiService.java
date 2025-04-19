package com.example.weatherapp.network;

import com.example.weatherapp.models.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("forecast")
    Call<WeatherResponse> getWeatherForecast(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

    @GET("direct")
    Call<java.util.List<com.example.weatherapp.models.CityInfo>> getCityLocation(
            @Query("q") String cityName,
            @Query("limit") int limit,
            @Query("appid") String apiKey
    );

    @GET("forecast")
    Call<WeatherResponse> getWeatherForecastByCoords(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

}
