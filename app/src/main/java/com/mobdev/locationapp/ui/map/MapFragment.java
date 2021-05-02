package com.mobdev.locationapp.ui.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mobdev.locationapp.MainActivity;
import com.mobdev.locationapp.Model.Location;
import com.mobdev.locationapp.R;
import com.mobdev.locationapp.ui.bookmark.BookmarkAdapter;

import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MapFragment extends Fragment implements
        OnMapReadyCallback, PermissionsListener {
    public static MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private LatLng currentLocation;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

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
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        // return inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    private void initSearchFab() {
        getActivity().findViewById(R.id.fab_location_search).setOnClickListener(new View.OnClickListener() {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);
            currentLocation = new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude());
// Set the component's camera mode
            // locationComponent.zoomWhileTracking(17);
            // locationComponent.setCameraMode(CameraMode.TRACKING);
            if (getArguments() == null) {
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude())) // Sets the new camera position
                        .zoom(17) // Sets the zoom
                        .build(); // Creates a CameraPosition from the builder
                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 3000);
            } else {
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(getArguments().getFloat("bookmark_x"), getArguments().getFloat("bookmark_y"))));
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(getArguments().getFloat("bookmark_x"), getArguments().getFloat("bookmark_y"))) // Sets the new camera position
                        .zoom(17) // Sets the zoom
                        .build(); // Creates a CameraPosition from the builder

                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 3000);
            }

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
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

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
            // finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

}