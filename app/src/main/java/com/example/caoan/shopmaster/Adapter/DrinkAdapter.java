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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caoan.shopmaster.Model.Drink;
import com.example.caoan.shopmaster.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class DrinkAdapter extends ArrayAdapter<Drink> {
    private List<Drink> drinkList;
    public DrinkAdapter(@NonNull Context context, @NonNull List<Drink> objects) {
        super(context, 0, objects);
        drinkList = objects;
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
