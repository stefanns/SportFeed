package com.example.android.sportsfeed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Stefan on 5/3/2018.
 * Class used to have all the utilities for the app
 * JSON parsing, HTTP networking and all helpers methods
 */


public final class Utils {
    private final static String LOG_TAG = Utils.class.getSimpleName();

    private Utils() {
    }

    public static ArrayList<SportEvent> fetchEarthquakeData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        /*Extract relevant fields from the JSON response and create an object
        and return it
         */
        return extractSportNews(jsonResponse);
    }

    //create URL based on string input
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }


    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<SportEvent> extractSportNews(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        // Create an empty ArrayList in which we can add info
        ArrayList<SportEvent> sports = new ArrayList<>();
        try {
            // this is for taking the whole root ot the JSON object
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject response = root.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); ++i) {
                JSONObject sportsCurrent = results.getJSONObject(i);
                String titleCurrent = sportsCurrent.getString("webTitle");
                String dateCurrent = sportsCurrent.getString("webPublicationDate");
                String sectionCurrent = sportsCurrent.getString("sectionId");
                String url = sportsCurrent.getString("webUrl");
                JSONObject fields = sportsCurrent.getJSONObject("fields");
                String imageUrl = fields.getString("thumbnail");
                Bitmap newsImage = decodeImage(imageUrl);
                String date = shapeDate(dateCurrent);
                sports.add(new SportEvent(titleCurrent, date, sectionCurrent, url, newsImage));
            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of sport events
        return sports;
    }

    /*
    method used to download the image from the url
    and convert it into a Bitmap.
     */
    public static Bitmap decodeImage(String url) {

        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }


    //shape the date and removes unnecessary characters
    public static String shapeDate(String date) {
        StringBuilder sb = new StringBuilder(date);
        sb.replace(10, 11, " ");
        sb.delete(16, 20);
        return sb.toString();
    }

}
