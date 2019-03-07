package com.example.caoan.shopmaster;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.caoan.shopmaster.Adapter.OrderAdapter;
import com.example.caoan.shopmaster.Model.Bill;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderManagementActivity extends AppCompatActivity {

    private List<Bill> billList;
    private OrderAdapter orderAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ListView lvOrder;
    private Button btsize;
    private ProgressBar progressBar;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private Intent resultIntent;
    private int notificationId = 001;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        lvOrder = findViewById(R.id.lvorder);
        btsize = findViewById(R.id.btsize);
        progressBar = findViewById(R.id.progressbar);

        SharedPreferences sharedPreferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
        final String userID = sharedPreferences.getString("userID", "");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("NewOrder").child(userID);
        Load();
        btsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), String.valueOf(billList.size()), Toast.LENGTH_SHORT).show();
                Load();
            }
        });
    }

    public void Load() {
        progressBar.setVisibility(View.VISIBLE);
        lvOrder.setVisibility(View.INVISIBLE);

        billList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                billList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Bill bill = snapshot.getValue(Bill.class);
                    billList.add(bill);
                }
                progressBar.setVisibility(View.INVISIBLE);
                lvOrder.setVisibility(View.VISIBLE);
                orderAdapter = new OrderAdapter(OrderManagementActivity.this, billList);
                lvOrder.setAdapter(orderAdapter);
                lvOrder.invalidateViews();

                //send notification
                builder = new NotificationCompat.Builder(getApplicationContext());
                builder.setAutoCancel(true);
                builder.setSmallIcon(R.drawable.ic_transport_active);
                builder.setContentTitle("Thông báo");
                builder.setContentText("Bạn nhận được đơn hàng mới");

                pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), ShopActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                builder.setContentIntent(pendingIntent);
                notificationManager.notify(notificationId, builder.build());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}