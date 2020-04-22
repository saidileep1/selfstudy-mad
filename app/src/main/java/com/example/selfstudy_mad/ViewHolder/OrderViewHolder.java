package com.example.selfstudy_mad.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfstudy_mad.Interface.ItemClickListener;
import com.example.selfstudy_mad.R;

public class OrderViewHolder extends RecyclerView.ViewHolder  {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);


        txtOrderAddress = (TextView) itemView.findViewById(R.id.order_address);
        txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);


    }
public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
}


}

