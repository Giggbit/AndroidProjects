package com.example.weatherforecastapp.db;

import android.content.Context;
import androidx.room.Room;

public class DatabaseClient {
    private static DatabaseClient instance;
    private final AppDatabase database;

    private DatabaseClient(Context context) {
        database = Room.databaseBuilder(context, AppDatabase.class, "WeatherAppDB").build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
