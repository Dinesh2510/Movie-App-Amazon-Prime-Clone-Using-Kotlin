package com.movie.app.Activity

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.navigation.NavigationView
import com.movie.app.Activity.AppLock.NumLockScreen
import com.movie.app.Forms.Login
import com.movie.app.Forms.Login.Companion.FNAME
import com.movie.app.Forms.Login.Companion.LNAME
import com.movie.app.Forms.Login.Companion.SHARED_PREFERENCES_NAME
import com.movie.app.Fragment.DownloadFragment
import com.movie.app.Fragment.HomeFragment
import com.movie.app.Fragment.MyStuffFragment
import com.movie.app.Fragment.TabsFragment.FindFragment
import com.movie.app.Helper.Utils
import com.movie.app.NoteApp.MainActivity
import com.movie.app.R
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    GoogleApiClient.OnConnectionFailedListener {
    internal lateinit var toolbar: Toolbar
    private var content_frame: FrameLayout? = null
    private var googleApiClient: GoogleApiClient? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var sharedPreferences: SharedPreferences? = null
    var str_fname: String? =null
    var str_lname: String? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        str_fname = sharedPreferences?.getString(FNAME, "")
        str_lname = sharedPreferences?.getString(LNAME, "")
        FacebookSdk.sdkInitialize(applicationContext)
        //Google Login
        //Google Login
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this, gso)

        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        initUI()
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            if (item.itemId == android.R.id.home) {
                finish()
            } else {
                when (item.itemId) {
                    R.id.searchMenu -> {
                        val fragment = HomeFragment()
                        addFragment(fragment)
                    }
                    R.id.favouriteMenu -> {
                        val fragment = FindFragment()
                        addFragment(fragment)
                    }
                    R.id.saveMenu -> {
                        val fragment = DownloadFragment()
                        addFragment(fragment)
                    }

                    R.id.profileMenu -> {
                        val fragment = MyStuffFragment()
                        addFragment(fragment)
                    }
                }
            }

            true
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            val i = Intent(this, ShortVideo::class.java)
            startActivity(i)
        } else if (id == R.id.nav_gallery) {
            val i = Intent(this, NumLockScreen::class.java)
            startActivity(i)
        }else if (id == R.id.nav_note) {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        } else if (id == R.id.nav_Videos) {
            val i = Intent(this, GallaryVideoList::class.java)
            startActivity(i)
        } else if (id == R.id.nav_slideshow) {
            onLoadSubscribeDialog()
            val i = Intent(this, UserLocation::class.java)
            startActivity(i)
        } else if (id == R.id.nav_manage) {
            onClearCache()
        } else if (id == R.id.nav_about) {
            AboutUs()
        } else if (id == R.id.nav_share) {
            ShareUs()
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody =
                "Get the Amazon prime video clone app code and other android related source code at https://github.com/Dinesh2510"//url or content

            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Open Source")
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        } else if (id == R.id.nav_send) {
            // PrivacyPolicy()
            showTermServicesDialog()
            /* val i = Intent(this, VerificationScreen::class.java)
             startActivity(i)*/
        } else if (id == R.id.nav_setting) {
            val i = Intent(this, Setting::class.java)
            startActivity(i)
        } else if (id == R.id.nav_logout) {
            DialogForLogout()
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    class ShareUs {

    }

    private fun AboutUs() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before

        dialog.setContentView(R.layout.dialog_about)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val imageView: ImageView = dialog.findViewById(R.id.logo_aboutus)

        //Tools.loadMainLogo(imageView)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT


        val tvVersion = dialog.findViewById<TextView>(R.id.tv_version)

        tvVersion.text = String.format("Version 1.0")


        dialog.findViewById<View>(R.id.bt_getcode)
            .setOnClickListener { v15: View? ->
                if (false) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com")))
                } else {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("")
                        )
                    )
                }
            }

        dialog.findViewById<View>(R.id.bt_close)
            .setOnClickListener { v14: View? -> dialog.dismiss() }

        dialog.findViewById<View>(R.id.app_url)
            .setOnClickListener { v13: View? ->
                if (false) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("")))
                } else {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com")
                        )
                    )
                }
            }

        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun PrivacyPolicy() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before

        dialog.setContentView(R.layout.dialog_gdpr_basic)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow()?.getAttributes())
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT

        val reportMovieName: TextView = dialog.findViewById(R.id.tv_content)
        reportMovieName.setText(getString(R.string.pcpc))

