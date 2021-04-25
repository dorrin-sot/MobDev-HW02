package com.mobdev.locationapp.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mobdev.locationapp.R;
import static com.mobdev.locationapp.ui.bookmark.BookmarkAdapter.addPlace;
public class SettingsFragment extends Fragment {
    private Button button;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_settings, container, false);
//        button=view.findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addPlace("https://i.redd.it/tpsnoz5bzo501.jpg","sample name","sample coor");
//            }
//        });
        return view;
    }
}