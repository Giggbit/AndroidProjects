package com.example.weatherforecastapp.models;

import java.util.List;

public class WeatherResponse {
    public Main main;
    public List<Weather> weather;
    public Wind wind;

    public class Main {
        public float temp;
        public int humidity;
    }

    public class Weather {
        public String description;
        public String icon;
    }

    public class Wind {
        public float speed;
    }
}
