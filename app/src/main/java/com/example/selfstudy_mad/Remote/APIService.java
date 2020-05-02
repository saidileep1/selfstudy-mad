package com.example.selfstudy_mad.Remote;


import com.example.selfstudy_mad.Model.MyResponse;
import com.example.selfstudy_mad.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
@Headers(
        {
                "Content-Type:application/json",
                "Authorization:key=AAAAmF_ujLA:APA91bHbkBiZKAwwAOl8gKIx2kiVcmqkueEv1sJV8t62y0do2Zzb7B0AnTBfyQC0PWj5gh2HPmxiJ5hPQ2_ivujDHwpBXNQL-8Gg_ZnQWjpVdTEMD5cehqyj7hpfif-7JsFsOykwNUlm"
        }
)
    @POST("fcm/send")

    Call<MyResponse> sendNotification(@Body Sender body);


}
