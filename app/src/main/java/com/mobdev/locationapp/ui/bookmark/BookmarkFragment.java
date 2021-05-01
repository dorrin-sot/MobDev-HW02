package com.mobdev.locationapp.ui.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdev.locationapp.Logger;
import com.mobdev.locationapp.R;

import java.util.ArrayList;
import java.util.Locale;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.mobdev.locationapp.Logger.d;
import static com.mobdev.locationapp.Logger.e;

public class BookmarkFragment extends Fragment {
    private static final int REQUESR_SPEACH_CODE = 1000;
    private TextView bookmarkSearch_textview;
    private ImageButton bookmarkSearch_voice_btn;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Logger.e("oncreateview gets called");
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        init(view);
        bookmarkSearch_textview = view.findViewById(R.id.bookmark_search_text);
        bookmarkSearch_voice_btn= view.findViewById(R.id.bookmark_search_voice_btn);
        bookmarkSearch_textview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                BookmarkAdapter.searchMessage(s.toString());
            }
        });
        bookmarkSearch_voice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lunchSpeakIntent(v);
            }
        });
        return view;
    }

    private void lunchSpeakIntent(View view) {
        boolean hasMicrophonePermission = hasMicrophonePermission();
        e("called - hasMicrophonePermission = " + hasMicrophonePermission);
        if (hasMicrophonePermission) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "please name your desired location");
            try {
                startActivityForResult(intent, REQUESR_SPEACH_CODE);
            } catch (Exception e) {
                Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            askForMicrophonePermission();
        }
    }

    private void askForMicrophonePermission() {
        requestPermissions(new String[]{RECORD_AUDIO}, 0);
    }

    private boolean hasMicrophonePermission() {
        return checkSelfPermission(getContext(), RECORD_AUDIO) == PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (grantResults[0]) {
            case 0: // RECORD_AUDIO
                bookmarkSearch_voice_btn.performClick();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void init(View view){
        d("initImageBitmaps: preparing bitmaps.");
        initRecyclerView(view);
        getBookmarkFromSQL();


    }

    private void getBookmarkFromSQL() {
        BookmarkAdapter.updateBookmarkListMessage();
    }



    private void initRecyclerView(View view) {
        Logger.d("initRecyclerView: recycler view init");
        RecyclerView recyclerView = view.findViewById(R.id.bookMark_RV);
        recyclerView.setAdapter(BookmarkAdapter.adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUESR_SPEACH_CODE:{
                if(resultCode==RESULT_OK && data!=null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    bookmarkSearch_textview.setText(result.get(0));
                }
                break;
            }

        }

    }
}