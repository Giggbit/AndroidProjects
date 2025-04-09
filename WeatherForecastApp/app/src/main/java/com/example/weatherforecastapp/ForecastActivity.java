package com.example.weatherforecastapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecastapp.adapters.ForecastAdapter;
import com.example.weatherforecastapp.models.ForecastResponse;
import com.example.weatherforecastapp.network.WeatherApiClient;
import com.example.weatherforecastapp.network.WeatherApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ForecastAdapter adapter;

    private final String API_KEY = "d46eae5769b4f02b93e8090c1342f323";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        recyclerView = findViewById(R.id.recyclerForecast);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String city = getIntent().getStringExtra("city");
        if (city != null) {
            getForecast(city);
        }
    }

    private void getForecast(String city) {
        WeatherApiService apiService = WeatherApiClient.getClient().create(WeatherApiService.class);
        Call<ForecastResponse> call = apiService.getForecast(city, API_KEY, "metric");

        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new ForecastAdapter(response.body().list);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(ForecastActivity.this, "Error loading forecast", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Toast.makeText(ForecastActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
