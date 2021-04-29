package com.mobdev.locationapp.ui.bookmark;

import android.content.Context;

import android.os.Bundle;
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
import com.mobdev.locationapp.Handler.Message;
import com.bumptech.glide.Glide;
import com.mobdev.locationapp.Handler;
import com.mobdev.locationapp.Logger;
import com.mobdev.locationapp.Model.Location;
import com.mobdev.locationapp.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder>  {


    public static ArrayList<Location> bookmarkList =  new ArrayList<>();

    private Context mContext;
    public static BookmarkAdapter adapter;


    public BookmarkAdapter(Context mContext) {
        this.mContext = mContext;
        adapter=this;
    }

    public static void updateBookmarkList(ArrayList<Location> bookmarks) {
        Logger.e("bookmarks to update: "+bookmarks.toString());
        bookmarkList=bookmarks;
        adapter.notifyDataSetChanged();

    }

    public static void searchMessage(String searchString) {
        android.os.Message message = new android.os.Message();
        message.what=Message.SEARCH_BOOKMARKS.ordinal();
        message.obj=searchString;
        sendMessageToHandler(message);

    }

    private static void sendMessageToHandler(android.os.Message message) {
        Handler.getHandler().sendMessage(message);
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
        Logger.i("onBindViewHolder: called");
        Glide.with(mContext)
                .asBitmap()
                .load(bookmarkList.get(position).getImgURL())
                .into(holder.image);
        holder.coordinates.setText(bookmarkList.get(position).getX()+","+ bookmarkList.get(position).getY());
        holder.name.setText(bookmarkList.get(position).getName());


    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    public void filterList(ArrayList<Location> filteredList) {
        bookmarkList =filteredList;
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
                removeBookmarkMessage(getAdapterPosition());
            }
            else {
                Toast.makeText(v.getContext(),"click on other parts",Toast.LENGTH_SHORT).show();
                //todo send coordinates to mapview
                
            }
        }


    }
    public void removeBookmarkMessage(int adapterPosition) {
        Location newLocation = bookmarkList.get(adapterPosition);
        android.os.Message message = new android.os.Message();
        message.what= Message.DELETE_BOOKMARK.ordinal();
        Bundle bundle =new Bundle();
        bundle.putString("location_name",newLocation.getName());
        bundle.putDouble("x",newLocation.getX());
        bundle.putDouble("y",newLocation.getY());
        bundle.putString("img_url",newLocation.getImgURL());
        bundle.putInt("position",adapterPosition);
        message.setData(bundle);
        sendMessageToHandler(message);

    }

    public static void addBookmarkMessage( String name , double x,double y,String imgUrl){
        android.os.Message message = new android.os.Message();
        message.what= Message.ADD_BOOKMARK.ordinal() ;
        Bundle bundle =new Bundle();
        bundle.putString("location_name",name);
        bundle.putDouble("x",x);
        bundle.putDouble("y",y);
        bundle.putString("img_url",imgUrl);
        message.setData(bundle);
        sendMessageToHandler(message);


    }
    public static void updateBookmarkListMessage(){
        android.os.Message message = new android.os.Message();
        message.what=Message.GET_ALL_BOOKMARKS.ordinal();
        sendMessageToHandler(message);
    }
    public static synchronized void addBookmark(Location location){
        bookmarkList.add(location);
        adapter.notifyDataSetChanged();
    }
    public static synchronized void removeBookmark(int position){
        bookmarkList.remove(position);
        adapter.notifyDataSetChanged();
    }
}
