package com.example.selfstudy_mad.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfstudy_mad.Interface.ItemClickListener;
import com.example.selfstudy_mad.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtfoodName,txtfoodPrice;
    public ImageView foodimageView,fav_image,quickaddcart;
    private ItemClickListener itemClickListener;


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;

    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);
        txtfoodName=(TextView)itemView.findViewById(R.id.food_name);
        foodimageView=(ImageView)itemView.findViewById(R.id.food_image);
        txtfoodPrice=(TextView)itemView.findViewById(R.id.food_price);
      //  fav_image=(ImageView)itemView.findViewById(R.id.fav);
        quickaddcart=(ImageView)itemView.findViewById(R.id.addcart);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}
