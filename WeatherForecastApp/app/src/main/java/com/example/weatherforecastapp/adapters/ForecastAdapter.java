package com.example.weatherforecastapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherforecastapp.R;
import com.example.weatherforecastapp.models.ForecastResponse;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private final List<ForecastResponse.ForecastItem> forecastList;

    public ForecastAdapter(List<ForecastResponse.ForecastItem> forecastList) {
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        ForecastResponse.ForecastItem item = forecastList.get(position);

        holder.date.setText(item.dt_txt);
        holder.temp.setText("Temp: " + item.main.temp + "°C");

        if (item.weather != null && !item.weather.isEmpty()) {
            holder.desc.setText("Weather: " + item.weather.get(0).description);
            Glide.with(holder.itemView.getContext())
                    .load("https://openweathermap.org/img/wn/" + item.weather.get(0).icon + "@2x.png")
                    .into(holder.icon);
        } else {
            holder.desc.setText("Weather: N/A");
            holder.icon.setImageResource(R.drawable.ic_placeholder);
        }

        holder.wind.setText("Wind: " + item.wind.speed + " m/s");
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView date, temp, desc, wind;
        ImageView icon;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.textDate);
            temp = itemView.findViewById(R.id.textTemp);
            desc = itemView.findViewById(R.id.textDesc);
            wind = itemView.findViewById(R.id.textWind);
            icon = itemView.findViewById(R.id.imageIcon);
        }
    }
}
