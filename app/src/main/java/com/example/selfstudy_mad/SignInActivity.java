package com.example.selfstudy_mad;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class SignInActivity extends AppCompatActivity {
    EditText edtPhone, edtPassword;
    Button btnSignIn;
    CheckBox btncheckbox;
    TextView txtnewuser;
    EditText s;

    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPhone = findViewById(R.id.userphnum);
        edtPassword = findViewById(R.id.userpwd);
        btnSignIn = findViewById(R.id.signin);
        btncheckbox=findViewById(R.id.rememberme);
        txtnewuser=findViewById(R.id.newuser);
        //txtForgotpassword=findViewById(R.id.forgotpassword);
        //s=findViewById(R.id.secure);



        //init paper
        Paper.init(this);

        //Init Firebase
         database = FirebaseDatabase.getInstance();
         table_user = database.getReference("User");

        /*txtForgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotpwd();
            }
        });*/
        txtnewuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (common.isConnectedToInternet(getBaseContext())){


                //save user and password
                if(btncheckbox.isChecked())
                {
                    Paper.book().write(common.USER_KEY,edtPhone.getText().toString());
                    Paper.book().write(common.PWD_KEY,edtPassword.getText().toString());

                }

                final ProgressDialog mDialog = new ProgressDialog(SignInActivity.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();



                if (edtPassword.length()==0||edtPhone.length()==0)
                {
                    mDialog.dismiss();
                    Toast.makeText(SignInActivity.this, "Please enter the details !", Toast.LENGTH_SHORT).show();
                }
                else {
                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //check if user not in db
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                //get user inf0
                                mDialog.dismiss();
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(edtPhone.getText().toString());
                                if (user.getPassword().equals(edtPassword.getText().toString())) {
                                    Intent homeIntent = new Intent(SignInActivity.this, Home.class);
                                    common.currentUser = user;
                                    common.loggedin = "y";
                                    startActivity(homeIntent);
                                    finish();
                                    table_user.removeEventListener(this);
                                } else {
                                    Toast.makeText(SignInActivity.this, "Wrong pasword", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignInActivity.this, "User does not Exist;New User?Please register first", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
                else
                {
                    Toast.makeText(SignInActivity.this,"Please check Your Connection",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });



    }

    private void showForgotpwd() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter Your Secure Code");

        LayoutInflater inflater=this.getLayoutInflater();
        View forgot=inflater.inflate(R.layout.forgot_password,null);

        builder.setView(forgot);

      final  EditText phone=forgot.findViewById(R.id.phnum);
        //final EditText s=(EditText)forgot.findViewById(R.id.secure);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //check if user is available
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user=dataSnapshot.child(phone.getText().toString()).getValue(User.class);
                        Toast.makeText(SignInActivity.this,"kk:"+s.getText().toString(),Toast.LENGTH_SHORT).show();

                       // if (user.getSecure().equals(s.getText().toString())) {
                          //  Toast.makeText(SignInActivity.this, "Your Password :"+user.getPassword(), Toast.LENGTH_SHORT).show();
                      //  }else
                        //    Toast.makeText(SignInActivity.this,"Wrong Secure code!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }
}
