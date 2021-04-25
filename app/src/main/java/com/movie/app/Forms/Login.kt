package com.movie.app.Forms

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.movie.app.Activity.MainActivity
import com.movie.app.Helper.API
import com.movie.app.Helper.NetworkCheck
import com.movie.app.Helper.RestAdapter
import com.movie.app.Helper.Tools
import com.movie.app.Model.User
import com.movie.app.R
import com.movie.app.ServerCall.CallbackUser
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class Login : AppCompatActivity(), OnConnectionFailedListener {
    var email: EditText? = null
    var password: EditText? = null
    var str_email: String? = null
    var str_password: String? = null
    var submit: Button? = null
    private var callback: Call<CallbackUser>? = null
    var progressBar: ProgressBar? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var skip: TextView? = null

    var signInButton: SignInButton? = null
    private var googleApiClient: GoogleApiClient? = null
    private var mGoogleSignInClient: GoogleSignInOptions? = null
    var textView: TextView? = null
    private val RC_SIGN_IN = 1
    private var callbackManager: CallbackManager? = null
    var token: String? = null
    var login_button: LoginButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
/*
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(OnCompleteListener<String?> { task ->
                if (!task.isSuccessful) {
                    Log.w("data", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                token = task.result
                Log.d("data_fcm", "onComplete: $token")
                // Log and toast
            })
*/


        //fb login
        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()

        //Google Login
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        email = findViewById(R.id.et_email)
        password = findViewById(R.id.et_password)
        submit = findViewById(R.id.btn_submit)
        progressBar = findViewById(R.id.progress_bar)
        skip = findViewById(R.id.skip)


        signInButton = findViewById(R.id.sign_in_button)
        login_button = findViewById(R.id.login_button)
        login_button?.setReadPermissions(Arrays.asList<String>("email"))
        login_button?.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(
                    "TAG_result",
                    "handleSignInResult: " + loginResult.accessToken.userId
                )

                setFacebookData(loginResult)
                //gotoProfile();
            }

            override fun onCancel() {
                Toast.makeText(applicationContext, "Login Cancel", Toast.LENGTH_SHORT).show()
            }

            override fun onError(e: FacebookException) {
                Log.d("FacebookException", "onError: $e")
                Toast.makeText(applicationContext, "Login Error", Toast.LENGTH_SHORT).show()
            }
        })
        signInButton!!.setOnClickListener {
            val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(
                intent,
                RC_SIGN_IN
            )

        }
        val tvreg = findViewById<TextView>(R.id.tvreg)
        tvreg.setOnClickListener {
            val intent = Intent(applicationContext, Register::class.java)
            finish()
            startActivity(intent)
        }
        skip?.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, ForgotPassword::class.java)
            finish()
            startActivity(intent)
        })
        submit?.setOnClickListener(View.OnClickListener {
            str_email = email?.getText().toString()
            str_password = password?.getText().toString()
            if (str_email!!.trim { it <= ' ' } == "") {
                Toast.makeText(
                    applicationContext,
                    "Email cannot empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            if (str_password!!.trim { it <= ' ' } == "") {
                Toast.makeText(
                    applicationContext,
                    "Password cannot empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
            showLoading(true)
            processLogin(str_email!!, str_password!!)
        })
    }

    private fun processLogin(email: String, password: String) {
        val api: API = RestAdapter.createAPI()
        callback = api.login(email, password, Tools.getDeviceID(applicationContext))
        Log.d("login", "processLogin: " + Tools.getDeviceID(this))
        callback!!.enqueue(object : Callback<CallbackUser?> {
            override fun onResponse(
                call: Call<CallbackUser?>,
                response: Response<CallbackUser?>,
            ) {
                val resp: CallbackUser? = response.body()
                Log.d("TAG_0", "onResponse: " + resp?.status.toString())
                if (resp == null) {
                    onFailRequest(null)
                } else if (resp.status.equals("success")) {
                    saveLoginData(resp.response)
                    showDialogSuccess(getString(R.string.login_success_info))
                } else {
                    onFailRequest(resp.status)
                }
                showLoading(false)
            }

            override fun onFailure(
                call: Call<CallbackUser?>,
                t: Throwable,
            ) {
                // Log.e("onFailure", t.getMessage());
                if (!call.isCanceled()) onFailRequest(null)
                showLoading(false)
            }
        })
    }
    private fun setFacebookData(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { `object`, response -> // Application code
            try {
                Log.i("Response", response.toString())
                Log.i("Response", `object`.toString())
                val id = response.jsonObject.getString("id")
                val firstName =
                    response.jsonObject.getString("first_name")
                val lastName =
                    response.jsonObject.getString("last_name")
                val email = response.jsonObject.getString("email")
                val fullname = "$firstName $lastName"
                Log.d("TAG_name", "onCompleted: " + fullname + "id:" + id)
               // SubmitUserData(id, fullname, "")
                SubmitUserData(id, fullname, email)
            } catch (e: JSONException) {
                Log.d("TAG_fb_er", "onCompleted: $e")
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,email,first_name,last_name,gender")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun saveLoginData(response: User) {
        userid = response.user_id
        useremail = response.email
        userfname = response.first_name
        userlname = response.last_name
        userpwd = response.password
        userdob = response.date_birth
        usephone = response.phone_number
        userrefer = response.refer_code
        usercoins = response.coin
        userverify_flag = response.verify_flag

        //userPremiumFlag = response.body().loginresponse.userpremiumflag;
        sharedPreferences = getSharedPreferences(
            SHARED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences?.edit()
        editor?.putString(USER_ID, userid)
        editor?.putString(FNAME, userfname)
        editor?.putString(LNAME, userlname)
        editor?.putString(EMAIL, useremail)
        editor?.putString(PWD, userpwd)
        editor?.putString(DATEOFBIRTH, userdob)
        editor?.putString(PHONENO, usephone)
        editor?.putString(REFERCODE, userrefer)
        editor?.putString(COINS, usercoins)
        editor?.putString(VERIFY_FLAG, userverify_flag)
        editor?.apply()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showDialogSuccess(message: String) {
        val dialog =
            AlertDialog.Builder(this)
        dialog.setMessage(message)
        dialog.setPositiveButton(
            R.string.OK
        ) { dialogInterface, i -> finish() }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun onFailRequest(code: String?) {
        if (NetworkCheck.isConnect(this)) {
            if (TextUtils.isEmpty(code)) {
                showDialogFailed(getString(R.string.failed_text))
            } else if (code.equals("NO_EMAIL", ignoreCase = true)) {
                showDialogFailed(getString(R.string.Email))
            } else if (code.equals("NOT_VERIFIY", ignoreCase = true)) {
                showDialogFailed(getString(R.string.NOT_verifity))
            } else if (code.equals("NOT_FOUND", ignoreCase = true)) {
                showDialogFailed(getString(R.string.invalid_email_password))
            }
        } else {
            showDialogFailed(getString(R.string.no_internet_text))
        }
    }

    private fun showLoading(show: Boolean) {
        submit!!.visibility = if (show) View.INVISIBLE else View.VISIBLE
        progressBar!!.visibility = if (!show) View.INVISIBLE else View.VISIBLE
    }

    fun skipLogin(view: View?) {
        Tools.putString(this@Login, ISLOGINSKIPPED, "true")
        val i = Intent(this, MainActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun showDialogFailed(message: String) {
        val dialog =
            AlertDialog.Builder(this)
        dialog.setMessage(message)
        dialog.setPositiveButton(R.string.OK, null)
        dialog.setCancelable(true)
        dialog.show()
    }

    companion object {
        const val SHARED_PREFERENCES_NAME = "login_portal"
        const val USER_ID = "user_id"
        const val FNAME = "fname"
        const val ISLOGINSKIPPED = "loginSkipped"
        const val LNAME = "lname"
        const val EMAIL = "email"
        const val PHONENO = "phoneno"
        const val DATEOFBIRTH = "dateofbirth"
        const val PWD = "pwd"
        const val PREMIUMUSER = "premiumuser"
        const val SKIP = "skip"
        const val REFERCODE = "refer"
        const val ADDRESS_ID = "address_id"
        const val ADDRESS_MAIN = "address_main"
        const val CITY = "city"
        const val COUNTRY = "country"
        const val STATE = "state"
        const val PINCODE = "pincode"
        const val COINS = "coin"
        const val VERIFY_FLAG = "verify_flag"
        const val USER_PIC = "user_pic"
        var userid = ""
        var userverify_flag = ""
        var usercoins = ""
        var userfname = ""
        var userlname = ""
        var useremail = ""
        var userpwd = ""
        var userPremiumFlag = ""
        var userdob = ""
        var usephone = ""
        var userrefer = ""
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult?) {
        if (result!!.isSuccess) {
            val account = result.signInAccount
            if (account != null) {
                val personName = account.displayName
                val personGivenName = account.givenName
                val personFamilyName = account.familyName
                val personEmail = account.email
                val personId = account.id
                val personPhoto = account.photoUrl
                Log.d(
                    "TAG_result",
                    "account: " + account.displayName + account.id + account.email
                )
              //  SharedPref.putuserId(this@LoginActivity, SharedPref.user_id, account.id)
               SubmitUserData(personId.toString(), personName.toString(), personEmail.toString())

            }
        } else {
            Toast.makeText(applicationContext, "Sign in cancel", Toast.LENGTH_LONG).show()
        }
    }

    private fun gotoProfile() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        finish()
        startActivity(intent)
    }

    override fun onConnectionFailed(onConnectionFailed: ConnectionResult) {
        Log.d("TAG_result", "onConnectionFailed: $onConnectionFailed")
    }
    private fun SubmitUserData(id: String, fullname: String, email: String) {
        Log.d("TAG_SubmitUserData", "SubmitUserData: $id$fullname$email")
        val progressDialog: ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading ...")
        progressDialog.show()
        if (Tools.isConnectedToInternet(this)) {
            val stringRequest: StringRequest = object : StringRequest(Method.POST, API.Social_Login,
                com.android.volley.Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        Log.d("TAG_Submit_response", "SubmitUserData: $response")
                        val message = jsonObject.getString("status")
                        val code = jsonObject.getString("code")
                        if (code == "200") {
                            progressDialog.dismiss()
                            val jsonObject1 = jsonObject.getJSONObject("UserDetails")
                            val user_id = jsonObject1.getString("user_id")
                            val first_name = jsonObject1.getString("first_name")
                            val last_name = jsonObject1.getString("last_name")
                            val email = jsonObject1.getString("email")
                            val password = jsonObject1.getString("password")
                            val date_birth = jsonObject1.getString("date_birth")
                            //val image = jsonObject1.getString("image")
                            val refer_code = jsonObject1.getString("refer_code")
                            val coin = jsonObject1.getString("coin")
                            val verify_flag = jsonObject1.getString("verify_flag")
                            sharedPreferences =
                                getSharedPreferences(SHARED_PREFERENCES_NAME,
                                    MODE_PRIVATE)
                            editor = sharedPreferences?.edit()
                            editor?.putString(USER_ID, user_id)
                            editor?.putString(FNAME,
                                first_name)
                            editor?.putString(LNAME, last_name)
                            editor?.putString(EMAIL, email)
                            editor?.putString(PWD, password)
                            editor?.putString(DATEOFBIRTH,
                                date_birth)
                            editor?.putString(PHONENO, "")
                            editor?.putString(REFERCODE,
                                refer_code)
                            editor?.putString(COINS, coin)
                            editor?.putString(VERIFY_FLAG,
                                verify_flag)
                            editor?.putString(USER_PIC, "-")
                            editor?.apply()
                            gotoProfile()
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(this@Login, "" + message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        progressDialog.dismiss()
                        e.printStackTrace()
                    }
                },
                com.android.volley.Response.ErrorListener { error ->
                    progressDialog.dismiss()
                    error.printStackTrace()
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    params["id"] = id
                    params["name"] = fullname
                    params["fcm"] = "token"
                    Log.d("TAG_parameter", "getParams: $params")
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(this@Login)
            stringRequest.retryPolicy = DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
            requestQueue.add(stringRequest)
        } else {
            progressDialog.dismiss()
            Toast.makeText(this, "Checked Internet Connection", Toast.LENGTH_LONG).show()
        }
    }

}