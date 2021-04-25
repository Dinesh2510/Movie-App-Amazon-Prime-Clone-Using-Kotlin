package com.movie.app.Activity

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.movie.app.Activity.AppLock.NumLockScreen
import com.movie.app.Forms.Login
import com.movie.app.Forms.Login.Companion.ISLOGINSKIPPED
import com.movie.app.Forms.Login.Companion.SHARED_PREFERENCES_NAME
import com.movie.app.Forms.Login.Companion.SKIP
import com.movie.app.Forms.Login.Companion.USER_ID
import com.movie.app.Helper.Tools
import com.movie.app.R


class WelcomeScreen : AppCompatActivity() {
    private var btnTurnOn: Button? = null
    private var layoutDeniedPermissionLayout: LinearLayout? = null
    private val REQUEST_CODE_WRITE_STORAGE = 102
    private val TAG = WelcomeScreen::class.java.simpleName
    private val PERMISSION_WRITE_STORAGE: String = WRITE_EXTERNAL_STORAGE
    private val tv: TextView? = null
    private val iv: ImageView? = null
    var sharedPreferences: SharedPreferences? = null
    var user_id: String? = null
    var skip:kotlin.String? = null
    var editor: SharedPreferences.Editor? = null
    var REQUEST_CHECK_SETTINGS = "1001"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)


        sharedPreferences =
            getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        user_id = sharedPreferences?.getString(USER_ID, "")
        skip = sharedPreferences?.getString(SKIP, "")
        Log.d("SKIP_1", "onCreate: $skip")

        layoutDeniedPermissionLayout =
            findViewById<View>(R.id.id_access_permissions_layout) as LinearLayout
        btnTurnOn = findViewById<View>(R.id.id_btn_turn_on) as Button
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            accessStorage()
        } else {
            loadScreenLockActivity()
        }
        btnTurnOn?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {

                accessStorage()
            }
        })

    }

    private fun loadScreenLockActivity() {
        load()
//        val intent = Intent(applicationContext, MainActivity::class.java)
//        startActivity(intent)
//        finish()
    }
    fun load() {
        val handler = Handler()
        handler.postDelayed({
            if (user_id != "" || Tools.getString(this, ISLOGINSKIPPED, "")
                    .equals("true")
            ) {
                startActivity(Intent(this, NumLockScreen::class.java))
                finish()
            } else {
                val i = Intent(this, Login::class.java)
                finish()
                startActivity(i)
            }
        }, 2000)
    }

    private fun accessStorage() {
        val hasWriteStoragePermission =
            ContextCompat.checkSelfPermission(applicationContext, PERMISSION_WRITE_STORAGE)
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            val showRequestAgain =
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this@WelcomeScreen,
                    PERMISSION_WRITE_STORAGE
                )
            Log.e(TAG, "showRequestAgain: $showRequestAgain")
            if (showRequestAgain) {
                AlertDialog.Builder(this).setMessage("Storage permission is required")
                    .setPositiveButton("ALLOW",
                        DialogInterface.OnClickListener { dialog, which ->
                            ActivityCompat.requestPermissions(
                                this@WelcomeScreen,
                                arrayOf(PERMISSION_WRITE_STORAGE),
                                REQUEST_CODE_WRITE_STORAGE
                            )
                        }).setNegativeButton("DENY",
                        DialogInterface.OnClickListener { dialog, which ->
                            layoutDeniedPermissionLayout!!.visibility = View.VISIBLE
                        }).show()
                return
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(PERMISSION_WRITE_STORAGE),
                    REQUEST_CODE_WRITE_STORAGE
                )
                return
            }
        }
        loadScreenLockActivity()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        vararg permissions: String?,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_WRITE_STORAGE -> if (grantResults.size > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadScreenLockActivity()
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // Permission Denied
                    layoutDeniedPermissionLayout!!.visibility = View.VISIBLE
                    val pref = getSharedPreferences("fileManager", 0)
                    if (!pref.getBoolean("is_camera_requested", false)) {
                        val editor = pref.edit()
                        editor.putBoolean("is_camera_requested", true)
                        editor.apply()
                        return
                    }
                    val showRequestAgain =
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this@WelcomeScreen,
                            PERMISSION_WRITE_STORAGE
                        )
                    if (showRequestAgain) {
                        //true,
                        val builder: AlertDialog.Builder = AlertDialog.Builder(applicationContext)
                        builder.setTitle("Permission Required")
                        builder.setMessage("Storage Permission is required")
                        builder.setPositiveButton("DENY",
                            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                        builder.setNegativeButton(
                            "RE-TRY",
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                                ActivityCompat.requestPermissions(
                                    this@WelcomeScreen,
                                    arrayOf(PERMISSION_WRITE_STORAGE),
                                    REQUEST_CODE_WRITE_STORAGE
                                )
                            })
                        builder.show()
                    } else {
                        promptSettings()
                    }
                } else {
                    Log.e(TAG, "last else")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        }
    }

    private fun promptSettings() {
        val builder: AlertDialog.Builder =AlertDialog.Builder(this)
        builder.setTitle("Permission Required")
        builder.setMessage(Html.fromHtml("We require your consent to additional permission in order to proceed. Please enable them in <b>Settings</b>"))
        builder.setPositiveButton("go to Settings",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                goToSettings()
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which ->
                // finish();
            })
        builder.show()
    }

    private fun goToSettings() {
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:$packageName")
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(i)
    }


}