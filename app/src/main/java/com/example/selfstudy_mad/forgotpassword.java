package com.example.selfstudy_mad;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.selfstudy_mad.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class forgotpassword extends AppCompatActivity {

    EditText sec;
    EditText phone;
    Button btn;
    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        //sec=findViewById(R.id.secure);
        phone=findViewById(R.id.phnum);
        btn=findViewById(R.id.getpass);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               table_user.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                       Toast.makeText(forgotpassword.this,"kk:"+phone,Toast.LENGTH_SHORT).show();
                       User user=dataSnapshot.child(phone.getText().toString()).getValue(User.class);
                       user.setPhone(phone.getText().toString());


                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
            }
        });

    }
}
