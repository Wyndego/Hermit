package com.assignment.mike.hermit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final Button loginBtn = (Button) findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signIn();
            }
        });

        mUsernameEditText = (EditText)findViewById(R.id.edit_username);
        mPasswordEditText = (EditText)findViewById(R.id.edit_password);
    }

    private void signIn() {
        Log.d(LOG_TAG, "Signing in");

        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        // TODO - add user validation against a stubbed out API call.

        // Now go ahead and start the Twitter wall.
        Intent goToTweetWallIntent = new Intent(this, TwitterWallActivity.class);
        startActivity(goToTweetWallIntent);
    }
}
