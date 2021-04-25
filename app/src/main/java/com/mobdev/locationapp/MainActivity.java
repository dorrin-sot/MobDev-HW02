package com.mobdev.locationapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdev.locationapp.Model.LocationDB;
import com.mobdev.locationapp.ui.map.MapFragment;

import java.lang.ref.WeakReference;

import static androidx.navigation.Navigation.findNavController;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;
import static androidx.navigation.ui.NavigationUI.setupWithNavController;
import static com.mobdev.locationapp.Handler.getHandler;
import static com.mobdev.locationapp.Logger.d;
import static com.mobdev.locationapp.R.id.navigation_bookmark;
import static com.mobdev.locationapp.R.id.navigation_map;
import static com.mobdev.locationapp.R.id.navigation_settings;

public class MainActivity extends AppCompatActivity {
    static LocationDB db;

    public static AppCompatActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity=this;
        new Handler(getMainLooper());
        getHandler().setActivityWeakReference(new WeakReference<>(this));

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                navigation_bookmark, navigation_map, navigation_settings)
                .build();
        NavController navController = findNavController(this, R.id.nav_host_fragment);
        setupActionBarWithNavController(this, navController, appBarConfiguration);
        setupWithNavController(navView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
         MapFragment.mapView.onStart();
        if (db == null) {
            initDB();
        }
    }

    private void initDB() {
        db = Room.databaseBuilder(getApplicationContext(),
                LocationDB.class, "bookmark-database")
                .build();
        d("Initialized database");
    }
    @Override
    protected void onResume() {
        super.onResume();
        MapFragment.mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MapFragment.mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MapFragment.mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MapFragment.mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        MapFragment.mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapFragment.mapView.onDestroy();
    }

}