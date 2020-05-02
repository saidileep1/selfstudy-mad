package com.example.selfstudy_mad;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfstudy_mad.ViewHolder.OrderDetailAdapter;
import com.example.selfstudy_mad.common.common;

public class OrderDetails extends AppCompatActivity {

    TextView order_id,order_phone,order_address,order_total,order_comment;
    String order_id_value="";
    RecyclerView listFoods;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        order_id=(TextView)findViewById(R.id.order_id);
        order_phone=(TextView)findViewById(R.id.order_phone);
        order_address=(TextView)findViewById(R.id.order_address);
        order_total=(TextView)findViewById(R.id.order_total);
        order_comment=(TextView)findViewById(R.id.order_comment);

        listFoods=(RecyclerView)findViewById(R.id.listfoods);
        listFoods.setHasFixedSize(true);
        layoutManager =new LinearLayoutManager(this);
        listFoods.setLayoutManager(layoutManager);

        if (getIntent()!=null)
            order_id_value=getIntent().getStringExtra("OrderId");

        //Set value
        order_id.setText(order_id_value);
        order_phone.setText(common.currentRequest.getPhone());
        order_total.setText(common.currentRequest.getTotal());
        order_address.setText(common.currentRequest.getAddress());
        order_comment.setText(common.currentRequest.getComment());

        OrderDetailAdapter adapter=new OrderDetailAdapter(common.currentRequest.getFoods());

        adapter.notifyDataSetChanged();
        listFoods.setAdapter(adapter);

    }
}
