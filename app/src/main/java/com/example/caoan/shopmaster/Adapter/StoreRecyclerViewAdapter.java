package com.example.caoan.shopmaster.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caoan.shopmaster.EditShopActivity;
import com.example.caoan.shopmaster.ItemClickListener;
import com.example.caoan.shopmaster.Model.Store;
import com.example.caoan.shopmaster.ProductActivity;
import com.example.caoan.shopmaster.R;
import com.squareup.picasso.Picasso;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class StoreRecyclerViewAdapter extends RecyclerView.Adapter<StoreRecyclerViewAdapter.ViewHolder> implements Filterable {
    private List<Store> storeList;
    private Context context;
    private List<Store> filter;

    public StoreRecyclerViewAdapter(List<Store> storeList, Context context) {
        this.storeList = storeList;
        this.context = context;
        this.filter = storeList;
    }

    @NonNull
    @Override
    public StoreRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.store_item_layout,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StoreRecyclerViewAdapter.ViewHolder holder, int position) {
        final Store store = storeList.get(position);
        if(store != null){
            Picasso.get().load(store.getUrlImage()).into(holder.ivstore);
            holder.tvname.setText(store.getName());
            holder.tvaddress.setText(store.getDuong()+", "+ store.getXa()+"-"+store.getHuyen()+"-"+store.getTinh());
            holder.tvphone.setText("Phone: "+store.getPhone());
        }
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(!isLongClick){
                    String key_store = store.getKey();
                    SharedPreferences sharedPreferences = context.getSharedPreferences("key_store", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("key",key_store);
                    editor.commit();
                    context.startActivity(new Intent(context,ProductActivity.class));
                }else {
                    Intent intent = new Intent(context, EditShopActivity.class);
                    intent.putExtra("store", store);
                    context.startActivity(intent);
                }
            }
        });
    }

    public void Swipe(int position, int direction){
        storeList.remove(position);
        notifyItemRemoved(position);
    }

    public void Move(int oldPosition, int newPosition){
        if (oldPosition < newPosition){
            for (int i=oldPosition;i<newPosition;i++){
                Collections.swap(storeList,i,i+1);
            }
        }else {
            for (int i=oldPosition;i>newPosition;i--){
                Collections.swap(storeList,i,i-1);
            }
        }
        notifyItemMoved(oldPosition,newPosition);
    }

    @Override
    public int getItemCount() {
        return storeList.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private ImageView ivstore;
        private TextView tvname, tvaddress, tvphone;
        private ItemClickListener itemClickListener;
        public ViewHolder(View itemView) {
            super(itemView);

            ivstore = itemView.findViewById(R.id.ivstore);
            tvname = itemView.findViewById(R.id.tvname);
            tvaddress = itemView.findViewById(R.id.tvaddress);
            tvphone = itemView.findViewById(R.id.tvphone);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),true);
            return true;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                List<Store> suggestion = new ArrayList<>();
                if (charSequence == null || charSequence.length() == 0) {
                    suggestion.addAll(filter);
                } else {
                    String str = convertString(charSequence.toString().toLowerCase().trim());
                    for (Store store : filter) {
                        if (convertString(store.getName().toLowerCase().trim()).contains(str)
                                || convertString(store.getDuong().toLowerCase().trim()).contains(str)
                                || convertString(store.getXa().toLowerCase().trim()).contains(str)
                                || convertString(store.getHuyen().trim().toLowerCase()).contains(str)
                                || convertString(store.getTinh().toLowerCase().trim()).contains(str)) {
                            suggestion.add(store);
                        }
                    }
                }
                results.count = suggestion.size();
                results.values = suggestion;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                storeList = (List<Store>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