//        dialog.findViewById(R.id.bt_accept).setOnClickListener({ v1 -> dialog.dismiss() })
//
//         dialog?.findViewById(R.id.bt_decline).setOnClickListener({ v12 -> dialog.dismiss() })


        dialog.show()
        dialog.getWindow()?.setAttributes(lp)
    }

    private fun initUI() {
        initToolbar()

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val navUsername = headerView.findViewById(R.id.tvHeaderName) as TextView
        navUsername.text = ""+str_fname + " "+ str_lname

        content_frame = findViewById<FrameLayout>(R.id.content_frame)

        navigationView.setNavigationItemSelectedListener(this)
        if (Utils.isRTL) {
            navigationView.textDirection = View.TEXT_DIRECTION_RTL
        } else {
            navigationView.textDirection = View.TEXT_DIRECTION_LTR
        }
        val fragment = HomeFragment()
        addFragment(fragment)
    }
    //region Init Toolbar

    private fun initToolbar() {

        toolbar = findViewById(R.id.toolbar)

        toolbar.setNavigationIcon(R.drawable.baseline_menu_black_24)

        if (toolbar.navigationIcon != null) {
            toolbar.navigationIcon?.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.md_white_1000
                ), PorterDuff.Mode.SRC_ATOP
            )
        }

        toolbar.title = "Basic Navigation Drawer"

        try {
            toolbar.setTitleTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.md_white_1000
                )
            )
        } catch (e: Exception) {
            Log.e("TEAMPS", "Can't set color.")
        }

        try {
            setSupportActionBar(toolbar)
        } catch (e: Exception) {
            Log.e("TEAMPS", "Error in set support action bar.")
        }

        try {
            if (supportActionBar != null) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        } catch (e: Exception) {
            Log.e("TEAMPS", "Error in set display home as up enabled.")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.design_bottom_sheet_slide_in,
                R.anim.design_bottom_sheet_slide_out
            )
            .replace(R.id.content_frame, fragment, fragment.javaClass.getSimpleName())
            //  .addToBackStack(fragment.javaClass.getSimpleName())
            .commit()
    }

    fun onClearCache() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.clear_cache)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.findViewById<View>(R.id.bt_getcode)
            .setOnClickListener { x: View? ->
                deleteCache(this)
                Toast.makeText(this, "The App cache has been cleared !", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }
        dialog.findViewById<View>(R.id.bt_close)
            .setOnClickListener { x: View? -> dialog.dismiss() }
        dialog.show()
        dialog.window!!.attributes = lp

    }

    fun deleteCache(context: Context) {
        try {
            val dir: File = context.getCacheDir()
            deleteFile(dir)
        } catch (e: java.lang.Exception) {
            e.message?.let { Log.d("Error Deleting : %s", it) }
        }
    }

    fun deleteFile(file: File?): Boolean {
        var deletedAll = true
        if (file != null) {
            if (file.isDirectory()) {
                val children: Array<kotlin.String> = file.list()
                for (child in children) {
                    deletedAll = deleteFile(File(file, child)) && deletedAll
                }
            } else {
                deletedAll = file.delete()
            }
        }
        return deletedAll
    }

    private fun onLoadSubscribeDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_subscribe)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.gravity = Gravity.BOTTOM
        lp.width = MATCH_PARENT
        lp.height = MATCH_PARENT
        dialog.findViewById<View>(R.id.text_view_go_pro)
            .setOnClickListener { v: View? ->
                startActivity(Intent(this, com.movie.app.PaymentGateway.MainActivity::class.java))
                dialog.dismiss()
            }
        dialog.findViewById<View>(R.id.view_watch_ads_to_play)
            .setOnClickListener { v: View? ->

                dialog.dismiss()
            }
        dialog.findViewById<View>(R.id.bt_close)
            .setOnClickListener { v: View? -> dialog.dismiss() }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun DialogForLogout() {
        val dialog =
            AlertDialog.Builder(this)
        dialog.setTitle(R.string.confirmation)
        dialog.setMessage(R.string.logout_confirmation_text)
        dialog.setNegativeButton(R.string.CANCEL, null)
        dialog.setPositiveButton(R.string.YES,
            DialogInterface.OnClickListener { dialogInterface, i ->
                signOut()
                LoginManager.getInstance().logOut()

                ClearAllSherf()
            })
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun ClearAllSherf() {
        val sharedpreferences = applicationContext.getSharedPreferences(
            SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sharedpreferences.edit()
        editor.clear()
        editor.apply()
        val intent = Intent(applicationContext, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun showTermServicesDialog() {
        Toast.makeText(this, "Loading..", Toast.LENGTH_SHORT).show()
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_term_of_services)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        val mywebview = dialog.findViewById<View>(R.id.webView) as WebView
        //mywebview.loadUrl("file:///android_asset/pvpc.html");
        mywebview.loadUrl("http://pixeldev.in/webservices/machine_learning/pvpc.html")
        (dialog.findViewById<View>(R.id.bt_close) as ImageButton).setOnClickListener { dialog.dismiss() }
        (dialog.findViewById<View>(R.id.bt_accept) as Button).setOnClickListener { dialog.dismiss() }
        (dialog.findViewById<View>(R.id.bt_decline) as Button).setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()?.addOnCompleteListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

}