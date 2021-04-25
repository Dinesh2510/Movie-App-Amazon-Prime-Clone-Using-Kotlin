package com.movie.app.PaymentGateway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.movie.app.R;
import com.paytm.pg.merchant.PaytmChecksum;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Security;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;
    String midString = "BAvVOw78299026020286";
    String txnAmountString;
    String orderIdString = "123545";
    String txnTokenString;
    Button btnPayNow;

    Integer ActivityRequestCode = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnPayNow = (Button) findViewById(R.id.txnProcessBtn);
        btnPayNow.setOnClickListener(MainActivity.this);

    }


    public void btnProcessEvent() {
        progressBar.setVisibility(View.VISIBLE);

        EditText mid = (EditText) findViewById(R.id.midId);
        EditText orderId = (EditText) findViewById(R.id.orderId);
        EditText txnToken = (EditText) findViewById(R.id.txnTokenId);
        EditText txnAmount = (EditText) findViewById(R.id.txnAmountId);
        CheckBox environment = (CheckBox) findViewById(R.id.environmentCheckbox);


        txnAmountString = txnAmount.getText().toString();
        midString = mid.getText().toString();
        orderIdString = orderId.getText().toString();
        txnTokenString = txnToken.getText().toString();

        new UpdateTask().execute();

        /* for Production */
// URL url = new URL("https://securegw.paytm.in/theia/api/v1/initiateTransaction?mid=YOUR_MID_HERE&orderId=ORDERID_98765");



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txnProcessBtn:
                btnProcessEvent();
                break;
        }
    }

    private class UpdateTask extends AsyncTask<String, String,String> {
        protected String doInBackground(String... urls) {


            JSONObject paytmParams = new JSONObject();

            JSONObject body = new JSONObject();
            try {
                body.put("requestType", "Payment");

                body.put("mid", "YOUR_MID_HERE");
                body.put("websiteName", "WEBSTAGING");
                body.put("orderId", "ORDERID_98765");
                body.put("callbackUrl", "https://merchant.com/callback");

                JSONObject txn_Amount = new JSONObject();
                txn_Amount.put("value", "1.00");
                txn_Amount.put("currency", "INR");

                JSONObject userInfo = new JSONObject();
                userInfo.put("custId", "CUST_001");
                body.put("txnAmount", txn_Amount);
                body.put("userInfo", userInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*
             * Generate checksum by parameters we have in body
             * You can get Checksum JAR from https://developer.paytm.com/docs/checksum/
             * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys
             */

            String checksum = null;
            try {
                checksum = PaytmChecksum.generateSignature(body.toString(), midString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject head = new JSONObject();
            try {
                head.put("signature", checksum);


                paytmParams.put("body", body);
                paytmParams.put("head", head);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("tag_catch_1", "btnProcessEvent: "+e.toString());

            }
            String post_data = paytmParams.toString();

            /* for Staging */
            URL url = null;
            try {
                url = new URL("https://securegw-stage.paytm.in/theia/api/v1/initiateTransaction?mid=YOUR_MID_HERE&orderId=ORDERID_98765");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
                requestWriter.writeBytes(post_data);
                requestWriter.close();
                String responseData = "";
                InputStream is = connection.getInputStream();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
                if ((responseData = responseReader.readLine()) != null) {
                    System.out.append("Response: " + responseData);
                }
                responseReader.close();
            } catch (Exception exception) {
                exception.printStackTrace();
                Log.d("tag_catch", "btnProcessEvent: "+exception.toString());
            }
            return null;
        }

    }
}