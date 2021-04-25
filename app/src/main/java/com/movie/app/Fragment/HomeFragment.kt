package com.movie.app.Fragment

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.movie.app.Activity.AutoPlayVideo
import com.movie.app.Activity.MovieDetail
import com.movie.app.Activity.ShortVideo
import com.movie.app.Adapter.*
import com.movie.app.Helper.*
import com.movie.app.Model.*
import com.movie.app.R
import com.movie.app.Ui.ExoPlayerRecyclerView
import kotlinx.android.synthetic.main.fragment_home2.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap
import kotlin.jvm.Throws


class HomeFragment : Fragment() {

    private var callbackCall: Call<CallbackSliderImage>? = null
    private var runnableCode: Runnable? = null
    private var features_news_title: TextView? = null
    private var loadingPanel: RelativeLayout? = null
    private val handler = Handler()
    private var layout_dots: LinearLayout? = null
    private var btn_video: Button? = null
    private var btn_pdf: Button? = null
    private var bt_previous: ImageButton? = null
    private var bt_next: ImageButton? = null
    var new_product_slider: LinearLayout? = null
    private var lyt_main_content: View? = null
    var viewPager: ViewPager? = null

    var recyclerView_subj: RecyclerView? = null
    var recyclerView_cVideo: RecyclerView? = null
    var recyclerView_popular: RecyclerView? = null
    var recyclerView_studio: RecyclerView? = null
    var recyclerView_soon: RecyclerView? = null
    var recyclerView_csoon: RecyclerView? = null
    var recyclerView_og: RecyclerView? = null
    private lateinit var langaugeArrayList: ArrayList<LanguageModel>
    private lateinit var genreModel: ArrayList<GenreModel>
    private lateinit var movieModel: ArrayList<MovieModel>
    private lateinit var movieModel_coming: ArrayList<MovieModel>
    private lateinit var movieModel_comingsoon: ArrayList<MovieModel>
    private lateinit var movieModel_og: ArrayList<MovieModel>
    private lateinit var studioModel: ArrayList<StudioModel>
    private lateinit var mediaObjectList: ArrayList<MovieModel>
    private lateinit var adapter: AdapterImageSlider
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    // private lateinit var languageAdapter: LanguageAdapter
    var mRecyclerView: ExoPlayerRecyclerView? = null

    private var mAdapter: MediaRecyclerAdapter? = null
    private var firstTime = true

    //http://pixeldev.in/webservices/movie_app/GetAllHomeList.php
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home2, container, false)


        lyt_main_content = view.findViewById(R.id.lyt_cart)
        features_news_title = view.findViewById(R.id.featured_news_title)
        layout_dots = view.findViewById(R.id.layout_dots)
        viewPager = view.findViewById(R.id.pager)
        bt_previous = view.findViewById(R.id.bt_previous)
        bt_next = view.findViewById(R.id.bt_next)
        btn_video = view.findViewById(R.id.btn_video)
        btn_pdf = view.findViewById(R.id.btn_pdf)
        loadingPanel = view.findViewById(R.id.loadingPanel)

        langaugeArrayList = java.util.ArrayList<LanguageModel>()
        genreModel = java.util.ArrayList<GenreModel>()
        movieModel = java.util.ArrayList<MovieModel>()
        movieModel_coming = java.util.ArrayList<MovieModel>()
        studioModel = java.util.ArrayList<StudioModel>()
        mediaObjectList = java.util.ArrayList<MovieModel>()
        movieModel_comingsoon = java.util.ArrayList<MovieModel>()
        movieModel_og = java.util.ArrayList<MovieModel>()

        recyclerView_subj = view.findViewById(R.id.recyclerView_video_logs)
        recyclerView_cVideo = view.findViewById(R.id.recyclerView_cVideo)
        recyclerView_popular = view.findViewById(R.id.recyclerView_popular)
        recyclerView_studio = view.findViewById(R.id.recyclerView_studio)
        mRecyclerView = view.findViewById(R.id.exoPlayerRecyclerView)
        recyclerView_soon = view.findViewById(R.id.recyclerView_soon)
        recyclerView_csoon = view.findViewById(R.id.recyclerView_coming_soon)
        recyclerView_og = view.findViewById(R.id.recyclerView_og)


        recyclerView_og?.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        recyclerView_csoon?.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        mRecyclerView?.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        )
        recyclerView_soon?.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        recyclerView_studio?.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        recyclerView_popular?.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        recyclerView_cVideo?.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        recyclerView_subj?.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        val dividerDrawable =
            context?.let { ContextCompat.getDrawable(it, R.drawable.divider_drawable) }
        mRecyclerView?.addItemDecoration(DividerItemDecoration(dividerDrawable))
        mRecyclerView?.setItemAnimator(DefaultItemAnimator())

        new_product_slider = view.findViewById<LinearLayout>(R.id.new_product_slider)
        adapter = activity?.let {
            AdapterImageSlider(
                it,
                ArrayList<NewsInfo>()
            )
        }!!

        bt_previous!!.setOnClickListener({ prevAction() })
        bt_next!!.setOnClickListener { nextAction() }

        swipeRefreshLayout = view.findViewById(R.id.swipe)
        swipeRefreshLayout.setOnRefreshListener {

            if (Tools.isNetworkAvailable(activity)) {
                getSubjectList()
                ListFeaturedNews()
                getWishList()
            } else {
                Toast.makeText(activity, "Internet Connection Not Available", Toast.LENGTH_SHORT)
                    .show()
                loadingPanel?.setVisibility(View.GONE)

            }
            Handler().postDelayed(Runnable {
                swipeRefreshLayout.isRefreshing = false
            }, 4000)
        }

        ListFeaturedNews()
