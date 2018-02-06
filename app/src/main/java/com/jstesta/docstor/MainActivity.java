package com.jstesta.docstor;

import android.os.Bundle;
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Log.d(TAG, "onNavigationItemSelected");

        FileListFragment currentFragment = (FileListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        FragmentTransaction fragmentTransaction;

        switch (item.getItemId()) {
            case R.id.documents:
                if (currentFragment != null && currentFragment.getMediaType() == MediaType.DOCUMENTS) {
                    return true;
                }
                Fragment docFragment = FileListFragment.newInstance(MediaType.DOCUMENTS);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, docFragment, "docFragment");
                fragmentTransaction.commit();
                return true;

            case R.id.music:
                if (currentFragment != null && currentFragment.getMediaType() == MediaType.MUSIC) {
                    return true;
                }
                Fragment musicFragment = FileListFragment.newInstance(MediaType.MUSIC);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, musicFragment, "musicFragment");
                fragmentTransaction.commit();
                return true;

            case R.id.pictures:
                if (currentFragment != null && currentFragment.getMediaType() == MediaType.PICTURES) {
                    return true;
                }
                Fragment picsFragment = FileListFragment.newInstance(MediaType.PICTURES);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, picsFragment, "picsFragment");
                fragmentTransaction.commit();
                return true;
        }

        return false;
    }
}
