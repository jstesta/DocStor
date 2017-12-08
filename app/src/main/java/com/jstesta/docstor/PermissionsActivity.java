package com.jstesta.docstor;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class PermissionsActivity extends AppCompatActivity {
    private static final String TAG = "PermissionsActivity";

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private Snackbar permissionsSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkFirstRun();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        showPermissionDeniedBar();
    }

    private boolean hasPermission(String perm) {
        return ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED;
    }

    private void askUserPermissions() {
        boolean hasStoragePerm = hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!hasStoragePerm) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d(TAG, "askUserPermissions: shouldShowRequestPermissionRationale");

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                Log.d(TAG, "askUserPermissions: no explanation needed");
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


    private void showPermissionDeniedBar() {
        if (permissionsSnackbar != null) {
            permissionsSnackbar.show();
            return;
        }

        permissionsSnackbar = Snackbar.make(
                findViewById(R.id.coordinator_layout),
                "Missing core application permissions.",
                Snackbar.LENGTH_INDEFINITE);
        permissionsSnackbar.setAction("settings", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "permissionsSnackbar onClick: ask me");

                if (shouldShowPermRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    openApplicationSettingsActivity();
                } else {
                    askUserPermissions();
                }
            }
        });
        permissionsSnackbar.show();
    }

    private boolean shouldShowPermRationale(String perm) {
        return ActivityCompat.shouldShowRequestPermissionRationale(PermissionsActivity.this, perm);
    }

    private void checkFirstRun() {
        final String PREFS_NAME = "DocStorPreferences";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {

            // TODO This is a new install (or the user cleared the shared preferences)
            askUserPermissions();

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    private void openApplicationSettingsActivity() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}
