package com.example.selfstudy_mad.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfstudy_mad.R;


public class FoodHolder extends RecyclerView.ViewHolder{

    public TextView name,price;
    public ImageView image;
    public Button add;


    public FoodHolder(@NonNull View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.item_title);
        image = itemView.findViewById(R.id.item_img);
        price = itemView.findViewById(R.id.item_price);
        add=itemView.findViewById(R.id.add_button);
    }
}
