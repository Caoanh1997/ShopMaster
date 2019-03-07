package com.example.caoan.shopmaster.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caoan.shopmaster.Model.Store;
import com.example.caoan.shopmaster.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StoreAdapter extends ArrayAdapter<Store> {

    private List<Store> storeList;

    public static String covertString(String str) {
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll(" ", "-").replaceAll("đ", "d");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            ArrayList<Store> suggestions = new ArrayList<>();

            if (charSequence == null || charSequence.length()==0){
                suggestions.addAll(storeList);
                //System.out.println("null");
            }else {
                String str = covertString(charSequence.toString()).toLowerCase().trim();

                for(Store store : storeList){
                    String s3 = covertString(store.getName()).toLowerCase().trim();
                    if(s3.contains(str)){
                        suggestions.add(store);
                    }
                    String s = covertString(store.getTinh()).toLowerCase().trim();
                    if(s.equals(str)){
                        suggestions.add(store);
                    }
                    if(covertString(store.getDuong().toLowerCase().trim()).contains(str)){
                        suggestions.add(store);
                    }
                    else {
                        String s1 = covertString(store.getHuyen()).toLowerCase().trim();
                        if(s1.equals(str)){
                            suggestions.add(store);
                        }
                        String s2 = covertString(store.getXa()).toLowerCase().trim();
                        if(s2.equals(str)){
                            suggestions.add(store);
                        }
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List)filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Store)resultValue).getName();
        }
    };

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    public List<Store> getStoreList() {
        return storeList;
    }

    public StoreAdapter(@NonNull Context context, @NonNull List<Store> objects) {
        super(context, 0, objects);
        storeList = new ArrayList<>(objects);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.store_item_layout,parent,false);

            viewHolder.imageView = convertView.findViewById(R.id.ivstore);
            viewHolder.tvname = convertView.findViewById(R.id.tvname);
            viewHolder.tvaddress = convertView.findViewById(R.id.tvaddress);
            viewHolder.tvphone = convertView.findViewById(R.id.tvphone);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Store store = getItem(position);
        if(store != null){
            Picasso.get().load(store.getUrlImage()).into(viewHolder.imageView);
            viewHolder.tvname.setText(store.getName());
            viewHolder.tvaddress.setText(store.getDuong()+", "+ store.getXa()+"-"+store.getHuyen()+"-"+store.getTinh());
            viewHolder.tvphone.setText("Phone: "+store.getPhone());
        }

        return convertView;
    }

    class ViewHolder{
        private ImageView imageView;
        private TextView tvname, tvaddress, tvphone;
    }

}
