package com.mobdev.locationapp.ui.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import static com.mobdev.locationapp.Handler.Message.DELETE_ALL_DATA;
import static com.mobdev.locationapp.Handler.getHandler;
import static com.mobdev.locationapp.R.string.delete_data;
import static com.mobdev.locationapp.R.string.themeLight_title;
import static com.mobdev.locationapp.R.xml.settings;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(settings, rootKey);

        findPreference(getString(themeLight_title)).setOnPreferenceChangeListener(this);

        findPreference(getString(delete_data)).setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(delete_data))) {
            getHandler().sendMessage(
                    getHandler().obtainMessage(DELETE_ALL_DATA.ordinal())
            );
            return false;
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(themeLight_title))) {
            Boolean goDark = (Boolean) newValue;

            // TODO: 4/25/21
            return false;
        }
        return true;
    }
}