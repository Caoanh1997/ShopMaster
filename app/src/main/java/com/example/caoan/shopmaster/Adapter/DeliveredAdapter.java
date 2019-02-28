package com.example.caoan.shopmaster.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.caoan.shopmaster.Model.Bill;
import com.example.caoan.shopmaster.Model.Store;
import com.example.caoan.shopmaster.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DeliveredAdapter extends ArrayAdapter<Bill> {
    private List<Bill> billList;
    private Context context;
    private FirebaseDatabase firebaseDatabase;

    public DeliveredAdapter(@NonNull Context context, @NonNull List<Bill> objects) {
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

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.delivered_item_layout,parent,false);
            viewHolder.tvproduct = convertView.findViewById(R.id.tvproduct);
            viewHolder.tvprice = convertView.findViewById(R.id.tvprice);
            viewHolder.tvuser = convertView.findViewById(R.id.tvuser);
            viewHolder.tvdatetime = convertView.findViewById(R.id.tvdatetime);
            viewHolder.tvstate = convertView.findViewById(R.id.tvstate);
            viewHolder.tvdatime_delivered = convertView.findViewById(R.id.tvdatetime_delivered);
            viewHolder.tvstore = convertView.findViewById(R.id.tvstore);

            Bill bill = getItem(position);
            if(bill != null){
                viewHolder.tvproduct.setText(bill.getProduct());
                viewHolder.tvprice.setText(bill.getTotal_price());
                viewHolder.tvuser.setText(bill.getName_user()+", "+bill.getAddress()+", "+bill.getPhone());
                viewHolder.tvdatetime.setText(bill.getDatetime());
                viewHolder.tvstate.setText(bill.getState());
                viewHolder.tvdatime_delivered.setText(bill.getDatetime_delivered());
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
            }
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    class ViewHolder{
        private TextView tvproduct, tvprice, tvuser, tvdatetime, tvstate, tvdatime_delivered, tvstore;
    }

    @NonNull
    @Override
    public Context getContext() {
        return context;
    }
}
