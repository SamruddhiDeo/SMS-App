package com.example.smsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

public class PermissionUtils {
    public static void showPermissionDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Please enable SMS and contacts permissions in settings to continue.")
                .setCancelable(false)
                .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openAppSettings(activity);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private static void openAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
}
