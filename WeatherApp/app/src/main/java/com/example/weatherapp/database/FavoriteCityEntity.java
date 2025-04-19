package com.example.weatherapp.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_cities")
public class FavoriteCityEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String country;
    private double lat;
    private double lon;

    public FavoriteCityEntity(String name, String country, double lat, double lon) {
        this.name = name;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public String getCountry() {
        return country;
    }
    public double getLat() {
        return lat;
    }
    public double getLon() {
        return lon;
    }
}
