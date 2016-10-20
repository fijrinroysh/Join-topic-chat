package com.example.app.ourapplication;

import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sarumugam on 17/04/16.
 */
public class Util {
    private static final String TAG = Util.class.getSimpleName();
    private static final int CONNECTION_TIMEOUT = 60000;

    public static HttpURLConnection getHttpConnection(String url,String type) throws IOException {
        // We opened the connection using the URL.
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

        // These are basic APIs we need to use.
        conn.setRequestMethod(type);
        conn.setConnectTimeout(CONNECTION_TIMEOUT);
        conn.setReadTimeout(CONNECTION_TIMEOUT);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Cache-Control", "no-cache");

        return conn;
    }

    public static void writeToStream(HttpURLConnection connection,String content) throws IOException {
        // This is for writing to the stream
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(content);
        out.close();
    }

    public static String readInputStream(HttpURLConnection connection) throws IOException {
        int HttpResult = connection.getResponseCode();
        String msg = connection.getResponseMessage();
        Log.i(TAG, "Http response code for Token request " + HttpResult);
        StringBuilder sb = new StringBuilder();
        // This is read from the connection.
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();

            connection.disconnect();
        }else {
            Log.e(TAG, msg);
            connection.disconnect();
            throw new IOException(msg);
        }
        return sb.toString();
    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

}
