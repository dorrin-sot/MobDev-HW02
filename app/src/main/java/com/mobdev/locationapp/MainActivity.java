package com.mobdev.locationapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdev.locationapp.Model.LocationDB;
import com.mobdev.locationapp.ui.map.MapFragment;

import static com.mobdev.locationapp.Logger.d;

public class MainActivity extends AppCompatActivity {
    static LocationDB db;

    public static AppCompatActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity=this;
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_bookmark, R.id.navigation_map, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();
         MapFragment.mapView.onStart();
        if (db == null) {
            db = Room.databaseBuilder(getApplicationContext(),
                    LocationDB.class, "bookmark-database")
                    .build();
            d("Initialized database");
        }
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