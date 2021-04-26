package com.mobdev.locationapp.Model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Entity(tableName = "Bookmarks", primaryKeys = {"X", "Y"})
public class Location {
    @ColumnInfo(name = "NAME")
    @NonNull
    private final String name;

    @ColumnInfo(name = "X")
    @NonNull
    private final double x;

    @ColumnInfo(name = "Y")
    @NonNull
    private final double y;

    @ColumnInfo(name = "IMAGE_URL")
    @NonNull
    private final String imgURL;

    public Location(String name, double x, double y, String imgURL) {
//        print();
//        Log.e("location","location in Location: "+ name);
        this.name = name;
        this.x = x;
        this.y = y;
        this.imgURL = imgURL;
    }

    @Dao
    public interface LocationDao {
        @Query("SELECT * FROM Bookmarks")
        List<Location> getBookmarks();

        @Query("SELECT * FROM Bookmarks WHERE NAME LIKE :name")
        List<Location> searchBookmarks (String name);

        @Query("SELECT * FROM Bookmarks WHERE NAME LIKE :name")
        Location getBookmark(String name);

        @Query("SELECT * FROM Bookmarks WHERE EXISTS(" +
                "SELECT * FROM Bookmarks WHERE NAME LIKE :name" +
                ")")
        boolean bookmarkExists(String name);

        @Insert
        void addBookmark(Location... location);

        @Delete
        void deleteBookmark(Location... location);

        @Query("DELETE FROM Bookmarks")
        void deleteAllBookmarks();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getName() {
        return name;
    }

    public String getImgURL() {
        return imgURL;
    }
}