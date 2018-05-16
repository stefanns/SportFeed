package com.example.android.sportsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class SportsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<SportEvent>> {
    //get the class name and us it as a constant for logging messages
    public static final String LOG_TAG = SportsActivity.class.getName();
    public static final String SPORTS_LINK = "http://content.guardianapis.com/search?";
    private static final int SPORT_EVENT_LOADER_ID = 1;
    TextView tvEmptyState;
    private SportsEventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //enable caching
        enableHttpResponseCache();
        tvEmptyState = findViewById(R.id.empty_state);
        // Find a reference to the list view in the layout
        // Create a new adapter of sports events
        ListView sportNews = (ListView) findViewById(R.id.list);
        //set the list to be empty when no data is displayed
        sportNews.setEmptyView(tvEmptyState);
        adapter = new SportsEventAdapter(SportsActivity.this, new ArrayList<SportEvent>());
        sportNews.setAdapter(adapter);
        //start WebActivity when the user clicks on a news
        sportNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SportEvent currentEvent = adapter.getItem(i);
                String url = currentEvent.getUrl();
                Intent startWeb = new Intent(getApplicationContext(), WebActivity.class);
                startWeb.putExtra("URL", url);
                startActivity(startWeb);
            }
        });
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(SPORT_EVENT_LOADER_ID, null, this).forceLoad();

        } else {
            ProgressBar loading = (ProgressBar) findViewById(R.id.loading);
            loading.setVisibility(View.GONE);
            tvEmptyState.setText(R.string.no_internet);
            enableHttpResponseCache();
        }
    }


    @Override
    public Loader<ArrayList<SportEvent>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String noOfNews = sharedPrefs.getString(
                getString(R.string.settings_max_news_displayed_key),
                getString(R.string.settings_max_news_displayed_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(SPORTS_LINK);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("page-size", noOfNews);
        uriBuilder.appendQueryParameter("section", "sport");
        uriBuilder.appendQueryParameter("q", "sport");
        uriBuilder.appendQueryParameter("api-key", "71654aa8-366c-4f56-a120-194b4eeb0606");

        // Return the completed uri `http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=10&minmag=minMagnitude&orderby=time
        return new SportEventLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<SportEvent>> loader, ArrayList<SportEvent> sportEvents) {
        ProgressBar loading = (ProgressBar) findViewById(R.id.loading);
        loading.setVisibility(View.INVISIBLE);
        tvEmptyState.setText(R.string.no_data);
        adapter.clear();
        if (sportEvents != null && !sportEvents.isEmpty()) {
            adapter.addAll(sportEvents);
        }

        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<SportEvent>> loader) {
        adapter.clear();
    }

    /*
    Enable caching to avoid unnecessary network operations
     */
    private void enableHttpResponseCache() {
        try {
            //set the limit of the cache, 10 MiB
            long httpCacheSize = 10 * 1024 * 1024;
            File httpCacheDir = new File(getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.d(LOG_TAG, "HTTP response cache is unavailable.");
        }
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

