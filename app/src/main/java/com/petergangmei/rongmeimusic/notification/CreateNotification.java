package com.petergangmei.rongmeimusic.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.common.collect.BiMap;
import com.petergangmei.rongmeimusic.MainActivity;
import com.petergangmei.rongmeimusic.R;
import com.petergangmei.rongmeimusic.model.Music;
import com.petergangmei.rongmeimusic.services.NotificationActionServices;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class CreateNotification {
    public static  final String CHANNEL_ID = "channelid";
    private static  final String ACTION_PREVIOUS = "actionprevious";
    private static  final String ACTION_PLAY = "actionplay";
    private static  final String ACTION_NEXT = "actionnext";

    private static Notification notification;

    public static void createNotification(Context context, Music music, int playbutton, int pos, int size){
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
           MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");
//           int pic

           Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_music);

//           actionprevious
           PendingIntent pendingIntentPrevious;
           int drw_previous;
           if(pos == 0){
               pendingIntentPrevious = null;
               drw_previous = 0;
           }else {
               Intent intentPrevious = new Intent(context, NotificationActionServices.class)
                       .setAction(ACTION_PREVIOUS);
               pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                       intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
               drw_previous = R.drawable.ic_skip_previous;
           }

           PendingIntent pendingIntentPlay;
           Intent intentPlay = new Intent(context, NotificationActionServices.class)
                   .setAction(ACTION_PLAY);
           pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                   intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
           int drw_play = R.drawable.ic_play;

           PendingIntent pendingIntentNext;
           int drw_next;
           if(pos == size){
               pendingIntentNext = null;
               drw_next = 0;
           }else {
               Intent intentNext = new Intent(context, NotificationActionServices.class)
                       .setAction(ACTION_NEXT);
               pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                       intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
               drw_next = R.drawable.ic_skip_next;
           }

           PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                   new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

               notification = new NotificationCompat.Builder(context, CHANNEL_ID)
//                       .setContentIntent(contentIntent)
                       .setSmallIcon(R.drawable.ic_play)
                       .setContentTitle(music.getTitle())
                       .setContentText(music.getArtist())
                       .setLargeIcon(icon)
                       .setOnlyAlertOnce(true)
                       .setShowWhen(false)
                       .setOngoing(true)
                       .addAction(drw_previous, "Previous", pendingIntentPrevious)
                       .addAction(playbutton, "Play", pendingIntentPlay)
                       .addAction(drw_next, "Next", pendingIntentNext)
                       .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                               .setShowCancelButton(true)
                               .setMediaSession(mediaSessionCompat.getSessionToken()))
                       .setPriority(NotificationCompat.PRIORITY_HIGH)
                       .build();

           notificationManagerCompat.notify(1,notification);

        }
    }

}
