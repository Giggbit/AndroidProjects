package com.example.weatherforecastapp.adapters;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherforecastapp.R;
import com.example.weatherforecastapp.models.WeatherResponse;

import java.util.List;

public class FavoriteCityAdapter extends RecyclerView.Adapter<FavoriteCityAdapter.ViewHolder> {

    public interface OnCityClickListener {
        void onCityClick(String city);
    }

    private final List<WeatherResponse> cities;
    private final OnCityClickListener listener;

    public FavoriteCityAdapter(List<WeatherResponse> cities, OnCityClickListener listener) {
        this.cities = cities;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherResponse city = cities.get(position);
        holder.cityName.setText(city.name);
        holder.temp.setText("Temp: " + city.main.temp + "°C");

        if (city.weather != null && !city.weather.isEmpty()) {
            String iconCode = city.weather.get(0).icon;
            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

            Glide.with(holder.itemView.getContext())
                    .load(iconUrl)
                    .into(holder.weatherIcon);
        }

        holder.itemView.setOnClickListener(v -> listener.onCityClick(city.name));
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cityName, temp;
        ImageView weatherIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.textCity);
            temp = itemView.findViewById(R.id.textTempCard);
            weatherIcon = itemView.findViewById(R.id.imageIcon);
        }
    }

    public void removeItem(int position) {
        cities.remove(position);
        notifyItemRemoved(position);
    }
}
