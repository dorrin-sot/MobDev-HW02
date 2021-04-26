package com.mobdev.locationapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.mobdev.locationapp.Model.Location;
import com.mobdev.locationapp.ui.bookmark.BookmarkAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import static android.content.Context.MODE_PRIVATE;
import static android.content.res.Configuration.UI_MODE_NIGHT_MASK;
import static android.content.res.Configuration.UI_MODE_NIGHT_NO;
import static android.content.res.Configuration.UI_MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;
import static com.mobdev.locationapp.Logger.d;
import static com.mobdev.locationapp.MainActivity.db;
import static com.mobdev.locationapp.R.string.firstTime_title;
import static com.mobdev.locationapp.R.string.themeLight_title;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class Handler extends android.os.Handler {
    private static Handler handler;
    private WeakReference<Activity> activityWeakReference;
    private static ExecutorService executor = newFixedThreadPool(5);

    @Override
    public void handleMessage(@NonNull android.os.Message msg) {
        super.handleMessage(msg);

        d("message = " + Message.values()[msg.what]);
        Bundle bundle;
        AtomicReference<Location> location = new AtomicReference<>();
        switch (Message.values()[msg.what]) {
            case DELETE_ALL_DATA:
                executor.execute(() ->
                        db.locationDao().deleteAllBookmarks()
                );
                break;
            case ADD_BOOKMARK:

                bundle = msg.getData();
                executor.execute(() ->{
                    location.set(getLocationFromBundle(bundle));
                    db.locationDao().addBookmark(location.get());
                    activityWeakReference.get().runOnUiThread(()->
                            BookmarkAdapter.addBookmark(location.get())

                    );
                });
                break;
            case DELETE_BOOKMARK:
                bundle = msg.getData();
                executor.execute(() -> {
                    location.set(getLocationFromBundle(bundle));
                    int position = bundle.getInt("position");
                    db.locationDao().deleteBookmark(location.get());
                    activityWeakReference.get().runOnUiThread(() -> {
                        BookmarkAdapter.removeBookmark(position);
                    });
                });
                break;
            case GET_ALL_BOOKMARKS:
                executor.execute(() -> {
                            ArrayList<Location> bookmarks = (ArrayList<Location>) db.locationDao().getBookmarks();
                             activityWeakReference.get().runOnUiThread(() ->
                                    BookmarkAdapter.updateBookmarkList(bookmarks)
                            );
                        }
                );
                break;
            case SEARCH_BOOKMARKS:
                String searchPhrase = "%"+ msg.obj + "%";
                Logger.e("searchPhrase: "+searchPhrase );
                executor.execute(() -> {
                            ArrayList<Location> bookmarks = new ArrayList<>(db.locationDao().searchBookmarks(searchPhrase));
                            activityWeakReference.get().runOnUiThread(() ->
                                    BookmarkAdapter.updateBookmarkList(bookmarks)
                            );
                        }
                );
                break;
            case SET_THEME:
                executor.execute(() -> {
                    // TODO: 4/26/21  
                    Activity activity = activityWeakReference.get();

                    SharedPreferences theme_prefs = activity.getSharedPreferences("theme_prefs", MODE_PRIVATE);

                    boolean goDark;
                    int uiMode = activity.getResources().getConfiguration().uiMode & UI_MODE_NIGHT_MASK;
                    switch (uiMode) {
                        case UI_MODE_NIGHT_YES:
                            goDark = theme_prefs.getBoolean(activity.getString(firstTime_title), true);
                            break;
                        case UI_MODE_NIGHT_NO:
                            goDark = !theme_prefs.getBoolean(activity.getString(firstTime_title), true);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + uiMode);
                    }
                    activity.runOnUiThread(() -> setDefaultNightMode(
                            goDark ? MODE_NIGHT_YES : MODE_NIGHT_NO
                    ));

                    d("changed theme to " + (goDark ? "MODE_NIGHT_YES" : "MODE_NIGHT_NO"));

                    theme_prefs.edit()
                            .putBoolean(activity.getString(themeLight_title), !goDark)
                            .apply();
                });
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + Message.values()[msg.what]);
        }
    }

    private Location getLocationFromBundle(Bundle locationData) {
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

    public  enum Message {
        SWITCH_THEME,
        DELETE_ALL_DATA,
        ADD_BOOKMARK,
        DELETE_BOOKMARK,
        GET_ALL_BOOKMARKS,
        SEARCH_BOOKMARKS,
        SET_THEME
    }
}
