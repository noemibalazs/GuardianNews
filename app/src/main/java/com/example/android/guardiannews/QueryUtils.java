package com.example.android.guardiannews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import java.util.List;

/**
 * Created by Noemi on 1/26/2018.
 */

public final class QueryUtils {

    private QueryUtils(){}
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List<News> fetchDataFromNews(String requestUrl){

        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        }
        catch (IOException e){
            Log.e(LOG_TAG, "Error making HTTP request", e);
        }
        List<News> news = extractDataFromJson(jsonResponse);

        return  news;
    }

    private static URL createUrl (String stringUrl)  {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error building Url", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else {
                Log.e(LOG_TAG, "Error response code" + urlConnection.getResponseCode());
            }
        }
        catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving News JSON results", e);
        }
        finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static Bitmap makeHttpRequest(String imageUrl)throws IOException{
        Bitmap thumbnail = null;
        if(imageUrl == null){
            return thumbnail;
        }

        URL url = createUrl(imageUrl);
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            if (urlConnection.getResponseCode()==200){
                inputStream = urlConnection.getInputStream();
                thumbnail = BitmapFactory.decodeStream(inputStream);
            }
            else {
                Log.e(LOG_TAG, "Error request code"+ urlConnection.getResponseCode());
            }
        }
        catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving News image results", e);
        }
        finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if (inputStream!=null){
                inputStream.close();
            }
        }
        return thumbnail;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream!=null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line!=null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();

    }

    private static List<News> extractDataFromJson(String newsJSON){

        if (TextUtils.isEmpty(newsJSON)){
            return null;
        }
        List<News> news = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(newsJSON);

            JSONObject responseObject = root.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");

            for (int i=0; i<resultsArray.length(); i++){

                JSONObject elementNews = resultsArray.getJSONObject(i);

                String webUrl = elementNews.getString("webUrl");

                JSONObject fieldsNews = elementNews.getJSONObject("fields");
                String title = fieldsNews.getString("headline");
                String text = fieldsNews.getString("trailText");
                String date = fieldsNews.getString("lastModified");

                String image = fieldsNews.getString("thumbnail");
                Bitmap imageBitmap = makeHttpRequest(image);

                News newss = new News(title, text, date, imageBitmap, webUrl);
                news.add(newss);

            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing Json results", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return news;

    }
}
