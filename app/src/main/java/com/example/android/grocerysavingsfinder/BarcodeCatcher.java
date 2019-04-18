package com.example.android.grocerysavingsfinder;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class BarcodeCatcher {

    private static final String TAG = "BarcodeCatcher";
    //private static final String API_KEY = "71A442A46039094B7F7E0CAA7EC97B35";
    private static final String API_KEY =  "1eg9q55zf79gcsm3hy8zuyux5unszb";
    public byte[] getURLBytes(String urlspec) throws IOException {

        URL url = new URL(urlspec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        Log.i(TAG, "Anything");
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            Log.i(TAG, "Get Ready...");
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "No Connection");
                throw new IOException(connection.getResponseMessage() +
                        ": with " + urlspec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            Log.i(TAG, "Reading Bytes");
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);

            }
            out.close();
            Log.i(TAG, "Nailed it");
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        Log.i(TAG, "We here" );
        URL url1 = new URL(urlSpec);
        BufferedReader br = new BufferedReader(new InputStreamReader(url1.openStream()));
        String str = "";
        String data = "";
        while (null != (str= br.readLine())) {
            data+=str;
        }
        return data;
        //return new String(getURLBytes(urlSpec));
        /**
         URL url = new URL(urlSpec);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String str = "";
        String data = "";
        while (null != (str= br.readLine())) {
            data+=str;
        }
        Log.i(TAG, "Received data: " + data);
        return data;**/
    }

    public String fetchItems(String code) {
        String data = "";
        /**try {
            String url = Uri.parse("https://api.upcdatabase.org/product/")
                    .buildUpon()
                    .appendPath(code)
                    .appendPath(API_KEY)
                    .build().toString();
            Log.i(TAG, "Received url " + url);
            data = getUrlString(url);
            //String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + data);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);

            return null;
        }**/
        try {
            String url = Uri.parse("https://api.barcodelookup.com/v2/products")
                    .buildUpon()
                    .appendQueryParameter("barcode", code)
                    .appendQueryParameter("formatted", "n")
                    .appendQueryParameter("key", API_KEY)
                    .build().toString();
            Log.i(TAG, "Received url " + url);
            data = getUrlString(url);
            //String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + data);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);

            return null;
        }



        try {

            JSONObject jsonBody = new JSONObject(data);
            JSONArray jsonItem =  (jsonBody.getJSONArray("products"));
            String name = jsonItem.getJSONObject(0).getString("brand");
            Log.i(TAG, "Maybe this worked" + name);
            return name;

        } catch (JSONException je) {
            je.printStackTrace();
            Log.e(TAG, "JSON parse failed: " + je);
        }
        return null;
    }


}
