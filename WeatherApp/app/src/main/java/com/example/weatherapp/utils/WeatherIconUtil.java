package com.example.weatherapp.utils;

import com.example.weatherapp.R;

public class WeatherIconUtil {

    public static int getWeatherIconResource(String iconCode) {
        if (iconCode == null) return R.drawable.default_icon;

        switch (iconCode) {
            case "01d":
            case "01n":
                return R.drawable.clear_sky;
            case "02d":
            case "02n":
                return R.drawable.partly_cloudy;
            case "03d":
            case "03n":
                return R.drawable.cloudy;
            case "04d":
            case "04n":
                return R.drawable.broken_clouds;
            case "09d":
            case "09n":
                return R.drawable.rain;
            case "10d":
            case "10n":
                return R.drawable.heavy_rain;
            case "11d":
            case "11n":
                return R.drawable.thunderstorm;
            case "13d":
            case "13n":
                return R.drawable.snow;
            case "50d":
            case "50n":
                return R.drawable.fog;
            default:
                return R.drawable.default_icon;
        }
    }

//    public static int getBackgroundResource(String iconCode) {
//        if (iconCode.contains("d")) {
//            if (iconCode.startsWith("01")) {
//                return R.drawable.clear_sky;
//            } else if (iconCode.startsWith("02") || iconCode.startsWith("03") || iconCode.startsWith("04")) {
//                return R.drawable.clear_sky;
//            } else {
//                return R.drawable.bg_day; // default
//            }
//        } else {
//            if (iconCode.startsWith("01")) {
//                return R.drawable.clear_sky;
//            } else {
//                return R.drawable.clear_sky; // default
//            }
//        }
//    }


}
