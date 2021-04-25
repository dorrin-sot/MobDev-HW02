package com.mobdev.locationapp.ui.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import static com.mobdev.locationapp.R.string.themeLight_title;
import static com.mobdev.locationapp.R.xml.settings;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(settings, rootKey);

        findPreference(getString(themeLight_title)).setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        return false;
    }
}

enum Theme {
    DARK,
    LIGHT
}
//package com.mobdev.locationapp.ui.settings;
//
//import android.os.Bundle;
//<<<<<<< HEAD
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//=======
//>>>>>>> b83e6fc534d3c201b8565988a903700186f509e4
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.preference.Preference;
//import androidx.preference.PreferenceFragmentCompat;
//
//<<<<<<< HEAD
//import com.mobdev.locationapp.R;
//import static com.mobdev.locationapp.ui.bookmark.BookmarkAdapter.addPlace;
//public class SettingsFragment extends Fragment {
//    private Button button;
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        View view= inflater.inflate(R.layout.fragment_settings, container, false);
////        button=view.findViewById(R.id.button);
////        button.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                addPlace("https://i.redd.it/tpsnoz5bzo501.jpg","sample name","sample coor");
////            }
////        });
//        return view;
//=======
//import static com.mobdev.locationapp.R.string.themeLight_title;
//import static com.mobdev.locationapp.R.xml.settings;
//
//public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
//    @Override
//    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//        setPreferencesFromResource(settings, rootKey);
//
//        findPreference(getString(themeLight_title)).setOnPreferenceChangeListener(this);
//>>>>>>> b83e6fc534d3c201b8565988a903700186f509e4
//    }
//
//    @Override
//    public boolean onPreferenceChange(Preference preference, Object newValue) {
//
//        return false;
//    }
//}
//
//enum Theme {
//    DARK,
//    LIGHT
//}