package com.example.weatherforecastapp.db;

import androidx.room.*;
import java.util.List;

@Dao
public interface FavoriteCityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(FavoriteCity city);

    @Delete
    void delete(FavoriteCity city);

    @Query("SELECT * FROM favorite_cities")
    List<FavoriteCity> getAll();
}