package com.mobdev.locationapp.ui.map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
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
import com.mobdev.locationapp.MainActivity;
import com.mobdev.locationapp.Model.Location;
import com.mobdev.locationapp.R;
import com.mobdev.locationapp.ui.bookmark.BookmarkAdapter;

import java.util.List;

public class MapFragment extends Fragment implements
        OnMapReadyCallback, PermissionsListener {
    public static MapView mapView;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;

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


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MapFragment.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);

                    }
                });
        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull LatLng point) {
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(point.getLatitude(), point.getLongitude()))
                );
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                EditText editText = new EditText(getActivity());
                builder.setView(editText)
                        .setTitle("Save Location (" + point.getLatitude() + "," + point.getLongitude() + ")")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String name = editText.getText().toString();
                                BookmarkAdapter.addBookmarkMessage(name,point.getLatitude(),point.getLongitude(),"https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.flaticon.com%2Fpremium-icon%2Flocation_1176403&psig=AOvVaw3KK593e2GunqMWVPN1h82Z&ust=1619901219552000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCNiJtcDopvACFQAAAAAdAAAAABAD");
                            }
                        });

                AlertDialog dialog = builder.create();
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

// Set the component's camera mode
            // locationComponent.zoomWhileTracking(17);
            // locationComponent.setCameraMode(CameraMode.TRACKING);
            if(getArguments()==null) {
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude())) // Sets the new camera position
                        .zoom(17) // Sets the zoom
                        .build(); // Creates a CameraPosition from the builder
                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 3000);
            }else {
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