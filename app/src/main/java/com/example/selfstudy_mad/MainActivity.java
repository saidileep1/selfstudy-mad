package com.example.selfstudy_mad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnSignUp;
    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn =  findViewById(R.id.signin);
        btnSignUp = findViewById(R.id.signup);

        txtSlogan = findViewById(R.id.foodtitle);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);

        //init paper
        Paper.init(this);

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
        });

        //Check Remeber is checked or not
        String user=Paper.book().read(common.USER_KEY);
        String pwd=Paper.book().read(common.PWD_KEY);
        
        if (user !=null && pwd !=null)
        {
            if (!user.isEmpty()&&!pwd.isEmpty())
                login(user,pwd);
        }

    }
    //Init Firebase
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference table_user = database.getReference("User");



    private void login(final String phone, final String pwd) {
        //save user and password


        final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("please wait...");
        mDialog.show();




        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check if user not in db

                if(dataSnapshot.child(phone).exists()) {
                    //get user inf0
                    mDialog.dismiss();
                    User user = dataSnapshot.child(phone).getValue(User.class);
                    user.setPhone(phone);
                    if (user.getPassword().equals(pwd)) {
                        Intent homeIntent=new Intent(MainActivity.this,Home.class);
                        common.currentUser=user;
                        startActivity(homeIntent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Wrong pasword", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    mDialog.dismiss();
                    Toast.makeText(MainActivity.this,"User does not Exist;New User?Please register first",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}