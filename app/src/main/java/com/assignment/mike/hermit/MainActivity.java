package com.assignment.mike.hermit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String LOGIN_ERROR = "Login Error";
    private static final String NETWORK_ERROR = "Network Error";

    // Simulate a datastore with valid username and login credentials.
    private static final HashMap<String, String> validUserMap = new HashMap<String, String>();
    private static final String USERNAME = "gooduser@success.com";
    private static final String PASSWORD = "success";
    static {
        validUserMap.put(USERNAME, PASSWORD);
    }
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;

    AlertDialog.Builder mMessageDialog;

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

    // Simulate a call to an authorization service while also adding in
    // some potential failuer conditions for login. (i.e. timeout)
    private void signIn() {

        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        // Simulate a call to an authorization service to validate a user.
        if (simulateNetworkConditions()) {
            displayMessage(NETWORK_ERROR, "Failed to reach server. Please try again.");
            return;
        }

        // Have a network connection...proceed with login.
        boolean successLogin = validateUser(username, password);
        if (successLogin) {
            // Now go ahead and start the Twitter wall.
            Intent goToTweetWallIntent = new Intent(this, TwitterWallActivity.class);
            startActivity(goToTweetWallIntent);
        }
    }

    private boolean validateUser(String username, String password) {
        if (username == null || username.length() == 0 ||
                password == null || password.length() == 0) {
            displayMessage(LOGIN_ERROR, "Please provide both a username and a password");
            return false;
        }

        if (!validUserMap.containsKey(username)) {
            displayMessage(LOGIN_ERROR, "Invalid username: " + username);
            return false;
        }

        String retrievedPassword = validUserMap.get(username);
        if (retrievedPassword == null || !retrievedPassword.equals(password)) {
            displayMessage(LOGIN_ERROR, "Password does not match stored value for " + username);
            return false;
        }

        return true;
    }

    private void displayMessage(String title, String message) {
        AlertDialog.Builder mMessageDialog = new AlertDialog.Builder(this);
        mMessageDialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Noting to do here.
                    }
                }).show();
    }

    private boolean simulateNetworkConditions() {
        Random rand = new Random();
        int roll = rand.nextInt(10);

        // 10% chance of network failure.
        return roll == 0;
    }
}
