package com.example.weatherapp.models;

import java.io.Serializable;

public class CityInfo implements Serializable {
    private String name;
    private String country;
    private String state;
    private double lat;
    private double lon;

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
