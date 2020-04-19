package com.example.selfstudy_mad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfstudy_mad.Interface.ItemClickListener;
import com.example.selfstudy_mad.Model.Food;
import com.example.selfstudy_mad.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodlist;

    String categoryId="";

    FirebaseRecyclerAdapter<Food, FoodViewHolder>adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //firebase
        database=FirebaseDatabase.getInstance();
        foodlist=database.getReference("Food");

        recyclerView=(RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //getting the intent here
        if (getIntent()!=null)
            categoryId=getIntent().getStringExtra("categoryId");
        if (categoryId!=null &&!categoryId.isEmpty())
        {
            loadListFood(categoryId);
        }
    }

    private void loadListFood(final String categoryId) {
        adapter =new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_list,
                FoodViewHolder.class,
                foodlist.orderByChild("MenuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {
                foodViewHolder.txtfoodName.setText(food.getName());
                Picasso.with(getBaseContext()).load(food.getImage())
                        .into(foodViewHolder.foodimageView);

                final Food local=food;
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Start Food Details activity
                        Intent fooddetail =new Intent(FoodList.this,FoodDetails.class);
                        fooddetail.putExtra("FoodId",adapter.getRef(position).getKey());//send foodid
                        startActivity(fooddetail);
                        Toast.makeText(FoodList.this,""+local.getMenuId()+categoryId,Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };
        //set Adapter
        Log.d("TAG",""+adapter.getItemCount());
        recyclerView.setAdapter(adapter);


    }
}
