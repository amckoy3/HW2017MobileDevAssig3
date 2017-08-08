package com.mckoy.howardchat;

import android.app.Service;

/**
 * Created by mckoy on 8/8/17.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MessageNotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("DEI-SERVICE", "service started");
        setupDatabase();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("DEI-SERVICE", "service stopped");
    }

    private void setupDatabase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messagesRef = databaseRef.child("messages");
        Query lastMessageQuery = messagesRef.limitToLast(1);

        lastMessageQuery.addValueEventListener(new ValueEventListener() {
            boolean mDidInitialLoad = false;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mDidInitialLoad == false) {
                    mDidInitialLoad = true;
                    return;
                }
                DataSnapshot messageSnapShot = dataSnapshot.getChildren().iterator().next();
                Message message = new Message(messageSnapShot);
                Log.i("DEI-SERVICE", "Received article change: " + message.getmUserName());
                showNotification(message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showNotification(Message message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Notification n = new NotificationCompat.Builder(this)
                .setContentTitle("New message")
                .setContentText("" + message.getmUserName() + " sent a message:  " + message.getmContent())
                .setSmallIcon(android.R.drawable.btn_dropdown)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(0, n);
    }
}


