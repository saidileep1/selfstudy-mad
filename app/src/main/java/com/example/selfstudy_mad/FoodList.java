package com.example.selfstudy_mad;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selfstudy_mad.Database.Database;
import com.example.selfstudy_mad.Interface.ItemClickListener;
import com.example.selfstudy_mad.Model.Food;
import com.example.selfstudy_mad.Model.Order;
import com.example.selfstudy_mad.ViewHolder.FoodViewHolder;
import com.example.selfstudy_mad.common.common;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodlist;



    String categoryId="";

    FirebaseRecyclerAdapter<Food, FoodViewHolder>adapter;


    //Search Functionality
    FirebaseRecyclerAdapter<Food,FoodViewHolder>searchadapter;
    List<String> suggestList=new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    //favourites
    Database localdb;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //firebase
        database=FirebaseDatabase.getInstance();
        foodlist=database.getReference("Food");
        //LocalDb
        localdb =new Database(this);


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






        //search
        materialSearchBar=(MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");

        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            //When user types,change suggestlist

                List<String> suggest=new ArrayList<String>();
                for (String search:suggestList)//loop in suggestlist
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When search bar is close restor adapter
                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When search finish.show result adapter
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {


            }
        });
    }

    private void startSearch(CharSequence text) {
        //Creaate Query by name
        Query searchbyname=foodlist.orderByChild("name").equalTo(text.toString());

        FirebaseRecyclerOptions<Food> options=new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchbyname,Food.class)
                .build();


        searchadapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder foodViewHolder, int i, @NonNull Food food) {

                foodViewHolder.txtfoodName.setText(food.getName());
                Picasso.with(getBaseContext()).load(food.getImage())
                        .into(foodViewHolder.foodimageView);

                final Food local=food;
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Start Food Details activity
                        Intent fooddetail =new Intent(FoodList.this,FoodDetails.class);
                        fooddetail.putExtra("FoodId",searchadapter.getRef(position).getKey());//send foodid
                        startActivity(fooddetail);
                        Toast.makeText(FoodList.this,"",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate( R.layout.food_list,parent,false);
                return new FoodViewHolder(itemView);
            }
        };

        searchadapter.startListening();
        recyclerView.setAdapter(searchadapter);
    }

    private void loadSuggest() {
        foodlist.orderByChild("menuid").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Food item=postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());//Add name of food to suggest list
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {


                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
       if (adapter!=null)
           adapter.startListening();
    }

    private void loadListFood(final String categoryId) {
        Query searchbyid=foodlist.orderByChild("menuid").equalTo(categoryId);


        FirebaseRecyclerOptions<Food> options=new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchbyid,Food.class)
                .build();

        adapter =new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder foodViewHolder, final int i, @NonNull final Food food) {

                foodViewHolder.txtfoodName.setText(food.getName());
                foodViewHolder.txtfoodPrice.setText(String.format("Rs. %s", food.getPrice()));
                Picasso.with(getBaseContext()).load(food.getImage())
                        .into(foodViewHolder.foodimageView);

                //Quick Cart

                final boolean isExists = new Database(getBaseContext()).checkFoodExists(adapter.getRef(i).getKey(), common.currentUser.getPhone());
                if (!isExists)
                    foodViewHolder.quickaddcart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            new Database(getBaseContext()).addToCart(new Order(
                                    common.currentUser.getPhone(),
                                    adapter.getRef(i).getKey(),
                                    food.getName(),
                                    "1",
                                    food.getPrice(),
                                    food.getImage()
                            ));

                         /*   else {
                                new Database(getBaseContext()).increaseCart(common.currentUser.getPhone(), adapter.getRef(i).getKey());
                            }*/
                            Toast.makeText(FoodList.this, "ADDED TO CART", Toast.LENGTH_SHORT).show();

                        }
                    });


                final Food local = food;
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //Start Food Details activity
                        Intent fooddetail = new Intent(FoodList.this, FoodDetails.class);
                        fooddetail.putExtra("FoodId", adapter.getRef(position).getKey());//send foodid
                        startActivity(fooddetail);
                        Toast.makeText(FoodList.this, "" + local.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }




            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View itemView= LayoutInflater.from(parent.getContext())
                       .inflate( R.layout.food_list,parent,false);
               return new FoodViewHolder(itemView);
            }
        };
        //set Adapter
        Log.d("TAG",""+adapter.getItemCount());
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        if(searchadapter!=null)
            searchadapter.stopListening();
    }
}
