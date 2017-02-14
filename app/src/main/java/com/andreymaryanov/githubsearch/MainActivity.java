package com.andreymaryanov.githubsearch;

import android.app.SearchManager;
import android.content.Context;
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
import android.widget.Toast;

import com.andreymaryanov.githubsearch.adapters.*;
import com.andreymaryanov.githubsearch.api.ApiService;
import com.andreymaryanov.githubsearch.listener.IApiCallback;
import com.andreymaryanov.githubsearch.model.Feed;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mainListView=null;
    private String textQuery = "";
    private Integer iPageSearch = 1;
    private Feed feedData = null;
    private Boolean isLoading=false;
    private Integer loadCurrentCount=0;
    private int visibleThreshold = 29;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private LinearLayoutManager layoutManager=null;
    private ApiService apiService = new ApiService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainListView = (RecyclerView) findViewById(R.id.listViewData);
        mainListView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                visibleItemCount = mainListView.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if ((isLoading == false) && newState == RecyclerView.SCROLL_STATE_IDLE &&
                        (firstVisibleItem + visibleItemCount)>= (visibleThreshold) &&
                        totalItemCount>=10) {
                    GitHuBSearch(textQuery, iPageSearch);
                    isLoading = true;
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                runSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                runSearch(newText);
                return false;
            }
        });
        return true;
    }

    void runSearch(String text) {
        textQuery = text;
        iPageSearch = 1;
        visibleThreshold=9;
        loadCurrentCount=0;
        GitHuBSearch(textQuery, iPageSearch);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void GitHuBSearch(String Query, Integer Page) {

        if (isConnectEnternet()) {
            apiService.getSearchResult(Query, "stars", "desc", Page, 10, new IApiCallback<Feed>() {
                @Override
                public void onSuccess(Feed data) {
                    if ((data != null)) {
                        loadCurrentCount = loadCurrentCount + data.getItems().size();
                        if (iPageSearch == 1) {
                            iPageSearch++;
                            feedData = new Feed();
                            feedData = data;
                            RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this, feedData.getItems());
                            layoutManager = new LinearLayoutManager(MainActivity.this);
                            mainListView.setAdapter(adapter);
                            mainListView.setLayoutManager(layoutManager);
                        } else if (isLoading == true) {
                            iPageSearch++;
                            visibleThreshold += 10;
                            feedData.addNewItems(data.getItems());
                            RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this, feedData.getItems());
                            Parcelable recyclerViewState = mainListView.getLayoutManager().onSaveInstanceState();//save
                            mainListView.setAdapter(adapter);
                            mainListView.getLayoutManager().onRestoreInstanceState(recyclerViewState);//restore
                            isLoading = false;
                        }
                    } else isLoading = false;
                }

                @Override
                public void onError(Throwable t) {
                    isLoading = false;
                }
            });
        }
    }

    private boolean isConnectEnternet(){

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            Toast.makeText(this, "нет соединений", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
