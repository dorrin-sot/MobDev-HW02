package com.mobdev.locationapp;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdev.locationapp.Model.LocationDB;
import com.mobdev.locationapp.ui.bookmark.BookmarkAdapter;
import com.mobdev.locationapp.ui.map.MapFragment;

import java.lang.ref.WeakReference;

import static android.content.res.Configuration.UI_MODE_NIGHT_MASK;
import static android.content.res.Configuration.UI_MODE_NIGHT_YES;
import static androidx.navigation.Navigation.findNavController;
import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;
import static androidx.navigation.ui.NavigationUI.setupWithNavController;
import static com.mobdev.locationapp.Handler.getHandler;
import static com.mobdev.locationapp.Logger.d;
import static com.mobdev.locationapp.Logger.e;
import static com.mobdev.locationapp.R.id.nav_view;
import static com.mobdev.locationapp.R.id.navigation_bookmark;
import static com.mobdev.locationapp.R.id.navigation_map;
import static com.mobdev.locationapp.R.id.navigation_settings;
import static com.mobdev.locationapp.R.integer.theme_enum_dark;
import static com.mobdev.locationapp.R.integer.theme_enum_default;
import static com.mobdev.locationapp.R.layout.activity_main;
import static com.mobdev.locationapp.R.string.themeDark_title;
import static com.mobdev.locationapp.R.string.theme_title;
import static com.mobdev.locationapp.R.style.DarkLocationApp;
import static com.mobdev.locationapp.R.style.LightLocationApp;

public class MainActivity extends AppCompatActivity {
    static LocationDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        d("");
        handleTheme();
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        new Handler(getMainLooper());
        getHandler().setActivityWeakReference(new WeakReference<>(this));

        BottomNavigationView navView = findViewById(nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                navigation_bookmark, navigation_map, navigation_settings)
                .build();
        NavController navController = findNavController(this, R.id.nav_host_fragment);
        setupActionBarWithNavController(this, navController, appBarConfiguration);
        setupWithNavController(navView, navController);
        initAdapter();
    }

    private void handleTheme()  {
        int themeId = getSharedPreferences("theme_prefs", MODE_PRIVATE)
                .getInt(getString(theme_title), 0);

        boolean goDark;
        if (themeId == getResources().getInteger(theme_enum_default))
            goDark = (getResources().getConfiguration().uiMode & UI_MODE_NIGHT_MASK)
                    == UI_MODE_NIGHT_YES;
        else
            goDark = themeId == getResources().getInteger(theme_enum_dark);
        setTheme(goDark ? DarkLocationApp : LightLocationApp);
        d("theme = " + (goDark ? "Dark" : "Light"));
//        PackageInfo packageInfo = null;
//        try {
//            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        int themeResId = packageInfo.applicationInfo.theme;
//        e("theme= "+getResources().getResourceName(themeResId));
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.name, outValue, true);
        e("theme= "+outValue.string);

    }
    private void initAdapter() {
//        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

//        Logger.d("initRecyclerView: recycler view init");
//        RecyclerView recyclerView = findViewById(R.id.bookMark_RV);

        BookmarkAdapter adapter = new BookmarkAdapter(getApplication());
//        Logger.e("recycler: "+recyclerView.toString());
        Logger.e("adapter: "+adapter.toString());
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        d("");
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