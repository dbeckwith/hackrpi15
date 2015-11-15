package com.runningbuddy.mobileapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.tiles.BandIcon;
import com.microsoft.band.tiles.BandTile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by Aditya on 11/15/2015.
 */
public class CreateTileTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = CreateTileTask.class.getCanonicalName();

    private BandClient client;
    private Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean getConnectedBandClient() throws BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                Log.e(TAG, "Band isn't paired with your phone.");
                return false;
            }
            client = BandClientManager.getInstance().create(activity, devices[0]);
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

    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (getConnectedBandClient()) {
                Log.d(TAG, "Registering tile...");

                try {
                    List<BandTile> tiles = client.getTileManager().getTiles().await();
                } catch (BandException e) {
                } catch (InterruptedException e) {
                }

                try {
                    int tileCapacity = client.getTileManager().getRemainingTileCapacity().await();
                    Log.d("Tile capacity", tileCapacity + "");
                } catch (BandException e) {
                } catch (InterruptedException e) {
                }

                Bitmap smallIconBitmap = null;
                try {
                    smallIconBitmap = BitmapFactory.decodeStream(activity.getAssets().open("24sizesmiley.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BandIcon smallIcon = BandIcon.toBandIcon(smallIconBitmap);

                Bitmap tileIconBitmap = null;
                try {
                    tileIconBitmap = BitmapFactory.decodeStream(activity.getAssets().open("46sizesmiley.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BandIcon tileIcon = BandIcon.toBandIcon(tileIconBitmap);

                UUID tileUuid = UUID.randomUUID();//fromString("1c8ef123-caa5-4670-a3cc-9a55e300fef7");

                BandTile tile = new BandTile.Builder(tileUuid, "Activity Buddy", tileIcon).setTileSmallIcon(smallIcon).build();

                try {
                    if (client.getTileManager().addTile(activity, tile).await()) {
                        Log.d(TAG, "Adding tile with main activity.");
                    }
                } catch (BandException e) {
                } catch (InterruptedException e) {
                }
            }
        } catch (BandException e) {
            Log.e(TAG, "Error creating tile", e);
        }
        return null;
    }
}
