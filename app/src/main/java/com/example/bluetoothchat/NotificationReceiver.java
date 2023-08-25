package com.example.bluetoothchat;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver{
    private  SharedPreferences.Editor editor;
    private Context context;
    private NotificationManager notificationManager;
    public NotificationReceiver(){

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(1);
        BluetoothChatting.mute=true;
        Toast.makeText(context, "prawin", Toast.LENGTH_SHORT).show();

    }


}
