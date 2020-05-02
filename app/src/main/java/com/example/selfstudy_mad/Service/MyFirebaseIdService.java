package com.example.selfstudy_mad.Service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.selfstudy_mad.Model.Token;
import com.example.selfstudy_mad.common.common;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseIdService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("NEW_TOKENS",s);
       //String tokenrefresh= FirebaseInstanceId.getInstance().getToken();
       FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
           @Override
           public void onSuccess(InstanceIdResult instanceIdResult) {
             String tokenrefresh=instanceIdResult.getToken();
               if (common.currentUser!=null)
                   updateTokenToFirebase(tokenrefresh);
           }
       });




    }

    private void updateTokenToFirebase(String tokenrefresh) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Token");
        Token token=new Token(tokenrefresh,false);
        tokens.child(common.currentUser.getPhone()).setValue(token);

    }
}
