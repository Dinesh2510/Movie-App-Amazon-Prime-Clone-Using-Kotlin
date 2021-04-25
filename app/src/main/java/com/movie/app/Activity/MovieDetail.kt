package com.movie.app.Activity

import android.app.Dialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.movie.app.Adapter.SectionsPagerAdapter
import com.movie.app.Forms.Login.Companion.SHARED_PREFERENCES_NAME
import com.movie.app.Forms.Login.Companion.USER_ID
import com.movie.app.Fragment.TabsFragment.MoreDetailsFragment
import com.movie.app.Fragment.TabsFragment.RelatedFragment
import com.movie.app.Helper.API
import com.movie.app.Helper.FileDownloader
import com.movie.app.Helper.NetworkCheck
import com.movie.app.Helper.ScreenshotUtils
import com.movie.app.R
import kotlinx.android.synthetic.main.activity_movie_detail.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MovieDetail : AppCompatActivity() {
    private var btn_play: RelativeLayout? = null
    private var spinner_season: Spinner? = null
    private var btn_season: RelativeLayout? = null
    var tab_layout: TabLayout? = null
    //var view_pager: ViewPager2? = null
    var ll_lyt: LinearLayout? = null
    var lyt_download: LinearLayout? = null
    var lyt_AddWish: LinearLayout? = null
    var image: ImageView? = null
    var txt_soon: TextView? = null
    var lyt_shareall: LinearLayout? = null
    private var screenShotFile: File? = null
    var b: Bitmap? = null
    var str_movieId: String? = null
    var str_moviename: String? = null
    var trailer_url: String? = null
    var moive_episode: String? = null
    var str_userid: String? = null
    private var dialog: Dialog? = null
    private var tvYes: TextView? = null
    private var tvNo: TextView? = null
    private var textTitle: TextView? = null
    private var movie_details: TextView? = null
    private val ivProfilePic: ImageView? = null
    private var wishlist_img: ImageView? = null
    private var ivShare: ImageView? = null
    private var str_video_url: String? = null
    private var str_moive_tag: String? = null
    var sharedPreferences: SharedPreferences? = null
    private var fragmentOne: RelatedFragment? = null
    private var fragmentTwo:MoreDetailsFragment ? = null

    var seekBar: AppCompatSeekBar? = null
    var pdfdailog: Dialog? = null
    var lnr_seekbar: LinearLayout? = null
    var textView: TextView? = null

    var MY_PDF = ""
    var PDF_link = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_movie_detail)

        str_movieId = intent.getStringExtra("id")
        str_moive_tag = intent.getStringExtra("moive_tag")


        sharedPreferences =
            getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        str_userid = sharedPreferences?.getString(USER_ID, "")
        tab_layout = findViewById<TabLayout>(R.id.tab_layout)
        lyt_AddWish = findViewById(R.id.lyt_watchlist)
       // view_pager = findViewById<ViewPager2>(R.id.view_pager)
        ll_lyt = findViewById<LinearLayout>(R.id.ll_lyt)
        wishlist_img = findViewById<ImageView>(R.id.wishlist_img)
        lyt_download = findViewById<LinearLayout>(R.id.lyt_download)
        lyt_shareall = findViewById<LinearLayout>(R.id.lyt_shareall)
        textTitle = findViewById(R.id.textTitle)
        txt_soon = findViewById(R.id.txt_soon)
        btn_season = findViewById(R.id.btn_season)
        spinner_season = findViewById(R.id.spinner_season)
        btn_play = findViewById<RelativeLayout>(R.id.btn_play)

        if (moive_episode.equals("")){
            btn_season?.setVisibility(View.GONE)
        }else{
            btn_season?.setVisibility(View.GONE)
        }
        bindWidgetsWithAnEvent();
        setupTabLayout();
        movie_details = findViewById(R.id.movie_details)
        image = findViewById(R.id.image)
        //onSetupTabs();
        if (str_moive_tag.equals("5")) {
            txt_soon?.setVisibility(View.VISIBLE)
            btn_play?.setVisibility(View.GONE)
        }else{
            txt_soon?.setVisibility(View.GONE)
        }
        //setHasOptionsMenu(true);
        CheckWishList()
        GETMOvieData(str_movieId)
        btn_play?.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, VideoPlayerExo::class.java)
            intent.putExtra("url", str_video_url)
            intent.putExtra("tag", "0")
            println("deeplinkingcallbackstr_video_url   :- $str_video_url")

            startActivity(intent)
            //takeScreenshot()
        })


        lyt_shareall?.setOnClickListener(View.OnClickListener {

            takeScreenshot()
        })
        lyt_AddWish?.setOnClickListener(View.OnClickListener {
            AddToWishList()
        });
        lyt_play?.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, VideoPlayerExo::class.java)
            intent.putExtra("url", trailer_url)
            intent.putExtra("tag", "1")
            println("deeplinkingcallbackstr_video_url   :- $str_video_url")

            startActivity(intent)
        });

    }

