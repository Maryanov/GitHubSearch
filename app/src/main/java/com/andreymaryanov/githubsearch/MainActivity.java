package com.andreymaryanov.githubsearch;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.andreymaryanov.githubsearch.adapters.*;
import com.andreymaryanov.githubsearch.api.ApiService;
import com.andreymaryanov.githubsearch.listener.IApiCallback;
import com.andreymaryanov.githubsearch.model.Feed;

public class MainActivity extends AppCompatActivity {

    ListView lView=null;
    ProgressBar vProgressBar = null;
    String sQuery = "";
    Integer page = 1;
    Feed Data = null;
    Boolean loadRun=false;
    Integer loadCurrentCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handleIntent(getIntent());

        lView = (ListView) findViewById(R.id.listViewData);
        vProgressBar = (ProgressBar) findViewById(R.id.status_loading);

        lView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState==SCROLL_STATE_IDLE & loadRun==true){
                    if (loadCurrentCount<Data.getTotalCount()) {
                        page++;
                        GitHuBSearch(sQuery, page);
                    } else loadRun=false;
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem+visibleItemCount)==totalItemCount & totalItemCount!=0
                        & visibleItemCount!=totalItemCount){
                        loadRun=true;
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
                        DataAdapter adapter = new DataAdapter(MainActivity.this, Data.getItems());
                        lView.setAdapter(adapter);
                    } else {
                        Data.addNewItems(data.getItems());
                        DataAdapter adapter = new DataAdapter(MainActivity.this, Data.getItems());
                        Parcelable state = lView.onSaveInstanceState();
                        lView.setAdapter(adapter);
                        lView.onRestoreInstanceState(state);
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
