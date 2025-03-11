package com.example.hw_0503;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView launchCountText, usageTimeText;
    private AppUsageTracker appUsageTracker;
    private Handler handler = new Handler();
    private Runnable updateTimeRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        launchCountText = findViewById(R.id.launchCountText);
        usageTimeText = findViewById(R.id.usageTimeText);
        appUsageTracker = (AppUsageTracker) getApplication();

        launchCountText.setText("Запусков: " + appUsageTracker.getLaunchCount());

        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                long totalTime = appUsageTracker.getTotalUsageTime() + appUsageTracker.getElapsedTime();
                usageTimeText.setText("Время в приложении: " + totalTime + " сек");
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateTimeRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimeRunnable);
        appUsageTracker.updateUsageTime();
    }
}