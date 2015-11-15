package com.runningbuddy.mobileapp;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Daniel on 11/15/2015.
 */
public class BuddyAPI {

    private static final String TAG = BuddyAPI.class.getCanonicalName();

    public static final MediaType JSONType = MediaType.parse("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static JsonObject call(String method, JsonObject requestBody) {
        Log.d(TAG, "calling " + method);
        Log.d(TAG, "body: " + requestBody);
        try {
            return new APICallTask().execute(method, gson.toJson(requestBody)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class APICallTask extends AsyncTask<String, Void, JsonObject> {
        @Override
        protected JsonObject doInBackground(String... params) {
            RequestBody body = RequestBody.create(JSONType, params[1]);
            Request request = new Request.Builder()
                    .url("http://activitybuddy.cloudapp.net:8080/" + params[0])
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Log.d(TAG, response.toString());
                String responseBody = response.body().string();
                Log.d(TAG, responseBody);
                if (responseBody.length() == 0) return null;
                else return gson.fromJson(response.body().string(), JsonObject.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
