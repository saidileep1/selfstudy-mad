package com.example.selfstudy_mad;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.selfstudy_mad.Database.Database;
import com.example.selfstudy_mad.Model.Food;
import com.example.selfstudy_mad.Model.Order;
import com.example.selfstudy_mad.common.common;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetails extends AppCompatActivity {


    TextView food_name,food_price,food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CounterFab btnCart;
    ElegantNumberButton numberButton;

    String foodId="";

    FirebaseDatabase database;
    DatabaseReference foods;
    Food currentfood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);


        //Firebase
        database =FirebaseDatabase.getInstance();
        foods=database.getReference("Food");


        //Intialize View

        numberButton=(ElegantNumberButton)findViewById(R.id.number_button);
        btnCart=(CounterFab) findViewById(R.id.btncart);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Database(getBaseContext()).addToCart(new Order(
                        common.currentUser.getPhone(),
                        foodId,
                        currentfood.getName(),
                        numberButton.getNumber(),
                        currentfood.getPrice(),
                        currentfood.getImage()
                ));
                Toast.makeText(FoodDetails.this,"ADDED TO CART",Toast.LENGTH_SHORT).show();


            }
        });
        btnCart.setCount(new Database(this).geCountCart(common.currentUser.getPhone()));

        food_description=(TextView)findViewById(R.id.food_description);
        food_name=(TextView)findViewById(R.id.food_name);
        food_image=(ImageView) findViewById(R.id.food_image);
        food_price=(TextView)findViewById(R.id.food_price);

        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);

        //Get food id from Intent
        if (getIntent()!=null)
            foodId=getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty())
        {

            getDetailFood(foodId);
        }

    }

    private void getDetailFood(final String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 currentfood=dataSnapshot.getValue(Food.class);

                //Set Image
                Picasso.with(getBaseContext()).load(currentfood.getImage())
                        .into(food_image);
                collapsingToolbarLayout.setTitle(currentfood.getName());

                food_price.setText(currentfood.getPrice());
                food_name.setText(currentfood.getName());
                food_description.setText(currentfood.getDescription());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
