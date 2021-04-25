package com.movie.app.Forms

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.movie.app.Helper.API
import com.movie.app.Helper.NetworkCheck
import com.movie.app.Helper.RestAdapter
import com.movie.app.Helper.Tools
import com.movie.app.Model.User
import com.movie.app.R
import com.movie.app.ServerCall.CallbackUser
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class Register : AppCompatActivity() {
    var et_email: EditText? = null
    var et_password: EditText? = null
    var et_fname: EditText? = null
    var et_lname: EditText? = null
    var login_now: TextView? = null
    var tvreg: TextView? = null
    var tc_con: TextView? = null
    var btn_register: Button? = null
    var str_email: String? = null
    var str_password: String? = null
    var str_fname: String? = null
    var str_lname: String? = null
    var str_login_now: String? = null
    var submit: Button? = null
    private var callback: Call<CallbackUser>? = null
    var progressBar: ProgressBar? = null
    private var avatar: ImageView? = null
    private val bitmap: Bitmap? = null
    private val user: User? = null
    var ll_tc: LinearLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        avatar = findViewById(R.id.avatar)
        et_email = findViewById(R.id.et_email)
        ll_tc = findViewById(R.id.ll_tc)
        tc_con = findViewById(R.id.tc_con)
        et_password = findViewById(R.id.et_password)
        et_fname = findViewById(R.id.et_fname)
        et_lname = findViewById(R.id.et_lname)
        login_now = findViewById(R.id.tvreg)
        btn_register = findViewById(R.id.btn_submit)
        progressBar = findViewById(R.id.progress_bar)
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        tc_con?.setOnClickListener(View.OnClickListener { showTermServicesDialog() })
        login_now?.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, Login::class.java)
            finish()
            startActivity(intent)
        })
        findViewById<View>(R.id.lyt_avatar).setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                REQUEST_CODE_PICTURE
            )
        }


        btn_register?.setOnClickListener(View.OnClickListener {
            str_password = et_password?.getText().toString().trim { it <= ' ' }
            str_email = et_email?.getText().toString().trim { it <= ' ' }
            str_fname = et_fname?.getText().toString().trim { it <= ' ' }
            str_lname = et_lname?.getText().toString().trim { it <= ' ' }

            /*   user.first_name = str_fname;
                    user.last_name = str_lname;
                    user.email = str_email;
                    user.password = str_password;*/if (str_fname!!.trim { it <= ' ' } == "") {
            Toast.makeText(this@Register, "First name cannot empty", Toast.LENGTH_SHORT)
                .show()
            return@OnClickListener
        }
            if (str_lname!!.trim { it <= ' ' } == "") {
                Toast.makeText(this@Register, "Last name cannot empty", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }
            if (!Tools.validate(str_email)) {
                Toast.makeText(this@Register, "Enter valid Email", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (str_password!!.trim { it <= ' ' } == "") {
                Toast.makeText(this@Register, "Password cannot empty", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }
            Handler().postDelayed({ requestRegisterApi() }, 1000)
            showLoading(true)
        })
    }

    private fun showToastNReturnFalse(text: String): Boolean {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        return false
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICTURE && resultCode == Activity.RESULT_OK) {
            val image_uri = data!!.data
            //bitmap = AvatarUtils.getBitmapFormUri(this, image_uri);
            if (bitmap != null) {
                Tools.displayImageCircle(this, avatar, bitmap)
            }
        }
    }

    private fun createPartFromString(value: String?): RequestBody {
        return value?.let { RequestBody.create(MultipartBody.FORM, it) }!!
    }

    private fun requestRegisterApi() {
        val map =
            HashMap<String, RequestBody>()
        map["first_name"] = createPartFromString(str_fname)
        map["last_name"] = createPartFromString(str_lname)
        map["email"] = createPartFromString(str_email)
        map["notif_device"] = createPartFromString(Tools.getDeviceID(this))
        map["password"] = createPartFromString(str_password)
        val body: MultipartBody.Part? = null

        /* if (bitmap != null) {
            //File file = AvatarUtils.createTempFile(this, bitmap);
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/ *"), file);
            body = MultipartBody.Part.createFormData("avatar", file.getName(), reqFile);
        }*/
        val api: API = RestAdapter.createAPI()
        callback = api.register(body, map)
        callback!!.enqueue(object : Callback<CallbackUser?> {
            override fun onResponse(
                call: Call<CallbackUser?>,
                response: Response<CallbackUser?>
            ) {
                val resp: CallbackUser? = response.body()
                if (resp == null) {
                    onFailRequest(null)
                } else if (resp.status.equals("SUCCESS")) {
                    // user = resp.response;
                    showDialogSuccess(getString(R.string.register_success))
                } else {
                    onFailRequest(resp.status)
                }
                showLoading(false)
            }

            override fun onFailure(
                call: Call<CallbackUser?>,
                t: Throwable
            ) {
                Log.e("onFailure", t.message!!)
                if (!call.isCanceled()) onFailRequest(null)
                showLoading(false)
            }
        })
    }

    private fun onFailRequest(code: String?) {
        if (NetworkCheck.isConnect(this)) {
            if (TextUtils.isEmpty(code) || code.equals("FAILED", ignoreCase = true)) {
                showDialogFailed(getString(R.string.failed_text))
            } else if (code.equals("EXIST", ignoreCase = true)) {
                showDialogFailed(getString(R.string.email_in_use))
            } else if (code.equals("NOT_FOUND", ignoreCase = true)) {
                showDialogFailed(getString(R.string.account_not_found))
            }
        } else {
            showDialogFailed(getString(R.string.no_internet_text))
        }
    }

    private fun showLoading(show: Boolean) {
        btn_register!!.visibility = if (show) View.INVISIBLE else View.VISIBLE
        progressBar!!.visibility = if (!show) View.INVISIBLE else View.VISIBLE
    }

    private fun showDialogFailed(message: String) {
        val dialog =
            AlertDialog.Builder(this)
        dialog.setMessage(message)
        dialog.setPositiveButton(R.string.OK, null)
        dialog.setCancelable(true)
        dialog.show()
    }

    private fun showDialogSuccess(message: String) {
        val dialog =
            AlertDialog.Builder(this)
        dialog.setMessage(message)
        dialog.setPositiveButton(
            R.string.OK
        ) { dialogInterface, i ->
            val intent = Intent(applicationContext, Login::class.java)
            finish()
            startActivity(intent)
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showTermServicesDialog() {
        Toast.makeText(this, "Loading..", Toast.LENGTH_SHORT).show()
        val dialog = Dialog(this@Register)
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

    companion object {
        private const val REQUEST_CODE_PICTURE = 500
    }
}