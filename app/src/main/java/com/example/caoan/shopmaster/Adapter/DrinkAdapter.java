package com.example.caoan.shopmaster.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caoan.shopmaster.Model.Drink;
import com.example.caoan.shopmaster.R;
import com.squareup.picasso.Picasso;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class DrinkAdapter extends ArrayAdapter<Drink> {


    private List<Drink> drinkList;
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Drink> suggestion = new ArrayList<>();

            if (constraint.length() == 0 || constraint == null) {
                suggestion.addAll(drinkList);
            } else {
                String str = covertString(constraint.toString().trim().toLowerCase());
                for (Drink drink : drinkList) {
                    if (covertString(drink.getName().toLowerCase().trim()).contains(str)) {
                        suggestion.add(drink);
                    }
                }
            }
            results.values = suggestion;
            results.count = suggestion.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((Collection<? extends Drink>) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Drink) resultValue).getName();
        }
    };
    public DrinkAdapter(@NonNull Context context, @NonNull List<Drink> objects) {
        super(context, 0, objects);
        drinkList = new ArrayList<>(objects);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    public String covertString(String str) {
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll(" ", "").replaceAll("đ", "d");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.food_item_layout,parent,false);
            viewHolder.imageView = convertView.findViewById(R.id.imagefood);
            viewHolder.tvname = convertView.findViewById(R.id.tvname);
            viewHolder.tvprice = convertView.findViewById(R.id.tvprice);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Drink drink = getItem(position);
        if(drink != null){
            Picasso.get().load(drink.getUrlimage()).into(viewHolder.imageView);
            viewHolder.tvname.setText(drink.getName());
            viewHolder.tvprice.setText(String.valueOf(drink.getPrice())+" d");
        }

        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
        TextView tvname,tvprice;
    }
}