/*
    @SuppressLint("ResourceAsColor")
    private fun onSetupTabs() {
        setupViewPager(view_pager)
        tab_layout.let {
            view_pager?.let { it1 ->
                if (it != null) {
                    TabLayoutMediator(it, it1,
                        TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                            if (position == 0) {
                                tab.text = "Related"
                            } else if (position == 1) {
                                tab.text = "More details"
                            }
                        }
                    ).attach()
                }
            }
        }
        tab_layout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {


                // on Tab Selected
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

                // on Tab UnSelected
            }

            override fun onTabReselected(tab: TabLayout.Tab) {


                // on Tab ReSelected
            }
        })
    }
*/


    private fun setupViewPager(viewPager: ViewPager2?) {
        val viewPagerAdapter =
            SectionsPagerAdapter(getSupportFragmentManager(), lifecycle)
        viewPagerAdapter.addFragment(MoreDetailsFragment())
        viewPagerAdapter.addFragment(RelatedFragment())
        viewPager?.adapter = viewPagerAdapter
        viewPager?.offscreenPageLimit = 1
        viewPagerAdapter.notifyDataSetChanged()
    }


    /*  fun onPrepareOptionsMenu(menu: Menu) {
          menu.clear()
      }*/


//    fun onDestroyView() {
//        super.onDestroyView()
//        view_pager?.setSaveFromParentEnabled(true)
//        view_pager?.setAdapter(null)
//    }

    private fun takeScreenshot() {
        b = ScreenshotUtils.screenShot(ll_lyt)
        if (b != null) {
            val timeStamp =
                SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val saveFile: File =
                ScreenshotUtils.getMainDirectoryName(this) //get the path to save screenshot
            screenShotFile = ScreenshotUtils.store(
                b,
                API.APP_NMAE.toString() + timeStamp + ".jpg",
                saveFile
            ) //save the screenshot to selected path
            dialogshare()
        } else {

        }
    }

    fun dialogshare() {
        dialog = Dialog(this, android.R.style.Theme_Dialog)
        dialog!!.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.dailog_share)
        tvYes = dialog!!.findViewById(R.id.tvYes) as TextView
        tvNo = dialog!!.findViewById(R.id.tvNo) as TextView
        ivShare = dialog!!.findViewById(R.id.ivShare) as ImageView
        dialog!!.show()
        dialog!!.setCancelable(false)
        ivShare!!.setImageBitmap(b)
        tvNo!!.setOnClickListener(View.OnClickListener {
            dialog!!.dismiss()
            if (screenShotFile != null) {
                screenShotFile!!.delete()
            }
        })
        tvYes!!.setOnClickListener(
            View.OnClickListener {
                shareMedia(b!!)
                dialog!!.dismiss()
            })
    }

    fun shareMedia(mBitmap: Bitmap) {
        try {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/jpeg"
            val bytes = ByteArrayOutputStream()
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                contentResolver,
                mBitmap, "Title", null
            )
            val imageUri = Uri.parse(path)
            share.putExtra(Intent.EXTRA_STREAM, imageUri)
            share.putExtra(
                Intent.EXTRA_TEXT,
                "Hey I'am watching $str_moviename. Check Out Now on Prime Video! https://pixeldev.in/id/$str_movieId"
            )
            startActivity(Intent.createChooser(share, "Share With"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        val `in` = intent
        val data = `in`.data
        val uri = intent.data
        if (uri != null) {
            val para = uri.pathSegments
            val id = para[para.size - 1]
            println("deeplinkingcallbackID   :$id")
            GETMOvieData(id)
        }
        println("deeplinkingcallback   :- $data")
    }

    private fun AddToWishList() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
        progressDialog.show();
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            API.ADD_WISH_DATA,
            Response.Listener { response ->
                Log.d("quick_2", "onResponse: $response")
                try {
                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("message")
                    val code = jsonObject.getString("code")
                    if (code == "200") {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        wishlist_img?.setImageResource(R.drawable.tick);
                        lyt_AddWish?.setOnClickListener(View.OnClickListener {
                            if (NetworkCheck.isConnect(applicationContext)) {
                                RemovedBookMark()
                            } else {
                                Toast.makeText(
                                    this,
                                    "You'r offline!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })

                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
                params["user_id"] = str_userid
                params["movie_id"] = str_movieId
                Log.d("tag_pa", "getParams: $params")
                return params
            }

        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(
            3000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        stringRequest.retryPolicy = retryPolicy
        requestQueue.add(stringRequest)
    }

    private fun CheckWishList() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
        progressDialog.show();
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            API.CHECK_WISHLIST,
            Response.Listener { response ->
                Log.d("quick_2", "onResponse: $response")
                try {
                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("message")
                    val code = jsonObject.getString("code")
                    if (code == "200") {
                        wishlist_img?.setImageResource(R.drawable.tick);
                        lyt_AddWish?.setOnClickListener(View.OnClickListener {
                            if (NetworkCheck.isConnect(applicationContext)) {
                                RemovedBookMark()
                            } else {
                                Toast.makeText(
                                    this,
                                    "You'r offline!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    } else {
                        wishlist_img?.setImageResource(R.drawable.plus);

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
                params["user_id"] = str_userid
                params["movie_id"] = str_movieId
                Log.d("tag_pa", "getParams: $params")
                return params
            }

        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(
            3000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        stringRequest.retryPolicy = retryPolicy
        requestQueue.add(stringRequest)
    }

    private fun GETMOvieData(id: String?) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
        progressDialog.show();
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            API.GetMovieData,
            Response.Listener { response ->
                Log.d("quick_2", "onResponse: $response")
                try {
                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("message")
                    val code = jsonObject.getString("code")
                    if (code == "200") {
                        val jsonArray_popular = jsonObject.getJSONArray("movie_data")
                        val jsonArray_movie_data_list = jsonObject.getJSONArray("movie_data_list")
                        for (k in 0 until jsonArray_popular.length()) {
                            val listpObject = jsonArray_popular.getJSONObject(k)
                            str_moviename = (listpObject.getString("name"))
                            str_video_url = (listpObject.getString("video_url"))
                            trailer_url = (listpObject.getString("trailer_url"))
                            PDF_link = trailer_url.toString();
                            findViewById<View>(R.id.lyt_download).setOnClickListener {

                                val rand = Random()
                                val rand_int1 = rand.nextInt(1000)
                                MY_PDF = rand_int1.toString() + "_BillGenerate.mp4"

                                PDFDialog(PDF_link);
                            }
                            moive_episode = (listpObject.getString("moive_episode"))
                            var str_desc: String? = (listpObject.getString("movie_description"))
                            if (str_desc.equals("")) {
                                movie_details?.text =
                                    "This is demo description if the description is empty than this dummy data is show also the video the app from youtube and it is only use for educational purpose. "

                            } else {
                                movie_details?.text = str_desc

                            }
                            var str_rate: String? =
                                ("IMDb " + listpObject.getString("moive_rating"))
                            rating?.text = str_rate
                            year_mat?.text =
                                (listpObject.getString("moive_year") + " 164 min " + "+16")
                            textTitle?.text = str_moviename
                            val media = API.play_image + (listpObject.getString("banner_url"))
                            image?.let {
                                Glide.with(applicationContext)
                                    .load(media)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .placeholder(R.drawable.cinema)
                                    .into(it)
                            }


                        }

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
        val requestQueue = Volley.newRequestQueue(applicationContext)
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(
            3000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        stringRequest.retryPolicy = retryPolicy
        requestQueue.add(stringRequest)
    }

    private fun RemovedBookMark() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading..")
        progressDialog.show();
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            API.Remove_WISHLIST,
            Response.Listener { response ->
                Log.d("quick_2", "onResponse: $response")
                try {
                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("message")
                    val code = jsonObject.getString("code")
                    if (code == "200") {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                        wishlist_img?.setImageResource(R.drawable.plus);
                        lyt_AddWish?.setOnClickListener(View.OnClickListener {
                            if (NetworkCheck.isConnect(applicationContext)) {
                                AddToWishList()
                            } else {
                                Toast.makeText(
                                    this,
                                    "You'r offline!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    } else {
                        wishlist_img?.setImageResource(R.drawable.tick);

                        progressDialog.dismiss()
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
                params["user_id"] = str_userid
                params["movie_id"] = str_movieId
                Log.d("tag_pa", "getParams: $params")
                return params
            }

        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(
            3000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        stringRequest.retryPolicy = retryPolicy
        requestQueue.add(stringRequest)

    }
    private fun setupTabLayout() {
        fragmentOne =RelatedFragment()
        fragmentTwo = MoreDetailsFragment()
        tab_layout?.addTab(tab_layout!!.newTab().setText("Related"), true)
        tab_layout?.addTab(tab_layout!!.newTab().setText("More Details"))
    }

    private fun bindWidgetsWithAnEvent() {
        tab_layout?.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                setCurrentTabFragment(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setCurrentTabFragment(tabPosition: Int) {
        when (tabPosition) {
            0 -> replaceFragment(fragmentOne)
            1 -> replaceFragment(fragmentTwo)
        }
    }

    fun replaceFragment(fragment: Fragment?) {
        val fm: FragmentManager = supportFragmentManager
        val ft: FragmentTransaction = fm.beginTransaction()
        if (fragment != null) {
            ft.replace(R.id.frame_container, fragment)
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }


    private fun PDFDialog(filePath: String) {
        pdfdailog = Dialog(this)
        pdfdailog?.getWindow()?.requestFeature(Window.FEATURE_NO_TITLE)
        pdfdailog?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val window: Window? = pdfdailog?.getWindow()
        window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        pdfdailog?.setContentView(R.layout.dialog_dow_seekbar)
        lnr_seekbar = pdfdailog?.findViewById<LinearLayout>(R.id.lnr_seekbar)
        //        pdfView = pdfdailog.findViewById(R.id.pdfview);
        textView = pdfdailog?.findViewById<TextView>(R.id.txtcount)
        pdfdailog?.show()
        initSeekBar()
        //        downloadPdf(MY_PDF,view);
        New_DownloadPDF(filePath, MY_PDF)
    }
    private fun New_DownloadPDF(filePath: String, file_name: String) {
        object : AsyncTask<Void?, Int?, Boolean>() {


            private fun DownloadPDF(): Boolean {
                val file = File(getExternalFilesDir(null).toString() + "/PixelDev_Video/" + MY_PDF)
                if (file.exists()) return true
                val extStorageDirectory = getExternalFilesDir(null).toString()
                val folder = File(extStorageDirectory, "PixelDev_Video")
                folder.mkdir()
                val pdfFile = File(folder, file_name)
                try {
                    pdfFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return if (FileDownloader.downloadFile(filePath, pdfFile)) {
                    true
                } else {
                    false
                }
            }

            override fun onProgressUpdate(vararg values: Int?) {
                super.onProgressUpdate(*values)
                seekBar!!.progress = values[0]!!
            }
            override fun onPostExecute(aBoolean: Boolean) {
                super.onPostExecute(aBoolean)
                if (aBoolean) {
                    openPDF(file_name)
                } else {
                    Toast.makeText(applicationContext,
                        "Unable to donwload this Pdf",
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun doInBackground(vararg params: Void?): Boolean {
                return DownloadPDF()
            }
        }.execute()
    }


    private fun openPDF(fileName: String) {
        try {
            OpenVideoFILES()
           /* val snackbar = Snackbar
                .make(view, "" + fileName, Snackbar.LENGTH_INDEFINITE)
                .setAction("OPEN PDF") { OpenPDFFILES() }
            snackbar.show()*/
            pdfdailog!!.dismiss()
            lnr_seekbar!!.visibility = View.GONE
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun OpenVideoFILES() {
        val path: Uri
        val pdfFile =
            File(getExternalFilesDir(null).toString() + "/PixelDev_Video/" + MY_PDF)
        path = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) FileProvider.getUriForFile(
            this,
            applicationContext.packageName + ".provider",
            pdfFile) else Uri.fromFile(pdfFile)
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(path, "video/mp4")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this,
                "No Application available to view PDF",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun initSeekBar() {
        seekBar = pdfdailog!!.findViewById(R.id.seekbar)
        seekBar?.getProgressDrawable()?.setColorFilter(Color.RED, PorterDuff.Mode.SRC)
        seekBar?.getThumb()?.setColorFilter(Color.RED, PorterDuff.Mode.SRC)
        seekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                val `val` = progress * (seekBar.width - 3 * seekBar.thumbOffset) / seekBar.max
                textView!!.text = "" + progress
                textView!!.x = seekBar.x + `val` + seekBar.thumbOffset / 2
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}