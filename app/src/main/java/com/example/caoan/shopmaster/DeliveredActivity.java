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

import com.example.caoan.shopmaster.Adapter.DeliveredAdapter;
import com.example.caoan.shopmaster.Adapter.TransportAdapter;
import com.example.caoan.shopmaster.Model.Bill;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeliveredActivity extends AppCompatActivity {

    private List<Bill> billList;
    private DeliveredAdapter deliveredAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ListView lvOrder;
    private Button btsize;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered);

        lvOrder = findViewById(R.id.lvorder);
        btsize = findViewById(R.id.btsize);
        progressBar = findViewById(R.id.progressbar);

        SharedPreferences sharedPreferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
        final String userID = sharedPreferences.getString("userID","");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Delivered").child(userID);

        Load();
        btsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),String.valueOf(billList.size()),Toast.LENGTH_SHORT).show();
                /*for(Bill bill : billList){
                    System.out.println(bill.toString());
                }*/
                if(progressBar.getVisibility()==View.INVISIBLE){
                    progressBar.setVisibility(View.VISIBLE);
                    //new ProcessGetBill().execute();
                }
                Load();
            }
        });
    }

    class ProcessGetBill extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog = new ProgressDialog(DeliveredActivity.this);
            //progressDialog.setMessage("Đang tải đơn hàng...");
            //progressDialog.setTitle();
            //progressDialog.setIndeterminate(true);
            //progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //progressDialog.dismiss();
            deliveredAdapter = new DeliveredAdapter(DeliveredActivity.this,billList);
            //deliveredAdapter.notifyDataSetChanged();
            //lvOrder.invalidateViews();
            lvOrder.setAdapter(deliveredAdapter);
            progressBar.setVisibility(View.INVISIBLE);
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
    public void Load(){
        new ProcessGetBill().execute();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                billList = new ArrayList<>();
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
    }
}
