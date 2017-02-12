package com.andreymaryanov.githubsearch;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.andreymaryanov.githubsearch.adapters.*;
import com.andreymaryanov.githubsearch.api.ApiService;
import com.andreymaryanov.githubsearch.listener.IApiCallback;
import com.andreymaryanov.githubsearch.model.Feed;

public class MainActivity extends AppCompatActivity {

    RecyclerView rView=null;
    ProgressBar vProgressBar = null;
    String sQuery = "";
    Integer page = 1;
    Feed Data = null;
    Boolean loadRun=false;
    Integer loadCurrentCount=0;

    private int previousTotal = 0;
    private int visibleThreshold = 9;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    LinearLayoutManager layoutManager=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handleIntent(getIntent());

        rView = (RecyclerView) findViewById(R.id.listViewData);
        vProgressBar = (ProgressBar) findViewById(R.id.status_loading);

        rView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = rView.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (loadRun) {
                    if (totalItemCount > previousTotal) {
                        loadRun = false;
                        previousTotal = totalItemCount;
                        visibleThreshold+=10;
                    }
                }
                if (!loadRun && (firstVisibleItem + visibleItemCount)
                        >= (visibleThreshold) && totalItemCount>=10) {
                    page++;
                    GitHuBSearch(sQuery, page);
                    loadRun = true;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private ApiService apiService = new ApiService();

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            sQuery = query;
            page = 1;
            visibleThreshold=9;
            loadCurrentCount=0;
            GitHuBSearch(sQuery, page);
        }
    }

    void GitHuBSearch(String Query, Integer Page) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            Toast.makeText(this, "нет соединений", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Page>1) vProgressBar.setVisibility(View.VISIBLE);

        apiService.getSearchResult(Query, "stars", "desc", Page, 10, new IApiCallback<Feed>() {
            @Override
            public void onSuccess(Feed data) {
                if (data != null) {
                    loadCurrentCount=loadCurrentCount+data.getItems().size();
                    if (page == 1) {
                        Data = new Feed();
                        Data = data;
                        RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this, Data.getItems());
                        layoutManager = new LinearLayoutManager(MainActivity.this);
                        rView.setAdapter(adapter);
                        rView.setLayoutManager(layoutManager);
                    } else {
                        Data.addNewItems(data.getItems());
                        RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this, Data.getItems());
                        Parcelable recyclerViewState = rView.getLayoutManager().onSaveInstanceState();//save
                        rView.setAdapter(adapter);
                        rView.getLayoutManager().onRestoreInstanceState(recyclerViewState);//restore
                        vProgressBar.setVisibility(View.GONE);
                        loadRun = false;
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                if (vProgressBar != null) vProgressBar.setVisibility(View.GONE);
                if (page > 1) page--;
                loadRun = false;
            }
        });
    }

}
