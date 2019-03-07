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

import com.example.caoan.shopmaster.Model.Food;
import com.example.caoan.shopmaster.R;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
                System.out.println("null");
                suggestions.addAll(foodList);
                System.out.println(foodList.size());
            }else {
                String str = charSequence.toString().toLowerCase().trim();
                System.out.println(str);
                for(Food food : foodList){
                    if(food.getName().toLowerCase().contains(str)){
                        suggestions.add(food);
                    }
                }
                System.out.println(suggestions.size());
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

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }
}
