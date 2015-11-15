package com.runningbuddy.mobileapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.google.gson.JsonObject;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.tiles.BandIcon;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.TileEvent;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private BandClient client;

    @Bind(R.id.userNameField)
    EditText userNameField;
    @Bind(R.id.loginButton)
    Button loginButton;
    @Bind(R.id.sessionButton)
    ToggleButton sessionButton;
    @Bind(R.id.findBuddiesButton)
    Button findBuddiesButton;

    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SessionManager.getInstance().setContext(this);
        new CreateTileTask();
        userName = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(tileReceiver);
    }

    protected BroadcastReceiver tileReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == TileEvent.ACTION_TILE_OPENED) {
                findBuddies();
            } else if (intent.getAction() == TileEvent.ACTION_TILE_BUTTON_PRESSED) {
            } else if (intent.getAction() == TileEvent.ACTION_TILE_CLOSED) {
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(TileEvent.ACTION_TILE_OPENED);
        filter.addAction(TileEvent.ACTION_TILE_BUTTON_PRESSED);
        filter.addAction(TileEvent.ACTION_TILE_CLOSED);
        registerReceiver(tileReceiver, filter);
    }

    @OnClick(R.id.loginButton)
    public void addUser() {
        JsonObject req = new JsonObject();
        req.addProperty("userName", userName = userNameField.getText().toString());
        BuddyAPI.call("login", req);
        userNameField.setEnabled(false);
        loginButton.setEnabled(false);
    }

    @OnClick(R.id.sessionButton)
    public void toggleSession(ToggleButton button) {
        if (button.isChecked()) {
            SessionManager.getInstance().startSession();
        } else {
            SessionManager.getInstance().stopSession(userName);
        }
    }

    @OnClick(R.id.findBuddiesButton)
    public void findBuddies() {
        JsonObject req = new JsonObject();
        req.addProperty("userName", userName);
        JsonObject res = BuddyAPI.call("getBuddyMatches", req);
        Log.d(TAG, res + "");
    }

    private boolean getConnectedBandClient() throws BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                Log.e(TAG, "Band isn't paired with your phone.");
                return false;
            }
            client = BandClientManager.getInstance().create(this, devices[0]);
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

    private class CreateTileTask extends AsyncTask<Void, Void, Void> {

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
                        smallIconBitmap = BitmapFactory.decodeStream(getAssets().open("24sizesmiley.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    BandIcon smallIcon = BandIcon.toBandIcon(smallIconBitmap);

                    Bitmap tileIconBitmap = null;
                    try {
                        tileIconBitmap = BitmapFactory.decodeStream(getAssets().open("46sizesmiley.png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    BandIcon tileIcon = BandIcon.toBandIcon(tileIconBitmap);

                    UUID tileUuid = UUID.fromString("1c8ef123-caa5-4670-a3cc-9a55e300fef7");

                    BandTile tile = new BandTile.Builder(tileUuid, "TileName", tileIcon).setTileSmallIcon(smallIcon).build();

                    try {
                        if (client.getTileManager().addTile(MainActivity.this, tile).await()) {
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
}
