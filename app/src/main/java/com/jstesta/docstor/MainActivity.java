package com.jstesta.docstor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jstesta.docstor.core.enums.MediaType;
import com.jstesta.docstor.gui.fragment.FileListFragment;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private BroadcastReceiver mExternalStorageReceiver;
    private boolean mExternalStorageAvailable = false;
    private boolean mExternalStorageWriteable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.documents);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        startWatchingExternalStorage();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");

        if (mExternalStorageReceiver != null) {
            stopWatchingExternalStorage();
        }
    }

    private void updateExternalStorageState() {
        Log.d(TAG, "updateExternalStorageState");

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        handleExternalStorageState(mExternalStorageAvailable, mExternalStorageWriteable);
    }

    private void handleExternalStorageState(boolean mExternalStorageAvailable, boolean mExternalStorageWriteable) {

    }

    private void startWatchingExternalStorage() {
        mExternalStorageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "Storage: " + intent.getData());
                updateExternalStorageState();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        registerReceiver(mExternalStorageReceiver, filter);
        updateExternalStorageState();
    }

    private void stopWatchingExternalStorage() {
        unregisterReceiver(mExternalStorageReceiver);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        FragmentTransaction fragmentTransaction;

        switch (item.getItemId()) {
            case R.id.documents:
                Fragment docFragment = FileListFragment.newInstance(MediaType.DOCUMENTS);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, docFragment, "docFragment");
                fragmentTransaction.commit();
                return true;
            case R.id.music:
                Fragment musicFragment = FileListFragment.newInstance(MediaType.MUSIC);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, musicFragment, "musicFragment");
                fragmentTransaction.commit();
                return true;
            case R.id.pictures:
                Fragment picsFragment = FileListFragment.newInstance(MediaType.PICTURES);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, picsFragment, "picsFragment");
                fragmentTransaction.commit();
                return true;
        }

        return false;
    }
}
