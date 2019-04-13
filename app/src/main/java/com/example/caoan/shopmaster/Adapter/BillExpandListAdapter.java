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

import com.example.caoan.shopmaster.EventBus.BillEvent;
import com.example.caoan.shopmaster.EventBus.DeleteEvent;
import com.example.caoan.shopmaster.EventBus.LoadEvent;
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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class BillExpandListAdapter extends BaseExpandableListAdapter implements Filterable {

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
    private List<Store> list_store;
    //private String token;

    public BillExpandListAdapter(Context context, List<Bill> billList, HashMap<Bill, List<Cart>> listDetailBill, Fragment fragment) {
        this.context = context;
        this.billList = billList;
        ListDetailBill = listDetailBill;
        this.fragment = fragment;
        this.filter = billList;
        list_store = new ArrayList<>();
    }

    public void getUserInfor() {
        userID =  context.getSharedPreferences("Account", Context.MODE_PRIVATE).getString("userID", "");
    }

    @Override
    public int getGroupCount() {
        return this.billList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.ListDetailBill.get(this.billList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.billList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.ListDetailBill.get(this.billList.get(i)).get(i1);
    }

    public List<Cart> getCartList(int i) {
        return this.ListDetailBill.get(this.billList.get(i));
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

    public static String convertString(String str) {
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll(" ", "").replaceAll("đ", "d");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        Cart cart = (Cart) getChild(i, i1);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.detail_bill_item_layout, null);
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

    public void setNewItem(List<Bill> billList, HashMap<Bill, List<Cart>> billListHashMap) {
        this.billList = billList;
        this.ListDetailBill = billListHashMap;
        notifyDataSetChanged();
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        //getToken_master();
        final Bill bill = (Bill) getGroup(i);
        cartList = ListDetailBill.get(bill);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.bill_item_layout, null);
        }
        Load_Name_Store(bill.getKey_store());

        TextView tvkeycart = view.findViewById(R.id.tvkeycart);
        final TextView tvstore = view.findViewById(R.id.tvstore);
        TextView tvdateput = view.findViewById(R.id.tvdateput);
        TextView tvdatepay = view.findViewById(R.id.tvdatepay);
        TextView tvname = view.findViewById(R.id.tvname);
        TextView tvaddress = view.findViewById(R.id.tvaddress);
        TextView tvphone = view.findViewById(R.id.tvphone);
        TextView tvsumprice = view.findViewById(R.id.tvsumprice);
        TextView tvstate = view.findViewById(R.id.tvstate);
        final TextView tvdeleteorder = view.findViewById(R.id.tvdeleteorder);
        TextView tvconfirm = view.findViewById(R.id.tvconfirm);

        tvkeycart.setText("Mã đơn hàng: " + bill.getKey_cart());
        FirebaseDatabase.getInstance().getReference("Shopmaster").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Store store = dataSnapshot.child(bill.getKey_store()).getValue(Store.class);
                if (store != null) {
                    tvstore.setText("Cửa hàng: " + store.getName());
                } else {
                    tvstore.setText("Cửa hàng không tồn tại hoặc đã xóa");
                }

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
        tvconfirm.setClickable(true);
        tvdeleteorder.setClickable(true);

        getUserInfor();
        tvconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference("NewOrder")
                        .child(userID).child(bill.getKey_cart()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        item = dataSnapshot.getValue(Bill.class);
                        EventBus.getDefault().post(new BillEvent(item));
                        firebaseDatabase.getReference("Order").child(bill.getUserID()).child("New")
                                .child(bill.getKey_cart()).removeValue();
                        firebaseDatabase.getReference("NewOrder").child(userID)
                                .child(bill.getKey_cart()).removeValue();
                        EventBus.getDefault().post(new LoadEvent(true,fragment));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
        tvdeleteorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference("NewOrder")
                        .child(userID).child(bill.getKey_cart()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        item = dataSnapshot.getValue(Bill.class);
                        EventBus.getDefault().post(new DeleteEvent(item));
                        firebaseDatabase.getReference("Order").child(bill.getUserID()).child("New")
                                .child(bill.getKey_cart()).removeValue();
                        firebaseDatabase.getReference("NewOrder").child(userID)
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

    public void Load_Name_Store(final String store_key) {
        list_store = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Store").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Store store = dataSnapshot.child(store_key).getValue(Store.class);
                list_store.add(store);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*public void getToken_master() {
        token = context.getSharedPreferences("Account",Context.MODE_PRIVATE)
                .getString("token","");
    }*/

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Bill> suggestion = new ArrayList<>();
                if (charSequence == null || charSequence.length() == 0) {
                    suggestion.addAll(filter);
                } else {
                    String str = convertString(charSequence.toString().toLowerCase().trim());
                    for (Bill bill : filter) {
                        if (convertString(bill.getName_user().toLowerCase().trim()).contains(str)
                                || bill.getDatetime().trim().contains(str)) {
                            suggestion.add(bill);
                        }
                    }
                    
                    /*for (Store store : storeList){
                        if(convertString(store.getName().toLowerCase().trim()).contains(str)){
                            for (Bill bill : filter){
                                if(convertString(bill.getKey_store().toLowerCase().trim()).equals(store.getKey())){
                                    suggestion.add(bill);
                                }
                            }
                        }
                    }*/
                }
                FilterResults results = new FilterResults();
                results.values = suggestion;
                results.count = suggestion.size();

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
