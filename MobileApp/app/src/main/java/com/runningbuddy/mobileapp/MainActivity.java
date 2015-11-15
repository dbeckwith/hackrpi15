package com.runningbuddy.mobileapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.google.gson.JsonObject;

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

    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SessionManager.getInstance().setContext(this);
        userName = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
}
