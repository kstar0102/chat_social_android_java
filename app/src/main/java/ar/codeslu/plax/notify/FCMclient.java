package ar.codeslu.plax.notify;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by CodeSlu on 9/6/2018.
 */

public class FCMclient {
    private static Retrofit retrofit = null;
    public static  Retrofit getClient(String baseurl)
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseurl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
