package com.example.aoi.myapplication;

/**
 * Created by JT on 12/3/16.
 */
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import android.os.AsyncTask;

public class MyUtils {

    public static class down {
        static String DEBUG_TAG = "HttpExample";
        static String result = "";
        static String anRe = "";

        public static String downloadJson(String myurl) {
            String API_KEY = "fa986f9bbd6747a29e1906f2289f97ce";
            InputStream is = null;
            try {
                URL taskUrl = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) taskUrl.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setRequestProperty("Ocp-Apim-Subscription-Key ", API_KEY);
                // Starts the query
                conn.connect();

                //read the JSON data and assign it to city variable
                StringBuffer buffer = new StringBuffer();
                is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = br.readLine()) != null)
                    buffer.append(line + "\r\n");
                is.close();
                conn.disconnect();
                result = buffer.toString();
                Log.d(DEBUG_TAG, result);
                return result;
            } catch (Exception e) {
                Log.i(DEBUG_TAG, e.toString());
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Throwable t) {
                    }
                }
            }
            return null;
        }

    }
}
