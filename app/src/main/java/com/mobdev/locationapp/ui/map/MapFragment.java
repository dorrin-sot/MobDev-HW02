package com.mobdev.locationapp.ui.map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView;
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mobdev.locationapp.R;
import com.mobdev.locationapp.ui.bookmark.BookmarkAdapter;

import java.util.Arrays;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.os.Looper.getMainLooper;
import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.mapbox.android.core.location.LocationEngineProvider.getBestLocationEngine;
import static com.mapbox.android.core.location.LocationEngineRequest.PRIORITY_HIGH_ACCURACY;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mobdev.locationapp.Logger.d;
import static com.mobdev.locationapp.Logger.e;
import static com.mobdev.locationapp.R.drawable.ic_baseline_gps_off_24;
import static com.mobdev.locationapp.R.id.action_navigation_no_gps;
import static com.mobdev.locationapp.R.id.speed_card_view;
import static com.mobdev.locationapp.R.id.speed_text_view;
import static com.mobdev.locationapp.R.layout.speed_marker;

public class MapFragment extends Fragment implements OnMapReadyCallback, PermissionsListener {
    public static MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MaterialTextView speedTextView;
    private LocationEngine locationEngine;
    private LocationChangeListeningLocationCallback callback = new LocationChangeListeningLocationCallback();
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    public static boolean isSearch=true;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));
        View view = inflater.inflate(R.layout.fragment_map, container, false);
// Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.
        // Mapbox.getInstance(MainActivity.activity, getString(R.string.mapbox_access_token));

// This contains the MapView in XML and needs to be called after the access token is configured.
        // setContentView(R.layout.activity_location_component);

        mapView = view.findViewById(R.id.mapView);
        speedTextView = ((CardView) mapView.findViewById(speed_card_view)).findViewById(speed_text_view);

        hideSoftKeyboardIfOpen();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        // return inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    private void hideSoftKeyboardIfOpen() {
        Activity activity = getActivity();
        if (activity.getCurrentFocus() == null)
            return;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void initSearchFab() {
        EditText searchText =getActivity().findViewById(R.id.map_search_text);
        searchText.setInputType(InputType.TYPE_NULL);
        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(getActivity());
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})
        ));
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MapFragment.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        initSearchFab();
                        enableLocationComponent(style);
                        //    style.addImage(symbolIconId, BitmapFactory.decodeResource(
                        //           getResources(), R.drawable.ic_baseline_location_on_24));

// Create an empty GeoJSON source using the empty feature collection
                        setUpSource(style);

// Set up a new symbol layer for displaying the searched location's feature coordinates
                        setupLayer(style);


                    }
                });
        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull LatLng point) {
                mapboxMap.clear();
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(point.getLatitude(), point.getLongitude()))
                );
                Builder builder = new Builder(getActivity());

                EditText editText = new EditText(getActivity());
                builder.setView(editText)
                        .setTitle("Save Location (" + point.getLatitude() + "," + point.getLongitude() + ")")
                        .setMessage("Location Name")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String name = editText.getText().toString();
                                BookmarkAdapter.addBookmarkMessage(name, point.getLatitude(), point.getLongitude(), "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.flaticon.com%2Fpremium-icon%2Flocation_1176403&psig=AOvVaw3KK593e2GunqMWVPN1h82Z&ust=1619901219552000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCNiJtcDopvACFQAAAAAdAAAAABAD");
                            }
                        });

                AlertDialog dialog = builder.create();
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                wlp.gravity = Gravity.BOTTOM;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                dialog.show();
                return true;
            }
        });

    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
//            LocationComponentOptions locationComponentOptions = builder(getContext())
//                    .foregroundName("nothing")
//                    .backgroundName("nothing")
//                    .foregroundStaleName("nothing")
//                    .backgroundStaleName("nothing")
//                    .build();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(getContext(), loadedMapStyle)
                            .useDefaultLocationEngine(false)
