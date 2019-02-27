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

import java.util.List;

public class OrderAdapter extends ArrayAdapter<Bill> {
    private List<Bill> billList;
    private Context context;
    private FirebaseDatabase firebaseDatabase;

    public OrderAdapter(@NonNull Context context, @NonNull List<Bill> objects) {
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

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_item_layout,parent,false);

            viewHolder.tvproduct = convertView.findViewById(R.id.tvproduct);
            viewHolder.tvprice = convertView.findViewById(R.id.tvprice);
            viewHolder.tvuser = convertView.findViewById(R.id.tvuser);
            viewHolder.tvstate = convertView.findViewById(R.id.tvstate);
            viewHolder.tvstore = convertView.findViewById(R.id.tvstore);
            viewHolder.btconfirm = convertView.findViewById(R.id.btconfirm);

            final Bill bill = getItem(position);
            if (bill != null){
                viewHolder.tvproduct.setText(bill.getProduct());
                viewHolder.tvprice.setText(bill.getTotal_price());
                viewHolder.tvuser.setText(bill.getName_user()+", "+bill.getAddress()+", "+bill.getPhone());
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
                viewHolder.btconfirm.setEnabled(true);
                if(state.equals("Đã xác nhận")){
                    viewHolder.btconfirm.setEnabled(false);
                    viewHolder.btconfirm.setText("Đã xác nhận");
                }
                viewHolder.btconfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(),"Xác nhận", Toast.LENGTH_SHORT).show();
                        viewHolder.btconfirm.setText("Đã xác nhận");
                        viewHolder.btconfirm.setEnabled(false);
                        DatabaseReference reference = firebaseDatabase.getReference("Transport");
                        String userID = getContext().getSharedPreferences("Account",Context.MODE_PRIVATE)
                                .getString("userID","");
                        reference.child(userID).child(bill.getKey_cart())
                                .setValue(new Bill(bill.getKey_cart(),bill.getName_user(),bill.getAddress(),bill.getPhone()
                                        ,bill.getProduct(),bill.getTotal_price(),"Đã xác nhận",bill.getKey_store(),bill.getDatetime()));

                        DatabaseReference reference1 = firebaseDatabase.getReference("NewOrder");
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
        private TextView tvproduct, tvprice, tvuser, tvstate, tvstore;
        private Button btconfirm;
    }

    @NonNull
    @Override
    public Context getContext() {
        return context;
    }
}
