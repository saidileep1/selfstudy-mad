package com.example.selfstudy_mad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.selfstudy_mad.Model.User;
import com.example.selfstudy_mad.common.common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {


    //Variable Definition
    Button btnSignIn, btnSignUp;
    TextView txtSlogan;
    Timer timer;
    Intent intent;
    Boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Introduction Activity(Application starts here)
        Paper.init(this);

        String user = Paper.book().read(common.USER_KEY);
        String pwd = Paper.book().read(common.PWD_KEY);

        if (user != null && pwd != null) {
            if (!user.isEmpty() && !pwd.isEmpty()) {
                login(user, pwd);
                flag=true;
            }
        }

        timer = new Timer();         //Create timer obj
        //To automatically switch activity after timer over
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!flag){
                    intent = new Intent(MainActivity.this, SignInActivity.class);   //Switch to Dashboard
                    startActivity(intent);
                }
            }
        }, 3000);
    }

/*
        btnSignIn =  findViewById(R.id.signin);
        btnSignUp = findViewById(R.id.signup);

        txtSlogan = findViewById(R.id.foodtitle);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);

        //init paper
        Paper.init(this);mDialog.dismiss();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent (MainActivity.this, SignInActivity.class);
                startActivity(signIn);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent (MainActivity.this, SignUpActivity.class);
                startActivity(signup);
            }
        });*/

        //Check Remeber is checked or not

    //Init Firebase
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference table_user = database.getReference("User");

    private void login(final String phone, final String pwd) {
        //save user and password

        /*final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.show();*/

        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check if user not in db
                if(dataSnapshot.child(phone).exists()) {
                    //get user inf0
                    /*mDialog.dismiss();*/
                    User user = dataSnapshot.child(phone).getValue(User.class);
                    user.setPhone(phone);
                    if (user.getPassword().equals(pwd)) {
                        Intent homeIntent=new Intent(MainActivity.this,Home.class);
                        common.currentUser=user;
                        common.loggedin="y";
                        startActivity(homeIntent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,SignInActivity.class));
                        finish();
                    }
                }
                else
                {
                    /*mDialog.dismiss();*/
                    Toast.makeText(MainActivity.this,"User Not Found! Please Sign Up.",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,Home.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}