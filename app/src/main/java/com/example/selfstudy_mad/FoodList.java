package com.example.selfstudy_mad;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.selfstudy_mad.Database.Database;
import com.example.selfstudy_mad.Interface.ItemClickListener;
import com.example.selfstudy_mad.Model.Food;
import com.example.selfstudy_mad.Model.Food1;
import com.example.selfstudy_mad.Model.Order;
import com.example.selfstudy_mad.ViewHolder.FoodHolder;
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


//////////////////////////////////////////////////////////////////////////////////////////////

import androidx.core.content.ContextCompat;
import android.widget.Button;

public class FoodList extends AppCompatActivity implements View.OnClickListener{

    Integer category;
    RecyclerView recyclerView;
    Button b;
    Integer pressed;
    Button previous;
    FirebaseDatabase database;
    DatabaseReference ref;
    Switch veg;

    //Search Adapter
    FirebaseRecyclerAdapter<Food1, FoodHolder> searchadapter;
    List<String> suggestList=new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //FoodAdapter adapter;
    FirebaseRecyclerAdapter<Food1, FoodHolder>adapter;
    String categoryid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        recyclerView = findViewById(R.id.items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Food1");
        /*FirebaseRecyclerOptions<Food> options =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(ref.child("Food"), Food.class)
                        .build();
*/
        /*adapter=new FoodAdapter(options);
        recyclerView.setAdapter(adapter);
*/
        //veg=findViewById(R.id.veg);
        //Get Category clicked
        Intent intent = getIntent();
        category = intent.getIntExtra(Home.EXTRA_category, 0);
        //set selected button
        switch (category) {
            case R.id.img_pizza:
                b = findViewById(R.id.Pizza);
                categoryid = "02";
                break;
            case R.id.img_pasta:
                b = findViewById(R.id.Pasta);
                categoryid = "03";
                break;
            case R.id.img_burger:
                b = findViewById(R.id.Burger);
                categoryid = "01";
                break;
            case R.id.img_sides:
                b = findViewById(R.id.Sides);
                categoryid = "06";
                break;
            case R.id.img_dessert:
                b = findViewById(R.id.Dessert);
                categoryid = "04";
                break;
            case R.id.img_beverages:
                b = findViewById(R.id.Beverages);
                categoryid = "05";
                break;
        }
        b.setBackgroundResource(R.drawable.round_btn_menu_black);
        b.setTextColor(ContextCompat.getColor(this, R.color.white));
        previous = b;

        loadListFood(categoryid);

        //search
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Find your Desired Dishes Here!!");

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
                List<String> suggest = new ArrayList<String>();
                for (String search : suggestList)//loop in suggestlist
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
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
                //When search bar is close restore adapter
                if (!enabled)
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
        //
        //End of onCreate()
        //
    }

    @Override
    public void onClick(View v){
        pressed = v.getId();
        switch (pressed) {
            case R.id.Pizza:
                categoryid = "02";
                break;
            case R.id.Pasta:
                categoryid = "03";
                break;
            case R.id.Burger:
                categoryid = "01";
                break;
            case R.id.Sides:
                categoryid = "06";
                break;
            case R.id.Dessert:
                categoryid = "04";
                break;
            case R.id.Beverages:
                categoryid = "05";
                break;
        }
        b = findViewById(pressed);
        b.setBackgroundResource(R.drawable.round_btn_menu_black);
        b.setTextColor(ContextCompat.getColor(this, R.color.white));

        previous.setBackgroundResource(R.drawable.round_btn_menu_white);
        previous.setTextColor(ContextCompat.getColor(this, R.color.black));
        previous = b;

        //load it's menu
        loadListFood(categoryid);
    }

