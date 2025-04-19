package com.example.weatherforecastapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecastapp.adapters.FavoriteCityAdapter;
import com.example.weatherforecastapp.db.AppDatabase;
import com.example.weatherforecastapp.db.FavoriteCity;
import com.example.weatherforecastapp.db.FavoriteCityDao;
import com.example.weatherforecastapp.models.WeatherResponse;
import com.example.weatherforecastapp.network.WeatherApiClient;
import com.example.weatherforecastapp.network.WeatherApiService;
import com.github.mikephil.charting.BuildConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView cityTextView;
    private RecyclerView favoritesRecyclerView;
    private FavoriteCityAdapter adapter;

    private String selectedCity;

    private final List<WeatherResponse> favoriteCitiesList = new ArrayList<>();
    private final HashMap<String, WeatherResponse> cityToWeatherMap = new HashMap<>();

    private AppDatabase db;
    private FavoriteCityDao favoriteCityDao;
    private EditText cityInput;
    private Button btnGetWeather, btnForecast, btnAddToFavorites;
    private TextView weatherResult;
    private RecyclerView recyclerFavorites;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityTextView = findViewById(R.id.cityInput);
        btnAddToFavorites = findViewById(R.id.btnAddToFavorites);
        favoritesRecyclerView = findViewById(R.id.recyclerFavorites);

        cityInput = findViewById(R.id.cityInput);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        weatherResult = findViewById(R.id.weatherResult);
        btnAddToFavorites = findViewById(R.id.btnAddToFavorites);
        btnForecast = findViewById(R.id.btnForecast);
        recyclerFavorites = findViewById(R.id.recyclerFavorites);

        db = AppDatabase.getDatabase(this);
        favoriteCityDao = db.favoriteCityDao();

        adapter = new FavoriteCityAdapter(favoriteCitiesList, this::onCityClicked);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesRecyclerView.setAdapter(adapter);

        loadFavoriteCities();

        btnGetWeather.setOnClickListener(v -> {
            String city = cityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                getWeather(city);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });

        btnForecast.setOnClickListener(v -> {
            String city = cityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ForecastActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Enter city first", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddToFavorites.setOnClickListener(v -> {
            if (selectedCity != null) {
                FavoriteCity newCity = new FavoriteCity(selectedCity);

                List<FavoriteCity> current = favoriteCityDao.getAll();
                boolean alreadyExists = false;
                for (FavoriteCity c : current) {
                    if (c.cityName.equalsIgnoreCase(selectedCity)) {
                        alreadyExists = true;
                        break;
                    }
                }

                if (!alreadyExists) {
                    favoriteCityDao.insert(newCity);
                    Toast.makeText(this, selectedCity + " added to favorites", Toast.LENGTH_SHORT).show();
                    loadFavoriteCities();
                } else {
                    Toast.makeText(this, selectedCity + " is already in favorites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override public boolean onMove(@NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                WeatherResponse weather = favoriteCitiesList.get(position);
                String city = weather.getName();
                favoriteCityDao.delete(new FavoriteCity(city));
                loadFavoriteCities();
                Toast.makeText(MainActivity.this, city + " removed from favorites", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(favoritesRecyclerView);

        // Тестовый выбор города
        selectedCity = "London";
        cityTextView.setText("Selected city: " + selectedCity);
    }

    private void loadFavoriteCities() {
        List<FavoriteCity> favoriteCities = favoriteCityDao.getAll();

        favoriteCitiesList.clear();
        cityToWeatherMap.clear();

        for (FavoriteCity favorite : favoriteCities) {
            getWeatherForFavorite(favorite.cityName);
        }
    }

    private void getWeatherForFavorite(String city) {
        WeatherApiService service = WeatherApiClient.getClient().create(WeatherApiService.class);
        Call<WeatherResponse> call = service.getWeatherByCity(city, "metric", BuildConfig.WEATHER_API_KEY);
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call,
                                   @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    cityToWeatherMap.put(city, weather);
                    updateWeatherList();
                } else {
                    Log.e("WEATHER", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                Log.e("WEATHER", "Failure: " + t.getMessage());
            }
        });
    }

    private void updateWeatherList() {
        favoriteCitiesList.clear();
        favoriteCitiesList.addAll(cityToWeatherMap.values());
        adapter.notifyDataSetChanged();
    }

    private void onCityClicked(String city) {
        Intent intent = new Intent(MainActivity.this, ForecastActivity.class);
        intent.putExtra("city", city);
        startActivity(intent);
    }
}
