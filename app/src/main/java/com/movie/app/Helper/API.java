package com.movie.app.Helper;


import com.movie.app.Helper.CallbackSliderImage;
import com.movie.app.ServerCall.CallbackFlag;
import com.movie.app.ServerCall.CallbackPin;
import com.movie.app.ServerCall.CallbackUser;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface API {
    public static String APP_NMAE = "Movie App";

    String WEB_URL = "http://pixeldev.in/webservices/";
    String WEB_URL_all = "http://pixeldev.in/webservices/movie_app/";

    String ImageUrl = "http://pixeldev.in/webservices/movie_app/movie_admin/";
    String movie_short = "http://pixeldev.in/webservices/movie_app/movie_admin/movie_shorts/";
    String language_img = "http://pixeldev.in/webservices/movie_app/movie_admin/language_img/";
    String movie_screen = "http://pixeldev.in/webservices/movie_app/movie_admin/movie_screen/";
    String movie_thumbnail = "http://pixeldev.in/webservices/movie_app/movie_admin/movie_thumbnail/";
    String movie_video = "http://pixeldev.in/webservices/movie_app/movie_admin/";
    String studio_image = "http://pixeldev.in/webservices/movie_app/movie_admin/";
    String play_image = "http://pixeldev.in/webservices/movie_app/movie_admin/banner_images/"; //movie_banner url
    String movie_genre = "http://pixeldev.in/webservices/movie_app/movie_admin/movie_genre/";
    String lang_banner = "http://pixeldev.in/webservices/movie_app/movie_admin/lang_banner/";

    String Social_Login = WEB_URL_all + "Social_Login.php";
    String HOME_DATA = WEB_URL_all + "GetAllHomeList.php";
    String FIND_DATA = WEB_URL_all + "GetFindData.php";
    String ADD_WISH_DATA = WEB_URL_all + "AddToWishList.php";
    String CHECK_WISHLIST = WEB_URL_all + "CheckWishList.php";
    String Remove_WISHLIST = WEB_URL_all + "RemoveWishList.php";
    String WISHLIST_DATA = WEB_URL_all + "GetWishList.php";
    String GETSTUDIO_DATA = WEB_URL_all + "GetStudioMovie.php";
    String GetAllMoviesList_DATA = WEB_URL_all + "GetAllMoviesList.php";
    String GetMovieData = WEB_URL_all + "GetMovieData.php";

    //MainScreen Slider
    @GET("movie_app/GetBannerList.php")
    Call<CallbackSliderImage> getSliderImage();



    @GET("movie_app/Login.php")
    Call<CallbackUser> login(
            @Query("email") String email,
            @Query("password") String password,
            @Query("device_id") String notif_device
    );

    @GET("movie_app/insert_address.php")
    Call<CallbackUser> insert_address(
            @Query("address") String address,
            @Query("city") String city,
            @Query("state") String state,
            @Query("country") String country,
            @Query("zipcode") String zipcode,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("user_id") String user_id

    );

    @GET("movie_app/Update_Address.php")
    Call<CallbackUser> Update_Address(
            @Query("address_id") String address_id,
            @Query("address") String address,
            @Query("city") String city,
            @Query("state") String state,
            @Query("country") String country,
            @Query("zipcode") String zipcode,
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("user_id") String user_id

    );

    @GET("movie_app/Delete_User.php")
    Call<CallbackUser> Delete_User(
            @Query("user_id") String user_id
    );

    @GET("movie_app/Delete_address.php")
    Call<CallbackUser> Delete_address(
            @Query("address_id") String address_id,
            @Query("user_id") String user_id

    );
    @GET("movie_app/sendemail.php")
    Call<CallbackPin> getpincode(
            @Query("email") String email,
            @Query("name") String name
    );
    @GET("movie_app/Update_User.php")
    Call<CallbackUser> Update_User(
            @Query("email") String email,
            @Query("first_name") String first_name,
            @Query("last_name") String last_name,
            @Query("password") String password,
            @Query("user_id") String user_id,
            @Query("dob") String dob,
            @Query("phone") String phone

    );
    @GET("movie_app/sendflag.php")
    Call<CallbackFlag> sendFlag(
            @Query("user_id") String user_id
    );

    @GET("movie_app/SendPasswordEmail.php")
    Call<CallbackPin> Sendpassemail(
            @Query("email") String email
    );
    @Multipart
    @POST("movie_app/Register.php")
    Call<CallbackUser> register(
            @Part MultipartBody.Part avatar,
            @PartMap Map<String, RequestBody> data
    );

}
