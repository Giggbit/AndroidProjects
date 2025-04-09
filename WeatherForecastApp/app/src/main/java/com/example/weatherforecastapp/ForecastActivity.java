package com.example.weatherforecastapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecastapp.adapters.ForecastAdapter;
import com.example.weatherforecastapp.models.ForecastResponse;
import com.example.weatherforecastapp.models.ForecastResponse.ForecastItem;
import com.example.weatherforecastapp.network.WeatherApiClient;
import com.example.weatherforecastapp.network.WeatherApiService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ForecastAdapter adapter;
    LineChart chart;
    Button btnAddToFavorites;

    private final String API_KEY = "d46eae5769b4f02b93e8090c1342f323";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        recyclerView = findViewById(R.id.recyclerForecast);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chart = findViewById(R.id.chartTemperature);
        btnAddToFavorites = findViewById(R.id.btnAddToFavorites);

        String city = getIntent().getStringExtra("city");
        if (city != null) {
            getForecast(city);
        }

        btnAddToFavorites.setOnClickListener(v -> {
            String cityName = getIntent().getStringExtra("city");
            if (cityName != null) {
                SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(cityName, cityName);
                editor.apply();
                Toast.makeText(this, cityName + " added to favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getForecast(String city) {
        WeatherApiService apiService = WeatherApiClient.getClient().create(WeatherApiService.class);
        Call<ForecastResponse> call = apiService.getForecast(city, API_KEY, "metric");

        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ForecastItem> forecastList = response.body().list;
                    adapter = new ForecastAdapter(forecastList);
                    recyclerView.setAdapter(adapter);
                    drawChart(forecastList);
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

    private void drawChart(List<ForecastItem> forecastList) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < forecastList.size(); i++) {
            float temp = (float) forecastList.get(i).main.temp;
            entries.add(new Entry(i, temp));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Temperature °C");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getDescription().setEnabled(false);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setEnabled(false);
        chart.invalidate();
        chart.setBackgroundColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.BLACK);
        chart.getXAxis().setTextColor(Color.BLACK);
        chart.getLegend().setTextColor(Color.BLACK);
        chart.getDescription().setTextColor(Color.BLACK);

    }
}
