package com.movie.app.Activity.AppLock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.movie.app.Activity.MainActivity;
import com.movie.app.Helper.PreferManager;
import com.movie.app.Helper.Tools;
import com.movie.app.R;

import java.util.ArrayList;
import java.util.List;

public class NumLockScreen extends AppCompatActivity {
    private PreferManager preferManager;
    private Button btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight, btnNine, btnZero, btnCancel;
    private ImageView imgDelete;
    private String tempPassword = "";
    private EditText txtPassword;
    private LinearLayout fingerlyt;
    private ArrayList<String> pswArray;
    private int passwordLength;

    private static final String TRUE_CODE = "2222";
    private static final int MAX_LENGHT = 4;
    private String codeString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_num_lock_screen);
        preferManager = new PreferManager(getApplicationContext());

        if (!preferManager.isPasswordActivated()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            fingerlyt = findViewById(R.id.fingerlyt);

            btnOne = (Button) findViewById(R.id.id_one);
            btnTwo = (Button) findViewById(R.id.id_two);
            btnThree = (Button) findViewById(R.id.id_three);
            btnFour = (Button) findViewById(R.id.id_four);
            btnFive = (Button) findViewById(R.id.id_five);
            btnSix = (Button) findViewById(R.id.id_six);
            btnSeven = (Button) findViewById(R.id.id_seven);
            btnEight = (Button) findViewById(R.id.id_eight);
            btnNine = (Button) findViewById(R.id.id_nine);
            btnZero = (Button) findViewById(R.id.id_zero);
            btnCancel = (Button) findViewById(R.id.id_cancel);
            imgDelete = (ImageView) findViewById(R.id.id_delete);
            txtPassword = (EditText) findViewById(R.id.id_password);
            // set the ad unit ID

            pswArray = new ArrayList<>();
            btnOne.setOnClickListener(view -> setPassword("1"));
            btnTwo.setOnClickListener(view -> setPassword("2"));
            btnThree.setOnClickListener(view -> setPassword("3"));
            btnFour.setOnClickListener(view -> setPassword("4"));
            btnFive.setOnClickListener(view -> setPassword("5"));
            btnSix.setOnClickListener(view -> setPassword("6"));
            btnSeven.setOnClickListener(view -> setPassword("7"));
            btnEight.setOnClickListener(view -> setPassword("8"));
            btnNine.setOnClickListener(view -> setPassword("9"));
            btnZero.setOnClickListener(view -> setPassword("0"));
            btnCancel.setOnClickListener(view -> finish());
            imgDelete.setOnClickListener(view -> removePassword());
            fingerlyt.setOnClickListener(v -> {
                if (checkBiometricSupport()) {
                    Intent intent = new Intent(getApplicationContext(), VerificationScreen.class);
                    startActivity(intent);
                    finish();
                }
            });

        }
    }

    private Boolean checkBiometricSupport() {

        KeyguardManager keyguardManager =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        PackageManager packageManager = this.getPackageManager();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            notifyUser("This Android version does not support fingerprint authentication.");
            return false;
        }

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            notifyUser("Fingerprint Sensor not supported");
            return false;
        }

        if (!keyguardManager.isKeyguardSecure()) {
            notifyUser("Lock screen security not enabled in Settings");

            return false;
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_BIOMETRIC) !=
                PackageManager.PERMISSION_GRANTED) {
            notifyUser("Fingerprint authentication permission not enabled");

            return false;
        }

        return true;
    }

    private void notifyUser(String message) {
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_LONG).show();

    }


    private void removePassword() {
        passwordLength = pswArray.size();
        Log.d("psw length", "" + passwordLength);
        if (passwordLength > 0) {
            pswArray.remove(passwordLength - 1);
            tempPassword = tempPassword.substring(0, passwordLength - 1);
            txtPassword.setText(tempPassword);
            Log.d("remove psw", tempPassword);
        }
    }

    private void setPassword(String strPassword) {
        passwordLength = pswArray.size();
        if (passwordLength < 4) {
            pswArray.add(passwordLength, strPassword);
            tempPassword = tempPassword + pswArray.get(passwordLength);
            txtPassword.setText(tempPassword);
            Log.d("password", tempPassword);
        }
        if (passwordLength == 3) {
            if (tempPassword.equals(preferManager.getPassword())) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
                Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                tempPassword = "";
                txtPassword.setText("");
                passwordLength = 0;
                pswArray.clear();
                fingerlyt.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}