//        mRecyclerView?.setMediaObjects(mediaObjectList)
//        mAdapter = MediaRecyclerAdapter(mediaObjectList, initGlide())
//        mRecyclerView?.setAdapter(mAdapter)
//
//        if (firstTime) {
//            Handler(Looper.getMainLooper()).post { mRecyclerView?.playVideo(false) }
//            firstTime = false
//        }
//        /*AutoPlay Video end */

        btn_video?.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, AutoPlayVideo::class.java)
            startActivity(intent)
        });
        btn_pdf?.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, ShortVideo::class.java)
            startActivity(intent)
        });

        return view


    }

    override fun onResume() {
        super.onResume()
        if (Tools.isNetworkAvailable(activity)) {
            getSubjectList()
           // ListFeaturedNews()
            getWishList()
        } else {
            Toast.makeText(activity, "Internet Connection Not Available", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun initGlide(): RequestManager? {
        val options = RequestOptions()
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    private fun getSubjectList() {
        val url = "http://pixeldev.in/webservices/movie_app/GetAllHomeList.php"

        val progressDialog = ProgressDialog(activity, R.style.AppCompatAlertDialogStyle)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
        //  progressDialog.show()
        loadingPanel?.setVisibility(View.VISIBLE)

        langaugeArrayList.clear()
        genreModel.clear()
        movieModel.clear()
        studioModel.clear()
        movieModel_comingsoon.clear()
        movieModel_og.clear()
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, url,
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
                            val jsonArray_moive_play = jsonObject.getJSONArray("moive_coming")
                            val jsonArray_movie_og = jsonObject.getJSONArray("movie_og")

                            Log.d("jsonArray_langauge", "" + jsonArray_moive_play)

                            /*Language LIST START*/
                            for (i in 0 until jsonArray_langauge.length()) {
                                val listObject = jsonArray_langauge.getJSONObject(i)
                                val model = LanguageModel()
                                model.lang_name = (listObject.getString("lang_name"))
                                model.lang_img = (listObject.getString("lang_img"))
                                model.lang_id = (listObject.getString("lang_id"))
                                model.banner_img = (listObject.getString("lang_banner"))
                                langaugeArrayList.add(model)
                            }
                            var adaptor = activity?.let { LanguageAdapter(langaugeArrayList, it) }
                            recyclerView_subj?.adapter = adaptor

                            /*Genre LIST START*/
                            for (j in 0 until jsonArray_genre.length()) {
                                val listgObject = jsonArray_genre.getJSONObject(j)
                                val model = GenreModel()
                                model.genre_id = (listgObject.getString("genre_id"))
                                model.genre_name = (listgObject.getString("genre_name"))
                                model.genre_img = (listgObject.getString("genre_img"))
                                genreModel.add(model)
                            }
                            var adaptor_genre = activity?.let { GenreAdapter(genreModel, it) }
                            recyclerView_cVideo?.adapter = adaptor_genre
                            3
                            /*Popular LIST START*/
                            for (k in 0 until jsonArray_popular.length()) {
                                val listpObject = jsonArray_popular.getJSONObject(k)
                                val model = MovieModel()
                                model.thumbnail = (listpObject.getString("thumbnail"))
                                model.name = (listpObject.getString("name"))
                                model.moive_id = (listpObject.getString("moive_id"))
                                model.moive_tag = (listpObject.getString("moive_tag"))
                                model.trailer_url = (listpObject.getString("trailer_url"))
                                movieModel.add(model)
                            }
                            var adaptor_movie = activity?.let { PopularAdapter(movieModel, it) }
                            recyclerView_popular?.adapter = adaptor_movie

                            /*studio LIST START*/
                            for (l in 0 until jsonArray_studio.length()) {
                                val listshortObject = jsonArray_studio.getJSONObject(l)
                                val model = StudioModel()
                                model.studio_id = (listshortObject.getString("studio_id"))
                                model.studio_name = (listshortObject.getString("studio_name"))
                                model.studio_img = (listshortObject.getString("studio_img"))
                                studioModel.add(model)
                            }
                            var adaptor_movie_studio =
                                activity?.let { MovieStudioAdapter(studioModel, it) }
                            recyclerView_studio?.adapter = adaptor_movie_studio

                            //coming soon START*//*
                            for (m in 0 until jsonArray_moive_play.length()) {
                                val listplayObject = jsonArray_moive_play.getJSONObject(m)
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
                                    "getSubjectList: " + (listplayObject.getString("video_url"))
                                )
                                movieModel_comingsoon.add(model)
                            }
                            var adaptor_moive_play =
                                activity?.let { ComingAdapter(movieModel_comingsoon, it) }
                            recyclerView_coming_soon?.adapter = adaptor_moive_play
                            /* end */
                            //recyclerView_og START*//*
                            for (p in 0 until jsonArray_movie_og.length()) {
                                val listplayObject = jsonArray_movie_og.getJSONObject(p)
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
                                    "getSubjectList: " + (listplayObject.getString("video_url"))
                                )
                                movieModel_og.add(model)
                            }
                            var adaptor_moive_og =
                                activity?.let { ComingAdapter(movieModel_og, it) }
                            recyclerView_og?.adapter = adaptor_moive_og
                            /* end */


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
                    loadingPanel?.setVisibility(View.GONE)

                }, com.android.volley.Response.ErrorListener {
                    progressDialog.dismiss()
                    loadingPanel?.setVisibility(View.GONE)

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

    private fun getWishList() {

        val progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
        // progressDialog.show()
        loadingPanel?.setVisibility(View.VISIBLE)

        movieModel_coming.clear()
        val stringRequest: StringRequest =
            object : StringRequest(
                Method.POST, API.GetAllMoviesList_DATA,
                com.android.volley.Response.Listener { response ->
                    val code: String
                    val message: String
                    try {
                        val jsonObject = JSONObject(response)
                        Log.d("response_wish", response)
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
                                movieModel_coming.add(model)
                            }
                            val adaptor_movie_coming =
                                activity?.let { ComingAdapter(movieModel_coming, it) }
                            recyclerView_soon?.adapter = adaptor_movie_coming


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
                    loadingPanel?.setVisibility(View.GONE)

                }, com.android.volley.Response.ErrorListener {
                    progressDialog.dismiss()
                    loadingPanel?.setVisibility(View.GONE)

                    Toast.makeText(
                        activity,
                        "Something went wrong",
                        Toast.LENGTH_LONG
                    ).show()
                }) {
/*
                @Throws(AuthFailureError::class)
                override fun getParams(): MutableMap<String?, String?> {
                    val params: MutableMap<String?, String?> =
                        HashMap()
                    Log.d("tag_pa", "getParams: $params")
                    return params
                }
*/

            }
        Volley.newRequestQueue(activity).add(stringRequest)
    }

    private fun ListFeaturedNews() {

        Log.d("dasasas", "ListFeaturedNews: ")
        val api: API = RestAdapter.createAPI()
        callbackCall = api.getSliderImage()
        Log.d("TAG_resp_im", "onResponse: " + api.getSliderImage())
        callbackCall?.enqueue(object :
            Callback<CallbackSliderImage?> {
            override fun onResponse(
                call: Call<CallbackSliderImage?>,
                response: Response<CallbackSliderImage?>
            ) {
                val resp = response.body()
                if (resp != null && resp.status.equals("Success")) {
                    Log.d("TAG_resp", "onResponse: " + resp.banner_info.toString())
                    displayResultData(resp.banner_info)
                } else {
                    onFailRequest()
                }
            }

            override fun onFailure(
                call: Call<CallbackSliderImage?>,
                t: Throwable
            ) {
                if (!call.isCanceled) onFailRequest()
            }
        })
    }

    private fun displayResultData(items: List<NewsInfo>) {
        adapter!!.setItems(items)
        viewPager!!.adapter = adapter
        val params = viewPager!!.layoutParams
        params.height = Tools.getFeaturedNewsImageHeight(activity)
        viewPager!!.layoutParams = params

        // displaying selected image first
        viewPager!!.currentItem = 0
        features_news_title!!.text = adapter.getItem(0).title
        addBottomDots(layout_dots!!, adapter.count, 0)
        Log.d("TAG_display", "displayResultData: " + adapter.getItem(0).image)
        viewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                pos: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(pos: Int) {
                val cur = adapter!!.getItem(pos)
                features_news_title!!.text = cur.title
                addBottomDots(layout_dots!!, adapter!!.count, pos)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        startAutoSlider(adapter!!.count)

        adapter!!.setOnItemClickListener(object : AdapterImageSlider.OnItemClickListener {
            override fun onItemClick(view: View?, obj: NewsInfo?) {
              /*  val i = Intent(activity, NewsDetails::class.java)
                i.putExtra("title", obj!!.title)
                i.putExtra("brief_content", obj.brief_content)
                i.putExtra("image", obj.image)
                i.putExtra("status", obj.status)
                i.putExtra("created_at", obj.created_at)
                i.putExtra("link", obj.link)
                i.putExtra("price_disc", "0")
                i.putExtra("price", " ")
                startActivity(i)*/
            }
        })
        lyt_main_content!!.visibility = View.VISIBLE
    }

    private fun onFailRequest() {
        if (NetworkCheck.isConnect(activity)) {
            Toast.makeText(activity, "Something wents wrong!", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(activity, "No internet! ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startAutoSlider(count: Int) {
        runnableCode = Runnable {
            var pos = viewPager!!.currentItem
            pos = pos + 1
            if (pos >= count) pos = 0
            viewPager!!.currentItem = pos
            handler.postDelayed(runnableCode!!, 3000)
        }
        handler.postDelayed(runnableCode!!, 3000)
    }

    override fun onDestroy() {
        if (mRecyclerView != null) {
            mRecyclerView!!.releasePlayer()
        }
        if (runnableCode != null) handler.removeCallbacks(runnableCode!!)
        super.onDestroy()
    }


    private fun addBottomDots(
        layout_dots: LinearLayout,
        size: Int,
        current: Int
    ) {
        val dots =
            arrayOfNulls<ImageView>(size)
        layout_dots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = ImageView(activity)
            val width_height = 10
            val params =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams(width_height, width_height))
            params.setMargins(10, 10, 10, 10)
            dots[i]!!.layoutParams = params
            dots[i]!!.setImageResource(R.drawable.shape_circle)
            this.activity?.let {
                ContextCompat.getColor(
                    it,
                    R.color.darkOverlaySoft
                )
            }?.let {
                dots[i]!!.setColorFilter(
                    it
                )
            }
            layout_dots.addView(dots[i])
        }
        if (dots.size > 0) {
            activity?.let {
                ContextCompat.getColor(
                    it,
                    R.color.colorPrimaryLight
                )
            }?.let {
                dots[current]!!.setColorFilter(
                    it
                )
            }
        }
    }


    private fun prevAction() {
        var pos = viewPager!!.currentItem
        pos = pos - 1
        if (pos < 0) pos = adapter!!.count
        viewPager!!.currentItem = pos
    }

    private fun nextAction() {
        var pos = viewPager!!.currentItem
        pos = pos + 1
        if (pos >= adapter!!.count) pos = 0
        viewPager!!.currentItem = pos
    }
}