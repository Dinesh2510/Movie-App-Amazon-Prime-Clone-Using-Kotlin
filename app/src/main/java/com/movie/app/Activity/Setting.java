package com.movie.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.movie.app.Helper.PreferManager;
import com.movie.app.R;

import java.util.ArrayList;

public class Setting extends AppCompatActivity {
    private Switch sBtnLock, sBtnShowHiddenFile;
    private Button btnOne, btnTwo, btnThree, btnFour, btnFive, btnSix, btnSeven, btnEight, btnNine, btnZero, btnCancel;
    private ImageView imgDelete;
    private String tempPassword = "";
    private TextView lblEnterPassword;
    private String password = "";
    private String rePassword;
    private EditText txtPassword;
    private Dialog appLockDialog;
    private ArrayList<String> pswArray;
    private int passwordLength;
    private PreferManager preferManager;
    private TextView lblAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sBtnLock =  findViewById(R.id.id_setting_lock);
        sBtnShowHiddenFile = findViewById(R.id.id_setting_hide_file);
        lblAbout = (TextView) findViewById(R.id.id_about);
        pswArray = new ArrayList<>();

        preferManager = new PreferManager(getApplicationContext());
        if (preferManager.isPasswordActivated()) {
            sBtnLock.setChecked(true);
        } else {
            sBtnLock.setChecked(false);
        }
        if (preferManager.isHiddenFileVisible()) {
            sBtnShowHiddenFile.setChecked(true);
        } else {
            sBtnShowHiddenFile.setChecked(false);
        }
        sBtnLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (preferManager.getPassword().length() == 0) {
                        showPasswordDialog();
                    } else {
                        preferManager.setPasswordActivated(true);
                    }
                } else {
                    preferManager.setPasswordActivated(false);
                }
            }
        });
        sBtnShowHiddenFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    preferManager.setHiddenFileVisible(true);
                } else {
                    preferManager.setHiddenFileVisible(false);
                }
            }
        });
//        lblAbout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
//                startActivity(intent);
//            }
//        });
    }


    private void showPasswordDialog() {
        appLockDialog = new Dialog(Setting.this, android.R.style.Theme_Translucent_NoTitleBar);
        appLockDialog.setContentView(R.layout.custom_app_lock_dialog);
        btnOne = (Button) appLockDialog.findViewById(R.id.id_one);
        btnTwo = (Button) appLockDialog.findViewById(R.id.id_two);
        btnThree = (Button) appLockDialog.findViewById(R.id.id_three);
        btnFour = (Button) appLockDialog.findViewById(R.id.id_four);
        btnFive = (Button) appLockDialog.findViewById(R.id.id_five);
        btnSix = (Button) appLockDialog.findViewById(R.id.id_six);
        btnSeven = (Button) appLockDialog.findViewById(R.id.id_seven);
        btnEight = (Button) appLockDialog.findViewById(R.id.id_eight);
        btnNine = (Button) appLockDialog.findViewById(R.id.id_nine);
        btnZero = (Button) appLockDialog.findViewById(R.id.id_zero);
        btnCancel = (Button) appLockDialog.findViewById(R.id.id_cancel);
        imgDelete = (ImageView) appLockDialog.findViewById(R.id.id_delete);
        txtPassword = (EditText) appLockDialog.findViewById(R.id.id_password);
        lblEnterPassword = (TextView) appLockDialog.findViewById(R.id.id_lbl_password);
        btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("1");
            }
        });
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("2");
            }
        });
        btnThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("3");
            }
        });
        btnFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("4");
            }
        });
        btnFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("5");
            }
        });
        btnSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("6");
            }
        });
        btnSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("7");
            }
        });
        btnEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("8");
            }
        });
        btnNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("9");
            }
        });
        btnZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword("0");
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appLockDialog.dismiss();
                sBtnLock.setChecked(false);
            }
        });
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePassword();
            }
        });
        appLockDialog.show();
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
            if (password.length() == 0) {
                lblEnterPassword.setText("Re-enter password");
                password = tempPassword;
                txtPassword.setText("");
                tempPassword = "";
                passwordLength = 0;
                pswArray.clear();
            } else {
                rePassword = tempPassword;
                if (password.equals(rePassword)) {
                    Toast.makeText(getApplicationContext(), "corrcet", Toast.LENGTH_SHORT).show();
                    preferManager.setPassword(password);
                    preferManager.setPasswordActivated(true);
                    appLockDialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "miss match", Toast.LENGTH_SHORT).show();
                    rePassword = "";
                    tempPassword = "";
                    txtPassword.setText("");
                    passwordLength = 0;
                    pswArray.clear();
                }
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event


}
