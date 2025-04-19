package com.example.weatherforecastapp.db;

import android.content.Context;
import androidx.room.*;

@Database(entities = {FavoriteCity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FavoriteCityDao favoriteCityDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class, "weather_db"
                    ).allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}
