package com.example.weatherforecastapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherforecastapp.models.WeatherResponse;
import com.example.weatherforecastapp.network.WeatherApiClient;
import com.example.weatherforecastapp.network.WeatherApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText cityInput;
    private Button btnGetWeather;
    private TextView weatherResult;

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

        Button btnForecast = findViewById(R.id.btnForecast);
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
}
