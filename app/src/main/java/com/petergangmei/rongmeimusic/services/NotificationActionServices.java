package com.petergangmei.rongmeimusic.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.petergangmei.rongmeimusic.ItemClickInterface;

public class NotificationActionServices extends BroadcastReceiver {
    ItemClickInterface itemClickInterface;
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("TRACKS_TRACKS")
        .putExtra("actionname", intent.getAction()));

        Log.d("serviceTAG", "......"+intent.getAction());

    }
}
