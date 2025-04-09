package com.example.weatherforecastapp.models;

import java.util.List;

public class ForecastResponse {
    public List<ForecastItem> list;

    public static class ForecastItem {
        public Main main;
        public List<Weather> weather;
        public Wind wind;
        public String dt_txt;
    }

    public static class Main {
        public float temp;
        public int humidity;
    }

    public static class Weather {
        public String description;
        public String icon;
    }

    public static class Wind {
        public float speed;
    }
}
