package com.movie.app.Fragment.TabsFragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.movie.app.Activity.AutoPlayVideo
import com.movie.app.Activity.ShortVideo
import com.movie.app.Adapter.AdapterImageSlider
import com.movie.app.Adapter.FindAdapters.FindGenreAdapter
import com.movie.app.Adapter.FindAdapters.FindLangAdapter
import com.movie.app.Adapter.GenreAdapter
import com.movie.app.Adapter.LanguageAdapter
import com.movie.app.Adapter.MediaRecyclerAdapter
import com.movie.app.Helper.API
import com.movie.app.Helper.CallbackSliderImage
import com.movie.app.Helper.DividerItemDecoration
import com.movie.app.Helper.Tools
import com.movie.app.Model.*
import com.movie.app.R
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.lang.reflect.Method


class FindFragment : Fragment() {

    var recyclerView_subj: RecyclerView? = null
    var recyclerView_cVideo: RecyclerView? = null
    private lateinit var langaugeArrayList: ArrayList<LanguageModel>
    private lateinit var genreModel: ArrayList<GenreModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_find, container, false)

        langaugeArrayList = java.util.ArrayList<LanguageModel>()
        genreModel = java.util.ArrayList<GenreModel>()


        recyclerView_subj = view.findViewById(R.id.recyclerView_video_logs)
        recyclerView_cVideo = view.findViewById(R.id.recyclerView_cVideo)

        recyclerView_cVideo?.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        recyclerView_subj?.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        )


        if (Tools.isNetworkAvailable(activity)) {
            getSubjectList()
        } else {
            Toast.makeText(activity, "Internet Connection Not Available", Toast.LENGTH_SHORT)
                .show()
        }



        return view
    }

    private fun getSubjectList() {
        val url = "http://pixeldev.in/webservices/movie_app/GetAllHomeList.php"

        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
        progressDialog.show()

        langaugeArrayList.clear()
        genreModel.clear()

        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, API.HOME_DATA,
                com.android.volley.Response.Listener { response ->
                    val code: String
                    val message: String
                    try {
                        val jsonObject = JSONObject(response)
                        Log.d("response", response)
                        code = jsonObject.getString("code")
                        message = jsonObject.getString("status")
                        if (code == "200") {
                            val jsonArray_banner = jsonObject.getJSONArray("banner")
                            val jsonArray_langauge = jsonObject.getJSONArray("langauge")
                            val jsonArray_genre = jsonObject.getJSONArray("genre")
                            val jsonArray_popular = jsonObject.getJSONArray("popular")
                            val jsonArray_studio = jsonObject.getJSONArray("studio")
                            val jsonArray_moive_play = jsonObject.getJSONArray("moive_play")

                            Log.d("jsonArray_langauge", "" + jsonArray_langauge)

                            /*Language LIST START*/
                            for (i in 0 until jsonArray_langauge.length()) {
                                val listObject = jsonArray_langauge.getJSONObject(i)
                                val model = LanguageModel()
                                model.lang_name = (listObject.getString("lang_name"))
                                model.lang_img = (listObject.getString("lang_img"))
                                model.lang_id = (listObject.getString("lang_id"))
                                langaugeArrayList.add(model)
                            }
                            var adaptor = activity?.let { FindLangAdapter(langaugeArrayList, it) }
                            recyclerView_subj?.adapter = adaptor

                            /*Genre LIST START*/
                            for (j in 0 until jsonArray_genre.length()) {
                                val listgObject = jsonArray_genre.getJSONObject(j)
                                val model = GenreModel()
                                model.genre_id = (listgObject.getString("genre_id"))
                                model.genre_name = (listgObject.getString("genre_name"))
                                genreModel.add(model)
                            }
                            var adaptor_genre = activity?.let { FindGenreAdapter(genreModel, it) }
                            recyclerView_cVideo?.adapter = adaptor_genre
                            3


                        } else {
                            Toast.makeText(
                                activity,
                                "" + message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {

                        Toast.makeText(
                            activity,
                            "" + e.printStackTrace().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    progressDialog.dismiss()
                }, com.android.volley.Response.ErrorListener {
                    progressDialog.dismiss()
                    Toast.makeText(
                        activity,
                        "Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }) {
/*
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> =
                        HashMap()
                   */
/* params["class_id"] =
                        PrefMnger.getString(this@SubjectListActivity, PrefMnger.CLASSID)*//*

                    return params
                }
*/

            }
        Volley.newRequestQueue(activity).add(stringRequest)
    }

}