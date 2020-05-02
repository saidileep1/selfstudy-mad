package com.example.selfstudy_mad;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.selfstudy_mad.Model.User;
import com.example.selfstudy_mad.common.common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    EditText edtPhone, edtName, edtPassword,edtsecure;
    Button btnSignUp;
    TextView existinguser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

   edtPhone = findViewById(R.id.phnum);
      edtName = findViewById(R.id.name);
       edtPassword = findViewById(R.id.pwd);
        //edtsecure=findViewById(R.id.secure);

        btnSignUp = findViewById(R.id.signup);
        existinguser=findViewById(R.id.existinguser);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        existinguser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (common.isConnectedToInternet(getBaseContext())){
                final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                mDialog.setMessage("Please Waiting...");
                mDialog.show();
                if (edtName.length()==0 ||edtPassword.length()==0||edtPhone.length()==0)
                {
                    mDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Please enter the details !", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (edtPhone.length()<10||edtPhone.length()>10){
                        mDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Please enter the proper number !", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        table_user.addValueEventListener(new ValueEventListener() {
                            @Override

                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //check if user not exist in database

                                if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                    //get user information
                                    mDialog.dismiss();
                                    User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                    Toast.makeText(SignUpActivity.this, "Phone number already registered !", Toast.LENGTH_SHORT).show();

                                } else {
                                    mDialog.dismiss();
                                    User user = new User(edtName.getText().toString(), edtPassword.getText().toString()
                                            );
                                    table_user.child(edtPhone.getText().toString()).setValue(user);
                                    Toast.makeText(SignUpActivity.this, "Sign Up Successfully !", Toast.LENGTH_SHORT).show();
//////////Send it to Home Page/////////////////
                                    finish();//////////
                                }
                            }
                            @Override
                            public void onCancelled (DatabaseError databaseError){

                            }
                        });
                    }

                }
            }
                else
                {
                    Toast.makeText(SignUpActivity.this,"Please check Your Connection",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
