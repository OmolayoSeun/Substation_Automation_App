package com.cyril.substationautomationapp;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FirebaseBackgroundService extends Service {

    private DatabaseReference databaseRef;
    private ValueEventListener valueEventListener;

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String channelId = "substation_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Firebase Listener",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Substation Service Running")
                .setContentText("Listening for updates...")
                .setSmallIcon(R.drawable.ic_notification)
                .build();

        startForeground(1001, notification);

        listenToFirebase();
        return START_STICKY;
    }

    private void listenToFirebase() {
        databaseRef = FirebaseDatabase.getInstance().getReference("notifications");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new Thread(() -> {
                    DataSnapshot lastChild = null;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        lastChild = snapshot;
                    }
                    if (lastChild != null) {

                        long time = Long.parseLong(Objects.requireNonNull(lastChild.getKey()));


                        LocalDateTime dateTime;
                        String formatted = String.valueOf(time);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            dateTime = Instant.ofEpochSecond(time)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime();

                            formatted = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        }

                        String message = lastChild.child(Objects.requireNonNull(lastChild.getKey())).getValue(String.class);
                        String notificationText = formatted + ": " + message;


                        showNotification(notificationText);
                    }

                }).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        databaseRef.addValueEventListener(valueEventListener);
    }

    private void showNotification(String message) {
        String channelId = "firebase_channel";

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Firebase Listener", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Substation Alert!")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        manager.notify(1, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseRef != null && valueEventListener != null) {
            databaseRef.removeEventListener(valueEventListener);
        }
    }
}

