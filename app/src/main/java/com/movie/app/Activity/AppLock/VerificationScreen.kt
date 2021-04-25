package com.movie.app.Activity.AppLock

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.movie.app.Activity.MainActivity
import com.movie.app.R
import kotlinx.android.synthetic.main.activity_verification_screen.*

class VerificationScreen : AppCompatActivity() {
    private var cancellationSignal: CancellationSignal? = null
    private var fing_img: ImageView? = null

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object :
                BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(
                        errorCode,
                        errString
                    )
                    notifyUser("$errString");
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    fing_img = findViewById(R.id.fing_img)
                    fing_img?.setImageResource(R.drawable.finger_accept)
                    notifyUser("Authentication success!")
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_screen)
        checkBiometricSupport()
        btn_auth.setOnClickListener {
            val biometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("Welcome To Amazon Prime")
                .setSubtitle("Authentication is required")
                .setDescription("This app uses fingerprint protection to keep your data secure")
                .setNegativeButton("Cancel", this.mainExecutor, DialogInterface.OnClickListener
                { dialog, which -> notifyUser("Authentication cancelled") }).build()
            biometricPrompt.authenticate(
                getCancellationSignal(),
                mainExecutor,
                authenticationCallback
            )
        }
    }


    private fun notifyUser(message: String) {
        Toast.makeText(applicationContext, "" + message, Toast.LENGTH_LONG).show()

    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by the user")
        }
        return cancellationSignal as CancellationSignal

    }

    private fun checkBiometricSupport(): Boolean {
        var keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isKeyguardSecure) {
            notifyUser("Fingerprint authentication has not been enabled in settings")
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notifyUser("Fingerprint authentication permission is not enable")
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
    }


}