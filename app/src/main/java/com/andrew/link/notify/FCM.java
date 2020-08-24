package com.andrew.link.notify;

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
            "Authorization:key=AAAAEZd0qv0:APA91bEFaPhCPog7xCTFdP0w6Y8qVivwVOO6KYDh4adhDPd-TtZnHgx4gi-ogGhwwwE1RqfQDkqkS-EyA9Ybp-KNdfgh9khHw33scnRMOht5cCprcjCNOr6Z28rNseqio9KVkRY7QkT6"
    })
    @POST("fcm/send")
    Call<FCMresp> send(@Body Sender body);
}
