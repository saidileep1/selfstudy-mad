package com.example.selfstudy_mad.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfstudy_mad.Interface.ItemClickListener;
import com.example.selfstudy_mad.R;

public class OrderViewHolder extends RecyclerView.ViewHolder  {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress,txtOrderDate;
    public Button Orderdetails;
    public ImageView Imgdeleteorder;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);


        txtOrderAddress = (TextView) itemView.findViewById(R.id.order_address);
        txtOrderId = (TextView) itemView.findViewById(R.id.order_id);
        txtOrderPhone = (TextView) itemView.findViewById(R.id.order_phone);
        txtOrderStatus = (TextView) itemView.findViewById(R.id.order_status);
        Orderdetails=(Button)itemView.findViewById(R.id.btnDetail);
        txtOrderDate=(TextView)itemView.findViewById(R.id.order_date);
        Imgdeleteorder=(ImageView) itemView.findViewById(R.id.btn_delete);

    }
public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
}


}

