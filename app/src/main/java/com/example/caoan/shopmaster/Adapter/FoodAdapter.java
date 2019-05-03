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

import com.example.caoan.shopmaster.Model.Food;
import com.example.caoan.shopmaster.R;
import com.squareup.picasso.Picasso;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FoodAdapter extends ArrayAdapter<Food> {
    private List<Food> foodList;
    private Context context;

    public FoodAdapter(@NonNull Context context, @NonNull List<Food> objects) {
        super(context, 0, objects);
        foodList = new ArrayList<>(objects);
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.food_item_layout,parent,false);

            viewHolder.imageView = convertView.findViewById(R.id.imagefood);
            viewHolder.tvname = convertView.findViewById(R.id.tvname);
            viewHolder.tvprice = convertView.findViewById(R.id.tvprice);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Food food = getItem(position);
        if(food != null){
            viewHolder.tvname.setText(food.getName());
            viewHolder.tvprice.setText(String.valueOf(food.getPrice())+" d");
            Picasso.get().load(food.getUrlimage()).into(viewHolder.imageView);
        }

        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
        TextView tvname,tvprice;
    }
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults results = new FilterResults();
            List<Food> suggestions = new ArrayList<>();
            if(charSequence == null || charSequence.length()==0){
                suggestions.addAll(foodList);
            }else {
                String str = covertString(charSequence.toString().toLowerCase().trim());
                System.out.println(str);
                for(Food food : foodList){
                    if (covertString(food.getName().toLowerCase().trim()).contains(str)) {
                        suggestions.add(food);
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
            addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
//            return super.convertResultToString(resultValue);
            return ((Food)resultValue).getName();
        }
    };

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
    public Filter getFilter() {
        return filter;
    }
}
