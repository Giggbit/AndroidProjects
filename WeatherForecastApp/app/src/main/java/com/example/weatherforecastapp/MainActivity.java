package com.example.weatherforecastapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherforecastapp.models.WeatherResponse;
import com.example.weatherforecastapp.network.WeatherApiClient;
import com.example.weatherforecastapp.network.WeatherApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText cityInput;
    private Button btnGetWeather;
    private Button btnForecast;
    private TextView weatherResult;
    private Button btnAddToFavorites;
    private String selectedCity = null;

    private final String API_KEY = "d46eae5769b4f02b93e8090c1342f323";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        weatherResult = findViewById(R.id.weatherResult);

        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityInput.getText().toString().trim();
                if (!city.isEmpty()) {
                    getWeather(city);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnForecast = findViewById(R.id.btnForecast);
        btnForecast.setOnClickListener(v -> {
            String city = cityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ForecastActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);
                Toast.makeText(this, "Город: " + city, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Enter city first", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddToFavorites = findViewById(R.id.btnAddToFavorites);
        btnAddToFavorites.setOnClickListener(v -> {
            if (selectedCity != null) {
                SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(selectedCity, selectedCity);
                editor.apply();
                Toast.makeText(MainActivity.this, selectedCity + " added to favorites", Toast.LENGTH_SHORT).show();
                loadFavoriteCities();
            }
        });
    }

    private void getWeather(String city) {
        WeatherApiService apiService = WeatherApiClient.getClient().create(WeatherApiService.class);

        Call<WeatherResponse> call = apiService.getWeatherByCity(city, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weather = response.body();
                    String result = "Temperature: " + weather.main.temp + "°C\n" +
                            "Humidity: " + weather.main.humidity + "%\n" +
                            "Wind speed: " + weather.wind.speed + " m/s\n" +
                            "Description: " + weather.weather.get(0).description;
                    weatherResult.setText(result);
                    selectedCity = city;
                    btnAddToFavorites.setVisibility(View.VISIBLE);
                    loadFavoriteCities();
                } else {
                    weatherResult.setText("City not found.");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                weatherResult.setText("Error: " + t.getMessage());
            }
        });
    }

    private void loadFavoriteCities() {
        SharedPreferences prefs = getSharedPreferences("favorites", MODE_PRIVATE);
        Map<String, ?> favorites = prefs.getAll();

        LinearLayout container = findViewById(R.id.favoriteCitiesContainer);
        container.removeAllViews();

        for (String city : favorites.keySet()) {
            getWeatherCard(city, container);
        }
    }

    private void getWeatherCard(String city, LinearLayout container) {
        WeatherApiService apiService = WeatherApiClient.getClient().create(WeatherApiService.class);
        Call<WeatherResponse> call = apiService.getWeatherByCity(city, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();

                    View card = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_weather_card, container, false);
                    TextView cityText = card.findViewById(R.id.textCity);
                    TextView tempText = card.findViewById(R.id.textTempCard);

                    cityText.setText(city);
                    tempText.setText("Temp: " + weather.main.temp + "°C");

                    card.setOnClickListener(v -> {
                        Intent intent = new Intent(MainActivity.this, ForecastActivity.class);
                        intent.putExtra("city", city);
                        startActivity(intent);
                    });

                    container.addView(card);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // ничего не делаем
            }
        });
    }

}
