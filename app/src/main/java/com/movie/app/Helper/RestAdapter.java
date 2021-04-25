package com.movie.app.Helper;


import com.movie.app.Helper.API;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.movie.app.Helper.API.WEB_URL;


//import com.digital.cart.BuildConfig;

public class RestAdapter {

    public static API createAPI() {
      /*  HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(5, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        if(BuildConfig.DEBUG){
            builder.addInterceptor(logging);
        }
        builder.cache(null);
        OkHttpClient okHttpClient = builder.build();
*/
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WEB_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(new OkHttpClient().newBuilder()
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(API.class);
    }
}
