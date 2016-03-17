package com.example.roysh.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ROYSH on 3/9/2016.
 */
public class fragment extends Fragment {

    private final String TAG = fragment.class.getSimpleName();
    private final String ACCESS_URL = "http://ec2-54-254-185-153.ap-southeast-1.compute.amazonaws.com/sign_in.php";
    private final int CONNECTION_TIMEOUT = 60000;

    // We need to define the Rest Api url here to establish the connection.
//    private String mAccessURL="http://192.168.1.10:8889";

    private EditText mUserNameBox;
    private EditText mPasswordBox;
    private Button mLoginButton;
    private TextView mLogText;
    private ViewFlipper mFlipper;
    private EditText mUrlBox;
    private ProgressDialog mLoginProgressDlg;

    private String mReadMessage;
    private String mErrorMessage;

    public static fragment newInstance(String name) {

        Bundle args = new Bundle();
        args.putString("name", name);

        fragment fragment = new fragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        TextView txtview = (TextView) view.findViewById(R.id.text);
        //txtview.setText(getArguments().getString("name"));
        mUserNameBox = (EditText) view.findViewById(R.id.username_field);
        mPasswordBox = (EditText) view.findViewById(R.id.password_field);
        mLoginButton = (Button) view.findViewById(R.id.login_button);
        mLogText = (TextView) view.findViewById(R.id.log_text);
        mFlipper = (ViewFlipper) view.findViewById(R.id.flipper);
        mUrlBox = (EditText) view.findViewById(R.id.url_field);
        mUrlBox.setText(ACCESS_URL);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = getRequestBody();
                String connectingUrl = mUrlBox.getText().toString();
                if (TextUtils.isEmpty(connectingUrl)) {
                    connectingUrl = ACCESS_URL;
                }
                new LoginTask().execute(body, connectingUrl);
            }
        });
    return view;
    }
/*
    @Override
    public void onBackPressed() {
        if(mFlipper.getDisplayedChild() != 0){
            flipToPage(0);
        }else {
            super.onBackPressed();
        }
    }
*/
    private void showProgressDialog() {
        if(mLoginProgressDlg == null){
            mLoginProgressDlg = new ProgressDialog(getActivity());
            mLoginProgressDlg.setCanceledOnTouchOutside(false);
            mLoginProgressDlg.setCancelable(false);
            mLoginProgressDlg.setMessage("Logging In...");
        }
        mLoginProgressDlg.show();
    }

    private void sampleWebServices(String sReqBody,String url) throws IOException {
        // We opened the connection using the URL.
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

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
        Log.i(TAG, "Http response code for Token request " + HttpResult);
        StringBuilder sb = new StringBuilder();

        // Tis is read from the connection.
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            if (conn != null)
                conn.disconnect();
            mReadMessage = mReadMessage+sb.toString();
        }else {
            Log.e(TAG, msg);
            if (conn != null)
                conn.disconnect();
            throw new IOException(msg);
        }
    }

    private String getRequestBody(){
        JSONObject body = new JSONObject();
        try {
            body.put("name",mUserNameBox.getText());
            body.put("password",mPasswordBox.getText());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body.toString();
    }

    private void flipToPage(int page){
        switch (page){
            case 0:
                break;
            case 1:
                break;
        }
        mFlipper.setDisplayedChild(page);
    }

    private class LoginTask extends AsyncTask<String,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mReadMessage = "Reading :\n\n";
            mErrorMessage = "";

           showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean result = false;
            // The web services part to be plugged here.
            String body = params[0];
            String url = params[1];

            try {
                sampleWebServices(body,url);
                result = true;
            } catch (IOException e) {
                mReadMessage = mReadMessage+" Failed.";
                mErrorMessage = "Error Message is: \n\n";
                mErrorMessage = mErrorMessage+e.toString();
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            super.onPostExecute(response);
            mLoginProgressDlg.dismiss();
            Log.d(TAG, "Response : " + response);
            mLogText.setText(mReadMessage);
            mLogText.append("\n\n" + mErrorMessage);
            flipToPage(1);
        }
    }



}
