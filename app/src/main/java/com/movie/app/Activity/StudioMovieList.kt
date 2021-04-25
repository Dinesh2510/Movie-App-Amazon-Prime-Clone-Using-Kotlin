package com.movie.app.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.movie.app.Adapter.WishListAdapter
import com.movie.app.Forms.Login
import com.movie.app.Helper.API
import com.movie.app.Helper.Tools
import com.movie.app.Model.MovieModel
import com.movie.app.R
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap
import kotlin.jvm.Throws

class StudioMovieList : AppCompatActivity() {
    var recyclerView_popular: RecyclerView? = null
    private lateinit var movieModel: ArrayList<MovieModel>
    var sharedPreferences: SharedPreferences? = null
    var str_userid: String? = null
    var str_studio: String? = null
    var str_studio_name: String? = null
    var toolname: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studio_movie_list)
        str_studio = intent.getStringExtra("id")
        str_studio_name = intent.getStringExtra("name")

        sharedPreferences =
            getSharedPreferences(Login.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        str_userid = sharedPreferences?.getString(Login.USER_ID, "")
        movieModel = java.util.ArrayList<MovieModel>()
        toolname = findViewById(R.id.toolname)
        toolname?.text = str_studio_name
        recyclerView_popular = findViewById(R.id.recyclerView_studio_movie)
        recyclerView_popular?.setLayoutManager(GridLayoutManager(applicationContext, 2))

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
                Method.POST, API.GETSTUDIO_DATA,
                com.android.volley.Response.Listener { response ->
                    val code: String
                    val message: String
                    try {
                        val jsonObject = JSONObject(response)
                        Log.d("response", response)
                        code = jsonObject.getString("code")
                        message = jsonObject.getString("status")
                        if (code == "200") {

                            val jsonArray_popular = jsonObject.getJSONArray("popular")


                            Log.d("jsonArray_langauge", "" + jsonArray_popular)


                            /*Popular LIST START*/
                            for (k in 0 until jsonArray_popular.length()) {
                                val listpObject = jsonArray_popular.getJSONObject(k)
                                val model = MovieModel()
                                model.thumbnail = (listpObject.getString("thumbnail"))
                                model.name = (listpObject.getString("name"))
                                model.moive_id = (listpObject.getString("moive_id"))
                                movieModel.add(model)
                            }
                            val adaptor_movie = let { WishListAdapter(movieModel, it) }
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
                    params["studio_id"] = str_studio
                    Log.d("tag_pa", "getParams: $params")
                    return params
                }

            }
        Volley.newRequestQueue(applicationContext).add(stringRequest)
    }
}