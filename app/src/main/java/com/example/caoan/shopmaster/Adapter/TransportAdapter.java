package com.example.caoan.shopmaster.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caoan.shopmaster.Model.Bill;
import com.example.caoan.shopmaster.Model.Store;
import com.example.caoan.shopmaster.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TransportAdapter extends ArrayAdapter<Bill> {
    private List<Bill> billList;
    private Context context;
    private FirebaseDatabase firebaseDatabase;
    private Calendar calendar;

    public TransportAdapter(@NonNull Context context, @NonNull List<Bill> objects) {
        super(context, 0, objects);
        billList = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.transport_item_layout,parent,false);

            viewHolder.tvproduct = convertView.findViewById(R.id.tvproduct);
            viewHolder.tvprice = convertView.findViewById(R.id.tvprice);
            viewHolder.tvuser = convertView.findViewById(R.id.tvuser);
            viewHolder.tvdatetime = convertView.findViewById(R.id.tvdatetime);
            viewHolder.tvstate = convertView.findViewById(R.id.tvstate);
            viewHolder.tvstore = convertView.findViewById(R.id.tvstore);
            viewHolder.bttransport = convertView.findViewById(R.id.bttransported);

            final Bill bill = getItem(position);
            if (bill != null){
                viewHolder.tvproduct.setText(bill.getProduct());
                viewHolder.tvprice.setText(bill.getTotal_price());
                viewHolder.tvuser.setText(bill.getName_user()+", "+bill.getAddress()+", "+bill.getPhone());
                viewHolder.tvdatetime.setText(bill.getDatetime());
                viewHolder.tvstate.setText(bill.getState());
                firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("Store");

                databaseReference.child(bill.getKey_store()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Store store = dataSnapshot.getValue(Store.class);
                        viewHolder.tvstore.setText(store.getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                String state = String.valueOf(viewHolder.tvstate.getText());
                viewHolder.bttransport.setEnabled(true);
                if(state.equals("Đã giao")){
                    viewHolder.bttransport.setEnabled(false);
                    viewHolder.bttransport.setText("Đã giao");
                }
                viewHolder.bttransport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(),"Đã giao", Toast.LENGTH_SHORT).show();
                        viewHolder.bttransport.setText("Đã giao");
                        viewHolder.bttransport.setEnabled(false);
                        DatabaseReference reference = firebaseDatabase.getReference("Delivered");
                        String userID = getContext().getSharedPreferences("Account",Context.MODE_PRIVATE)
                                .getString("userID","");
                        calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

                        String datetime_delivered = dateFormat.format(calendar.getTime())+" "+timeFormat.format(calendar.getTime());
                        reference.child(userID).child(bill.getKey_cart())
                                .setValue(new Bill(bill.getKey_cart(),bill.getName_user(),bill.getAddress(),bill.getPhone()
                                        ,bill.getProduct(),bill.getTotal_price(),"Đã giao",bill.getKey_store(),bill.getDatetime(),datetime_delivered));

                        DatabaseReference reference1 = firebaseDatabase.getReference("Transport");
                        reference1.child(userID).child(bill.getKey_cart()).removeValue();

                    }
                });

            }
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    class ViewHolder{
        private TextView tvproduct, tvprice, tvuser, tvdatetime, tvstate, tvstore;
        private Button bttransport;
    }

    @NonNull
    @Override
    public Context getContext() {
        return context;
    }
}
