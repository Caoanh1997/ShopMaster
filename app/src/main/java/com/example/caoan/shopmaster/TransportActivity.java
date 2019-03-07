package com.example.caoan.shopmaster;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.caoan.shopmaster.Adapter.OrderAdapter;
import com.example.caoan.shopmaster.Adapter.TransportAdapter;
import com.example.caoan.shopmaster.Model.Bill;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransportActivity extends AppCompatActivity {

    private List<Bill> billList;
    private TransportAdapter transportAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ListView lvOrder;
    private Button btsize;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);

        lvOrder = findViewById(R.id.lvorder);
        btsize = findViewById(R.id.btsize);
        progressBar = findViewById(R.id.progressbar);

        SharedPreferences sharedPreferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
        final String userID = sharedPreferences.getString("userID","");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Transport").child(userID);

        Load();
        btsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),String.valueOf(billList.size()),Toast.LENGTH_SHORT).show();
                Load();
            }
        });
    }

    public void Load(){
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        billList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                billList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Bill bill = snapshot.getValue(Bill.class);
                    billList.add(bill);
                }
                progressBar.setVisibility(View.INVISIBLE);
                lvOrder.setVisibility(View.VISIBLE);
                transportAdapter = new TransportAdapter(TransportActivity.this,billList);
                lvOrder.setAdapter(transportAdapter);
                lvOrder.invalidateViews();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
