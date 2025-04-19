package com.example.weatherapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {

    @SerializedName("list")
    private List<ForecastItem> list;

    @SerializedName("city")
    private City city;

    public List<ForecastItem> getList() {
        return list;
    }

    public City getCity() {
        return city;
    }

    public static class ForecastItem {

        @SerializedName("dt_txt")
        private String dtTxt;

        @SerializedName("main")
        private Main main;

        @SerializedName("weather")
        private List<Weather> weather;

        public String getDtTxt() {
            return dtTxt;
        }

        public Main getMain() {
            return main;
        }

        public List<Weather> getWeather() {
            return weather;
        }
    }

    public static class Main {
        @SerializedName("temp")
        private double temp;

        public double getTemp() {
            return temp;
        }
    }

    public static class Weather {
        @SerializedName("description")
        private String description;

        @SerializedName("icon")
        private String icon;

        public String getDescription() {
            return description;
        }

        public String getIcon() {
            return icon;
        }
    }

    public static class City {
        @SerializedName("name")
        private String name;

        @SerializedName("country")
        private String country;

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }
    }
}
