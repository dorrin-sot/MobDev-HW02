package com.mobdev.locationapp.Model;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Entity(tableName = "Bookmarks", primaryKeys = {"X", "Y"})
public class Location {
    @ColumnInfo(name = "LOCATION_NAME")
    private final String locationName;
    @ColumnInfo(name = "X")
    private final double x;
    @ColumnInfo(name = "Y")
    private final double y;

    @ColumnInfo(name = "BOOKMARK_NAME")
    private String bookmarkName;

    public Location(String locationName, double x, double y) {
        this.locationName = locationName;
        this.x = x;
        this.y = y;
    }

    @Dao
    public interface LocationDao {
        @Query("SELECT * FROM Bookmarks")
        List<Location> getBookmarks();

        @Query("SELECT * FROM Bookmarks WHERE BOOKMARK_NAME LIKE :bookmarkName")
        com.mobdev.locationapp.Model.Location getBookmark(String bookmarkName);

        @Query("SELECT * FROM Bookmarks WHERE EXISTS(" +
                "SELECT * FROM Bookmarks WHERE BOOKMARK_NAME LIKE :bookmarkName" +
                ")")
        boolean bookmarkExists(String bookmarkName);

        @Query("UPDATE Bookmarks SET BOOKMARK_NAME = :bookmarkName WHERE LOCATION_NAME = :locationName")
        void updateBookmarkName(String locationName, String bookmarkName);

        @Insert
        void addBookmark(com.mobdev.locationapp.Model.Location... location);

        @Delete
        void deleteBookmark(com.mobdev.locationapp.Model.Location... location);
    }

    public String getLocationName() {
        return locationName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getBookmarkName() {
        return bookmarkName;
    }

    public void setBookmarkName(String bookmarkName) {
        this.bookmarkName = bookmarkName;
    }
}