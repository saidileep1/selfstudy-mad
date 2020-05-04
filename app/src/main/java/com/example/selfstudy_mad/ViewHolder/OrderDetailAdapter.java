package com.example.selfstudy_mad.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfstudy_mad.Model.Order;
import com.example.selfstudy_mad.R;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder{
    public TextView name,quantity,price;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        name=(TextView)itemView.findViewById(R.id.product_name);
        quantity=(TextView)itemView.findViewById(R.id.product_quantity);
        price=(TextView)itemView.findViewById(R.id.product_price);
    }
}
public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {

    List<Order> myOrder;

    public OrderDetailAdapter(List<Order> myOrder) {
        this.myOrder = myOrder;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);

        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Order order=myOrder.get(position);
        holder.name.setText(String.format("Name : %s",order.getProductName()));
        holder.quantity.setText(String.format("Quantity : %s",order.getQuantity()));
        holder.price.setText(String.format("Price : %s",order.getPrice()));
    }

    @Override
    public int getItemCount() {
        return myOrder.size();
    }

}


