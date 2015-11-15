package com.runningbuddy.mobileapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.google.gson.JsonObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();

    @Bind(R.id.userNameField)
    EditText userNameField;
    @Bind(R.id.addUserButton)
    Button addUserButton;
    @Bind(R.id.sessionButton)
    ToggleButton sessionButton;

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

    @OnClick(R.id.addUserButton)
    public void addUser() {
        JsonObject req = new JsonObject();
        req.addProperty("userName", userName = userNameField.getText().toString());
        BuddyAPI.call("addUser", req);
        userNameField.setEnabled(false);
        addUserButton.setEnabled(false);
    }

    @OnClick(R.id.sessionButton)
    public void toggleSession(ToggleButton button) {
        if (button.isChecked()) {
            SessionManager.getInstance().startSession();
        } else {
            SessionManager.getInstance().stopSession(userName);
        }
    }
}
