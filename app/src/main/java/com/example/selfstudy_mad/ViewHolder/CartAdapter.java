package com.example.selfstudy_mad.ViewHolder;


import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.selfstudy_mad.Cart;
import com.example.selfstudy_mad.Database.Database;
import com.example.selfstudy_mad.Interface.ItemClickListener;
import com.example.selfstudy_mad.Model.Order;
import com.example.selfstudy_mad.R;
import com.example.selfstudy_mad.common.common;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        , View.OnContextClickListener, View.OnCreateContextMenuListener {

    public TextView txt_cart_name, txt_price;
    public ElegantNumberButton btn_quantity;
    public ImageView cart_image,del;

    private ItemClickListener itemClickListener;


    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CardViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = (TextView) itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView) itemView.findViewById(R.id.cart_item_Price);
        btn_quantity = (ElegantNumberButton) itemView.findViewById(R.id.btn_quantity);
        cart_image = (ImageView) itemView.findViewById(R.id.cart_image);
        del=itemView.findViewById(R.id.cart_item_del);
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onContextClick(View v) {
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select Action");
        menu.add(0, 0, getAdapterPosition(), common.DELETE);
    }
}

public class CartAdapter extends RecyclerView.Adapter<CardViewHolder> {


    private List<Order> listData = new ArrayList<>();
    private Cart cart;


    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);

        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, final int position) {
        //TextDrawable drawable =TextDrawable.builder()
        //      .buildRound(""+listData.get(position).getQuantity(), Color.BLACK);
        //            holder.img_cart_count.setImageDrawable(drawable);
        Picasso.with(cart.getBaseContext())
                .load(listData.get(position).getImage())
                .resize(70, 70)
                .centerCrop()
                .into(holder.cart_image);
        holder.btn_quantity.setNumber(listData.get(position).getQuantity());
        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);
                //Update txttotal
                //Calculate total Price
                int total = 0;
                List<Order> orders = new Database(cart).getCarts(common.currentUser.getPhone());
                for (Order item : orders)
                    total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(item.getQuantity()));
                Locale locale = new Locale("en", "IN");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                cart.txtTotalPrice.setText(fmt.format(total));
                if(newValue==0)
                {
                    listData.remove(position);
                    new Database(cart.getBaseContext()).cleanCart(common.currentUser.getPhone());
                    //update new data from sqlite
                    for (Order item : listData)
                        new Database(cart).addToCart(item);
                    cart.loadListFood();
                    cart.changevisibility();
                }
            }
        });
        Locale locale = new Locale("en", "IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));
        holder.txt_price.setText(fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listData.remove(position);
                new Database(cart.getBaseContext()).cleanCart(common.currentUser.getPhone());
                //update new data from sqlite
                for (Order item : listData)
                    new Database(cart).addToCart(item);
                cart.loadListFood();
                cart.changevisibility();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
