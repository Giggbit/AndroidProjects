package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherapp.adapters.FavoriteCityAdapter;
import com.example.weatherapp.database.AppDatabase;
import com.example.weatherapp.database.FavoriteCityEntity;
import com.example.weatherapp.models.CityInfo;
import com.example.weatherapp.models.WeatherResponse;
import com.example.weatherapp.network.ApiService;
import com.example.weatherapp.network.RetrofitClient;
import com.example.weatherapp.utils.Constants;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import android.location.Location;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

// GEO
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.Task;
import android.content.IntentSender;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.weatherapp.utils.WeatherIconUtil;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private ImageButton searchButton;
    private RecyclerView favoritesRecyclerView;
    private FavoriteCityAdapter adapter;
    private List<FavoriteCityEntity> favoriteCityList = new ArrayList<>();
    private List<WeatherResponse.ForecastItem> weatherList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private CardView currentWeatherCard;
    private TextView currentCityText, currentTempText, currentWeatherDescText;
    private ImageView currentWeatherIcon;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int REQUEST_CHECK_SETTINGS = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.editCity);
        searchButton = findViewById(R.id.btnSearch);
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);

        currentWeatherCard = findViewById(R.id.currentWeatherCard);
        currentCityText = findViewById(R.id.currentCityText);
        currentTempText = findViewById(R.id.currentTempText);
        currentWeatherDescText = findViewById(R.id.currentWeatherDescText);
        currentWeatherIcon = findViewById(R.id.currentWeatherIcon);

        setDynamicBackground();

        adapter = new FavoriteCityAdapter(this, favoriteCityList, weatherList);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesRecyclerView.setAdapter(adapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermissionAndLoadWeather();

        searchButton.setOnClickListener(v -> {
            String cityName = cityEditText.getText().toString().trim();
            if (!cityName.isEmpty()) {
                fetchCityLocation(cityName);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                FavoriteCityEntity cityToDelete = adapter.getCityAtPosition(position);

                AppDatabase.getInstance(MainActivity.this).favoriteCityDao().deleteByName(cityToDelete.getName());
                adapter.removeItem(position);

                Toast.makeText(MainActivity.this, cityToDelete.getName() + " removed", Toast.LENGTH_SHORT).show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(favoritesRecyclerView);
    }

    // Загружает список избранных городов из базы данных и получает для них актуальную погоду
    private void loadFavoriteCities() {
        List<FavoriteCityEntity> citiesFromDb = AppDatabase.getInstance(this).favoriteCityDao().getAll();
        Log.d("FAVORITES", "Loaded " + citiesFromDb.size() + " favorite cities from DB");

        favoriteCityList.clear();
        weatherList.clear();
        adapter.notifyDataSetChanged();

        for (FavoriteCityEntity city : citiesFromDb) {
            fetchWeatherForCity(city);
        }
    }

    // Получает прогноз погоды по названию города (для каждого города из избранного)
    private void fetchWeatherForCity(FavoriteCityEntity city) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<WeatherResponse> call = apiService.getWeatherForecast(city.getName(), Constants.API_KEY, "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getList().isEmpty()) {
                    WeatherResponse.ForecastItem currentWeather = response.body().getList().get(0);
                    favoriteCityList.add(city);
                    weatherList.add(currentWeather);
                    adapter.notifyItemInserted(favoriteCityList.size() - 1);
                    Log.d("WEATHER_FETCH", "Weather added for " + city.getName());
                } else {
                    Log.w("WEATHER_FETCH", "Empty response for " + city.getName());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("WEATHER_FETCH", "Failed to load weather for " + city.getName(), t);
            }
        });
    }

    // Получает координаты города по названию (используется при поиске)
    private void fetchCityLocation(String cityName) {
        ApiService apiService = RetrofitClient.getGeoClient().create(ApiService.class);

        Call<List<CityInfo>> call = apiService.getCityLocation(cityName, 1, Constants.API_KEY);
        call.enqueue(new Callback<List<CityInfo>>() {
            @Override
            public void onResponse(Call<List<CityInfo>> call, Response<List<CityInfo>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    CityInfo cityInfo = response.body().get(0);

                    Intent intent = new Intent(MainActivity.this, WeatherDetailsActivity.class);
                    intent.putExtra("city", cityInfo.getName());
                    intent.putExtra("country", cityInfo.getCountry());
                    intent.putExtra("lat", cityInfo.getLat());
                    intent.putExtra("lon", cityInfo.getLon());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "City not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CityInfo>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Метод вызывается при возвращении к активности (например, после добавления города)
    @Override
    protected void onResume() {
        super.onResume();
        cityEditText.setText("");

        cityEditText.clearFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(cityEditText.getWindowToken(), 0);
        }
        loadFavoriteCities();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkLocationSettings();
        }
    }

    // Обрабатывает результат запроса на включение геолокацииs
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                checkLocationPermissionAndLoadWeather();
            } else {
                Toast.makeText(this, "Геолокация не включена", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Скрывает карточку с текущей погодой (например, если не удалось получить местоположение)
    private void hideCurrentWeatherCard() {
        currentWeatherCard.setVisibility(View.GONE);
    }

    // Проверяет наличие разрешений на геолокацию и запускает загрузку погоды
    private void checkLocationPermissionAndLoadWeather() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            hideCurrentWeatherCard();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            checkLocationSettings();
        }
    }

    // Обработка результата запроса разрешений на геолокацию
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationSettings();
            } else {
                Toast.makeText(this, "Требуется разрешение на геолокацию", Toast.LENGTH_SHORT).show();
                hideCurrentWeatherCard();
            }
        }
    }

    // Получает последнее известное местоположение и запрашивает по нему погоду
    private void getCurrentLocationWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    Log.d("LOCATION", "lat: " + lat + ", lon: " + lon);
                    fetchWeatherForCurrentLocation(lat, lon);
                } else {
                    Log.w("LOCATION", "Location is null");
                }
            }
        });
    }

    // Делает запрос к API для получения прогноза погоды по координатам
    private void fetchWeatherForCurrentLocation(double lat, double lon) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<WeatherResponse> call = apiService.getWeatherForecastByCoords(lat, lon, Constants.API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().getList().isEmpty()) {
                    WeatherResponse.ForecastItem currentWeather = response.body().getList().get(0);
                    String cityName = response.body().getCity().getName();
                    String countryName = response.body().getCity().getCountry();
                    showCurrentWeatherCard(cityName, countryName, currentWeather);
                } else {
                    hideCurrentWeatherCard();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                hideCurrentWeatherCard();
            }
        });
    }

    // Проверяет, включена ли геолокация на устройстве, и предлагает включить при необходимости
    private void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create().setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {
            getCurrentLocationWeather();
        });

        task.addOnFailureListener(this, e -> {
            hideCurrentWeatherCard();
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    sendEx.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Не удалось запросить включение геолокации", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Отображает карточку с текущей погодой: город, температура, описание и иконка
    private void showCurrentWeatherCard(String city, String country, WeatherResponse.ForecastItem item) {
        Log.d("UI_UPDATE", "Показываем карточку с погодой: " + city + ", " + country);
        currentWeatherCard.setVisibility(View.VISIBLE);
        currentCityText.setText(country + ", " + city);
        currentTempText.setText(String.format(Locale.getDefault(), "%.0f°C", item.getMain().getTemp()));
        currentWeatherDescText.setText(item.getWeather().get(0).getDescription());

        String iconCode = item.getWeather().get(0).getIcon();
        int iconResId = WeatherIconUtil.getWeatherIconResource(iconCode);
        currentWeatherIcon.setImageResource(iconResId);
    }

    // Устанавливает фоновое изображение карточки в зависимости от времени суток (день/ночь)
    private void setDynamicBackground() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        View currentWeatherCard = findViewById(R.id.currentWeatherCard);

        if (hour >= 6 && hour < 18) {
            currentWeatherCard.setBackgroundResource(R.drawable.bg_day);
        } else {
            currentWeatherCard.setBackgroundResource(R.drawable.bg_night);
        }
    }


}
