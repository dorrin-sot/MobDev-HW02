package com.mobdev.locationapp.ui.bookmark;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdev.locationapp.Logger;
import com.mobdev.locationapp.Model.Location;
import com.mobdev.locationapp.R;

import java.util.ArrayList;

public class BookmarkFragment extends Fragment {
    private TextView bookmarkSearch;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Logger.e("oncreateview gets called");
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        init(view);
        bookmarkSearch= view.findViewById(R.id.bookmark_search_text);
        bookmarkSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        return view;
    }


    private void filter(String text) {
        ArrayList<Location> filteredList = new ArrayList<>();
        for(Location location : BookmarkAdapter.fullLocationList ){
            if (location.getLocationName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(location);
            }
        }
        BookmarkAdapter.adapter.filterList(filteredList);

    }

    private void init(View view){
        Logger.d("initImageBitmaps: preparing bitmaps.");
        clearBookmarkList();
        initRecyclerView(view);
        getBookmarkFromSQL();


    }

    private void getBookmarkFromSQL() {
        BookmarkAdapter.addPlace(new Location("Havasu Falls"
                ,2,1,"https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"));
        BookmarkAdapter.addPlace(new Location("fasdfFalls"
                ,2,1,"https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"));
        BookmarkAdapter.addPlace(new Location("dfgu Falls"
                ,2,1,"https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"));
        BookmarkAdapter.addPlace(new Location("klk"
                ,2,1,"https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg"));
        BookmarkAdapter.addPlace(new Location("fhrg"
                ,2,1,"https://i.imgur.com/ZcLLrkY.jpg"));
        BookmarkAdapter.addPlace(new Location("Hava fgfg"
                ,2,1,"https://i.imgur.com/ZcLLrkY.jpg"));
        BookmarkAdapter.addPlace(new Location("Hava bbbb"
                ,2,1,"https://i.imgur.com/ZcLLrkY.jpg"));
        BookmarkAdapter.addPlace(new Location("Hava aaa"
                ,2,1,"https://i.imgur.com/ZcLLrkY.jpg"));
        BookmarkAdapter.addPlace(new Location("Havasu Fs"
                ,2,1,"https://i.imgur.com/ZcLLrkY.jpg"));
    }

    private void clearBookmarkList() {
        BookmarkAdapter.fullLocationList.clear();
        BookmarkAdapter.tempLocationList.clear();
    }

    private void initRecyclerView(View view) {
        Logger.d("initRecyclerView: recycler view init");
        RecyclerView recyclerView = view.findViewById(R.id.bookMark_RV);
        BookmarkAdapter adapter = new BookmarkAdapter(view.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }


}