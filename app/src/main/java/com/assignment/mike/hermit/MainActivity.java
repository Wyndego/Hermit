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

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new TweetFragment())
//                    .commit();
//        }

        final Button loginBtn = (Button) findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signIn();
            }
        });

        mUsernameEditText = (EditText)findViewById(R.id.edit_username);
        mPasswordEditText = (EditText)findViewById(R.id.edit_password);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int selectedMenuItem = item.getItemId();
//        switch (selectedMenuItem) {
//            case R.id.compose:
//                Log.d(LOG_TAG, "Compose menu option selected");
//                return true;
//            case R.id.logout:
//                Log.d(LOG_TAG, "Logout menu option selected");
//                return true;
//            default:
//                Log.d(LOG_TAG, "Uknown menu item selected.");
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void signIn() {
        Log.d(LOG_TAG, "Signing in");

        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        // TODO - add user validation against a stubbed out API call.

        // Now go ahead and start the Twitter wall.
        Intent goToTweetWallIntent = new Intent(this, TweetFragment.class);
        startActivity(goToTweetWallIntent);
    }
}
