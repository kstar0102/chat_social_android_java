package ar.codeslu.plax.notify;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Cryp2Code on 9/4/2018.
 */

public interface FCM {
    @Headers({

            "Content-Type: application/json",
            "Authorization:key=AAAAeIu0q_4:APA91bEf4SfkhW_qxce9Y02u6KtREWBFpQV5chjshIzJYxe7KChvGW9JzK9HDKHmRZwcdCzccICY4cqyJIIx_NDBQz2NyDF9I5dRj57eP4KtoFnOehAnyPF7fPk0kIi-hCdOigcKeXCl"
    })
    @POST("fcm/send")
    Call<FCMresp> send(@Body Sender body);
}
