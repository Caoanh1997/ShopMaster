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
        billList = new ArrayList<>();

        new ProcessGetBill().execute();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Bill bill = snapshot.getValue(Bill.class);
                    System.out.println(bill.toString());
                    billList.add(bill);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),String.valueOf(billList.size()),Toast.LENGTH_SHORT).show();
                /*for(Bill bill : billList){
                    System.out.println(bill.toString());
                }*/
            }
        });
    }

    class ProcessGetBill extends AsyncTask<Void, Void, Void> {
        //private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(TransportActivity.this);
//            progressDialog.setMessage("Đang tải đơn hàng...");
//            //progressDialog.setTitle();
//            progressDialog.setIndeterminate(true);
//            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //progressDialog.dismiss();
            progressBar.setVisibility(View.INVISIBLE);
            transportAdapter = new TransportAdapter(TransportActivity.this,billList);
            lvOrder.setAdapter(transportAdapter);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for(int i=1;i<=100;i++){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
