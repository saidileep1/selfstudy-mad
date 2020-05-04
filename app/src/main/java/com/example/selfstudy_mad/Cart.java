package com.example.selfstudy_mad;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfstudy_mad.Database.Database;
import com.example.selfstudy_mad.Model.MyResponse;
import com.example.selfstudy_mad.Model.Notification;
import com.example.selfstudy_mad.Model.Order;
import com.example.selfstudy_mad.Model.Request;
import com.example.selfstudy_mad.Model.Sender;
import com.example.selfstudy_mad.Model.Token;
import com.example.selfstudy_mad.Remote.APIService;
import com.example.selfstudy_mad.ViewHolder.CartAdapter;
import com.example.selfstudy_mad.common.common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotalPrice;
    Button btnPlace;
    List<Order> cart=new ArrayList<>();
    CartAdapter adapter;
    APIService mService;
    Dialog msg;
    Button closedialog;
    TextView order_msg;

   public String haddress,gaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Init Service
        mService=common.getFCMService();

        //Firebase
        database =FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        //Init
        recyclerView=(RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        msg=new Dialog(this);
        txtTotalPrice=(TextView)findViewById(R.id.total);
        btnPlace=(Button)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cart.size()>0)
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this,"Your Cart is Empty!!",Toast.LENGTH_SHORT).show();
                }
        });

        loadListFood();

    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your Address");

        LayoutInflater inflater=this.getLayoutInflater();
        View order_address_comment=inflater.inflate(R.layout.order_address_comment,null);

        final MaterialEditText address=(MaterialEditText)order_address_comment.findViewById(R.id.address);

        final MaterialEditText comment=(MaterialEditText)order_address_comment.findViewById(R.id.comment);


        //radio
        final RadioButton homeaddress=(RadioButton) order_address_comment.findViewById(R.id.rdihome);
        final RadioButton customaddress=(RadioButton)order_address_comment.findViewById(R.id.rdicustom);

        //custom
        customaddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    address.setVisibility(View.VISIBLE);

                }
            }
        });

        homeaddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    if (TextUtils.isEmpty(common.currentUser.getHomeaddress())||
                    common.currentUser.getHomeaddress()==null)
                        Toast.makeText(Cart.this,"Please add your address!",Toast.LENGTH_SHORT).show();
                    else
                        haddress=common.currentUser.getHomeaddress();
                    address.setVisibility(View.INVISIBLE);
                }
            }
        });




        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
if (homeaddress.isChecked()){

    if (haddress==null){
        Toast.makeText(Cart.this,"Please Update Your Address ",Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
    else
    {
        Request request = new Request(
                common.currentUser.getPhone(),
                common.currentUser.getName(),
                haddress,
                txtTotalPrice.getText().toString(),
                "0",
                comment.getText().toString(),
                cart
        );


        //Submit to Firebase
        //Using Sytem.currentmilli to key
        String order_number=String.valueOf(System.currentTimeMillis());
        requests.child(order_number).setValue(request);
        //Toast.makeText(Cart.this, "Thank You!Your Order Placed!", Toast.LENGTH_SHORT).show();
        //finish();
        new Database(getBaseContext()).cleanCart(common.currentUser.getPhone());
        sendNotificationOrder(order_number);

    }
}
else if (customaddress.isChecked())
{
    if (address.getText().toString().length()>0){
        Request request = new Request(
                common.currentUser.getPhone(),
                common.currentUser.getName(),
                address.getText().toString(),
                txtTotalPrice.getText().toString(),
                "0",
                comment.getText().toString(),
                cart
        );


        //Submit to Firebase
        //Using Sytem.currentmilli to key
        String order_number=String.valueOf(System.currentTimeMillis());
        requests.child(order_number).setValue(request);
        //Toast.makeText(Cart.this, "Thank You!Your Order Placed!", Toast.LENGTH_SHORT).show();
        //finish();
        new Database(getBaseContext()).cleanCart(common.currentUser.getPhone());
        sendNotificationOrder(order_number);

    }
    else if(address.getText().toString().length()==0)
        Toast.makeText(Cart.this,"Please enter the address!",Toast.LENGTH_SHORT).show();



}
else{
    Toast.makeText(Cart.this,"Please select the Address!",Toast.LENGTH_SHORT).show();
}



/*else {                Request request = new Request(
                            common.currentUser.getPhone(),
                            common.currentUser.getName(),
                            haddress,
                            txtTotalPrice.getText().toString(),
                            "0",
                            comment.getText().toString(),
                            cart
                    );


                //Submit to Firebase
                //Using Sytem.currentmilli to key
                String order_number=String.valueOf(System.currentTimeMillis());
                requests.child(order_number).setValue(request);
                //Toast.makeText(Cart.this, "Thank You!Your Order Placed!", Toast.LENGTH_SHORT).show();
                //finish();
                new Database(getBaseContext()).cleanCart(common.currentUser.getPhone());
               sendNotificationOrder(order_number);}*/



            }

        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();


    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens =FirebaseDatabase.getInstance().getReference("Token");
        Query data=tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Token serverToken=postSnapShot.getValue(Token.class);
                    //
                    Notification notification=new Notification("APP","You have new Order"+order_number);
                    Sender content=new Sender(serverToken.getToken(),notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank You! Your Order Placed!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "Order Successful, waiting for Server", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }/*
                                        msg.setContentView(R.layout.cart_layout);
                                        closedialog=findViewById(R.id.close_dialog);
                                        closedialog.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                msg.dismiss();
                                                finish();
                                            }
                                        });*/

                                }
                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                    Log.e("ERROR",t.getMessage());
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadListFood() {
        cart=new Database(this).getCarts(common.currentUser.getPhone());
        adapter=new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

//Calculate total Price
        int total=0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));

        Locale locale=new Locale("en","IN");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    //Location

    @Override
    public boolean onContextItemSelected( MenuItem item) {
        if (item.getTitle().equals(common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position)
    {
        //we will remove item by position in order
        cart.remove(position);
        //We will delete all old data fromsqlite
        new Database(this).cleanCart(common.currentUser.getPhone());
        //update new data from sqlite
        for (Order item:cart)
            new Database(this).addToCart(item);
        loadListFood();
    }
}
