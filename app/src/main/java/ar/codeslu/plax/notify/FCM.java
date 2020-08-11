package ar.codeslu.plax.notify;

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
            "Authorization:key=AAAADsiHrEY:APA91bG2a0p-hfJlw-_t7kch0OALLFpNK-ZAMSL9dMyjkBuVqqFJCBGGzDOGpMo2a2ws_l8NEvRQcC0EwOewDolDdqv52eYMcCUtiUYrXoCuodBQk9hdJfrBanfD8Zan1TkKH4hxl2ay"
    })
    @POST("fcm/send")
    Call<FCMresp> send(@Body Sender body);
}
