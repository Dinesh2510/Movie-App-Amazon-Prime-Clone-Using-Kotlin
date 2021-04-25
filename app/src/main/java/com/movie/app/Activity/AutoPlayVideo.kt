package com.movie.app.Activity

import android.app.ProgressDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.movie.app.Adapter.MediaRecyclerAdapter
import com.movie.app.Model.MovieModel
import com.movie.app.R
import com.movie.app.Ui.ExoPlayerRecyclerView
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AutoPlayVideo : AppCompatActivity() {

    var mRecyclerView: ExoPlayerRecyclerView? = null

    private val mediaObjectList: ArrayList<MovieModel> =
        ArrayList<MovieModel>()
    private var mAdapter: MediaRecyclerAdapter? = null
    private var firstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_play_video)
        initView()

        //prepareVideoList()
        getSubjectList()
        //set data object

        //set data object
        mRecyclerView?.setMediaObjects(mediaObjectList)
        mAdapter = MediaRecyclerAdapter(mediaObjectList, initGlide())

        //Set Adapter

        //Set Adapter
        mRecyclerView?.setAdapter(mAdapter)

        if (firstTime) {
            Handler(Looper.getMainLooper()).post { mRecyclerView?.playVideo(false) }
            firstTime = false
        }

    }

    private fun initView() {
        mRecyclerView = findViewById(R.id.exoPlayerRecyclerView)
        mRecyclerView?.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        val dividerDrawable: Drawable? =
            ContextCompat.getDrawable(applicationContext, R.drawable.divider_drawable)
        //mRecyclerView?.addItemDecoration(DividerItemDecoration(dividerDrawable))
        mRecyclerView?.setItemAnimator(DefaultItemAnimator())
    }

    private fun initGlide(): RequestManager? {
        val options = RequestOptions()
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    override fun onDestroy() {
        if (mRecyclerView != null) {
            mRecyclerView?.releasePlayer()
        }
        super.onDestroy()
    }

    private fun prepareVideoList() {
        val mediaObject = MovieModel()
        mediaObject.moive_id = 1.toString()
        mediaObject.name = (
                "Do you think the concept of marriage will no longer exist in the future?"
                )
        mediaObject.moive_banner = (
                "endgame.png"
                )
        mediaObject.video_url = ("movie_video/endgame.mp4")
        val mediaObject2 = MovieModel()
        mediaObject2.name = (
                "If my future husband doesn't cook food as good as my mother should I scold him?"
                )
        mediaObject2.moive_banner = (
                "endgame.png"
                )
        mediaObject2.video_url = ("movie_video/endgame.mp4")
        val mediaObject3 = MovieModel()
        mediaObject3.name = ("Give your opinion about the Ayodhya temple controversy.")
        mediaObject3.moive_banner = (
                "endgame.png"
                )
        mediaObject3.video_url = ("movie_video/endgame.mp4")
        val mediaObject4 = MovieModel()
        mediaObject4.name = ("When did kama founders find sex offensive to Indian traditions")
        mediaObject4.moive_banner = (
                "endgame.png"
                )
        mediaObject4.video_url = ("movie_video/endgame.mp4")
        val mediaObject5 = MovieModel()
        mediaObject5.name = ("When did you last cry in front of someone?")
        mediaObject5.moive_banner = (
                "endgame.png"
                )
        mediaObject5.video_url = ("movie_video/endgame.mp4")
        mediaObjectList.add(mediaObject)
        mediaObjectList.add(mediaObject2)
        mediaObjectList.add(mediaObject3)
        mediaObjectList.add(mediaObject4)
        mediaObjectList.add(mediaObject5)
    }

    private fun getSubjectList() {
        val url = "http://pixeldev.in/webservices/movie_app/GetAllHomeList.php"

        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
        progressDialog.show()


        val stringRequest: StringRequest =
            object : StringRequest(
                Request.Method.POST, url,
                com.android.volley.Response.Listener { response ->
                    val code: String
                    val message: String
                    try {
                        val jsonObject = JSONObject(response)
                        Log.d("response", response)
                        code = jsonObject.getString("code")
                        message = jsonObject.getString("status")
                        if (code == "200") {

                            val jsonArray_moive_play = jsonObject.getJSONArray("moive_play")

                            Log.d("jsonArray_langauge", "" + jsonArray_moive_play)

                            /*studio AutoPlay START*/
                            for (m in 0 until jsonArray_moive_play.length()) {
                                val listplayObject = jsonArray_moive_play.getJSONObject(m)
                                val model = MovieModel()
                                model.thumbnail = (listplayObject.getString("thumbnail"))
                                model.name = (listplayObject.getString("name"))
                                model.moive_banner = (listplayObject.getString("moive_banner"))
                                model.video_url = (listplayObject.getString("video_url"))
                                Log.d(
                                    "Tag_boow",
                                    "getSubjectList: " + (listplayObject.getString("video_url"))
                                )
                                mediaObjectList.add(model)
                            }
                            var adaptor_moive_play =
                                applicationContext?.let {
                                    MediaRecyclerAdapter(
                                        mediaObjectList,
                                        initGlide()
                                    )
                                }
                            mRecyclerView?.adapter = adaptor_moive_play

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
        Volley.newRequestQueue(applicationContext).add(stringRequest)
    }

}