package com.example.weatherapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.adapters.ForecastAdapter;
import com.example.weatherapp.database.AppDatabase;
import com.example.weatherapp.database.FavoriteCityEntity;
import com.example.weatherapp.models.WeatherResponse;
import com.example.weatherapp.network.ApiService;
import com.example.weatherapp.network.RetrofitClient;
import com.example.weatherapp.utils.Constants;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDetailsActivity extends AppCompatActivity {

    private TextView cityTextView;
    private RecyclerView forecastRecyclerView;
    private ImageButton backButton;
    private ImageButton btnAddToFavorites;

    private SwipeRefreshLayout swipeRefreshLayout;

    private String city;
    private String country;
    private double lat;
    private double lon;

    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchWeatherData(city);
        });

        cityTextView = findViewById(R.id.cityTextView);
        forecastRecyclerView = findViewById(R.id.forecastRecyclerView);
        backButton = findViewById(R.id.backButton);
        btnAddToFavorites = findViewById(R.id.btnAddToFavorites);

        city = getIntent().getStringExtra("city");
        country = getIntent().getStringExtra("country");
        lat = getIntent().getDoubleExtra("lat", 0);
        lon = getIntent().getDoubleExtra("lon", 0);

        cityTextView.setText(country + ", " + city);

        backButton.setOnClickListener(v -> finish());

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        FavoriteCityEntity existingCity = db.favoriteCityDao().getCityByName(city);
        isFavorite = existingCity != null;
        updateFavoriteButtonIcon();

        btnAddToFavorites.setOnClickListener(v -> {
            if (isFavorite) {
                db.favoriteCityDao().deleteByName(city);
                isFavorite = false;
                Toast.makeText(this, "Город удалён из избранного", Toast.LENGTH_SHORT).show();
            } else {
                FavoriteCityEntity cityEntity = new FavoriteCityEntity(city, country, lat, lon);
                db.favoriteCityDao().insert(cityEntity);
                isFavorite = true;
                Toast.makeText(this, "Город добавлен в избранное", Toast.LENGTH_SHORT).show();
            }
            updateFavoriteButtonIcon();
        });
        fetchWeatherData(city);
    }

    // Загружает 5-дневный прогноз погоды по названию города
    private void fetchWeatherData(String cityName) {
        swipeRefreshLayout.setRefreshing(true);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<WeatherResponse> call = apiService.getWeatherForecast(cityName, Constants.API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WeatherResponse.ForecastItem> forecastList = response.body().getList();

                    Map<String, List<WeatherResponse.ForecastItem>> grouped = new LinkedHashMap<>();
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat dateKeyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    for (WeatherResponse.ForecastItem item : forecastList) {
                        try {
                            Date date = inputFormat.parse(item.getDtTxt());
                            String key = dateKeyFormat.format(date);
                            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    List<String> dates = new ArrayList<>(grouped.keySet());
                    forecastRecyclerView.setLayoutManager(new LinearLayoutManager(WeatherDetailsActivity.this));
                    forecastRecyclerView.setAdapter(new ForecastAdapter(WeatherDetailsActivity.this, dates, grouped));
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(WeatherDetailsActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(WeatherDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                Log.e("WeatherDetails", "Failure: " + t.getMessage());
            }
        });
    }

    /**
     * Обновляет иконку кнопки "Добавить в избранное" в зависимости от того,
     * находится ли текущий город в списке избранных.
     */
    private void updateFavoriteButtonIcon() {
        if (isFavorite) {
            btnAddToFavorites.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            btnAddToFavorites.setImageResource(R.drawable.ic_add_to_favorites);
        }
    }
}
