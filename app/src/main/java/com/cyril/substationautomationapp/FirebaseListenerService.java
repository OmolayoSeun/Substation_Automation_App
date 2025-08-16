package com.cyril.substationautomationapp;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FirebaseListenerService extends Service {
    private DatabaseReference databaseReference;
    private static final String NOTIFICATION_CHANNEL_ID = "FirebaseListenerChannel";
    private static final int FOREGROUND_NOTIFICATION_ID = 123; // For the foreground service
    private static final String TAG = "FirebaseListenerService";

    @Override
    public void onCreate() {
        super.onCreate();
        // IMPORTANT: Change "your_data_path" to the specific Firebase Realtime Database path
        // you want to listen to. For example, if you want to listen to "logs":
        databaseReference = FirebaseDatabase.getInstance().getReference("notifications");
        createNotificationChannel();
        Log.d(TAG, "Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service Started");
        startForeground(FOREGROUND_NOTIFICATION_ID, createForegroundNotification());
        startListeningForDataChanges();
        return START_STICKY; // Service will be restarted if killed by the system
    }

    private void startListeningForDataChanges() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "New data added: " + snapshot.getKey());

                DataSnapshot lastChild = null;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    lastChild = dataSnapshot;
                }
                if (lastChild != null) { showDataAddedNotification(lastChild);}

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle data changes if needed
                Log.d(TAG, "Data changed: " + snapshot.getKey());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle data removal if needed
                Log.d(TAG, "Data removed: " + snapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle data movement if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void showDataAddedNotification(DataSnapshot dataSnapshot) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class); // Or your desired activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0, // Request code
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );
        long time = Long.parseLong(Objects.requireNonNull(dataSnapshot.getKey()));


        LocalDateTime dateTime;
        String formatted = String.valueOf(time);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateTime = Instant.ofEpochSecond(time)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            formatted = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // Customize your notification content based on the new data
        String notificationTitle = "Alert From Substation";
        String message = dataSnapshot.child(Objects.requireNonNull(dataSnapshot.getKey())).getValue(String.class);
        // String notificationText = (message != null) ? message : "A new item was added: " + dataSnapshot.getKey();
        String notificationText = formatted + ": " + message; //

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(com.google.firebase.appcheck.interop.R.drawable.common_google_signin_btn_text_disabled) // **IMPORTANT: Add your notification icon**
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH); // For heads-up notification

        // Use a unique ID for each notification to show multiple notifications
        // or a fixed ID to update the existing one.
        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
        Log.d(TAG, "Notification sent for: " + dataSnapshot.getKey());
    }

    private Notification createForegroundNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class); // Activity to open when notification is tapped
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Firebase Listener Active")
                .setContentText("Monitoring new data entries in the background.")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // **IMPORTANT: Add your app icon (e.g., launcher icon)**
                .setContentIntent(pendingIntent)
                .setOngoing(true) // Makes the notification non-dismissible by swipe
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Substation", // User-visible name
                    NotificationManager.IMPORTANCE_DEFAULT // Or IMPORTANCE_HIGH for sound/vibration
            );
            serviceChannel.setDescription("Channel for Firebase data change notifications"); // User-visible description
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
                Log.d(TAG, "Notification channel created.");
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // We don't provide binding for this service
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // It's good practice to remove listeners when the service is destroyed,
        // though START_STICKY will try to restart it.
        // if (databaseReference != null && yourChildEventListener != null) {
        //     databaseReference.removeEventListener(yourChildEventListener);
        // }
        Log.d(TAG, "Service Destroyed");
    }
}
