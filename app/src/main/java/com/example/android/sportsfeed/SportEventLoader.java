package com.example.android.sportsfeed;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Stefan on 5/5/2018.
 */

public class SportEventLoader extends AsyncTaskLoader<ArrayList<SportEvent>> {
    private static final String LOG_TAG = SportEventLoader.class.getName();
    private String url;

    public SportEventLoader(Context context, String urlLoader) {
        super(context);
        url = urlLoader;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<SportEvent> loadInBackground() {
        if (url == null) {
            return null;
        }
        ArrayList<SportEvent> sportEvents = Utils.fetchEarthquakeData(url);
        return sportEvents;
    }
}
