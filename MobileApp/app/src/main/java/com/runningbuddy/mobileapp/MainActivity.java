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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.JsonElement;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();

    @Bind(R.id.userNameField)
    EditText userNameField;
    @Bind(R.id.loginButton)
    Button loginButton;
    @Bind(R.id.sessionButton)
    ToggleButton sessionButton;
    @Bind(R.id.findBuddiesButton)
    Button findBuddiesButton;
    @Bind(R.id.buddyMatchesListView)
    ListView buddyMatchesListView;

    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SessionManager.getInstance().setContext(this);
        CreateTileTask ctt = new CreateTileTask();
        ctt.setActivity(this);
        ctt.execute();
        userName = null;
        buddyMatchesListView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(tileReceiver);
    }

    protected BroadcastReceiver tileReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TileEvent.ACTION_TILE_OPENED)) {
                findBuddies();
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
        if (res == null) return;
        buddyMatchesListView.setVisibility(View.VISIBLE);
        List<JsonObject> matches = new ArrayList<>();
        Iterator<JsonElement> matchesIterator = res.getAsJsonArray("matches").iterator();
        JsonObject match;
        while (matchesIterator.hasNext()) {
            match = matchesIterator.next().getAsJsonObject();
            matches.add(match);
        }
        buddyMatchesListView.setAdapter(new ArrayAdapter<JsonObject>(this, R.layout.list_item_buddy_match, matches) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) MainActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.list_item_buddy_match, parent, false);
                TextView buddyName = (TextView) rowView.findViewById(R.id.buddyName);
                TextView buddyWeight = (TextView) rowView.findViewById(R.id.buddyWeight);

                buddyName.setText(getItem(position).get("userName").getAsString());
                buddyWeight.setText(String.format("%3.1f", getItem(position).get("weight").getAsFloat()));

                return rowView;
            }

        });
    }
}
