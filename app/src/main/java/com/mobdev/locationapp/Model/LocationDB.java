package com.mobdev.locationapp.Model;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Location.class}, version = 1, exportSchema = false)
public abstract class LocationDB extends RoomDatabase {
    public abstract Location.LocationDao locationDao();
}