package com.mobdev.locationapp.ui.bookmark;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {
    private static final String TAG = "BookmarkAdapter";
    private ArrayList<String>  imageSrcs_array=  new ArrayList<>();
    private ArrayList<String>  name_array=  new ArrayList<>();
    private ArrayList<String>  coordinatees_array=  new ArrayList<>();
    private Context mContext;


    public BookmarkAdapter(Context mContext,ArrayList<String> imageSrcs_array, ArrayList<String> name_array, ArrayList<String> coordinatees_array) {
        this.imageSrcs_array = imageSrcs_array;
        this.name_array = name_array;
        this.coordinatees_array = coordinatees_array;
        this.mContext = mContext;
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
        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,name_array.get(position),Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return name_array.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
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

        }
    }
}
