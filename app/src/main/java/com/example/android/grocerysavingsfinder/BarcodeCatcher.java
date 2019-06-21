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
        String str;
        String data = "";
        while (null != (str= br.readLine())) {
            data+=str;
        }
        return data;
    }

    public String[] fetchItems(String code) {
        if(code.matches("[0-9]+") == false || code.length() < 9 || code.length() > 13) {
            Log.d(TAG, "Not a valid barcode");
            return null;
        }

        String[] result = new String[4];
        String data;
        /**try {
            String url = Uri.parse("https://api.barcodelookup.com/v2/products")
                    .buildUpon()
                    .appendQueryParameter("barcode", code)
                    .appendQueryParameter("formatted", "n")
                    .appendQueryParameter("key", API_KEY)
                    .build().toString();
            data = getUrlString(url);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
            return null;
        }**/

        try {
            String url = Uri.parse("https://api.upcitemdb.com/prod/trial/lookup")
                    .buildUpon()
                    .appendQueryParameter("upc", code)
                    .build().toString();
            data = getUrlString(url);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
            return null;
        }
        try {
            JSONObject jsonBody = new JSONObject(data);
            JSONArray jsonItem =  (jsonBody.getJSONArray("items"));
            result[0] = jsonItem.getJSONObject(0).getString("brand");
            result[1] = jsonItem.getJSONObject(0).getString("title");
            String[] name = jsonItem.getJSONObject(0).getString("title").split(" ");
            result[2] = name[0] + name[1];
            result[3] = name[1];
            Log.d(TAG, result[0] + ", " + result[1] + ", " + result[2] + ", " + result[3]);
            return result;
        } catch (JSONException je) {
            je.printStackTrace();
            Log.e(TAG, "JSON parse failed: " + je);
        }
        return null;
    }


}
