package com.example.hw_notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("PLAY".equals(action)) {
            Toast.makeText(context, "Play/Pause Clicked!", Toast.LENGTH_SHORT).show();
        } else if ("PREV".equals(action)) {
            Toast.makeText(context, "Previous Clicked!", Toast.LENGTH_SHORT).show();
        } else if ("NEXT".equals(action)) {
            Toast.makeText(context, "Next Clicked!", Toast.LENGTH_SHORT).show();
        }
    }
}
