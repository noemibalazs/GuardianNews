package com.example.android.guardiannews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SharedPreferences.OnSharedPreferenceChangeListener   {

    private static String tempLink;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int NEWS_LOADER_ID = 1;

    private NewsAdapter mAdapter;

    private TextView mEmptyTextView;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.list_view);
        mEmptyTextView = findViewById(R.id.welcome_text);
        listView.setEmptyView(mEmptyTextView);

        mProgressBar = findViewById(R.id.progress_indicator);

        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News news = mAdapter.getItem(position);
                Uri newsUri = Uri.parse(news.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(intent);
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo !=null && networkInfo.isConnected()){
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }  else {
            mProgressBar.setVisibility(View.GONE);
            mEmptyTextView.setText("No internet connection");
        }
    }

    private boolean isChecked() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return isAvailable;
    }


    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key),
                                                     getString(R.string.settings_order_by_default));
        String search = sharedPreferences.getString(getString(R.string.search_key),
                                                   getString(R.string.search_default_value));

        tempLink= "https://content.guardianapis.com/search?" + "&show-fields=all" + "&show-tags=all";


        Uri baseUri = Uri.parse(tempLink);
        Uri.Builder builder= baseUri.buildUpon();

        builder.appendQueryParameter("api-key", "bbd8da9b-8352-4aca-bbf3-55625e6cb570");
        builder.appendQueryParameter("format", "json");
        builder.appendQueryParameter("q", search);
        builder.appendQueryParameter("tag", orderBy);

        return new NewsLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        mProgressBar.setVisibility(View.GONE);
        mAdapter.clear();

        if (news!=null && !news.isEmpty()){
            mAdapter.addAll(news);
        } else {
            mEmptyTextView.setText("No news found!");
        }
        if (!isChecked()){
            mProgressBar.setVisibility(View.GONE);
            mEmptyTextView.setText("No internet connection!");
        } else {
            Log.v(LOG_TAG, "Network is available!");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class );
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.settings_order_by_key)) || key.equals(getString(R.string.search_key))){
            mAdapter.clear();
            mEmptyTextView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
    }
}
