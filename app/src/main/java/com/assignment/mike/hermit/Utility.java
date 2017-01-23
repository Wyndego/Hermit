package com.assignment.mike.hermit;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.Random;

/**
 * Helper class for some general operatin that are need for the app.
 * Created by Mike on 1/23/17.
 */

public class Utility {

    public static void displayMessage(Context context, String title, String message) {
        AlertDialog.Builder mMessageDialog = new AlertDialog.Builder(context, R.style.HermitAlertDialogStyle);
        mMessageDialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Noting to do here.
                    }
                }).show();
    }

    public static boolean simulateNetworkConditions() {
        Random rand = new Random();
        int roll = rand.nextInt(10);

        // 10% chance of network failure.
        return roll == 0;
    }
}
