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

import com.example.caoan.shopmaster.Model.Bill;
import com.example.caoan.shopmaster.Model.Cart;
import com.example.caoan.shopmaster.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BillDeliveredAdapter extends BaseExpandableListAdapter implements Filterable{

    private List<Bill> billList;
    private HashMap<Bill, List<Cart>> billListHashMap;
    private Context context;
    private Fragment fragment;

    public BillDeliveredAdapter(List<Bill> billList, HashMap<Bill, List<Cart>> billListHashMap, Context context, Fragment fragment) {
        this.billList = billList;
        this.billListHashMap = billListHashMap;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public int getGroupCount() {
        return billList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return billListHashMap.get(billList.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return billList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return billListHashMap.get(billList.get(i)).get(i1);
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
        Bill bill = (Bill) getGroup(i);
        if(view == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.bill_item_delivered_layout,null);
        }
        TextView tvkeycart = view.findViewById(R.id.tvkeycart);
        TextView tvdateput = view.findViewById(R.id.tvdateput);
        TextView tvdatepay = view.findViewById(R.id.tvdatepay);
        TextView tvname = view.findViewById(R.id.tvname);
        TextView tvaddress = view.findViewById(R.id.tvaddress);
        TextView tvphone = view.findViewById(R.id.tvphone);
        TextView tvsumprice = view.findViewById(R.id.tvsumprice);
        TextView tvstate = view.findViewById(R.id.tvstate);

        tvkeycart.setText("Mã đơn hàng: " + bill.getKey_cart());
        tvdateput.setText("Ngày đặt: " + bill.getDatetime());
        tvdatepay.setText("Ngày thanh toán: " + bill.getDatetime_delivered());
        tvsumprice.setText("Tổng tiền: " + bill.getTotal_price() + "đ");
        tvname.setText("Tên khách hàng: "+bill.getName_user());
        tvaddress.setText("Địa chỉ giao hàng: "+bill.getAddress());
        tvphone.setText("Số điện thoại: "+bill.getPhone());
        tvstate.setText(bill.getState());

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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Bill> suggestion = new ArrayList<>();

                if(charSequence == null || charSequence.length()==0){
                    suggestion.addAll(billList);
                }else {
                    String str = charSequence.toString().trim().toLowerCase();
                    System.out.println(str);
                    for(Bill bill : billList){
                        if(bill.getName_user().trim().toLowerCase().contains(str)){
                            suggestion.add(bill);
                        }
                    }
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
