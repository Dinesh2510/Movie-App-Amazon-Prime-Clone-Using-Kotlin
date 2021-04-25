package com.movie.app.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.movie.app.Adapter.ComingAdapter
import com.movie.app.Adapter.WishListAdapter
import com.movie.app.Forms.Login
import com.movie.app.Helper.API
import com.movie.app.Helper.Tools
import com.movie.app.Model.MovieModel
import com.movie.app.R
import kotlinx.android.synthetic.main.activity_langauge_details.*
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap
import kotlin.jvm.Throws

class LangaugeDetails : AppCompatActivity() {
    var recyclerView_popular: RecyclerView? = null
    private lateinit var movieModel: ArrayList<MovieModel>
    var sharedPreferences: SharedPreferences? = null
    var str_userid: String? = null
    var str_name: String? = null
    var str_id: String? = null
    var str_image: String? = null
    var name: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_langauge_details)
        str_image = intent.getStringExtra("image")
        str_name = intent.getStringExtra("name")
        str_id = intent.getStringExtra("id")
        Log.d("tag_labn", "onCreate: " + str_name)
        sharedPreferences =
            getSharedPreferences(Login.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        str_userid = sharedPreferences?.getString(Login.USER_ID, "")
        movieModel = java.util.ArrayList<MovieModel>()

        banner_image != findViewById<ImageView>(R.id.banner_image)
        name != findViewById(R.id.name)
        name?.text = "-$str_name-"
        val media = API.lang_banner + str_image
        Glide.with(this)
            .load(media)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.cinema)
            .into(banner_image)

        ;
        recyclerView_popular = findViewById(R.id.recyclerView_wishlist)
//        recyclerView_popular?.setLayoutManager(GridLayoutManager(applicationContext, 2))
        recyclerView_popular?.setLayoutManager(
            LinearLayoutManager(
                applicationContext,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
    }

    override fun onResume() {
        super.onResume()
        if (Tools.isNetworkAvailable(applicationContext)) {
            getWishList()
        } else {
            Toast.makeText(
                applicationContext,
                "Internet Connection Not Available",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }


    private fun getWishList() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
        progressDialog.show()

        movieModel.clear()
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, API.GetAllMoviesList_DATA,
                com.android.volley.Response.Listener { response ->
                    val code: String
                    val message: String
                    try {
                        val jsonObject = JSONObject(response)
                        Log.d("response", response)
                        code = jsonObject.getString("code")
                        message = jsonObject.getString("status")
                        if (code == "200") {

                            val jsonArray_popular = jsonObject.getJSONArray("movie")


                            Log.d("jsonArray_langauge", "" + jsonArray_popular)


                            /*Popular LIST START*/
                            for (k in 0 until jsonArray_popular.length()) {
                                val listpObject = jsonArray_popular.getJSONObject(k)
                                val model = MovieModel()
                                model.thumbnail = (listpObject.getString("thumbnail"))
                                model.name = (listpObject.getString("name"))
                                model.moive_id = (listpObject.getString("moive_id"))
                                model.moive_banner = (listpObject.getString("banner_url"))
                                movieModel.add(model)
                            }
                            val adaptor_movie = let { ComingAdapter(movieModel, it) }
                            recyclerView_popular?.adapter = adaptor_movie


                        } else {
                            Toast.makeText(
                                applicationContext,
                                "" + message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {

                        Toast.makeText(
                            applicationContext,
                            "" + e.printStackTrace().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    progressDialog.dismiss()
                }, com.android.volley.Response.ErrorListener {
                    progressDialog.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): MutableMap<String?, String?> {
                    val params: MutableMap<String?, String?> =
                        HashMap()
                    params["user_id"] = str_userid
                    Log.d("tag_pa", "getParams: $params")
                    return params
                }

            }
        Volley.newRequestQueue(applicationContext).add(stringRequest)
    }


}