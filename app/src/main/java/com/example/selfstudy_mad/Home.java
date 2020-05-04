package com.example.selfstudy_mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.animation.ArgbEvaluator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.selfstudy_mad.Database.Database;
import com.example.selfstudy_mad.Model.Token;
import com.example.selfstudy_mad.Model.User;
import com.example.selfstudy_mad.common.common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Home extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    //--------------------------------Variable Declaration------------------------------
    public static final String EXTRA_category="com.example.food_app.EXTRA_category";
    ViewPager viewPager;    //for banner
    Adapter adapter;        //for viewpager
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ArrayList<Integer> banner=new ArrayList<>();
    CounterFab fab;
    //ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    //-------------------------------onCreate------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //inti paper

        Paper.init(this);
        updateToken(FirebaseInstanceId.getInstance().getToken());
        //---------------------------Hook--------------------------------
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("The Corridor");
        //--------------------------Toolbar-------------------------------
        setSupportActionBar(toolbar);
        //--------------------------Navigation View----------------------------------------------------
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);         //implemented later

        //----------banner array data----------------
        banner.add(R.drawable.banner1);
        banner.add(R.drawable.banner5);
        banner.add(R.drawable.banner2);
        banner.add(R.drawable.banner3);
        banner.add(R.drawable.banner4);

        adapter = new Adapter(banner,this);

        //-----------------View Pager---------------------------
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(50, 0, 50, 0);
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(new CircularViewPagerHandler(viewPager));

        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent =new Intent(Home.this,Cart.class);
                startActivity(cartIntent);
            }
        });
        fab.setCount(new Database(this).geCountCart(common.currentUser.getPhone()));

    }

    private void updateToken(String token) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Token");
        Token data=new Token(token,false);
        tokens.child(common.currentUser.getPhone()).setValue(data);
    }
    //----------------------For Circular ViewPager-----------------------------
    public static class CircularViewPagerHandler implements ViewPager.OnPageChangeListener {
        private ViewPager   mViewPager;
        private int         mCurrentPosition;
        private int         mScrollState;

        public CircularViewPagerHandler(final ViewPager viewPager) {
            mViewPager = viewPager;
        }

        @Override
        public void onPageSelected(final int position) {
            mCurrentPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
            handleScrollState(state);
            mScrollState = state;
        }

        private void handleScrollState(final int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE && mScrollState == ViewPager.SCROLL_STATE_DRAGGING) {
                setNextItemIfNeeded();
            }
        }

        private void setNextItemIfNeeded() {
            if (!isScrollStateSettling()) {
                handleSetNextItem();
            }
        }

        private boolean isScrollStateSettling() {
            return mScrollState == ViewPager.SCROLL_STATE_SETTLING;
        }

        private void handleSetNextItem() {
            final int lastPosition = mViewPager.getAdapter().getCount() - 1;
            if(mCurrentPosition == 0) {
                mViewPager.setCurrentItem(lastPosition, true);
            } else if(mCurrentPosition == lastPosition) {
                mViewPager.setCurrentItem(0, true);
            }
        }

        @Override
        public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
        }
    }

    //-----------To pressing back while Drawer is open ---------------------------------
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    //-----------------------Common Onclick Listener------------------------
    //-----------------------Redirects to Menu Activity---------------------
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(Home.this,FoodList.class);
        intent.putExtra(EXTRA_category, v.getId());
        startActivity(intent);
    }

    //---------------------Nav Menu item selected----------------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.nav_cart){
            Intent cart=new Intent(Home.this,Cart.class);
            startActivity(cart);
        }
        else if(id==R.id.nav_orders) {
            Intent order = new Intent(Home.this, OrderStatus.class);
            startActivity(order);
        }
        else if(id==R.id.nav_log_out){
            //Delete Rememnber me
            Paper.book().destroy();
            common.loggedin="n";
            //Logout
            Intent signin=new Intent(Home.this,SignInActivity.class);
            signin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signin);
            finish();
        }
        else if (id==R.id.nav_change_password)
        {
            showchangepassworddialog();
        }
        else if (id==R.id.nav_home)
        {
            showhomeaddressdialog();
        }

        DrawerLayout drawer=findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).geCountCart(common.currentUser.getPhone()));
    }

    private void showhomeaddressdialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);

        alertDialog.setTitle("Add Home Address");
        alertDialog.setMessage("Please Fill the Information");

        LayoutInflater inflater=LayoutInflater.from(this);
        View layout_home=inflater.inflate(R.layout.home_address_layout,null);

        final MaterialEditText homeaddress=(MaterialEditText)layout_home.findViewById(R.id.homeaddress);

        alertDialog.setView(layout_home);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                //Set New Home address
                common.currentUser.setHomeaddress(homeaddress.getText().toString());

                FirebaseDatabase.getInstance().getReference("User")
                        .child(common.currentUser.getPhone())
                        .setValue(common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Home.this,"Address Updated Successfully!!",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        alertDialog.show();

    }



    private void showchangepassworddialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Change Password");
        alertDialog.setMessage("Please Fill the Information");

        LayoutInflater inflater=LayoutInflater.from(this);
        View layout_pwd=inflater.inflate(R.layout.change_password_layout,null);

        final MaterialEditText oldPassword=(MaterialEditText)layout_pwd.findViewById(R.id.oldPass);
        final MaterialEditText newPassword=(MaterialEditText)layout_pwd.findViewById(R.id.newPass);
        final MaterialEditText confirmPassword=(MaterialEditText)layout_pwd.findViewById(R.id.confirmPass);

        alertDialog.setView(layout_pwd);

        //Button
        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Change Password here

                final android.app.AlertDialog waitingDialog=new SpotsDialog(Home.this);
                waitingDialog.show();

                //Check Old Password
                if(oldPassword.getText().toString().equals(common.currentUser.getPassword()))
                {
                    //Check new password and confirm password
                    if (newPassword.getText().toString().equals(confirmPassword.getText().toString()))
                    {
                        Map<String,Object> passwordUpdate = new HashMap<>();
                        passwordUpdate.put("password",newPassword.getText().toString());

                        //Make update in database
                        DatabaseReference user=FirebaseDatabase.getInstance().getReference("User");
                        user.child(common.currentUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(Home.this,"Password Updated!!",Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Home.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else
                    {
                        waitingDialog.dismiss();
                        Toast.makeText(Home.this,"Both Passwords are different.",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this,"Wrong old Password! Please retry",Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////////
/*

package com.example.selfstudy_mad;

        import android.content.DialogInterface;
        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.ActionBarDrawerToggle;
        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;
        import androidx.core.view.GravityCompat;
        import androidx.drawerlayout.widget.DrawerLayout;
        import androidx.navigation.NavController;
        import androidx.navigation.Navigation;
        import androidx.navigation.ui.AppBarConfiguration;
        import androidx.navigation.ui.NavigationUI;
        import androidx.recyclerview.widget.GridLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.andremion.counterfab.CounterFab;
        import com.daimajia.slider.library.Animations.DescriptionAnimation;
        import com.daimajia.slider.library.SliderLayout;
        import com.daimajia.slider.library.SliderTypes.BaseSliderView;
        import com.daimajia.slider.library.SliderTypes.TextSliderView;
        import com.example.selfstudy_mad.Database.Database;
        import com.example.selfstudy_mad.Interface.ItemClickListener;
        import com.example.selfstudy_mad.Model.Banner;
        import com.example.selfstudy_mad.Model.Category;
        import com.example.selfstudy_mad.Model.Token;
        import com.example.selfstudy_mad.ViewHolder.MenuViewHolder;
        import com.example.selfstudy_mad.common.common;
        import com.firebase.ui.database.FirebaseRecyclerAdapter;
        import com.firebase.ui.database.FirebaseRecyclerOptions;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.Task;
        import com.google.android.material.navigation.NavigationView;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.iid.FirebaseInstanceId;
        import com.rengwuxian.materialedittext.MaterialEditText;
        import com.squareup.picasso.Picasso;

        import java.util.HashMap;
        import java.util.Map;

        import dmax.dialog.SpotsDialog;
        import io.paperdb.Paper;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;
    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;
    CounterFab fab;

    //Slider
    HashMap<String,String>image_list;
    SliderLayout mSlider;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");

        setSupportActionBar(toolbar);
        //View

        //Init Firebase
        database=FirebaseDatabase.getInstance();
        category=database.getReference("Category");

        //inti paper

        Paper.init(this);



        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent =new Intent(Home.this,Cart.class);
                startActivity(cartIntent);
            }
        });

        fab.setCount(new Database(this).geCountCart(common.currentUser.getPhone()));

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        ActionBarDrawerToggle toggle =

                new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);



        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        //Set name for user
        View headerView=navigationView.getHeaderView(0);
        txtFullName=(TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(common.currentUser.getName());
        //Load menu from db
        recycler_menu=(RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        //layoutManager=new LinearLayoutManager(this);
        //recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.setLayoutManager(new GridLayoutManager(this,2));

        if (common.isConnectedToInternet(getBaseContext()))
            loadMenu();
        else
        {
            Toast.makeText(getBaseContext(),"Please check Your Connection",Toast.LENGTH_SHORT).show();
            return;
        }



        updateToken(FirebaseInstanceId.getInstance().getToken());

        //Setup Slider
        setupSlider();

    }

    private void setupSlider() {
        mSlider=findViewById(R.id.slider);
        image_list =new HashMap<>();

        final DatabaseReference banners=database.getReference("Banner");
        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Banner banner=postSnapshot.getValue(Banner.class);
                    //We Will concat string name and id and use pix=zaa for description
                    image_list.put(banner.getName()+"@@@"+banner.getId(),banner.getImage());
                }
                for (String key:image_list.keySet())
                {
                    String[] keySplit=key.split("@@@");
                    String nameOfFood=keySplit[0];
                    String idOfFood=keySplit[1];

                    //Create Slider
                    final TextSliderView textSliderView=new TextSliderView(getBaseContext());
                    textSliderView
                            .description(nameOfFood)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent=new Intent(Home.this,FoodDetails.class);
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });
                    //Add extra bundle
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("FoodId",idOfFood);

                    mSlider.addSlider(textSliderView);
                    //remove event after finish
                    banners.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Datareturn true;
        base(this).geCountCart(common.currentUser.getPhone()));
        mSlider.startAutoCycle();

        if(adapter!=null)
            adapter.startListening();
    }


    private void updateToken(String token) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Token");
        Token data=new Token(token,false);
        tokens.child(common.currentUser.getPhone()).setValue(data);
    }

    private void loadMenu() {

        FirebaseRecyclerOptions<Category> options=new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category,Category.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder menuViewHolder, int i, @NonNull Category model) {

                menuViewHolder.txtMenuName.setText(model.getName());
                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(menuViewHolder.imageView);
                final Category clickitem=model;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //get category id and send to new activity
                        Intent foodlist =new Intent(Home.this,FoodList.class);
                        //Categoryid is key,just the key
                        foodlist.putExtra("categoryId",adapter.getRef(position).getKey());
                        startActivity(foodlist);
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemview=LayoutInflater.from(parent.getContext())
                        .inflate( R.layout.menu_item,parent,false);
                return new MenuViewHolder(itemview);
            }
        };
        adapter.startListening();
        Log.d("TAG",""+adapter.getItemCount());
        recycler_menu.setAdapter(adapter);
    }//ccc

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        mSlider.stopAutoCycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.nav_cart){
            Intent cart=new Intent(Home.this,Cart.class);
            startActivity(cart);

        }
        else if(id==R.id.nav_orders){
            Intent order=new Intent(Home.this,OrderStatus.class);
            startActivity(order);

        }else if(id==R.id.nav_log_out){
            //Delete Rememnber me
            Paper.book().destroy();

            //Logout
            Intent signin=new Intent(Home.this,SignInActivity.class);
            signin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signin);

        }
        else if (id==R.id.nav_change_password)
        {
            showchangepassworddialog();
        }
        else if (id==R.id.nav_home)
        {
            showhomeaddressdialog();


        }

        DrawerLayout drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showhomeaddressdialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);

        alertDialog.setTitle("Add Home Address");
        alertDialog.setMessage("Please Fill the Information");

        LayoutInflater inflater=LayoutInflater.from(this);
        View layout_home=inflater.inflate(R.layout.home_address_layout,null);

        final MaterialEditText homeaddress=(MaterialEditText)layout_home.findViewById(R.id.homeaddress);

        alertDialog.setView(layout_home);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                //Set New Home address
                common.currentUser.setHomeaddress(homeaddress.getText().toString());

                FirebaseDatabase.getInstance().getReference("User")
                        .child(common.currentUser.getPhone())
                        .setValue(common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Home.this,"Updated Address Successfully!!",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        alertDialog.show();

    }

    private void showchangepassworddialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Change Password");
        alertDialog.setMessage("Please Fill the Information");

        LayoutInflater inflater=LayoutInflater.from(this);
        View layout_pwd=inflater.inflate(R.layout.change_password_layout,null);

        final MaterialEditText oldPassword=(MaterialEditText)layout_pwd.findViewById(R.id.oldPass);
        final MaterialEditText newPassword=(MaterialEditText)layout_pwd.findViewById(R.id.newPass);
        final MaterialEditText confirmPassword=(MaterialEditText)layout_pwd.findViewById(R.id.confirmPass);

        alertDialog.setView(layout_pwd);

        //Button
        alertDialog.setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Change Password here

                final android.app.AlertDialog waitingDialog=new SpotsDialog(Home.this);
                waitingDialog.show();

                //Check Old Password
                if(oldPassword.getText().toString().equals(common.currentUser.getPassword()))
                {
                    //Check new password and confirm password
                    if (newPassword.getText().toString().equals(confirmPassword.getText().toString()))
                    {
                        Map<String,Object> passwordUpdate = new HashMap<>();
                        passwordUpdate.put("password",newPassword.getText().toString());

                        //Make update in database
                        DatabaseReference user=FirebaseDatabase.getInstance().getReference("User");
                        user.child(common.currentUser.getPhone())
                                .updateChildren(passwordUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(Home.this,"Password Updated!!",Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Home.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else
                    {
                        waitingDialog.dismiss();
                        Toast.makeText(Home.this,"Both Passwords are different.",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    waitingDialog.dismiss();
                    Toast.makeText(Home.this,"Wrong old Password! Please retry",Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

}
*/
