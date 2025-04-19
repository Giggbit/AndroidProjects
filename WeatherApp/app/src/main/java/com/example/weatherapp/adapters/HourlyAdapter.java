package com.example.weatherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.models.WeatherResponse;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Адаптер для отображения почасового прогноза погоды в RecyclerView.
 * Используется в деталях прогноза на день.
 */
public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.HourViewHolder> {

    private final Context context;
    private final List<WeatherResponse.ForecastItem> hourlyList;

    public HourlyAdapter(Context context, List<WeatherResponse.ForecastItem> hourlyList) {
        this.context = context;
        this.hourlyList = hourlyList;
    }

    /**
     * Создает и возвращает новый ViewHolder для элемента списка.
     *
     * @param parent родительский ViewGroup
     * @param viewType тип элемента (в данном случае один тип)
     * @return новый экземпляр HourViewHolder
     */
    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hourly_forecast, parent, false);
        return new HourViewHolder(view);
    }

    /**
     * Заполняет данные для конкретного элемента списка.
     *
     * @param holder ViewHolder, содержащий элементы интерфейса
     * @param position позиция элемента в списке
     */
    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
        WeatherResponse.ForecastItem item = hourlyList.get(position);

        String time = formatTime(item.getDtTxt());
        String temp = String.format(Locale.getDefault(), "%.1f°C", item.getMain().getTemp());
        String desc = item.getWeather().get(0).getDescription();
        String iconCode = item.getWeather().get(0).getIcon();
        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

        holder.timeTextView.setText(time);
        holder.tempTextView.setText(temp);
        holder.descTextView.setText(desc);

        Glide.with(context).load(iconUrl).into(holder.weatherIcon);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 6 && hour < 18) {
            holder.itemView.setBackgroundResource(R.drawable.bg_day);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_night);
        }
    }

    /**
     * Возвращает общее количество элементов в списке.
     *
     * @return количество элементов в hourlyList
     */
    @Override
    public int getItemCount() {
        return hourlyList.size();
    }

    /**
     * ViewHolder, содержащий элементы интерфейса одного почасового прогноза:
     * время, температура, описание и иконка погоды.
     */
    static class HourViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView, tempTextView, descTextView;
        ImageView weatherIcon;

        public HourViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            tempTextView = itemView.findViewById(R.id.tempTextView);
            descTextView = itemView.findViewById(R.id.descTextView);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
        }
    }

    /**
     * Преобразует строку даты и времени в формат "HH:mm".
     *
     * @param dtTxt строка даты и времени в формате "yyyy-MM-dd HH:mm:ss"
     * @return строка с отформатированным временем ("HH:mm")
     */
    private String formatTime(String dtTxt) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dtTxt);
            return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
        } catch (ParseException e) {
            return dtTxt;
        }
    }
}
