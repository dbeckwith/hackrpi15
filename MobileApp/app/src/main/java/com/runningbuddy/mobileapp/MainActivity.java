package com.runningbuddy.mobileapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();

    public static final MediaType JSONType = MediaType.parse("application/json; charset=utf-8");

    @Bind(R.id.userNameField)
    EditText userNameField;
    @Bind(R.id.addUserButton)
    Button addUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        final OkHttpClient client = new OkHttpClient();

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        RequestBody body = RequestBody.create(JSONType, String.format("{\"userName\":\"%s\"}", params[0]));
                        Request request = new Request.Builder()
                                .url("http://activitybuddy.cloudapp.net:8080/addUser")
                                .post(body)
                                .build();
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();
                            Log.d(TAG, response.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(userNameField.getText().toString());
            }
        });
    }
}
