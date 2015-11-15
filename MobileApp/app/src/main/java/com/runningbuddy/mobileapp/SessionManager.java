package com.runningbuddy.mobileapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.sensors.BandCaloriesEvent;
import com.microsoft.band.sensors.BandCaloriesEventListener;
import com.microsoft.band.sensors.BandDistanceEvent;
import com.microsoft.band.sensors.BandDistanceEventListener;

import java.util.concurrent.ExecutionException;

/**
 * Created by Daniel on 11/15/2015.
 */
public class SessionManager {

    private static final String TAG = SessionManager.class.getCanonicalName();

    private static SessionManager instance = new SessionManager();

    public static SessionManager getInstance() {
        return instance;
    }

    private Context context;
    private BandClient client;
    private BandDistanceEventListener distanceEventListener;
    private BandCaloriesEventListener caloriesEventListener;

    private boolean firstDistance, firstCalories;
    private long startDistance, distance;
    private long startTime, time;
    private long startCalories, calories;

    private SessionManager() {
        client = null;

        firstDistance = false;
        firstCalories = false;
        distanceEventListener = new BandDistanceEventListener() {
            @Override
            public void onBandDistanceChanged(BandDistanceEvent bandDistanceEvent) {
                Log.d(TAG, "onBandDistanceChanged: " + bandDistanceEvent);
                distance = bandDistanceEvent.getTotalDistance();
                if (firstDistance) {
                    startDistance = distance;
                    firstDistance = false;
                }
            }
        };
        caloriesEventListener = new BandCaloriesEventListener() {
            @Override
            public void onBandCaloriesChanged(BandCaloriesEvent bandCaloriesEvent) {
                Log.d(TAG, "onBandCaloriesChanged: " + bandCaloriesEvent);
                calories = bandCaloriesEvent.getCalories();
                if (firstCalories) {
                    startCalories = calories;
                    firstCalories = false;
                }
            }
        };
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void startSession() {
        firstDistance = true;
        firstCalories = true;
        startTime = System.currentTimeMillis();
        new SensorSubscriptionTask().execute();
    }

    public void stopSession(String userName) {
        try {
            new SensorUnsubscriptionTask().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        time = System.currentTimeMillis();

        JsonObject data = new JsonObject();
        data.addProperty("userName", userName);
        JsonObject runInfo = new JsonObject();
        runInfo.addProperty("distance", distance - startDistance);
        runInfo.addProperty("speed", (float) (distance - startDistance) / ((time - startTime) / 1000f));
        runInfo.addProperty("calories", calories - startCalories);
        runInfo.addProperty("timestamp", startTime);
        data.add("runInfo", runInfo);
        BuddyAPI.call("submitRun", data);
    }

    private class SensorSubscriptionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    Log.d(TAG, "Registering sensors...");
                    client.getSensorManager().registerDistanceEventListener(distanceEventListener);
                    client.getSensorManager().registerCaloriesEventListener(caloriesEventListener);
                }
            } catch (BandException e) {
                Log.e(TAG, "Error subscribing to band sensors", e);
            }
            return null;
        }
    }

    private class SensorUnsubscriptionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    Log.d(TAG, "Unregistering sensors...");
                    client.getSensorManager().unregisterDistanceEventListener(distanceEventListener);
                    client.getSensorManager().unregisterCaloriesEventListener(caloriesEventListener);
                }
            } catch (BandException e) {
                Log.e(TAG, "Error unsubscribing to band sensors", e);
            }
            return null;
        }
    }

    private boolean getConnectedBandClient() throws BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                Log.e(TAG, "Band isn't paired with your phone.");
                return false;
            }
            client = BandClientManager.getInstance().create(context, devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        Log.i(TAG, "Band is connecting...");
        try {
            return ConnectionState.CONNECTED == client.connect().await();
        } catch (InterruptedException e) {
            return false;
        }
    }
}
