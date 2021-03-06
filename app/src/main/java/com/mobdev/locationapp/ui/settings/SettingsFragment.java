package com.mobdev.locationapp.ui.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import static com.mobdev.locationapp.Handler.Message.DELETE_ALL_DATA;
import static com.mobdev.locationapp.Handler.Message.SET_THEME;
import static com.mobdev.locationapp.Handler.getHandler;
import static com.mobdev.locationapp.R.string.delete_data;
import static com.mobdev.locationapp.R.string.themeDark_title;
import static com.mobdev.locationapp.R.xml.settings;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSoftKeyboardIfOpen();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(settings, rootKey);

        findPreference(getString(themeDark_title)).setOnPreferenceChangeListener(this);

        findPreference(getString(delete_data)).setOnPreferenceClickListener(this);
    }

    private void hideSoftKeyboardIfOpen() {
        Activity activity = getActivity();
        if (activity.getCurrentFocus() == null)
            return;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(delete_data))) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("Delete Data?")
                    .setMessage("Are you sure you want to delete all bookmarks?")
                    .setPositiveButton("Erase", (dialog, which) ->
                            getHandler().sendMessage(
                                    getHandler().obtainMessage(DELETE_ALL_DATA.ordinal())
                            ))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .create();
            alertDialog.show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(themeDark_title))) {
            getHandler().sendMessage(
                    getHandler().obtainMessage(
                            SET_THEME.ordinal(), // set app theme
                            1, // true -> in settings page
                            0
                    )
            );
            return true;
        }
        return false;
    }
}