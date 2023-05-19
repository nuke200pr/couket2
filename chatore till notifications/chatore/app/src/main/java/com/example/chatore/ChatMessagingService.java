package com.example.chatore;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ChatMessagingService extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        util.updateDeviceToken(this ,token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        String title = message.getData().get(Constants.NOTIFICATION_TITLE);
        String Rmessage = message.getData().get(Constants.NOTIFICATION_MESSAGE);

        Intent intentChat = new Intent(this,loginactivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this ,0,intentChat,PendingIntent.FLAG_IMMUTABLE);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder ;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
          NotificationChannel channel= new NotificationChannel(Constants.CHANNEL_ID,Constants.CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
          channel.setDescription(Constants.CHANNEL_DESC);
          notificationManager.createNotificationChannel(channel);
          notificationBuilder = new NotificationCompat.Builder(this,Constants.CHANNEL_ID);

        }
        else
        {
            notificationBuilder = new NotificationCompat.Builder(this);
        }
        notificationBuilder.setSmallIcon(R.drawable.ic_baseline_send_24);
        notificationBuilder.setColor(getResources().getColor(R.color.light_green));
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);


        if(Rmessage.startsWith("https://firebasestorage.")) {
            try {
                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();

                Glide.with(this)
                        .asBitmap()
                        .load(message)
                        .into(new CustomTarget<Bitmap>(200,200) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                              bigPictureStyle.bigPicture(resource);
                              notificationBuilder.setStyle(bigPictureStyle);
                              notificationManager.notify(999,notificationBuilder.build());
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });

            } catch (Exception ex) {
                notificationBuilder.setContentText("New file received.");
            }
        }
        else {
            notificationBuilder.setContentText(Rmessage);


            notificationManager.notify(999, notificationBuilder.build());
        }




    }
}