package com.example.hw_0503;

import android.app.Application;
import android.content.SharedPreferences;

public class AppUsageTracker extends Application {
    private SharedPreferences preferences;
    private long startTime;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences("AppUsagePrefs", MODE_PRIVATE);

        int launches = preferences.getInt("launch_count", 0) + 1;
        preferences.edit().putInt("launch_count", launches).apply();
        startTime = System.currentTimeMillis();
    }

    public int getLaunchCount() {
        return preferences.getInt("launch_count", 0);
    }

    public long getTotalUsageTime() {
        return preferences.getLong("total_usage_time", 0);
    }

    public long getElapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    public void updateUsageTime() {
        long elapsedTime = getElapsedTime();
        preferences.edit().putLong("total_usage_time", getTotalUsageTime() + elapsedTime).apply();
    }
}