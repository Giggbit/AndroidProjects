package com.example.weatherapp.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteCityDao {
    @Insert
    void insert(FavoriteCityEntity city);

    @Query("SELECT * FROM favorite_cities")
    List<FavoriteCityEntity> getAll();

    @Query("DELETE FROM favorite_cities WHERE name = :cityName")
    void deleteByName(String cityName);

    @Query("SELECT * FROM favorite_cities WHERE name = :cityName LIMIT 1")
    FavoriteCityEntity getCityByName(String cityName);

}
