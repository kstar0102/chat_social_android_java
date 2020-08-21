package com.codeslutest.plax.notify;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by CodeSlu on 9/4/2018.
 */

public interface FCM {
    @Headers({

            "Content-Type: application/json",
            "Authorization:key=AAAAin4GMP8:APA91bGHN8bOwSCBDjfbjMZkS3vltp58nVw817K9-3aWIHvqtp2mAp9rC8lqtwScVA5CR9KYJqNFXn5YmBSe5nuyrd6-yB8S_2KNKw9g4a_uKYfuKzdvhdju7PJwymT3WOjnmNy9uZA1"
    })
    @POST("fcm/send")
    Call<FCMresp> send(@Body Sender body);
}