//                            .locationComponentOptions(locationComponentOptions)
                            .build()
            );

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            FloatingActionButton goToMyLocation = getActivity().findViewById(R.id.goToMyLocationFab);
            goToMyLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CameraPosition position = new CameraPosition.Builder()
                            .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude())) // Sets the new camera position
                            .zoom(17) // Sets the zoom
                            .build(); // Creates a CameraPosition from the builder

                    mapboxMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(position), 3000);
                }
            });

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            iniLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    private void initLocationAndCameraPosition(LocationComponent locationComponent, Location location) {
        // Set the component's camera mode
        // locationComponent.zoomWhileTracking(17);
        // locationComponent.setCameraMode(CameraMode.TRACKING);
        if (!isSearch&&getArguments() == null) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude())) // Sets the new camera position
                    .zoom(17) // Sets the zoom
                    .build(); // Creates a CameraPosition from the builder
            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 3000);
        } else if(!isSearch&&getArguments() != null) {
            mapboxMap.addMarker(new MarkerOptions()
                    .position(new LatLng(getArguments().getFloat("bookmark_x"), getArguments().getFloat("bookmark_y"))));
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(getArguments().getFloat("bookmark_x"), getArguments().getFloat("bookmark_y"))) // Sets the new camera position
                    .zoom(17) // Sets the zoom
                    .build(); // Creates a CameraPosition from the builder

            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 3000);
        }
        isSearch=false;
    }

    private boolean isGpsTurnedOff() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        assert locationManager != null;
        return !locationManager.isProviderEnabled(GPS_PROVIDER);
    }

    private void showNeedGpsDialog() {
        AlertDialog alertDialog = new Builder(getContext())
                .setTitle("GPS required")
                .setMessage("Your GPS seems to be turned off. GPS connection is required to view map.")
                .setPositiveButton("Try Again", (dialog, which) -> {
                    if (isGpsTurnedOff())
                        Navigation.findNavController(getView()).navigate(action_navigation_no_gps);
                    else
                        onStart();
                    dialog.dismiss();
                })
                .setNegativeButton("Dismiss", (dialog, which) -> {
                    if (isGpsTurnedOff())
                        Navigation.findNavController(getView()).navigate(action_navigation_no_gps);
                    else
                        onStart();
                    dialog.dismiss();
                })
                .setIcon(ic_baseline_gps_off_24)
                .create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    private void showCurrentLocationAndSpeed() {
        MarkerView markerView;
        MarkerViewManager markerViewManager;
        LocationComponent locationComponent = mapboxMap.getLocationComponent();

        // Initialize the MarkerViewManager
        markerViewManager = new MarkerViewManager(mapView, mapboxMap);

        // Use an XML layout to create a View object
        View customView = LayoutInflater.from(getContext()).inflate(
                speed_marker, null);
        customView.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        MaterialTextView speedTextView = customView.findViewById(R.id.speed);

        // Use the View to create a MarkerView which will eventually be given to
        // the plugin's MarkerViewManager class
        double offsetLat = 0.00019, offsetLong = -0.00013;
        markerView = new MarkerView(new LatLng(
                locationComponent.getLastKnownLocation().getLatitude() + offsetLat,
                locationComponent.getLastKnownLocation().getLongitude() + offsetLong
        ), customView);
        markerViewManager.addMarker(markerView);

        // update location and speed
        locationComponent.addOnIndicatorPositionChangedListener(point -> {
                    float speed = locationComponent
                            .getLastKnownLocation()
                            .getSpeed();
                    speedTextView.setText(speed + " m/s");
                    markerView.setLatLng(new LatLng(
                            point.latitude() + offsetLat,
                            point.longitude() + offsetLong
                    ));
                }
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            isSearch=true;
// Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

// Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
// Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }
                    mapboxMap.addMarker(new MarkerOptions()
                            .position(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                    ((Point) selectedCarmenFeature.geometry()).longitude())));

// Move map camera to the selected location
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getContext(), R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(getContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (locationEngine != null)
            locationEngine.removeLocationUpdates(callback);

        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @SuppressLint("MissingPermission")
    private void iniLocationEngine () {
        locationEngine = getBestLocationEngine(getContext());

        long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L,
                DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
                .build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    private class LocationChangeListeningLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        @SuppressLint("MissingPermission")
        @Override
        public void onSuccess(LocationEngineResult result) {
            Location location = result.getLastLocation();

            if (location == null) return;

            initLocationAndCameraPosition(mapboxMap.getLocationComponent(), location);

            speedTextView.setText(String.format("%.1f", location.getSpeed()));
            e("speed = " + speedTextView.getText());

            // Pass the new location to the Maps SDK's LocationComponent
            if (mapboxMap != null)
                mapboxMap.getLocationComponent().forceLocationUpdate(location);
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            e(Arrays.toString(exception.getStackTrace()));
            if (isGpsTurnedOff())
                showNeedGpsDialog();
        }
    }
}