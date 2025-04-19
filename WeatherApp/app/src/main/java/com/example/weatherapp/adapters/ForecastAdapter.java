package com.example.weatherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.models.WeatherResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.DayViewHolder> {

    private final List<String> dates;
    private final Map<String, List<WeatherResponse.ForecastItem>> groupedData;
    private final Context context;

    public ForecastAdapter(Context context, List<String> dates, Map<String, List<WeatherResponse.ForecastItem>> groupedData) {
        this.context = context;
        this.dates = dates;
        this.groupedData = groupedData;
    }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_forecast, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position) {
        String dateKey = dates.get(position);
        holder.dateTextView.setText(formatDate(dateKey));

        HourlyAdapter adapter = new HourlyAdapter(context, groupedData.get(dateKey));
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        RecyclerView recyclerView;

        public DayViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            recyclerView = itemView.findViewById(R.id.innerRecyclerView);
        }
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            return new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }
}
