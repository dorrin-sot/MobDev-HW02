package com.mobdev.locationapp.ui.bookmark;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdev.locationapp.R;

import java.util.ArrayList;

public class BookmarkFragment extends Fragment {
    private static final String TAG = "BookmarkFragment";
    private ArrayList<String> imageSrcs_array=  new ArrayList<>();
    private ArrayList<String>  name_array=  new ArrayList<>();
    private ArrayList<String>  coordinatees_array=  new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        init(view);
        return view;
    }
    private void init(View view){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

//        imageSrcs_array.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");
//        name_array.add("Havasu Falls");
//        coordinatees_array.add("1,2");
//
//        imageSrcs_array.add("https://i.redd.it/tpsnoz5bzo501.jpg");
//        name_array.add("Trondheim");
//        coordinatees_array.add("1,3");
//
//        imageSrcs_array.add("https://i.redd.it/qn7f9oqu7o501.jpg");
//        name_array.add("Portugal");
//        coordinatees_array.add("1,4");
//
//        imageSrcs_array.add("https://i.redd.it/j6myfqglup501.jpg");
//        name_array.add("Rocky Mountain National Park");
//        coordinatees_array.add("1,5");
//
//
//        imageSrcs_array.add("https://i.redd.it/0h2gm1ix6p501.jpg");
//        name_array.add("Mahahual");
//        coordinatees_array.add("1,6");
//
//        imageSrcs_array.add("https://i.redd.it/k98uzl68eh501.jpg");
//        name_array.add("Frozen Lake");
//        coordinatees_array.add("1,7");
//
//
//        imageSrcs_array.add("https://i.redd.it/glin0nwndo501.jpg");
//        name_array.add("White Sands Desert");
//        coordinatees_array.add("1,8");
//
//        imageSrcs_array.add("https://i.redd.it/obx4zydshg601.jpg");
//        name_array.add("Austrailia");
//        coordinatees_array.add("1,9");
//
//        imageSrcs_array.add("https://i.imgur.com/ZcLLrkY.jpg");
//        name_array.add("Washington");
//        coordinatees_array.add("1,10");

        initRecyclerView(view);
    }

    private void initRecyclerView(View view) {
        Log.d(TAG, "initRecyclerView: recycler view init");
        RecyclerView recyclerView = view.findViewById(R.id.bookMark_RV);
//        BookmarkAdapter adapter = new BookmarkAdapter(view.getContext(),imageSrcs_array,name_array,coordinatees_array);
        BookmarkAdapter adapter = new BookmarkAdapter(view.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }


}