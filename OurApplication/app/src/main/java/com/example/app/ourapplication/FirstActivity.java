package com.example.app.ourapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Starting point for our App, lets start our trials here.
 */
public class FirstActivity extends AppCompatActivity {

    private final String TAG = FirstActivity.class.getSimpleName();
    private final int CONNECTION_TIMEOUT = 60000;

    private EditText mUserNameBox;
    private EditText mPasswordBox;
    private Button mLoginButton;

    private ProgressDialog mLoginProgressDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        mUserNameBox = (EditText) findViewById(R.id.editText);
        mPasswordBox = (EditText) findViewById(R.id.editText2);
        mLoginButton = (Button) findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUserNameBox.getText().toString();
                String password = mPasswordBox.getText().toString();
                new LoginTask().execute(username,password);
            }
        });
    }

    private void showProgressDialog() {
        if(mLoginProgressDlg == null){
            mLoginProgressDlg = new ProgressDialog(FirstActivity.this);
            mLoginProgressDlg.setCanceledOnTouchOutside(false);
            mLoginProgressDlg.setCancelable(false);
            mLoginProgressDlg.setMessage("Logging In...");
        }
        mLoginProgressDlg.show();
    }

    private class LoginTask extends AsyncTask<String,Void,Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // The web services part to be plugged here.
            String username = params[0];
            String password = params[1];

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int result = 0;
            return result;
        }

        @Override
        protected void onPostExecute(Integer response) {
            super.onPostExecute(response);
            mLoginProgressDlg.dismiss();
            Log.d(TAG,"Response : "+response);
        }
    }

    private void sampleWebServices(String sReqBody,String sAccessURL) throws IOException {
        // We need to define the Rest Api url here to establish the connection.
        String accessURL="https://ggogle.com/oauth2/token";

        // We opened the connection using the URL.
        HttpURLConnection conn = (HttpURLConnection) new URL(accessURL).openConnection();

        // These are basic APIs we need to use.
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setReadTimeout(CONNECTION_TIMEOUT);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Cache-Control", "no-cache");

        // This is for writing to the stream
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        out.write(sReqBody);
        out.close();

        int HttpResult = conn.getResponseCode();
        String msg=conn.getResponseMessage();
        Log.i(TAG, "Http response code for Token request "+HttpResult);
        StringBuilder sb = new StringBuilder();

        // Tis is read from the connection.
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            if (conn != null)
                conn.disconnect();
        }else {
            Log.e(TAG, msg);
            if (conn != null)
                conn.disconnect();

            throw new IOException(msg);
        }
    }
}