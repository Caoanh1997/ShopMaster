package com.example.caoan.shopmaster.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caoan.shopmaster.EventBus.FailedEvent;
import com.example.caoan.shopmaster.EventBus.LoadEvent;
import com.example.caoan.shopmaster.EventBus.SucessEvent;
import com.example.caoan.shopmaster.Model.Bill;
import com.example.caoan.shopmaster.Model.Cart;
import com.example.caoan.shopmaster.Model.Store;
import com.example.caoan.shopmaster.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BillTransportAdapter extends BaseExpandableListAdapter implements Filterable {

    private Context context;
    private List<Bill> billList;
    private HashMap<Bill, List<Cart>> ListDetailBill;
    private String key;
    private String key_master;
    private String userID;
    private FirebaseDatabase firebaseDatabase;
    private List<Cart> cartList;
    private Fragment fragment;
    private Bill item;
    private List<Bill> filter;

    public BillTransportAdapter(Context context, List<Bill> billList, HashMap<Bill, List<Cart>> listDetailBill, Fragment fragment) {
        this.context = context;
        this.billList = billList;
        ListDetailBill = listDetailBill;
        this.fragment = fragment;
        this.filter = billList;
    }

    @Override
    public int getGroupCount() {
        return billList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return ListDetailBill.get(billList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return billList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return ListDetailBill.get(billList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        final Bill bill = (Bill) getGroup(i);
        if(view == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.bill_item_transport_layout,null);
        }
        TextView tvkeycart = view.findViewById(R.id.tvkeycart);
        final TextView tvstore = view.findViewById(R.id.tvstore);
        TextView tvdateput = view.findViewById(R.id.tvdateput);
        TextView tvdatepay = view.findViewById(R.id.tvdatepay);
        TextView tvname = view.findViewById(R.id.tvname);
        TextView tvaddress = view.findViewById(R.id.tvaddress);
        TextView tvphone = view.findViewById(R.id.tvphone);
        TextView tvsumprice = view.findViewById(R.id.tvsumprice);
        TextView tvstate = view.findViewById(R.id.tvstate);
        final TextView tvfailed = view.findViewById(R.id.tvfailed);
        TextView tvsuccess = view.findViewById(R.id.tvsuccess);

        tvkeycart.setText("Mã đơn hàng: " + bill.getKey_cart());
        FirebaseDatabase.getInstance().getReference("Store").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Store store = dataSnapshot.child(bill.getKey_store()).getValue(Store.class);
                tvstore.setText(store.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        tvdateput.setText("Ngày đặt: " + bill.getDatetime());
        tvdatepay.setText("Ngày thanh toán: " + bill.getDatetime_delivered());
        tvsumprice.setText("Tổng tiền: " + bill.getTotal_price() + "đ");
        tvname.setText("Tên khách hàng: "+bill.getName_user());
        tvaddress.setText("Địa chỉ giao hàng: "+bill.getAddress());
        tvphone.setText("Số điện thoại: "+bill.getPhone());
        tvstate.setText(bill.getState());
        tvsuccess.setClickable(true);
        tvfailed.setClickable(true);
        getUserID();
        tvsuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference("TransportOrder")
                        .child(userID).child(bill.getKey_cart()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        item = dataSnapshot.getValue(Bill.class);
                        EventBus.getDefault().post(new SucessEvent(item));
                        firebaseDatabase.getReference("Order").child(bill.getUserID()).child("Transport")
                                .child(bill.getKey_cart()).removeValue();
                        firebaseDatabase.getReference("TransportOrder").child(userID)
                                .child(bill.getKey_cart()).removeValue();
                        EventBus.getDefault().post(new LoadEvent(true,fragment));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        tvfailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference("TransportOrder")
                        .child(userID).child(bill.getKey_cart()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        item = dataSnapshot.getValue(Bill.class);
                        EventBus.getDefault().post(new FailedEvent(item));
                        firebaseDatabase.getReference("Order").child(bill.getUserID()).child("Transport")
                                .child(bill.getKey_cart()).removeValue();
                        firebaseDatabase.getReference("TransportOrder").child(userID)
                                .child(bill.getKey_cart()).removeValue();
                        EventBus.getDefault().post(new LoadEvent(true,fragment));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        Cart cart = (Cart) getChild(i,i1);
        if(view == null){
            LayoutInflater layoutInflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.detail_bill_item_layout,null);
        }
        ImageView avatar = view.findViewById(R.id.avatar);
        TextView tvnameproduct = view.findViewById(R.id.tvnameproduct);
        TextView tvprice = view.findViewById(R.id.tvprice);
        TextView tvnumber = view.findViewById(R.id.tvnumber);

        Picasso.get().load(cart.getUrlImage()).into(avatar);
        tvnameproduct.setText(cart.getName());
        tvprice.setText("Giá: " + String.valueOf(cart.getPrice()) + "đ");
        tvnumber.setText("Số lượng: " + String.valueOf(cart.getNumber()));

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
    public void getUserID(){
        userID = context.getSharedPreferences("Account",Context.MODE_PRIVATE).getString("userID","");
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                List<Bill> suggestion = new ArrayList<>();

                if (charSequence == null || charSequence.length() == 0) {
                    suggestion.addAll(filter);
                } else {
                    String str = BillExpandListAdapter.convertString(charSequence.toString().trim().toLowerCase());
                    for (Bill bill : filter) {
                        if (BillExpandListAdapter.convertString(bill.getName_user().toLowerCase().trim()).contains(str)
                                || bill.getDatetime().contains(str)) {
                            suggestion.add(bill);
                        }
                    }
                }
                results.count = suggestion.size();
                results.values = suggestion;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                billList = (List<Bill>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
