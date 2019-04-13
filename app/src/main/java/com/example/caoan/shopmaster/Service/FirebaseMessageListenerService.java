package com.example.caoan.shopmaster.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.caoan.shopmaster.OrderActivity;
import com.example.caoan.shopmaster.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageListenerService extends FirebaseMessagingService {
    public FirebaseMessageListenerService() {
    }

    @Override
    public void onNewToken(String s) {
        System.out.println("New token: " + s);

        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String s) {
        SharedPreferences sharedPreferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", s);
        editor.commit();
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            String message = remoteMessage.getNotification().getBody();
            sendNotification(message);
        }
    }

    public void sendNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Notification");
        builder.setContentText(message);
        builder.setAutoCancel(true);

        Intent intent = new Intent(this, OrderActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(OrderActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
