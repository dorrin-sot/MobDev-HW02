package com.mobdev.locationapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.mobdev.locationapp.Model.Location;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.mobdev.locationapp.Logger.d;
import static com.mobdev.locationapp.MainActivity.db;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class Handler extends android.os.Handler {
    private static Handler handler;
    private WeakReference<Activity> activityWeakReference;
    private static ExecutorService executor = newFixedThreadPool(5);

    @Override
    public void handleMessage(@NonNull android.os.Message msg) {
        super.handleMessage(msg);

        d("message = " + Message.values()[msg.what]);

        switch (Message.values()[msg.what]) {
            case SWITCH_THEME:
                // TODO: 4/25/21  
                break;
            case DELETE_ALL_DATA:
                executor.execute(() ->
                        db.locationDao().deleteAllBookmarks()
                );
                break;
            case ADD_BOOKMARK:
                executor.execute(() ->
                        db.locationDao().addBookmark(getLocationFromMessage(msg))
                );
                break;
            case DELETE_BOOKMARK:
                executor.execute(() ->
                        db.locationDao().deleteBookmark(getLocationFromMessage(msg))
                );
                break;
            case GET_ALL_BOOKMARKS:
                executor.execute(() -> {
                            List<Location> bookmarks = db.locationDao().getBookmarks();

                            activityWeakReference.get().runOnUiThread(() ->
                                    updateBookmarkList(bookmarks)
                            );
                        }
                );
                break;
            case SEARCH_BOOKMARKS:
                executor.execute(() -> {
                            String searchPhrase = (String) msg.obj;
                            List<Location> bookmarks = db.locationDao().searchBookmarks(searchPhrase);

                            activityWeakReference.get().runOnUiThread(() ->
                                    updateBookmarkList(bookmarks)
                            );
                        }
                );
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + Message.values()[msg.what]);
        }
    }

    private void updateBookmarkList(List<Location> bookmarks) {
        // TODO: 4/25/21  
    }

    private Location getLocationFromMessage(android.os.Message msg) {
        Bundle locationData = msg.getData();
        return new Location(
                locationData.getString("location_name"),
                locationData.getDouble("x"),
                locationData.getDouble("y"),
                locationData.getString("img_url")
        );
    }

    public Handler(@NonNull Looper looper) {
        super(looper);

        if (handler == null)
            handler = this;
    }

    public static Handler getHandler() {
        return handler;
    }

    public void setActivityWeakReference(WeakReference<Activity> activityWeakReference) {
        this.activityWeakReference = activityWeakReference;
    }

    public enum Message {
        SWITCH_THEME,
        DELETE_ALL_DATA,
        ADD_BOOKMARK,
        DELETE_BOOKMARK,
        GET_ALL_BOOKMARKS,
        SEARCH_BOOKMARKS,
        // todo add for map stuff if needed
    }
}
