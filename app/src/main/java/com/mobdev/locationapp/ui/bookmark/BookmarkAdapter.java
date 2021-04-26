package com.mobdev.locationapp.ui.bookmark;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobdev.locationapp.Logger;
import com.mobdev.locationapp.Model.Location;
import com.mobdev.locationapp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder>  {
    private static final String TAG = "BookmarkAdapter";

    public static ArrayList<Location> fullLocationList =  new ArrayList<>();
    public static ArrayList<Location> tempLocationList =  new ArrayList<>();

    private Context mContext;
    public static BookmarkAdapter adapter;


    public BookmarkAdapter(Context mContext) {
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
                .load(tempLocationList.get(position).getImgURL())
                .into(holder.image);
        holder.coordinates.setText(tempLocationList.get(position).getX()+","+tempLocationList.get(position).getY());
//        Log.e("location","location in holder: "+tempLocationList.get(position).getName());
//        Logger.e("location in Location: "+);
        holder.name.setText(tempLocationList.get(position).getName());
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
        return tempLocationList.size();
    }

    public void filterList(ArrayList<Location> filteredList) {
        tempLocationList=filteredList;
        notifyDataSetChanged();
    }

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
        //todo fix removing stuff
        removeFromFullLocationList(tempLocationList.get(adapterPosition).getName(),
                tempLocationList.get(adapterPosition).getX(),tempLocationList.get(adapterPosition).getY());
        tempLocationList.remove(adapterPosition);

        notifyItemRemoved(adapterPosition);
        notifyItemRangeChanged(adapterPosition, tempLocationList.size());
    }

    private void removeFromFullLocationList(String locationName , double x, double y) {
        //todo complete this function
        int position=-1;
        for (int i=0 ;i <fullLocationList.size();i++){
            Location location =fullLocationList.get(i);
            if (location.getName().toLowerCase().equals(locationName.toLowerCase()) &&
                location.getX()==x && location.getY()==y){
                position=i;
                break;
            }
        }
        if(position==-1){
            new Exception("position in removeFromFullLocationList is -1");
        }
        fullLocationList.remove(position);
    }

    public static void addPlace(Location newLocation){
//        Location newLocation = new Location(name,x,y,image);
        fullLocationList.add(newLocation);
        tempLocationList = (ArrayList<Location>) fullLocationList.clone();
        adapter.notifyDataSetChanged();
//        adapter.notifyItemInserted(imageSrcs_array.size()-1);

    }
}
