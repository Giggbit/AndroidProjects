package com.example.weatherapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherapp.R;
import com.example.weatherapp.WeatherDetailsActivity;
import com.example.weatherapp.database.FavoriteCityEntity;
import com.example.weatherapp.models.WeatherResponse;

import java.util.Calendar;
import java.util.List;

public class FavoriteCityAdapter extends RecyclerView.Adapter<FavoriteCityAdapter.ViewHolder> {

    private Context context;
    private List<FavoriteCityEntity> cityList;
    private List<WeatherResponse.ForecastItem> weatherList;

    public FavoriteCityAdapter(Context context, List<FavoriteCityEntity> cityList, List<WeatherResponse.ForecastItem> weatherList) {
        this.context = context;
        this.cityList = cityList;
        this.weatherList = weatherList;
    }

    public void removeItem(int position) {
        cityList.remove(position);
        weatherList.remove(position);
        notifyItemRemoved(position);
    }

    public FavoriteCityEntity getCityAtPosition(int position) {
        return cityList.get(position);
    }

    @NonNull
    @Override
    public FavoriteCityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_city_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteCityAdapter.ViewHolder holder, int position) {
        FavoriteCityEntity city = cityList.get(position);
        WeatherResponse.ForecastItem weather = weatherList.get(position);

        holder.cityTextView.setText(city.getCountry() + ", " + city.getName());
        holder.tempTextView.setText(String.format("%.0f°C", weather.getMain().getTemp()));
        holder.descriptionTextView.setText(weather.getWeather().get(0).getDescription());

        String iconUrl = "https://openweathermap.org/img/wn/" + weather.getWeather().get(0).getIcon() + "@2x.png";
        Glide.with(context).load(iconUrl).into(holder.iconImageView);

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WeatherDetailsActivity.class);
            intent.putExtra("city", city.getName());
            intent.putExtra("country", city.getCountry());
            intent.putExtra("lat", city.getLat());
            intent.putExtra("lon", city.getLon());
            context.startActivity(intent);
        });

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        View cardLayout = holder.itemView.findViewById(R.id.cardBackgroundLayout);
        if (hour >= 6 && hour < 18) {
            cardLayout.setBackgroundResource(R.drawable.bg_day);
        } else {
            cardLayout.setBackgroundResource(R.drawable.bg_night);
        }
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityTextView, tempTextView, descriptionTextView;
        ImageView iconImageView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityTextView = itemView.findViewById(R.id.cityName);
            tempTextView = itemView.findViewById(R.id.temperature);
            descriptionTextView = itemView.findViewById(R.id.weatherDescription);
            iconImageView = itemView.findViewById(R.id.weatherIcon);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
