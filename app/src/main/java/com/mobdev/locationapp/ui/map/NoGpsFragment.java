package com.mobdev.locationapp.ui.map;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.mobdev.locationapp.R;

import static android.content.Context.LOCATION_SERVICE;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
import static com.mobdev.locationapp.Logger.d;
import static com.mobdev.locationapp.R.id.action_navigation_load_map_again;

public class NoGpsFragment extends Fragment {
    public NoGpsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        d("called");
        View view = inflater.inflate(R.layout.fragment_no_gps, container, false);

        View tryAgainBtn = view.findViewById(R.id.try_again_btn);
        tryAgainBtn.setOnClickListener(v -> {
            d("isGpsTurnedOn = " + !isGpsTurnedOff());
            if (!isGpsTurnedOff())
                Navigation.findNavController(getView()).navigate(action_navigation_load_map_again);
        });
        view.findViewById(R.id.turn_gps_on_btn).setOnClickListener(v -> {
            Intent intent = new Intent(ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });

        return view;
    }

    private boolean isGpsTurnedOff() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        assert locationManager != null;
        return !locationManager.isProviderEnabled(GPS_PROVIDER);
    }
}