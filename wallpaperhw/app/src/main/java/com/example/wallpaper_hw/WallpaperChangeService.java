package com.example.wallpaper_hw;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import java.io.IOException;

public class WallpaperChangeService extends Service {

    private final Handler handler = new Handler();
    private final int[] wallpapers = {
            R.drawable.wallpaper1,
            R.drawable.wallpaper2,
            R.drawable.wallpaper3
    };
    private int currentIndex = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        changeWallpaper();
        return START_STICKY;
    }

    private void changeWallpaper() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                try {
                    wallpaperManager.setBitmap(BitmapFactory.decodeResource(getResources(), wallpapers[currentIndex]));
                    currentIndex = (currentIndex + 1) % wallpapers.length;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                changeWallpaper();
            }
        }, 30000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
