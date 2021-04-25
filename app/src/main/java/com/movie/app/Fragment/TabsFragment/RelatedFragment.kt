package com.movie.app.Fragment.TabsFragment

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.movie.app.Adapter.ComingAdapter
import com.movie.app.Helper.API
import com.movie.app.Helper.Tools
import com.movie.app.Model.MovieModel
import com.movie.app.R
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.fragment_home2.*
import kotlinx.android.synthetic.main.fragment_related.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
import kotlin.jvm.Throws

class RelatedFragment : Fragment() {
    private lateinit var movieModel_comingsoon: ArrayList<MovieModel>
    var recyclerView_csoon: RecyclerView? = null
    var str_movieId: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_related, container, false)
        str_movieId = activity?.intent?.getStringExtra("id")
        Log.d("str_movieId_1", "onResume: "+str_movieId)

        movieModel_comingsoon = java.util.ArrayList<MovieModel>()
        recyclerView_csoon = view.findViewById(R.id.recyclerView_rel_video)

        recyclerView_csoon?.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        return view
    }
    override fun onResume() {
        super.onResume()
        if (Tools.isNetworkAvailable(activity)) {
            Log.d("str_movieId", "onResume: "+str_movieId)
            GETMOvieData(str_movieId)
        } else {
            Toast.makeText(activity, "Internet Connection Not Available", Toast.LENGTH_SHORT)
                .show()
            no_internet?.setVisibility(View.VISIBLE)
        }
    }
    private fun GETMOvieData(id: String?) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
        progressDialog.show();
        movieModel_comingsoon.clear()

        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            API.GetMovieData,
            Response.Listener { response ->
                Log.d("quick_4", "onResponse: $response")
                try {
                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("message")
                    val code = jsonObject.getString("code")
                    if (code == "200") {
                        val jsonArray_popular = jsonObject.getJSONArray("movie_data")
                        val jsonArray_movie_data_list = jsonObject.getJSONArray("movie_data_list")
                        //coming soon START*//*
                        for (m in 0 until jsonArray_movie_data_list.length()) {
                            val listplayObject = jsonArray_movie_data_list.getJSONObject(m)
                            val model = MovieModel()
                            model.thumbnail = (listplayObject.getString("thumbnail"))
                            model.name = (listplayObject.getString("name"))
                            model.moive_id = (listplayObject.getString("moive_id"))
                            model.moive_banner = (listplayObject.getString("moive_banner"))
                            model.video_url = (listplayObject.getString("video_url"))
                            model.moive_tag = (listplayObject.getString("moive_tag"))
                            model.trailer_url = (listplayObject.getString("trailer_url"))
                            Log.d(
                                "Tag_boow",
                                "getSubjectList: " + (listplayObject.getString("name"))
                            )
                            movieModel_comingsoon.add(model)
                        }
                        var adaptor_moive_play =
                            activity?.let { ComingAdapter(movieModel_comingsoon, it) }
                        recyclerView_csoon?.adapter = adaptor_moive_play
                        /* end */
                    } else {

                        progressDialog.dismiss()
                        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    progressDialog.dismiss()
                }
                progressDialog.dismiss()
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                progressDialog.dismiss()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): MutableMap<String?, String?> {
                val params: MutableMap<String?, String?> =
                    HashMap()
                params["movie_id"] = id
                Log.d("tag_pa", "getParams: $params")
                return params
            }

        }
        val requestQueue = Volley.newRequestQueue(activity)
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(
            3000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        stringRequest.retryPolicy = retryPolicy
        requestQueue.add(stringRequest)
    }


}