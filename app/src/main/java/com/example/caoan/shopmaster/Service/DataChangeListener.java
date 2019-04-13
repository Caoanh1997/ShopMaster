package com.example.caoan.shopmaster.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.caoan.shopmaster.EventBus.ReadEvent;
import com.example.caoan.shopmaster.Model.Bill;
import com.example.caoan.shopmaster.OrderActivity;
import com.example.caoan.shopmaster.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class DataChangeListener extends Service {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String userID;
    private String token;
    private List<Bill> billList;
    private boolean isRead = true;

    public DataChangeListener() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        billList = new ArrayList<>();
        getInforUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("NewOrder");

        databaseReference.child(userID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (!dataSnapshot.getValue().toString().isEmpty()) {
                    sendNotification("Xem các đơn hàng mới cần xác nhận");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Toast.makeText(getApplicationContext(),"Change child",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //sendNotification("Đơn hàng đã được xóa");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Toast.makeText(getApplicationContext(),"Move child",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getApplicationContext(),"Cancell child",Toast.LENGTH_SHORT).show();
            }
        });

        return START_STICKY;
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

    public void getInforUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", "");
        token = sharedPreferences.getString("token", "");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe
    public void onReadEvent(ReadEvent readEvent) {
        //Toast.makeText(getApplicationContext(),"Read",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