    private void startSearch(CharSequence text) {
        //Create Query by name
        Query searchbyname=ref.orderByChild("name").equalTo(text.toString());

        FirebaseRecyclerOptions<Food1> options=new FirebaseRecyclerOptions.Builder<Food1>()
                .setQuery(searchbyname,Food1.class)
                .build();


        searchadapter=new FirebaseRecyclerAdapter<Food1, FoodHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodHolder foodViewHolder, final int i, @NonNull final Food1 food1) {

                foodViewHolder.name.setText(food1.getName());
                Picasso.with(getBaseContext()).load(food1.getImage())
                        .into(foodViewHolder.image);
                foodViewHolder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final boolean isExists = new Database(getBaseContext()).checkFoodExists(searchadapter.getRef(i).getKey(), common.currentUser.getPhone());
                        if(!isExists)
                        {
                            new Database(getBaseContext()).addToCart(new Order(
                                    common.currentUser.getPhone(),
                                    searchadapter.getRef(i).getKey(),
                                    food1.getName(),
                                    "1",
                                    food1.getPrice(),
                                    food1.getImage()
                            ));
                            Toast.makeText(FoodList.this, "ADDED TO CART", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(FoodList.this,"Already added to Cart.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public FoodHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate( R.layout.item_cards,parent,false);
                return new FoodHolder(itemView);
            }
        };

        searchadapter.startListening();
        recyclerView.setAdapter(searchadapter);
    }

    private void loadSuggest() {
        ref.orderByChild("menuid").equalTo(categoryid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Food1 item=postSnapshot.getValue(Food1.class);
                            suggestList.add(item.getName());//Add name of food to suggest list
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }


    private void loadListFood(final String categoryId) {
        Query searchbyid=ref.orderByChild("menuid").equalTo(categoryId);;
        /*if(veg.isChecked()) {
            searchbyid = ref.orderByChild("menuid").equalTo(categoryId).orderByChild("veg").equalTo("y");
        }
        else
            searchbyid = ref.orderByChild("menuid").equalTo(categoryId);
        */
        FirebaseRecyclerOptions<Food1> options=new FirebaseRecyclerOptions.Builder<Food1>()
                .setQuery(searchbyid,Food1.class)
                .build();

        adapter =new FirebaseRecyclerAdapter<Food1, FoodHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodHolder foodHolder, final int i, @NonNull final Food1 food1) {
                foodHolder.name.setText(food1.getName());
                foodHolder.price.setText(String.format("Rs. %s", food1.getPrice()));
                Picasso.with(getBaseContext()).load(food1.getImage())
                        .into(foodHolder.image);
               /* veg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    notifyDataSetChanged();
                    }
                });*/
               if(new Database(getBaseContext()).checkFoodExists(adapter.getRef(i).getKey(),common.currentUser.getPhone()))
               {
               }

                //Add to Cart
                foodHolder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final boolean isExists = new Database(getBaseContext()).checkFoodExists(adapter.getRef(i).getKey(), common.currentUser.getPhone());
                        if(!isExists)
                        {
                            new Database(getBaseContext()).addToCart(new Order(
                                    common.currentUser.getPhone(),
                                    adapter.getRef(i).getKey(),
                                    food1.getName(),
                                    "1",
                                    food1.getPrice(),
                                    food1.getImage()));
                            Toast.makeText(FoodList.this, "ADDED TO CART", Toast.LENGTH_SHORT).show();
                            foodHolder.add.setVisibility(View.GONE);
                            foodHolder.ele.setVisibility(View.VISIBLE);
                        }
                        else {
                            Toast.makeText(FoodList.this, "Already Added to Cart", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                foodHolder.ele.setNumber("1");
                foodHolder.ele.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                    @Override
                    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                        new Database(getBaseContext()).increaseCart(common.currentUser.getPhone(),adapter.getRef(i).getKey());
                        if(newValue==0)
                        {
                            foodHolder.add.setVisibility(View.VISIBLE);
                            foodHolder.ele.setVisibility(View.GONE);
                        }
                    }
                });

            }

            @NonNull
            @Override
            public FoodHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate( R.layout.item_cards,parent,false);
                return new FoodHolder(itemView);
            }
        };
        //set Adapter
        Log.d("TAG",""+adapter.getItemCount());
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null)
            adapter.startListening();
        if(searchadapter!=null)
            searchadapter.stopListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        if(searchadapter!=null)
            searchadapter.stopListening();
    }

}

/*

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
    ////COOMOMOC

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

                         */
/*   else {
                                new Database(getBaseContext()).increaseCart(common.currentUser.getPhone(), adapter.getRef(i).getKey());
                            }*//*

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
*/
