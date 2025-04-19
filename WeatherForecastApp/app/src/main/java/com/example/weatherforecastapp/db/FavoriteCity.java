package com.example.weatherforecastapp.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_cities")
public class FavoriteCity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String cityName;

    public FavoriteCity(String cityName) {
        this.cityName = cityName;
    }
}