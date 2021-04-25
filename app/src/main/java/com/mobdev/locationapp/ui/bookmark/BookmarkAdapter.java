package com.mobdev.locationapp.ui.bookmark;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobdev.locationapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder>  {
    private static final String TAG = "BookmarkAdapter";

    public static ArrayList<String>  imageSrcs_array=  new ArrayList<>();
    public static ArrayList<String>  name_array=  new ArrayList<>();
    public static ArrayList<String>  coordinatees_array=  new ArrayList<>();
    public static ArrayList<String>  imageSrcs_array_copy=  new ArrayList<>();
    public static ArrayList<String>  name_array_copy=  new ArrayList<>();
    public static ArrayList<String>  coordinatees_array_copy=  new ArrayList<>();

    private Context mContext;
    public static BookmarkAdapter adapter;

//,ArrayList<String> imageSrcs_array, ArrayList<String> name_array, ArrayList<String> coordinatees_array
    public BookmarkAdapter(Context mContext) {
//        this.imageSrcs_array = imageSrcs_array;
//        this.name_array = name_array;
//        this.coordinatees_array = coordinatees_array;
        this.mContext = mContext;
        adapter=this;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        Glide.with(mContext)
                .asBitmap()
                .load(imageSrcs_array.get(position))
                .into(holder.image);
        holder.coordinates.setText(coordinatees_array.get(position));
        holder.name.setText(name_array.get(position));
//        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext,name_array.get(position),Toast.LENGTH_LONG).show();
//
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return name_array.size();
    }

//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                ArrayList<String> filteredList = new ArrayList<>();
//
//                if (constraint == null || constraint.length() == 0) {
//                    filteredList.addAll();
//                } else {
//                    String filterPattern = constraint.toString().toLowerCase().trim();
//
//                    for (ExampleItem item : exampleListFull) {
//                        if (item.getText2().toLowerCase().contains(filterPattern)) {
//                            filteredList.add(item);
//                        }
//                    }
//                }
//
//                FilterResults results = new FilterResults();
//                results.values = filteredList;
//
//                return results;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//
//            }
//        };
//    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView image;
        TextView name;
        TextView coordinates;
        ImageButton delete_btn;
        ConstraintLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.bookmark_image);
            name= itemView.findViewById(R.id.bookmark_name);
            coordinates= itemView.findViewById(R.id.coordinates);
            delete_btn= itemView.findViewById(R.id.bookmark_delete);
            parentLayout = itemView.findViewById(R.id.bookmark_parent);
            delete_btn.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.equals(delete_btn)){
                removeAt(getAdapterPosition());
            }
        }


    }
    public void removeAt(int adapterPosition) {
        imageSrcs_array.remove(adapterPosition);
        name_array.remove(adapterPosition);
        coordinatees_array.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        notifyItemRangeChanged(adapterPosition, imageSrcs_array.size());
    }
    public static void addPlace(String image,String name,String coordinate){
        imageSrcs_array.add(image);
        name_array.add(name);
        coordinatees_array.add(coordinate);
        imageSrcs_array_copy= (ArrayList<String>) imageSrcs_array.clone();
        name_array_copy= (ArrayList<String>) name_array.clone();
        coordinatees_array_copy= (ArrayList<String>) coordinatees_array.clone();
        adapter.notifyDataSetChanged();
//        adapter.notifyItemInserted(imageSrcs_array.size()-1);

    }
}
